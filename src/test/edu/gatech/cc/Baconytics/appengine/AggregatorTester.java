package test.edu.gatech.cc.Baconytics.appengine;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.Baconytics.appengine.GAEAggregator;

@SuppressWarnings("serial")
public class AggregatorTester extends HttpServlet {

    private static PrintWriter writer;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        new GAEAggregator().aggregate();
        writer = resp.getWriter();
        writer.println("<a href='./show'>See Results</a>");
    }
}
