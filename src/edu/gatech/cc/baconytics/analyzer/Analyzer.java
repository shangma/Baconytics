package edu.gatech.cc.baconytics.analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.PMF;

public class Analyzer extends HttpServlet {
	private static PersistenceManager pm = PMF.get().getPersistenceManager();
	private static PrintWriter writer;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		try {
			writer = res.getWriter();
		} catch (IOException e1) {
			System.out.println("IO exception!");
			e1.printStackTrace();
		}
		Query query = pm.newQuery(KeywordLinkMap.class);
		List<KeywordLinkMap> keywordList = (List<KeywordLinkMap>) query.execute();
		Collections.sort(keywordList, new KeywordComparator());
		if (!keywordList.isEmpty()) {
			writer.println("Total: " + keywordList.size() + "\n");
			for (KeywordLinkMap e : keywordList) {
				writer.println(e.toString());
			}

			/*
			 * for (GAEKeyword e : keywordList) { for (GAEBundle b :
			 * e.getBundleSet()) { b.get } }
			 */
		}
		/*
		 * Getting only top 25 results
		 */

	}
}
