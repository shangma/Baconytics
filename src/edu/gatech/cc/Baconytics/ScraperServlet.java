package edu.gatech.cc.Baconytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressWarnings("serial")
public class ScraperServlet extends HttpServlet {
	private static final int TOTAL_TIME_REQUEST = 4;
	private static final int POSTS_PER_REQUEST = 25;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// Gather Data
		PrintWriter writer = resp.getWriter();
		List<Article> articles = new LinkedList<Article>();
		String baseUrl = "http://www.reddit.com/r/all/top/.json";

		// TODO(Andrew) Parse JSON into Article objects and store into database
		// Requesting top 100 topics
		try {
			List<Article> articleBundle;
			String strJson = "", lastTopic = "";
			for (int requests = 0; requests < TOTAL_TIME_REQUEST; requests++) {
				if (requests == 0) {
					strJson = getJSON(baseUrl);
				} else {
					strJson = getJSON(baseUrl + "?count=25&after=" + lastTopic);
				}
				articleBundle = parseJSON(strJson);
				if (articleBundle.size() > 0) {
					articles.addAll(articleBundle);
					lastTopic = articleBundle.get(articleBundle.size() - 1)
							.getId();
				} else {
					break;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		int i = 0;
		for (Article a : articles) {
			writer.println(i + " " + a.toString());
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

		// Build the JSON String
		reader = new BufferedReader(new InputStreamReader(connection
				.getInputStream()));
		while ((nextLine = reader.readLine()) != null) {
			toRet.append(nextLine);
		}
		connection.disconnect();
		return toRet.toString();
	}

	/**
	 * Parses the Reddit JSON in to a list of articles
	 * 
	 * @param strJson
	 *            the JSON String
	 * @return A list of Articles
	 * @throws JSONException
	 */
	private List<Article> parseJSON(String strJson) throws JSONException {
		List<Article> toRet = new LinkedList<Article>();
		String author, domain, id, name, permalink, selftext, subreddit, subredditId, title, url;
		int downs, numComments, score, ups;
		long createdUtc, timeSeen;
		boolean isSelf, over18;

		JSONObject json = new JSONObject(strJson);
		JSONArray arrJson = json.optJSONObject("data").optJSONArray("children");

		for (int i = 0; i < arrJson.length(); i++) {
			JSONObject articleJson = arrJson.getJSONObject(i).getJSONObject(
					"data");
			author = articleJson.getString("author");
			domain = articleJson.getString("domain");
			id = articleJson.getString("id");
			name = articleJson.getString("name");
			permalink = articleJson.getString("permalink");
			selftext = articleJson.getString("selftext");
			subreddit = articleJson.getString("subreddit");
			subredditId = articleJson.getString("subreddit_id");
			title = articleJson.getString("title");
			url = articleJson.getString("url");
			createdUtc = articleJson.getInt("created_utc");
			downs = articleJson.getInt("downs");
			numComments = articleJson.getInt("num_comments");
			score = articleJson.getInt("score");
			ups = articleJson.getInt("ups");
			timeSeen = System.currentTimeMillis();
			isSelf = articleJson.getBoolean("is_self");
			over18 = articleJson.getBoolean("over_18");
			Article myArticle = new Article(author, domain, id, name,
					permalink, selftext, subreddit, subredditId, title, url,
					createdUtc, downs, numComments, score, ups, timeSeen,
					isSelf, over18);
			toRet.add(myArticle);
		}

		return toRet;
	}
}
