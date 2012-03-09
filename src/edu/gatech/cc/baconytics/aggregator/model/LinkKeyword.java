package edu.gatech.cc.baconytics.aggregator.model;

import java.util.HashSet;

import edu.gatech.cc.baconytics.model.LinkKeywordMap;

public class LinkKeyword {

    private String id;

    private String title;

    private String subreddit;

    private HashSet<KeywordLink> keywordSet;

    private LinkKeywordMap linkKeywordMap;

    public LinkKeyword(String id, String title, String subreddit) {
        this.setId(id);
        this.setTitle(title);
        this.subreddit = subreddit;
        keywordSet = new HashSet<KeywordLink>();
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

    public HashSet<KeywordLink> getKeywordSet() {
        return keywordSet;
    }

    public void setKeywordSet(HashSet<KeywordLink> keywordSet) {
        this.keywordSet = keywordSet;
    }

    public LinkKeywordMap getGaeReddit() {
        return linkKeywordMap;
    }

    public void setGaeReddit(LinkKeywordMap gaeReddit) {
        this.linkKeywordMap = gaeReddit;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("ID: ").append(getId()).append(" Title: ")
                .append(getTitle()).append(" Size: ").append(keywordSet.size())
                .append("\n");
        for (KeywordLink key : keywordSet) {
            ret.append("\t").append(key.getKeyword()).append("\n");
        }
        return ret.toString();
    }
}
