package edu.gatech.cc.Baconytics.appengine.DataModel;

import java.util.HashSet;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class GAEReddit {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private String id;

    @Persistent
    private HashSet<Key> keywordSet;

    public GAEReddit(String id) {
        this.id = id;
        keywordSet = new HashSet<Key>();
    }

    public GAEReddit(String id, HashSet<Key> keywordSet) {
        this.id = id;
        this.keywordSet = keywordSet;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setKeywordSet(HashSet<Key> keywordSet) {
        this.keywordSet = keywordSet;
    }

    public HashSet<Key> getKeywordSet() {
        return keywordSet;
    }

    public Key getKey() {
        return key;
    }

    public void setKey(Key key) {
        this.key = key;
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
