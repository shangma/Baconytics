package edu.gatech.cc.Baconytics.appengine.DataModel;

import java.util.HashSet;

public class KeyRedRel {

    private String keyword;

    private GAEKeyword gaeKeyword;

    private HashSet<RedRel> redrelSet;

    public KeyRedRel(String keyword) {
        this.keyword = keyword;
        redrelSet = new HashSet<RedRel>();
    }

    public KeyRedRel(String keyword, HashSet<RedRel> redrelSet) {
        this.keyword = keyword;
        this.redrelSet = redrelSet;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public HashSet<RedRel> getRedrelSet() {
        return redrelSet;
    }

    public void setRedrelSet(HashSet<RedRel> redrelSet) {
        this.redrelSet = redrelSet;
    }

    public GAEKeyword getGaeKeyword() {
        return gaeKeyword;
    }

    public void setGaeKeyword(GAEKeyword gaeKeyword) {
        this.gaeKeyword = gaeKeyword;
    }

}
