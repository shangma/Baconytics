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
import edu.gatech.cc.Baconytics.appengine.DataModel.KeyRedRel;
import edu.gatech.cc.Baconytics.appengine.DataModel.RedRel;
import edu.gatech.cc.Baconytics.appengine.DataModel.Reddit;

@SuppressWarnings("serial")
public class ExtractorTester extends HttpServlet {

    private static PrintWriter writer;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        AlchemyImpl api = new AlchemyImpl();
        GAEFeeder feeder = new GAEFeeder();
        HashSet<Reddit> source = feeder.feed();
        writer = resp.getWriter();

        writer.println("Testing GAEFeeder");
        GAEExtractor extractor = new GAEExtractor();
        HashSet<KeyRedRel> results = (HashSet<KeyRedRel>) extractor.extract(
                source, api);
        if (results == null) {
            writer.println("Restuls set is null");
            return;
        }
        for (KeyRedRel item : results) {
            writer.println(item.getKeyword());
            HashSet<RedRel> redrel = item.getRedrelSet();
            for (RedRel rr : redrel) {
                Reddit reddit = rr.getReddit();
                writer.println("\treddit:" + reddit.getId() + " rel "
                        + rr.getRelevance());
            }
        }
        writer.println("=====================================================");
        for (Reddit red : source) {
            writer.println(red.getId());
            HashSet<KeyRedRel> krrSet = red.getKeywordSet();
            for (KeyRedRel krr : krrSet) {
                writer.println("\t" + krr.getKeyword());
            }
        }
    }
}
