package com.clashwars.cwbounty;

import com.clashwars.cwbounty.config.BountyCfg;
import com.clashwars.cwbounty.config.BountyData;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class BountyManager {

    private CWBounty cwb;
    private Gson gson;
    private BountyCfg cfg;

    public BountyManager(CWBounty cwb) {
        this.cwb = cwb;
        cfg = cwb.getBountyCfg();
        gson = new Gson();
    }


    public BountyData getBounty(int id) {
        String jsonStr = cfg.getBounty(id);
        if (jsonStr.isEmpty()) {
            return null;
        }
        return gson.fromJson(jsonStr, BountyData.class);
    }

    public void setBounty(BountyData bd) {
        cfg.setBounty(bd.getID(), gson.toJson(bd));
    }

    public void setBounty(int id, BountyData bd) {
        cfg.setBounty(id, gson.toJson(bd));
    }

    public int createBounty(String creator, String target, int value) {
        BountyData bd = new BountyData();

        int i = 0;
        while (cfg.getBounties().containsKey(i)) {
            i++;
        }
        bd.setID(i);
        bd.setCreator(creator);
        bd.setTarget(target);
        bd.setBounty(value);

        cfg.setBounty(i, gson.toJson(bd));
        return i;
    }

    public Map<Integer, BountyData> getBounties() {
        Map<Integer, BountyData> bounties = new HashMap<Integer, BountyData>();
        for (int ID : bounties.keySet()) {
            bounties.put(ID, gson.fromJson(cfg.getBounty(ID), BountyData.class));
        }
        return bounties;
    }
}
