package test.edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.baconytics.model.PMF;
import edu.gatech.cc.baconytics.model.UTCTime;

@SuppressWarnings("serial")
public class SetUTCTime extends HttpServlet {

    private static PrintWriter writer;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        writer = resp.getWriter();
        long newTime = -1;
        try {
            PersistenceManager pm = PMF.get().getPersistenceManager();
            String param = req.getParameter("time");
            if (param == null) {
                System.out.println("no param");
                UTCTime time = new UTCTime();
                time.setTime(0);
                pm.makePersistent(time);
                pm.close();
                writer.println("Set UTC Time: " + time.getTime());
                return;
            }

            newTime = Long.parseLong(param);
            System.out.println("param " + param);
            pm = PMF.get().getPersistenceManager();
            Query query = pm.newQuery(UTCTime.class);
            @SuppressWarnings("unchecked")
            List<UTCTime> results = (List<UTCTime>) query.execute();
            if (!results.isEmpty()) {
                for (UTCTime e : results) {
                    writer.println("Old Time: " + e.getTime());
                    e.setTime(newTime);
                    writer.println("New Time: " + e.getTime());
                    System.out.println("New time " + newTime);
                }
            }
            pm.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

    }
}
