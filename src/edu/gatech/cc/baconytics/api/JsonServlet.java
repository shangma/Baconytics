package edu.gatech.cc.baconytics.api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
			writer.println(getLinkStats().toString());
		}

		writer.close();
	}

	private JSONObject getLinkStats() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(LinkStats.class);
		query.setOrdering("timeSeen desc, score desc");
		query.setRange(0, 2400);

		@SuppressWarnings("unchecked")
		List<LinkStats> linkStats = (List<LinkStats>) query.execute();

		JSONObject jsonObj = new JSONObject();
		JSONArray jsonArr = new JSONArray();
		try {
			for (LinkStats e : linkStats) {
				JSONObject linkStatsJson = new JSONObject();
				linkStatsJson.put("downs", e.getDowns());
				linkStatsJson.put("num_comments", e.getNumComments());
				linkStatsJson.put("score", e.getScore());
				linkStatsJson.put("time_seen", e.getTimeSeen());
				linkStatsJson.put("ups", e.getUps());
				linkStatsJson.put("id", e.getId());
				jsonArr.put(linkStatsJson);
			}
			jsonObj.put("linkStats", jsonArr);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return jsonObj;
	}
}