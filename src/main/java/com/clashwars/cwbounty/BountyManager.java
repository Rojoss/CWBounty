package com.clashwars.cwbounty;

import com.clashwars.cwbounty.config.BountyCfg;
import com.clashwars.cwbounty.config.BountyData;
import com.clashwars.cwbounty.config.PlayerCfg;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Bukkit;
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



    // GET/SET BOUNTIES

    /**
     * Get a bounty by ID
     * @param id The ID of the bounty you want to get.
     * @return BountyData
     */
    public BountyData getBounty(int id) {
        String jsonStr = bCfg.getBounty(id);
        if (jsonStr.isEmpty()) {
            return null;
        }
        return gson.fromJson(jsonStr, BountyData.class);
    }

    /**
     * Set/update a bounty and save it to config.
     * @param bd The bounty data to set.
     */
    public void setBounty(BountyData bd) {
        bCfg.setBounty(bd.getID(), gson.toJson(bd));
    }

    /**
     * Get a map with all bounties by ID.
     * @return Map with all bounties out of the config.
     */
    public Map<Integer, BountyData> getBounties() {
        Map<Integer, BountyData> bounties = new HashMap<Integer, BountyData>();
        for (int ID : bCfg.getBounties().keySet()) {
            bounties.put(ID, gson.fromJson(bCfg.getBounty(ID), BountyData.class));
        }
        return bounties;
    }




    // ACTIONS

    /**
     * Create a new bounty and save it to config.
     * @param creator The name of the player who created the bounty.
     * @param target The name of the target who the bounty will be put on.
     * @param value The amount of coins this bounty has.
     * @return The ID of the bounty.
     */
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

    /**
     * Remove a bounty.
     * @param bd The bounty to remvoe.
     */
    public void removeBounty(BountyData bd) {
        removeBounty(bd.getID());
    }
    /**
     * Remove a bounty by ID.
     * @param id The ID of the bounty to remove.
     */
    public void removeBounty(int id) {
        bCfg.removeBounty(id);
    }

    /**
     * Accept a bounty and add this player as hunter.
     * @param hunter The name of the hunter that has accepted the bounty.
     * @param bd The bounty.
     */
    public void acceptBounty(String hunter, BountyData bd) {
        bd.addHunter(hunter, false);
        setBounty(bd);
    }

    /**
     * Cancel a bounty and remove this player from hunters.
     * @param hunter The name of the hunter that has cancelled hunting the bounty.
     * @param bd The bounty.
     */
    public void cancelBounty(String hunter, BountyData bd) {
        bd.removeHunter(hunter);
        setBounty(bd);
    }




    // UTILS

    /**
     * Get the bounty reward depending on time.
     * Every hour it will be decreased by 0.005% of the original value.
     * So after a week it would return 160 if the original value was 1000.
     * @param bd The bounty.
     * @return Reward depending on time difference between creation and now.
     */
    public int getReward(BountyData bd) {
        long timeDiff = (System.currentTimeMillis() - bd.getTimeCreated()) / 1000;
        int hours = (int)timeDiff / 3600;
        return (int)Math.round(bd.getBounty() * (1 - (0.005 * hours)));
    }


    /**
     * Get a string with location information.
     * It will return a proper string with a message if the location couldn't be located.
     * It will also randomize the location in a 100 block radius.
     * @param bd The bounty
     * @return String with location or message with reason why no location.
     */
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
