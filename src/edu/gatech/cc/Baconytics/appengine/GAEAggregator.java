package edu.gatech.cc.Baconytics.appengine;

import java.util.Set;

import javax.servlet.http.HttpServlet;

import edu.gatech.cc.Baconytics.Aggregator.AlchemyAPI.AlchemyImpl;
import edu.gatech.cc.Baconytics.Aggregator.BaseClasses.AggregatorBase;
import edu.gatech.cc.Baconytics.appengine.DataModel.KeyRedRel;
import edu.gatech.cc.Baconytics.appengine.DataModel.Reddit;

@SuppressWarnings("serial")
public class GAEAggregator extends HttpServlet implements AggregatorBase {

    private static GAEFeeder feeder;
    private static GAEExtractor extractor;
    private static GAEPutter putter;

    public GAEAggregator() {
        feeder = new GAEFeeder();
        extractor = new GAEExtractor();
        putter = new GAEPutter();
    }

    public GAEAggregator(GAEFeeder feeder, GAEExtractor extractor,
            GAEPutter putter) {
        GAEAggregator.feeder = feeder;
        GAEAggregator.extractor = extractor;
        GAEAggregator.setPutter(putter);
    }

    @Override
    public void aggregate() {
        AlchemyImpl nlpApi = new AlchemyImpl();
        Set<Reddit> reddits = getFeeder().feed();
        Set<KeyRedRel> extracted = getExtractor().extract(reddits, nlpApi);
        getPutter().put(reddits, extracted);
    }

    public static GAEFeeder getFeeder() {
        return feeder;
    }

    public static void setFeeder(GAEFeeder feeder) {
        GAEAggregator.feeder = feeder;
    }

    public static GAEExtractor getExtractor() {
        return extractor;
    }

    public static void setExtractor(GAEExtractor extractor) {
        GAEAggregator.extractor = extractor;
    }

    public static GAEPutter getPutter() {
        return putter;
    }

    public static void setPutter(GAEPutter putter) {
        GAEAggregator.putter = putter;
    }
}
