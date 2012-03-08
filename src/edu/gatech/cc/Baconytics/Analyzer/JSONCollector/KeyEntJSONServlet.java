package edu.gatech.cc.Baconytics.Analyzer.JSONCollector;

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

import edu.gatech.cc.Baconytics.Analyzer.KeywordEntity;
import edu.gatech.cc.Baconytics.DataModel.PMF;

public class KeyEntJSONServlet extends HttpServlet {

	private static PersistenceManager pm = PMF.get().getPersistenceManager();
	private List<KeywordEntity> keyEntList = new ArrayList<KeywordEntity>();
	// private List<KeywordEntity> keyEntList = new ArrayList<KeywordEntity>();
	private static PrintWriter writer;

	private ArrayList<JSONObject> jsonArr = new ArrayList<JSONObject>();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		writer = resp.getWriter();
		// JSONObject json; = new JSONObject();

		Query query = pm.newQuery(KeywordEntity.class);
		keyEntList = (List<KeywordEntity>) query.execute();
		for (KeywordEntity e : keyEntList) {
			try {
				jsonArr.add(new JSONObject().put("keyword", e.getKeyword())
						.put("score", e.getNumOfPosts()));
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		for (int i = 0; i < jsonArr.size(); i++) {
			writer.println(jsonArr.get(i));
		}

	}

}
