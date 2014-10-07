package com.clashwars.cwbounty.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BountyData {

    private int id = -1;
    private boolean collected = false;
    private String creator = "";
    private String target = "";
    private int bounty = -1;
    private long timeCreated = System.currentTimeMillis();
    private Map<String, Boolean> hunters = new HashMap<String, Boolean>();

    public BountyData() {
    }


    public int getID() {
        return id;
    }
    public void setID(int id) {
        this.id = id;
    }


    public boolean isCollected() {
        return collected;
    }
    public void setCollected(boolean state) {
        this.collected = state;
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


    public long getTimeRemaining() {
        return Math.max((604800 * 1000) - (System.currentTimeMillis() - getTimeCreated()), 0);
    }


    public Map<String, Boolean> getHunters() {
        return hunters;
    }
    public void setHunters(Map<String, Boolean> hunters) {
        this.hunters = hunters;
    }
    public void addHunter(String hunter, boolean unlockedCoords) {
        hunters.put(hunter, unlockedCoords);
    }
    public void removeHunter(String hunter) {
        hunters.remove(hunter);
    }

    public boolean getCoordsUnlocked(String hunter) {
        if (hunters.containsKey(hunter)) {
            return hunters.get(hunter);
        }
        return false;
    }
    public void setCoordsUnlocked(String hunter, boolean unlocked) {
        hunters.put(hunter, unlocked);
    }
}
