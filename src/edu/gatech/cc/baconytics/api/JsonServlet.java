package edu.gatech.cc.baconytics.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkStats;
import edu.gatech.cc.baconytics.model.PMF;

public class JsonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter writer = resp.getWriter();

		String action = req.getParameter("action");
		if (action.equals("getLinkStats")) {
			writer.println(getLinkStats(2400).toString());
		}

		writer.close();
	}

	/**
	 * Returns Links and LinkStats in the form of JSON
	 * 
	 * @param range
	 *            The number of records returned
	 * @return JSONObject representation of LinkStats
	 */
	private JSONObject getLinkStats(int range) {
		// Query for the last [range] LinkStats
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(LinkStats.class);
		query.setOrdering("timeSeen desc, score desc");
		query.setRange(0, range);

		@SuppressWarnings("unchecked")
		List<LinkStats> linkStats = (List<LinkStats>) query.execute();

		JSONObject jsonObj = new JSONObject();
		Set<Link> linkSet = new HashSet<Link>();
		JSONArray linkArr = new JSONArray();
		JSONArray linkStatsArr = new JSONArray();

		try {
			for (LinkStats e : linkStats) {
				// Query the Link and add it to the set
				Link myLink = pm.getObjectById(Link.class, e.getId());
				linkSet.add(myLink);

				// Build JSON Data for a single LinkStat
				JSONObject linkStatsJson = new JSONObject();
				linkStatsJson.put("downs", e.getDowns());
				linkStatsJson.put("num_comments", e.getNumComments());
				linkStatsJson.put("score", e.getScore());
				linkStatsJson.put("time_seen", e.getTimeSeen());
				linkStatsJson.put("ups", e.getUps());
				linkStatsJson.put("id", e.getId());
				linkStatsArr.put(linkStatsJson);
			}
			for (Link e : linkSet) {
				// Build JSON Data for a single Link
				JSONObject linkJson = new JSONObject();
				linkJson.put("id", e.getId());
				linkJson.put("author", e.getAuthor());
				linkJson.put("domain", e.getDomain());
				linkJson.put("name", e.getName());
				linkJson.put("permalink", e.getPermalink());
				linkJson.put("subreddit", e.getSubreddit());
				linkJson.put("subreddit_id", e.getSubredditId());
				linkJson.put("title", e.getTitle());
				linkJson.put("url", e.getUrl());
				linkJson.put("created_utc", e.getCreatedUtc());
				linkJson.put("is_self", e.isSelf());
				linkJson.put("over_18", e.isOver18());
				linkArr.put(linkJson);
			}

			jsonObj.put("link", linkArr);
			jsonObj.put("linkStats", linkStatsArr);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return jsonObj;
	}
}