package test.edu.gatech.cc.Baconytics.appengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.Baconytics.appengine.GAEFeeder;
import edu.gatech.cc.Baconytics.appengine.DataModel.Reddit;

@SuppressWarnings("serial")
public class FeederTester extends HttpServlet {

    private static PrintWriter writer;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        GAEFeeder feeder = new GAEFeeder();
        writer = resp.getWriter();
        HashSet<Reddit> results = feeder.feed();
        writer.println("Testing GAEFeeder");
        writer.println("Total: " + results.size());
        for (Reddit reddit : results) {
            writer.println("Name " + reddit.getId() + "\n\tTitle: "
                    + reddit.getTitle());
        }
    }
}
