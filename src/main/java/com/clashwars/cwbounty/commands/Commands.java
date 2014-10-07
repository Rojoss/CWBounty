package com.clashwars.cwbounty.commands;

import com.clashwars.cwbounty.CWBounty;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class Commands {
    private CWBounty cwb;

    public Commands(CWBounty cwb) {
        this.cwb = cwb;
    }


    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("bounty") || label.equalsIgnoreCase("bounties")) {
            if (args.length >= 1)                 {
                //##########################################################################################################################
                //###################################################### /bounty info ######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("info")) {

                    return true;
                }

                //##########################################################################################################################
                //############################################### /bounty set {player} {amt} ###############################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("set") ||args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("put")) {

                    return true;
                }

                //##########################################################################################################################
                //################################################### /bounty list [me] ####################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("list")) {

                    return true;
                }

                //##########################################################################################################################
                //################################################## /bounty accept {ID} ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("hunt") || args[0].equalsIgnoreCase("claim")) {

                    return true;
                }

                //##########################################################################################################################
                //################################################## /bounty cancel {ID} ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("cancel")) {

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
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " info &8- &5Show information how bounties work."));
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
