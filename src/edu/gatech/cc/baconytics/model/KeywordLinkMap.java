package edu.gatech.cc.baconytics.model;

import java.util.HashSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;


@PersistenceCapable(detachable = "true")
public class KeywordLinkMap {

	@Persistent(mappedBy = "keyword")
	private HashSet<LinkRelevanceBundle> bundleSet;

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String keyword;

	public KeywordLinkMap(String keyword) {
		this.keyword = keyword;
		bundleSet = new HashSet<LinkRelevanceBundle>();
	}

	public KeywordLinkMap(String keyword, HashSet<LinkRelevanceBundle> bundleSet) {
		this.keyword = keyword;
		this.bundleSet = bundleSet;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof KeywordLinkMap
				&& this.key.equals(((KeywordLinkMap) obj).getKey());
	}

	public HashSet<LinkRelevanceBundle> getBundleSet() {
		return bundleSet;
	}

	public int getBundleSetSize() {
		return bundleSet.size();
	}

	public Key getKey() {
		return key;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public void setLinkList(HashSet<LinkRelevanceBundle> bundleSet) {
		this.bundleSet = bundleSet;
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("Keyword: ").append(getKeyword()).append(" Size: ")
				.append(bundleSet.size()).append("\n");
		for (LinkRelevanceBundle bundle : bundleSet) {
			ret.append("\t" + bundle.toString());
		}
		return ret.toString();
	}
}
