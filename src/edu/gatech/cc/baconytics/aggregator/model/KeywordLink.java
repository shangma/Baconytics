package edu.gatech.cc.baconytics.aggregator.model;

import java.util.HashSet;

import edu.gatech.cc.baconytics.model.KeywordLinkMap;

public class KeywordLink {

	private String keyword;

	private KeywordLinkMap keywordLink;

	private HashSet<LinkRelevance> linkRelSet;

	public KeywordLink(String keyword) {
		this.keyword = keyword;
		linkRelSet = new HashSet<LinkRelevance>();
	}

	public KeywordLink(String keyword, HashSet<LinkRelevance> linkRelSet) {
		this.keyword = keyword;
		this.linkRelSet = linkRelSet;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public HashSet<LinkRelevance> getLinkRelSet() {
		return linkRelSet;
	}

	public void setLinkRelSet(HashSet<LinkRelevance> linkRelSet) {
		this.linkRelSet = linkRelSet;
	}

	public KeywordLinkMap getKeywordLink() {
		return keywordLink;
	}

	public void setKeywordLink(KeywordLinkMap keywordLink) {
		this.keywordLink = keywordLink;
	}

}
