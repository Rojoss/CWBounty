package com.clashwars.cwbounty.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

import java.util.HashMap;
import java.util.Map;

public class PlayerCfg extends EasyConfig {

    public Map<String, Long> protections = new HashMap<String, Long>();

    public PlayerCfg(String fileName) {
        this.setFile(fileName);
    }


    public long getProtection(String playerName) {
        if (protections.containsKey(playerName)) {
            return protections.get(playerName);
        }
        return -1;
    }

    public void setProtection(String playerName, long timestamp) {
        protections.put(playerName, timestamp);
        save();
    }

    public Map<String, Long> getProtections() {
        return protections;
    }



    public long getProtectionTimeRemaining(String playerName) {
        return Math.max((getProtection(playerName) - System.currentTimeMillis()), 0);
    }

    public int getProtectionDays(String playerName) {
        return Math.max((int)Math.ceil(getProtectionTimeRemaining(playerName) / 86400), 0);
    }

    public boolean hasProtection(String playerName) {
        return getProtectionTimeRemaining(playerName) > 0;
    }
}
