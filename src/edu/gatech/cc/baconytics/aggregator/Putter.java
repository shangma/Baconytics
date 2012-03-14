package edu.gatech.cc.baconytics.aggregator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.LinkRelevanceBundle;
import edu.gatech.cc.baconytics.model.PMF;

public class Putter {

    /**
     * Check if the given keyword is already in the database, if yes, then
     * return the object, else return null
     * 
     * @param keyword
     * @param pm
     * @return
     */
    @SuppressWarnings("finally")
    private static KeywordLinkMap lookup(String keyword, PersistenceManager pm) {
        try {
            Query query = pm.newQuery(KeywordLinkMap.class);
            query.setFilter("keyword == lastNameParam");
            query.declareParameters("String lastNameParam");
            @SuppressWarnings("unchecked")
            List<KeywordLinkMap> results = (List<KeywordLinkMap>) query
                    .execute(keyword);
            if (!results.isEmpty()) {
                for (KeywordLinkMap e : results) {
                    return e; // There should be only one
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            return null;
        }
    }

    /**
     * Set each LinkKeyword object with a GAE datastore key.
     * 
     * @param redditSet
     */
    private static void populateLinkKeyword(Set<LinkKeywordMap> redditSet) {
        for (LinkKeywordMap reddit : redditSet) {
            String redditID = reddit.getId();
            Key redditKey = KeyFactory.createKey(
                    LinkKeywordMap.class.getSimpleName(), redditID);
            reddit.setKey(redditKey);
        }
    }

    /**
     * Add LinkeRelevanceBundle set to the existing KeywordLink object. Also add
     * the Key of KeywordLink back to LinkKeyword's keywordSet
     * 
     * @param krr
     * @param gaeKW
     */
    private static void updateGAEKeyword(KeywordLinkMap krr,
            KeywordLinkMap gaeKW) {
        HashSet<LinkRelevanceBundle> bdSet = gaeKW.getBundleSet();
        HashSet<LinkRelevanceBundle> rrSet = krr.getBundleSet();
        for (LinkRelevanceBundle item : rrSet) {
            // Add the Key of KeywordLink back to LinkKeyword
            LinkKeywordMap reddit = item.getLink();
            reddit.getKeywordSet().add(gaeKW.getKey());
            // Add the bundle to gaeKW which is going to be stored in database
            LinkRelevanceBundle lrBundle = new LinkRelevanceBundle(
                    reddit.getKey(), item.getRelevance());
            bdSet.add(lrBundle);
        }
    }

    /**
     * Either update the existing KeywordLink or insert a new KeywordLink object
     * 
     * @param keywordSet
     */
    private static void populateKeywordLink(Set<KeywordLinkMap> keywordSet) {
        for (KeywordLinkMap krr : keywordSet) {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            String keyword = krr.getKeyword();
            KeywordLinkMap gaeKW = lookup(keyword, pm);
            if (gaeKW != null) {
                System.out.println(keyword + " already exists");
                updateGAEKeyword(krr, gaeKW);
            } else {
                Key kwKey = KeyFactory.createKey(
                        KeywordLinkMap.class.getSimpleName(), krr.getKeyword());
                gaeKW = new KeywordLinkMap(keyword);
                gaeKW.setKey(kwKey);
                updateGAEKeyword(krr, gaeKW);
                // For creating an new item, use pm.makePersistent()
                pm.makePersistent(gaeKW);
            }
            pm.close(); // For updating & creating an item, use pm.close()
        }
    }

    /**
     * Take two sets and put them into database. The Link -> Keyword set will be
     * stored directly since each Link is new. The Keyword -> Link set needs to
     * do lookups, either update the database or insert a new item.
     * 
     * @param linkKeyword
     *            - Link -> Keyword (one-to-many) map
     * @param keywordLink
     *            - Keyword -> Link (one-to-many) map
     */
    public static void put(Set<LinkKeywordMap> linkKeyword,
            Set<KeywordLinkMap> keywordLink) {
        if (linkKeyword == null || keywordLink == null
                || linkKeyword.size() == 0 || keywordLink.size() == 0) {
            System.out.println("Many-to-many map does not exist");
            // TODO: Should throw an exception here
            return;
        }

        // To initialize the KeywordLinkMap entity in datastore
        // If the KeywordLinkMap table hasn't been created yet, then put a dummy
        // data first.
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.newQuery(KeywordLinkMap.class);
        } catch (Exception e) {
            Key linkKey = KeyFactory.createKey(
                    LinkKeywordMap.class.getSimpleName(), "@@");
            Key keywordKey = KeyFactory.createKey(
                    KeywordLinkMap.class.getSimpleName(), "@@");
            KeywordLinkMap keyword = new KeywordLinkMap("@@");
            keyword.setKey(keywordKey);
            LinkRelevanceBundle bundle = new LinkRelevanceBundle(linkKey,
                    keyword, -1.0);
            HashSet<LinkRelevanceBundle> bdSet = new HashSet<LinkRelevanceBundle>();
            bdSet.add(bundle);
            keyword.setLinkList(bdSet);
            pm.makePersistent(keyword);
            System.out.println("Initialized KeywordLinkMap Entity");
        } finally {
            pm.close();
        }

        // Populate LinkKeywordMap set with keys
        populateLinkKeyword(linkKeyword);

        // Populate and store KeywordLinkMap set
        populateKeywordLink(keywordLink);

        // Store LinkKeywordMap set
        pm = PMF.get().getPersistenceManager();
        pm.makePersistentAll(linkKeyword);
        pm.close();
    }
}
