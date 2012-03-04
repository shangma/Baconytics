package edu.gatech.cc.Baconytics.Aggregator.BaseClasses;

import java.util.Set;

public interface PutterBase<L, R> {

    public void put(Set<L> left, Set<R> right);

}
