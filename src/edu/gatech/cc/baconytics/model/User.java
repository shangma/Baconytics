package edu.gatech.cc.baconytics.model;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable
public class User implements Comparable<User> {

	@PrimaryKey
	private String userName;

	@Persistent
	private int karma = 0;

	@Persistent
	private int totalVisits = 0;

	@Persistent
	private int totalLinks = 0;

	@Persistent
	private int totalComments = 0;

	public User(String username, int karma) {
		this.userName = username;
		this.karma = karma;
	}

	public void addKarma(int k) {
		this.karma += k;
	}

	public void addVisits(int v) {
		this.totalVisits += v;
	}

	public void addComments(int c) {
		this.totalComments += c;
	}

	public void incrementTotalLinks() {
		this.totalLinks++;
	}

	public int getKarma() {
		return this.karma;
	}

	public String getUsername() {
		return this.userName;
	}

	public int getTotalVisits() {
		return this.totalVisits;
	}

	public int getTotalLinks() {
		return this.totalLinks;
	}

	public int getTotalComments() {
		return this.totalComments;
	}

	@Override
	public int compareTo(User u) {
		return (this.karma - u.getKarma());
	}
}
