package edu.gatech.cc.baconytics.analyzer.nlp;

/**
 * This is an intermediate class for representing the output data of NLP API.
 * Should only be used by Extractor
 * 
 * @author Michael
 * 
 */
public class KeywordRelevance {
    private String keyword;
    private double relevance;

    public KeywordRelevance(String keyword, double relevance) {
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
