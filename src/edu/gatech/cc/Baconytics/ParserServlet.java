package edu.gatech.cc.Baconytics;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

@SuppressWarnings("serial")
public class ParserServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		// Gather Data
		PrintWriter writer = resp.getWriter();
		List<Article> articles = new LinkedList<Article>();
		Document doc = Jsoup.connect("http://www.reddit.com/r/all/top/.json")
				.get();
		String author, domain, id, name, permalink, selftext, subreddit, subredditId, title, url;
		int createdUtc, downs, numComments, score, ups;
		boolean isSelf, over18;

		// TODO(Andrew) Parse JSON into Article objects and store into database

	}

}
