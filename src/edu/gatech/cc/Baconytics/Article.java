package edu.gatech.cc.Baconytics;

public final class Article implements Comparable<Article> {
	private final String author, domain, id, name, permalink, selftext,
			subreddit, subredditId, title, url;
	private final int downs, numComments, score, ups;
	private final long createdUtc, timeSeen;
	private final boolean isSelf, over18;

	public Article(String author, String domain, String id, String name,
			String permalink, String selftext, String subreddit,
			String subredditId, String title, String url, long createdUtc,
			int downs, int numComments, int score, int ups, long timeSeen,
			boolean isSelf, boolean over18) {
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
		this.timeSeen = timeSeen;
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

	public long getCreatedUtc() {
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

	public long getTimeSeen() {
		return timeSeen;
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

	@Override
	public String toString() {
		StringBuilder toRet = new StringBuilder();
		toRet.append("author: ").append(getAuthor()).append("\n");
		toRet.append("domain: ").append(getDomain()).append("\n");
		toRet.append("id: ").append(getId()).append("\n");
		toRet.append("name: ").append(getName()).append("\n");
		toRet.append("permalink: ").append(getPermalink()).append("\n");
		toRet.append("selftext: ").append(getSelftext()).append("\n");
		toRet.append("subreddit: ").append(getSubreddit()).append("\n");
		toRet.append("subredditId: ").append(getSubredditId()).append("\n");
		toRet.append("title: ").append(getTitle()).append("\n");
		toRet.append("url: ").append(getUrl()).append("\n");
		toRet.append("createdUtc: ").append(getCreatedUtc()).append("\n");
		toRet.append("downs: ").append(getDowns()).append("\n");
		toRet.append("numComments: ").append(getNumComments()).append("\n");
		toRet.append("score: ").append(getScore()).append("\n");
		toRet.append("ups: ").append(getUps()).append("\n");
		toRet.append("timeSeen: ").append(getTimeSeen()).append("\n");
		if (isSelf) {
			toRet.append("isSelf: true\n");
		} else {
			toRet.append("isSelf: false\n");
		}
		if (over18) {
			toRet.append("over18: true\n");
		} else {
			toRet.append("over18: false\n");
		}
		return toRet.toString();
	}

}