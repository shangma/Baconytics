package edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.gatech.cc.baconytics.aggregator.model.KeywordRelevance;
import edu.gatech.cc.baconytics.aggregator.nlp.AlchemyImpl;
import edu.gatech.cc.baconytics.aggregator.nlp.NlpApi;
import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.LinkRelevanceBundle;
import edu.gatech.cc.baconytics.model.PMF;
import edu.gatech.cc.baconytics.model.UTCTime;

@SuppressWarnings("serial")
public class AggregatorServlet extends HttpServlet {
	// Due to GAE datastore operation 1min-limit issue, we do aggregating
	// through several batches.
	private final int BATCHSIZE = 100;

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		NlpApi nlpApi = new AlchemyImpl();
		Set<LinkKeywordMap> links = feed();
		Set<KeywordLinkMap> extracted = extract(links, nlpApi);
		put(links, extracted);
		resp.getWriter().println("<a href='./show'>See Results</a>");
	}

	/*
	 * Feeder
	 */

	/**
	 * Mark the last processed Link and update the UTCTime table in database
	 * 
	 * @param time
	 */
	private void commitLastUTCTime(long time) {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(UTCTime.class);
		try {
			@SuppressWarnings("unchecked")
			List<UTCTime> results = (List<UTCTime>) query.execute();
			if (!results.isEmpty()) {
				for (UTCTime t : results) {
					t.setTime(time);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
	}

	/**
	 * Populate a set of LinkKeyword objects from database. Only take the title
	 * and id of each Link
	 * 
	 * @return a HashSet of LinkKeyword objects
	 */
	private Set<LinkKeywordMap> feed() {
		long lastUTCTime = fetchLastUTCTime();
		Query query = null;
		PersistenceManager pm = PMF.get().getPersistenceManager();
		try {
			Set<LinkKeywordMap> ret = new HashSet<LinkKeywordMap>();
			query = pm.newQuery(Link.class);
			query.setFilter("createdUtc > lastTime");
			query.setOrdering("createdUtc asc");
			query.declareParameters("long lastTime");
			query.setRange(0, BATCHSIZE);
			@SuppressWarnings("unchecked")
			List<Link> results = (List<Link>) query.execute(lastUTCTime);
			if (!results.isEmpty()) {
				for (Link e : results) {
					LinkKeywordMap reddit = new LinkKeywordMap(e.getId(),
							e.getTitle(), e.getSubreddit());
					long utcTime = e.getCreatedUtc();
					if (utcTime > lastUTCTime) {
						lastUTCTime = utcTime;
					}
					ret.add(reddit);
				}
			}
			commitLastUTCTime(lastUTCTime);
			return ret;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (query != null) {
				query.closeAll();
			}
			pm.close();
		}
		return null;
	}

	/**
	 * Read the UTC time of last processed Link
	 * 
	 * @return The UTC time
	 */
	public long fetchLastUTCTime() {
		PersistenceManager pm = PMF.get().getPersistenceManager();
		Query query = pm.newQuery(UTCTime.class);
		long ret = 9331619531L; // Here if the UTC table does not exsit or the
								// marker is not set properly, the aggregator
								// should do nothing, so set the return time
								// larger than any Link in database.
		try {
			@SuppressWarnings("unchecked")
			List<UTCTime> results = (List<UTCTime>) query.execute();
			if (!results.isEmpty()) {
				for (UTCTime t : results) {
					ret = t.getTime();
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			pm.close();
		}
		return ret;
	}

	/*
	 * Extractor
	 */

	/**
	 * Add new generated LinkKeyword object into a set. Here we do a in-memory
	 * aggregation to combine same keywords
	 * 
	 * @param pool
	 *            - the set of all LinkKeyword objects
	 * @param kwMap
	 *            - the new generated LinkKeyword object
	 */
	private void addToKeywordsMap(
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
	private Set<KeywordLinkMap> extract(Set<LinkKeywordMap> source,
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
	private HashMap<String, LinkRelevanceBundle> map(LinkKeywordMap link,
			Set<KeywordRelevance> krSet) {
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

	/*
	 * Putter
	 */

	/**
	 * Check if the given keyword is already in the database, if yes, then
	 * return the object, else return null
	 * 
	 * @param keyword
	 * @param pm
	 * @return
	 */
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

	/**
	 * Set each LinkKeyword object with a GAE datastore key.
	 * 
	 * @param redditSet
	 */
	private void populateLinkKeyword(Set<LinkKeywordMap> redditSet) {
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
	private void updateGAEKeyword(KeywordLinkMap krr, KeywordLinkMap gaeKW) {
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
	private void populateKeywordLink(Set<KeywordLinkMap> keywordSet) {
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
	private void put(Set<LinkKeywordMap> linkKeyword,
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
