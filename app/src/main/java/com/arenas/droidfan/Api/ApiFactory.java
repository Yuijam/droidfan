package com.arenas.droidfan.api;

/**
 * @author mcxiaoke
 * @version 1.0 2012-2-27 上午11:42:44
 */
public final class ApiFactory {
    private static final int TWITTER = 0;
    private static final int FANFOU = 1;

    public static Api getDefaultApi() {
        return new FanFouApi();
    }

    public static ApiParser getDefaultParser() {
        return new FanFouParser();
    }

}
