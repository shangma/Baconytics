package edu.gatech.cc.baconytics.aggregator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.gatech.cc.baconytics.aggregator.model.KeywordLink;
import edu.gatech.cc.baconytics.aggregator.model.KeywordRelevance;
import edu.gatech.cc.baconytics.aggregator.model.LinkKeyword;
import edu.gatech.cc.baconytics.aggregator.model.LinkRelevance;
import edu.gatech.cc.baconytics.aggregator.nlp.NlpApi;

public class Extractor {

	private void addToKeywordsMap(HashMap<String, HashSet<LinkRelevance>> pool,
			HashMap<String, LinkRelevance> kwMap, LinkKeyword reddit) {
		Iterator<Entry<String, LinkRelevance>> iter = kwMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, LinkRelevance> pair = iter.next();
			if (pool.containsKey(pair.getKey())) {
				pool.get(pair.getKey()).add(pair.getValue());
			} else {
				HashSet<LinkRelevance> nBundle = new HashSet<LinkRelevance>();
				nBundle.add(pair.getValue());
				pool.put(pair.getKey(), nBundle);
			}
		}
	}

	public Set<KeywordLink> extract(Set<LinkKeyword> source, NlpApi apiInterface) {
		HashSet<KeywordLink> ret = new HashSet<KeywordLink>();
		HashMap<String, HashSet<LinkRelevance>> sbMap = new HashMap<String, HashSet<LinkRelevance>>();
		for (LinkKeyword linkKeyword : source) {
			System.out.println("Processing " + linkKeyword.getTitle());
			Set<KeywordRelevance> newWords = apiInterface.process(linkKeyword
					.getTitle());
			if (newWords == null || newWords.size() == 0) {
				continue;
			}
			HashMap<String, LinkRelevance> kwMap = map(linkKeyword, newWords);
			if (kwMap == null) {
				continue;
			}
			addToKeywordsMap(sbMap, kwMap, linkKeyword);
		}
		Iterator<Entry<String, HashSet<LinkRelevance>>> iter = sbMap.entrySet()
				.iterator();
		while (iter.hasNext()) {
			Map.Entry<String, HashSet<LinkRelevance>> pair = iter.next();
			HashSet<LinkRelevance> lrSet = pair.getValue();
			KeywordLink keyword = new KeywordLink(pair.getKey(), lrSet);
			for (LinkRelevance lr : lrSet) {
				lr.getLinkKeyword().getKeywordSet().add(keyword);
			}
			ret.add(keyword);
		}
		return ret;
	}

	private HashMap<String, LinkRelevance> map(LinkKeyword link,
			Set<KeywordRelevance> krSet) {
		HashMap<String, LinkRelevance> ret = new HashMap<String, LinkRelevance>();
		for (KeywordRelevance kr : krSet) {
			if (kr == null) {
				continue;
			}
			LinkRelevance redrel = new LinkRelevance(link, kr.getRelevance());
			ret.put(kr.getKeyword().toLowerCase(), redrel); // Alchemy is
															// case-sensitive
		}
		if (ret.size() == 0) {
			return null;
		}
		return ret;
	}
}
