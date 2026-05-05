package com.game309.neonwing.service;

import android.app.Activity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryPurchasesParams;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BillingGateway implements PurchasesUpdatedListener {
    public interface PurchaseCallback {
        void onPurchased(String productId);

        void onPurchasePending(String productId);

        void onPurchaseFailed(String productId);
    }

    private interface ReadyAction {
        void run();
    }

    private final Activity activity;
    private final GameRepository repository;
    private final BillingClient billingClient;
    private final Map<String, ProductDetails> productDetailsById = new HashMap<>();
    private final List<ReadyAction> readyActions = new ArrayList<>();

    private PurchaseCallback activeCallback;
    private String activeProductId;
    private boolean connecting;

    public BillingGateway(Activity activity, GameRepository repository) {
        this.activity = activity;
        this.repository = repository;
        billingClient = BillingClient.newBuilder(activity)
                .setListener(this)
                .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .build())
                .build();
        ensureReady(new ReadyAction() {
            @Override
            public void run() {
                queryAllProductDetails(null);
                refreshPurchases();
            }
        });
    }

    public void purchase(String productId, PurchaseCallback callback) {
        activeProductId = productId;
        activeCallback = callback;
        ensureReady(new ReadyAction() {
            @Override
            public void run() {
                launchPurchase(productId, false);
            }
        });
    }

    public String priceLabel(String productId) {
        ProductDetails productDetails = productDetailsById.get(productId);
        String loadedPrice = formattedPrice(productDetails);
        if (loadedPrice != null && loadedPrice.length() > 0) {
            return loadedPrice;
        }
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

    public void refreshPurchases() {
        ensureReady(new ReadyAction() {
            @Override
            public void run() {
                queryExistingPurchases(BillingClient.ProductType.INAPP);
                queryExistingPurchases(BillingClient.ProductType.SUBS);
            }
        });
    }

    public void destroy() {
        if (billingClient.isReady()) {
            billingClient.endConnection();
        }
    }

    @Override
    public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && purchases != null) {
            handlePurchases(purchases, true);
            return;
        }
        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
            notifyFailure();
            return;
        }
        notifyFailure();
    }

    private void ensureReady(ReadyAction action) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (billingClient.isReady()) {
                    action.run();
                    return;
                }
                readyActions.add(action);
                if (connecting) {
                    return;
                }
                connecting = true;
                billingClient.startConnection(new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(BillingResult billingResult) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connecting = false;
                                if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                                    notifyFailure();
                                    readyActions.clear();
                                    return;
                                }
                                ArrayList<ReadyAction> actions = new ArrayList<>(readyActions);
                                readyActions.clear();
                                for (ReadyAction readyAction : actions) {
                                    readyAction.run();
                                }
                            }
                        });
                    }

                    @Override
                    public void onBillingServiceDisconnected() {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                connecting = false;
                            }
                        });
                    }
                });
            }
        });
    }

    private void queryAllProductDetails(final ReadyAction afterQuery) {
        queryProductDetails(BillingClient.ProductType.INAPP, Arrays.asList(
                ProductIds.REMOVE_ADS,
                ProductIds.STARTER_PACK,
                ProductIds.PREMIUM_SHIP_ASTRA,
                ProductIds.GEM_PACK_SMALL), afterQuery);
        queryProductDetails(BillingClient.ProductType.SUBS, Arrays.asList(
                ProductIds.MONTHLY_SUPPLY_PASS), null);
    }

    private void queryOneProductDetails(final String productId, final ReadyAction afterQuery) {
        queryProductDetails(productType(productId), Arrays.asList(productId), afterQuery);
    }

    private void queryProductDetails(String productType, List<String> productIds, final ReadyAction afterQuery) {
        ArrayList<QueryProductDetailsParams.Product> products = new ArrayList<>();
        for (String productId : productIds) {
            products.add(QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(productType)
                    .build());
        }
        billingClient.queryProductDetailsAsync(
                QueryProductDetailsParams.newBuilder()
                        .setProductList(products)
                        .build(),
                (billingResult, queryProductDetailsResult) -> activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            for (ProductDetails productDetails : queryProductDetailsResult.getProductDetailsList()) {
                                productDetailsById.put(productDetails.getProductId(), productDetails);
                            }
                        }
                        if (afterQuery != null) {
                            afterQuery.run();
                        }
                    }
                }));
    }

    private void launchPurchase(final String productId, final boolean queriedOnce) {
        ProductDetails productDetails = productDetailsById.get(productId);
        if (productDetails == null) {
            if (queriedOnce) {
                notifyFailure();
                return;
            }
            queryOneProductDetails(productId, new ReadyAction() {
                @Override
                public void run() {
                    launchPurchase(productId, true);
                }
            });
            return;
        }

        BillingFlowParams.ProductDetailsParams.Builder detailsBuilder =
                BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails);
        String offerToken = offerToken(productDetails);
        if (BillingClient.ProductType.SUBS.equals(productDetails.getProductType()) && offerToken == null) {
            notifyFailure();
            return;
        }
        if (offerToken != null) {
            detailsBuilder.setOfferToken(offerToken);
        }

        BillingResult result = billingClient.launchBillingFlow(
                activity,
                BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(Arrays.asList(detailsBuilder.build()))
                        .build());
        if (result.getResponseCode() != BillingClient.BillingResponseCode.OK) {
            notifyFailure();
        }
    }

    private void queryExistingPurchases(final String productType) {
        billingClient.queryPurchasesAsync(
                QueryPurchasesParams.newBuilder()
                        .setProductType(productType)
                        .build(),
                (billingResult, purchases) -> activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (billingResult.getResponseCode() != BillingClient.BillingResponseCode.OK) {
                            return;
                        }
                        handlePurchases(purchases, false);
                        if (BillingClient.ProductType.SUBS.equals(productType)) {
                            repository.setSupplyPassActive(hasActiveProduct(purchases, ProductIds.MONTHLY_SUPPLY_PASS));
                        }
                    }
                }));
    }

    private void handlePurchases(List<Purchase> purchases, boolean notifyActivePurchase) {
        if (purchases == null) {
            return;
        }
        for (Purchase purchase : purchases) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PENDING) {
                notifyPendingPurchase(purchase, notifyActivePurchase);
                continue;
            }
            if (purchase.getPurchaseState() != Purchase.PurchaseState.PURCHASED) {
                continue;
            }
            for (String productId : purchase.getProducts()) {
                if (isConsumable(productId)) {
                    grantConsumable(productId, purchase, notifyActivePurchase);
                } else {
                    repository.grantPurchase(productId);
                    acknowledgeIfNeeded(purchase);
                    if (notifyActivePurchase && productId.equals(activeProductId)) {
                        notifySuccess(productId);
                    }
                }
            }
        }
    }

    private void notifyPendingPurchase(Purchase purchase, boolean notifyActivePurchase) {
        if (!notifyActivePurchase || activeProductId == null || !purchase.getProducts().contains(activeProductId)) {
            return;
        }
        PurchaseCallback callback = activeCallback;
        String productId = activeProductId;
        activeCallback = null;
        activeProductId = null;
        if (callback != null) {
            callback.onPurchasePending(productId);
        }
    }

    private void grantConsumable(String productId, Purchase purchase, boolean notifyActivePurchase) {
        int quantity = Math.max(1, purchase.getQuantity());
        for (int i = 0; i < quantity; i++) {
            repository.grantPurchase(productId);
        }
        ConsumeParams params = ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build();
        billingClient.consumeAsync(params, (billingResult, purchaseToken) -> {
            if (notifyActivePurchase && productId.equals(activeProductId)) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                            notifySuccess(productId);
                        } else {
                            notifyFailure();
                        }
                    }
                });
            }
        });
    }

    private void acknowledgeIfNeeded(Purchase purchase) {
        if (purchase.isAcknowledged()) {
            return;
        }
        billingClient.acknowledgePurchase(
                AcknowledgePurchaseParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build(),
                billingResult -> {
                });
    }

    private void notifySuccess(String productId) {
        PurchaseCallback callback = activeCallback;
        activeCallback = null;
        activeProductId = null;
        if (callback != null) {
            callback.onPurchased(productId);
        }
    }

    private void notifyFailure() {
        PurchaseCallback callback = activeCallback;
        String productId = activeProductId;
        activeCallback = null;
        activeProductId = null;
        if (callback != null) {
            callback.onPurchaseFailed(productId);
        }
    }

    private static String productType(String productId) {
        return ProductIds.MONTHLY_SUPPLY_PASS.equals(productId)
                ? BillingClient.ProductType.SUBS
                : BillingClient.ProductType.INAPP;
    }

    private static boolean isConsumable(String productId) {
        return ProductIds.GEM_PACK_SMALL.equals(productId);
    }

    private static boolean hasActiveProduct(List<Purchase> purchases, String productId) {
        for (Purchase purchase : purchases) {
            if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED
                    && purchase.getProducts().contains(productId)) {
                return true;
            }
        }
        return false;
    }

    private static String formattedPrice(ProductDetails productDetails) {
        if (productDetails == null) {
            return null;
        }
        if (BillingClient.ProductType.SUBS.equals(productDetails.getProductType())) {
            List<ProductDetails.SubscriptionOfferDetails> offers = productDetails.getSubscriptionOfferDetails();
            if (offers != null && !offers.isEmpty()
                    && offers.get(0).getPricingPhases() != null
                    && !offers.get(0).getPricingPhases().getPricingPhaseList().isEmpty()) {
                return offers.get(0).getPricingPhases().getPricingPhaseList().get(0).getFormattedPrice();
            }
            return null;
        }
        List<ProductDetails.OneTimePurchaseOfferDetails> offers = productDetails.getOneTimePurchaseOfferDetailsList();
        if (offers != null && !offers.isEmpty()) {
            return offers.get(0).getFormattedPrice();
        }
        ProductDetails.OneTimePurchaseOfferDetails legacyOffer = productDetails.getOneTimePurchaseOfferDetails();
        return legacyOffer != null ? legacyOffer.getFormattedPrice() : null;
    }

    private static String offerToken(ProductDetails productDetails) {
        if (BillingClient.ProductType.SUBS.equals(productDetails.getProductType())) {
            List<ProductDetails.SubscriptionOfferDetails> offers = productDetails.getSubscriptionOfferDetails();
            return offers != null && !offers.isEmpty() ? offers.get(0).getOfferToken() : null;
        }
        List<ProductDetails.OneTimePurchaseOfferDetails> offers = productDetails.getOneTimePurchaseOfferDetailsList();
        if (offers != null && !offers.isEmpty()) {
            return offers.get(0).getOfferToken();
        }
        return null;
    }
}
