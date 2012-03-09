package edu.gatech.cc.baconytics.aggregator.model;

public class LinkRelevance {

    private LinkKeyword linkKeyword;
    private double relevance;

    public LinkRelevance(LinkKeyword linkKeyword, double relevance) {
        this.linkKeyword = linkKeyword;
        this.relevance = relevance;
    }

    public LinkKeyword getLinkKeyword() {
        return linkKeyword;
    }

    public void setLinkKeyword(LinkKeyword linkKeyword) {
        this.linkKeyword = linkKeyword;
    }

    public double getRelevance() {
        return relevance;
    }

    public void setRelevance(double relevance) {
        this.relevance = relevance;
    }
}
