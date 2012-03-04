package test.edu.gatech.cc.Baconytics.appengine;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.Baconytics.DataModel.PMF;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEReddit;

@SuppressWarnings("serial")
public class DeleteGAEReddit extends HttpServlet {
    private static PrintWriter writer;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        writer = resp.getWriter();
        Query query = pm.newQuery(GAEReddit.class);

        query.deletePersistentAll();
        query.closeAll();

        pm.close();
        writer.println("Deleted GAEReddit");

    }
}
