package test.edu.gatech.cc.Baconytics.appengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Time;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.Baconytics.DataModel.PMF;
import edu.gatech.cc.Baconytics.appengine.GAEFeeder;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEKeyword;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEReddit;

@SuppressWarnings("serial")
public class Show extends HttpServlet {

    private static PrintWriter writer;
    private static PersistenceManager pm = PMF.get().getPersistenceManager();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        writer = resp.getWriter();
        Time tm = new Time(GAEFeeder.lastUTCTime);
        writer.println("Last update: " + tm.toString());
        // Print all topics with keywords
        writer.println("============= Title -> Keyword =================\n");
        Query query = pm.newQuery(GAEReddit.class);
        @SuppressWarnings("unchecked")
        List<GAEReddit> results = (List<GAEReddit>) query.execute();
        if (!results.isEmpty()) {
            writer.println("Total: " + results.size());
            for (GAEReddit e : results) {
                writer.println(e.toString());
            }
        }
        writer.println("============= Keyword -> Title =================\n");
        query = pm.newQuery(GAEKeyword.class);
        @SuppressWarnings("unchecked")
        List<GAEKeyword> kresults = (List<GAEKeyword>) query.execute();
        if (!kresults.isEmpty()) {
            writer.println("Total: " + kresults.size() + "\n");
            for (GAEKeyword e : kresults) {
                writer.println(e.toString());
            }
        }
    }
}
