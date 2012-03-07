package edu.gatech.cc.Baconytics.appengine.DataModel;

import java.util.HashSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable = "true")
public class GAEKeyword {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String keyword;

	@Persistent(mappedBy = "keyword")
	private HashSet<GAEBundle> bundleSet;

	public GAEKeyword(String keyword) {
		this.keyword = keyword;
		bundleSet = new HashSet<GAEBundle>();
	}

	public GAEKeyword(String keyword, HashSet<GAEBundle> bundleSet) {
		this.keyword = keyword;
		this.bundleSet = bundleSet;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public Key getKey() {
		return key;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setRedditList(HashSet<GAEBundle> bundleSet) {
		this.bundleSet = bundleSet;
	}

	public HashSet<GAEBundle> getBundleSet() {
		return bundleSet;
	}

	public int getBundleSetSize() {
		return bundleSet.size();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj instanceof GAEKeyword
				&& this.key.equals(((GAEKeyword) obj).getKey());
	}

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("Keyword: ").append(getKeyword()).append(" Size: ")
				.append(bundleSet.size()).append("\n");
		for (GAEBundle bundle : bundleSet) {
			ret.append("\t" + bundle.toString());
		}
		return ret.toString();
	}
}
