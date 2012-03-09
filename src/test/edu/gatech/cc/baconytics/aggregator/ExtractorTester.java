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
import edu.gatech.cc.baconytics.aggregator.model.KeywordLink;
import edu.gatech.cc.baconytics.aggregator.model.LinkKeyword;
import edu.gatech.cc.baconytics.aggregator.model.LinkRelevance;
import edu.gatech.cc.baconytics.aggregator.nlp.AlchemyImpl;

@SuppressWarnings("serial")
public class ExtractorTester extends HttpServlet {

	private static PrintWriter writer;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		AlchemyImpl api = new AlchemyImpl();
		Feeder feeder = new Feeder();
		Set<LinkKeyword> source = feeder.feed();
		writer = resp.getWriter();

		writer.println("Testing Feeder");
		Extractor extractor = new Extractor();
		HashSet<KeywordLink> results = (HashSet<KeywordLink>) extractor
				.extract(source, api);
		if (results == null) {
			writer.println("Results set is null");
			return;
		}
		for (KeywordLink item : results) {
			writer.println(item.getKeyword());
			HashSet<LinkRelevance> redrel = item.getLinkRelSet();
			for (LinkRelevance lr : redrel) {
				LinkKeyword link = lr.getLinkKeyword();
				writer.println("\tlink:" + link.getId() + " rel "
						+ lr.getRelevance());
			}
		}
		writer.println("=====================================================");
		for (LinkKeyword link : source) {
			writer.println(link.getId());
			HashSet<KeywordLink> krrSet = link.getKeywordSet();
			for (KeywordLink klr : krrSet) {
				writer.println("\t" + klr.getKeyword());
			}
		}
	}
}
