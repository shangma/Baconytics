package edu.gatech.cc.Baconytics.Aggregator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;

import com.alchemyapi.api.AlchemyAPI;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class BAggregator extends HttpServlet {

    private static final String apiKey = "7805e728abe20f66b57c94fa1dabac2558a06dd4";// "7e868c622fa14eeeca487626bad8416c9df4a036";
    private static final AlchemyAPI alchemyObj = AlchemyAPI
            .GetInstanceFromString(apiKey);
    private static PrintWriter writer;
    private static final int TOTAL_TIME_REQUEST = 1; //
    private static PersistenceManager pm = PMF.get().getPersistenceManager();

    private JSONObject extractKeywordsJSON(String str) {
        try {
            JSONObject json = XML.toJSONObject(str);
            // Track to the keyword array
            JSONObject results = json.optJSONObject("results");
            if (results == null) {
                return null;
            }
            JSONObject kw = results.optJSONObject("keywords");
            if (kw == null) {
                return null;
            } else {
                return kw;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addTag(Document doc, Reddit reddit) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter swriter = new StringWriter();
            StreamResult result = new StreamResult(swriter);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            JSONObject kw = extractKeywordsJSON(swriter.toString());
            if (kw == null) {
                return;
            }
            JSONObject key = kw.optJSONObject("keyword");
            if (key != null) { // only one keyword
                System.out.println("\t[INFO] Single keyword ");
                update(key, reddit);
                return;
            }
            // multiple keywords
            JSONArray keywords = kw.optJSONArray("keyword");
            if (keywords == null) {
                return;
            }
            System.out.println("\t[INFO] Multiple keywords ");
            int length = keywords.length();
            for (int i = 0; i < length; ++i) {
                JSONObject keyword = keywords.getJSONObject(i);
                update(keyword, reddit);
            }

        } catch (org.json.JSONException ej) {
            System.out.println(ej.toString());
            System.out.println("[Error]: Cannot convert " + reddit.getTitle());
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private Reddit lookupReddit(String name) {
        Query query = pm.newQuery(Reddit.class);
        query.setFilter("name == lastNameParam");
        query.declareParameters("String lastNameParam");

        try {
            @SuppressWarnings("unchecked")
            List<Reddit> results = (List<Reddit>) query.execute(name);
            if (!results.isEmpty()) {
                for (Reddit e : results) {
                    return e; // There should be only one
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Keyword lookupKeyword(String keyword) {
        Query query = pm.newQuery(Keyword.class);
        query.setFilter("keyword == lastNameParam");
        query.declareParameters("String lastNameParam");

        try {
            @SuppressWarnings("unchecked")
            List<Keyword> results = (List<Keyword>) query.execute(keyword);
            if (!results.isEmpty()) {
                for (Keyword e : results) {
                    return e; // There should be only one
                }
            }
        } finally {
            query.closeAll();
        }
        return null;
    }

    private void update(JSONObject json, Reddit reddit) {
        try {
            String keyword = json.getString("text");
            double relevance = json.getDouble("relevance");
            Keyword kwObj = lookupKeyword(keyword);

            // Here check whether the tag already exists or not,
            // If so, get the refer, if not, create a tag
            if (kwObj == null) {
                System.out.println("[INFO] " + keyword
                        + " is a new keyword, added to db");
                kwObj = new Keyword(keyword);
                Key key = KeyFactory.createKey(Keyword.class.getSimpleName(),
                        keyword);
                kwObj.setKey(key);
            } else {
                System.out.println("[INFO] " + keyword + " already exists");
            }

            // Append a bundle to the kwObj
            System.out
                    .println("[WARNING] Adding reddit KEY " + reddit.getKey());
            Bundle bundle = new Bundle(reddit.getName(), kwObj, relevance);
            kwObj.getBundleSet().add(bundle);

            // Commit Keyword
            System.out.println("Commit Keyword " + kwObj.getKeyword());
            pm.makePersistent(kwObj);

            // Add back kwObj to reddit
            reddit.getKeywordSet().add(kwObj.getKey());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractRedditTopics(String str) {
        try {
            // Goto the children node and fetch its title and name
            JSONObject json = new JSONObject(str);
            JSONArray children = json.optJSONObject("data").optJSONArray(
                    "children");
            if (children == null) {
                return null;
            }
            String name = null;
            int size = children.length();
            System.out.println("How many reddits: " + size);
            for (int i = 0; i < size; ++i) { // There will be 25 posts in each
                                             // returned JSON
                JSONObject childData = children.getJSONObject(i).getJSONObject(
                        "data");
                String title = childData.getString("title");
                name = childData.getString("name");

                System.out.print("[DEBUG] current title " + title);
                Reddit reddit = lookupReddit(name);
                if (reddit != null) {
                    System.out.println("already exists");
                    continue; // Skip the ones that have already been processed
                }
                Key key = KeyFactory.createKey(Reddit.class.getSimpleName(),
                        name);
                System.out.println("[INFO] " + key + "is new, add to db");
                reddit = new Reddit(name, title);
                reddit.setKey(key);

                Document doc = alchemyObj.TextGetRankedKeywords(title);
                addTag(doc, reddit);
                // Commit Reddit
                System.out.println("Commit Reddit " + reddit.getName());
                pm.makePersistent(reddit);
            }
            return name;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getRedditTitles(String url) throws Exception {
        HttpURLConnection connection = null;
        URL serverAddress = null;
        BufferedReader rd = null;
        StringBuffer line = new StringBuffer();
        String l = null;

        serverAddress = new URL(url);
        connection = (HttpURLConnection) serverAddress.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoOutput(true);
        connection.setReadTimeout(10000);
        connection.connect();
        System.out.println("Connected to Reddit.com...");
        rd = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        while ((l = rd.readLine()) != null) {
            line.append(l);
        }
        connection.disconnect();
        System.out.println("Disconnected to Reddit.com...");
        return extractRedditTopics(line.toString());
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String baseUrl = "http://www.reddit.com/top/.json";
        writer = resp.getWriter();
        // Requesting top 100 topics
        try {
            String suffix = getRedditTitles(baseUrl);
            for (int timesOfRequests = 1; timesOfRequests < TOTAL_TIME_REQUEST; ++timesOfRequests) {
                System.out.println("suffix " + suffix);
                suffix = getRedditTitles(baseUrl + "?count=25&after=" + suffix);
            }
            pm.close();
            // printAllData();
            writer.println("<a href='./show'>See results</a>");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
