package edu.gatech.cc.Baconytics.Aggregator.BaseClasses;

public interface AggregatorBase {

    /*
     * Get data from the source by using feeder Process data by using extractor
     * Store processed data by using putter
     */
    public void aggregate();

}
