package edu.gatech.cc.baconytics.analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
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
	private static final int RETURN_THRESHHOLD = 10;
	private static PersistenceManager pm = PMF.get().getPersistenceManager();
	private static PrintWriter writer;
	private List<KeywordLinkMap> keywordList;
	private List<KeywordEntity> keyEntList;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {
		try {
			writer = res.getWriter();
		} catch (IOException e1) {
			System.out.println("IO exception!");
			e1.printStackTrace();
		}

		Query query = pm.newQuery(KeywordLinkMap.class);
		keywordList = (List<KeywordLinkMap>) query.execute();

		// Collections.sort(keywordList, new KeywordComparator());

		keyEntList = putKeywordInEntities();

		// sort keyEntList based on the number of posts about a particular
		// topic.
		Collections.sort(keyEntList, new EntityComparator());

		// Print sorted keyEntList
		if (!keyEntList.isEmpty()) {
			writer.println("Total: " + keywordList.size() + "\n");
			for (KeywordEntity e : keyEntList) {
				writer.println(e.toString());
			}
		}

		// store keyEntList into datastore
		for (int i = 0; i < RETURN_THRESHHOLD; i++) {
			pm.makePersistent(keyEntList.get(i));
			writer.println(i + " " + keyEntList.toString());
		}

	}

	// populates KeywordEntity objects with a particular trend's keyword and
	// nieve popularity (number of posts).
	private List<KeywordEntity> putKeywordInEntities() {

		Query query = pm.newQuery(KeywordLinkMap.class);
		List eList = new ArrayList<KeywordEntity>();
		for (KeywordLinkMap e : keywordList) {
			KeywordEntity kE = new KeywordEntity(e.getKeyword(),
					e.getBundleSetSize());
			// System.out.println(kE);
			eList.add(kE);

		}
		return eList;
	}
}
