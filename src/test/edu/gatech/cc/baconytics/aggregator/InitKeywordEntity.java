package test.edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.LinkRelevanceBundle;
import edu.gatech.cc.baconytics.model.PMF;

@SuppressWarnings("serial")
public class InitKeywordEntity extends HttpServlet {

	private static PrintWriter writer;
	private static PersistenceManager pm = PMF.get().getPersistenceManager();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Key linkKey = KeyFactory.createKey(LinkKeywordMap.class.getSimpleName(),
				"@@");
		Key kwKey = KeyFactory.createKey(KeywordLinkMap.class.getSimpleName(),
				"@@");
		KeywordLinkMap keyword = new KeywordLinkMap("@@");
		keyword.setKey(kwKey);
		LinkRelevanceBundle bundle = new LinkRelevanceBundle(linkKey, keyword,
				-1.0);
		HashSet<LinkRelevanceBundle> bdSet = new HashSet<LinkRelevanceBundle>();
		bdSet.add(bundle);
		keyword.setLinkList(bdSet);
		pm.makePersistent(keyword);
		writer = resp.getWriter();
		writer.println("Initialized KeywordLinkMap Entity");
	}
}
