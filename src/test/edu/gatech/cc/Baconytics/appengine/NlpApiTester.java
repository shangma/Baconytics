package test.edu.gatech.cc.Baconytics.appengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.Baconytics.Aggregator.AlchemyAPI.AlchemyImpl;
import edu.gatech.cc.Baconytics.appengine.DataModel.TagRel;

@SuppressWarnings("serial")
public class NlpApiTester extends HttpServlet {

    private static PrintWriter writer;
    final private static String str = "I am just testing this servlet to see if Alchemy API really works";

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        AlchemyImpl api = new AlchemyImpl();
        writer = resp.getWriter();
        writer.println("Test String: " + str);
        HashSet<TagRel> ret = api.process(str);
        for (TagRel tr : ret) {
            writer.println("keyword: " + tr.getKeyword() + " rel: "
                    + tr.getRelevance());
        }
    }
}