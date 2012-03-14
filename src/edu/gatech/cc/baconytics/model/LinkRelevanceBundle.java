package edu.gatech.cc.baconytics.model;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class LinkRelevanceBundle {

    @PrimaryKey
    @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
    private Key key;

    @Persistent
    private KeywordLinkMap keyword;

    @Persistent
    private Key linkKey;

    @Persistent
    private double relevance;

    private LinkKeywordMap link;

    public LinkRelevanceBundle(LinkKeywordMap link, double relevance) {
        this.link = link;
        this.relevance = relevance;
    }

    public LinkRelevanceBundle(Key linkKey, double relevance) {
        this.setLinkKey(linkKey);
        this.setRelevance(relevance);
    }

    public LinkRelevanceBundle(Key linkKey, KeywordLinkMap keyword,
            double relevance) {
        this.linkKey = linkKey;
        this.keyword = keyword;
        this.setRelevance(relevance);
    }

    public Key getKey() {
        return key;
    }

    public KeywordLinkMap getKeyword() {
        return keyword;
    }

    public Key getLinkKey() {
        return linkKey;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setKey(Key key) {
        this.key = key;
    }

    public void setKeyword(KeywordLinkMap keyword) {
        this.keyword = keyword;
    }

    public void setLinkKey(Key linkKey) {
        this.linkKey = linkKey;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }

    @Override
    public String toString() {
        StringBuilder ret = new StringBuilder();
        ret.append("RID: ").append(getLinkKey()).append(" Rel: ")
                .append(getRelevance()).append("\n");
        return ret.toString();
    }

    public LinkKeywordMap getLink() {
        return link;
    }

    public void setLink(LinkKeywordMap link) {
        this.link = link;
    }
}
