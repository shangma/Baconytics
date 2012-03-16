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

import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkStats;
import edu.gatech.cc.baconytics.model.PMF;

@SuppressWarnings("serial")
public class ForumActivity extends HttpServlet {

    private final static int SECONDS_IN_ONE_DAY = 3600 * 24;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        PrintWriter writer = resp.getWriter();
        writer = resp.getWriter();
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = null;
        int[] votesChart = new int[SECONDS_IN_ONE_DAY / 300];
        int[] postsChart = new int[SECONDS_IN_ONE_DAY / 300];
        double[] avgVote = new double[SECONDS_IN_ONE_DAY / 300];
        try {
            query = pm.newQuery(Link.class);
            query.setOrdering("createdUtc asc");
            @SuppressWarnings("unchecked")
            List<Link> results = (List<Link>) query.execute();
            System.out.println(results.size());
            if (!results.isEmpty()) {
                for (Link e : results) {
                    System.out.println(e.getId() + " "
                            + (int) (e.getCreatedUtc() % SECONDS_IN_ONE_DAY)
                            / 300);
                    List<LinkStats> list = e.getLinkStats();
                    int index = (int) (e.getCreatedUtc() % SECONDS_IN_ONE_DAY) / 300;
                    for (LinkStats ls : list) {
                        votesChart[index] += ls.getDowns() + ls.getUps();
                    }
                    ++postsChart[index];
                }
                for (Link e : results) {
                    int index = (int) (e.getCreatedUtc() % SECONDS_IN_ONE_DAY) / 300;
                    avgVote[index] = votesChart[index] * 1.0
                            / postsChart[index];
                }

                JSONArray json = new JSONArray();
                for (int i = 0; i < SECONDS_IN_ONE_DAY / 300; ++i) {
                    JSONArray item = new JSONArray();
                    String time = getTime(i);
                    item.put(time);
                    item.put(avgVote[i]);
                    item.put(postsChart[i]);
                    item.put(votesChart[i]);
                    json.put(item);
                }

                writer.println(json.toString());
            }
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
