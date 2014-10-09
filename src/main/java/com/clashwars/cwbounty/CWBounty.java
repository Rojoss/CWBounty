package com.clashwars.cwbounty;

import com.clashwars.cwbounty.commands.Commands;
import com.clashwars.cwbounty.config.BountyCfg;
import com.clashwars.cwbounty.config.PlayerCfg;
import com.clashwars.cwbounty.config.PluginCfg;
import com.clashwars.cwbounty.events.MainEvents;
import com.clashwars.cwcore.CWCore;
import com.clashwars.cwcore.utils.CWUtil;
import com.massivecraft.factions.Factions;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class CWBounty extends JavaPlugin {
    private static CWBounty instance;
    private CWCore cwcore;
    private Factions factions;
    private Economy econ;

    private BountyManager bm;

    private PluginCfg cfg;
    private BountyCfg bountyCfg;
    private PlayerCfg playerCfg;

    private Commands cmds;

    private final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
        bountyCfg.save();
        playerCfg.save();
        Bukkit.getScheduler().cancelTasks(this);
        log("disabled");
    }

    @Override
    public void onEnable() {
        instance = this;

        Plugin plugin = getServer().getPluginManager().getPlugin("CWCore");
        if (plugin == null || !(plugin instanceof CWCore)) {
            log("CWCore dependency couldn't be loaded!");
            setEnabled(false);
            return;
        }
        cwcore = (CWCore) plugin;
        econ = cwcore.GetDM().getEconomy();
        if (econ == null) {
            log("Vault couldn't be loaded.");
            setEnabled(false);
            return;
        }
        factions = cwcore.GetDM().getFactions();
        if (factions == null) {
            log("Factions couldn't be loaded meaning that faction members can collect bounties of their own faction members.");
        }

        cfg = new PluginCfg("plugins/CWBounty/CWBounty.yml");
        cfg.load();

        bountyCfg = new BountyCfg("plugins/CWBounty/bounties.yml");
        bountyCfg.load();

        playerCfg = new PlayerCfg("plugins/CWBounty/playerData.yml");
        playerCfg.load();

        bm = new BountyManager(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MainEvents(this), this);

        cmds = new Commands(this);

        log("loaded successfully");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmds.onCommand(sender, cmd, label, args);
    }

    public void log(Object msg) {
        log.info("[CWBounty " + getDescription().getVersion() + "] " + msg.toString());
    }

    public static CWBounty inst() {
        return instance;
    }

	
	/* Getters & Setters */

    public Factions getFactions() {
        return factions;
    }

    public Economy getEconomy() {
        return econ;
    }

    public PluginCfg getCfg() {
        return cfg;
    }

    public BountyCfg getBountyCfg() {
        return bountyCfg;
    }

    public PlayerCfg getPlayerCfg() {
        return playerCfg;
    }

    public BountyManager getBM() {
        return bm;
    }
}