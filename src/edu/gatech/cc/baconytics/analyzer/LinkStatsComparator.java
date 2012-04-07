package edu.gatech.cc.baconytics.analyzer;

import java.util.Comparator;

import edu.gatech.cc.baconytics.model.LinkStats;

public class LinkStatsComparator implements Comparator<LinkStats> {

	@Override
	public int compare(LinkStats arg0, LinkStats arg1) {
		if (arg0.getScore() > arg1.getScore()) {

			return -1;
		}

		return 1;
	}

}
