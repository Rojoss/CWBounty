package com.clashwars.cwbounty;

import com.clashwars.cwbounty.config.BountyCfg;
import com.clashwars.cwbounty.config.BountyData;
import com.clashwars.cwbounty.config.PlayerCfg;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BountyManager {

    private CWBounty cwb;
    private Gson gson;
    private BountyCfg bCfg;
    private PlayerCfg pCfg;

    public BountyManager(CWBounty cwb) {
        this.cwb = cwb;
        bCfg = cwb.getBountyCfg();
        pCfg = cwb.getPlayerCfg();
        gson = new Gson();
    }


    public BountyData getBounty(int id) {
        String jsonStr = bCfg.getBounty(id);
        if (jsonStr.isEmpty()) {
            return null;
        }
        return gson.fromJson(jsonStr, BountyData.class);
    }

    public void setBounty(BountyData bd) {
        bCfg.setBounty(bd.getID(), gson.toJson(bd));
    }

    public void setBounty(int id, BountyData bd) {
        bCfg.setBounty(id, gson.toJson(bd));
    }

    public int createBounty(String creator, String target, int value) {
        BountyData bd = new BountyData();

        int i = 0;
        while (bCfg.getBounties().containsKey(i)) {
            i++;
        }
        bd.setID(i);
        bd.setCreator(creator);
        bd.setTarget(target);
        bd.setBounty(value);

        bCfg.setBounty(i, gson.toJson(bd));
        return i;
    }

    public Map<Integer, BountyData> getBounties() {
        Map<Integer, BountyData> bounties = new HashMap<Integer, BountyData>();
        for (int ID : bounties.keySet()) {
            bounties.put(ID, gson.fromJson(bCfg.getBounty(ID), BountyData.class));
        }
        return bounties;
    }

    public int getReward(BountyData bd) {
        long timeDiff = (System.currentTimeMillis() - bd.getTimeCreated()) / 1000;
        int hours = (int)timeDiff / 60;
        return (int)Math.round(bd.getBounty() * (1 - (0.005 * hours)));
    }

    public String getLocation(BountyData bd) {
        if (bd.getTimeRemaining() <= 0 || bd.isCollected()) {
            return "&7Already collected";
        }

        Player target = cwb.getServer().getPlayer(bd.getTarget());
        if (target == null || !target.isOnline()) {
            return "&4Offline";
        }

        if (pCfg.getProtection(bd.getTarget()) > 0) {
            return "&cProtected";
        }

        //TODO: Check for near faction home.

        Location targetLoc = target.getLocation();
        int x = targetLoc.getBlockX();
        int z = targetLoc.getBlockZ();
        int r = 100;
        Location loc = new Location(targetLoc.getWorld(), CWUtil.random(x-r, x+r), targetLoc.getBlockY(), CWUtil.random(z - r, z + r));

        return "&4X:&c" + loc.getBlockX() + " &2Y:&a" + loc.getBlockY() + " &1Z:&9" + loc.getBlockZ();
    }
}
