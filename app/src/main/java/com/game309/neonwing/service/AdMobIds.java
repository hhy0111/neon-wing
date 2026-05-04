package com.game309.neonwing.service;

import com.game309.neonwing.BuildConfig;

public final class AdMobIds {
    public static final String APP_ID = "ca-app-pub-4402708884038037~5663016098";

    public static final String APP_OPEN = BuildConfig.DEBUG
            ? "ca-app-pub-3940256099942544/9257395921"
            : "ca-app-pub-4402708884038037/6356001880";

    public static final String BANNER_HANGAR = BuildConfig.DEBUG
            ? "ca-app-pub-3940256099942544/6300978111"
            : "ca-app-pub-4402708884038037/7473642430";

    public static final String INTERSTITIAL_GAME_OVER = BuildConfig.DEBUG
            ? "ca-app-pub-3940256099942544/1033173712"
            : "ca-app-pub-4402708884038037/2412887441";

    public static final String REWARDED_MAIN = BuildConfig.DEBUG
            ? "ca-app-pub-3940256099942544/5224354917"
            : "ca-app-pub-4402708884038037/5524249442";

    public static final String REWARDED_INTERSTITIAL_CONTINUE = BuildConfig.DEBUG
            ? "ca-app-pub-3940256099942544/5354046379"
            : "ca-app-pub-4402708884038037/9156372699";

    private AdMobIds() {
    }
}
