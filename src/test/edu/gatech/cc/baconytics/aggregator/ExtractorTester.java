package test.edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.baconytics.aggregator.Extractor;
import edu.gatech.cc.baconytics.aggregator.Feeder;
import edu.gatech.cc.baconytics.aggregator.nlp.AlchemyImpl;
import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.LinkRelevanceBundle;

@SuppressWarnings("serial")
public class ExtractorTester extends HttpServlet {

    private static PrintWriter writer;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        AlchemyImpl api = new AlchemyImpl();
        Set<LinkKeywordMap> source = Feeder.feed();
        writer = resp.getWriter();

        writer.println("Testing Feeder<br/>");
        HashSet<KeywordLinkMap> results = (HashSet<KeywordLinkMap>) Extractor
                .extract(source, api);
        if (results == null) {
            writer.println("Results set is null<br/>");
            return;
        }
        writer.println("====== Print the Keyword -> Link map ======================<br/>");
        for (KeywordLinkMap item : results) {
            writer.println(item.getKeyword() + "<br/>");
            HashSet<LinkRelevanceBundle> redrel = item.getBundleSet();
            for (LinkRelevanceBundle lr : redrel) {
                LinkKeywordMap link = lr.getLink();
                writer.println("\tlink:" + link.getId() + " rel "
                        + lr.getRelevance() + "<br/>");
            }
        }
    }
}
