package com.clashwars.cwbounty.commands;

import com.clashwars.cwbounty.BountyManager;
import com.clashwars.cwbounty.CWBounty;
import com.clashwars.cwbounty.Util;
import com.clashwars.cwbounty.config.BountyData;
import com.clashwars.cwcore.utils.CWUtil;
import org.apache.commons.lang.ArrayUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

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
                if (args[0].equalsIgnoreCase("set") || args[0].equalsIgnoreCase("create") || args[0].equalsIgnoreCase("put")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("Player command only."));
                        return true;
                    }
                    Player player = (Player)sender;

                    if (args.length < 3) {
                        player.sendMessage(Util.formatMsg("&cInvalid command usage. &4/" + label + " " + args[0] + " {player} {amt}"));
                        return true;
                    }

                    if (cwb.getServer().getPlayer(args[1]) == null) {
                        player.sendMessage(Util.formatMsg("&cInvalid player specified."));
                        return true;
                    }
                    Player target = cwb.getServer().getPlayer(args[1]);

                    if (CWUtil.getInt(args[2]) < 250) {
                        player.sendMessage(Util.formatMsg("&cThe bounty has to be at least &4250 coins&c."));
                        return true;
                    }
                    int value = CWUtil.getInt(args[2]);

                    if (cwb.getEconomy().getBalance(player) < value) {
                        player.sendMessage(Util.formatMsg("&cYou don't have enough coins. You need &4" + value + " coins&c."));
                        return true;
                    }

                    cwb.getEconomy().withdrawPlayer(player, value);
                    int ID = cwb.getBM().createBounty(player.getName(), target.getName(), value);
                    player.sendMessage(Util.formatMsg("&6Bounty created!"));
                    cwb.getServer().broadcastMessage(Util.formatMsg("&5" + player.getName() + " &6placed a bounty with a value of &e" + value + " coins &6on &5" + target.getName() + "'s &6head!"));
                    cwb.getServer().broadcastMessage(Util.formatMsg("&6Use &5/bounty accept " + ID  + " &6and &4kill &6him to collect this bounty!"));
                    return true;
                }

                //##########################################################################################################################
                //################################################# /bounty list [page] ####################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("list")) {
                    Map<Integer, BountyData> bounties = bm.getBounties();
                    int pages = Math.max(bounties.size() > 0 ? (int)Math.ceil(bounties.size()/12) : 1, 1);

                    int page = 1;
                    if (args.length >= 2) {
                        if (CWUtil.getInt(args[1]) > 1 && CWUtil.getInt(args[1]) <= pages) {
                            page = CWUtil.getInt(args[1]);
                        } else {
                            sender.sendMessage(Util.formatMsg("&cInvalid page number specified. Must be a number between 1 and " + pages));
                            return true;
                        }
                    }

                    sender.sendMessage(CWUtil.integrateColor("&8========= &4&lListing all bounties &7[&d" + page + "&8/&5" + pages + "&7] &8========="));
                    List<Integer> bountyIds = new ArrayList<Integer>();
                    bountyIds.addAll(bounties.keySet());
                    for (int i = (pages * 12) - 12; i < pages * 12; i++) {
                        BountyData bd = bounties.get(bountyIds.get(i));
                        if (bd != null) {
                            sender.sendMessage(CWUtil.integrateColor("&8[&5" + bd.getID() + "&8] &6" + bd.getTarget() + " &8- &e" + bd.getBounty()));
                        }
                    }
                    sender.sendMessage(CWUtil.integrateColor("&8===== &4Use &c/" + label + " list " + (page+1) + " &4for the next page &8====="));
                    return true;
                }

                //##########################################################################################################################
                //################################################# /bounty info {ID} ####################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("show")) {
                    if (args.length < 2) {
                        sender.sendMessage(Util.formatMsg("&cInvalid command usage. &4/" + label + " " + args[0] + " {ID}"));
                        return true;
                    }

                    if (CWUtil.getInt(args[1]) < 0) {
                        sender.sendMessage(Util.formatMsg("&cID must be a number."));
                        return true;
                    }

                    BountyData bd = bm.getBounty(CWUtil.getInt(args[1]));
                    if (bd == null) {
                        sender.sendMessage(Util.formatMsg("&cNo bounty found with this ID."));
                        sender.sendMessage(Util.formatMsg("&cUse &4/bounty list &cto see all available bounties."));
                        return true;
                    }

                    sender.sendMessage(CWUtil.integrateColor("&8========= &4&lBounty information &8========="));
                    sender.sendMessage(CWUtil.integrateColor("&6Status&8: &5" + (bd.isCollected() ? "&4Collected" : "&aNot yet collected")));
                    sender.sendMessage(CWUtil.integrateColor("&6Creator&8: &5" + (bd.getTarget().equalsIgnoreCase(sender.getName()) ? "&aYou!" : bd.getTarget())));
                    sender.sendMessage(CWUtil.integrateColor("&6Target&8: &5" + (bd.getTarget().equalsIgnoreCase(sender.getName()) ? "&c&lYou!" : bd.getTarget())));
                    sender.sendMessage(CWUtil.integrateColor("&6Original reward&8: &5" + bd.getBounty()));
                    sender.sendMessage(CWUtil.integrateColor("&6Current reward&8: &5" + bm.getReward(bd) + " &8(&7What you get&8)"));
                    sender.sendMessage(CWUtil.integrateColor("&6Time since creation&8: &5" + CWUtil.getHourMinSecStr(System.currentTimeMillis() - bd.getTimeCreated())));

                    //Coords
                    if (bd.getHunters().containsKey(sender.getName())) {
                        String coords = (bd.getHunters().get(sender.getName()) ? bm.getLocation(bd) : "&7Not purchased");
                        sender.sendMessage(CWUtil.integrateColor("&6Coordinates&8: &5" + coords));
                    }

                    sender.sendMessage(CWUtil.integrateColor("&6Hunters&8: &5" + CWUtil.implode(bd.getHunters().keySet().toArray(new String[bd.getHunters().size()]), "&8, &5")));
                    return true;
                }

                //##########################################################################################################################
                //################################################## /bounty accept {ID} ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("accept") || args[0].equalsIgnoreCase("take") || args[0].equalsIgnoreCase("hunt") || args[0].equalsIgnoreCase("claim")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("Player command only."));
                        return true;
                    }
                    Player player = (Player) sender;

                    if (args.length < 2) {
                        player.sendMessage(Util.formatMsg("&cInvalid command usage. &4/" + label + " " + args[0] + " {ID}"));
                        return true;
                    }

                    if (CWUtil.getInt(args[1]) < 0) {
                        player.sendMessage(Util.formatMsg("&cID must be a number."));
                        return true;
                    }

                    BountyData bd = bm.getBounty(CWUtil.getInt(args[1]));
                    if (bd == null) {
                        player.sendMessage(Util.formatMsg("&cNo bounty found with this ID."));
                        player.sendMessage(Util.formatMsg("&cUse &4/bounty list &cto see all available bounties."));
                        return true;
                    }

                    if (bd.getHunters().containsKey(player.getName())) {
                        player.sendMessage(Util.formatMsg("&cYou've already accepted this bounty."));
                        return true;
                    }

                    int price = Math.round(bd.getBounty() / 10);

                    if (cwb.getEconomy().getBalance(player) < price) {
                        player.sendMessage(Util.formatMsg("&cYou don't have enough coins. You need &4" + price + " coins&c."));
                        return true;
                    }

                    if (args.length >= 3) {
                        //Confirmed
                        cwb.getEconomy().withdrawPlayer(player, price);

                        //TODO: Should prob be in bountymanager and add to team and all hunter stuff.
                        bd.addHunter(sender.getName(), false);
                        bm.setBounty(bd);

                        player.sendMessage(Util.formatMsg("&6Bounty accepted for &e" + price + " coins&6."));
                        player.sendMessage(Util.formatMsg("&6Now &4kill him &6to collect your reward!"));
                        if (cwb.getServer().getPlayer(bd.getTarget()) != null && cwb.getServer().getPlayer(bd.getTarget()).isOnline()) {
                            cwb.getServer().getPlayer(bd.getTarget()).sendMessage(Util.formatMsg("&c&lYou're being hunted by &4&l" + player.getName() + "&c&l!"));
                        }
                    } else {
                        //Unconfirmed
                        player.sendMessage(Util.formatMsg("&6You're about to accept a bounty on &5" + bd.getTarget()));
                        player.sendMessage(Util.formatMsg("&6You will have to pay &510% of bounty which is &e" + price + " coins&6."));
                        player.sendMessage(Util.formatMsg("&6You will get this money back when you collected the bounty."));
                        player.sendMessage(Util.formatMsg("&6If you don't kill him first you won't get it back though."));
                        player.sendMessage(Util.formatMsg("&6Use &5/" + label + " " + args[0] + " " + args[1] + " confirm/c &6to confirm this."));
                    }
                    return true;
                }

                //##########################################################################################################################
                //################################################## /bounty cancel {ID} ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("cancel")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("Player command only."));
                        return true;
                    }
                    Player player = (Player) sender;

                    if (args.length < 2) {
                        player.sendMessage(Util.formatMsg("&cInvalid command usage. &4/" + label + " " + args[0] + " {ID}"));
                        return true;
                    }

                    if (CWUtil.getInt(args[1]) < 0) {
                        player.sendMessage(Util.formatMsg("&cID must be a number."));
                        return true;
                    }

                    BountyData bd = bm.getBounty(CWUtil.getInt(args[1]));
                    if (bd == null) {
                        player.sendMessage(Util.formatMsg("&cNo bounty found with this ID."));
                        player.sendMessage(Util.formatMsg("&cUse &4/bounty status &cto see all your bounties."));
                        return true;
                    }

                    if (!bd.getHunters().containsKey(player.getName())) {
                        player.sendMessage(Util.formatMsg("&cYou haven't accepted this bounty."));
                        return true;
                    }

                    int refund = Math.round(bd.getBounty() / 2);
                    refund += bd.getCoordsUnlocked(player.getName()) ? 1000 : 0;

                    if (args.length >= 3) {
                        //Confirmed
                        //TODO: Should prob be in bountymanager and remove from team and all hunter stuff.
                        bd.removeHunter(sender.getName());
                        bm.setBounty(bd);

                        cwb.getEconomy().depositPlayer(player, refund);

                        player.sendMessage(Util.formatMsg("&6Bounty cancelled, you have been refunded &e" + refund + " coins&6."));
                        if (cwb.getServer().getPlayer(bd.getTarget()) != null && cwb.getServer().getPlayer(bd.getTarget()).isOnline()) {
                            cwb.getServer().getPlayer(bd.getTarget()).sendMessage(Util.formatMsg("&5" + player.getName() + " &6has stopped hunting you down!"));
                        }
                    } else {
                        //Unconfirmed
                        player.sendMessage(Util.formatMsg("&6You're about to cancel an accepted bounty."));
                        player.sendMessage(Util.formatMsg("&6You will only get 50% of the paid money back which is &e" + refund + " coins&6."));
                        player.sendMessage(Util.formatMsg("&6Use &5/" + label + " " + args[0] + " " + args[1] + " confirm/c &6to confirm this."));
                    }
                    return true;
                }

                //##########################################################################################################################
                //################################################# /bounty status [page] ##################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("status")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("Player command only."));
                        return true;
                    }
                    Player player = (Player) sender;

                    //Get all bounties which player is hunting.
                    Map<Integer, BountyData> bounties = new HashMap<Integer, BountyData>();
                    for (int ID : bounties.keySet()) {
                        if (bounties.get(ID).getHunters().containsKey(player.getName())) {
                            bounties.put(ID, bounties.get(ID));
                        }
                    }
                    int pages = Math.max(bounties.size() > 0 ? (int)Math.ceil(bounties.size()/12) : 1, 1);

                    int page = 1;
                    if (args.length >= 2) {
                        if (CWUtil.getInt(args[1]) > 1 && CWUtil.getInt(args[1]) <= pages) {
                            page = CWUtil.getInt(args[1]);
                        } else {
                            sender.sendMessage(Util.formatMsg("&cInvalid page number specified. Must be a number between 1 and " + pages));
                            return true;
                        }
                    }

                    sender.sendMessage(CWUtil.integrateColor("&8========= &4&lBounties you're hunting &7[&d" + page + "&8/&5" + pages + "&7] &8========="));
                    List<Integer> bountyIds = new ArrayList<Integer>();
                    bountyIds.addAll(bounties.keySet());
                    for (int i = (pages * 12) - 12; i < pages * 12; i++) {
                        BountyData bd = bounties.get(bountyIds.get(i));
                        if (bd != null) {
                            String time = CWUtil.getHourMinSecStr(bd.getTimeRemaining(), ChatColor.DARK_RED, ChatColor.RED);
                            sender.sendMessage(CWUtil.integrateColor("&8[&5" + bd.getID() + "&8] &6" + bd.getTarget() + " &8- &e₵" + bm.getReward(bd) + " &7- " + time));
                        }
                    }
                    sender.sendMessage(CWUtil.integrateColor("&8===== &4Use &c/" + label + " " + args[0] + " " + (page+1) + " &4for the next page &8====="));
                    return true;
                }

                //##########################################################################################################################
                //################################################### /bounty me [page] ####################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("me")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("Player command only."));
                        return true;
                    }
                    Player player = (Player) sender;

                    //Get all bounties with this player as target.
                    Map<Integer, BountyData> bounties = new HashMap<Integer, BountyData>();
                    for (int ID : bounties.keySet()) {
                        if (bounties.get(ID).getTarget().equalsIgnoreCase(player.getName())) {
                            bounties.put(ID, bounties.get(ID));
                        }
                    }
                    int pages = Math.max(bounties.size() > 0 ? (int)Math.ceil(bounties.size()/12) : 1, 1);

                    int page = 1;
                    if (args.length >= 2) {
                        if (CWUtil.getInt(args[1]) > 1 && CWUtil.getInt(args[1]) <= pages) {
                            page = CWUtil.getInt(args[1]);
                        } else {
                            sender.sendMessage(Util.formatMsg("&cInvalid page number specified. Must be a number between 1 and " + pages));
                            return true;
                        }
                    }

                    sender.sendMessage(CWUtil.integrateColor("&8========= &4&lBounties on you &7(&4&l" + bounties.size() + "&7) &7[&d" + page + "&8/&5" + pages + "&7] &8========="));
                    if (bounties.size() > 0) {
                        List<Integer> bountyIds = new ArrayList<Integer>();
                        bountyIds.addAll(bounties.keySet());
                        Set<String> huntersWithCoords = new HashSet<String>();
                        for (int i = (pages * 10) - 10; i < pages * 10; i++) {
                            BountyData bd = bounties.get(bountyIds.get(i));
                            if (bd != null) {
                                String time = CWUtil.getHourMinSecStr(bd.getTimeRemaining(), ChatColor.DARK_RED, ChatColor.RED);
                                sender.sendMessage(CWUtil.integrateColor("&8[&5" + bd.getID() + "&8] &6" + bd.getCreator() + " &8- &e₵" + bm.getReward(bd) + " &7- " + time
                                        + " &7- &c" + bd.getHunters().size() + " hunters"));

                                //Get a list of all hunters that have bought coords.
                                for (String hunter : bd.getHunters().keySet()) {
                                    if (bd.getHunters().get(hunter)) {
                                        huntersWithCoords.add(hunter);
                                    }
                                }
                            }
                        }

                        if (huntersWithCoords.size() > 0) {
                            if (cwb.getPlayerCfg().getProtection(player.getName()) > 0) {
                                sender.sendMessage(CWUtil.integrateColor("&6Nobody can see your coordinates for &a&l" + cwb.getPlayerCfg().getProtection(player.getName()) + " &6more days!"));
                            } else {
                                sender.sendMessage(CWUtil.integrateColor("&6The following hunters can see your location&8: &5" + CWUtil.implode(huntersWithCoords, "&8, &5")));
                            }
                        } else {
                            sender.sendMessage(CWUtil.integrateColor("&6Nobody has bought your coordinates."));
                        }
                        sender.sendMessage(CWUtil.integrateColor("&8===== &4Use &c/" + label + " " + args[0] + " " + (page + 1) + " &4for the next page &8====="));
                    } else {
                        sender.sendMessage(CWUtil.integrateColor("&a&lThere are no bounties on you!"));
                    }
                    return true;
                }

                //##########################################################################################################################
                //################################################ /bounty protect {days} ##################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("protect") || args[0].equalsIgnoreCase("protection") || args[0].equalsIgnoreCase("prot")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("Player command only."));
                        return true;
                    }
                    Player player = (Player) sender;

                    if (args.length < 2) {
                        player.sendMessage(Util.formatMsg("&cInvalid command usage. &4/" + label + " " + args[0] + " {days}"));
                        return true;
                    }

                    if (CWUtil.getInt(args[1]) <= 0) {
                        player.sendMessage(Util.formatMsg("&cDays must be a number above 0."));
                        return true;
                    }
                    int days = CWUtil.getInt(args[1]);
                    int price = days * 200;

                    if (cwb.getEconomy().getBalance(player) < price) {
                        player.sendMessage(Util.formatMsg("&cYou don't have enough coins. You need &4" + price + " coins&c."));
                        return true;
                    }

                    if (args.length >= 3) {
                        //Confirmed
                        cwb.getEconomy().withdrawPlayer(player, price);

                        cwb.getPlayerCfg().setProtection(player.getName(), cwb.getPlayerCfg().getProtection(player.getName()) + days);
                        player.sendMessage(Util.formatMsg("&6You have bought &a" + days + " &6days of protection!"));
                        player.sendMessage(Util.formatMsg("&6You are protected for &a" + cwb.getPlayerCfg().getProtection(player.getName()) + " &6more days."));
                    } else {
                        //Unconfirmed
                        player.sendMessage(Util.formatMsg("&6You're about to purchase &5" + days + " &6days of protection for &e₵" + price + "&6."));
                        player.sendMessage(Util.formatMsg("&6During these days bounty hunters can't see your location."));
                        player.sendMessage(Util.formatMsg("&6This can not be undone!"));
                        player.sendMessage(Util.formatMsg("&6Use &5/" + label + " " + args[0] + " " + args[1] + " confirm/c &6to confirm this."));
                    }
                    return true;
                }

                //##########################################################################################################################
                //################################################## /bounty coords {ID} ###################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("coords") || args[0].equalsIgnoreCase("locate") || args[0].equalsIgnoreCase("location") || args[0].equalsIgnoreCase("buycoords")
                        || args[0].equalsIgnoreCase("purchasecoords") || args[0].equalsIgnoreCase("buyloc") || args[0].equalsIgnoreCase("purchaseloc") || args[0].equalsIgnoreCase("loc")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Util.formatMsg("Player command only."));
                        return true;
                    }
                    Player player = (Player) sender;

                    if (args.length < 2) {
                        player.sendMessage(Util.formatMsg("&cInvalid command usage. &4/" + label + " " + args[0] + " {ID}"));
                        return true;
                    }

                    if (CWUtil.getInt(args[1]) < 0) {
                        player.sendMessage(Util.formatMsg("&cID must be a number."));
                        return true;
                    }

                    BountyData bd = bm.getBounty(CWUtil.getInt(args[1]));
                    if (bd == null) {
                        player.sendMessage(Util.formatMsg("&cNo bounty found with this ID."));
                        player.sendMessage(Util.formatMsg("&cUse &4/bounty status &cto see all your bounties."));
                        return true;
                    }

                    if (!bd.getHunters().containsKey(player.getName())) {
                        player.sendMessage(Util.formatMsg("&cYou haven't accepted this bounty."));
                        return true;
                    }

                    if (bd.getCoordsUnlocked(player.getName())) {
                        player.sendMessage(Util.formatMsg("&cCoordinates already purchased."));
                        return true;
                    }
                    int price = 2000;

                    if (cwb.getEconomy().getBalance(player) < price) {
                        player.sendMessage(Util.formatMsg("&cYou don't have enough coins. You need &4" + price + " coins&c."));
                        return true;
                    }

                    if (args.length >= 3) {
                        //Confirmed
                        cwb.getEconomy().withdrawPlayer(player, price);
                        bd.setCoordsUnlocked(player.getName(), true);

                        player.sendMessage(Util.formatMsg("&6Coordinates have been purchased for &e" + price + " coins&6."));
                        if (cwb.getServer().getPlayer(bd.getTarget()) != null && cwb.getServer().getPlayer(bd.getTarget()).isOnline()) {
                            Player target =  cwb.getServer().getPlayer(bd.getTarget());
                            if (cwb.getPlayerCfg().getProtection(bd.getTarget()) <= 0) {
                                target.sendMessage(Util.formatMsg("&5" + player.getName() + " &6can now locate you."));
                                target.sendMessage(Util.formatMsg("&6Use &5/bounty protect &6to hide your location for coins."));
                            } else {
                                target.sendMessage(Util.formatMsg("&5" + player.getName() + " &6has purchased your location."));
                                target.sendMessage(Util.formatMsg("&6However, you are protected for &5" + cwb.getPlayerCfg().getProtection(bd.getTarget()) + " &6more days."));
                            }
                        }
                    } else {
                        //Unconfirmed
                        player.sendMessage(Util.formatMsg("&6You're about to purchase &5" + bd.getTarget() + "'s &6coordinates. &e&l₵" + price));
                        player.sendMessage(Util.formatMsg("&6His location will update every time you check it."));
                        player.sendMessage(Util.formatMsg("&6However, if he's near his faction home it wont show the location."));
                        player.sendMessage(Util.formatMsg("&6He can also purchase protection which makes you unable to locate him."));
                        player.sendMessage(Util.formatMsg("&6And the location is random within 100 blocks radius of him."));
                        player.sendMessage(Util.formatMsg("&5" + bd.getTarget() + " &6has &5" + cwb.getPlayerCfg().getProtection(bd.getTarget()) + " &6days of protection currently."));
                        player.sendMessage(Util.formatMsg("&6Use &5/" + label + " " + args[0] + " " + args[1] + " confirm/c &6to confirm this."));
                    }
                    return true;
                }
            }

            sender.sendMessage(CWUtil.integrateColor("&8===== &4&lCommand Help &6/" + label + " &8====="));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " &8- &5Show this page."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " help &8- &5Show information how bounties work."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " set {player} {amount} &8- &5Create a new bounty."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " list [page] &8- &5List all bounties."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " accept {ID} &8- &5Accept a bounty and start hunting!"));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " cancel {ID} &8- &5Cancel a accepted bounty!"));
            sender.sendMessage(CWUtil.integrateColor("&8(&7You won't get your coins back!&8)"));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " status [page] &8- &5See the status of your accepted bounties."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " me [page] &8- &5See all bounties on yourself."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " protect {days} &8- &5Purchase protection per day!"));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " coords {ID} &8- &5Purchase coords of an active bounty!"));
            return true;
        }
        return false;
    }
}
