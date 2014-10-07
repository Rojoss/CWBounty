package com.clashwars.cwbounty.config;

import java.util.ArrayList;
import java.util.List;

public class BountyData {

    private int id = -1;
    private String creator = "";
    private String target = "";
    private int bounty = -1;
    private long timeCreated = System.currentTimeMillis();
    private List<String> hunters = new ArrayList<String>();

    public BountyData() {
    }


    public int getID() {
        return id;
    }
    public void setID(int id) {
        this.id = id;
    }


    public String getCreator() {
        return creator;
    }
    public void setCreator(String creator) {
        this.creator = creator;
    }


    public String getTarget() {
        return creator;
    }
    public void setTarget(String target) {
        this.target = target;
    }


    public int getBounty() {
        return bounty;
    }
    public void setBounty(int bounty) {
        this.bounty = bounty;
    }


    public long getTimeCreated() {
        return timeCreated;
    }
    public void setTimeCreated(long timeCreated) {
        this.timeCreated = timeCreated;
    }


    public List<String> getHunters() {
        return hunters;
    }
    public void setHunters(List<String> hunters) {
        this.hunters = hunters;
    }
    public void addHunter(String hunter) {
        hunters.add(hunter);
    }
    public void removeHunter(String hunter) {
        hunters.remove(hunter);
    }
}
