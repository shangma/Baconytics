package test.edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.baconytics.aggregator.Feeder;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;

@SuppressWarnings("serial")
public class FeederTester extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        PrintWriter writer = resp.getWriter();
        writer.println("Testing Feeder<br/>");
        Set<LinkKeywordMap> results = Feeder.feed();
        writer.println("Total: " + results.size() + " UTCTime: "
                + Feeder.fetchLastUTCTime() + "<br/>");
        for (LinkKeywordMap linkKeyword : results) {
            writer.println(linkKeyword.getId() + "<br/>\n\t"
                    + linkKeyword.getTitle() + "<br/>");
        }
    }
}
