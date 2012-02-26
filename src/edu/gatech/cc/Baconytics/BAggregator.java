package edu.gatech.cc.Baconytics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

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

public class BAggregator extends HttpServlet {

    private static final String apiKey = "7805e728abe20f66b57c94fa1dabac2558a06dd4";// "7e868c622fa14eeeca487626bad8416c9df4a036";
    private static final AlchemyAPI alchemyObj = AlchemyAPI
            .GetInstanceFromString(apiKey);
    private static HashMap<String, Tag> tag2int = new HashMap<String, Tag>();
    private static ArrayList<Reddit> redditList = new ArrayList<Reddit>();
    private static ArrayList<Tag> tagList = new ArrayList<Tag>();
    private static PrintWriter writer;
    private static final int TOTAL_TIME_REQUEST = 3; //
    private static final int POSTS_PER_REQUEST = 25;

    class Reddit {
        String title;
        ArrayList<Tag> tags;

        Reddit(String title) {
            this.title = title;
            tags = new ArrayList<Tag>();
        }
    }

    class Bundle {
        Reddit reddit;
        double relevance;

        Bundle(Reddit reddit, double relevance) {
            this.reddit = reddit;
            this.relevance = relevance;
        }
    }

    class Tag {
        String tagString;
        ArrayList<Bundle> bundles;

        Tag(String tag) {
            this.tagString = tag;
            bundles = new ArrayList<Bundle>();
        }
    }

    private void addTag(Document doc, Reddit reddit) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter swriter = new StringWriter();
            StreamResult result = new StreamResult(swriter);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            JSONObject json = XML.toJSONObject(swriter.toString());

            // Track to the keyword array
            JSONObject results = json.optJSONObject("results");
            if (results == null) {
                return;
            }
            JSONObject kw = results.optJSONObject("keywords");
            if (kw == null) {
                return;
            }
            // Check if the returned JSON object has only one keyword
            JSONObject key = kw.optJSONObject("keyword");
            if (key != null) {
                update(key, reddit);
                return;
            }
            // If not, then check if it's an array of keywords
            JSONArray keywords = kw.optJSONArray("keyword");
            if (keywords == null) {
                return;
            }
            writer.println("[DEBUG] " + key.toString());
            int length = keywords.length();
            for (int i = 0; i < length; ++i) {
                // Extract tag and relevance from JSON
                JSONObject keyword = keywords.getJSONObject(i);
                String tag = keyword.getString("text");
                update(keyword, reddit);
            }
        } catch (org.json.JSONException ej) {
            System.out.println(ej.toString());
            System.out.println("[Error]: Cannot convert " + reddit.title);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private void update(JSONObject json, Reddit reddit) {
        try {
            String tag = json.getString("text");
            double relevance = json.getDouble("relevance");
            Tag tagObj = null;
            // Here check whether the tag already exists or not,
            // If so, get the refer, if not, new a tag
            if (!tag2int.containsKey(tag)) {
                tagObj = new Tag(tag);
                tagList.add(tagObj);
                tag2int.put(tag, tagObj);
            } else {
                tagObj = tag2int.get(tag);
            }
            // Append a bundle to the tagObj
            Bundle bundle = new Bundle(reddit, relevance);
            tagObj.bundles.add(bundle);
            // Add back tag to reddit
            reddit.tags.add(tagObj);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractRedditTopics(String str) {
        try {
            // Goto the children node and fetch its title and name
            JSONObject json = new JSONObject(str);

            JSONObject data = json.getJSONObject("data");
            JSONArray children = data.getJSONArray("children");
            String retName = null;
            for (int i = 0; i < POSTS_PER_REQUEST; ++i) { // There will be 25
                                                          // posts in each
                                                          // returned JSON
                JSONObject childData = children.getJSONObject(i).getJSONObject(
                        "data");
                String title = childData.getString("title");
                Reddit reddit = new Reddit(title);
                redditList.add(reddit);

                try {
                    Document doc = alchemyObj.TextGetRankedKeywords(title);
                    addTag(doc, reddit);
                } catch (Exception e) {
                    System.out.println("[ERROR] reddit " + reddit.title);
                    e.printStackTrace();
                }
                if (i == 24) { // Return the name of the last post for next
                               // query
                    retName = childData.getString("name");
                }
            }
            return retName;
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

        rd = new BufferedReader(new InputStreamReader(
                connection.getInputStream()));
        while ((l = rd.readLine()) != null) {
            line.append(l);
        }
        connection.disconnect();
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

            printAllData();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void printAllData() {
        writer.println("Print out all tags");
        int length = tagList.size();
        for (int i = 0; i < length; ++i) {
            Tag tag = tagList.get(i);
            writer.println("TAG: " + (i + 1) + " " + tag.tagString);
            int size = tag.bundles.size();
            for (int j = 0; j < size; ++j) {
                Bundle bundle = tag.bundles.get(j);
                writer.println("\tTitle: " + " " + bundle.reddit.title
                        + " rel: " + bundle.relevance);
            }
            writer.println("==============================================");
        }

        writer.println("\n\nPrint out all Reddits");
        length = redditList.size();
        for (int i = 0; i < length; ++i) {
            Reddit red = redditList.get(i);
            writer.println("TITLE " + (i + 1) + " " + red.title);
            int size = red.tags.size();
            for (int j = 0; j < size; ++j) {
                writer.println("\tTAG " + " " + red.tags.get(j).tagString);
            }
            writer.println("==============================================");
        }

    }

}