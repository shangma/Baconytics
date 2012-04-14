package edu.gatech.cc.baconytics.analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkStats;
import edu.gatech.cc.baconytics.model.PMF;
import edu.gatech.cc.baconytics.model.TimeInterval;
import edu.gatech.cc.baconytics.model.UTCTime;

@SuppressWarnings("serial")
public class ForumActivity extends HttpServlet {

    public final static int SECONDS_IN_ONE_DAY = 3600 * 24;
    public final static int TIME_INTERVAL = 300; // 5 min

    private final static int BATCH_SIZE = 100;

    public final static String CURSORTYPE = "TIMEVIS";
    public final static String TIMEINTVAL_TYPE = "FORUMACTIVITY";

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        PrintWriter writer = resp.getWriter();
        int tmpTotal = SECONDS_IN_ONE_DAY / TIME_INTERVAL;
        Query query = pm.newQuery(TimeInterval.class);
        query.setFilter("type == typeParam");
        query.declareParameters("String typeParam");

        try {
            @SuppressWarnings("unchecked")
            List<TimeInterval> results = (List<TimeInterval>) query
                    .execute(TIMEINTVAL_TYPE);
            if (!results.isEmpty()) {
                TimeInterval timeInv = results.get(0);
                ArrayList<Integer> postsChart = timeInv.getPostsChart();
                ArrayList<Integer> votesChart = timeInv.getVotesChart();
                JSONObject ret = new JSONObject();
                JSONArray voteJson = new JSONArray();
                JSONArray postJson = new JSONArray();
                for (int i = 0; i < tmpTotal; ++i) {
                    JSONArray itemVote = new JSONArray();
                    JSONArray itemPost = new JSONArray();
                    String time = getTime(i);
                    itemVote.put(time);
                    itemVote.put(votesChart.get(i));
                    voteJson.put(itemVote);
                    itemPost.put(time);
                    itemPost.put(postsChart.get(i));
                    postJson.put(itemPost);
                }
                ret.put("votes", voteJson);
                ret.put("posts", postJson);
                writer.println(ret.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = null;
        PrintWriter writer = resp.getWriter();
        int tmpTotal = SECONDS_IN_ONE_DAY / TIME_INTERVAL;
        int[] votesChart = new int[tmpTotal];
        int[] postsChart = new int[tmpTotal];
        try {
            UTCTime utcTimeObj = UTCTime.fetchLastUTCTime(CURSORTYPE);
            if (utcTimeObj == null) {
                return;
            }
            long lastUTC = utcTimeObj.getTime();
            int counter = 0;
            query = pm.newQuery(Link.class);
            query.setFilter("createdUtc > lastUTC");
            query.declareParameters("long lastUTC");
            query.setOrdering("createdUtc asc");
            query.setRange(0, BATCH_SIZE);
            @SuppressWarnings("unchecked")
            List<Link> results = (List<Link>) query.execute(lastUTC);
            if (results.isEmpty()) {
                return;
            }
            for (Link e : results) {
                List<LinkStats> list = e.getLinkStats();
                int index = (int) (e.getCreatedUtc() % SECONDS_IN_ONE_DAY)
                        / TIME_INTERVAL;
                for (LinkStats ls : list) {
                    votesChart[index] += ls.getDowns() + ls.getUps();
                }
                ++postsChart[index];
                lastUTC = Math.max(lastUTC, e.getCreatedUtc());
                ++counter;
            }
            UTCTime.commitLastUTCTime(lastUTC, CURSORTYPE);

            Query uQuery = pm.newQuery(TimeInterval.class);
            uQuery.setFilter("type == typeParam");
            uQuery.declareParameters("String typeParam");
            @SuppressWarnings({ "unchecked" })
            List<TimeInterval> tiResults = (List<TimeInterval>) uQuery
                    .execute(TIMEINTVAL_TYPE);
            TimeInterval timeInv;
            if (tiResults.isEmpty()) {
                timeInv = new TimeInterval(TIMEINTVAL_TYPE, tmpTotal);
                System.out.println("New created");
            } else {
                timeInv = tiResults.get(0);
                System.out.println("Old one");
            }
            ArrayList<Integer> posts = timeInv.getPostsChart();
            ArrayList<Integer> votes = timeInv.getVotesChart();
            System.out
                    .println("post " + posts.size() + " vote " + votes.size());
            for (int i = 0; i < tmpTotal; ++i) {
                posts.set(i, posts.get(i) + postsChart[i]);
                votes.set(i, votes.get(i) + votesChart[i]);
                System.out.println("posts " + posts.get(i) + " votes "
                        + votes.get(i));
            }
            if (tiResults.isEmpty()) {
                pm.makePersistent(timeInv);
            }
            JSONObject ret = new JSONObject();
            ret.put("results", counter);
            ret.put("cursor", lastUTC);
            writer.println(ret.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pm.close();
        }
    }

    private static String getTime(int index) {
        int min = (index % 12) * 5;
        int hour = index / 12;
        String ret = "" + hour + ":" + min;
        return ret;
    }
}
