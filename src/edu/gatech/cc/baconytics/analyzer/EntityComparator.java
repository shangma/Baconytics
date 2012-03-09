package edu.gatech.cc.baconytics.analyzer;

import java.util.Comparator;

public class EntityComparator implements Comparator<KeywordEntity> {

	@Override
	public int compare(KeywordEntity o1, KeywordEntity o2) {
		if (o1.getNumOfPosts() > o2.getNumOfPosts()) {
			return -1;
		} else if (o1.getNumOfPosts() < o2.getNumOfPosts()) {
			return 1;
		} else if (o1.getNumOfPosts() == o2.getNumOfPosts()) {
			return 0;
		}
		return 0;
	}
}
