package edu.gatech.cc.Baconytics.Analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.Baconytics.DataModel.PMF;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEKeyword;

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
		Query query = pm.newQuery(GAEKeyword.class);
		List<GAEKeyword> keywordList = (List<GAEKeyword>) query.execute();
		Collections.sort(keywordList, new KeywordComparator());
		if (!keywordList.isEmpty()) {
			writer.println("Total: " + keywordList.size() + "\n");
			for (GAEKeyword e : keywordList) {
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
