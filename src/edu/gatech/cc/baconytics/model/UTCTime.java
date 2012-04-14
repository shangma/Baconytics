package edu.gatech.cc.baconytics.model;

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
}
