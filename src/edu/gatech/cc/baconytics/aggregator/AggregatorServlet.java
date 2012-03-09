package edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.baconytics.aggregator.model.KeywordLink;
import edu.gatech.cc.baconytics.aggregator.model.LinkKeyword;
import edu.gatech.cc.baconytics.aggregator.nlp.AlchemyImpl;
import edu.gatech.cc.baconytics.aggregator.nlp.NlpApi;

@SuppressWarnings("serial")
public class AggregatorServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Feeder feeder = new Feeder();
		Extractor extractor = new Extractor();
		Putter putter = new Putter();
		NlpApi nlpApi = new AlchemyImpl();

		Set<LinkKeyword> links = feeder.feed();
		Set<KeywordLink> extracted = extractor.extract(links, nlpApi);
		putter.put(links, extracted);
		resp.getWriter().println("<a href='./show'>See Results</a>");
	}
}
