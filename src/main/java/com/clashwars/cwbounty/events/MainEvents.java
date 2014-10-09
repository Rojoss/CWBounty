package com.clashwars.cwbounty.events;

import com.clashwars.cwbounty.BountyManager;
import com.clashwars.cwbounty.CWBounty;
import com.clashwars.cwbounty.config.BountyData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class MainEvents implements Listener {

    private CWBounty cwb;
    private BountyManager bm;

    public MainEvents(CWBounty cwb) {
        this.cwb = cwb;
        bm = cwb.getBM();
    }

    @EventHandler
    public void kill(PlayerDeathEvent event) {
        Player killed = event.getEntity();
        if (killed.getKiller() == null) {
            return;
        }
        Player killer = killed.getKiller();
        BountyData bd;
        for (int ID : bm.getBounties().keySet()) {
            bd = bm.getBounties().get(ID);
            if (bd.getTarget().equalsIgnoreCase(killed.getName())) {
                if (bd.getHunters().containsKey(killer.getName())) {
                    bm.collectBounty(bd, killer.getName());
                }
            }
        }
    }

}
