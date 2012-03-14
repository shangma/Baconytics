package edu.gatech.cc.baconytics.aggregator.nlp;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;
import org.w3c.dom.Document;

import com.alchemyapi.api.AlchemyAPI;

import edu.gatech.cc.baconytics.aggregator.model.KeywordRelevance;

public class AlchemyImpl implements NlpApi {
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

    private static KeywordRelevance makePair(JSONObject json) {
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
            KeywordRelevance ret = new KeywordRelevance(keyword, relevance);
            return ret;
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return null;
    }

    @Override
    public Set<KeywordRelevance> process(String param) {
        HashSet<KeywordRelevance> ret = new HashSet<KeywordRelevance>();
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
