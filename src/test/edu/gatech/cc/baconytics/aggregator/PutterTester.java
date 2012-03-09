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
import edu.gatech.cc.baconytics.aggregator.model.KeywordLink;
import edu.gatech.cc.baconytics.aggregator.model.LinkKeyword;
import edu.gatech.cc.baconytics.aggregator.nlp.AlchemyImpl;

@SuppressWarnings("serial")
public class PutterTester extends HttpServlet {

	private static PrintWriter writer;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		AlchemyImpl api = new AlchemyImpl();
		Feeder feeder = new Feeder();
		Extractor extractor = new Extractor();

		Set<LinkKeyword> source = feeder.feed();
		writer = resp.getWriter();
		HashSet<KeywordLink> results = (HashSet<KeywordLink>) extractor
				.extract(source, api);
		writer.println("Testing Putter");
		Putter putter = new Putter();
		putter.put(source, results);

	}
}
