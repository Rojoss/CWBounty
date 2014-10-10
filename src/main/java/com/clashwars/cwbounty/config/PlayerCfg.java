package com.clashwars.cwbounty.config;

import com.clashwars.cwbounty.CWBounty;
import com.clashwars.cwcore.config.internal.EasyConfig;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCfg extends EasyConfig {

    public Map<String, Long> protections = new HashMap<String, Long>();

    public PlayerCfg(String fileName) {
        this.setFile(fileName);
    }


    public long getProtection(UUID uuid) {
        if (protections.containsKey(uuid.toString())) {
            return protections.get(uuid.toString());
        }
        return -1;
    }

    public void setProtection(UUID uuid, long timestamp) {
        protections.put(uuid.toString(), timestamp);
        save();
    }



    public long getProtectionTimeRemaining(UUID uuid) {
        return Math.max((getProtection(uuid) - System.currentTimeMillis()), 0);
    }

    public boolean hasProtection(UUID uuid) {
        return getProtectionTimeRemaining(uuid) > 0;
    }
}
