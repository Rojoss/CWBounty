package com.clashwars.cwbounty.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

import java.util.HashMap;
import java.util.Map;

public class PlayerCfg extends EasyConfig {

    public Map<String, Integer> protections = new HashMap<String, Integer>();

    public PlayerCfg(String fileName) {
        this.setFile(fileName);
    }


    public int getProtection(String playerName) {
        if (protections.containsKey(playerName)) {
            return protections.get(playerName);
        }
        return 0;
    }

    public void setProtection(String playerName, int days) {
        protections.put(playerName, days);
        save();
    }

    public Map<String, Integer> getProtections() {
        return protections;
    }

}
