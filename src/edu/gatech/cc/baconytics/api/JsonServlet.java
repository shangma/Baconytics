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

import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;

import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkStats;
import edu.gatech.cc.baconytics.model.PMF;

public class JsonServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static final int recordsPerHour = 100;
	Cache cache;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public JsonServlet() {
		super();

		try {
			Map props = new HashMap();
			props.put(GCacheFactory.EXPIRATION_DELTA, 3600);
			props.put(MemcacheService.SetPolicy.SET_ALWAYS, true);

			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			cache = cacheFactory.createCache(props);
		} catch (CacheException e) {
			// ...
		}
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter writer = resp.getWriter();

		String action = req.getParameter("action");
		String json = "";
		if (action == null || action.isEmpty()) {
			return;
		}
		if (action.equals("getLinkStats")) {
			json = getLinkStats();
		} else if (action.equals("refreshCache")) {
			// Refresh the cache as needed
			json = getLinkStats();
		}
		writer.println(json);
		writer.close();
	}

	/**
	 * Returns Links and LinkStats from MemCache (or from the datastore upon
	 * cache miss)
	 * 
	 * @return String containing JSON for Links and Linkstats
	 */
	private String getLinkStats() {
		String toRet;
		if (cache.containsKey("getLinkStats")) {
			toRet = (String) cache.get("getLinkStats");
		} else {
			toRet = fetchLinkStats(normalizeToMidnight(24 * recordsPerHour))
					.toString();
			cache.put("getLinkStats", toRet);
		}
		return toRet;
	}

	/**
	 * Returns Links and LinkStats from the datastore
	 * 
	 * @param range
	 *            The number of records returned
	 * @return JSONObject representation of Links and LinkStats
	 */
	@SuppressWarnings("unchecked")
	private JSONObject fetchLinkStats(int range) {
		// Initial JSON Variables
		JSONObject toRet = new JSONObject();
		JSONObject linkJson = new JSONObject();
		JSONArray linkStatsJsonArr = new JSONArray();
		Set<String> linkIds = new HashSet<String>();
		Set<Link> linkSet = new HashSet<Link>();

		// Query for the last [range] LinkStats
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(LinkStats.class);
		query.setOrdering("timeSeen desc, score desc");
		query.setRange(0, range);
		List<LinkStats> linkStats = (List<LinkStats>) query.execute();

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
				List<LinkStats> linkStatsList = timeLinkMap.get(timeSeen);
				JSONArray timeGroupJsonArr = new JSONArray();
				for (LinkStats e : linkStatsList) {
					timeGroupJsonArr.put(e.toJson());
				}
				JSONObject timeGroupJson = new JSONObject();
				timeGroupJson.put("time_seen", timeSeen);
				timeGroupJson.put("stats", timeGroupJsonArr);
				linkStatsJsonArr.put(timeGroupJson);
			}
			for (Link e : linkSet) {
				linkJson.put(e.getId(), e.toJson());
			}
			toRet.put("linkStats", linkStatsJsonArr);
			toRet.put("link", linkJson);
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
		return toRet;
	}

	/**
	 * Normalizes a range to the previous midnight (EST)
	 * 
	 * @param range
	 *            the number of records fetched
	 * @return the normalized range
	 */
	private int normalizeToMidnight(int range) {
		Calendar currentTime = Calendar
				.getInstance(TimeZone.getTimeZone("EST"));
		int currentHour = currentTime.get(Calendar.HOUR_OF_DAY);
		return range + currentHour * recordsPerHour;
	}
}