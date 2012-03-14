package edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.baconytics.aggregator.nlp.AlchemyImpl;
import edu.gatech.cc.baconytics.aggregator.nlp.NlpApi;
import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;

@SuppressWarnings("serial")
public class AggregatorServlet extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        NlpApi nlpApi = new AlchemyImpl();
        Set<LinkKeywordMap> links = Feeder.feed();
        Set<KeywordLinkMap> extracted = Extractor.extract(links, nlpApi);
        Putter.put(links, extracted);
        resp.getWriter().println("<a href='./show'>See Results</a>");
    }
}
