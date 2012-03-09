package edu.gatech.cc.baconytics.aggregator;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.gatech.cc.baconytics.aggregator.model.KeywordLink;
import edu.gatech.cc.baconytics.aggregator.model.LinkKeyword;
import edu.gatech.cc.baconytics.aggregator.model.LinkRelevance;
import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.LinkRelevanceBundle;
import edu.gatech.cc.baconytics.model.PMF;

public class Putter {

	@SuppressWarnings("finally")
	private KeywordLinkMap lookup(String keyword, PersistenceManager pm) {
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

	private HashSet<LinkKeywordMap> populateLinkKeyword(
			Set<LinkKeyword> redditSet) {
		HashSet<LinkKeywordMap> ret = new HashSet<LinkKeywordMap>();
		for (LinkKeyword reddit : redditSet) {
			String redditID = reddit.getId();
			LinkKeywordMap gaeReddit = new LinkKeywordMap(redditID);
			Key redditKey = KeyFactory.createKey(
					LinkKeywordMap.class.getSimpleName(), redditID);
			gaeReddit.setKey(redditKey);
			reddit.setGaeReddit(gaeReddit);
			ret.add(gaeReddit);
		}
		return ret;
	}

	private void updateGAEKeyword(KeywordLink krr, KeywordLinkMap gaeKW) {
		krr.setKeywordLink(gaeKW);
		HashSet<LinkRelevanceBundle> bdSet = gaeKW.getBundleSet();
		HashSet<LinkRelevance> rrSet = krr.getLinkRelSet();
		for (LinkRelevance item : rrSet) {
			LinkKeywordMap iGaeReddit = item.getLinkKeyword().getGaeReddit();
			iGaeReddit.getKeywordSet().add(gaeKW.getKey());
			LinkRelevanceBundle bundle = new LinkRelevanceBundle(
					iGaeReddit.getKey(), item.getRelevance());
			bdSet.add(bundle);
		}
	}

	private void populateKeywordLink(Set<KeywordLink> keywordSet) {
		for (KeywordLink krr : keywordSet) {
			PersistenceManager pm = PMF.get().getPersistenceManager();
			String keyword = krr.getKeyword();
			KeywordLinkMap gaeKW = lookup(keyword, pm);
			Key kwKey = null;
			if (gaeKW != null) {
				kwKey = gaeKW.getKey();
				updateGAEKeyword(krr, gaeKW);
			} else {
				kwKey = KeyFactory.createKey(
						KeywordLinkMap.class.getSimpleName(), keyword);
				gaeKW = new KeywordLinkMap(keyword);
				gaeKW.setKey(kwKey);
				updateGAEKeyword(krr, gaeKW); // For creating an item, use
												// pm.makePersistent()
				pm.makePersistent(gaeKW);
			}
			pm.close(); // For updating & creating an item, use pm.close()
		}
	}

	public void put(Set<LinkKeyword> linkKeyword, Set<KeywordLink> keywordLink) {
		if (linkKeyword == null || keywordLink == null) {
			System.out.println("Many-to-many map not exists");
			// TODO: Should throw an exception here
			return;
		}

		// To initialize the KeywordLinkMap entity in datastore
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
		}

		// Populate LinkKeywordMap set with keys
		HashSet<LinkKeywordMap> linkKeywordSet = populateLinkKeyword(linkKeyword);

		// Populate and store KeywordLinkMap set
		populateKeywordLink(keywordLink);

		// Store LinkKeywordMap set
		pm.makePersistentAll(linkKeywordSet);
	}
}
