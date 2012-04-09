package edu.gatech.cc.baconytics.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable = "true")
public class AnalyzerCheckpoint {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;

	@Persistent
	private long time = 0;

	public AnalyzerCheckpoint() {
		this.time = 0;
	}

	public Key getKey() {
		return this.key;
	}

	public long getTime() {
		return this.time;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public void resetCheckPoint() {
		this.time = 0;
	}

}
