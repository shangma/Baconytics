package edu.gatech.cc.Baconytics.appengine.DataModel;

public class RedRel {

    private Reddit reddit;
    private double relevance;

    public RedRel(Reddit reddit, double relevance) {
        this.reddit = reddit;
        this.relevance = relevance;
    }

    public Reddit getReddit() {
        return reddit;
    }

    public void setReddit(Reddit reddit) {
        this.reddit = reddit;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }
}
