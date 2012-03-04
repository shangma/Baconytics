package edu.gatech.cc.Baconytics.Aggregator.AlchemyAPI;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;

import com.alchemyapi.api.AlchemyAPI;

import edu.gatech.cc.Baconytics.Aggregator.BaseClasses.NlpApiInterface;
import edu.gatech.cc.Baconytics.appengine.DataModel.TagRel;

public class AlchemyImpl implements NlpApiInterface<String, TagRel> {
    // private static final String apiKey =
    // "7805e728abe20f66b57c94fa1dabac2558a06dd4";
    private static final String apiKey = "7e868c622fa14eeeca487626bad8416c9df4a036";
    private static final AlchemyAPI alchemyObj = AlchemyAPI
            .GetInstanceFromString(apiKey);

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

    private static TagRel makePair(JSONObject json) {
        try {
            // System.out.println("json " + json.toString());
            String keyword = json.getString("text");
            double relevance = 0.0;
            String sRel = json.optString("relevance"); // Alchemy sometimes goes
                                                       // crazy an returns -inf
            if (sRel != null) {
                relevance = json.getDouble("relevance");
            } else {
                relevance = -1.0;
            }
            TagRel ret = new TagRel(keyword, relevance);
            return ret;
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    @Override
    public HashSet<TagRel> process(String param) {
        HashSet<TagRel> ret = new HashSet<TagRel>();
        try {
            Document doc = alchemyObj.TextGetRankedKeywords(param);
            DOMSource domSource = new DOMSource(doc);
            StringWriter swriter = new StringWriter();
            StreamResult result = new StreamResult(swriter);

            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);

            JSONObject kw = extractKeywordsJSON(swriter.toString());
            if (kw == null) {
                return null;
            }
            JSONObject key = kw.optJSONObject("keyword");
            if (key != null) { // only one keyword
                ret.add(makePair(key));
                return ret;
            }
            // multiple keywords
            JSONArray keywords = kw.optJSONArray("keyword");
            if (keywords == null) {
                return null;
            }
            int length = keywords.length();
            for (int i = 0; i < length; ++i) {
                JSONObject keyword = keywords.getJSONObject(i);
                ret.add(makePair(keyword));
            }
            return ret;
        } catch (IllegalArgumentException ie) {
            System.out.println("Alchemy cannot figure out " + param);
            // TagRel tr = new TagRel("N/A", 0.0);
            // ret.add(tr);
            return ret;
        } catch (IOException ioe) {
            System.out.println(ioe.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
