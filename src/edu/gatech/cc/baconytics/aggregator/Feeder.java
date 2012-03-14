package edu.gatech.cc.baconytics.aggregator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.PMF;
import edu.gatech.cc.baconytics.model.UTCTime;

public class Feeder {

    // Due to GAE datastore operation 1min-limit issue, we do aggregating
    // through several batches.
    private final static int BATCHSIZE = 100;

    /**
     * Mark the last processed Link and update the UTCTime table in database
     * 
     * @param time
     */
    private static void commitLastUTCTime(long time) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(UTCTime.class);
        try {
            @SuppressWarnings("unchecked")
            List<UTCTime> results = (List<UTCTime>) query.execute();
            if (!results.isEmpty()) {
                for (UTCTime t : results) {
                    t.setTime(time);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pm.close();
        }
    }

    /**
     * Populate a set of LinkKeyword objects from database. Only take the title
     * and id of each Link
     * 
     * @return a HashSet of LinkKeyword objects
     */
    public static Set<LinkKeywordMap> feed() {
        long lastUTCTime = fetchLastUTCTime();
        Query query = null;
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Set<LinkKeywordMap> ret = new HashSet<LinkKeywordMap>();
            query = pm.newQuery(Link.class);
            query.setFilter("createdUtc > lastTime");
            query.setOrdering("createdUtc asc");
            query.declareParameters("long lastTime");
            query.setRange(0, BATCHSIZE);
            @SuppressWarnings("unchecked")
            List<Link> results = (List<Link>) query.execute(lastUTCTime);
            if (!results.isEmpty()) {
                for (Link e : results) {
                    LinkKeywordMap reddit = new LinkKeywordMap(e.getId(),
                            e.getTitle(), e.getSubreddit());
                    long utcTime = e.getCreatedUtc();
                    if (utcTime > lastUTCTime) {
                        lastUTCTime = utcTime;
                    }
                    ret.add(reddit);
                }
            }
            commitLastUTCTime(lastUTCTime);
            return ret;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (query != null) {
                query.closeAll();
            }
            pm.close();
        }
        return null;
    }

    /**
     * Read the UTC time of last processed Link
     * 
     * @return The UTC time
     */
    public static long fetchLastUTCTime() {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(UTCTime.class);
        long ret = 9331619531L; // Here if the UTC table does not exsit or the
                                // marker is not set properly, the aggregator
                                // should do nothing, so set the return time
                                // larger than any Link in database.
        try {
            @SuppressWarnings("unchecked")
            List<UTCTime> results = (List<UTCTime>) query.execute();
            if (!results.isEmpty()) {
                for (UTCTime t : results) {
                    ret = t.getTime();
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pm.close();
        }
        return ret;
    }
}
