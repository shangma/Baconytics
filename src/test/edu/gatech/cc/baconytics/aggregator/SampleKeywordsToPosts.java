package test.edu.gatech.cc.baconytics.aggregator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import edu.gatech.cc.baconytics.model.KeywordLinkMap;
import edu.gatech.cc.baconytics.model.Link;
import edu.gatech.cc.baconytics.model.LinkRelevanceBundle;
import edu.gatech.cc.baconytics.model.PMF;

@SuppressWarnings("serial")
public class SampleKeywordsToPosts extends HttpServlet {
    public static PrintWriter writer = null;

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        writer = resp.getWriter();
        PersistenceManager pm = PMF.get().getPersistenceManager();
        Query query = pm.newQuery(KeywordLinkMap.class);
        JSONObject ret = new JSONObject();
        query.setRange(0, 1);
        try {
            @SuppressWarnings("unchecked")
            List<KeywordLinkMap> results = (List<KeywordLinkMap>) query
                    .execute();
            if (!results.isEmpty()) {
                KeywordLinkMap klMap = results.get(0);
                HashSet<LinkRelevanceBundle> krBundle = klMap.getBundleSet();
                JSONArray linkJsonArray = new JSONArray();
                for (LinkRelevanceBundle item : krBundle) {
                    Query linkQuery = pm.newQuery(Link.class);
                    linkQuery.setFilter("id == idParam");
                    linkQuery.declareParameters("String idParam");

                    @SuppressWarnings("unchecked")
                    List<Link> linkResults = (List<Link>) linkQuery
                    // Use getName() to get the link's id
                            .execute(item.getLinkKey().getName());
                    Link link = linkResults.get(0);
                    JSONObject linkJson = new JSONObject();
                    linkJson.put("author", link.getAuthor());
                    linkJson.put("domain", link.getDomain());
                    linkJson.put("name", link.getName());
                    linkJson.put("permalink", link.getPermalink());
                    linkJson.put("title", link.getTitle());
                    linkJson.put("url", link.getUrl());
                    linkJson.put("subreddit", link.getSubreddit());
                    linkJson.put("subredditId", link.getSubredditId());
                    linkJsonArray.put(linkJson);
                }
                ret.put("keyword", klMap.getKeyword());
                ret.put("link_list", linkJsonArray);
            }
            writer.println(ret.toString());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            pm.close();
        }
    }
}
