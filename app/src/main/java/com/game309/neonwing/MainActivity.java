package com.game309.neonwing;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.game309.neonwing.game.NeonWingView;
import com.game309.neonwing.service.AdsGateway;
import com.game309.neonwing.service.BillingGateway;
import com.game309.neonwing.service.GameRepository;

public class MainActivity extends Activity {
    private NeonWingView gameView;
    private GameRepository repository;
    private AdsGateway adsGateway;
    private BillingGateway billingGateway;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        hideSystemUi();

        repository = new GameRepository(this);
        adsGateway = new AdsGateway(this);
        billingGateway = new BillingGateway(this, repository);
        gameView = new NeonWingView(this, repository, adsGateway, billingGateway);
        gameView.setScreenListener(screenName ->
                adsGateway.setHangarBannerVisible("HANGAR".equals(screenName) && !repository.hasRemovedAds()));

        FrameLayout root = new FrameLayout(this);
        root.addView(gameView, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        FrameLayout.LayoutParams bannerParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL);
        root.addView(adsGateway.createHangarBannerView(), bannerParams);
        setContentView(root);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUi();
        if (gameView != null) {
            gameView.resume();
        }
        if (adsGateway != null) {
            adsGateway.onResume();
            adsGateway.showAppOpenIfAvailable(repository != null && repository.hasRemovedAds());
        }
        if (billingGateway != null) {
            billingGateway.refreshPurchases();
        }
    }

    @Override
    protected void onPause() {
        if (adsGateway != null) {
            adsGateway.onPause();
        }
        if (gameView != null) {
            gameView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (adsGateway != null) {
            adsGateway.destroy();
        }
        if (billingGateway != null) {
            billingGateway.destroy();
        }
        super.onDestroy();
    }

    private void hideSystemUi() {
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }
}
