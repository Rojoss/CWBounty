package com.clashwars.cwbounty.config;

import org.bukkit.Bukkit;

import java.util.*;

public class BountyData {

    private int id = -1;
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


    public String getCreator() {
        return Bukkit.getPlayer(UUID.fromString(creator)).getName();
    }
    public void setCreator(UUID creator) {
        this.creator = creator.toString();
    }


    public String getTarget() {
        return Bukkit.getPlayer(UUID.fromString(target)).getName();
    }
    public void setTarget(UUID target) {
        this.target = target.toString();
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
        Map<String, Boolean> playerHunters = new HashMap<String, Boolean>();
        for (String uuid : hunters.keySet()) {
            playerHunters.put(Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName(), hunters.get(uuid));
        }
        return playerHunters;
    }
    public void addHunter(UUID hunter, boolean unlockedCoords) {
        hunters.put(hunter.toString(), unlockedCoords);
    }
    public void removeHunter(UUID hunter) {
        hunters.remove(hunter.toString());
    }

    public boolean getCoordsUnlocked(UUID hunter) {
        if (hunters.containsKey(hunter.toString())) {
            return hunters.get(hunter.toString());
        }
        return false;
    }
    public void setCoordsUnlocked(UUID hunter, boolean unlocked) {
        hunters.put(hunter.toString(), unlocked);
    }
}
