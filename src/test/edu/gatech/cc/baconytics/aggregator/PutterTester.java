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
import edu.gatech.cc.baconytics.aggregator.Putter;
import edu.gatech.cc.baconytics.aggregator.nlp.AlchemyImpl;
import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;

@SuppressWarnings("serial")
public class PutterTester extends HttpServlet {

    private static PrintWriter writer;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        AlchemyImpl api = new AlchemyImpl();
        Set<LinkKeywordMap> source = Feeder.feed();
        writer = resp.getWriter();
        HashSet<KeywordLinkMap> results = (HashSet<KeywordLinkMap>) Extractor
                .extract(source, api);
        writer.println("Testing Putter");
        Putter.put(source, results);
    }
}
