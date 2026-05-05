package com.game309.neonwing.service;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameRepository {
    private static final String PREFS = "neon_wing_save";
    private static final String KEY_COINS = "coins";
    private static final String KEY_GEMS = "gems";
    private static final String KEY_BEST_SCORE = "best_score";
    private static final String KEY_LEADERBOARD = "leaderboard_top_50";
    private static final String KEY_CORE_LEVEL = "core_level";
    private static final String KEY_MISSILE_LEVEL = "missile_level";
    private static final String KEY_DRONE_LEVEL = "drone_level";
    private static final String KEY_MAGNET_LEVEL = "magnet_level";
    private static final String KEY_ASTRA_OWNED = "astra_owned";
    private static final String KEY_REMOVE_ADS = "remove_ads";
    private static final String KEY_STARTER_CLAIMED = "starter_claimed";
    private static final String KEY_SUPPLY_PASS = "supply_pass";

    private final SharedPreferences prefs;

    public static final class LeaderboardEntry {
        public final int rank;
        public final int score;
        public final int kills;
        public final float time;
        public final String shipId;
        public final String shipName;
        public final String phase;
        public final long timestamp;

        private LeaderboardEntry(int rank, JSONObject object) {
            this.rank = rank;
            score = object.optInt("score", 0);
            kills = object.optInt("kills", 0);
            time = (float) object.optDouble("time", 0d);
            shipId = object.optString("ship", "neon_wing");
            shipName = object.optString("shipName", "네온 윙");
            phase = object.optString("phase", "진입");
            timestamp = object.optLong("at", 0L);
        }
    }

    public GameRepository(Context context) {
        prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        if (!prefs.contains(KEY_CORE_LEVEL)) {
            prefs.edit()
                    .putInt(KEY_COINS, 450)
                    .putInt(KEY_GEMS, 15)
                    .putInt(KEY_CORE_LEVEL, 1)
                    .putInt(KEY_MISSILE_LEVEL, 1)
                    .putInt(KEY_DRONE_LEVEL, 1)
                    .putInt(KEY_MAGNET_LEVEL, 1)
                    .apply();
        }
    }

    public int getCoins() {
        return prefs.getInt(KEY_COINS, 0);
    }

    public int getGems() {
        return prefs.getInt(KEY_GEMS, 0);
    }

    public int getBestScore() {
        return prefs.getInt(KEY_BEST_SCORE, 0);
    }

    public int getCoreLevel() {
        return prefs.getInt(KEY_CORE_LEVEL, 1);
    }

    public int getMissileLevel() {
        return prefs.getInt(KEY_MISSILE_LEVEL, 1);
    }

    public int getDroneLevel() {
        return prefs.getInt(KEY_DRONE_LEVEL, 1);
    }

    public int getMagnetLevel() {
        return prefs.getInt(KEY_MAGNET_LEVEL, 1);
    }

    public boolean isAstraOwned() {
        return prefs.getBoolean(KEY_ASTRA_OWNED, false);
    }

    public boolean hasRemovedAds() {
        return prefs.getBoolean(KEY_REMOVE_ADS, false);
    }

    public boolean hasSupplyPass() {
        return prefs.getBoolean(KEY_SUPPLY_PASS, false);
    }

    public void setSupplyPassActive(boolean active) {
        prefs.edit().putBoolean(KEY_SUPPLY_PASS, active).apply();
    }

    public void addCoins(int amount) {
        prefs.edit().putInt(KEY_COINS, Math.max(0, getCoins() + amount)).apply();
    }

    public void addGems(int amount) {
        prefs.edit().putInt(KEY_GEMS, Math.max(0, getGems() + amount)).apply();
    }

    public void saveBestScore(int score) {
        if (score > getBestScore()) {
            prefs.edit().putInt(KEY_BEST_SCORE, score).apply();
        }
    }

    public int recordLeaderboardScore(int score, int kills, float time, String shipId, String shipName, String phase) {
        if (score <= 0) {
            return -1;
        }
        String id = System.currentTimeMillis() + "-" + score + "-" + kills;
        ArrayList<JSONObject> entries = readLeaderboardObjects();
        JSONObject object = new JSONObject();
        try {
            object.put("id", id);
            object.put("score", score);
            object.put("kills", kills);
            object.put("time", time);
            object.put("ship", shipId);
            object.put("shipName", shipName);
            object.put("phase", phase);
            object.put("at", System.currentTimeMillis());
            entries.add(object);
        } catch (JSONException ignored) {
            return -1;
        }
        normalizeLeaderboard(entries);
        writeLeaderboardObjects(entries);
        for (int i = 0; i < entries.size(); i++) {
            if (id.equals(entries.get(i).optString("id"))) {
                return i + 1;
            }
        }
        return -1;
    }

    public List<LeaderboardEntry> getLeaderboardTop50() {
        ArrayList<JSONObject> objects = readLeaderboardObjects();
        normalizeLeaderboard(objects);
        ArrayList<LeaderboardEntry> result = new ArrayList<>();
        for (int i = 0; i < objects.size(); i++) {
            result.add(new LeaderboardEntry(i + 1, objects.get(i)));
        }
        return result;
    }

    public boolean upgradeCore() {
        int level = getCoreLevel();
        int cost = upgradeCost(level, 170);
        if (spendCoins(cost)) {
            prefs.edit().putInt(KEY_CORE_LEVEL, level + 1).apply();
            return true;
        }
        return false;
    }

    public boolean upgradeMissile() {
        int level = getMissileLevel();
        int cost = upgradeCost(level, 210);
        if (spendCoins(cost)) {
            prefs.edit().putInt(KEY_MISSILE_LEVEL, level + 1).apply();
            return true;
        }
        return false;
    }

    public boolean upgradeDrone() {
        int level = getDroneLevel();
        int cost = upgradeCost(level, 240);
        if (spendCoins(cost)) {
            prefs.edit().putInt(KEY_DRONE_LEVEL, level + 1).apply();
            return true;
        }
        return false;
    }

    public boolean upgradeMagnet() {
        int level = getMagnetLevel();
        int cost = upgradeCost(level, 150);
        if (spendCoins(cost)) {
            prefs.edit().putInt(KEY_MAGNET_LEVEL, level + 1).apply();
            return true;
        }
        return false;
    }

    public int coreUpgradeCost() {
        return upgradeCost(getCoreLevel(), 170);
    }

    public int missileUpgradeCost() {
        return upgradeCost(getMissileLevel(), 210);
    }

    public int droneUpgradeCost() {
        return upgradeCost(getDroneLevel(), 240);
    }

    public int magnetUpgradeCost() {
        return upgradeCost(getMagnetLevel(), 150);
    }

    public boolean buyAstraWithGems() {
        if (isAstraOwned()) {
            return true;
        }
        if (getGems() >= 120) {
            prefs.edit()
                    .putInt(KEY_GEMS, getGems() - 120)
                    .putBoolean(KEY_ASTRA_OWNED, true)
                    .apply();
            return true;
        }
        return false;
    }

    public void grantPurchase(String productId) {
        SharedPreferences.Editor editor = prefs.edit();
        switch (productId) {
            case ProductIds.REMOVE_ADS:
                editor.putBoolean(KEY_REMOVE_ADS, true);
                break;
            case ProductIds.STARTER_PACK:
                if (!prefs.getBoolean(KEY_STARTER_CLAIMED, false)) {
                    editor.putBoolean(KEY_STARTER_CLAIMED, true)
                            .putInt(KEY_COINS, getCoins() + 1800)
                            .putInt(KEY_GEMS, getGems() + 80);
                }
                break;
            case ProductIds.PREMIUM_SHIP_ASTRA:
                editor.putBoolean(KEY_ASTRA_OWNED, true);
                break;
            case ProductIds.MONTHLY_SUPPLY_PASS:
                editor.putBoolean(KEY_SUPPLY_PASS, true)
                        .putInt(KEY_GEMS, getGems() + 40);
                break;
            case ProductIds.GEM_PACK_SMALL:
                editor.putInt(KEY_GEMS, getGems() + 110);
                break;
            default:
                break;
        }
        editor.apply();
    }

    private boolean spendCoins(int cost) {
        if (getCoins() < cost) {
            return false;
        }
        prefs.edit().putInt(KEY_COINS, getCoins() - cost).apply();
        return true;
    }

    private int upgradeCost(int level, int baseCost) {
        return baseCost + (level - 1) * (level - 1) * 55 + level * 90;
    }

    private ArrayList<JSONObject> readLeaderboardObjects() {
        ArrayList<JSONObject> entries = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(prefs.getString(KEY_LEADERBOARD, "[]"));
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.optJSONObject(i);
                if (object != null) {
                    entries.add(object);
                }
            }
        } catch (JSONException ignored) {
            return entries;
        }
        return entries;
    }

    private void writeLeaderboardObjects(List<JSONObject> entries) {
        JSONArray array = new JSONArray();
        for (JSONObject entry : entries) {
            array.put(entry);
        }
        prefs.edit().putString(KEY_LEADERBOARD, array.toString()).apply();
    }

    private void normalizeLeaderboard(ArrayList<JSONObject> entries) {
        Collections.sort(entries, new Comparator<JSONObject>() {
            @Override
            public int compare(JSONObject left, JSONObject right) {
                int scoreCompare = Integer.compare(right.optInt("score", 0), left.optInt("score", 0));
                if (scoreCompare != 0) {
                    return scoreCompare;
                }
                int killCompare = Integer.compare(right.optInt("kills", 0), left.optInt("kills", 0));
                if (killCompare != 0) {
                    return killCompare;
                }
                return Long.compare(left.optLong("at", 0L), right.optLong("at", 0L));
            }
        });
        while (entries.size() > 50) {
            entries.remove(entries.size() - 1);
        }
    }
}
