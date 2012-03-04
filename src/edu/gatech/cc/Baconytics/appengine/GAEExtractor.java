package edu.gatech.cc.Baconytics.appengine;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.gatech.cc.Baconytics.Aggregator.BaseClasses.ExtractorBase;
import edu.gatech.cc.Baconytics.Aggregator.BaseClasses.NlpApiInterface;
import edu.gatech.cc.Baconytics.appengine.DataModel.KeyRedRel;
import edu.gatech.cc.Baconytics.appengine.DataModel.RedRel;
import edu.gatech.cc.Baconytics.appengine.DataModel.Reddit;
import edu.gatech.cc.Baconytics.appengine.DataModel.TagRel;

public class GAEExtractor implements
        ExtractorBase<Reddit, KeyRedRel, String, TagRel> {

    public static void addToKeywordsMap(HashMap<String, HashSet<RedRel>> pool,
            HashMap<String, RedRel> kwMap, Reddit reddit) {
        Iterator<Entry<String, RedRel>> iter = kwMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, RedRel> pair = iter.next();
            if (pool.containsKey(pair.getKey())) {
                pool.get(pair.getKey()).add(pair.getValue());
            } else {
                HashSet<RedRel> nBundle = new HashSet<RedRel>();
                nBundle.add(pair.getValue());
                pool.put(pair.getKey(), nBundle);
            }
        }
    }

    @Override
    public Set<KeyRedRel> extract(Set<Reddit> source,
            NlpApiInterface<String, TagRel> apiInterface) {
        HashSet<KeyRedRel> ret = new HashSet<KeyRedRel>();
        HashMap<String, HashSet<RedRel>> sbMap = new HashMap<String, HashSet<RedRel>>();
        for (Reddit reddit : source) {
            System.out.println("Processing " + reddit.getTitle());
            Set<TagRel> newWords = apiInterface.process(reddit.getTitle());
            if (newWords == null) {
                continue;
            }
            HashMap<String, RedRel> kwMap = Mapper.map(reddit, newWords);
            if (kwMap == null) {
                continue;
            }
            addToKeywordsMap(sbMap, kwMap, reddit);
        }
        Iterator<Entry<String, HashSet<RedRel>>> iter = sbMap.entrySet()
                .iterator();
        while (iter.hasNext()) {
            Map.Entry<String, HashSet<RedRel>> pair = iter.next();
            HashSet<RedRel> rrSet = pair.getValue();
            KeyRedRel keyword = new KeyRedRel(pair.getKey(), rrSet);
            for (RedRel rr : rrSet) {
                rr.getReddit().getKeywordSet().add(keyword);
            }
            ret.add(keyword);
        }
        return ret;
    }

    public static class Mapper {
        public static HashMap<String, RedRel> map(Reddit reddit,
                Set<TagRel> trSet) {
            HashMap<String, RedRel> ret = new HashMap<String, RedRel>();
            for (TagRel tr : trSet) {
                if (tr == null) {
                    continue;
                }
                RedRel redrel = new RedRel(reddit, tr.getRelevance());
                ret.put(tr.getKeyword(), redrel);
            }
            if (ret.size() == 0) {
                return null;
            }
            return ret;
        }
    }
}
