package com.game309.neonwing.service;

import android.app.Activity;

public class BillingGateway {
    public interface PurchaseCallback {
        void onPurchased(String productId);

        void onPurchaseFailed(String productId);
    }

    private final Activity activity;
    private final GameRepository repository;

    public BillingGateway(Activity activity, GameRepository repository) {
        this.activity = activity;
        this.repository = repository;
    }

    public void purchase(String productId, PurchaseCallback callback) {
        activity.runOnUiThread(() -> {
            repository.grantPurchase(productId);
            callback.onPurchased(productId);
        });
    }

    public String priceLabel(String productId) {
        switch (productId) {
            case ProductIds.REMOVE_ADS:
                return "$2.99";
            case ProductIds.STARTER_PACK:
                return "$1.99";
            case ProductIds.PREMIUM_SHIP_ASTRA:
                return "$4.99";
            case ProductIds.MONTHLY_SUPPLY_PASS:
                return "$3.99";
            case ProductIds.GEM_PACK_SMALL:
                return "$0.99";
            default:
                return "";
        }
    }
}
