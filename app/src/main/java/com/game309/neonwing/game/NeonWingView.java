package com.game309.neonwing.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.game309.neonwing.service.AdsGateway;
import com.game309.neonwing.service.BillingGateway;
import com.game309.neonwing.service.GameRepository;
import com.game309.neonwing.service.ProductIds;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SuppressLint("ViewConstructor")
public class NeonWingView extends SurfaceView implements SurfaceHolder.Callback, Runnable {
    private enum Screen {
        SPLASH,
        TITLE,
        HANGAR,
        PLAYING,
        GAME_OVER,
        LEADERBOARD
    }

    public interface ScreenListener {
        void onScreenChanged(String screenName);
    }

    private static final int CYAN = Color.rgb(82, 244, 255);
    private static final int BLUE = Color.rgb(64, 120, 255);
    private static final int GREEN = Color.rgb(99, 255, 154);
    private static final int MAGENTA = Color.rgb(255, 79, 216);
    private static final int ORANGE = Color.rgb(255, 139, 48);
    private static final int RED = Color.rgb(255, 64, 86);
    private static final int GOLD = Color.rgb(255, 220, 94);
    private static final int WHITE = Color.rgb(246, 247, 255);
    private static final int BG = Color.rgb(5, 7, 15);

    private final SurfaceHolder surfaceHolder;
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Random random = new Random();
    private final GameRepository repository;
    private final AdsGateway adsGateway;
    private final BillingGateway billingGateway;

    private final Player player = new Player();
    private final List<Bullet> playerBullets = new ArrayList<>();
    private final List<Bullet> enemyBullets = new ArrayList<>();
    private final List<Enemy> enemies = new ArrayList<>();
    private final List<Particle> particles = new ArrayList<>();
    private final List<Pickup> pickups = new ArrayList<>();
    private final List<FloatText> floatTexts = new ArrayList<>();

    private final RectF startButton = new RectF();
    private final RectF buffButton = new RectF();
    private final RectF hangarButton = new RectF();
    private final RectF leaderboardButton = new RectF();
    private final RectF coreButton = new RectF();
    private final RectF missileButton = new RectF();
    private final RectF droneButton = new RectF();
    private final RectF magnetButton = new RectF();
    private final RectF astraButton = new RectF();
    private final RectF starterButton = new RectF();
    private final RectF removeAdsButton = new RectF();
    private final RectF passButton = new RectF();
    private final RectF backButton = new RectF();
    private final RectF skillButton = new RectF();
    private final RectF restoreDroneButton = new RectF();
    private final RectF exitRunButton = new RectF();
    private final RectF claimButton = new RectF();
    private final RectF doubleButton = new RectF();
    private final RectF reviveButton = new RectF();
    private final RectF retryButton = new RectF();
    private final RectF gameOverHangarButton = new RectF();
    private final RectF gameOverLeaderboardButton = new RectF();
    private final RectF leaderboardBackButton = new RectF();
    private final RectF leaderboardPrevButton = new RectF();
    private final RectF leaderboardNextButton = new RectF();

    private volatile boolean running;
    private Thread gameThread;
    private Screen screen = Screen.SPLASH;
    private Screen leaderboardBackScreen = Screen.TITLE;
    private ScreenListener screenListener;
    private int width;
    private int height;
    private float density = 1f;
    private float unit = 1f;
    private float[] starX = new float[120];
    private float[] starY = new float[120];
    private float[] starSpeed = new float[120];
    private float[] starSize = new float[120];

    private boolean dragging;
    private boolean usedRevive;
    private boolean rewardClaimed;
    private boolean doubleRewardClaimed;
    private boolean runExited;
    private int score;
    private int kills;
    private int pendingCoins;
    private int pendingGems;
    private int lastRank = -1;
    private int leaderboardPage;
    private float runTime;
    private float spawnTimer;
    private float shotTimer;
    private float missileTimer;
    private float laserTimer;
    private float bossTimer;
    private float shakeTime;
    private float shakePower;
    private float hitFlash;
    private String statusMessage = "";
    private float statusTimer;
    private boolean startBuffActive;
    private long lastNanos;

    public NeonWingView(Context context, GameRepository repository, AdsGateway adsGateway, BillingGateway billingGateway) {
        super(context);
        this.repository = repository;
        this.adsGateway = adsGateway;
        this.billingGateway = billingGateway;
        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        setFocusable(true);
        textPaint.setColor(WHITE);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void setScreenListener(ScreenListener screenListener) {
        this.screenListener = screenListener;
        notifyScreenChanged();
    }

    private void setScreen(Screen nextScreen) {
        if (screen == nextScreen) {
            return;
        }
        screen = nextScreen;
        notifyScreenChanged();
    }

    private void notifyScreenChanged() {
        if (screenListener != null) {
            screenListener.onScreenChanged(screen.name());
        }
    }

    public void resume() {
        running = true;
        if (surfaceHolder.getSurface().isValid()) {
            startThreadIfNeeded();
        }
    }

    public void pause() {
        running = false;
        Thread thread = gameThread;
        if (thread != null) {
            try {
                thread.join(800);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        gameThread = null;
    }

    private synchronized void startThreadIfNeeded() {
        if (gameThread != null && gameThread.isAlive()) {
            return;
        }
        gameThread = new Thread(this, "NeonWingLoop");
        gameThread.start();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        running = true;
        startThreadIfNeeded();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        this.width = width;
        this.height = height;
        density = getResources().getDisplayMetrics().density;
        unit = Math.max(1f, Math.min(width / 420f, height / 760f));
        layoutButtons();
        seedStars();
        if (player.x == 0f) {
            resetPlayerPosition();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        pause();
    }

    @Override
    public void run() {
        lastNanos = System.nanoTime();
        while (running) {
            long now = System.nanoTime();
            float dt = Math.min(0.034f, (now - lastNanos) / 1_000_000_000f);
            lastNanos = now;
            update(dt);
            Canvas canvas = null;
            try {
                canvas = surfaceHolder.lockCanvas();
                if (canvas != null) {
                    drawGame(canvas);
                }
            } finally {
                if (canvas != null) {
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }
            long frameMs = (System.nanoTime() - now) / 1_000_000L;
            long sleepMs = Math.max(2L, 16L - frameMs);
            try {
                Thread.sleep(sleepMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (width == 0 || height == 0) {
            return true;
        }
        float x = event.getX();
        float y = event.getY();
        int action = event.getActionMasked();

        if (action == MotionEvent.ACTION_DOWN) {
            if (handleTap(x, y)) {
                return true;
            }
            if (screen == Screen.PLAYING) {
                dragging = true;
                updatePlayerTarget(x, y);
            }
            return true;
        }

        if (action == MotionEvent.ACTION_MOVE && screen == Screen.PLAYING && dragging) {
            updatePlayerTarget(x, y);
            return true;
        }

        if (action == MotionEvent.ACTION_UP || action == MotionEvent.ACTION_CANCEL) {
            if (!dragging) {
                performClick();
            }
            dragging = false;
            return true;
        }
        return true;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    private boolean handleTap(float x, float y) {
        if (screen == Screen.SPLASH) {
            setScreen(Screen.TITLE);
            hitFlash = Math.max(hitFlash, 0.18f);
            return true;
        }
        if (screen == Screen.TITLE) {
            if (startButton.contains(x, y)) {
                startRun(false);
                return true;
            }
            if (buffButton.contains(x, y)) {
                requestStartBuff();
                return true;
            }
            if (hangarButton.contains(x, y)) {
                setScreen(Screen.HANGAR);
                return true;
            }
            if (leaderboardButton.contains(x, y)) {
                openLeaderboard(Screen.TITLE);
                return true;
            }
        } else if (screen == Screen.HANGAR) {
            if (backButton.contains(x, y)) {
                setScreen(Screen.TITLE);
                return true;
            }
            if (coreButton.contains(x, y)) {
                showEconomyResult(repository.upgradeCore(), "코어 업그레이드 완료", "코인이 부족합니다");
                return true;
            }
            if (missileButton.contains(x, y)) {
                showEconomyResult(repository.upgradeMissile(), "미사일 업그레이드 완료", "코인이 부족합니다");
                return true;
            }
            if (droneButton.contains(x, y)) {
                showEconomyResult(repository.upgradeDrone(), "드론 업그레이드 완료", "코인이 부족합니다");
                return true;
            }
            if (magnetButton.contains(x, y)) {
                showEconomyResult(repository.upgradeMagnet(), "자석 업그레이드 완료", "코인이 부족합니다");
                return true;
            }
            if (astraButton.contains(x, y)) {
                showEconomyResult(repository.buyAstraWithGems(), "아스트라 해금 완료", "보석 120개가 필요합니다");
                return true;
            }
            if (starterButton.contains(x, y)) {
                buy(ProductIds.STARTER_PACK);
                return true;
            }
            if (removeAdsButton.contains(x, y)) {
                buy(ProductIds.REMOVE_ADS);
                return true;
            }
            if (passButton.contains(x, y)) {
                buy(ProductIds.MONTHLY_SUPPLY_PASS);
                return true;
            }
        } else if (screen == Screen.PLAYING) {
            if (exitRunButton.contains(x, y)) {
                endRun(true);
                return true;
            }
            if (skillButton.contains(x, y)) {
                useNovaBomb();
                return true;
            }
            if (!player.droneActive && restoreDroneButton.contains(x, y)) {
                restoreDroneWithAd();
                return true;
            }
        } else if (screen == Screen.GAME_OVER) {
            if (claimButton.contains(x, y)) {
                claimReward(1);
                return true;
            }
            if (doubleButton.contains(x, y)) {
                doubleRewardWithAd();
                return true;
            }
            if (reviveButton.contains(x, y)) {
                reviveWithAd();
                return true;
            }
            if (retryButton.contains(x, y)) {
                if (!rewardClaimed) {
                    claimReward(1);
                }
                startRun(false);
                return true;
            }
            if (gameOverHangarButton.contains(x, y)) {
                if (!rewardClaimed) {
                    claimReward(1);
                }
                setScreen(Screen.HANGAR);
                return true;
            }
            if (gameOverLeaderboardButton.contains(x, y)) {
                openLeaderboard(Screen.GAME_OVER);
                return true;
            }
        } else if (screen == Screen.LEADERBOARD) {
            if (leaderboardBackButton.contains(x, y)) {
                setScreen(leaderboardBackScreen);
                return true;
            }
            if (leaderboardPrevButton.contains(x, y)) {
                leaderboardPage = Math.max(0, leaderboardPage - 1);
                return true;
            }
            if (leaderboardNextButton.contains(x, y)) {
                int maxPage = Math.max(0, (repository.getLeaderboardTop50().size() - 1) / 10);
                leaderboardPage = Math.min(maxPage, leaderboardPage + 1);
                return true;
            }
        }
        return false;
    }

    private void openLeaderboard(Screen backScreen) {
        leaderboardBackScreen = backScreen;
        leaderboardPage = 0;
        setScreen(Screen.LEADERBOARD);
    }

    private void buy(String productId) {
        billingGateway.purchase(productId, new BillingGateway.PurchaseCallback() {
            @Override
            public void onPurchased(String productId) {
                setStatus("구매 보상이 지급되었습니다");
            }

            @Override
            public void onPurchaseFailed(String productId) {
                setStatus("구매에 실패했습니다");
            }
        });
    }

    private void requestStartBuff() {
        adsGateway.showRewarded(AdsGateway.RewardPlacement.START_BUFF, new AdsGateway.RewardCallback() {
            @Override
            public void onRewardEarned() {
                startRun(true);
            }

            @Override
            public void onRewardUnavailable() {
                setStatus("보상형 광고를 불러올 수 없습니다");
            }
        });
    }

    private void doubleRewardWithAd() {
        if (rewardClaimed || doubleRewardClaimed) {
            return;
        }
        adsGateway.showRewarded(AdsGateway.RewardPlacement.DOUBLE_REWARD, new AdsGateway.RewardCallback() {
            @Override
            public void onRewardEarned() {
                doubleRewardClaimed = true;
                claimReward(2);
            }

            @Override
            public void onRewardUnavailable() {
                setStatus("보상형 광고를 불러올 수 없습니다");
            }
        });
    }

    private void reviveWithAd() {
        if (usedRevive || screen != Screen.GAME_OVER) {
            return;
        }
        adsGateway.showRewarded(AdsGateway.RewardPlacement.REVIVE, new AdsGateway.RewardCallback() {
            @Override
            public void onRewardEarned() {
                usedRevive = true;
                setScreen(Screen.PLAYING);
                player.hp = Math.max(player.maxHp * 0.4f, 36f);
                player.invulnerable = 1.6f;
                pendingCoins = 0;
                pendingGems = 0;
                rewardClaimed = false;
                setStatus("부활했습니다");
            }

            @Override
            public void onRewardUnavailable() {
                setStatus("보상형 광고를 불러올 수 없습니다");
            }
        });
    }

    private void restoreDroneWithAd() {
        adsGateway.showRewarded(AdsGateway.RewardPlacement.RESTORE_DRONE, new AdsGateway.RewardCallback() {
            @Override
            public void onRewardEarned() {
                player.droneActive = true;
                player.droneTime = 32f + repository.getDroneLevel() * 4f;
                createBurst(player.x, player.y + 18f * unit, CYAN, 18, 140f * unit);
                setStatus("드론이 복구되었습니다");
            }

            @Override
            public void onRewardUnavailable() {
                setStatus("보상형 광고를 불러올 수 없습니다");
            }
        });
    }

    private void showEconomyResult(boolean success, String ok, String fail) {
        setStatus(success ? ok : fail);
    }

    private void setStatus(String message) {
        statusMessage = message;
        statusTimer = 2.2f;
    }

    private String currentPhaseLabel() {
        if (runTime >= 240f) {
            return "보스 접근";
        }
        if (runTime >= 170f) {
            return "핵심부";
        }
        if (runTime >= 100f) {
            return "돌파";
        }
        if (runTime >= 30f) {
            return "외곽";
        }
        return "진입";
    }

    private void updatePlayerTarget(float x, float y) {
        float offset = 72f * unit;
        player.targetX = clamp(x, 24f * unit, width - 24f * unit);
        player.targetY = clamp(y - offset, height * 0.18f, height - 72f * unit);
    }

    private void startRun(boolean withStartBuff) {
        setScreen(Screen.PLAYING);
        playerBullets.clear();
        enemyBullets.clear();
        enemies.clear();
        particles.clear();
        pickups.clear();
        floatTexts.clear();
        score = 0;
        kills = 0;
        runTime = 0f;
        spawnTimer = 0.2f;
        shotTimer = 0f;
        missileTimer = 0.6f;
        laserTimer = 3.8f;
        bossTimer = 45f;
        shakeTime = 0f;
        hitFlash = 0f;
        pendingCoins = 0;
        pendingGems = 0;
        lastRank = -1;
        rewardClaimed = false;
        doubleRewardClaimed = false;
        usedRevive = false;
        runExited = false;
        startBuffActive = withStartBuff;
        resetPlayerPosition();
        int core = repository.getCoreLevel();
        player.maxHp = 96f + core * 9f + (repository.isAstraOwned() ? 18f : 0f) + (withStartBuff ? 22f : 0f);
        player.hp = player.maxHp;
        player.invulnerable = withStartBuff ? 2.4f : 1.2f;
        player.skillCharge = withStartBuff ? 0.55f : 0.15f;
        player.droneActive = true;
        player.droneTime = 46f + repository.getDroneLevel() * 7f;
        setStatus(withStartBuff ? "시작 버프 적용" : "출격 시작");
    }

    private void resetPlayerPosition() {
        player.x = width * 0.5f;
        player.y = height * 0.8f;
        player.targetX = player.x;
        player.targetY = player.y;
    }

    private void endRun() {
        endRun(false);
    }

    private void endRun(boolean exited) {
        runExited = exited;
        setScreen(Screen.GAME_OVER);
        repository.saveBestScore(score);
        lastRank = repository.recordLeaderboardScore(score, kills, runTime, "neon_wing", "네온 윙", currentPhaseLabel());
        pendingCoins = Math.max(25, 45 + score / 12 + kills * 3 + (int) (runTime * 1.5f));
        if (repository.hasSupplyPass()) {
            pendingCoins = Math.round(pendingCoins * 1.12f);
        }
        pendingGems = score >= 1400 ? 1 : 0;
        if (exited) {
            setStatus(lastRank > 0 ? "전투 종료 / 랭킹 " + lastRank + "위" : "전투 종료");
        } else {
            setStatus(lastRank > 0 ? "임무 실패 / 랭킹 " + lastRank + "위" : "임무 실패");
        }
        if (!exited) {
            adsGateway.showGameOverInterstitial(repository.hasRemovedAds(), runTime);
        }
    }

    private void claimReward(int multiplier) {
        if (rewardClaimed) {
            return;
        }
        repository.addCoins(pendingCoins * multiplier);
        repository.addGems(pendingGems);
        rewardClaimed = true;
        setStatus("보상을 받았습니다");
    }

    private void update(float dt) {
        updateBackground(dt);
        updateParticles(dt);
        updateFloatTexts(dt);
        statusTimer = Math.max(0f, statusTimer - dt);
        if (screen == Screen.PLAYING) {
            updatePlaying(dt);
        }
    }

    private void updateBackground(float dt) {
        if (height == 0) {
            return;
        }
        for (int i = 0; i < starY.length; i++) {
            starY[i] += starSpeed[i] * dt;
            if (starY[i] > height + 12f * unit) {
                starY[i] = -12f * unit;
                starX[i] = random.nextFloat() * width;
            }
        }
    }

    private void updatePlaying(float dt) {
        runTime += dt;
        shakeTime = Math.max(0f, shakeTime - dt);
        hitFlash = Math.max(0f, hitFlash - dt * 2.8f);

        float dx = player.targetX - player.x;
        float dy = player.targetY - player.y;
        float dist = (float) Math.sqrt(dx * dx + dy * dy);
        if (dist > 0.4f * unit) {
            float moveSpeed = Math.min(width, height) * 0.62f;
            float step = Math.min(dist, moveSpeed * dt);
            player.x += dx / dist * step;
            player.y += dy / dist * step;
        }
        player.invulnerable = Math.max(0f, player.invulnerable - dt);
        player.skillCharge = Math.min(1f, player.skillCharge + dt / Math.max(28f, 43f - repository.getCoreLevel()));

        if (player.droneActive) {
            player.droneTime -= dt;
            if (player.droneTime <= 0f) {
                player.droneActive = false;
                createExplosion(player.x - 34f * unit, player.y + 18f * unit, CYAN, 0.7f);
                createExplosion(player.x + 34f * unit, player.y + 18f * unit, CYAN, 0.7f);
                setStatus("드론 배터리 소진");
            }
        }

        shotTimer -= dt;
        missileTimer -= dt;
        laserTimer -= dt;
        spawnTimer -= dt;
        bossTimer -= dt;

        if (shotTimer <= 0f) {
            firePlayerShots();
            shotTimer = Math.max(0.07f, 0.17f - repository.getCoreLevel() * 0.006f);
        }
        if (missileTimer <= 0f) {
            fireMissiles();
            missileTimer = Math.max(0.38f, 1.03f - repository.getMissileLevel() * 0.032f);
        }
        if (laserTimer <= 0f) {
            fireLaser();
            laserTimer = Math.max(5.6f, 7.4f - repository.getCoreLevel() * 0.08f);
        }
        if (spawnTimer <= 0f) {
            spawnEnemyWave(false);
            float interval = Math.max(0.26f, 1.0f - runTime * 0.0065f);
            spawnTimer = interval + random.nextFloat() * 0.18f;
        }
        if (bossTimer <= 0f) {
            spawnEnemyWave(true);
            bossTimer = 44f + random.nextFloat() * 14f;
        }

        updateBullets(dt);
        updateEnemies(dt);
        updatePickups(dt);
        resolveCollisions();
        capEntityCounts();
    }

    private void firePlayerShots() {
        int core = repository.getCoreLevel();
        float damage = (14f + core * 2.4f) * (startBuffActive ? 1.1f : 1f);
        addPlayerBullet(player.x, player.y - 36f * unit, 0f, -height * 0.95f, 4.2f * unit, damage, CYAN, false);
        if (core >= 2) {
            addPlayerBullet(player.x - 16f * unit, player.y - 26f * unit, -width * 0.035f, -height * 0.88f, 3.6f * unit, damage * 0.72f, BLUE, false);
            addPlayerBullet(player.x + 16f * unit, player.y - 26f * unit, width * 0.035f, -height * 0.88f, 3.6f * unit, damage * 0.72f, BLUE, false);
        }
        if (player.droneActive) {
            int drone = repository.getDroneLevel();
            float sideDamage = damage * (0.34f + drone * 0.025f);
            addPlayerBullet(player.x - 42f * unit, player.y + 8f * unit, -width * 0.02f, -height * 0.74f, 3.2f * unit, sideDamage, WHITE, false);
            addPlayerBullet(player.x + 42f * unit, player.y + 8f * unit, width * 0.02f, -height * 0.74f, 3.2f * unit, sideDamage, WHITE, false);
        }
    }

    private void fireMissiles() {
        Enemy target = nearestEnemy(player.x, player.y);
        if (target == null) {
            return;
        }
        int missile = repository.getMissileLevel();
        float damage = 36f + missile * 12f;
        int count = 1 + (missile >= 3 ? 1 : 0) + (missile >= 6 ? 1 : 0) + (repository.isAstraOwned() ? 1 : 0);
        for (int i = 0; i < count; i++) {
            float spread = (i - (count - 1) * 0.5f) * 20f * unit;
            float damageScale = i == 0 ? 1f : 0.82f;
            addMissile(player.x + spread, player.y - 12f * unit, target, damage * damageScale);
        }
        if (missile >= 5) {
            addMissile(player.x - 42f * unit, player.y + 2f * unit, target, damage * 0.36f);
            addMissile(player.x + 42f * unit, player.y + 2f * unit, target, damage * 0.36f);
        }
    }

    private void fireLaser() {
        if (enemies.isEmpty()) {
            return;
        }
        float laserX = player.x;
        float halfWidth = 11f * unit;
        float damage = 52f + repository.getCoreLevel() * 10f;
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            if (Math.abs(enemy.x - laserX) < enemy.radius + halfWidth) {
                enemy.hp -= damage;
                addFloatText(enemy.x, enemy.y - enemy.radius, "레이저", CYAN);
                createBurst(enemy.x, enemy.y, CYAN, 8, 90f * unit);
                if (enemy.hp <= 0f) {
                    killEnemy(i);
                }
            }
        }
        Particle beam = Particle.beam(laserX, player.y - 48f * unit, laserX, -40f * unit, CYAN, 0.22f, 16f * unit);
        particles.add(beam);
        createBurst(player.x, player.y - 42f * unit, WHITE, 10, 80f * unit);
    }

    private void useNovaBomb() {
        if (player.skillCharge < 1f) {
            setStatus("노바 충전 중");
            return;
        }
        player.skillCharge = 0f;
        enemyBullets.clear();
        float damage = 165f + repository.getCoreLevel() * 18f;
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.hp -= damage;
            createExplosion(enemy.x, enemy.y, GOLD, 0.85f);
            if (enemy.hp <= 0f) {
                killEnemy(i);
            }
        }
        createShockwave(player.x, player.y, GOLD);
        addScreenShake(0.2f, 7f * unit);
        setStatus("노바 발동");
    }

    private void addPlayerBullet(float x, float y, float vx, float vy, float radius, float damage, int color, boolean missile) {
        Bullet bullet = new Bullet();
        bullet.x = x;
        bullet.y = y;
        bullet.vx = vx;
        bullet.vy = vy;
        bullet.radius = radius;
        bullet.damage = damage;
        bullet.color = color;
        bullet.playerOwned = true;
        bullet.missile = missile;
        bullet.life = 2.4f;
        playerBullets.add(bullet);
    }

    private void addMissile(float x, float y, Enemy target, float damage) {
        Bullet missile = new Bullet();
        missile.x = x;
        missile.y = y;
        missile.vx = (random.nextFloat() - 0.5f) * width * 0.16f;
        missile.vy = -height * 0.45f;
        float tier = Math.max(0f, repository.getMissileLevel() - 1f);
        missile.radius = (7f + Math.min(5f, tier * 0.7f)) * unit;
        missile.damage = damage;
        missile.color = GOLD;
        missile.playerOwned = true;
        missile.missile = true;
        missile.target = target;
        missile.life = 4f;
        playerBullets.add(missile);
    }

    private void addEnemyBullet(float x, float y, float vx, float vy, float radius, float damage, int color) {
        Bullet bullet = new Bullet();
        bullet.x = x;
        bullet.y = y;
        bullet.vx = vx;
        bullet.vy = vy;
        bullet.radius = radius;
        bullet.damage = damage;
        bullet.color = color;
        bullet.playerOwned = false;
        bullet.life = 4.2f;
        enemyBullets.add(bullet);
    }

    private void spawnEnemyWave(boolean elite) {
        if (width == 0) {
            return;
        }
        if (elite) {
            Enemy boss = makeEnemy(3, width * (0.24f + random.nextFloat() * 0.52f), -54f * unit);
            boss.hp *= 2.2f + runTime / 120f;
            boss.maxHp = boss.hp;
            enemies.add(boss);
            setStatus("엘리트 접근");
            return;
        }
        int count = 1
                + (runTime > 25f ? 1 : 0)
                + (runTime > 55f ? random.nextInt(2) : 0)
                + (runTime > 95f ? 1 : 0)
                + (runTime > 145f ? random.nextInt(2) : 0);
        for (int i = 0; i < count; i++) {
            int roll = random.nextInt(100);
            int type = roll < 52 ? 0 : roll < 78 ? 1 : roll < 93 ? 2 : 3;
            float x = width * (0.11f + random.nextFloat() * 0.78f);
            float y = -random.nextFloat() * 80f * unit - 24f * unit;
            enemies.add(makeEnemy(type, x, y));
        }
    }

    private Enemy makeEnemy(int type, float x, float y) {
        float difficulty = 1f + runTime * 0.015f + score * 0.00014f;
        Enemy enemy = new Enemy();
        enemy.type = type;
        enemy.x = x;
        enemy.y = y;
        enemy.phase = random.nextFloat() * 6.28f;
        enemy.fireTimer = 0.5f + random.nextFloat() * 1.2f;
        if (type == 0) {
            enemy.radius = 15f * unit;
            enemy.hp = 24f * difficulty;
            enemy.speed = height * (0.17f + random.nextFloat() * 0.05f);
            enemy.damage = 14f;
        } else if (type == 1) {
            enemy.radius = 18f * unit;
            enemy.hp = 48f * difficulty;
            enemy.speed = height * 0.12f;
            enemy.damage = 12f;
        } else if (type == 2) {
            enemy.radius = 25f * unit;
            enemy.hp = 128f * difficulty;
            enemy.speed = height * 0.09f;
            enemy.damage = 22f;
        } else {
            enemy.radius = 31f * unit;
            enemy.hp = 230f * difficulty;
            enemy.speed = height * 0.075f;
            enemy.damage = 20f;
        }
        enemy.maxHp = enemy.hp;
        return enemy;
    }

    private void updateBullets(float dt) {
        for (int i = playerBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = playerBullets.get(i);
            bullet.life -= dt;
            if (bullet.missile && bullet.target != null && enemies.contains(bullet.target)) {
                float dx = bullet.target.x - bullet.x;
                float dy = bullet.target.y - bullet.y;
                float len = Math.max(1f, (float) Math.sqrt(dx * dx + dy * dy));
                float desired = height * 0.62f;
                bullet.vx += (dx / len * desired - bullet.vx) * Math.min(1f, dt * 3.2f);
                bullet.vy += (dy / len * desired - bullet.vy) * Math.min(1f, dt * 3.2f);
                bullet.addTrail();
            }
            bullet.x += bullet.vx * dt;
            bullet.y += bullet.vy * dt;
            if (bullet.life <= 0f || bullet.y < -80f * unit || bullet.y > height + 80f * unit || bullet.x < -80f * unit || bullet.x > width + 80f * unit) {
                playerBullets.remove(i);
            }
        }

        for (int i = enemyBullets.size() - 1; i >= 0; i--) {
            Bullet bullet = enemyBullets.get(i);
            bullet.life -= dt;
            bullet.x += bullet.vx * dt;
            bullet.y += bullet.vy * dt;
            if (bullet.life <= 0f || bullet.y > height + 80f * unit || bullet.x < -80f * unit || bullet.x > width + 80f * unit) {
                enemyBullets.remove(i);
            }
        }
    }

    private void updateEnemies(float dt) {
        for (int i = enemies.size() - 1; i >= 0; i--) {
            Enemy enemy = enemies.get(i);
            enemy.age += dt;
            enemy.y += enemy.speed * dt;
            float sway = (float) Math.sin(enemy.age * (1.4f + enemy.type * 0.3f) + enemy.phase);
            enemy.x += sway * width * 0.035f * dt;
            enemy.x = clamp(enemy.x, enemy.radius + 8f * unit, width - enemy.radius - 8f * unit);
            enemy.fireTimer -= dt;
            if (enemy.fireTimer <= 0f && enemy.y > 40f * unit && enemy.y < height * 0.72f) {
                fireEnemy(enemy);
                enemy.fireTimer = enemy.type == 3 ? 0.78f : enemy.type == 2 ? 1.45f : 1.1f + random.nextFloat() * 0.45f;
            }
            if (enemy.y > height + 80f * unit) {
                enemies.remove(i);
            }
        }
    }

    private void fireEnemy(Enemy enemy) {
        float dx = player.x - enemy.x;
        float dy = player.y - enemy.y;
        float len = Math.max(1f, (float) Math.sqrt(dx * dx + dy * dy));
        float speed = height * (enemy.type == 3 ? 0.31f : 0.25f);
        addEnemyBullet(enemy.x, enemy.y + enemy.radius * 0.6f, dx / len * speed, dy / len * speed, 5.8f * unit, enemy.damage, enemy.type == 3 ? MAGENTA : RED);
        if (enemy.type == 3) {
            addEnemyBullet(enemy.x - 10f * unit, enemy.y + enemy.radius * 0.4f, -width * 0.13f, height * 0.26f, 5.2f * unit, enemy.damage * 0.75f, ORANGE);
            addEnemyBullet(enemy.x + 10f * unit, enemy.y + enemy.radius * 0.4f, width * 0.13f, height * 0.26f, 5.2f * unit, enemy.damage * 0.75f, ORANGE);
        }
    }

    private void updatePickups(float dt) {
        for (int i = pickups.size() - 1; i >= 0; i--) {
            Pickup pickup = pickups.get(i);
            float dx = player.x - pickup.x;
            float dy = player.y - pickup.y;
            float dist = Math.max(1f, (float) Math.sqrt(dx * dx + dy * dy));
            float magnetRadius = (72f + repository.getMagnetLevel() * 20f + repository.getDroneLevel() * 4f) * unit;
            if (dist < magnetRadius) {
                pickup.vx += dx / dist * width * 0.7f * dt;
                pickup.vy += dy / dist * height * 0.7f * dt;
            }
            pickup.x += pickup.vx * dt;
            pickup.y += pickup.vy * dt;
            pickup.life -= dt;
            if (dist < player.radius + 12f * unit) {
                score += pickup.value;
                addFloatText(player.x, player.y - 42f * unit, "+" + pickup.value, pickup.type == 1 ? GREEN : GOLD);
                pickups.remove(i);
            } else if (pickup.life <= 0f || pickup.y > height + 40f * unit) {
                pickups.remove(i);
            }
        }
    }

    private void resolveCollisions() {
        for (int b = playerBullets.size() - 1; b >= 0; b--) {
            Bullet bullet = playerBullets.get(b);
            boolean consumed = false;
            for (int e = enemies.size() - 1; e >= 0; e--) {
                Enemy enemy = enemies.get(e);
                if (distanceSquared(bullet.x, bullet.y, enemy.x, enemy.y) <= square(bullet.radius + enemy.radius)) {
                    enemy.hp -= bullet.damage;
                    createHitSpark(bullet.x, bullet.y, bullet.color);
                    if (bullet.missile) {
                        createExplosion(bullet.x, bullet.y, GOLD, 0.5f);
                        addScreenShake(0.08f, 2.8f * unit);
                    }
                    playerBullets.remove(b);
                    consumed = true;
                    if (enemy.hp <= 0f) {
                        killEnemy(e);
                    }
                    break;
                }
            }
            if (consumed) {
                continue;
            }
        }

        if (player.invulnerable <= 0f) {
            for (int i = enemyBullets.size() - 1; i >= 0; i--) {
                Bullet bullet = enemyBullets.get(i);
                if (distanceSquared(bullet.x, bullet.y, player.x, player.y) <= square(bullet.radius + player.radius * 0.72f)) {
                    enemyBullets.remove(i);
                    damagePlayer(bullet.damage);
                    break;
                }
            }
        }

        if (player.invulnerable <= 0f) {
            for (int i = enemies.size() - 1; i >= 0; i--) {
                Enemy enemy = enemies.get(i);
                if (distanceSquared(enemy.x, enemy.y, player.x, player.y) <= square(enemy.radius + player.radius * 0.7f)) {
                    damagePlayer(enemy.damage * 1.4f);
                    createExplosion(enemy.x, enemy.y, RED, 0.7f);
                    enemies.remove(i);
                    break;
                }
            }
        }
    }

    private void damagePlayer(float damage) {
        player.hp -= damage;
        player.invulnerable = 1.05f;
        hitFlash = 1f;
        addScreenShake(0.16f, 5f * unit);
        createBurst(player.x, player.y, RED, 14, 130f * unit);
        if (player.droneActive && random.nextFloat() < 0.16f) {
            player.droneActive = false;
            createExplosion(player.x + (random.nextBoolean() ? -34f : 34f) * unit, player.y + 18f * unit, CYAN, 0.75f);
            setStatus("드론 파괴");
        }
        if (player.hp <= 0f) {
            createExplosion(player.x, player.y, CYAN, 1.35f);
            addScreenShake(0.42f, 10f * unit);
            endRun();
        }
    }

    private void killEnemy(int index) {
        if (index < 0 || index >= enemies.size()) {
            return;
        }
        Enemy enemy = enemies.remove(index);
        kills++;
        int value = enemy.type == 3 ? 95 : enemy.type == 2 ? 46 : enemy.type == 1 ? 32 : 22;
        score += value;
        createExplosion(enemy.x, enemy.y, enemy.type == 3 ? MAGENTA : ORANGE, enemy.type == 3 ? 1.05f : 0.72f);
        addFloatText(enemy.x, enemy.y - enemy.radius, "+" + value, GOLD);
        int pickupCount = enemy.type == 3 ? 8 : enemy.type == 2 ? 5 : enemy.type == 1 ? 4 : 3;
        for (int i = 0; i < pickupCount; i++) {
            Pickup pickup = new Pickup();
            pickup.type = 0;
            pickup.x = enemy.x + (random.nextFloat() - 0.5f) * enemy.radius;
            pickup.y = enemy.y + (random.nextFloat() - 0.5f) * enemy.radius;
            pickup.vx = (random.nextFloat() - 0.5f) * 120f * unit;
            pickup.vy = (34f + random.nextFloat() * 96f) * unit;
            pickup.value = enemy.type == 3 ? 7 : 3;
            pickup.life = 6.2f;
            pickups.add(pickup);
        }
        int cashCount = enemy.type == 3 ? 2 : enemy.type == 2 ? (random.nextFloat() < 0.55f ? 1 : 0) : (random.nextFloat() < 0.32f ? 1 : 0);
        for (int i = 0; i < cashCount; i++) {
            Pickup pickup = new Pickup();
            pickup.type = 1;
            pickup.x = enemy.x + (random.nextFloat() - 0.5f) * enemy.radius;
            pickup.y = enemy.y + (random.nextFloat() - 0.5f) * enemy.radius;
            pickup.vx = (random.nextFloat() - 0.5f) * 110f * unit;
            pickup.vy = (34f + random.nextFloat() * 90f) * unit;
            pickup.value = 12 + Math.min(18, ((int) (runTime / 25f)) * 2);
            pickup.life = 6.2f;
            pickups.add(pickup);
        }
    }

    private Enemy nearestEnemy(float x, float y) {
        Enemy best = null;
        float bestDist = Float.MAX_VALUE;
        for (Enemy enemy : enemies) {
            float d = distanceSquared(x, y, enemy.x, enemy.y);
            if (d < bestDist) {
                bestDist = d;
                best = enemy;
            }
        }
        return best;
    }

    private void capEntityCounts() {
        trimFront(playerBullets, 190);
        trimFront(enemyBullets, 150);
        trimFront(enemies, 30);
        trimFront(particles, 520);
        trimFront(pickups, 130);
        trimFront(floatTexts, 32);
    }

    private <T> void trimFront(List<T> list, int max) {
        while (list.size() > max) {
            list.remove(0);
        }
    }

    private void updateParticles(float dt) {
        for (int i = particles.size() - 1; i >= 0; i--) {
            Particle p = particles.get(i);
            p.life -= dt;
            p.x += p.vx * dt;
            p.y += p.vy * dt;
            p.vx *= 1f - Math.min(0.7f, dt * p.drag);
            p.vy *= 1f - Math.min(0.7f, dt * p.drag);
            p.radius += p.growth * dt;
            if (p.life <= 0f) {
                particles.remove(i);
            }
        }
    }

    private void updateFloatTexts(float dt) {
        for (int i = floatTexts.size() - 1; i >= 0; i--) {
            FloatText text = floatTexts.get(i);
            text.life -= dt;
            text.y -= 34f * unit * dt;
            if (text.life <= 0f) {
                floatTexts.remove(i);
            }
        }
    }

    private void drawGame(Canvas canvas) {
        if (width == 0 || height == 0) {
            canvas.drawColor(BG);
            return;
        }
        drawBackground(canvas);
        if (screen == Screen.PLAYING && shakeTime > 0f) {
            float shakeX = (random.nextFloat() - 0.5f) * shakePower;
            float shakeY = (random.nextFloat() - 0.5f) * shakePower;
            canvas.save();
            canvas.translate(shakeX, shakeY);
            drawWorld(canvas);
            canvas.restore();
        } else {
            drawWorld(canvas);
        }
        drawOverlay(canvas);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawColor(BG);
        paint.setShader(new LinearGradient(0f, 0f, width, height, Color.rgb(8, 12, 26), Color.rgb(2, 4, 11), Shader.TileMode.CLAMP));
        canvas.drawRect(0f, 0f, width, height, paint);
        paint.setShader(null);

        for (int i = 0; i < starX.length; i++) {
            int alpha = i % 3 == 0 ? 180 : 90;
            paint.setColor(Color.argb(alpha, 130, 220, 255));
            canvas.drawCircle(starX[i], starY[i], starSize[i], paint);
        }

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f * unit);
        paint.setColor(Color.argb(28, 82, 244, 255));
        float grid = 58f * unit;
        float offset = (runTime * 28f * unit) % grid;
        for (float y = offset; y < height; y += grid) {
            canvas.drawLine(width * 0.08f, y, width * 0.92f, y + 18f * unit, paint);
        }
        paint.setColor(Color.argb(34, 255, 79, 216));
        canvas.drawLine(width * 0.08f, 0f, width * 0.03f, height, paint);
        canvas.drawLine(width * 0.92f, 0f, width * 0.97f, height, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawWorld(Canvas canvas) {
        for (Pickup pickup : pickups) {
            drawPickup(canvas, pickup);
        }
        for (Bullet bullet : playerBullets) {
            drawBullet(canvas, bullet);
        }
        for (Enemy enemy : enemies) {
            drawEnemy(canvas, enemy);
        }
        for (Bullet bullet : enemyBullets) {
            drawBullet(canvas, bullet);
        }
        for (Particle particle : particles) {
            drawParticle(canvas, particle);
        }
        if (screen == Screen.PLAYING || screen == Screen.GAME_OVER) {
            drawPlayer(canvas);
        }
    }

    private void drawOverlay(Canvas canvas) {
        if (screen == Screen.SPLASH) {
            drawSplash(canvas);
        } else if (screen == Screen.TITLE) {
            drawTitle(canvas);
        } else if (screen == Screen.HANGAR) {
            drawHangar(canvas);
        } else if (screen == Screen.PLAYING) {
            drawHud(canvas);
        } else if (screen == Screen.GAME_OVER) {
            drawGameOver(canvas);
        } else if (screen == Screen.LEADERBOARD) {
            drawLeaderboard(canvas);
        }
        if (screen != Screen.SPLASH) {
            drawStatus(canvas);
        }
        drawFloatTexts(canvas);
        if (hitFlash > 0f) {
            paint.setColor(Color.argb((int) (70 * hitFlash), 255, 30, 64));
            canvas.drawRect(0f, 0f, width, height, paint);
        }
    }

    private void drawSplash(Canvas canvas) {
        drawVignette(canvas, 184);
        drawLogoShip(canvas, width * 0.5f, height * 0.3f, 1.34f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(42f * unit);
        textPaint.setColor(WHITE);
        canvas.drawText("NEON WING", width * 0.5f, height * 0.46f, textPaint);
        float pulse = 0.55f + 0.45f * (float) Math.sin(System.nanoTime() / 250_000_000.0);
        textPaint.setTextSize(18f * unit);
        textPaint.setColor(Color.argb((int) (205 + pulse * 45), 235, 248, 255));
        canvas.drawText("클릭하여 실행", width * 0.5f, height * 0.72f, textPaint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.4f * unit);
        paint.setColor(Color.argb((int) (85 + pulse * 90), Color.red(CYAN), Color.green(CYAN), Color.blue(CYAN)));
        canvas.drawLine(width * 0.32f, height * 0.742f, width * 0.68f, height * 0.742f, paint);
        paint.setStyle(Paint.Style.FILL);
        textPaint.setFakeBoldText(false);
    }

    private void drawTitle(Canvas canvas) {
        drawVignette(canvas, 170);
        drawLogoShip(canvas, width * 0.5f, height * 0.27f, 1.25f);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(42f * unit);
        textPaint.setColor(WHITE);
        canvas.drawText("NEON WING", width * 0.5f, height * 0.39f, textPaint);
        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(14f * unit);
        textPaint.setColor(Color.argb(210, 178, 237, 255));
        canvas.drawText("자동 사격 / 드래그 회피 / 폭발적인 일격", width * 0.5f, height * 0.43f, textPaint);
        drawButton(canvas, startButton, "출격 시작", CYAN, true);
        drawButton(canvas, buffButton, "광고 보고 시작 버프", GOLD, false);
        drawButton(canvas, hangarButton, "격납고 / 상점", MAGENTA, false);
        drawButton(canvas, leaderboardButton, "랭킹 TOP 50", WHITE, false);
        drawTopEconomy(canvas);
    }

    private void drawHangar(Canvas canvas) {
        drawVignette(canvas, 120);
        drawTopEconomy(canvas);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(25f * unit);
        textPaint.setColor(WHITE);
        canvas.drawText("격납고", width * 0.07f, height * 0.15f, textPaint);
        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(12f * unit);
        textPaint.setColor(Color.argb(210, 178, 237, 255));
        canvas.drawText("성장은 플레이 코인으로, 결제는 편의와 연출 중심으로 구성됩니다.", width * 0.07f, height * 0.18f, textPaint);

        drawUpgradeButton(canvas, coreButton, "코어", repository.getCoreLevel(), repository.coreUpgradeCost(), CYAN);
        drawUpgradeButton(canvas, missileButton, "미사일", repository.getMissileLevel(), repository.missileUpgradeCost(), GOLD);
        drawUpgradeButton(canvas, droneButton, "드론", repository.getDroneLevel(), repository.droneUpgradeCost(), WHITE);
        drawUpgradeButton(canvas, magnetButton, "자석", repository.getMagnetLevel(), repository.magnetUpgradeCost(), BLUE);
        drawPurchaseButton(canvas, astraButton, "아스트라 기체", repository.isAstraOwned() ? "보유 중" : "", MAGENTA);
        if (!repository.isAstraOwned()) {
            drawCurrencyAmount(canvas, "gem", 120, astraButton.right - 76f * unit, astraButton.centerY() + 4f * unit, 13f * unit, Paint.Align.LEFT);
        }
        drawPurchaseButton(canvas, starterButton, "스타터 패키지", billingGateway.priceLabel(ProductIds.STARTER_PACK), GOLD);
        drawPurchaseButton(canvas, removeAdsButton, "광고 제거", repository.hasRemovedAds() ? "보유 중" : billingGateway.priceLabel(ProductIds.REMOVE_ADS), CYAN);
        drawPurchaseButton(canvas, passButton, "월간 보급 패스", repository.hasSupplyPass() ? "활성화" : billingGateway.priceLabel(ProductIds.MONTHLY_SUPPLY_PASS), MAGENTA);
        drawButton(canvas, backButton, "뒤로", WHITE, false);
    }

    private void drawHud(Canvas canvas) {
        drawTopEconomy(canvas);
        drawHealthBar(canvas, width * 0.06f, height * 0.055f, width * 0.38f, 8f * unit, player.hp / player.maxHp, RED, CYAN);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(18f * unit);
        textPaint.setColor(WHITE);
        canvas.drawText(String.format(Locale.US, "%06d", score), width * 0.94f, height * 0.068f, textPaint);
        textPaint.setFakeBoldText(false);

        float r = skillButton.width() * 0.5f;
        float charge = player.skillCharge;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(130, 8, 14, 30));
        canvas.drawOval(skillButton, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(4f * unit);
        paint.setColor(charge >= 1f ? GOLD : Color.argb(150, 120, 160, 180));
        canvas.drawArc(skillButton, -90f, 360f * charge, false, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(charge >= 1f ? Color.argb(190, 255, 220, 94) : Color.argb(105, 82, 244, 255));
        canvas.drawCircle(skillButton.centerX(), skillButton.centerY(), r * 0.52f, paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(11f * unit);
        textPaint.setColor(Color.rgb(8, 12, 20));
        canvas.drawText("노바", skillButton.centerX(), skillButton.centerY() + 4f * unit, textPaint);
        textPaint.setFakeBoldText(false);
        drawButton(canvas, exitRunButton, "나가기", WHITE, false);

        if (!player.droneActive) {
            drawButton(canvas, restoreDroneButton, "광고 드론", CYAN, false);
        } else {
            textPaint.setTextAlign(Paint.Align.LEFT);
            textPaint.setTextSize(11f * unit);
            textPaint.setColor(Color.argb(190, 190, 244, 255));
            canvas.drawText("드론 " + (int) Math.ceil(player.droneTime) + "초", width * 0.06f, height * 0.088f, textPaint);
        }
    }

    private void drawGameOver(Canvas canvas) {
        drawVignette(canvas, 190);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(34f * unit);
        textPaint.setColor(WHITE);
        canvas.drawText(runExited ? "전투 종료" : "임무 실패", width * 0.5f, height * 0.23f, textPaint);
        textPaint.setTextSize(16f * unit);
        textPaint.setColor(Color.argb(225, 178, 237, 255));
        canvas.drawText("점수 " + score + "   최고 " + repository.getBestScore(), width * 0.5f, height * 0.29f, textPaint);
        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(14f * unit);
        textPaint.setColor(GOLD);
        canvas.drawText("보상", width * 0.5f - 44f * unit, height * 0.335f, textPaint);
        drawCurrencyAmount(canvas, "coin", pendingCoins, width * 0.5f - 12f * unit, height * 0.335f, 15f * unit, Paint.Align.LEFT);
        if (pendingGems > 0) {
            drawCurrencyAmount(canvas, "gem", pendingGems, width * 0.5f + 72f * unit, height * 0.335f, 15f * unit, Paint.Align.LEFT);
        }
        if (lastRank > 0) {
            textPaint.setFakeBoldText(true);
            textPaint.setTextSize(13f * unit);
            textPaint.setColor(CYAN);
            canvas.drawText("로컬 랭킹 " + lastRank + "위 기록", width * 0.5f, height * 0.37f, textPaint);
            textPaint.setFakeBoldText(false);
        }
        drawButton(canvas, claimButton, rewardClaimed ? "보상 수령 완료" : "보상 받기", WHITE, false);
        drawButton(canvas, doubleButton, rewardClaimed ? "2배 보상 불가" : "광고 보고 보상 2배", GOLD, false);
        drawButton(canvas, reviveButton, usedRevive ? "부활 사용 완료" : "광고 보고 부활", CYAN, false);
        drawButton(canvas, retryButton, "다시 도전", MAGENTA, true);
        drawButton(canvas, gameOverHangarButton, "격납고", WHITE, false);
        drawButton(canvas, gameOverLeaderboardButton, "랭킹 TOP 50", CYAN, false);
    }

    private void drawLeaderboard(Canvas canvas) {
        drawVignette(canvas, 145);
        drawTopEconomy(canvas);
        List<GameRepository.LeaderboardEntry> entries = repository.getLeaderboardTop50();
        int pageSize = 10;
        int totalPages = Math.max(1, (entries.size() + pageSize - 1) / pageSize);
        leaderboardPage = Math.max(0, Math.min(leaderboardPage, totalPages - 1));
        int start = leaderboardPage * pageSize;

        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(25f * unit);
        textPaint.setColor(WHITE);
        canvas.drawText("랭킹 TOP 50", width * 0.07f, height * 0.12f, textPaint);
        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(12f * unit);
        textPaint.setColor(Color.argb(210, 190, 235, 255));
        canvas.drawText("이 기기에 저장된 로컬 기록입니다.", width * 0.07f, height * 0.152f, textPaint);

        if (entries.isEmpty()) {
            RectF emptyPanel = new RectF(width * 0.07f, height * 0.25f, width * 0.93f, height * 0.25f + 106f * unit);
            drawPanel(canvas, emptyPanel, CYAN);
            textPaint.setTextAlign(Paint.Align.CENTER);
            textPaint.setFakeBoldText(true);
            textPaint.setTextSize(16f * unit);
            textPaint.setColor(WHITE);
            canvas.drawText("아직 기록이 없습니다", width * 0.5f, emptyPanel.top + 46f * unit, textPaint);
            textPaint.setFakeBoldText(false);
            textPaint.setTextSize(12f * unit);
            textPaint.setColor(Color.argb(205, 220, 242, 255));
            canvas.drawText("출격 후 점수를 남겨보세요", width * 0.5f, emptyPanel.top + 72f * unit, textPaint);
        } else {
            int end = Math.min(entries.size(), start + pageSize);
            for (int i = start; i < end; i++) {
                GameRepository.LeaderboardEntry entry = entries.get(i);
                float y = height * 0.185f + (i - start) * 47f * unit;
                RectF row = new RectF(width * 0.07f, y, width * 0.93f, y + 39f * unit);
                int color = entry.rank == 1 ? GOLD : entry.rank <= 3 ? CYAN : WHITE;
                drawPanel(canvas, row, color);
                textPaint.setTextAlign(Paint.Align.LEFT);
                textPaint.setFakeBoldText(true);
                textPaint.setTextSize(13f * unit);
                textPaint.setColor(color);
                canvas.drawText(String.valueOf(entry.rank), row.left + 14f * unit, row.top + 25f * unit, textPaint);
                textPaint.setTextSize(14f * unit);
                textPaint.setColor(WHITE);
                canvas.drawText(String.format(Locale.US, "%06d", entry.score), row.left + 50f * unit, row.top + 17f * unit, textPaint);
                textPaint.setFakeBoldText(false);
                textPaint.setTextSize(10f * unit);
                textPaint.setColor(Color.argb(198, 210, 235, 248));
                canvas.drawText(entry.shipName + "  " + entry.kills + "킬  " + formatDuration(entry.time), row.left + 50f * unit, row.top + 32f * unit, textPaint);
            }
        }

        drawButton(canvas, leaderboardPrevButton, "이전", WHITE, false);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(12f * unit);
        textPaint.setColor(Color.argb(225, 235, 248, 255));
        canvas.drawText((leaderboardPage + 1) + " / " + totalPages, width * 0.5f, leaderboardPrevButton.centerY() + 5f * unit, textPaint);
        textPaint.setFakeBoldText(false);
        drawButton(canvas, leaderboardNextButton, "다음", WHITE, false);
        drawButton(canvas, leaderboardBackButton, "돌아가기", CYAN, true);
    }

    private String formatDuration(float seconds) {
        int total = Math.max(0, (int) seconds);
        return String.format(Locale.US, "%d:%02d", total / 60, total % 60);
    }

    private void drawTopEconomy(Canvas canvas) {
        drawCurrencyPill(canvas, "coin", repository.getCoins(), width * 0.06f, height * 0.018f, 86f * unit, GOLD);
        drawCurrencyPill(canvas, "gem", repository.getGems(), width * 0.285f, height * 0.018f, 70f * unit, MAGENTA);
        drawCurrencyPill(canvas, "trophy", repository.getBestScore(), width * 0.94f - 96f * unit, height * 0.018f, 96f * unit, CYAN);
    }

    private void drawCurrencyPill(Canvas canvas, String kind, int amount, float x, float y, float w, int accent) {
        RectF rect = new RectF(x, y, x + w, y + 24f * unit);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(164, 7, 12, 25));
        canvas.drawRoundRect(rect, 7f * unit, 7f * unit, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1f * unit);
        paint.setColor(Color.argb(92, Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawRoundRect(rect, 7f * unit, 7f * unit, paint);
        paint.setStyle(Paint.Style.FILL);
        drawCurrencyAmount(canvas, kind, amount, x + 8f * unit, y + 16.5f * unit, 13f * unit, Paint.Align.LEFT);
    }

    private void drawCurrencyAmount(Canvas canvas, String kind, int amount, float x, float y, float size, Paint.Align align) {
        String text = String.valueOf(Math.max(0, amount));
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(Math.max(10f * unit, size - 1f * unit));
        float gap = Math.max(4f * unit, size * 0.36f);
        float total = size + gap + textPaint.measureText(text);
        float left = align == Paint.Align.CENTER ? x - total * 0.5f : align == Paint.Align.RIGHT ? x - total : x;
        drawCurrencyIcon(canvas, kind, left + size * 0.5f, y - size * 0.32f, size);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setColor("cash".equals(kind) ? GREEN : Color.argb(240, 246, 247, 255));
        canvas.drawText(text, left + size + gap, y, textPaint);
        textPaint.setFakeBoldText(false);
    }

    private void drawCurrencyIcon(Canvas canvas, String kind, float x, float y, float size) {
        paint.setStyle(Paint.Style.FILL);
        if ("gem".equals(kind)) {
            Path gem = new Path();
            gem.moveTo(x, y - size * 0.55f);
            gem.lineTo(x + size * 0.5f, y - size * 0.08f);
            gem.lineTo(x + size * 0.25f, y + size * 0.55f);
            gem.lineTo(x - size * 0.25f, y + size * 0.55f);
            gem.lineTo(x - size * 0.5f, y - size * 0.08f);
            gem.close();
            paint.setColor(MAGENTA);
            canvas.drawPath(gem, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, size * 0.1f));
            paint.setColor(WHITE);
            canvas.drawPath(gem, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(160, 255, 255, 255));
            canvas.drawCircle(x - size * 0.13f, y - size * 0.12f, size * 0.11f, paint);
        } else if ("cash".equals(kind)) {
            RectF bill = new RectF(x - size * 0.58f, y - size * 0.36f, x + size * 0.58f, y + size * 0.36f);
            paint.setColor(GREEN);
            canvas.drawRoundRect(bill, size * 0.12f, size * 0.12f, paint);
            paint.setColor(Color.argb(190, 6, 24, 14));
            canvas.drawRect(x - size * 0.45f, y - size * 0.21f, x - size * 0.29f, y + size * 0.21f, paint);
            canvas.drawRect(x + size * 0.29f, y - size * 0.21f, x + size * 0.45f, y + size * 0.21f, paint);
            paint.setColor(Color.argb(210, 255, 255, 255));
            canvas.drawCircle(x, y, size * 0.18f, paint);
        } else if ("trophy".equals(kind)) {
            Path cup = new Path();
            cup.moveTo(x - size * 0.38f, y - size * 0.38f);
            cup.lineTo(x + size * 0.38f, y - size * 0.38f);
            cup.lineTo(x + size * 0.26f, y + size * 0.08f);
            cup.quadTo(x, y + size * 0.34f, x - size * 0.26f, y + size * 0.08f);
            cup.close();
            paint.setColor(CYAN);
            canvas.drawPath(cup, paint);
            paint.setStrokeWidth(Math.max(1f, size * 0.08f));
            paint.setStyle(Paint.Style.STROKE);
            paint.setColor(WHITE);
            canvas.drawLine(x - size * 0.18f, y + size * 0.33f, x + size * 0.18f, y + size * 0.33f, paint);
            canvas.drawLine(x, y + size * 0.1f, x, y + size * 0.33f, paint);
            paint.setStyle(Paint.Style.FILL);
        } else {
            paint.setColor(GOLD);
            canvas.drawCircle(x, y, size * 0.5f, paint);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, size * 0.09f));
            paint.setColor(Color.rgb(255, 242, 164));
            canvas.drawCircle(x, y, size * 0.44f, paint);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(Color.argb(150, 255, 255, 255));
            canvas.drawCircle(x - size * 0.13f, y - size * 0.15f, size * 0.12f, paint);
        }
    }

    private void drawUpgradeButton(Canvas canvas, RectF rect, String label, int level, int cost, int accent) {
        drawPanel(canvas, rect, accent);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(15f * unit);
        textPaint.setColor(WHITE);
        canvas.drawText(label + "  Lv." + level, rect.left + 18f * unit, rect.centerY() - 4f * unit, textPaint);
        textPaint.setFakeBoldText(false);
        textPaint.setTextSize(12f * unit);
        textPaint.setColor(Color.argb(220, 190, 235, 255));
        canvas.drawText("업그레이드", rect.left + 18f * unit, rect.centerY() + 16f * unit, textPaint);
        drawCurrencyAmount(canvas, "coin", cost, rect.left + 88f * unit, rect.centerY() + 16f * unit, 13f * unit, Paint.Align.LEFT);
        drawSmallChevron(canvas, rect.right - 30f * unit, rect.centerY(), accent);
    }

    private void drawPurchaseButton(Canvas canvas, RectF rect, String label, String price, int accent) {
        drawPanel(canvas, rect, accent);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(14f * unit);
        textPaint.setColor(WHITE);
        canvas.drawText(label, rect.left + 18f * unit, rect.centerY() - 2f * unit, textPaint);
        textPaint.setTextAlign(Paint.Align.RIGHT);
        textPaint.setTextSize(12f * unit);
        textPaint.setColor(accent);
        canvas.drawText(price, rect.right - 18f * unit, rect.centerY() + 4f * unit, textPaint);
        textPaint.setFakeBoldText(false);
    }

    private void drawButton(Canvas canvas, RectF rect, String label, int accent, boolean primary) {
        float radius = 8f * unit;
        paint.setStyle(Paint.Style.FILL);
        if (primary) {
            paint.setShader(new LinearGradient(rect.left, rect.top, rect.right, rect.bottom, accent, Color.rgb(18, 32, 58), Shader.TileMode.CLAMP));
        } else {
            paint.setShader(null);
            paint.setColor(Color.argb(176, 10, 16, 31));
        }
        canvas.drawRoundRect(rect, radius, radius, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.4f * unit);
        paint.setColor(Color.argb(200, Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawRoundRect(rect, radius, radius, paint);
        paint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        textPaint.setTextSize(14f * unit);
        textPaint.setColor(primary ? Color.rgb(5, 8, 16) : WHITE);
        canvas.drawText(label, rect.centerX(), rect.centerY() + 5f * unit, textPaint);
        textPaint.setFakeBoldText(false);
    }

    private void drawPanel(Canvas canvas, RectF rect, int accent) {
        float radius = 7f * unit;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(172, 9, 15, 29));
        canvas.drawRoundRect(rect, radius, radius, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.1f * unit);
        paint.setColor(Color.argb(165, Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawRoundRect(rect, radius, radius, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(34, Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawRect(rect.left, rect.top, rect.left + 5f * unit, rect.bottom, paint);
    }

    private void drawHealthBar(Canvas canvas, float x, float y, float w, float h, float ratio, int low, int high) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(150, 10, 14, 26));
        canvas.drawRoundRect(new RectF(x, y, x + w, y + h), h * 0.5f, h * 0.5f, paint);
        int color = ratio < 0.3f ? low : high;
        paint.setColor(color);
        canvas.drawRoundRect(new RectF(x, y, x + w * clamp(ratio, 0f, 1f), y + h), h * 0.5f, h * 0.5f, paint);
    }

    private void drawPlayer(Canvas canvas) {
        if (screen == Screen.GAME_OVER) {
            paint.setAlpha(100);
        }
        float blink = player.invulnerable > 0f ? 0.55f + 0.45f * (float) Math.sin(runTime * 28f) : 1f;
        int coreColor = repository.isAstraOwned() ? MAGENTA : CYAN;
        drawShipPath(canvas, player.x, player.y, 1f, coreColor, blink);
        if (player.droneActive && screen == Screen.PLAYING) {
            drawDrone(canvas, player.x - 42f * unit, player.y + 18f * unit, CYAN);
            drawDrone(canvas, player.x + 42f * unit, player.y + 18f * unit, CYAN);
        }
        paint.setAlpha(255);
    }

    private void drawLogoShip(Canvas canvas, float x, float y, float scale) {
        drawShipPath(canvas, x, y, scale, CYAN, 1f);
    }

    private void drawShipPath(Canvas canvas, float x, float y, float scale, int accent, float alphaRatio) {
        float s = unit * scale;
        Path hull = new Path();
        hull.moveTo(x, y - 46f * s);
        hull.lineTo(x + 25f * s, y + 30f * s);
        hull.lineTo(x + 8f * s, y + 21f * s);
        hull.lineTo(x, y + 48f * s);
        hull.lineTo(x - 8f * s, y + 21f * s);
        hull.lineTo(x - 25f * s, y + 30f * s);
        hull.close();
        paint.setShader(new LinearGradient(x, y - 48f * s, x, y + 48f * s, Color.argb((int) (240 * alphaRatio), 232, 247, 255), Color.argb((int) (220 * alphaRatio), 32, 48, 74), Shader.TileMode.CLAMP));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawPath(hull, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f * s);
        paint.setColor(Color.argb((int) (220 * alphaRatio), Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawPath(hull, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb((int) (180 * alphaRatio), Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawCircle(x, y - 4f * s, 8f * s, paint);
        paint.setColor(Color.argb((int) (120 * alphaRatio), Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawCircle(x, y + 35f * s, 12f * s, paint);
    }

    private void drawDrone(Canvas canvas, float x, float y, int accent) {
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(95, Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawCircle(x, y, 15f * unit, paint);
        paint.setColor(Color.argb(235, 214, 247, 255));
        canvas.drawCircle(x, y, 6f * unit, paint);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(1.6f * unit);
        paint.setColor(accent);
        canvas.drawCircle(x, y, 13f * unit, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void drawEnemy(Canvas canvas, Enemy enemy) {
        int accent = enemy.type == 0 ? RED : enemy.type == 1 ? ORANGE : enemy.type == 2 ? MAGENTA : Color.rgb(190, 82, 255);
        float r = enemy.radius;
        Path path = new Path();
        if (enemy.type == 0) {
            path.moveTo(enemy.x, enemy.y + r);
            path.lineTo(enemy.x - r * 0.85f, enemy.y - r * 0.6f);
            path.lineTo(enemy.x + r * 0.85f, enemy.y - r * 0.6f);
            path.close();
        } else if (enemy.type == 1) {
            path.moveTo(enemy.x, enemy.y + r);
            path.lineTo(enemy.x - r, enemy.y);
            path.lineTo(enemy.x - r * 0.35f, enemy.y - r);
            path.lineTo(enemy.x + r * 0.35f, enemy.y - r);
            path.lineTo(enemy.x + r, enemy.y);
            path.close();
        } else {
            path.moveTo(enemy.x, enemy.y + r);
            path.lineTo(enemy.x - r * 1.15f, enemy.y + r * 0.2f);
            path.lineTo(enemy.x - r * 0.6f, enemy.y - r);
            path.lineTo(enemy.x + r * 0.6f, enemy.y - r);
            path.lineTo(enemy.x + r * 1.15f, enemy.y + r * 0.2f);
            path.close();
        }
        paint.setStyle(Paint.Style.FILL);
        paint.setShader(new LinearGradient(enemy.x, enemy.y - r, enemy.x, enemy.y + r, Color.argb(230, 70, 34, 54), Color.argb(240, 20, 14, 28), Shader.TileMode.CLAMP));
        canvas.drawPath(path, paint);
        paint.setShader(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(enemy.type == 3 ? 2.2f * unit : 1.6f * unit);
        paint.setColor(Color.argb(225, Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(150, Color.red(accent), Color.green(accent), Color.blue(accent)));
        canvas.drawCircle(enemy.x, enemy.y, r * 0.28f, paint);
        if (enemy.type >= 2) {
            drawHealthBar(canvas, enemy.x - r, enemy.y - r - 8f * unit, r * 2f, 4f * unit, enemy.hp / enemy.maxHp, RED, MAGENTA);
        }
    }

    private void drawBullet(Canvas canvas, Bullet bullet) {
        if (bullet.missile) {
            for (int i = 0; i < bullet.trailCount; i++) {
                int idx = (bullet.trailIndex - i - 1 + bullet.trailX.length) % bullet.trailX.length;
                float ratio = 1f - i / (float) bullet.trailX.length;
                paint.setColor(Color.argb((int) (110 * ratio), 255, 220, 94));
                canvas.drawCircle(bullet.trailX[idx], bullet.trailY[idx], bullet.radius * ratio * 1.6f, paint);
            }
            paint.setColor(GOLD);
            canvas.drawCircle(bullet.x, bullet.y, bullet.radius, paint);
            paint.setColor(WHITE);
            canvas.drawCircle(bullet.x, bullet.y, bullet.radius * 0.45f, paint);
            return;
        }
        int color = bullet.color;
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb(70, Color.red(color), Color.green(color), Color.blue(color)));
        canvas.drawCircle(bullet.x, bullet.y, bullet.radius * 2.4f, paint);
        paint.setColor(color);
        canvas.drawCircle(bullet.x, bullet.y, bullet.radius, paint);
        paint.setColor(WHITE);
        canvas.drawCircle(bullet.x, bullet.y, bullet.radius * 0.42f, paint);
    }

    private void drawParticle(Canvas canvas, Particle p) {
        float ratio = clamp(p.life / p.maxLife, 0f, 1f);
        int alpha = (int) (p.alpha * ratio);
        if (p.beam) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeWidth(p.radius * 2.8f);
            paint.setColor(Color.argb((int) (55 * ratio), Color.red(p.color), Color.green(p.color), Color.blue(p.color)));
            canvas.drawLine(p.x, p.y, p.x2, p.y2, paint);
            paint.setStrokeWidth(p.radius * 1.2f);
            paint.setColor(Color.argb((int) (150 * ratio), Color.red(p.color), Color.green(p.color), Color.blue(p.color)));
            canvas.drawLine(p.x, p.y, p.x2, p.y2, paint);
            paint.setStrokeWidth(Math.max(1f, p.radius * 0.26f));
            paint.setColor(Color.argb((int) (230 * ratio), 250, 255, 255));
            canvas.drawLine(p.x, p.y, p.x2, p.y2, paint);
            paint.setStrokeCap(Paint.Cap.BUTT);
            paint.setStyle(Paint.Style.FILL);
            return;
        }
        if (p.ring) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, p.stroke * ratio));
            paint.setColor(Color.argb(alpha, Color.red(p.color), Color.green(p.color), Color.blue(p.color)));
            canvas.drawCircle(p.x, p.y, p.radius, paint);
            paint.setStyle(Paint.Style.FILL);
            return;
        }
        paint.setColor(Color.argb(alpha, Color.red(p.color), Color.green(p.color), Color.blue(p.color)));
        if (p.line) {
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(Math.max(1f, p.radius * 0.45f));
            canvas.drawLine(p.x, p.y, p.x - p.vx * 0.045f, p.y - p.vy * 0.045f, paint);
            paint.setStyle(Paint.Style.FILL);
        } else {
            canvas.drawCircle(p.x, p.y, p.radius * (0.45f + ratio), paint);
        }
    }

    private void drawPickup(Canvas canvas, Pickup pickup) {
        paint.setStyle(Paint.Style.FILL);
        int color = pickup.type == 1 ? GREEN : GOLD;
        paint.setColor(Color.argb(80, Color.red(color), Color.green(color), Color.blue(color)));
        canvas.drawCircle(pickup.x, pickup.y, pickup.type == 1 ? 12f * unit : 9f * unit, paint);
        if (pickup.type == 1) {
            drawCurrencyIcon(canvas, "cash", pickup.x, pickup.y, 18f * unit);
        } else {
            drawCurrencyIcon(canvas, "coin", pickup.x, pickup.y, 13f * unit);
        }
    }

    private void drawFloatTexts(Canvas canvas) {
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setFakeBoldText(true);
        for (FloatText text : floatTexts) {
            float ratio = clamp(text.life / text.maxLife, 0f, 1f);
            textPaint.setTextSize(12f * unit);
            textPaint.setColor(Color.argb((int) (230 * ratio), Color.red(text.color), Color.green(text.color), Color.blue(text.color)));
            canvas.drawText(text.text, text.x, text.y, textPaint);
        }
        textPaint.setFakeBoldText(false);
    }

    private void drawStatus(Canvas canvas) {
        if (statusTimer <= 0f || statusMessage.length() == 0) {
            return;
        }
        float alpha = clamp(statusTimer / 0.45f, 0f, 1f);
        RectF rect = new RectF(width * 0.16f, height * 0.105f, width * 0.84f, height * 0.145f);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.argb((int) (145 * alpha), 9, 15, 29));
        canvas.drawRoundRect(rect, 8f * unit, 8f * unit, paint);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setTextSize(12f * unit);
        textPaint.setColor(Color.argb((int) (235 * alpha), 235, 248, 255));
        canvas.drawText(statusMessage, rect.centerX(), rect.centerY() + 4f * unit, textPaint);
    }

    private void drawVignette(Canvas canvas, int alpha) {
        paint.setShader(new RadialGradient(width * 0.5f, height * 0.42f, height * 0.62f, Color.argb(0, 0, 0, 0), Color.argb(alpha, 0, 0, 0), Shader.TileMode.CLAMP));
        canvas.drawRect(0f, 0f, width, height, paint);
        paint.setShader(null);
    }

    private void drawSmallChevron(Canvas canvas, float x, float y, int color) {
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2f * unit);
        paint.setColor(color);
        Path path = new Path();
        path.moveTo(x - 5f * unit, y - 8f * unit);
        path.lineTo(x + 4f * unit, y);
        path.lineTo(x - 5f * unit, y + 8f * unit);
        canvas.drawPath(path, paint);
        paint.setStyle(Paint.Style.FILL);
    }

    private void createHitSpark(float x, float y, int color) {
        createBurst(x, y, color, 4, 70f * unit);
    }

    private void createExplosion(float x, float y, int color, float scale) {
        createBurst(x, y, color, (int) (16 * scale), 190f * unit * scale);
        Particle core = new Particle();
        core.x = x;
        core.y = y;
        core.radius = 8f * unit * scale;
        core.growth = 96f * unit * scale;
        core.life = 0.18f;
        core.maxLife = 0.18f;
        core.alpha = 210;
        core.color = WHITE;
        core.ring = true;
        core.stroke = 5f * unit * scale;
        particles.add(core);
        Particle ring = new Particle();
        ring.x = x;
        ring.y = y;
        ring.radius = 10f * unit * scale;
        ring.growth = 142f * unit * scale;
        ring.life = 0.42f;
        ring.maxLife = 0.42f;
        ring.alpha = 210;
        ring.color = color;
        ring.ring = true;
        ring.stroke = 4f * unit * scale;
        particles.add(ring);
    }

    private void createShockwave(float x, float y, int color) {
        Particle ring = new Particle();
        ring.x = x;
        ring.y = y;
        ring.radius = 24f * unit;
        ring.growth = height * 0.82f;
        ring.life = 0.48f;
        ring.maxLife = 0.48f;
        ring.alpha = 230;
        ring.color = color;
        ring.ring = true;
        ring.stroke = 7f * unit;
        particles.add(ring);
    }

    private void createBurst(float x, float y, int color, int count, float speed) {
        for (int i = 0; i < count; i++) {
            float angle = random.nextFloat() * 6.28318f;
            float v = speed * (0.28f + random.nextFloat() * 0.72f);
            Particle p = new Particle();
            p.x = x;
            p.y = y;
            p.vx = (float) Math.cos(angle) * v;
            p.vy = (float) Math.sin(angle) * v;
            p.radius = (1.6f + random.nextFloat() * 3.4f) * unit;
            p.life = 0.28f + random.nextFloat() * 0.42f;
            p.maxLife = p.life;
            p.alpha = 190 + random.nextInt(55);
            p.color = random.nextFloat() < 0.2f ? WHITE : color;
            p.drag = 2.4f;
            p.line = random.nextBoolean();
            particles.add(p);
        }
    }

    private void addFloatText(float x, float y, String value, int color) {
        FloatText text = new FloatText();
        text.x = x;
        text.y = y;
        text.text = value;
        text.color = color;
        text.life = 0.75f;
        text.maxLife = 0.75f;
        floatTexts.add(text);
    }

    private void addScreenShake(float time, float power) {
        shakeTime = Math.max(shakeTime, time);
        shakePower = Math.max(shakePower, power);
    }

    private void layoutButtons() {
        float cx = width * 0.5f;
        float buttonW = width * 0.72f;
        float buttonH = 48f * unit;
        setRect(startButton, cx, height * 0.58f, buttonW, buttonH);
        setRect(buffButton, cx, height * 0.655f, buttonW, buttonH);
        setRect(hangarButton, cx, height * 0.73f, buttonW, buttonH);
        setRect(leaderboardButton, cx, height * 0.805f, buttonW, buttonH);

        float left = width * 0.07f;
        float right = width * 0.93f;
        float rowH = 52f * unit;
        float gap = 7f * unit;
        setRectLTRB(coreButton, left, height * 0.22f, right, height * 0.22f + rowH);
        setRectLTRB(missileButton, left, coreButton.bottom + gap, right, coreButton.bottom + gap + rowH);
        setRectLTRB(droneButton, left, missileButton.bottom + gap, right, missileButton.bottom + gap + rowH);
        setRectLTRB(magnetButton, left, droneButton.bottom + gap, right, droneButton.bottom + gap + rowH);
        setRectLTRB(astraButton, left, magnetButton.bottom + gap * 1.4f, right, magnetButton.bottom + gap * 1.4f + rowH);
        setRectLTRB(starterButton, left, astraButton.bottom + gap, right, astraButton.bottom + gap + rowH);
        setRectLTRB(removeAdsButton, left, starterButton.bottom + gap, right, starterButton.bottom + gap + rowH);
        setRectLTRB(passButton, left, removeAdsButton.bottom + gap, right, removeAdsButton.bottom + gap + rowH);
        setRect(backButton, cx, height * 0.94f, width * 0.52f, 44f * unit);

        float skill = 66f * unit;
        setRect(skillButton, width * 0.13f, height * 0.88f, skill, skill);
        setRect(restoreDroneButton, width * 0.23f, height * 0.13f, width * 0.34f, 36f * unit);
        setRect(exitRunButton, width * 0.83f, height * 0.128f, width * 0.22f, 34f * unit);

        setRect(claimButton, cx, height * 0.43f, buttonW, buttonH);
        setRect(doubleButton, cx, height * 0.505f, buttonW, buttonH);
        setRect(reviveButton, cx, height * 0.58f, buttonW, buttonH);
        setRect(retryButton, cx, height * 0.67f, buttonW, buttonH);
        setRect(gameOverHangarButton, cx, height * 0.745f, buttonW, buttonH);
        setRect(gameOverLeaderboardButton, cx, height * 0.82f, buttonW, buttonH);
        setRect(leaderboardPrevButton, width * 0.195f, height * 0.86f, width * 0.25f, 42f * unit);
        setRect(leaderboardNextButton, width * 0.805f, height * 0.86f, width * 0.25f, 42f * unit);
        setRect(leaderboardBackButton, cx, height * 0.93f, width * 0.52f, 44f * unit);
    }

    private void setRect(RectF rect, float cx, float cy, float w, float h) {
        rect.set(cx - w * 0.5f, cy - h * 0.5f, cx + w * 0.5f, cy + h * 0.5f);
    }

    private void setRectLTRB(RectF rect, float left, float top, float right, float bottom) {
        rect.set(left, top, right, bottom);
    }

    private void seedStars() {
        for (int i = 0; i < starX.length; i++) {
            starX[i] = random.nextFloat() * Math.max(1, width);
            starY[i] = random.nextFloat() * Math.max(1, height);
            starSpeed[i] = (30f + random.nextFloat() * 145f) * unit;
            starSize[i] = (0.7f + random.nextFloat() * 2.1f) * unit;
        }
    }

    private static float clamp(float value, float min, float max) {
        return Math.max(min, Math.min(max, value));
    }

    private static float square(float value) {
        return value * value;
    }

    private static float distanceSquared(float ax, float ay, float bx, float by) {
        float dx = ax - bx;
        float dy = ay - by;
        return dx * dx + dy * dy;
    }

    private static final class Player {
        float x;
        float y;
        float targetX;
        float targetY;
        float radius = 18f;
        float hp;
        float maxHp;
        float invulnerable;
        float skillCharge;
        boolean droneActive;
        float droneTime;
    }

    private static final class Enemy {
        int type;
        float x;
        float y;
        float radius;
        float hp;
        float maxHp;
        float speed;
        float damage;
        float fireTimer;
        float phase;
        float age;
    }

    private static final class Bullet {
        float x;
        float y;
        float vx;
        float vy;
        float radius;
        float damage;
        float life;
        int color;
        boolean playerOwned;
        boolean missile;
        Enemy target;
        final float[] trailX = new float[14];
        final float[] trailY = new float[14];
        int trailIndex;
        int trailCount;

        void addTrail() {
            trailX[trailIndex] = x;
            trailY[trailIndex] = y;
            trailIndex = (trailIndex + 1) % trailX.length;
            trailCount = Math.min(trailCount + 1, trailX.length);
        }
    }

    private static final class Particle {
        float x;
        float y;
        float x2;
        float y2;
        float vx;
        float vy;
        float radius;
        float growth;
        float life;
        float maxLife;
        float alpha;
        float drag;
        float stroke;
        int color;
        boolean ring;
        boolean line;
        boolean beam;

        static Particle beam(float x1, float y1, float x2, float y2, int color, float life, float radius) {
            Particle p = new Particle();
            p.x = x1;
            p.y = y1;
            p.x2 = x2;
            p.y2 = y2;
            p.color = color;
            p.life = life;
            p.maxLife = life;
            p.radius = radius;
            p.beam = true;
            return p;
        }
    }

    private static final class Pickup {
        int type;
        float x;
        float y;
        float vx;
        float vy;
        int value;
        float life;
    }

    private static final class FloatText {
        float x;
        float y;
        String text;
        int color;
        float life;
        float maxLife;
    }
}
