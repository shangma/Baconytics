package edu.gatech.cc.Baconytics.Aggregator;

import java.io.IOException;
import java.io.PrintWriter;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;

@SuppressWarnings("serial")
public class DeleteServlet extends HttpServlet {
    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private static PrintWriter writer;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        PersistenceManager pm = PMF.get().getPersistenceManager();
        writer = resp.getWriter();
        writer.println("Deleting database");
        Query query = pm.newQuery(Keyword.class);
        query.deletePersistentAll();
        query.closeAll();

        query = pm.newQuery(Reddit.class);
        query.deletePersistentAll();
        query.closeAll();

        query = pm.newQuery(Bundle.class);
        query.deletePersistentAll();
        query.closeAll();

        pm.close();
        writer.println("Done.");

    }
}
