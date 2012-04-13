package edu.gatech.cc.baconytics.analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gatech.cc.baconytics.model.AnalyzerCheckpoint;
import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.LinkRelevanceBundle;
import edu.gatech.cc.baconytics.model.LinkStats;
import edu.gatech.cc.baconytics.model.PMF;
import edu.gatech.cc.baconytics.model.User;

public class Analyzer extends HttpServlet {
	private static final int RETURN_THRESHHOLD = 10;
	private static PersistenceManager pm = PMF.get().getPersistenceManager();
	private static PrintWriter writer;
	private List<KeywordLinkMap> keywordList;
	private List<KeywordEntity> keyEntList;
	private List<Link> linkList;
	private static final int TOP = 100;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {

		String path = req.getPathInfo();
		System.out.println(path);

		try {
			writer = res.getWriter();
		} catch (IOException e1) {
			System.out.println("IO exarg0ception!");
			e1.printStackTrace();
		}

		if (path != null && path.equals("/flush")) {
			Query userQuery = pm.newQuery(User.class);
			userQuery.deletePersistentAll();

			Query linkStatsQuery = pm.newQuery(LinkStats.class);
			linkStatsQuery.deletePersistentAll();

			Query linkQuery = pm.newQuery(Link.class);
			linkQuery.deletePersistentAll();

			Query linkRelevanceQuery = pm.newQuery(LinkRelevanceBundle.class);
			linkRelevanceQuery.deletePersistentAll();

			Query linkKeywordQuery = pm.newQuery(LinkKeywordMap.class);
			linkKeywordQuery.deletePersistentAll();

			Query keywordLinkQuery = pm.newQuery(KeywordLinkMap.class);
			keywordLinkQuery.deletePersistentAll();

			Query keywordQuery = pm.newQuery(KeywordEntity.class);
			keywordQuery.deletePersistentAll();

			Query timeQuery = pm.newQuery(AnalyzerCheckpoint.class);
			List<AnalyzerCheckpoint> list = (List<AnalyzerCheckpoint>) timeQuery
					.execute();
			AnalyzerCheckpoint analyzerCheckpoint;
			if (list.size() == 0) {
				analyzerCheckpoint = new AnalyzerCheckpoint();
				pm.makePersistent(analyzerCheckpoint);
			} else {
				pm.currentTransaction().begin();
				analyzerCheckpoint = list.get(0);
				analyzerCheckpoint.resetCheckPoint();
				pm.currentTransaction().commit();
				// writer.println(analyzerCheckpoint.getTime());
			}

			return;
		}

		Query query = pm.newQuery(KeywordLinkMap.class);
		keywordList = (List<KeywordLinkMap>) query.execute();

		// Collections.sort(keywordList, new KeywordComparator());

		System.out.println(keywordList.size());
		keyEntList = putKeywordInEntities();

		// sort keyEntList based on the number of posts about a particular
		// topic.
		Collections.sort(keyEntList, new EntityComparator());

		// Print sorted keyEntList
		if (!keyEntList.isEmpty()) {
			// writer.println("Total: " + keywordList.size() + "\n");
			for (KeywordEntity e : keyEntList) {
				// writer.println(e.toString());
			}
		}

		// store keyEntList into datastore
		for (int i = 0; i < RETURN_THRESHHOLD; i++) {
			pm.makePersistent(keyEntList.get(i));
			// writer.println("this is the " + i + "th entity; "
			// + keyEntList.get(i).toString());
		}
		// writer.flush();

		// user

		/*
		 * The User part of Analyzer starts from here
		 */

		/*
		 * Resetting the Analyzer-Checkpoint and emptying User datastore for
		 * demo results We remove these 3 lines while deploying
		 */
		resetAnalyzerCheckPoint();
		Query userQuery2 = pm.newQuery(User.class);
		userQuery2.deletePersistentAll();

		/*
		 * Picking up the Links created past the Analyzer-Checkpoint time and
		 * not Aggregators UTC
		 */
		long analyzerCheckpoint = getAnalyzerCheckpoint();
		Query linkQuery = pm.newQuery(Link.class);
		linkQuery.setFilter("createdUtc > lastTime");
		linkQuery.setOrdering("createdUtc asc");
		linkQuery.declareParameters("long lastTime");

		linkList = (List<Link>) linkQuery.execute(analyzerCheckpoint);
		int linkListLength = linkList.size();
		System.out.println("The size of the link list: " + linkListLength);

		List<LinkStats> linkStatsList = new ArrayList<LinkStats>();

		/*
		 * For each Link find the LinkStats entity and add it to linkStatsList
		 * defined above
		 */
		for (int i = 0; i < linkListLength; i++) {
			Link l = linkList.get(i);
			long utcTime = l.getCreatedUtc();
			if (utcTime > analyzerCheckpoint) {
				/*
				 * analyzer's checkpoint is taking the latest created UTC time
				 * of Links
				 */
				analyzerCheckpoint = utcTime;
			}
			String id = l.getId();
			Query linkStatsQuery = pm.newQuery(LinkStats.class);
			linkStatsQuery.setFilter("this.id==iden");
			linkStatsQuery.declareParameters("String iden");

			List<LinkStats> newList = (List<LinkStats>) linkStatsQuery
					.execute(id);

			LinkStats linkStat = newList.get(0);
			if (linkStat != null) {
				linkStatsList.add(linkStat);
			} // assuming one result
		}

		/*
		 * Checkpointing the Analyzer with the latest time
		 */
		checkpointAnalyzer(analyzerCheckpoint);

		/*
		 * Sorting the LinkStatsList on score. All analysis on users is made
		 * only on top scoring Links
		 */
		Collections.sort(linkStatsList, new LinkStatsComparator());

		/*
		 * User information is in the Link class and not in LinkStats.
		 * Currently, analyzer hits the Link again to collect the User
		 * information. We can think of putting user name of Link in LinkStats
		 */
		Query linkQuery2 = pm.newQuery(Link.class);
		linkQuery2.setFilter("this.id == iden");
		linkQuery2.declareParameters("String iden");
		for (LinkStats linkStats : linkStatsList) {
			String id = linkStats.getId();

			linkList = (List<Link>) linkQuery2.execute(id);

			Link l = linkList.get(0);

			Query userQuery = pm.newQuery(User.class);
			userQuery.setFilter("this.userName==user");
			userQuery.declareParameters("String user");

			List<User> userList = (List<User>) userQuery.execute(l.getAuthor());

			/*
			 * If a user has never been encountered before, create a new one
			 */
			if (userList.size() == 0) {
				User user = new User(l.getAuthor(), linkStats.getScore());
				user.addVisits((linkStats.getDowns() + linkStats.getUps()));
				user.incrementTotalLinks();
				user.addComments(linkStats.getNumComments());
				pm.makePersistent(user);
			}
			/*
			 * Otherwise update his profile in the datastore
			 */
			else {
				pm.currentTransaction().begin();
				User user = userList.get(0);
				user.addKarma(linkStats.getScore());
				user.addVisits(linkStats.getDowns() + linkStats.getUps());
				user.incrementTotalLinks();
				user.addComments(linkStats.getNumComments());
				pm.currentTransaction().commit();
			}

		}

		/*
		 * Querying User
		 */
		Query userQuery = pm.newQuery(User.class);
		userQuery.setRange(0, 10);
		/*
		 * User Fetching top 10 results of highest Karma scorers
		 */
		userQuery.setOrdering("karma desc");
		List<User> userList = (List<User>) userQuery.execute(); // sorted on
																// karma

		JSONObject jsonUser = new JSONObject();
		JSONArray categoryJSON;

		int i = 1;
		for (User user : userList) {
			String username = user.getUsername();
			try {
				JSONArray karmaJSON = new JSONArray();
				JSONObject jsonRank = new JSONObject();
				jsonRank.put("rank", i);

				JSONObject jsonValue = new JSONObject();
				jsonValue.put("value", user.getKarma());

				karmaJSON.put(jsonRank);
				karmaJSON.put(jsonValue);

				JSONObject jsonKarma = new JSONObject();
				jsonKarma.put("karma", karmaJSON);

				if (jsonUser.has(username)) {
					categoryJSON = jsonUser.getJSONArray(username);

				} else {
					categoryJSON = new JSONArray();
				}

				categoryJSON.put(jsonKarma);
				jsonUser.put(username, categoryJSON);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			i++;
		}

		userQuery.setRange(0, 10);
		/*
		 * Fetching top 10 results of total Visits / Highest hits on the link
		 */
		userQuery.setOrdering("totalVisits desc");
		userList = (List<User>) userQuery.execute();

		// writer.println(" ");
		// writer.println("Highest Hits collectors");
		i = 1;
		for (User user : userList) {
			String username = user.getUsername();
			try {
				JSONArray visitsJSON = new JSONArray();
				JSONObject jsonRank = new JSONObject();
				jsonRank.put("rank", i);

				JSONObject jsonValue = new JSONObject();
				jsonValue.put("value", user.getTotalVisits());

				visitsJSON.put(jsonRank);
				visitsJSON.put(jsonValue);

				JSONObject jsonVisits = new JSONObject();
				jsonVisits.put("visits", visitsJSON);

				if (jsonUser.has(username)) {
					categoryJSON = jsonUser.getJSONArray(username);

				} else {
					categoryJSON = new JSONArray();
				}

				categoryJSON.put(jsonVisits);
				jsonUser.put(username, categoryJSON);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			i++;
		}

		userQuery.setRange(0, 10);
		/*
		 * Fetching top 10 results of highest Contributors of links
		 */
		userQuery.setOrdering("totalLinks desc");
		userList = (List<User>) userQuery.execute();

		// writer.println(" ");
		// writer.println("Highest contributors");
		i = 1;
		for (User user : userList) {
			String username = user.getUsername();
			try {
				JSONArray contriJSON = new JSONArray();
				JSONObject jsonRank = new JSONObject();
				jsonRank.put("rank", i);

				JSONObject jsonValue = new JSONObject();
				jsonValue.put("value", user.getTotalLinks());

				contriJSON.put(jsonRank);
				contriJSON.put(jsonValue);

				JSONObject jsonContri = new JSONObject();
				jsonContri.put("contribution", contriJSON);

				if (jsonUser.has(username)) {
					categoryJSON = jsonUser.getJSONArray(username);

				} else {
					categoryJSON = new JSONArray();
				}

				categoryJSON.put(jsonContri);
				jsonUser.put(username, categoryJSON);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			i++;
		}

		userQuery.setRange(0, 10);
		/*
		 * Fetching top 10 results of highest Comments / attention seekers
		 */
		userQuery.setOrdering("totalComments desc");
		userList = (List<User>) userQuery.execute();

		// writer.println(" ");
		// writer.println("Highest Attention seekers");
		i = 1;
		for (User user : userList) {
			String username = user.getUsername();
			try {
				JSONArray commentJSON = new JSONArray();
				JSONObject jsonRank = new JSONObject();
				jsonRank.put("rank", i);

				JSONObject jsonValue = new JSONObject();
				jsonValue.put("value", user.getTotalComments());

				commentJSON.put(jsonRank);
				commentJSON.put(jsonValue);

				JSONObject jsonComment = new JSONObject();
				jsonComment.put("comments", commentJSON);

				if (jsonUser.has(username)) {
					categoryJSON = jsonUser.getJSONArray(username);

				} else {
					categoryJSON = new JSONArray();
				}

				categoryJSON.put(jsonComment);
				jsonUser.put(username, categoryJSON);

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			i++;
		}
		writer.println(jsonUser.toString());

	}

	/*
	 * Resets the Analyzer checkpoint to 0. If resetting to a particular time,
	 * we need to pass a parameter
	 */
	private void resetAnalyzerCheckPoint() {
		Query timeQuery = pm.newQuery(AnalyzerCheckpoint.class);
		List<AnalyzerCheckpoint> list = (List<AnalyzerCheckpoint>) timeQuery
				.execute();
		AnalyzerCheckpoint analyzerCheckpoint;
		/*
		 * Maintaining a simgle record of Analyzer checkpoint in the datastore
		 * If none, create one Else, update the existing one
		 */
		if (list.size() == 0) {
			analyzerCheckpoint = new AnalyzerCheckpoint();
			pm.makePersistent(analyzerCheckpoint);
		} else {
			pm.currentTransaction().begin();
			analyzerCheckpoint = list.get(0);
			analyzerCheckpoint.resetCheckPoint();
			pm.currentTransaction().commit();
			// writer.println(analyzerCheckpoint.getTime());

		}

	}

	// populates KeywordEntity objects with a particular trend's keyword and
	// naive popularity (number of posts).
	private List<KeywordEntity> putKeywordInEntities() {

		List eList = new ArrayList<KeywordEntity>();
		for (KeywordLinkMap e : keywordList) {
			if (e != null) {

				KeywordEntity kE = new KeywordEntity(e.getKeyword(),
						e.getBundleSetSize(), e);
				System.out.println(kE);
				eList.add(kE);
			}

		}
		return eList;
	}

	/*
	 * Checkpoint the Analyzer to a given time
	 */
	private void checkpointAnalyzer(long time) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(AnalyzerCheckpoint.class);
		try {
			@SuppressWarnings("unchecked")
			List<AnalyzerCheckpoint> results = (List<AnalyzerCheckpoint>) query
					.execute();
			if (!results.isEmpty()) {
				for (AnalyzerCheckpoint t : results) {
					t.setTime(time);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

	/**
	 * Read the UTC time of last processed Link
	 * 
	 * @return The UTC time
	 */
	public long getAnalyzerCheckpoint() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(AnalyzerCheckpoint.class);
		long ret = 9331619531L; // Here if the UTC table does not exsit or the
								// marker is not set properly, the aggregator
								// should do nothing, so set the return time
								// larger than any Link in database.
		try {
			@SuppressWarnings("unchecked")
			List<AnalyzerCheckpoint> results = (List<AnalyzerCheckpoint>) query
					.execute();
			if (!results.isEmpty()) {
				for (AnalyzerCheckpoint t : results) {
					ret = t.getTime();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return ret;
	}

}
