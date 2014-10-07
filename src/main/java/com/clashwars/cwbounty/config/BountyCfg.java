package com.clashwars.cwbounty.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BountyCfg extends EasyConfig {

    public Map<Integer, String> bounties = new HashMap<Integer, String>();

    public BountyCfg(String fileName) {
        this.setFile(fileName);
    }

    public String getBounty(int id) {
        if (bounties.containsKey(id)) {
            return bounties.get(id);
        }
        return "";
    }

    public void setBounty(int id, String data) {
        bounties.put(id, data);
        save();
    }

    public Map<Integer, String> getBounties() {
        return bounties;
    }
}
