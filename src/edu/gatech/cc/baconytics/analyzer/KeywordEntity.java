package edu.gatech.cc.baconytics.analyzer;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

import edu.gatech.cc.baconytics.model.KeywordLinkMap;

@PersistenceCapable(detachable = "true")
public class KeywordEntity {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private String keyword;

	@Persistent
	private int numOfPosts = 1;

	@Persistent
	private KeywordLinkMap keywordLinkMap;

	/*
	 * @Persistent private HashSet<Key> keywordSet;
	 */
	public KeywordEntity(String keyword, int numOfPosts, KeywordLinkMap k) {
		this.keyword = keyword;
		this.numOfPosts = numOfPosts;
		this.keywordLinkMap = k;
		// keywordSet = new HashSet<Key>();
	}

	// public GAEReddit(String id, HashSet<Key> keywordSet) {
	// this.id = id;
	// this.keywordSet = keywordSet;
	// }

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setNumOfPosts(int numOfPosts) {
		this.numOfPosts = numOfPosts;
	}

	public int getNumOfPosts() {
		return numOfPosts;
	}

	public KeywordLinkMap getKeywordLinkMap() {
		return this.keywordLinkMap;
	}

	/*
	 * public void setKeywordSet(HashSet<Key> keywordSet) { this.keywordSet =
	 * keywordSet; }
	 * 
	 * public HashSet<Key> getKeywordSet() { return keywordSet; }
	 * 
	 * public Key getKey() { return key; }
	 * 
	 * public void setKey(Key key) { this.key = key; }
	 * 
	 * @Override public String toString() { StringBuilder ret = new
	 * StringBuilder(); ret.append("id: ").append(id).append(" Total: ")
	 * .append(keywordSet.size()).append("\n"); for (Key key : keywordSet) {
	 * ret.append("\t").append(key.toString()).append("\n"); } return
	 * ret.toString(); }
	 */

	// @Override
	// public String toString() {
	// StringBuilder toRet = new StringBuilder();
	// toRet.append("Keyword: ")
	// .append(getKeyword())
	// .append("\n")
	// .append(toRet.append("Number of Posts: ")
	// .append(getNumOfPosts()).append("\n"));
	// return toRet.toString();
	// }

	@Override
	public String toString() {
		StringBuilder ret = new StringBuilder();
		ret.append("Keyword: ").append(getKeyword()).append("\nNumOfPosts: ")
				.append(getNumOfPosts()).append("\n");
		return ret.toString();
	}
}
