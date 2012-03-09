package test.edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.baconytics.aggregator.model.KeywordRelevance;
import edu.gatech.cc.baconytics.aggregator.nlp.AlchemyImpl;

@SuppressWarnings("serial")
public class NlpApiTester extends HttpServlet {

	private static PrintWriter writer;
	final private static String str = "I am just testing this servlet to see if Alchemy API really works";

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		AlchemyImpl api = new AlchemyImpl();
		writer = resp.getWriter();
		writer.println("Test String: " + str);
		Set<KeywordRelevance> ret = api.process(str);
		for (KeywordRelevance kr : ret) {
			writer.println("keyword: " + kr.getKeyword() + " rel: "
					+ kr.getRelevance());
		}
	}
}