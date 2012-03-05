package edu.gatech.cc.Baconytics.Analyzer;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.gatech.cc.Baconytics.DataModel.PMF;

public class Analyzer extends HttpServlet {
	private static PersistenceManager pm = PMF.get().getPersistenceManager();

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse res) {

	}

}
