package edu.gatech.cc.baconytics.model;

import java.util.HashSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class LinkKeywordMap {

    @Persistent
    private String id;

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private HashSet<Key> keywordSet;

    public LinkKeywordMap(String id) {
        this.id = id;
        keywordSet = new HashSet<Key>();
    }

    public LinkKeywordMap(String id, HashSet<Key> keywordSet) {
        this.id = id;
        this.keywordSet = keywordSet;
    }

    public String getId() {
        return id;
    }

    public Key getKey() {
        return key;
    }

    public HashSet<Key> getKeywordSet() {
        return keywordSet;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public void setKeywordSet(HashSet<Key> keywordSet) {
        this.keywordSet = keywordSet;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("id: ").append(id).append(" Total: ")
                .append(keywordSet.size()).append("\n");
        for (Key key : keywordSet) {
            ret.append("\t").append(key.toString()).append("\n");
        }
        return ret.toString();
    }
}
