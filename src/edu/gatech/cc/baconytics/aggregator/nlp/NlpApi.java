package edu.gatech.cc.baconytics.aggregator.nlp;

import java.util.Set;

import edu.gatech.cc.baconytics.aggregator.model.KeywordRelevance;

public interface NlpApi {
	public Set<KeywordRelevance> process(String param);
}
