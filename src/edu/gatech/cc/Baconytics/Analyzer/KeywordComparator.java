package edu.gatech.cc.Baconytics.Analyzer;

import java.util.Comparator;

import edu.gatech.cc.Baconytics.appengine.DataModel.GAEKeyword;

public class KeywordComparator implements Comparator<GAEKeyword> {

	@Override
	public int compare(GAEKeyword o1, GAEKeyword o2) {
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
