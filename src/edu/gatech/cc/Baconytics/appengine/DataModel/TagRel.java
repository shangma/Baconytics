package edu.gatech.cc.Baconytics.appengine.DataModel;

public class TagRel {
    private String keyword;
    private double relevance;

    public TagRel(String keyword, double relevance) {
        this.keyword = keyword;
        this.relevance = relevance;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }
}
