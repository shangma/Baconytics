package edu.gatech.cc.baconytics.analyzer;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AggregatorServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		List<Aggregator> aggregatorList = new LinkedList<Aggregator>();
		aggregatorList.add(new TopicAggregator());

		for (Aggregator e : aggregatorList) {
			e.aggregate();
		}

		resp.getWriter().println("Aggregator");
	}
}
