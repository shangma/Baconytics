package test.edu.gatech.cc.Baconytics.appengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.Baconytics.Aggregator.AlchemyAPI.AlchemyImpl;
import edu.gatech.cc.Baconytics.appengine.GAEExtractor;
import edu.gatech.cc.Baconytics.appengine.GAEFeeder;
import edu.gatech.cc.Baconytics.appengine.GAEPutter;
import edu.gatech.cc.Baconytics.appengine.DataModel.KeyRedRel;
import edu.gatech.cc.Baconytics.appengine.DataModel.Reddit;

@SuppressWarnings("serial")
public class PutterTester extends HttpServlet {

    private static PrintWriter writer;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        AlchemyImpl api = new AlchemyImpl();
        GAEFeeder feeder = new GAEFeeder();
        GAEExtractor extractor = new GAEExtractor();

        HashSet<Reddit> source = feeder.feed();
        writer = resp.getWriter();
        HashSet<KeyRedRel> results = (HashSet<KeyRedRel>) extractor.extract(
                source, api);
        writer.println("Testing GAEPutter");
        GAEPutter putter = new GAEPutter();
        putter.put(source, results);

    }
}
