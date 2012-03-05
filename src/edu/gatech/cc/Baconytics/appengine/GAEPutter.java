package edu.gatech.cc.Baconytics.appengine;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.gatech.cc.Baconytics.Aggregator.BaseClasses.PutterBase;
import edu.gatech.cc.Baconytics.DataModel.PMF;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEBundle;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEKeyword;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEReddit;
import edu.gatech.cc.Baconytics.appengine.DataModel.KeyRedRel;
import edu.gatech.cc.Baconytics.appengine.DataModel.RedRel;
import edu.gatech.cc.Baconytics.appengine.DataModel.Reddit;

public class GAEPutter implements PutterBase<Reddit, KeyRedRel> {

    // To initlize the GAEKeyword entity in datastore
    static {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            pm.newQuery(GAEKeyword.class);
        } catch (Exception e) {
            Key redKey = KeyFactory.createKey(GAEReddit.class.getSimpleName(),
                    "@@");
            Key kwKey = KeyFactory.createKey(GAEKeyword.class.getSimpleName(),
                    "@@");
            GAEKeyword keyword = new GAEKeyword("@@");
            keyword.setKey(kwKey);
            GAEBundle bundle = new GAEBundle(redKey, keyword, -1.0);
            HashSet<GAEBundle> bdSet = new HashSet<GAEBundle>();
            bdSet.add(bundle);
            keyword.setRedditList(bdSet);
            pm.makePersistent(keyword);
            System.out.println("Initialized GAEKeyword Entity");
        }
    }

    @SuppressWarnings("finally")
    private static GAEKeyword lookup(String keyword, PersistenceManager pm) {
        try {
            Query query = pm.newQuery(GAEKeyword.class);
            query.setFilter("keyword == lastNameParam");
            query.declareParameters("String lastNameParam");
            @SuppressWarnings("unchecked")
            List<GAEKeyword> results = (List<GAEKeyword>) query
                    .execute(keyword);
            if (!results.isEmpty()) {
                for (GAEKeyword e : results) {
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

    private static HashSet<GAEReddit> popluateGAEReddit(Set<Reddit> redditSet) {
        HashSet<GAEReddit> ret = new HashSet<GAEReddit>();
        for (Reddit reddit : redditSet) {
            String redditID = reddit.getId();
            GAEReddit gaeReddit = new GAEReddit(redditID);
            Key redditKey = KeyFactory.createKey(
                    GAEReddit.class.getSimpleName(), redditID);
            gaeReddit.setKey(redditKey);
            reddit.setGaeReddit(gaeReddit);
            ret.add(gaeReddit);
        }
        return ret;
    }

    private static void updateGAEKeyword(KeyRedRel krr, GAEKeyword gaeKW) {
        krr.setGaeKeyword(gaeKW);
        HashSet<GAEBundle> bdSet = gaeKW.getBundleSet();
        HashSet<RedRel> rrSet = krr.getRedrelSet();
        for (RedRel item : rrSet) {
            GAEReddit iGaeReddit = item.getReddit().getGaeReddit();
            iGaeReddit.getKeywordSet().add(gaeKW.getKey());
            GAEBundle bundle = new GAEBundle(iGaeReddit.getKey(),
                    item.getRelevance());
            bdSet.add(bundle);
        }
    }

    private static void popluateGAEKeyword(Set<KeyRedRel> keywordSet) {
        for (KeyRedRel krr : keywordSet) {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            String keyword = krr.getKeyword();
            GAEKeyword gaeKW = lookup(keyword, pm);
            Key kwKey = null;
            if (gaeKW != null) {
                kwKey = gaeKW.getKey();
                updateGAEKeyword(krr, gaeKW);
            } else {
                kwKey = KeyFactory.createKey(GAEKeyword.class.getSimpleName(),
                        keyword);
                gaeKW = new GAEKeyword(keyword);
                gaeKW.setKey(kwKey);
                updateGAEKeyword(krr, gaeKW); // For creating an item, use
                                              // pm.makePersistent()
                pm.makePersistent(gaeKW);
            }
            pm.close(); // For updating & creating an item, use pm.close()
        }
    }

    @Override
    public void put(Set<Reddit> left, Set<KeyRedRel> right) {
        if (left == null || right == null) {
            System.out.println("Many-to-many map not exists");
            // TODO: Should throw an exception here
            return;
        }

        // Populate GAEReddit set with keys
        HashSet<GAEReddit> gaeRedditSet = popluateGAEReddit(left);

        // Populate and store GAEKeyword set
        popluateGAEKeyword(right);

        // Store GAEReddit set
        PersistenceManager pm = PMF.get().getPersistenceManager();
        pm.makePersistentAll(gaeRedditSet);
    }
}
