package edu.gatech.cc.baconytics.model;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class UTCTime {

    // Indicates which table does this cursor belong to.
    // E.g. aggregator, or analyzer, or etc.
    @PrimaryKey
    @Persistent
    private String cursorType;

    @Persistent
    private long time;

    public UTCTime(String cursorType) {
        this.cursorType = cursorType;
        time = 0;
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
     * Read the UTC time of last processed Link
     * 
     * @return The UTC time
     */
    public static UTCTime fetchLastUTCTime(String CURSORTYPE) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(UTCTime.class);
        query.setFilter("cursorType == typeParam");
        query.declareParameters("String typeParam");
        UTCTime ret = null;
        try {
            @SuppressWarnings("unchecked")
            List<UTCTime> results = (List<UTCTime>) query.execute(CURSORTYPE);
            if (!results.isEmpty()) {
                ret = results.get(0);
            } else {
                ret = new UTCTime(CURSORTYPE);
                pm.makePersistent(ret);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pm.close();
        }
        return ret;
    }

    /**
     * Mark the last processed Link and update the UTCTime table in database
     * 
     * @param time
     */
    public static void commitLastUTCTime(long time, String CURSORTYPE) {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(UTCTime.class);
        query.setFilter("cursorType == typeParam");
        query.declareParameters("String typeParam");
        try {
            @SuppressWarnings("unchecked")
            List<UTCTime> results = (List<UTCTime>) query.execute(CURSORTYPE);
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
