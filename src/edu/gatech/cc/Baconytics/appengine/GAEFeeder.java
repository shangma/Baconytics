package edu.gatech.cc.Baconytics.appengine;

import java.util.HashSet;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import edu.gatech.cc.Baconytics.Aggregator.BaseClasses.FeederBase;
import edu.gatech.cc.Baconytics.DataModel.Link;
import edu.gatech.cc.Baconytics.DataModel.PMF;
import edu.gatech.cc.Baconytics.appengine.DataModel.Reddit;
import edu.gatech.cc.Baconytics.appengine.DataModel.UTCTime;

public class GAEFeeder implements FeederBase<Reddit> {

    public static long lastUTCTime = 0;
    private final static int BATCHSIZE = 100; // Due to GAE datastore operation
                                              // 1min-limit issue, we do
                                              // aggregating through several
                                              // batches.

    public GAEFeeder() {
    }

    private static long getLastUTCTime() {
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
        return 9330683348L; // Do nothing
    }

    private static void setLastUTCtime(long time) {
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

    @Override
    public HashSet<Reddit> feed() {
        lastUTCTime = getLastUTCTime();
        Query query = null;
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            HashSet<Reddit> ret = new HashSet<Reddit>();
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
                    Reddit reddit = new Reddit(e.getId(), e.getTitle(),
                            e.getSubreddit());
                    long utcTime = e.getCreatedUtc();
                    if (utcTime > lastUTCTime) {
                        lastUTCTime = utcTime;
                    }
                    ret.add(reddit);
                    ++index;
                }
            }
            // System.out.println("New Time " + lastUTCTime);
            setLastUTCtime(lastUTCTime);
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
}
