package com.clashwars.cwbounty.commands;

import com.clashwars.cwbounty.BountyManager;
import com.clashwars.cwbounty.CWBounty;
import com.clashwars.cwbounty.Util;
import com.clashwars.cwbounty.config.BountyData;
import com.clashwars.cwbounty.config.PluginCfg;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class Commands {
    private CWBounty cwb;
    private BountyManager bm;
    private PluginCfg cfg;

    public Commands(CWBounty cwb) {
        this.cwb = cwb;
        this.bm = cwb.getBM();
        this.cfg = cwb.getCfg();
    }


    @SuppressWarnings("deprecation")
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (label.equalsIgnoreCase("bounty") || label.equalsIgnoreCase("bounties")) {
            if (args.length >= 1) {
                //##########################################################################################################################
                //###################################################### /bounty help ######################################################
                //##########################################################################################################################
                if (args[0].equalsIgnoreCase("help")) {
                    sender.sendMessage(CWUtil.integrateColor("&8=========== &4&lBounty Help &8============"));
                    sender.sendMessage(CWUtil.integrateColor("&6You can place a bounty bounty on any player. "
                            + "When this player is killed by another player he will get the bounty. "
                            + "Every hour the bounty reward will decrease by " + cfg.PRICE__PERCENTAGE_REDUCED_PER_DAY + "% of the original value. "
                            + "A bounty has to be collected within 7 days else it will expire. "
                            + "When a bounty is expired the creator will get " + cfg.PRICE__EXPIRE_REFUND_PERCENTAGE + "% of the original bounty value. "
                            + "Before you kill a bounty you have to accept it else it wont count. "
                            + "To accept a bounty you first have to pay " + cfg.PRICE__ACCEPT_DEPOSIT_PERCENTAGE + "% of the reward. "
                            + "This " + cfg.PRICE__ACCEPT_DEPOSIT_PERCENTAGE + "% will be refunded when you collect the bounty and you will of course also get the bounty. "
                            + "It's also possible to purchase coordinates as a hunter. "
                            + "These coords are a random location within " + cfg.RANDOM_COORDS_RADIUS + " blocks of the target and wont show up if the target is near the faction home. "
                            + "If you're the one being hunted you can also purchase protection per day which will hide your location from the hunters. "
                            + "&7All commands work using ID's which can be found in the list."
                    ));
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
                    if (target.getName().equalsIgnoreCase(player.getName())) {
                        player.sendMessage(Util.formatMsg("&cCan't place a bounty on yourself..."));
                        return true;
                    }

                    if (CWUtil.getInt(args[2]) < cfg.PRICE__CREATE_MIN_REQUIRED) {
                        player.sendMessage(Util.formatMsg("&cThe bounty has to be at least &4250 coins&c."));
                        return true;
                    }
                    int value = CWUtil.getInt(args[2]);

                    if (cwb.getEconomy().getBalance(player) < value) {
                        player.sendMessage(Util.formatMsg("&cYou don't have enough coins. You need &4" + value + " coins&c."));
                        return true;
                    }

                    cwb.getEconomy().withdrawPlayer(player, value);
                    int ID = cwb.getBM().createBounty(player.getUniqueId(), target.getUniqueId(), value);
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
                    int pages = Math.max(bounties.size() > 0 ? (int)Math.ceil(bounties.size()/cfg.RESULTS_PER_PAGE) : 1, 1);

                    int page = 1;
                    if (args.length >= 2) {
                        if (CWUtil.getInt(args[1]) >= 1 && CWUtil.getInt(args[1]) <= pages) {
                            page = CWUtil.getInt(args[1]);
                        } else {
                            sender.sendMessage(Util.formatMsg("&cInvalid page number specified. Must be a number between 1 and " + pages));
                            return true;
                        }
                    }
                    sender.sendMessage(CWUtil.integrateColor("&8========= &4&lListing all bounties &7[&d" + page + "&8/&5" + pages + "&7] &8========="));
                    if (bounties.size() > 0) {
                        List<Integer> bountyIds = new ArrayList<Integer>();
                        bountyIds.addAll(bounties.keySet());
                        for (int i = (pages * cfg.RESULTS_PER_PAGE) - cfg.RESULTS_PER_PAGE; i < pages * cfg.RESULTS_PER_PAGE; i++) {
                            if (i >= bountyIds.size()) {
                                continue;
                            }
                            BountyData bd = bounties.get(bountyIds.get(i));
                            if (bd != null) {
                                if (bm.expireBounty(bd)) {
                                    continue;
                                }
                                sender.sendMessage(CWUtil.integrateColor("&8[&5" + bd.getID() + "&8] &6" + bd.getTarget() + " &8- &e" + bm.getReward(bd) + "&8/&7" + bd.getBounty()));
                            }
                        }
                        sender.sendMessage(CWUtil.integrateColor("&8===== &4Use &c/" + label + " list " + (page + 1) + " &4for the next page &8====="));
                    } else {
                        sender.sendMessage(CWUtil.integrateColor("&cThere are no bounties right now."));
                    }
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

                    if (bm.expireBounty(bd)) {
                        sender.sendMessage(Util.formatMsg("&cThis bounty just expired."));
                        return true;
                    }

                    sender.sendMessage(CWUtil.integrateColor("&8========= &4&lBounty information &8========="));
                    sender.sendMessage(CWUtil.integrateColor("&6Creator&8: &5" + (bd.getCreator().equalsIgnoreCase(sender.getName()) ? "&aYou!" : bd.getCreator())));
                    sender.sendMessage(CWUtil.integrateColor("&6Target&8: &5" + (bd.getTarget().equalsIgnoreCase(sender.getName()) ? "&c&lYou!" : bd.getTarget())));
                    sender.sendMessage(CWUtil.integrateColor("&6Original reward&8: &5" + bd.getBounty()));
                    sender.sendMessage(CWUtil.integrateColor("&6Current reward&8: &5" + bm.getReward(bd) + " &8(&7What you get&8)"));
                    sender.sendMessage(CWUtil.integrateColor("&6Time remaining&8: &5" + CWUtil.formatTime(bd.getTimeRemaining(), "&5%D&dd &5%H&8:&5%M&8:&5%S")));

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

                    if (bm.expireBounty(bd)) {
                        player.sendMessage(Util.formatMsg("&cThis bounty just expired."));
                        return true;
                    }

                    if (bd.getTarget().equalsIgnoreCase(player.getName()) || bd.getCreator().equalsIgnoreCase(player.getName())) {
                        player.sendMessage(Util.formatMsg("&cCan't accept your own bounty..."));
                        return true;
                    }

                    if (bd.getHunters().containsKey(player.getName())) {
                        player.sendMessage(Util.formatMsg("&cYou've already accepted this bounty."));
                        return true;
                    }

                    int price = (int)Math.round(bd.getBounty() / 100 * cfg.PRICE__ACCEPT_DEPOSIT_PERCENTAGE);

                    if (cwb.getEconomy().getBalance(player) < price) {
                        player.sendMessage(Util.formatMsg("&cYou don't have enough coins. You need &4" + price + " coins&c."));
                        return true;
                    }

                    if (args.length >= 3) {
                        //Confirmed
                        cwb.getEconomy().withdrawPlayer(player, price);

                        bm.acceptBounty(player.getUniqueId(), bd);

                        player.sendMessage(Util.formatMsg("&6Bounty accepted for &e" + price + " coins&6."));
                        player.sendMessage(Util.formatMsg("&6Now &4kill him &6to collect your reward!"));
                        if (cwb.getServer().getPlayer(bd.getTarget()) != null && cwb.getServer().getPlayer(bd.getTarget()).isOnline()) {
                            cwb.getServer().getPlayer(bd.getTarget()).sendMessage(Util.formatMsg("&c&lYou're being hunted by &4&l" + player.getName() + "&c&l!"));
                        }
                    } else {
                        //Unconfirmed
                        player.sendMessage(Util.formatMsg("&6You're about to accept a bounty on &5" + bd.getTarget() + " "
                                + "&6You will have to pay &510% of bounty which is &e" + price + " coins&6. "
                                + "You will get this money back when you collected the bounty. "
                                + "If you don't kill him first you won't get it back though."));
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

                    if (bm.expireBounty(bd)) {
                        player.sendMessage(Util.formatMsg("&cThis bounty just expired."));
                        return true;
                    }

                    if (!bd.getHunters().containsKey(player.getName())) {
                        player.sendMessage(Util.formatMsg("&cYou haven't accepted this bounty."));
                        return true;
                    }

                    int refund = (int)Math.round(bd.getBounty() / 100 * cfg.PRICE__CANCEL_REFUND_PERCENTAGE);
                    refund += bd.getCoordsUnlocked(player.getUniqueId()) ? cfg.PRICE__COORDS : 0;

                    if (args.length >= 3) {
                        //Confirmed
                        bm.cancelBounty(player.getUniqueId(), bd);

                        cwb.getEconomy().depositPlayer(player, refund);

                        player.sendMessage(Util.formatMsg("&6Bounty cancelled, you have been refunded &e" + refund + " coins&6."));
                        if (cwb.getServer().getPlayer(bd.getTarget()) != null && cwb.getServer().getPlayer(bd.getTarget()).isOnline()) {
                            cwb.getServer().getPlayer(bd.getTarget()).sendMessage(Util.formatMsg("&5" + player.getName() + " &6has stopped hunting you down!"));
                        }
                    } else {
                        //Unconfirmed
                        player.sendMessage(Util.formatMsg("&6You're about to cancel an accepted bounty. "
                                + "You will only get &550% &6of the paid coins back which is &e" + refund + " coins&6."));
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
                    for (int ID : bm.getBounties().keySet()) {
                        if (bm.getBounties().get(ID).getHunters().containsKey(player.getName())) {
                            bounties.put(ID, bm.getBounties().get(ID));
                        }
                    }
                    int pages = Math.max(bounties.size() > 0 ? (int)Math.ceil(bounties.size()/cfg.RESULTS_PER_PAGE) : 1, 1);

                    int page = 1;
                    if (args.length >= 2) {
                        if (CWUtil.getInt(args[1]) >= 1 && CWUtil.getInt(args[1]) <= pages) {
                            page = CWUtil.getInt(args[1]);
                        } else {
                            sender.sendMessage(Util.formatMsg("&cInvalid page number specified. Must be a number between 1 and " + pages));
                            return true;
                        }
                    }

                    sender.sendMessage(CWUtil.integrateColor("&8========= &4&lBounties you're hunting &7[&d" + page + "&8/&5" + pages + "&7] &8========="));
                    if (bounties.size() > 0) {
                        List<Integer> bountyIds = new ArrayList<Integer>();
                        bountyIds.addAll(bounties.keySet());
                        for (int i = (pages * cfg.RESULTS_PER_PAGE) - cfg.RESULTS_PER_PAGE; i < pages * cfg.RESULTS_PER_PAGE; i++) {
                            if (i >= bountyIds.size()) {
                                continue;
                            }
                            BountyData bd = bounties.get(bountyIds.get(i));
                            if (bd != null) {
                                if (bm.expireBounty(bd)) {
                                    continue;
                                }
                                String time = CWUtil.formatTime(bd.getTimeRemaining(), "&4%D&cd &4%H&8:&4%M&8:&4%S");
                                Player target = cwb.getServer().getPlayer(bd.getTarget());
                                sender.sendMessage(CWUtil.integrateColor("&8[&5" + bd.getID() + "&8] &6" + target == null ? "&c" + bd.getTarget() : "&a" + bd.getTarget()
                                        + " &8- &e₵" + bm.getReward(bd) + " &7- " + time + " &7- &5" + (bd.getHunters().get(player.getName()) ? bm.getLocation(bd) : "&7Not purchased")));
                            }
                        }
                        sender.sendMessage(CWUtil.integrateColor("&8===== &4Use &c/" + label + " " + args[0] + " " + (page + 1) + " &4for the next page &8====="));
                    } else {
                        sender.sendMessage(CWUtil.integrateColor("&6You haven't accepted any bounties yet."));
                        sender.sendMessage(CWUtil.integrateColor("&6Look at the bounty list with &5/bounty list."));
                        sender.sendMessage(CWUtil.integrateColor("&6And then accept one by doing &5/bounty accept {ID}."));
                        sender.sendMessage(CWUtil.integrateColor("&6The ID can be found in the brackets on the left of the list."));
                    }
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
                    for (int ID : bm.getBounties().keySet()) {
                        if (bm.getBounties().get(ID).getTarget().equalsIgnoreCase(player.getName())) {
                            bounties.put(ID, bm.getBounties().get(ID));
                        }
                    }
                    int resultsPerPage = cfg.RESULTS_PER_PAGE - 2;
                    int pages = Math.max(bounties.size() > 0 ? (int)Math.ceil(bounties.size()/resultsPerPage) : 1, 1);

                    int page = 1;
                    if (args.length >= 2) {
                        if (CWUtil.getInt(args[1]) >= 1 && CWUtil.getInt(args[1]) <= pages) {
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
                        for (int i = (pages * resultsPerPage) - resultsPerPage; i < pages * resultsPerPage; i++) {
                            if (i >= bountyIds.size()) {
                                continue;
                            }
                            BountyData bd = bounties.get(bountyIds.get(i));
                            if (bd != null) {
                                if (bm.expireBounty(bd)) {
                                    continue;
                                }
                                String time = CWUtil.formatTime(bd.getTimeRemaining(), "&4%D&cd &4%H&8:&4%M&8:&4%S");
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
                            if (cwb.getPlayerCfg().hasProtection(player.getUniqueId())) {
                                sender.sendMessage(CWUtil.integrateColor("&6Nobody can see your coordinates for &a"
                                        + CWUtil.formatTime(cwb.getPlayerCfg().getProtectionTimeRemaining(player.getUniqueId()), "&5%D&dd &5%H&8:&5%M") + "&l!"));
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
                    double price = days * cfg.PRICE__PROTECTION_PER_DAY;

                    if (cwb.getEconomy().getBalance(player) < price) {
                        player.sendMessage(Util.formatMsg("&cYou don't have enough coins. You need &4" + price + " coins&c."));
                        return true;
                    }

                    if (args.length >= 3) {
                        //Confirmed
                        cwb.getEconomy().withdrawPlayer(player, price);

                        long protTime = cwb.getPlayerCfg().getProtection(player.getUniqueId());
                        if (protTime < System.currentTimeMillis()) {
                            protTime = System.currentTimeMillis();
                        }

                        cwb.getPlayerCfg().setProtection(player.getUniqueId(), protTime + (days * 86400000));
                        player.sendMessage(Util.formatMsg("&6You have bought &a" + days + " &6days of protection!"));
                        player.sendMessage(Util.formatMsg("&6You are protected for &a"
                                + CWUtil.formatTime(cwb.getPlayerCfg().getProtectionTimeRemaining(player.getUniqueId()), "&5%D&dd &5%H&8:&5%M") + "&6."));
                    } else {
                        //Unconfirmed
                        player.sendMessage(Util.formatMsg("&6You're about to purchase &5" + days + " &6days of protection for &e₵" + price + "&6. "
                                + "During these days bounty hunters can't see your location. "
                                + "This can not be undone!"));
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

                    if (bm.expireBounty(bd)) {
                        sender.sendMessage(Util.formatMsg("&cThis bounty just expired."));
                        return true;
                    }

                    if (!bd.getHunters().containsKey(player.getName())) {
                        player.sendMessage(Util.formatMsg("&cYou haven't accepted this bounty."));
                        return true;
                    }

                    if (bd.getCoordsUnlocked(player.getUniqueId())) {
                        player.sendMessage(Util.formatMsg("&cCoordinates already purchased."));
                        return true;
                    }

                    if (cwb.getEconomy().getBalance(player) < cfg.PRICE__COORDS) {
                        player.sendMessage(Util.formatMsg("&cYou don't have enough coins. You need &4" + cfg.PRICE__COORDS + " coins&c."));
                        return true;
                    }

                    if (args.length >= 3) {
                        //Confirmed
                        cwb.getEconomy().withdrawPlayer(player, cfg.PRICE__COORDS);
                        bd.setCoordsUnlocked(player.getUniqueId(), true);
                        bm.setBounty(bd);

                        player.sendMessage(Util.formatMsg("&6Coordinates have been purchased for &e" + cfg.PRICE__COORDS + " coins&6."));
                        if (cwb.getServer().getPlayer(bd.getTarget()) != null && cwb.getServer().getPlayer(bd.getTarget()).isOnline()) {
                            Player target = cwb.getServer().getPlayer(bd.getTarget());
                            if (!cwb.getPlayerCfg().hasProtection(target.getUniqueId())) {
                                target.sendMessage(Util.formatMsg("&5" + player.getName() + " &6can now locate you."));
                                target.sendMessage(Util.formatMsg("&6Use &5/bounty protect &6to hide your location for coins."));
                            } else {
                                target.sendMessage(Util.formatMsg("&5" + player.getName() + " &6has purchased your location."));
                                target.sendMessage(Util.formatMsg("&6However, you are protected for &5"
                                      + CWUtil.formatTime(cwb.getPlayerCfg().getProtection(target.getUniqueId()), "&5%D&dd &5%H&8:&5%M") + " &6."));
                            }
                        }
                    } else {
                        //Unconfirmed
                        player.sendMessage(Util.formatMsg("&6You're about to purchase &5" + bd.getTarget() + "'s &6coordinates for &e₵" + cfg.PRICE__COORDS
                                + " &6His location will update every time you check it. "
                                + "However, if he's near his faction home it wont show the location. "
                                + "He can also purchase protection which makes you unable to locate him. "
                                + "And the location is random within 100 blocks radius of him."));
                        player.sendMessage(Util.formatMsg("&5" + bd.getTarget() + " &6has &5"
                                + CWUtil.formatTime(cwb.getPlayerCfg().getProtectionTimeRemaining(cwb.getServer().getOfflinePlayer(bd.getTarget()).getUniqueId()), "&5%D&dd &5%H&8:&5%M") + " &6protection currently."));
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
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " status [page] &8- &5See status of accepted bounties."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " me [page] &8- &5See all bounties on yourself."));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " protect {days} &8- &5Purchase protection per day!"));
            sender.sendMessage(CWUtil.integrateColor("&6/" + label + " coords {ID} &8- &5Purchase coords of an active bounty!"));
            return true;
        }
        return false;
    }
}
