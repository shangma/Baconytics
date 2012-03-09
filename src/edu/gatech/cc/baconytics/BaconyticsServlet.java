package edu.gatech.cc.baconytics;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

@SuppressWarnings("serial")
public class BaconyticsServlet extends HttpServlet {
	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	throws IOException {

		// Gather Data
		PrintWriter writer = resp.getWriter();
		List<Article> articles = new LinkedList<Article>();
		Document doc = Jsoup.connect("http://www.reddit.com/r/all/top/").get();

		for (Element article : doc.select("div.thing")) {
			Article currArticle = new Article();
			for(Element data : article.select("a.title")) {
				currArticle.setTitle(data.html());
				currArticle.setLink(data.attr("href"));
			}
			for(Element data : article.select("div.score.unvoted")) {
				currArticle.setScore(data.html());
			}
			for(Element data : article.select("a.subreddit")) {
				currArticle.setSubreddit(data.html());
			}
			for(Element data : article.select("a.comments")) {
				currArticle.setComments(data.html().split(" ")[0]);
			}
			articles.add(currArticle);
		}

		// Sort Data [by Comments]
		Collections.sort(articles);

		// Present Data as HTML Table
		writer.println("<html>");
		writer.println("<head><script src='sorttable.js'></script></head>");
		writer.println("<body>");
		writer.println("<h1>Proof of Concept: /r/all 'top'</h1>");
		writer.println("<table class='sortable'>");
		writer.println("<tr><th>Title</th><th>Link</th><th>Score</th><th>Subreddit</th><th>Comments</th></tr>");
		for(Article article : articles) {
			writer.println("<tr><td>" + article.getTitle() + "</td>");
			writer.println("<td>" + article.getLink() + "</td>");
			writer.println("<td>" + article.getScore() + "</td>");
			writer.println("<td>" + article.getSubreddit() + "</td>");
			writer.println("<td>" + article.getComments() + "</td></tr>");
		}
		writer.println("</table>");
		writer.println("</body>");
		writer.println("</html>");
	}

	private class Article implements Comparable<Article> {
		private String title, link, score, subreddit, comments;

		public void setTitle(String title) {
			this.title = title;
		}

		public String getTitle() {
			return title;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public String getLink() {
			return link;
		}

		public void setScore(String score) {
			this.score = score;
		}

		public String getScore() {
			return score;
		}

		public void setSubreddit(String subreddit) {
			this.subreddit = subreddit;
		}

		public String getSubreddit() {
			return subreddit;
		}

		public void setComments(String comments) {
			this.comments = comments;
		}

		public String getComments() {
			return comments;
		}

		@Override
		public int compareTo(Article arg0) {
			return Integer.parseInt(arg0.getComments())-Integer.parseInt(this.comments);
		}

	}
}
