package edu.gatech.cc.baconytics.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkStats;
import edu.gatech.cc.baconytics.model.PMF;

public class JsonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter writer = resp.getWriter();
		Cache cache;

		Map props = new HashMap();
		props.put(GCacheFactory.EXPIRATION_DELTA, 3600);

		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			cache = cacheFactory.createCache(props);

			String action = req.getParameter("action");
			String json;
			if (action.equals("getLinkStats")) {
				if (cache.containsKey("getLinkStats")) {
					json = (String) cache.get("getLinkStats");
				} else {
					json = getLinkStats(normalizedToMidnight(2400)).toString();
					cache.put("getLinkStats", json);
				}
				writer.println(json);
			} else {
				writer.println(normalizedToMidnight(2400));
			}
		} catch (CacheException e) {
			// ...
		}

		writer.close();
	}

	private int normalizedToMidnight(int seedRange) {
		Calendar currentTime = Calendar
				.getInstance(TimeZone.getTimeZone("EST"));
		int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
		return seedRange + currentHour * 100;
	}

	/**
	 * Returns Links and LinkStats from the datastore
	 * 
	 * @param range
	 *            The number of records returned
	 * @return JSONObject representation of LinkStats
	 */
	@SuppressWarnings("unchecked")
	private JSONObject getLinkStats(int range) {
		// Query for the last [range] LinkStats
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(LinkStats.class);
		query.setOrdering("timeSeen desc, score desc");
		query.setRange(0, range);
		List<LinkStats> linkStats = (List<LinkStats>) query.execute();

		// Initial JSON Variables
		JSONObject jsonObj = new JSONObject();
		JSONObject linkArr = new JSONObject();
		JSONArray timeWrapperArr = new JSONArray();
		Set<String> linkIds = new HashSet<String>();
		Set<Link> linkSet = new HashSet<Link>();

		// Group LinkStats by time seen
		Map<Long, List<LinkStats>> timeLinkMap = new HashMap<Long, List<LinkStats>>();
		System.out.println("linkStats.size(): " + linkStats.size());
		for (LinkStats e : linkStats) {
			List<LinkStats> linkStatsList = timeLinkMap.get(e.getTimeSeen());
			if (linkStatsList == null) {
				linkStatsList = new LinkedList<LinkStats>();
			}
			linkStatsList.add(e);
			timeLinkMap.put(e.getTimeSeen(), linkStatsList);

			// Store the link id for querying links
			linkIds.add(e.getId());
		}

		// Query the links
		System.out.println("linkIds.size(): " + linkIds.size());
		for (String id : linkIds) {
			Link myLink = pm.getObjectById(Link.class, id);
			linkSet.add(myLink);
		}

		// Build the JSON
		try {
			List<Long> keyList = new ArrayList<Long>(timeLinkMap.keySet());
			Collections.sort(keyList);
			for (Long timeSeen : keyList) {
				JSONArray linkStatsArr = new JSONArray();
				List<LinkStats> linkStatsList = timeLinkMap.get(timeSeen);
				for (LinkStats e : linkStatsList) {
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
				JSONObject timeJson = new JSONObject();
				timeJson.put("time_seen", timeSeen);
				timeJson.put("stats", linkStatsArr);
				timeWrapperArr.put(timeJson);
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
				linkArr.put(e.getId(), linkJson);
			}

			jsonObj.put("linkStats", timeWrapperArr);
			jsonObj.put("link", linkArr);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return jsonObj;
	}
}