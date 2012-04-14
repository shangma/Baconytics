package test.edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.baconytics.aggregator.AggregatorServlet;
import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.PMF;

@SuppressWarnings("serial")
public class Show extends HttpServlet {

    private static PrintWriter writer;
    private static PersistenceManager pm = PMF.get().getPersistenceManager();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        writer = resp.getWriter();
        Time tm = new Time((new AggregatorServlet()).fetchLastUTCTime()
                .getTime());
        writer.println("Last update: " + tm.toString() + "<br/>");
        // Print all topics with keywords
        writer.println("============= Title -> Keyword =================<br/>");
        Query query = pm.newQuery(LinkKeywordMap.class);
        @SuppressWarnings("unchecked")
        List<LinkKeywordMap> results = (List<LinkKeywordMap>) query.execute();
        if (!results.isEmpty()) {
            writer.println("Total: " + results.size() + "<br/>");
            for (LinkKeywordMap e : results) {
                writer.println(e.toString() + "<br/>");
            }
        }

        writer.println("============= Keyword -> Title =================<br/>");
        query = pm.newQuery(KeywordLinkMap.class);
        @SuppressWarnings("unchecked")
        List<KeywordLinkMap> kresults = (List<KeywordLinkMap>) query.execute();
        if (!kresults.isEmpty()) {
            writer.println("Total: " + kresults.size() + "<br/>");
            for (KeywordLinkMap e : kresults) {
                writer.println(e.toString() + "<br/>");
            }
        }
    }
}
