package edu.gatech.cc.Baconytics.appengine;

import java.util.HashSet;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import edu.gatech.cc.Baconytics.Aggregator.BaseClasses.FeederBase;
import edu.gatech.cc.Baconytics.DataModel.Link;
import edu.gatech.cc.Baconytics.DataModel.PMF;
import edu.gatech.cc.Baconytics.appengine.DataModel.Reddit;

public class GAEFeeder implements FeederBase<Reddit> {

    private static PersistenceManager pm = PMF.get().getPersistenceManager();

    public static long lastUTCTime = 0;

    public GAEFeeder() {
    }

    @Override
    public HashSet<Reddit> feed() {
        Query query = null;
        try {
            HashSet<Reddit> ret = new HashSet<Reddit>();
            query = pm.newQuery(Link.class);
            query.setFilter("createdUtc > lastTime");
            query.setOrdering("createdUtc desc");
            query.declareParameters("long lastTime");

            @SuppressWarnings("unchecked")
            List<Link> results = (List<Link>) query.execute(lastUTCTime);
            if (!results.isEmpty()) {
                for (Link e : results) {
                    Reddit reddit = new Reddit(e.getId(), e.getTitle(),
                            e.getSubreddit());
                    long utcTime = e.getCreatedUtc();
                    if (utcTime > lastUTCTime) {
                        lastUTCTime = utcTime;
                    }
                    ret.add(reddit);
                }
            }
            System.out.println("New Time " + lastUTCTime);

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
