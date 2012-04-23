package edu.gatech.cc.baconytics.analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.appengine.api.datastore.Key;

import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkKeywordMap;
import edu.gatech.cc.baconytics.model.LinkRelevanceBundle;
import edu.gatech.cc.baconytics.model.LinkStats;
import edu.gatech.cc.baconytics.model.PMF;
import edu.gatech.cc.baconytics.model.UTCTime;
import edu.gatech.cc.baconytics.model.WeeklyTrends;

@SuppressWarnings("serial")
public class DailyTrendsServlet extends HttpServlet {

    public static final long MILLISECOND_IN_ONE_HOUR = 3600000L;
    public static final String CURSORTYPE = "WEEKLY_TRENDS";
    public static final int THRESHHOLD = 10; // Only returns top 10 keywords
    public static final int BATCHSIZE = 300;

    public static PrintWriter writer;
    public static int numOfProcessedLinkStats = 0;
    public static int numOfProcessedKeywords = 0;

    private HashMap<Key, Integer> keywordCount = new HashMap<Key, Integer>();
    private HashMap<String, TimeVotesBundle> redditCount = new HashMap<String, TimeVotesBundle>();

    public class BundleComparator implements Comparator<KeywordVotesBundle> {

        @Override
        public int compare(KeywordVotesBundle arg0, KeywordVotesBundle arg1) {
            return arg1.voteCount - arg0.voteCount;
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        writer = resp.getWriter();
        PersistenceManager pm = PMF.get().getPersistenceManager();
        try {
            Query query = pm.newQuery(WeeklyTrends.class);
            List<WeeklyTrends> results = (List<WeeklyTrends>) query.execute();
            if (results.isEmpty()) {
                return;
            }
            JSONArray dailyJsonArray = new JSONArray();
            for (WeeklyTrends day : results) {
                JSONObject dailyJsonObj = new JSONObject();
                long startTime = day.getTimeStart();
                long endTime = day.getTimeEnd();
                dailyJsonObj.put("start_time", startTime);
                dailyJsonObj.put("end_time", endTime);

                JSONArray keywordListArray = new JSONArray();
                ArrayList<Key> keyList = day.getKeywordLinkMapKeys();
                ArrayList<Integer> voteList = day.getVoteList();
                int size = keyList.size();
                for (int i = 0; i < size; ++i) {
                    Query keylinkQuery = pm.newQuery(KeywordLinkMap.class);
                    keylinkQuery.setFilter("keyword == keywordParam");
                    keylinkQuery.declareParameters("String keywordParam");
                    List<KeywordLinkMap> klmResults = (List<KeywordLinkMap>) keylinkQuery
                            .execute(keyList.get(i).getName());
                    if (klmResults.isEmpty()) {
                        continue;
                    }
                    KeywordLinkMap keywordLink = klmResults.get(0);
                    JSONObject kwJson = new JSONObject();
                    HashSet<LinkRelevanceBundle> krBundle = keywordLink
                            .getBundleSet();
                    JSONArray linkJsonArray = new JSONArray();
                    for (LinkRelevanceBundle item : krBundle) {
                        Query linkQuery = pm.newQuery(Link.class);
                        linkQuery.setFilter("id == idParam");
                        linkQuery.declareParameters("String idParam");

                        List<Link> linkResults = (List<Link>) linkQuery
                        // Use getName() to get the link's id
                                .execute(item.getLinkKey().getName());
                        Link link = linkResults.get(0);
                        JSONObject linkJson = new JSONObject();
                        linkJson.put("author", link.getAuthor());
                        linkJson.put("domain", link.getDomain());
                        linkJson.put("name", link.getName());
                        linkJson.put("permalink", link.getPermalink());
                        linkJson.put("title", link.getTitle());
                        linkJson.put("url", link.getUrl());
                        linkJson.put("subreddit", link.getSubreddit());
                        linkJson.put("subredditId", link.getSubredditId());
                        linkJsonArray.put(linkJson);
                    }
                    kwJson.put("keyword", keywordLink.getKeyword());
                    kwJson.put("score", voteList.get(i));
                    kwJson.put("link_list", linkJsonArray);
                    keywordListArray.put(kwJson);
                }
                dailyJsonObj.put("keyword_list", keywordListArray);
                dailyJsonArray.put(dailyJsonObj);
            }
            writer.println(dailyJsonArray.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pm.close();
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            // First get a week's Link->Votes map
            fetchOneWeekLinkStats();
            // Second get the keyword->votes map
            populateKeywordVotesMap();
            // Put map key->value pair into a list
            List<KeywordVotesBundle> kvBundleList = convertMapToList(keywordCount);
            // Sort the list by number of votes
            Collections.sort(kvBundleList, new BundleComparator());
            // Only get the top 10 keywords
            int size = Math.min(kvBundleList.size(), THRESHHOLD);
            ArrayList<Key> weekList = new ArrayList<Key>();
            ArrayList<Integer> voteList = new ArrayList<Integer>();
            for (int i = 0; i < size; ++i) {
                KeywordVotesBundle kvBundle = kvBundleList.get(i);
                weekList.add(kvBundle.key);
                voteList.add(kvBundle.voteCount);
            }
            WeeklyTrends week = new WeeklyTrends(weekStart, weekEnd, weekList,
                    voteList);
            PersistenceManager pm = PMF.get().getPersistenceManager();
            pm.makePersistent(week);
            pm.close();
            writer = resp.getWriter();
            JSONObject retJson = new JSONObject();
            retJson.put("linkstats", numOfProcessedLinkStats);
            retJson.put("keywords", numOfProcessedKeywords);
            retJson.put("start_time", weekStart);
            retJson.put("end_time", weekEnd);
            writer.println(retJson.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<KeywordVotesBundle> convertMapToList(
            HashMap<Key, Integer> map) {
        List<KeywordVotesBundle> retList = new ArrayList<KeywordVotesBundle>();
        Iterator<Entry<Key, Integer>> iter = map.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry<Key, Integer> pair = iter.next();
            Key keywordKey = pair.getKey();
            int votes = pair.getValue();
            KeywordVotesBundle kvBundle = new KeywordVotesBundle(keywordKey,
                    votes);
            retList.add(kvBundle);
        }
        return retList;
    }

    @SuppressWarnings("unchecked")
    private void populateKeywordVotesMap() throws Exception {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Iterator<Entry<String, TimeVotesBundle>> iter = redditCount.entrySet()
                .iterator();
        while (iter.hasNext()) {
            Map.Entry<String, TimeVotesBundle> pair = iter.next();
            String redditId = pair.getKey();
            TimeVotesBundle tvBundle = pair.getValue();
            int vote = tvBundle.votes.upVotes + tvBundle.votes.downVotes;

            Query lkQuery = pm.newQuery(LinkKeywordMap.class);
            lkQuery.setFilter("id == idParam");
            lkQuery.declareParameters("String idParam");
            List<LinkKeywordMap> lkmResults = (List<LinkKeywordMap>) lkQuery
                    .execute(redditId);
            if (lkmResults == null || lkmResults.isEmpty()) {
                continue;
            }
            // There should be only one.
            LinkKeywordMap linkKeyword = lkmResults.get(0);
            HashSet<Key> keywordKeySet = linkKeyword.getKeywordSet();
            // Go through all keywords belong to the reddit and update the
            // Keyword->Vote map
            for (Key key : keywordKeySet) {
                int tmp = vote;
                if (keywordCount.containsKey(key)) {
                    tmp += keywordCount.get(key);
                } else {
                    ++numOfProcessedKeywords;
                }
                keywordCount.put(key, tmp);
            }
        }
        pm.close();
    }

    private static long weekStart = 0L;
    private static long weekEnd = 0L;

    @SuppressWarnings("unchecked")
    private void fetchOneWeekLinkStats() throws Exception {
        UTCTime utcTimeObj = UTCTime.fetchLastUTCTime(CURSORTYPE);
        long timeStart = utcTimeObj.getTime();
        weekStart = timeStart;
        long timeEnd = timeStart + MILLISECOND_IN_ONE_HOUR;

        // Doing fetching in batches to avoid timeout error
        // for (int i = 0; i < 7; ++i) {
        for (int j = 0; j < 24; ++j) {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            // Due to GAE limitation, here we only get the 300 LinkStats
            // Need a better way to solve it
            Query query = pm.newQuery("SELECT FROM "
                    + LinkStats.class.getName() + " WHERE timeSeen <= "
                    + timeStart + " && timeSeen < " + timeEnd
                    + " ORDER BY timeSeen DESC RANGE 0, " + BATCHSIZE);

            List<LinkStats> results = (List<LinkStats>) query.execute();
            if (!results.isEmpty()) {
                // Go through each LinkStats, and fill in a keyword->votes
                // Map.
                // Only added the latest ones' votes to the map
                for (LinkStats item : results) {
                    ++numOfProcessedLinkStats;
                    String redditId = item.getId();
                    int upvotes = item.getDowns();
                    int downvotes = item.getUps();
                    long timeSeen = item.getTimeSeen();

                    if (redditCount.containsKey(redditId)) {
                        TimeVotesBundle tvBundle = redditCount.get(redditId);
                        // Find the latest one
                        if (tvBundle.timeSeen < timeSeen) {
                            tvBundle.timeSeen = timeSeen;
                            tvBundle.votes.upVotes = upvotes;
                            tvBundle.votes.downVotes = downvotes;
                        }
                    } else {
                        TimeVotesBundle tvBundle = new TimeVotesBundle(
                                timeSeen, upvotes, downvotes);
                        redditCount.put(redditId, tvBundle);
                    }
                }
            }
            timeStart = timeEnd;
            timeEnd = timeStart + MILLISECOND_IN_ONE_HOUR;
            pm.close();
        }
        // }
        weekEnd = timeEnd;
        UTCTime.commitLastUTCTime(weekEnd, CURSORTYPE);
    }

    public static class UpDownVotesBundle {
        public int upVotes;
        public int downVotes;

        public UpDownVotesBundle(int upVotes, int downVotes) {
            this.upVotes = upVotes;
            this.downVotes = downVotes;
        }
    }

    public static class TimeVotesBundle {
        public UpDownVotesBundle votes;
        public long timeSeen;

        public TimeVotesBundle(long timeSeen, int upVotes, int downVotes) {
            this.votes = new UpDownVotesBundle(upVotes, downVotes);
            this.timeSeen = timeSeen;
        }
    }

    public static class KeywordVotesBundle {
        public Key key;
        public int voteCount;

        public KeywordVotesBundle(Key key, int votes) {
            this.key = key;
            this.voteCount = votes;
        }
    }
}
