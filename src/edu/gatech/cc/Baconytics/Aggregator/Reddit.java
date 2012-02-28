package edu.gatech.cc.Baconytics.Aggregator;

import java.util.HashSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class Reddit {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String name;

    @Persistent
    private String title;

    @Persistent
    private HashSet<Key> keywordSet;

    public Reddit(String name, String title) {
        this.name = name;
        this.title = title;
        keywordSet = new HashSet<Key>();
    }

    public Reddit(String name, String title, HashSet<Key> keywordSet) {
        this.name = name;
        this.title = title;
        this.keywordSet = keywordSet;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public Key getKey() {
        return key;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setKeywordSet(HashSet<Key> keywordSet) {
        this.keywordSet = keywordSet;
    }

    public HashSet<Key> getKeywordSet() {
        return keywordSet;
    }

}
