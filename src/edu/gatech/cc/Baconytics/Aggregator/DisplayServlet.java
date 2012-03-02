package edu.gatech.cc.Baconytics.Aggregator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;

import edu.gatech.cc.Baconytics.DataModel.PMF;

@SuppressWarnings("serial")
public class DisplayServlet extends HttpServlet {
	private static PrintWriter writer;

	private static PersistenceManager pm = PMF.get().getPersistenceManager();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		writer = resp.getWriter();
		writer.println("Print out all tags");
		// Reopen the pm
		Query query = pm.newQuery(Keyword.class);
		try {
			@SuppressWarnings("unchecked")
			List<Keyword> results = (List<Keyword>) query.execute();
			writer.println("size " + results.size());
			if (!results.isEmpty()) {
				int i = 1;
				for (Keyword e : results) {
					writer.println("keyword " + i++ + ": " + e.getKeyword());
					HashSet<Bundle> sb = e.getBundleSet();
					writer.println("REDDITS: " + sb.size());
					for (Bundle b : sb) {
						// Key k =
						// KeyFactory.createKey(Reddit.class.getSimpleName(),
						// b.redditName);
						Reddit reddit = pm.getObjectById(Reddit.class,
								b.redditName);
						writer.println("\t" + reddit.getTitle() + " "
								+ b.relevance);
					}
					writer
							.println("============================================");
				}
				writer.println();
			} else {
				// ... no results ...
			}
		} finally {
			query.closeAll();
		}

		writer
				.println("++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

		query = pm.newQuery(Reddit.class);
		try {
			@SuppressWarnings("unchecked")
			List<Reddit> results = (List<Reddit>) query.execute();
			writer.println("size " + results.size());
			if (!results.isEmpty()) {
				int i = 1;
				for (Reddit e : results) {
					writer.println("reddit " + i++ + ": " + e.getTitle());
					HashSet<Key> sb = e.getKeywordSet();
					writer.println("TAGS: " + sb.size());
					for (Key b : sb) {
						Keyword kw = pm.getObjectById(Keyword.class, b);
						writer.println("\t" + kw.getKeyword() + " | ");
					}
					writer
							.println("============================================");
				}
				writer.println();
			} else {
				// ... no results ...
			}
		} finally {
			query.closeAll();
		}
	}
}
