package edu.gatech.cc.baconytics.aggregator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import edu.gatech.cc.baconytics.aggregator.model.KeywordRelevance;
import edu.gatech.cc.baconytics.aggregator.nlp.NlpApi;
import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.LinkRelevanceBundle;

public class Extractor {

    /**
     * Add new generated LinkKeyword object into a set. Here we do a in-memory
     * aggregation to combine same keywords
     * 
     * @param pool
     *            - the set of all LinkKeyword objects
     * @param kwMap
     *            - the new generated LinkKeyword object
     */
    private static void addToKeywordsMap(
            HashMap<String, HashSet<LinkRelevanceBundle>> pool,
            HashMap<String, LinkRelevanceBundle> kwMap) {
        Iterator<Entry<String, LinkRelevanceBundle>> iter = kwMap.entrySet()
                .iterator();
        while (iter.hasNext()) {
            Map.Entry<String, LinkRelevanceBundle> pair = iter.next();
            if (pool.containsKey(pair.getKey())) {
                pool.get(pair.getKey()).add(pair.getValue());
            } else {
                HashSet<LinkRelevanceBundle> nBundle = new HashSet<LinkRelevanceBundle>();
                nBundle.add(pair.getValue());
                pool.put(pair.getKey(), nBundle);
            }
        }
    }

    /**
     * Take a set of LinkKeyword objects as input. Produce two sets, one is the
     * Keyword -> Link (one-to-many) map, the other set is the Link -> Keyword
     * (one-to-many) map, this set is populated in-place, so the source will be
     * manipulated -- keywords belong to each Link will be added.
     * 
     * @param source
     *            - a set of LinkKeyword objects. When passed by feeder, each
     *            object only has title and id, no Set<Keyword>, but when the
     *            extractor finishes, the Set<Keyword> will be added
     * @param apiInterface
     *            - an instance of NLP API
     * @return the set of Keyword -> Link (one-to-many) map
     */
    public static Set<KeywordLinkMap> extract(Set<LinkKeywordMap> source,
            NlpApi apiInterface) {
        HashSet<KeywordLinkMap> ret = new HashSet<KeywordLinkMap>();
        HashMap<String, HashSet<LinkRelevanceBundle>> sbMap = new HashMap<String, HashSet<LinkRelevanceBundle>>();
        for (LinkKeywordMap linkKeyword : source) {
            System.out.println("Processing " + linkKeyword.getTitle());
            Set<KeywordRelevance> newWords = apiInterface.process(linkKeyword
                    .getTitle());
            // If no valuable keyword extracted by NLP API, we put the subreddit
            // as keyword here, and set the relevance 1.0
            if (newWords == null || newWords.size() == 0) {
                newWords = new HashSet<KeywordRelevance>();
                KeywordRelevance kr = new KeywordRelevance(
                        linkKeyword.getSubreddit(), 1.0);
                newWords.add(kr);
            }
            HashMap<String, LinkRelevanceBundle> kwMap = map(linkKeyword,
                    newWords);
            if (kwMap == null) {
                continue;
            }
            // Here we create the Keyword -> Link (one-to-many) map
            addToKeywordsMap(sbMap, kwMap);
        }
        Iterator<Entry<String, HashSet<LinkRelevanceBundle>>> iter = sbMap
                .entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<String, HashSet<LinkRelevanceBundle>> pair = iter.next();
            HashSet<LinkRelevanceBundle> lrSet = pair.getValue();
            // Instantiate a KeywordLink object here, but if the same Keyword is
            // already in the database, then the object won't be store in the
            // database. The existing one will be updated with all the new
            // Set<LinkRelevanceBundle> added here.
            KeywordLinkMap keyword = new KeywordLinkMap(pair.getKey(), lrSet);

            // Add the KeywordLink back to LinkKeyword,
            // here we create the Link -> Keyword (one-to-many) map
            ret.add(keyword);
        }
        System.out.println("==========================================");
        return ret;
    }

    /**
     * convert pair<link, pair<keyword, relevance>> to map<keyword, pair<link,
     * relevance>>
     * 
     * @param link
     *            - a LinkKeyword instance
     * @param krSet
     *            - the set of <keyword,relevance> pair
     * @return - the map<keyword, pair<link, relevance>>
     */
    private static HashMap<String, LinkRelevanceBundle> map(
            LinkKeywordMap link, Set<KeywordRelevance> krSet) {
        HashMap<String, LinkRelevanceBundle> ret = new HashMap<String, LinkRelevanceBundle>();
        for (KeywordRelevance kr : krSet) {
            if (kr == null) {
                continue;
            }
            LinkRelevanceBundle redrel = new LinkRelevanceBundle(link,
                    kr.getRelevance());
            // Alchemy is case-sensitive
            ret.put(kr.getKeyword().toLowerCase(), redrel);
        }
        if (ret.size() == 0) {
            return null;
        }
        return ret;
    }
}
