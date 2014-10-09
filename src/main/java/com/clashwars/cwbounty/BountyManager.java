package com.clashwars.cwbounty;

import com.clashwars.cwbounty.config.BountyCfg;
import com.clashwars.cwbounty.config.BountyData;
import com.clashwars.cwbounty.config.PlayerCfg;
import com.clashwars.cwbounty.config.PluginCfg;
import com.clashwars.cwcore.packet.ParticleEffect;
import com.clashwars.cwcore.utils.CWUtil;
import com.massivecraft.factions.entity.Faction;
import com.massivecraft.factions.entity.UPlayer;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.libs.com.google.gson.Gson;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class BountyManager {

    private CWBounty cwb;
    private Gson gson;
    private BountyCfg bCfg;
    private PlayerCfg pCfg;
    private PluginCfg cfg;

    public BountyManager(CWBounty cwb) {
        this.cwb = cwb;
        bCfg = cwb.getBountyCfg();
        pCfg = cwb.getPlayerCfg();
        cfg = cwb.getCfg();
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
        if (cwb.getBountyTeam().hasPlayer(cwb.getServer().getOfflinePlayer(target))) {
            cwb.getBountyTeam().addPlayer(cwb.getServer().getOfflinePlayer(target));
        }
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
        OfflinePlayer target = cwb.getServer().getOfflinePlayer(getBounty(id).getTarget());
        bCfg.removeBounty(id);
        if (!cwb.getBountyTeam().hasPlayer(target)) {
            return;
        }
        for (BountyData bd : getBounties().values()) {
            if (!bd.getTarget().equalsIgnoreCase(target.getName())) {
                continue;
            }
            if (cwb.getBountyTeam().hasPlayer(cwb.getServer().getOfflinePlayer(bd.getTarget()))) {
                return;
            }
        }
        cwb.getBountyTeam().removePlayer(target);
    }

    /**
     * Collect a bounty and give the bounty value to the hunter.
     * Also remove the bounty from the config.
     * @param bd The bounty
     * @param hunterName The hunter who killed the bounty target.
     */
    public void collectBounty(BountyData bd, String hunterName) {
        if (expireBounty(bd)) {
            return;
        }
        Player hunter = cwb.getServer().getPlayer(hunterName);
        cwb.getEconomy().depositPlayer(hunter, getReward(bd));
        cwb.getEconomy().depositPlayer(hunter, Math.round(bd.getBounty() / 100 * cfg.PRICE__ACCEPT_DEPOSIT_PERCENTAGE));

        ParticleEffect.FLAME.display(hunter.getLocation(), 1.5f, 2.0f, 1.5f, 0.001f, 250);
        hunter.getLocation().getWorld().playSound(hunter.getLocation(), Sound.FIREWORK_TWINKLE, 1.5f, 2.0f);
        hunter.getLocation().getWorld().playSound(hunter.getLocation(), Sound.FIREWORK_TWINKLE2, 1.5f, 1.5f);

        hunter.sendMessage(Util.formatMsg("&6You collected a bounty of &e" + getReward(bd) + " coins &6by killing &5" + bd.getTarget() + "&6."));
        hunter.sendMessage(Util.formatMsg("&6You have also been refunded the coins paid for accepting the bounty."));
        cwb.getServer().broadcastMessage(Util.formatMsg("&4" + hunterName + " &6collected a bounty of &e" + getReward(bd) + " coins &6by killing " + bd.getTarget() + "."));
        removeBounty(bd);
    }

    /**
     * Expire a bounty and give back 50% of the value to the creator.
     * Also remove the bounty from the config.
     * @param bd The bounty
     * @return If the bounty is expired and removed or not.
     */
    public boolean expireBounty(BountyData bd) {
        if (bd.getTimeRemaining() <= 0) {
            OfflinePlayer creator = cwb.getServer().getPlayer(bd.getCreator());
            cwb.getEconomy().depositPlayer(creator, (bd.getBounty() / 100 * cfg.PRICE__EXPIRE_REFUND_PERCENTAGE));
            if (creator != null && creator.isOnline()) {
                ((Player)creator).sendMessage(Util.formatMsg("&6Your bounty expired. 50% of the original bounty value has been refunded."));
            }
            removeBounty(bd);
            return true;
        }
        return false;
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
        return (int)Math.round(bd.getBounty() * (1 - (cfg.PRICE__PERCENTAGE_REDUCED_PER_DAY * hours)));
    }


    /**
     * Get a string with location information.
     * It will return a proper string with a message if the location couldn't be located.
     * It will also randomize the location in a 100 block radius.
     * @param bd The bounty
     * @return String with location or message with reason why no location.
     */
    public String getLocation(BountyData bd) {
        Player target = cwb.getServer().getPlayer(bd.getTarget());
        if (target == null || !target.isOnline()) {
            return "&4Offline";
        }

        if (pCfg.getProtection(bd.getTarget()) > 0) {
            return "&cProtected";
        }

        if (cwb.getFactions() != null) {
            UPlayer uplayer = UPlayer.get(target);
            Faction faction = uplayer.getFaction();
            if (faction != null) {
                if (target.getWorld().getName().equalsIgnoreCase(faction.getHome().getWorld())) {
                    if (target.getLocation().distance(faction.getHome().asBukkitLocation()) <= cfg.FACTION_HOME_RADIUS) {
                        return "&cNear faction home.";
                    }
                }
            }
        }

        Location targetLoc = target.getLocation();
        int x = targetLoc.getBlockX();
        int z = targetLoc.getBlockZ();
        int r = cfg.RANDOM_COORDS_RADIUS;
        Location loc = new Location(targetLoc.getWorld(), CWUtil.random(x-r, x+r), targetLoc.getBlockY(), CWUtil.random(z - r, z + r));
        return "&4X:&c" + loc.getBlockX() + " &2Y:&a" + loc.getBlockY() + " &1Z:&9" + loc.getBlockZ() + " &8[&7" + target.getWorld().getEnvironment().name() + "&8]";
    }
}
