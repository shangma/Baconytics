package edu.gatech.cc.Baconytics.appengine.DataModel;

import java.util.HashSet;

public class Reddit {

    private String id;

    private String title;

    private String subreddit;

    private HashSet<KeyRedRel> keywordSet;

    private GAEReddit gaeReddit;

    public Reddit(String id, String title, String subreddit) {
        this.setId(id);
        this.setTitle(title);
        this.subreddit = subreddit;
        keywordSet = new HashSet<KeyRedRel>();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    public HashSet<KeyRedRel> getKeywordSet() {
        return keywordSet;
    }

    public void setKeywordSet(HashSet<KeyRedRel> keywordSet) {
        this.keywordSet = keywordSet;
    }

    public GAEReddit getGaeReddit() {
        return gaeReddit;
    }

    public void setGaeReddit(GAEReddit gaeReddit) {
        this.gaeReddit = gaeReddit;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("ID: ").append(getId()).append(" Title: ")
                .append(getTitle()).append(" Size: ").append(keywordSet.size())
                .append("\n");
        for (KeyRedRel key : keywordSet) {
            ret.append("\t").append(key.getKeyword()).append("\n");
        }
        return ret.toString();
    }
}
