package edu.gatech.cc.Baconytics;

public final class LinkStats implements Comparable<LinkStats> {

	private final int downs, numComments, score, ups;
	private final String id, selftext;
	private final long timeSeen;

	public LinkStats(String id, long timeSeen, int score, int ups, int downs,
			int numComments, String selftext) {
		this.downs = downs;
		this.numComments = numComments;
		this.score = score;
		this.ups = ups;
		this.id = id;
		this.selftext = selftext;
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

	public int getNumComments() {
		return numComments;
	}

	public int getScore() {
		return score;
	}

	public String getSelftext() {
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
		toRet.append("selftext: ").append(getSelftext()).append("\n");
		return toRet.toString();
	}
}
