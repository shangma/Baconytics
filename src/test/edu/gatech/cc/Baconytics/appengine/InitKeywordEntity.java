package test.edu.gatech.cc.Baconytics.appengine;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

import edu.gatech.cc.Baconytics.DataModel.PMF;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEBundle;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEKeyword;
import edu.gatech.cc.Baconytics.appengine.DataModel.GAEReddit;

@SuppressWarnings("serial")
public class InitKeywordEntity extends HttpServlet {

    private static PrintWriter writer;
    private static PersistenceManager pm = PMF.get().getPersistenceManager();

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Key redKey = KeyFactory
                .createKey(GAEReddit.class.getSimpleName(), "@@");
        Key kwKey = KeyFactory
                .createKey(GAEKeyword.class.getSimpleName(), "@@");
        GAEKeyword keyword = new GAEKeyword("@@");
        keyword.setKey(kwKey);
        GAEBundle bundle = new GAEBundle(redKey, keyword, -1.0);
        HashSet<GAEBundle> bdSet = new HashSet<GAEBundle>();
        bdSet.add(bundle);
        keyword.setRedditList(bdSet);
        pm.makePersistent(keyword);
        writer = resp.getWriter();
        writer.println("Initialized GAEKeyword Entity");
    }
}
