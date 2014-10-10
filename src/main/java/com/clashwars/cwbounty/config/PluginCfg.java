package com.clashwars.cwbounty.config;

import com.clashwars.cwcore.config.internal.EasyConfig;

public class PluginCfg extends EasyConfig {

    public int RESULTS_PER_PAGE = 12;
    public int RANDOM_COORDS_RADIUS = 100;
    public int FACTION_HOME_RADIUS = 200;

    public double PRICE__CREATE_MIN_REQUIRED = 250;
    public double PRICE__ACCEPT_DEPOSIT_PERCENTAGE = 10;
    public double PRICE__CANCEL_REFUND_PERCENTAGE = 50;
    public double PRICE__EXPIRE_REFUND_PERCENTAGE = 50;
    public double PRICE__COORDS = 2000;
    public double PRICE__PROTECTION_PER_DAY = 200;
    public double PRICE__PERCENTAGE_REDUCED_PER_DAY = 0.005f;

    public PluginCfg(String fileName) {
        this.setFile(fileName);
    }
}
