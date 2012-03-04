package test.edu.gatech.cc.Baconytics.appengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;

import edu.gatech.cc.Baconytics.DataModel.PMF;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEBundle;
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
        // Print all topics with keywords
        Query query = pm.newQuery(GAEReddit.class);
        @SuppressWarnings("unchecked")
        List<GAEReddit> results = (List<GAEReddit>) query.execute();
        if (!results.isEmpty()) {
            writer.println("Total: " + results.size());
            for (GAEReddit e : results) {
                HashSet<Key> kSet = e.getKeywordSet();
                writer.println("Title: " + e.getId() + " size " + kSet.size());
                for (Key key : kSet) {
                    writer.println("\tKW Key " + key.toString());
                }
            }
        }
        writer.println("=====================================================");
        query = pm.newQuery(GAEKeyword.class);
        @SuppressWarnings("unchecked")
        List<GAEKeyword> kresults = (List<GAEKeyword>) query.execute();
        if (!kresults.isEmpty()) {
            writer.println("Total: " + kresults.size());
            for (GAEKeyword e : kresults) {
                HashSet<GAEBundle> kSet = e.getBundleSet();
                writer.println("Tag: " + e.getKeyword() + " size "
                        + kSet.size());
                for (GAEBundle bundle : kSet) {
                    writer.println("\tR Key " + bundle.getRedditKey());
                }
            }
        }
    }
}
