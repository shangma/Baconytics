package edu.gatech.cc.Baconytics.Aggregator;

import java.util.HashSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable(detachable = "true")
public class Keyword {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String keyword;

    @Persistent(mappedBy = "keyword")
    private HashSet<Bundle> bundleSet;

    public Keyword(String keyword) {
        this.keyword = keyword;
        bundleSet = new HashSet<Bundle>();
    }

    public Keyword(String keyword, HashSet<Bundle> bundleSet) {
        this.keyword = keyword;
        this.bundleSet = bundleSet;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setRedditList(HashSet<Bundle> bundleSet) {
        this.bundleSet = bundleSet;
    }

    public HashSet<Bundle> getBundleSet() {
        return bundleSet;
    }

}
