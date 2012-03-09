package test.edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.baconytics.aggregator.Feeder;
import edu.gatech.cc.baconytics.aggregator.model.LinkKeyword;

@SuppressWarnings("serial")
public class FeederTester extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		Feeder feeder = new Feeder();
		PrintWriter writer = resp.getWriter();
		writer.println("Testing Feeder");
		Set<LinkKeyword> results = feeder.feed();
		writer.println("Total: " + results.size() + " UTCTime: "
				+ feeder.fetchLastUTCTime());
		for (LinkKeyword linkKeyword : results) {
			writer.println(linkKeyword.toString());
		}
	}
}
