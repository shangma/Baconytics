package edu.gatech.cc.Baconytics.Aggregator;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Bundle {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    public String redditName;

    @Persistent
    public Keyword keyword;

    @Persistent
    public double relevance;

    public Bundle(String redditName, Keyword keyword, double relevance) {
        this.redditName = redditName;
        this.keyword = keyword;
        this.relevance = relevance;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }
}
