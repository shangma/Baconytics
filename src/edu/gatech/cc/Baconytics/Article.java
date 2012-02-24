package edu.gatech.cc.Baconytics;

public final class Article implements Comparable<Article> {
	private final String author, domain, id, name, permalink, selftext,
			subreddit, subredditId, title, url;
	private final int createdUtc, downs, numComments, score, ups;
	private final boolean isSelf, over18;

	public Article(String author, String domain, String id, String name,
			String permalink, String selftext, String subreddit,
			String subredditId, String title, String url, int createdUtc,
			int downs, int numComments, int score, int ups, boolean isSelf,
			boolean over18) {
		this.author = author;
		this.domain = domain;
		this.id = id;
		this.name = name;
		this.permalink = permalink;
		this.selftext = selftext;
		this.subreddit = subreddit;
		this.subredditId = subredditId;
		this.title = title;
		this.url = url;
		this.createdUtc = createdUtc;
		this.downs = downs;
		this.numComments = numComments;
		this.score = score;
		this.ups = ups;
		this.isSelf = isSelf;
		this.over18 = over18;
	}

	@Override
	public int compareTo(Article compArticle) {
		return score - compArticle.getScore();
	}

	public String getAuthor() {
		return author;
	}

	public int getCreatedUtc() {
		return createdUtc;
	}

	public String getDomain() {
		return domain;
	}

	public int getDowns() {
		return downs;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getNumComments() {
		return numComments;
	}

	public String getPermalink() {
		return permalink;
	}

	public int getScore() {
		return score;
	}

	public String getSelftext() {
		return selftext;
	}

	public String getSubreddit() {
		return subreddit;
	}

	public String getSubredditId() {
		return subredditId;
	}

	public String getTitle() {
		return title;
	}

	public int getUps() {
		return ups;
	}

	public String getUrl() {
		return url;
	}

	public boolean isOver18() {
		return over18;
	}

	public boolean isSelf() {
		return isSelf;
	}

}