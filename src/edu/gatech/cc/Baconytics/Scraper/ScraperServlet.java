package edu.gatech.cc.Baconytics.Scraper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.gatech.cc.Baconytics.DataModel.Link;
import edu.gatech.cc.Baconytics.DataModel.LinkStats;
import edu.gatech.cc.Baconytics.DataModel.PMF;

@SuppressWarnings("serial")
public class ScraperServlet extends HttpServlet {
	// Grab 8 * 25 links (GAE has a hard deadline of 30 sec)
	private static final int TOTAL_TIME_REQUEST = 8;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		// Gather Data
		PrintWriter writer = resp.getWriter();
		List<Link> links = new LinkedList<Link>();
		String baseUrl = "http://www.reddit.com/r/all/top/.json";
		PersistenceManager pm = PMF.get().getPersistenceManager();

		// Requesting top [TOTAL_TIME_REQUEST * 25] topics
		try {
			List<Link> linkBundle;
			String strJson = "", lastTopic = "";
			for (int requests = 0; requests < TOTAL_TIME_REQUEST; requests++) {
				if (requests == 0) {
					strJson = getJSON(baseUrl);
				} else {
					strJson = getJSON(baseUrl + "?count=25&after=" + lastTopic);
				}
				linkBundle = parseJSON(strJson);
				if (linkBundle.size() > 0) {
					links.addAll(linkBundle);
					lastTopic = linkBundle.get(linkBundle.size() - 1).getName();
				} else {
					break;
				}
				// Make fewer than one request per two seconds
				Thread.sleep(3 * 1000);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		// Store links in to datastore
		int i = 0;
		for (Link link : links) {
			pm.makePersistent(link);
			writer.println(i + " " + link.toString());
			for (LinkStats linkStats : link.getLinkStats()) {
				pm.makePersistent(linkStats);
				writer.println(linkStats.toString());
			}
			i++;
		}

	}

	/**
	 * Returns the JSON String given a URL
	 * 
	 * @param url
	 *            The JSON's URL
	 * @return the JSON String
	 * @throws IOException
	 */
	private String getJSON(String url) throws IOException {
		StringBuilder toRet = new StringBuilder();
		String nextLine = null;
		URL serverAddress = new URL(url);
		BufferedReader reader = null;

		// Send HTTP GET request to URL
		HttpURLConnection connection = (HttpURLConnection) serverAddress
				.openConnection();
		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(10 * 1000);
		connection.setRequestMethod("GET");
		connection.connect();

		// Print Headers
		System.out.println(connection.getResponseCode() + ": "
				+ connection.getResponseMessage());

		// Build the JSON String
		reader = new BufferedReader(new InputStreamReader(
				connection.getInputStream()));
		while ((nextLine = reader.readLine()) != null) {
			toRet.append(nextLine);
		}
		connection.disconnect();
		return toRet.toString();
	}

	/**
	 * Parses the Reddit JSON in to a list of Links
	 * 
	 * @param strJson
	 *            the JSON String
	 * @return A list of Links
	 * @throws JSONException
	 */
	private List<Link> parseJSON(String strJson) throws JSONException {
		List<Link> toRet = new LinkedList<Link>();
		String author, domain, id, name, permalink, selftext, subreddit, subredditId, title, url;
		int downs, numComments, score, ups;
		long createdUtc, timeSeen;
		boolean isSelf, over18;

		JSONObject json = new JSONObject(strJson);
		JSONArray arrJson = json.optJSONObject("data").optJSONArray("children");

		for (int i = 0; i < arrJson.length(); i++) {
			JSONObject linkJson = arrJson.getJSONObject(i)
					.getJSONObject("data");
			author = linkJson.getString("author");
			domain = linkJson.getString("domain");
			id = linkJson.getString("id");
			name = linkJson.getString("name");
			permalink = linkJson.getString("permalink");
			selftext = linkJson.getString("selftext");
			subreddit = linkJson.getString("subreddit");
			subredditId = linkJson.getString("subreddit_id");
			title = linkJson.getString("title");
			url = linkJson.getString("url");
			createdUtc = linkJson.getInt("created_utc");
			downs = linkJson.getInt("downs");
			numComments = linkJson.getInt("num_comments");
			score = linkJson.getInt("score");
			ups = linkJson.getInt("ups");
			timeSeen = System.currentTimeMillis();
			isSelf = linkJson.getBoolean("is_self");
			over18 = linkJson.getBoolean("over_18");
			ArrayList<LinkStats> linkStats = new ArrayList<LinkStats>();
			linkStats.add(new LinkStats(id, timeSeen, score, ups, downs,
					numComments, selftext));
			Link myLink = new Link(id, name, author, domain, permalink,
					subreddit, subredditId, title, url, createdUtc, isSelf,
					over18, linkStats);
			toRet.add(myLink);
		}

		return toRet;
	}
}
