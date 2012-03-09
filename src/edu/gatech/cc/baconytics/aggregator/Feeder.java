package edu.gatech.cc.baconytics.aggregator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import edu.gatech.cc.baconytics.aggregator.model.LinkKeyword;
import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.PMF;
import edu.gatech.cc.baconytics.model.UTCTime;

public class Feeder {

	private final int BATCHSIZE = 100; // Due to GAE datastore operation
										// 1min-limit issue, we do
										// aggregating through several
										// batches.

	private void commitLastUTCTime(long time) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(UTCTime.class);
		try {
			@SuppressWarnings("unchecked")
			List<UTCTime> results = (List<UTCTime>) query.execute();
			if (!results.isEmpty()) {
				for (UTCTime t : results) {
					t.setTime(time);
					pm.close();
					return;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public Set<LinkKeyword> feed() {
		long lastUTCTime = fetchLastUTCTime();
		Query query = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Set<LinkKeyword> ret = new HashSet<LinkKeyword>();
			query = pm.newQuery(Link.class);
			query.setFilter("createdUtc > lastTime");
			query.setOrdering("createdUtc asc");
			query.declareParameters("long lastTime");

			@SuppressWarnings("unchecked")
			List<Link> results = (List<Link>) query.execute(lastUTCTime);
			int index = 0; //
			if (!results.isEmpty()) {
				for (Link e : results) {
					if (index >= BATCHSIZE) {
						break;
					}
					LinkKeyword reddit = new LinkKeyword(e.getId(),
							e.getTitle(), e.getSubreddit());
					long utcTime = e.getCreatedUtc();
					if (utcTime > lastUTCTime) {
						lastUTCTime = utcTime;
					}
					ret.add(reddit);
					++index;
				}
			}
			// System.out.println("New Time " + lastUTCTime);
			commitLastUTCTime(lastUTCTime);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (query != null) {
				query.closeAll();
			}
		}
		return null;
	}

	public long fetchLastUTCTime() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(UTCTime.class);
		try {
			@SuppressWarnings("unchecked")
			List<UTCTime> results = (List<UTCTime>) query.execute();
			if (!results.isEmpty()) {
				for (UTCTime t : results) {
					return t.getTime();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 0L; // Do nothing
	}
}
