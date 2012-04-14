package edu.gatech.cc.baconytics.analyzer;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;

import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkStats;
import edu.gatech.cc.baconytics.model.PMF;
import edu.gatech.cc.baconytics.model.UTCTime;

@SuppressWarnings("serial")
public class ForumActivity extends HttpServlet {

    public final static int SECONDS_IN_ONE_DAY = 3600 * 24;
    public final static int TIME_INTERVAL = 300; // 5 min

    private final static int BATCH_SIZE = 30;

    private final static String CURSORTYPE = "TIMEVIS";

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        PrintWriter writer = resp.getWriter();
        int tmpTotal = SECONDS_IN_ONE_DAY / TIME_INTERVAL;
        int[] votesChart = new int[tmpTotal];
        int[] postsChart = new int[tmpTotal];
        double[] avgVote = new double[tmpTotal];
        try {
            JSONArray json = new JSONArray();
            for (int i = 0; i < tmpTotal; ++i) {
                JSONArray item = new JSONArray();
                String time = getTime(i);
                item.put(time);
                item.put(avgVote[i]);

                item.put(postsChart[i]);
                item.put(votesChart[i]);
                json.put(item);
            }
            writer.println(json.toString());
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = null;
        int tmpTotal = SECONDS_IN_ONE_DAY / TIME_INTERVAL;
        int[] votesChart = new int[tmpTotal];
        int[] postsChart = new int[tmpTotal];
        double[] avgVote = new double[tmpTotal];
        try {
            UTCTime utcTimeObj = UTCTime.fetchLastUTCTime(CURSORTYPE);
            if (utcTimeObj == null) {
                return;
            }
            long lastUTC = utcTimeObj.getTime();
            while (true) {
                query = pm.newQuery(Link.class);
                query.setFilter("createdUtc > lastUTC");
                query.declareParameters("long lastUTC");
                query.setOrdering("createdUtc asc");
                query.setRange(0, BATCH_SIZE);
                @SuppressWarnings("unchecked")
                List<Link> results = (List<Link>) query.execute(lastUTC);
                if (results.isEmpty()) {
                    break;
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
                }
                for (Link e : results) {
                    int index = (int) (e.getCreatedUtc() % SECONDS_IN_ONE_DAY)
                            / TIME_INTERVAL;
                    avgVote[index] = votesChart[index] * 1.0
                            / postsChart[index];
                }
            }
            UTCTime.commitLastUTCTime(lastUTC, CURSORTYPE);
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
