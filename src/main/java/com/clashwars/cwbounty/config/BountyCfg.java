package com.clashwars.cwbounty.config;

import com.clashwars.cwcore.config.internal.EasyConfig;
import com.clashwars.cwcore.utils.CWUtil;
import org.bukkit.Bukkit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BountyCfg extends EasyConfig {

    public Map<String, String> bounties = new HashMap<String, String>();

    public BountyCfg(String fileName) {
        this.setFile(fileName);
    }

    public String getBounty(int id) {
        if (bounties.containsKey("" + id)) {
            return bounties.get("" + id);
        }
        return "";
    }

    public void setBounty(int id, String data) {
        bounties.put("" + id, data);
        save();
    }

    public Map<Integer, String> getBounties() {
        Map<Integer, String> intBounties = new HashMap<Integer, String>();
        for (String bountyID : bounties.keySet()) {
            intBounties.put(CWUtil.getInt(bountyID), bounties.get(bountyID));
        }
        return intBounties;
    }
}
