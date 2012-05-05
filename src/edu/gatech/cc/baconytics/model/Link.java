package edu.gatech.cc.baconytics.model;

import java.util.List;

import javax.jdo.annotations.NotPersistent;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import org.json.JSONException;
import org.json.JSONObject;

@PersistenceCapable
public class Link implements Comparable<Link> {
	@PrimaryKey
	private String id;
	@Persistent
	private String author, domain, name, permalink, subreddit, subredditId,
			title, url;
	@Persistent
	private long createdUtc;
	@Persistent
	private boolean isSelf, over18;
	@NotPersistent
	private List<LinkStats> linkStats; // Helper for saving LinkStats

	public Link(String id, String name, String author, String domain,
			String permalink, String subreddit, String subredditId,
			String title, String url, long createdUtc, boolean isSelf,
			boolean over18, List<LinkStats> linkStats) {
		this.author = author;
		this.domain = domain;
		this.id = id;
		this.name = name;
		this.permalink = permalink;
		this.subreddit = subreddit;
		this.subredditId = subredditId;
		this.title = title;
		this.url = url;
		this.createdUtc = createdUtc;
		this.isSelf = isSelf;
		this.over18 = over18;
		this.linkStats = linkStats;
	}

	@Override
	public int compareTo(Link compArticle) {
		return (int) (createdUtc - compArticle.getCreatedUtc());
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

	public String getId() {
		return id;
	}

	public List<LinkStats> getLinkStats() {
		return linkStats;
	}

	public String getName() {
		return name;
	}

	public String getPermalink() {
		return permalink;
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
		toRet.append("id: ").append(getId()).append("\n");
		toRet.append("name: ").append(getName()).append("\n");
		toRet.append("author: ").append(getAuthor()).append("\n");
		toRet.append("domain: ").append(getDomain()).append("\n");
		toRet.append("permalink: ").append(getPermalink()).append("\n");
		toRet.append("subreddit: ").append(getSubreddit()).append("\n");
		toRet.append("subredditId: ").append(getSubredditId()).append("\n");
		toRet.append("title: ").append(getTitle()).append("\n");
		toRet.append("url: ").append(getUrl()).append("\n");
		toRet.append("createdUtc: ").append(getCreatedUtc()).append("\n");
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

	/**
	 * Convert the Link Object to JSON
	 * 
	 * @return JSONObject representation of a Link
	 */
	public JSONObject toJson() {
		JSONObject toRet = new JSONObject();
		try {
			toRet.put("id", getId());
			toRet.put("author", getAuthor());
			toRet.put("domain", getDomain());
			toRet.put("name", getName());
			toRet.put("permalink", getPermalink());
			toRet.put("subreddit", getSubreddit());
			toRet.put("subreddit_id", getSubredditId());
			toRet.put("title", getTitle());
			toRet.put("url", getUrl());
			toRet.put("created_utc", getCreatedUtc());
			toRet.put("is_self", isSelf());
			toRet.put("over_18", isOver18());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return toRet;
	}
}