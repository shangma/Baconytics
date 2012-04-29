package edu.gatech.cc.baconytics.analyzer;

public abstract class Aggregator {
	public static final int BATCHSIZE = 100;
	public static final String CURSORTYPE = "AGGREGATOR";

	public abstract void aggregate();
}
