package com.clashwars.cwbounty;

import com.clashwars.cwbounty.commands.Commands;
import com.clashwars.cwbounty.events.MainEvents;
import com.clashwars.cwcore.CWCore;
import com.massivecraft.factions.Factions;
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

    private Commands cmds;

    private final Logger log = Logger.getLogger("Minecraft");

    @Override
    public void onDisable() {
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
        factions = cwcore.GetDM().getFactions();
        if (factions == null) {
            log("Factions couldn't be loaded meaning that faction members can collect bounties of their own faction members.");
        }

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new MainEvents(this), this);

        cmds = new Commands(this);

        log("loaded successfully");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        return cmds.onCommand(sender, cmd, label, args);
    }

    public void log(Object msg) {
        log.info("[CWBounty " + getDescription().getVersion() + "]: " + msg.toString());
    }

    public static CWBounty inst() {
        return instance;
    }

	
	/* Getters & Setters */

    public Factions getFactions() {
        return factions;
    }
}