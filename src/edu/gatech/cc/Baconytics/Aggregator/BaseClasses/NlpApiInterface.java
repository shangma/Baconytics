package edu.gatech.cc.Baconytics.Aggregator.BaseClasses;

import java.util.Set;

public interface NlpApiInterface<E, V> {
    public Set<V> process(E param);
}
