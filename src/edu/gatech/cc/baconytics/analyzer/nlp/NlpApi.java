package edu.gatech.cc.baconytics.analyzer.nlp;

import java.util.Set;


public interface NlpApi {
	public Set<KeywordRelevance> process(String param);
}
