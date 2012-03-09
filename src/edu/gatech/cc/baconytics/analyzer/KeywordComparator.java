package edu.gatech.cc.baconytics.analyzer;

import java.util.Comparator;

import edu.gatech.cc.baconytics.model.KeywordLinkMap;

public class KeywordComparator implements Comparator<KeywordLinkMap> {

	@Override
	public int compare(KeywordLinkMap o1, KeywordLinkMap o2) {
		if (o1.getBundleSetSize() > o2.getBundleSetSize()) {
			return -1;
		} else if (o1.getBundleSetSize() < o2.getBundleSetSize()) {
			return 1;
		} else if (o1.getBundleSetSize() == o2.getBundleSetSize()) {
			return 0;
		}
		return 0;
	}
}
