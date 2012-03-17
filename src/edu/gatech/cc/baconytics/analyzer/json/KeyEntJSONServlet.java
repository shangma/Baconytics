package edu.gatech.cc.baconytics.analyzer.json;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import edu.gatech.cc.baconytics.analyzer.KeywordEntity;
import edu.gatech.cc.baconytics.model.PMF;

public class KeyEntJSONServlet extends HttpServlet {

	private static PersistenceManager pm = PMF.get().getPersistenceManager();
	private List<KeywordEntity> keyEntList = new ArrayList<KeywordEntity>();
	// private List<KeywordEntity> keyEntList = new ArrayList<KeywordEntity>();
	private static PrintWriter writer;

	// private ArrayList<JSONObject> jsonArr = new ArrayList<JSONObject>();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		resp.setContentType("text/html");
		writer = resp.getWriter();

		// resp.get
		// JSONObject json; = new JSONObject();

		Query query = pm.newQuery(KeywordEntity.class);
		keyEntList = (List<KeywordEntity>) query.execute();
		// jsonArr.clear();
		// int k = 0;
		/*
		 * try { writer.println(new JSONObject().put("keyword", "KeyEntity")); }
		 * catch (JSONException e2) { // TODO Auto-generated catch block
		 * e2.printStackTrace(); }
		 */

		writer.println("{\"keyEnt\": [");
		boolean first = true;
		for (KeywordEntity e : keyEntList) {
			try {

				if (first) {
					first = false;
				} else {
					writer.println(",");
				}

				// if (k > 9) {
				// // break;
				// }
				/*
				 * jsonArr.add(new JSONObject().put("keyword", e.getKeyword())
				 * .put("score", e.getNumOfPosts()));
				 */
				// writer.println("test for input " + jsonArr.get(k)
				// + " count is " + k);
				// k++;

				writer.print(new JSONObject().put("keyword", e.getKeyword())
						.put("score", e.getNumOfPosts()));

				// writer.println(",");

			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		writer.println("] }");

		/*
		 * redundant code slated for removal for (int i = 0; i < jsonArr.size();
		 * i++) { writer.println(jsonArr.get(i)); }
		 */
		writer.flush();
		writer.close();

	}
}