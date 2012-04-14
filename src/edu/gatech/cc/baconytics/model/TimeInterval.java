package edu.gatech.cc.baconytics.model;

import java.util.ArrayList;

import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

@PersistenceCapable(detachable = "true")
public class TimeInterval {

    @PrimaryKey
    @Persistent
    private String type;

    public TimeInterval(String type, int size) {
        this.type = type;
        postsChart = new ArrayList<Integer>(size);
        votesChart = new ArrayList<Integer>(size);
        for (int i = 0; i < size; ++i) {
            postsChart.add(0);
            votesChart.add(0);
        }
    }

    @Persistent
    private ArrayList<Integer> postsChart;

    @Persistent
    private ArrayList<Integer> votesChart;

    public ArrayList<Integer> getVotesChart() {
        return votesChart;
    }

    public void setVotesChart(ArrayList<Integer> votesChart) {
        this.votesChart = votesChart;
    }

    public ArrayList<Integer> getPostsChart() {
        return postsChart;
    }

    public void setPostsChart(ArrayList<Integer> postsChart) {
        this.postsChart = postsChart;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
