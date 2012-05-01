package edu.gatech.cc.baconytics.model;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class Checkpoint {

	// Indicates which aggregator this cursor belongs to.
	@PrimaryKey
	@Persistent
	private String cursorType;

	@Persistent
	private long time;

	public Checkpoint(String cursorType) {
		this.cursorType = cursorType;
		time = -1;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getCursorType() {
		return cursorType;
	}

	public void setCursorType(String cursorType) {
		this.cursorType = cursorType;
	}

	/**
	 * Retrieves a checkpoint for the given CURSORTYPE
	 * 
	 * @return The checkpoint (usually a UTC time)
	 */
	@SuppressWarnings("unchecked")
	public static long getCheckpoint(String CURSORTYPE) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Checkpoint.class);
		query.setFilter("cursorType == typeParam");
		query.declareParameters("String typeParam");
		Checkpoint toRet = null;
		try {
			List<Checkpoint> results = (List<Checkpoint>) query
					.execute(CURSORTYPE);
			if (!results.isEmpty()) {
				toRet = results.get(0);
			} else {
				toRet = new Checkpoint(CURSORTYPE);
				pm.makePersistent(toRet);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return toRet.getTime();
	}

	/**
	 * Update the UTCTime table in datastore
	 * 
	 * @param time
	 */
	@SuppressWarnings("unchecked")
	public static void setCheckpoint(String cursorType, long time) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(Checkpoint.class);
		query.setFilter("cursorType == typeParam");
		query.declareParameters("String typeParam");
		try {
			List<Checkpoint> results = (List<Checkpoint>) query
					.execute(cursorType);
			if (!results.isEmpty()) {
				results.get(0).setTime(time);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}
}
