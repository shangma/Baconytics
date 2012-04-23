package edu.gatech.cc.baconytics.model;

import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable = "true")
public class WeeklyTrends {

    // Use String as the unique key, also for saving datastore I/O quota
    @PrimaryKey
    @Persistent
    private String timeInterval;

    @Persistent
    private ArrayList<Key> keywordLinkMapKeys;

    @Persistent
    private ArrayList<Integer> voteList;

    public WeeklyTrends(long startTime, long endTime, ArrayList<Key> keyList,
            ArrayList<Integer> voteList) {
        this.timeInterval = startTime + ";" + endTime;
        this.keywordLinkMapKeys = keyList;
        this.voteList = voteList;
    }

    public String getTimeInterval() {
        return timeInterval;
    }

    public void setTimeInterval(String timeIntv) {
        this.timeInterval = timeIntv;
    }

    public long getTimeStart() {
        return Long.parseLong(timeInterval.split(";")[0]);
    }

    public void setTimeStart(long timeStart) {
        this.timeInterval = timeStart + ";" + timeInterval.split(";")[1];
    }

    public long getTimeEnd() {
        return Long.parseLong(timeInterval.split(";")[1]);
    }

    public void setTimeEnd(long timeEnd) {
        this.timeInterval = timeInterval.split(";")[0] + ";" + timeEnd;
    }

    public ArrayList<Key> getKeywordLinkMapKeys() {
        return keywordLinkMapKeys;
    }

    public void setKeywordLinkMapKeys(ArrayList<Key> keywordLinkMapKeys) {
        this.keywordLinkMapKeys = keywordLinkMapKeys;
    }

    public ArrayList<Integer> getVoteList() {
        return voteList;
    }

    public void setVoteList(ArrayList<Integer> voteList) {
        this.voteList = voteList;
    }
}
