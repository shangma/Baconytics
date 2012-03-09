package edu.gatech.cc.baconytics.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;

@PersistenceCapable
public class LinkStats implements Comparable<LinkStats> {
	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private int downs, numComments, score, ups;
	@Persistent
	private String id;
	@Persistent
	private Text selftext;
	@Persistent
	private long timeSeen;

	public LinkStats(String id, long timeSeen, int score, int ups, int downs,
			int numComments, String selftext) {
		this.downs = downs;
		this.numComments = numComments;
		this.score = score;
		this.ups = ups;
		this.id = id;
		this.selftext = new Text(selftext);
		this.timeSeen = timeSeen;
	}

	@Override
	public int compareTo(LinkStats compStats) {
		return (int) (compStats.getTimeSeen() - timeSeen);
	}

	public int getDowns() {
		return downs;
	}

	public String getId() {
		return id;
	}

	public Key getKey() {
		return key;
	}

	public int getNumComments() {
		return numComments;
	}

	public int getScore() {
		return score;
	}

	public Text getSelftext() {
		return selftext;
	}

	public long getTimeSeen() {
		return timeSeen;
	}

	public int getUps() {
		return ups;
	}

	@Override
	public String toString() {
		StringBuilder toRet = new StringBuilder();
		toRet.append("id: ").append(getId()).append("\n");
		toRet.append("timeSeen: ").append(getTimeSeen()).append("\n");
		toRet.append("score: ").append(getScore()).append("\n");
		toRet.append("ups: ").append(getUps()).append("\n");
		toRet.append("downs: ").append(getDowns()).append("\n");
		toRet.append("numComments: ").append(getNumComments()).append("\n");
		toRet.append("selftext: ").append(getSelftext().toString())
				.append("\n");
		return toRet.toString();
	}
}
