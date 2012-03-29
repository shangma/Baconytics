package edu.gatech.cc.baconytics.analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.PMF;

@SuppressWarnings("serial")
public class trendLinks extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		PrintWriter writer = resp.getWriter();
		writer = resp.getWriter();
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = null;
		String[] trendTitle = new String[10];
		String[] trendLink = new String[10];
		String baselink = "http://reddit.com";

		try {
			query = pm.newQuery(Link.class);
			List<Link> results = (List<Link>) query.execute();
			System.out.println(results.size());
			if (!results.isEmpty()) {
				int i = 0;
				for (Link e : results) {
					if (i == 10) {
						break;
					}
					String title = e.getTitle();
					String link = e.getPermalink();
					System.out.println(title + " " + baselink + link);
					trendTitle[i] = title;
					trendLink[i] = baselink + link;
					i++;
				}
				JSONArray json = new JSONArray();
				for (int j = 0; j < 10; j++) {
					JSONObject item = new JSONObject();
					item.put("title", trendTitle[j]);
					item.put("link", trendLink[j]);
					json.put(item);
				}
				writer.println(json.toString());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
}
