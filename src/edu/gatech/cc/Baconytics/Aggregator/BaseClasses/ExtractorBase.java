package edu.gatech.cc.Baconytics.Aggregator.BaseClasses;

import java.util.Set;

public interface ExtractorBase<E, V, P, D> {

    /*
     * E: Many-to-many map input value.<br/> V: Many-to-many map output
     * value.<br/> P: Whatever NLP library takes as input. <br/> D: Whatever NLP
     * library returns as output
     */
    public Set<V> extract(Set<E> source, NlpApiInterface<P, D> apiInterface);

}
