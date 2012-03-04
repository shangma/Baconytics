package edu.gatech.cc.Baconytics.appengine.DataModel;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class GAEBundle {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private Key redditKey;

    @Persistent
    private GAEKeyword keyword;

    @Persistent
    public double relevance;

    public GAEBundle(Key redditKey, double relevance) {
        this.setRedditKey(redditKey);
        this.relevance = relevance;
    }

    public GAEBundle(Key redditKey, GAEKeyword keyword, double relevance) {
        this.redditKey = redditKey;
        this.keyword = keyword;
        this.relevance = relevance;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public GAEKeyword getKeyword() {
        return keyword;
    }

    public void setKeyword(GAEKeyword keyword) {
        this.keyword = keyword;
    }

    public Key getRedditKey() {
        return redditKey;
    }

    public void setRedditKey(Key redditKey) {
        this.redditKey = redditKey;
    }
}
