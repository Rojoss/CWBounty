package com.clashwars.cwbounty.commands;

import com.clashwars.cwbounty.BountyManager;
import com.clashwars.cwbounty.CWBounty;
import com.clashwars.cwbounty.config.BountyData;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class Commands {
    private CWBounty cwb;
    private BountyManager bm;

    public Commands(CWBounty cwb) {
        this.cwb = cwb;
        this.bm = cwb.getBM();
    }


    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("bounty") || label.equalsIgnoreCase("bounties")) {
            if (args.length >= 1) {
                //##########################################################################################################################
                //###################################################### /bounty help ######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("help")) {

                    return true;
                }

                //##########################################################################################################################
                //############################################### /bounty set {player} {amt} ###############################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("set") ||args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("put")) {
                    int ID = cwb.getBM().createBounty();
                    return true;
                }

                //##########################################################################################################################
                //################################################### /bounty list [me] ####################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("list")) {
                    Map<Integer, BountyData> bounties = bm.getBounties();
                    for (int ID : bounties.keySet()) {
                        BountyData bd = bounties.get(ID);
                        sender.sendMessage(CWUtil.integrateColor("&8[&5" + ID + "&8] &6" + bd.getTarget() + " &8- &e" + bd.getBounty()));
                    }
                    return true;
                }

                //##########################################################################################################################
                //################################################## /bounty accept {ID} ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("hunt") || args[0].equalsIgnoreCase("claim")) {
                    BountyData bd = bm.getBounty(1);
                    bd.addHunter(sender.getName());
                    bm.setBounty(bd);
                    return true;
                }

                //##########################################################################################################################
                //################################################## /bounty cancel {ID} ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("cancel")) {
                    BountyData bd = bm.getBounty(1);
                    bd.removeHunter(sender.getName());
                    bm.setBounty(bd);
                    return true;
                }

                //##########################################################################################################################
                //################################################## /bounty status [ID] ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("status")) {

                    return true;
                }

                //##########################################################################################################################
                //###################################################### /bounty me ########################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("me")) {

                    return true;
                }
            }

            sender.sendMessage(CWUtil.integrateColor("&8===== &4&lCommand Help &6/" + label + " &8====="));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " &8- &5Show this page."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " help &8- &5Show information how bounties work."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " set {player} {amount} &8- &5Create a new bounty."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " list [me] &8- &5List all bounties or your own created."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " accept {ID} &8- &5Accept a bounty and start hunting!"));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " cancel {ID} &8- &5Cancel a accepted bounty!"));
            sender.sendMessage(CWUtil.integrateColor("&8(&7You won't get your coins back!&8)"));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " status &8- &5See the status of your accepted bounties."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " me &8- &5See all bounties on yourself."));
            return true;
        }
        return false;
    }
}
