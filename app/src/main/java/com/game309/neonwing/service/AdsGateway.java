package com.game309.neonwing.service;

import android.app.Activity;
import android.os.SystemClock;
import android.view.View;

import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.appopen.AppOpenAd;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;

public class AdsGateway {
    private static final long APP_OPEN_EXPIRY_MS = 4L * 60L * 60L * 1000L;
    private static final long APP_OPEN_COOLDOWN_MS = 90L * 1000L;
    private static final long INTERSTITIAL_COOLDOWN_MS = 120L * 1000L;
    private static final float MIN_INTERSTITIAL_RUN_TIME = 30f;

    public enum RewardPlacement {
        DOUBLE_REWARD,
        REVIVE,
        RESTORE_DRONE,
        FREE_CHEST,
        PREMIUM_TRIAL,
        START_BUFF
    }

    public interface RewardCallback {
        void onRewardEarned();

        void onRewardUnavailable();
    }

    private final Activity activity;

    private AdView hangarBanner;
    private AppOpenAd appOpenAd;
    private RewardedAd rewardedAd;
    private RewardedInterstitialAd rewardedInterstitialAd;
    private InterstitialAd gameOverInterstitialAd;

    private boolean appOpenLoading;
    private boolean rewardedLoading;
    private boolean rewardedInterstitialLoading;
    private boolean interstitialLoading;
    private boolean showingFullScreenAd;

    private long appOpenLoadedAt;
    private long lastAppOpenShownAt;
    private long lastInterstitialShownAt;

    public AdsGateway(Activity activity) {
        this.activity = activity;
        MobileAds.initialize(activity, initializationStatus -> {
            loadAppOpen();
            loadRewarded();
            loadRewardedInterstitial();
            loadGameOverInterstitial();
        });
    }

    public View createHangarBannerView() {
        if (hangarBanner != null) {
            return hangarBanner;
        }
        hangarBanner = new AdView(activity);
        hangarBanner.setAdSize(AdSize.BANNER);
        hangarBanner.setAdUnitId(AdMobIds.BANNER_HANGAR);
        hangarBanner.setVisibility(View.GONE);
        hangarBanner.loadAd(new AdRequest.Builder().build());
        return hangarBanner;
    }

    public void setHangarBannerVisible(boolean visible) {
        if (hangarBanner == null) {
            return;
        }
        activity.runOnUiThread(() -> hangarBanner.setVisibility(visible ? View.VISIBLE : View.GONE));
    }

    public void onResume() {
        if (hangarBanner != null) {
            hangarBanner.resume();
        }
    }

    public void onPause() {
        if (hangarBanner != null) {
            hangarBanner.pause();
        }
    }

    public void destroy() {
        if (hangarBanner != null) {
            hangarBanner.destroy();
            hangarBanner = null;
        }
    }

    public boolean isRewardedReady(RewardPlacement placement) {
        if (placement == RewardPlacement.REVIVE) {
            return rewardedInterstitialAd != null || rewardedAd != null;
        }
        return rewardedAd != null;
    }

    public void showRewarded(RewardPlacement placement, RewardCallback callback) {
        activity.runOnUiThread(() -> {
            if (placement == RewardPlacement.REVIVE && rewardedInterstitialAd != null) {
                showRewardedInterstitial(callback);
                return;
            }
            if (rewardedAd != null) {
                showRewardedMain(callback);
                return;
            }
            callback.onRewardUnavailable();
            loadRewarded();
            if (placement == RewardPlacement.REVIVE) {
                loadRewardedInterstitial();
            }
        });
    }

    public void showGameOverInterstitial(boolean adsRemoved, float runTime) {
        if (adsRemoved || runTime < MIN_INTERSTITIAL_RUN_TIME) {
            return;
        }
        activity.runOnUiThread(() -> {
            long now = SystemClock.elapsedRealtime();
            if (showingFullScreenAd || now - lastInterstitialShownAt < INTERSTITIAL_COOLDOWN_MS) {
                return;
            }
            if (gameOverInterstitialAd == null) {
                loadGameOverInterstitial();
                return;
            }
            InterstitialAd ad = gameOverInterstitialAd;
            gameOverInterstitialAd = null;
            showingFullScreenAd = true;
            lastInterstitialShownAt = now;
            ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    showingFullScreenAd = false;
                    loadGameOverInterstitial();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    showingFullScreenAd = false;
                    loadGameOverInterstitial();
                }
            });
            ad.show(activity);
        });
    }

    public void showAppOpenIfAvailable(boolean adsRemoved) {
        if (adsRemoved) {
            return;
        }
        activity.runOnUiThread(() -> {
            long now = SystemClock.elapsedRealtime();
            if (showingFullScreenAd || now - lastAppOpenShownAt < APP_OPEN_COOLDOWN_MS) {
                return;
            }
            if (!isAppOpenFresh()) {
                loadAppOpen();
                return;
            }
            AppOpenAd ad = appOpenAd;
            appOpenAd = null;
            showingFullScreenAd = true;
            lastAppOpenShownAt = now;
            ad.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    showingFullScreenAd = false;
                    loadAppOpen();
                }

                @Override
                public void onAdFailedToShowFullScreenContent(AdError adError) {
                    showingFullScreenAd = false;
                    loadAppOpen();
                }
            });
            ad.show(activity);
        });
    }

    private void showRewardedMain(RewardCallback callback) {
        RewardedAd ad = rewardedAd;
        rewardedAd = null;
        showingFullScreenAd = true;
        final boolean[] earned = {false};
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                showingFullScreenAd = false;
                loadRewarded();
                if (!earned[0]) {
                    callback.onRewardUnavailable();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                showingFullScreenAd = false;
                loadRewarded();
                callback.onRewardUnavailable();
            }
        });
        ad.show(activity, rewardItem -> {
            earned[0] = true;
            callback.onRewardEarned();
        });
    }

    private void showRewardedInterstitial(RewardCallback callback) {
        RewardedInterstitialAd ad = rewardedInterstitialAd;
        rewardedInterstitialAd = null;
        showingFullScreenAd = true;
        final boolean[] earned = {false};
        ad.setFullScreenContentCallback(new FullScreenContentCallback() {
            @Override
            public void onAdDismissedFullScreenContent() {
                showingFullScreenAd = false;
                loadRewardedInterstitial();
                if (!earned[0]) {
                    callback.onRewardUnavailable();
                }
            }

            @Override
            public void onAdFailedToShowFullScreenContent(AdError adError) {
                showingFullScreenAd = false;
                loadRewardedInterstitial();
                callback.onRewardUnavailable();
            }
        });
        ad.show(activity, rewardItem -> {
            earned[0] = true;
            callback.onRewardEarned();
        });
    }

    private void loadAppOpen() {
        if (appOpenLoading || isAppOpenFresh()) {
            return;
        }
        appOpenLoading = true;
        AppOpenAd.load(activity, AdMobIds.APP_OPEN, new AdRequest.Builder().build(), new AppOpenAd.AppOpenAdLoadCallback() {
            @Override
            public void onAdLoaded(AppOpenAd ad) {
                appOpenAd = ad;
                appOpenLoadedAt = SystemClock.elapsedRealtime();
                appOpenLoading = false;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                appOpenAd = null;
                appOpenLoading = false;
            }
        });
    }

    private void loadRewarded() {
        if (rewardedLoading || rewardedAd != null) {
            return;
        }
        rewardedLoading = true;
        RewardedAd.load(activity, AdMobIds.REWARDED_MAIN, new AdRequest.Builder().build(), new RewardedAdLoadCallback() {
            @Override
            public void onAdLoaded(RewardedAd ad) {
                rewardedAd = ad;
                rewardedLoading = false;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                rewardedAd = null;
                rewardedLoading = false;
            }
        });
    }

    private void loadRewardedInterstitial() {
        if (rewardedInterstitialLoading || rewardedInterstitialAd != null) {
            return;
        }
        rewardedInterstitialLoading = true;
        RewardedInterstitialAd.load(
                activity,
                AdMobIds.REWARDED_INTERSTITIAL_CONTINUE,
                new AdRequest.Builder().build(),
                new RewardedInterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(RewardedInterstitialAd ad) {
                        rewardedInterstitialAd = ad;
                        rewardedInterstitialLoading = false;
                    }

                    @Override
                    public void onAdFailedToLoad(LoadAdError loadAdError) {
                        rewardedInterstitialAd = null;
                        rewardedInterstitialLoading = false;
                    }
                });
    }

    private void loadGameOverInterstitial() {
        if (interstitialLoading || gameOverInterstitialAd != null) {
            return;
        }
        interstitialLoading = true;
        InterstitialAd.load(activity, AdMobIds.INTERSTITIAL_GAME_OVER, new AdRequest.Builder().build(), new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(InterstitialAd ad) {
                gameOverInterstitialAd = ad;
                interstitialLoading = false;
            }

            @Override
            public void onAdFailedToLoad(LoadAdError loadAdError) {
                gameOverInterstitialAd = null;
                interstitialLoading = false;
            }
        });
    }

    private boolean isAppOpenFresh() {
        return appOpenAd != null && SystemClock.elapsedRealtime() - appOpenLoadedAt < APP_OPEN_EXPIRY_MS;
    }
}
