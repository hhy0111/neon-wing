(() => {
  "use strict";

  const canvas = document.getElementById("game");
  const ctx = canvas.getContext("2d");
  const W = canvas.width;
  const H = canvas.height;
  const TAU = Math.PI * 2;
  const colors = {
    bg: "#05070f",
    cyan: "#52f4ff",
    blue: "#4078ff",
    green: "#63ff9a",
    magenta: "#ff4fd8",
    red: "#ff4056",
    orange: "#ff8b30",
    gold: "#ffdc5e",
    white: "#f6f7ff",
    panel: "rgba(8, 14, 29, 0.78)",
  };

  const imageSources = {
    backgrounds: {
      neon_orbit: {
        far: "assets/bg/neon_orbit_far.png",
        mid: "assets/bg/neon_orbit_mid.png",
        near: "assets/bg/neon_orbit_near.png",
      },
      scrap_belt: {
        far: "assets/bg/scrap_belt_far.png",
        mid: "assets/bg/scrap_belt_mid.png",
        near: "assets/bg/scrap_belt_near.png",
      },
      crimson_foundry: {
        far: "assets/bg/crimson_foundry_far.png",
        mid: "assets/bg/crimson_foundry_mid.png",
        near: "assets/bg/crimson_foundry_near.png",
      },
      frost_relay: {
        far: "assets/bg/frost_relay_far.png",
        mid: "assets/bg/frost_relay_mid.png",
        near: "assets/bg/frost_relay_near.png",
      },
      void_citadel: {
        far: "assets/bg/void_citadel_far.png",
        mid: "assets/bg/void_citadel_mid.png",
        near: "assets/bg/void_citadel_near.png",
      },
    },
    ships: {
      neon_wing: "assets/ships/player_neon_wing.png",
      astra: "assets/ships/player_astra.png",
    },
    enemies: {
      0: "assets/enemies/enemy_scout.png",
      1: "assets/enemies/enemy_striker.png",
      2: "assets/enemies/enemy_wraith.png",
      3: "assets/enemies/enemy_elite.png",
      4: "assets/enemies/supply_drone.png",
    },
    missiles: {
      homing_gold: "assets/missiles/missile_homing_gold.png",
      micro_cyan: "assets/missiles/missile_micro_cyan.png",
      cluster_orange: "assets/missiles/missile_cluster_orange.png",
      plasma_magenta: "assets/missiles/missile_plasma_magenta.png",
      emp_blue: "assets/missiles/missile_emp_blue.png",
      rail_white: "assets/missiles/missile_rail_white.png",
      nova_gold: "assets/missiles/missile_nova_gold.png",
    },
    ui: {},
    icons: {
      coin: "assets/icons/icon_coin.png",
      gem: "assets/icons/icon_gem.png",
      cash: "assets/icons/icon_cash.png",
      trophy: "assets/icons/icon_trophy.png",
      upgrade_chip: "assets/icons/icon_upgrade_chip.png",
      field_energy: "assets/icons/icon_field_energy.png",
      repair: "assets/icons/icon_repair.png",
    },
  };

  const shipSpriteProfiles = {
    neon_wing: { asset: "ship_neon_wing", width: 124, height: 142, anchorX: 0.5, anchorY: 0.61, hitR: 12 },
    raptor: { asset: "ship_neon_wing", width: 120, height: 138, anchorX: 0.5, anchorY: 0.61, hitR: 11, filter: "hue-rotate(38deg) saturate(1.35)" },
    astra: { asset: "ship_astra", width: 118, height: 146, anchorX: 0.5, anchorY: 0.6, hitR: 12 },
    bastion: { asset: "ship_neon_wing", width: 132, height: 146, anchorX: 0.5, anchorY: 0.61, hitR: 13, filter: "hue-rotate(160deg) saturate(1.2)" },
    seraph: { asset: "ship_astra", width: 120, height: 146, anchorX: 0.5, anchorY: 0.6, hitR: 12, filter: "hue-rotate(42deg) saturate(1.25)" },
    phantom: { asset: "ship_neon_wing", width: 122, height: 140, anchorX: 0.5, anchorY: 0.61, hitR: 11, filter: "grayscale(0.72) brightness(1.18)" },
    nova_x: { asset: "ship_astra", width: 128, height: 154, anchorX: 0.5, anchorY: 0.6, hitR: 13, filter: "hue-rotate(92deg) saturate(1.65) brightness(1.12)" },
  };

  const enemySpriteProfiles = {
    0: { asset: "enemy_0", heightScale: 3.2 },
    1: { asset: "enemy_1", heightScale: 3.35 },
    2: { asset: "enemy_2", heightScale: 3.35 },
    3: { asset: "enemy_3", heightScale: 3.55 },
    4: { asset: "enemy_4", heightScale: 3.25 },
  };

  const imageAssets = {};
  const assetStats = { total: 0, loaded: 0, failed: 0 };

  const missileProfiles = {
    homing_gold: { label: "호밍 골드", color: colors.gold, r: 7, speed: 470, turn: 3.2, damage: 1, reload: 1, splash: 30 },
    micro_cyan: { label: "마이크로 시안", color: colors.cyan, r: 5.2, speed: 610, turn: 4.8, damage: 0.58, reload: 0.62, splash: 18 },
    plasma_magenta: { label: "플라즈마", color: colors.magenta, r: 9.5, speed: 420, turn: 2.7, damage: 1.65, reload: 1.28, splash: 62 },
    cluster_orange: { label: "클러스터", color: colors.orange, r: 8.5, speed: 430, turn: 2.9, damage: 1.1, reload: 1.15, splash: 74 },
    emp_blue: { label: "EMP 블루", color: colors.blue, r: 7.6, speed: 455, turn: 3.3, damage: 0.92, reload: 1.05, splash: 52 },
    rail_white: { label: "레일 화이트", color: colors.white, r: 6.5, speed: 680, turn: 2.1, damage: 1.35, reload: 1.2, splash: 18, pierce: 1 },
    nova_gold: { label: "노바 골드", color: colors.gold, r: 10.5, speed: 380, turn: 2.5, damage: 1.45, reload: 1.55, splash: 92 },
  };

  const ships = {
    neon_wing: {
      id: "neon_wing",
      name: "Neon Wing",
      label: "네온 윙",
      role: "균형형",
      missile: "homing_gold",
      accent: colors.cyan,
      passive: "코인 획득 +5%",
      bonus: { coin: 0.05 },
      unlock: "기본 지급",
      price: {},
    },
    raptor: {
      id: "raptor",
      name: "Raptor",
      label: "랩터",
      role: "연사/스피드",
      missile: "micro_cyan",
      accent: colors.blue,
      passive: "이동 속도 +8%",
      bonus: { speed: 0.08 },
      unlock: "최고 점수 250 이상",
      price: { coins: 1200 },
    },
    astra: {
      id: "astra",
      name: "Astra",
      label: "아스트라",
      role: "고화력 프리미엄",
      missile: "plasma_magenta",
      accent: colors.magenta,
      passive: "미사일 피해 +8%",
      bonus: { missileDamage: 0.08, hp: 18 },
      unlock: "보석 구매 또는 스타터 패키지",
      price: { gems: 120 },
    },
    bastion: {
      id: "bastion",
      name: "Bastion",
      label: "바스티온",
      role: "생존/분열",
      missile: "cluster_orange",
      accent: colors.orange,
      passive: "최대 HP +18%",
      bonus: { hpMultiplier: 0.18 },
      unlock: "최고 점수 650 이상",
      price: { coins: 2400 },
    },
    seraph: {
      id: "seraph",
      name: "Seraph",
      label: "세라프",
      role: "제어/EMP",
      missile: "emp_blue",
      accent: colors.cyan,
      passive: "보호막 개조 확률 증가",
      bonus: { defenseBias: 0.18 },
      unlock: "최고 점수 1100 이상",
      price: { gems: 80 },
    },
    phantom: {
      id: "phantom",
      name: "Phantom",
      label: "팬텀",
      role: "관통 저격",
      missile: "rail_white",
      accent: colors.white,
      passive: "피격 무적 +20%",
      bonus: { invuln: 0.2 },
      unlock: "최고 점수 1600 이상",
      price: { coins: 4800 },
    },
    nova_x: {
      id: "nova_x",
      name: "Nova X",
      label: "노바 X",
      role: "궁극기형",
      missile: "nova_gold",
      accent: colors.gold,
      passive: "노바 충전 +15%",
      bonus: { novaCharge: 0.15 },
      unlock: "최고 점수 2500 이상",
      price: { coins: 9000, gems: 180 },
    },
  };

  const shipOrder = ["neon_wing", "raptor", "astra", "bastion", "seraph", "phantom", "nova_x"];

  const runUpgradePool = [
    { id: "up_missile_damage", label: "고폭 탄두", desc: "미사일 피해 +28%, 폭발광 강화", rarity: "일반", max: 5, tags: ["missile"] },
    { id: "up_missile_reload", label: "고속 장전기", desc: "미사일 재장전 -18%, 즉시 재발사", rarity: "일반", max: 4, tags: ["missile"] },
    { id: "up_missile_extra", label: "다연장 포드", desc: "미사일 +1발, 측면 포드 추가", rarity: "희귀", max: 3, tags: ["missile"] },
    { id: "up_missile_split", label: "분열 탄두", desc: "명중 시 추적 파편과 연쇄 폭발", rarity: "희귀", max: 3, tags: ["missile"] },
    { id: "up_missile_homing", label: "추적 코어", desc: "미사일 속도/선회/궤적 강화", rarity: "희귀", max: 3, tags: ["missile"] },
    { id: "up_missile_barrage", label: "미사일 폭풍", desc: "발사 때 소형 보조 미사일 세례", rarity: "특급", max: 2, tags: ["missile"] },
    { id: "up_missile_supernova", label: "초신성 탄두", desc: "명중 지점에 광역 충격파 추가", rarity: "특급", max: 2, tags: ["missile"] },
    { id: "up_core_damage", label: "코어 증폭", desc: "기본탄 피해 +16%", rarity: "일반", max: 5, tags: ["core"] },
    { id: "up_core_side", label: "사이드 빔", desc: "기본탄 보조 사격 추가", rarity: "희귀", max: 2, tags: ["core"] },
    { id: "up_drone_wing", label: "윙 드론", desc: "드론 복구 + 지속시간 증가", rarity: "일반", max: 3, tags: ["drone"] },
    { id: "up_shield_regen", label: "재생 보호막", desc: "주기적으로 피해 1회 방어", rarity: "희귀", max: 2, tags: ["defense"] },
    { id: "up_nova_charge", label: "노바 축전기", desc: "노바 충전 속도 +25%", rarity: "일반", max: 4, tags: ["nova"] },
    { id: "up_signature", label: "전용 병장", desc: "현재 기체 미사일 특성 강화", rarity: "특급", max: 2, tags: ["ship", "missile"] },
  ];

  const defaultSave = {
    coins: 450,
    gems: 15,
    best: 0,
    core: 1,
    missile: 1,
    drone: 1,
    magnet: 1,
    selectedShip: "neon_wing",
    ships: {
      neon_wing: true,
      raptor: false,
      astra: false,
      bastion: false,
      seraph: false,
      phantom: false,
      nova_x: false,
    },
    astra: false,
    removeAds: false,
    supplyPass: false,
  };

  const saveKey = "neon-wing-web-save-v1";
  const leaderboardKey = "neon-wing-web-leaderboard-v1";
  const save = loadSave();
  const leaderboard = loadLeaderboard();
  const buttons = [];
  const pointer = { down: false, dragging: false, x: W / 2, y: H * 0.78 };
  const keys = new Set();
  const rng = mulberry32(3092026);

  const state = {
    mode: "splash",
    time: 0,
    score: 0,
    kills: 0,
    pendingCoins: 0,
    pendingGems: 0,
    rewardClaimed: false,
    doubleRewardClaimed: false,
    usedRevive: false,
    runExited: false,
    startBuff: false,
    status: "준비 완료",
    statusTimer: 2,
    flash: 0,
    flashColor: colors.white,
    shake: 0,
    shakePower: 0,
    stars: [],
    run: makeRunState(),
    runId: "",
    lastRank: null,
    selectedShopShip: save.selectedShip,
    leaderboardPage: 0,
    leaderboardBackMode: "title",
    player: makePlayer(),
    enemies: [],
    playerBullets: [],
    enemyBullets: [],
    particles: [],
    pickups: [],
    texts: [],
    timers: {
      shot: 0,
      missile: 0.5,
      laser: 3,
      spawn: 0.25,
      elite: 38,
      trail: 0,
      supply: 18,
    },
  };

  loadImageAssets();
  seedStars();
  bindInput();
  render();
  requestAnimationFrame(loop);

  function makePlayer() {
    return {
      x: W / 2,
      y: H * 0.8,
      tx: W / 2,
      ty: H * 0.8,
      r: 18,
      hitR: currentShipHitRadius(),
      hp: 110,
      maxHp: 110,
      invuln: 0,
      nova: 0.2,
      drone: true,
      droneTime: 54,
    };
  }

  function makeRunState() {
    return {
      fieldEnergy: 0,
      fieldEnergyNeeded: 100,
      upgradeLevel: 0,
      stacks: {},
      choices: [],
      lastUpgrade: "",
      chips: 0,
      shieldReady: 0,
    };
  }

  function loadSave() {
    try {
      const loaded = JSON.parse(localStorage.getItem(saveKey) || "{}");
      const merged = {
        ...defaultSave,
        ...loaded,
        ships: { ...defaultSave.ships, ...(loaded.ships || {}) },
      };
      if (merged.astra) merged.ships.astra = true;
      merged.ships.neon_wing = true;
      if (!ships[merged.selectedShip] || !merged.ships[merged.selectedShip]) merged.selectedShip = "neon_wing";
      return merged;
    } catch {
      return { ...defaultSave };
    }
  }

  function loadLeaderboard() {
    try {
      const loaded = JSON.parse(localStorage.getItem(leaderboardKey) || "[]");
      if (!Array.isArray(loaded)) return [];
      return normalizeLeaderboard(loaded);
    } catch {
      return [];
    }
  }

  function persist() {
    localStorage.setItem(saveKey, JSON.stringify(save));
  }

  function persistLeaderboard() {
    localStorage.setItem(leaderboardKey, JSON.stringify(leaderboard));
  }

  function upgradeCost(level, base) {
    return base + (level - 1) * (level - 1) * 55 + level * 90;
  }

  function setStatus(text) {
    state.status = text;
    state.statusTimer = 2.2;
  }

  function startRun(withBuff = false) {
    const ship = currentShip();
    state.mode = "playing";
    state.runId = `${Date.now()}-${Math.floor(rng() * 1000000)}`;
    state.lastRank = null;
    state.time = 0;
    state.score = 0;
    state.kills = 0;
    state.pendingCoins = 0;
    state.pendingGems = 0;
    state.rewardClaimed = false;
    state.doubleRewardClaimed = false;
    state.usedRevive = false;
    state.runExited = false;
    state.startBuff = withBuff;
    state.run = makeRunState();
    state.enemies.length = 0;
    state.playerBullets.length = 0;
    state.enemyBullets.length = 0;
    state.particles.length = 0;
    state.pickups.length = 0;
    state.texts.length = 0;
    state.timers.shot = 0;
    state.timers.missile = 0.45;
    state.timers.laser = 3.5;
    state.timers.spawn = 0.18;
    state.timers.elite = 38;
    state.timers.trail = 0;
    state.timers.supply = 18;
    state.player = makePlayer();
    state.player.maxHp = 96 + save.core * 9 + (ship.bonus.hp || 0) + (withBuff ? 22 : 0);
    if (ship.bonus.hpMultiplier) state.player.maxHp = Math.round(state.player.maxHp * (1 + ship.bonus.hpMultiplier));
    state.player.hp = state.player.maxHp;
    state.player.invuln = (withBuff ? 2.4 : 1.2) * (1 + (ship.bonus.invuln || 0));
    state.player.nova = withBuff ? 0.65 : 0.2;
    state.player.droneTime = 46 + save.drone * 7;
    screenFlash(ship.accent, 0.18);
    setStatus(withBuff ? `${ship.label} 시작 버프 적용` : `${ship.label} 출격`);
  }

  function endRun(reason = "fail") {
    state.mode = "gameover";
    state.runExited = reason === "exit";
    const coinBonus = 1 + (currentShip().bonus.coin || 0);
    state.pendingCoins = Math.round(Math.max(25, 45 + Math.floor(state.score / 12) + state.kills * 3 + Math.floor(state.time * 1.5)) * coinBonus);
    if (save.supplyPass) state.pendingCoins = Math.round(state.pendingCoins * 1.12);
    state.pendingGems = state.score >= 1400 ? 1 : 0;
    save.best = Math.max(save.best, state.score);
    state.lastRank = submitLeaderboardScore();
    persist();
    if (reason === "exit") {
      setStatus(state.lastRank ? `전투 종료 / 랭킹 ${state.lastRank}위` : "전투 종료");
    } else {
      setStatus(state.lastRank ? `임무 실패 / 랭킹 ${state.lastRank}위` : "임무 실패");
    }
  }

  function claimReward(multiplier) {
    if (state.rewardClaimed) return;
    save.coins += state.pendingCoins * multiplier;
    save.gems += state.pendingGems;
    state.rewardClaimed = true;
    persist();
    setStatus(multiplier > 1 ? "2배 보상을 받았습니다" : "보상을 받았습니다");
  }

  function submitLeaderboardScore() {
    if (state.score <= 0) return null;
    const ship = currentShip();
    const entry = {
      id: state.runId || `${Date.now()}-${Math.floor(rng() * 1000000)}`,
      score: state.score,
      kills: state.kills,
      time: round(state.time),
      ship: save.selectedShip,
      shipName: ship.label,
      phase: currentPhase().label,
      at: new Date().toISOString(),
    };
    const previousIndex = leaderboard.findIndex((item) => item.id === entry.id);
    if (previousIndex >= 0) leaderboard.splice(previousIndex, 1);
    leaderboard.push(entry);
    normalizeLeaderboard(leaderboard);
    persistLeaderboard();
    const rank = leaderboard.findIndex((item) => item.id === entry.id);
    return rank >= 0 ? rank + 1 : null;
  }

  function normalizeLeaderboard(entries) {
    entries.sort((a, b) => {
      if ((b.score || 0) !== (a.score || 0)) return (b.score || 0) - (a.score || 0);
      if ((b.kills || 0) !== (a.kills || 0)) return (b.kills || 0) - (a.kills || 0);
      return String(a.at || "").localeCompare(String(b.at || ""));
    });
    entries.splice(50);
    return entries;
  }

  function mockAd(placement, cb) {
    setStatus(`모의 광고: ${adLabel(placement)}`);
    setTimeout(cb, 120);
  }

  function adLabel(placement) {
    const labels = {
      START_BUFF: "시작 버프",
      RESTORE_DRONE: "드론 복구",
      DOUBLE_REWARD: "보상 2배",
      REVIVE: "부활",
    };
    return labels[placement] || "보상";
  }

  function mockPurchase(product) {
    if (product === "starter") {
      save.coins += 1800;
      save.gems += 80;
    } else if (product === "remove_ads") {
      save.removeAds = true;
    } else if (product === "supply_pass") {
      save.supplyPass = true;
      save.gems += 40;
    } else if (product === "astra") {
      save.astra = true;
      save.ships.astra = true;
    }
    persist();
    setStatus(`모의 구매 지급: ${productLabel(product)}`);
  }

  function productLabel(product) {
    const labels = {
      starter: "스타터 패키지",
      remove_ads: "광고 제거",
      supply_pass: "월간 보급 패스",
      astra: "아스트라 기체",
    };
    return labels[product] || "상품";
  }

  function update(dt) {
    updateBackground(dt);
    updateParticles(dt);
    state.statusTimer = Math.max(0, state.statusTimer - dt);
    if (state.mode !== "playing") return;

    state.time += dt;
    state.shake = Math.max(0, state.shake - dt);
    const p = state.player;
    const ship = currentShip();

    const moveSpeed = 260 * (1 + (ship.bonus.speed || 0));
    if (keys.has("ArrowLeft")) p.tx -= moveSpeed * dt;
    if (keys.has("ArrowRight")) p.tx += moveSpeed * dt;
    if (keys.has("ArrowUp")) p.ty -= moveSpeed * dt;
    if (keys.has("ArrowDown")) p.ty += moveSpeed * dt;
    p.tx = clamp(p.tx, 24, W - 24);
    p.ty = clamp(p.ty, H * 0.18, H - 64);
    const dx = p.tx - p.x;
    const dy = p.ty - p.y;
    const dist = Math.hypot(dx, dy);
    if (dist > 0.4) {
      const step = Math.min(dist, moveSpeed * dt);
      p.x += (dx / dist) * step;
      p.y += (dy / dist) * step;
    }
    p.invuln = Math.max(0, p.invuln - dt);
    const novaBonus = getRunStack("up_nova_charge") * 0.25 + (ship.bonus.novaCharge || 0);
    p.nova = Math.min(1, p.nova + (dt * (1 + novaBonus)) / Math.max(28, 43 - save.core));
    updateRunShield(dt);

    if (p.drone) {
      p.droneTime -= dt;
      if (p.droneTime <= 0) {
        p.drone = false;
        explosion(p.x - 34, p.y + 18, colors.cyan, 0.8);
        explosion(p.x + 34, p.y + 18, colors.cyan, 0.8);
        setStatus("드론 배터리 소진");
      }
    }

    state.timers.shot -= dt;
    state.timers.missile -= dt;
    state.timers.laser -= dt;
    state.timers.spawn -= dt;
    state.timers.elite -= dt;
    state.timers.trail -= dt;
    state.timers.supply -= dt;

    if (state.timers.trail <= 0) {
      engineTrail(p.x, p.y + 38, ship.accent);
      state.timers.trail = 0.045;
    }

    if (state.timers.shot <= 0) {
      fireShots();
      const coreSpeed = 1 + getRunStack("up_core_damage") * 0.04;
      state.timers.shot = Math.max(0.06, (0.17 - save.core * 0.006) / coreSpeed);
    }
    if (state.timers.missile <= 0) {
      fireMissiles();
      const profile = missileProfiles[ship.missile];
      const reloadBonus = 1 + getRunStack("up_missile_reload") * 0.2 + getRunStack("up_missile_barrage") * 0.05;
      state.timers.missile = Math.max(0.22, ((1.03 - save.missile * 0.032) * profile.reload) / reloadBonus);
    }
    if (state.timers.laser <= 0) {
      fireLaser();
      state.timers.laser = Math.max(5.6, 7.4 - save.core * 0.08);
    }
    if (state.timers.spawn <= 0) {
      spawnWave(false);
      state.timers.spawn = Math.max(0.26, 1.0 - state.time * 0.0065) + rng() * 0.18;
    }
    if (state.timers.elite <= 0) {
      spawnWave(true);
      state.timers.elite = 44 + rng() * 14;
    }
    if (state.timers.supply <= 0) {
      spawnSupplyDrone();
      state.timers.supply = 34 + rng() * 18;
    }

    updateBullets(dt);
    updateEnemies(dt);
    updatePickups(dt);
    collide();
    trim(state.playerBullets, 190);
    trim(state.enemyBullets, 150);
    trim(state.enemies, 30);
    trim(state.particles, 520);
    trim(state.pickups, 130);
    trim(state.texts, 32);
  }

  function updateBackground(dt) {
    for (const star of state.stars) {
      star.y += star.speed * dt;
      if (star.y > H + 8) {
        star.y = -8;
        star.x = rng() * W;
      }
    }
  }

  function fireShots() {
    const p = state.player;
    const ship = currentShip();
    const coreBonus = 1 + getRunStack("up_core_damage") * 0.16;
    const damage = (14 + save.core * 2.4) * (state.startBuff ? 1.1 : 1) * coreBonus;
    muzzleFlash(p.x, p.y - 38, colors.cyan, 0.75);
    addBullet(state.playerBullets, p.x, p.y - 36, 0, -720, 4.2, damage, colors.cyan);
    if (save.core >= 2) {
      addBullet(state.playerBullets, p.x - 16, p.y - 26, -15, -670, 3.6, damage * 0.72, colors.blue);
      addBullet(state.playerBullets, p.x + 16, p.y - 26, 15, -670, 3.6, damage * 0.72, colors.blue);
    }
    if (getRunStack("up_core_side") > 0) {
      const stacks = getRunStack("up_core_side");
      addBullet(state.playerBullets, p.x - 25, p.y - 18, -78 - stacks * 14, -620, 3.5, damage * 0.58, ship.accent);
      addBullet(state.playerBullets, p.x + 25, p.y - 18, 78 + stacks * 14, -620, 3.5, damage * 0.58, ship.accent);
    }
    if (p.drone) {
      const sideDamage = damage * (0.34 + save.drone * 0.025);
      addBullet(state.playerBullets, p.x - 42, p.y + 8, -8, -560, 3.2, sideDamage, colors.white);
      addBullet(state.playerBullets, p.x + 42, p.y + 8, 8, -560, 3.2, sideDamage, colors.white);
    }
  }

  function fireMissiles() {
    const target = nearestEnemy();
    if (!target) return;
    const ship = currentShip();
    const profile = missileProfiles[ship.missile];
    const damageStacks = getRunStack("up_missile_damage");
    const homingStacks = getRunStack("up_missile_homing");
    const supernovaStacks = getRunStack("up_missile_supernova");
    const barrageStacks = getRunStack("up_missile_barrage");
    const permanentTier = Math.max(0, save.missile - 1);
    const damageBonus = 1 + damageStacks * 0.32 + getRunStack("up_signature") * 0.22 + supernovaStacks * 0.15 + (ship.bonus.missileDamage || 0);
    const baseDamage = (36 + save.missile * 12) * profile.damage * damageBonus;
    const extra = getRunStack("up_missile_extra");
    const launchCount = (ship.missile === "micro_cyan" ? 2 : 1) + extra + (save.missile >= 3 ? 1 : 0) + (save.missile >= 6 ? 1 : 0);
    const powerTier = damageStacks + extra + getRunStack("up_missile_split") + homingStacks + supernovaStacks + barrageStacks + Math.min(8, permanentTier);
    for (let i = 0; i < launchCount; i++) {
      const spread = (i - (launchCount - 1) / 2) * (18 + Math.min(10, extra * 3));
      const damageScale = i === 0 ? 1 : Math.max(0.58, 0.9 - extra * 0.05);
      addMissile(state.player.x + spread, state.player.y - 12, target, baseDamage * damageScale, ship.missile, {
        powerTier,
        visualScale: 1 + Math.min(0.85, powerTier * 0.055),
        speedBonus: homingStacks * 42 + permanentTier * 14,
        turnBonus: homingStacks * 0.7 + permanentTier * 0.1,
        splashBonus: supernovaStacks * 42 + damageStacks * 7 + permanentTier * 6,
      });
      muzzleFlash(state.player.x + spread, state.player.y - 20, profile.color, 0.88 + powerTier * 0.035);
    }
    if (barrageStacks > 0 || save.missile >= 5) {
      const miniCount = barrageStacks * 2 + Math.min(2, extra) + (save.missile >= 5 ? 2 : 0);
      for (let i = 0; i < miniCount; i++) {
        const side = i % 2 === 0 ? -1 : 1;
        const lane = Math.ceil((i + 1) / 2);
        const spread = side * (34 + lane * 10);
        addMissile(state.player.x + spread, state.player.y + 2 + lane * 5, target, baseDamage * 0.34, ship.missile, {
          mini: true,
          powerTier,
          visualScale: 0.64,
          speedBonus: 70 + homingStacks * 22 + permanentTier * 10,
          turnBonus: 1.2,
          splashBonus: 12 + supernovaStacks * 12 + permanentTier * 4,
        });
        muzzleFlash(state.player.x + spread, state.player.y - 4, profile.color, 0.52);
      }
    }
  }

  function fireLaser() {
    if (!state.enemies.length) return;
    const x = state.player.x;
    const damage = 52 + save.core * 10;
    for (let i = state.enemies.length - 1; i >= 0; i--) {
      const e = state.enemies[i];
      if (Math.abs(e.x - x) < e.r + 11) {
        e.hp -= damage;
        burst(e.x, e.y, colors.cyan, 8, 90);
        addText(e.x, e.y - e.r, "레이저", colors.cyan);
        if (e.hp <= 0) killEnemy(i);
      }
    }
    state.particles.push({ beam: true, x, y: state.player.y - 48, x2: x, y2: -40, color: colors.cyan, life: 0.22, max: 0.22, r: 16 });
  }

  function useNova() {
    if (state.mode !== "playing") return;
    if (state.player.nova < 1) {
      setStatus("노바 충전 중");
      return;
    }
    state.player.nova = 0;
    state.enemyBullets.length = 0;
    const damage = 165 + save.core * 18;
    for (let i = state.enemies.length - 1; i >= 0; i--) {
      const e = state.enemies[i];
      e.hp -= damage;
      explosion(e.x, e.y, colors.gold, 0.85);
      if (e.hp <= 0) killEnemy(i);
    }
    shockwave(state.player.x, state.player.y, colors.gold);
    shake(0.2, 7);
    setStatus("노바 발동");
  }

  function spawnWave(elite) {
    if (elite) {
      const e = makeEnemy(3, W * (0.24 + rng() * 0.52), -54);
      e.hp *= 2.2 + state.time / 120;
      e.maxHp = e.hp;
      state.enemies.push(e);
      setStatus("엘리트 접근");
      return;
    }
    const count = 1
      + (state.time > 25 ? 1 : 0)
      + (state.time > 55 ? Math.floor(rng() * 2) : 0)
      + (state.time > 95 ? 1 : 0)
      + (state.time > 145 ? Math.floor(rng() * 2) : 0);
    for (let i = 0; i < count; i++) {
      const roll = rng() * 100;
      const type = roll < 52 ? 0 : roll < 78 ? 1 : roll < 93 ? 2 : 3;
      state.enemies.push(makeEnemy(type, W * (0.11 + rng() * 0.78), -rng() * 80 - 24));
    }
  }

  function spawnSupplyDrone() {
    const e = makeEnemy(4, W * (0.18 + rng() * 0.64), -46);
    state.enemies.push(e);
    setStatus("보급 드론 포착");
  }

  function makeEnemy(type, x, y) {
    const difficulty = 1 + state.time * 0.015 + state.score * 0.00014;
    const presets = [
      { r: 15, hp: 24, speed: 130, damage: 14 },
      { r: 18, hp: 48, speed: 92, damage: 12 },
      { r: 25, hp: 128, speed: 68, damage: 22 },
      { r: 31, hp: 230, speed: 58, damage: 20 },
      { r: 19, hp: 76, speed: 96, damage: 0 },
    ];
    const p = presets[type];
    return {
      type,
      x,
      y,
      r: p.r,
      hp: p.hp * difficulty,
      maxHp: p.hp * difficulty,
      speed: p.speed,
      damage: p.damage,
      fire: 0.5 + rng() * 1.2,
      phase: rng() * TAU,
      age: 0,
    };
  }

  function addBullet(list, x, y, vx, vy, r, damage, color) {
    list.push({ x, y, vx, vy, r, damage, color, life: 4, missile: false });
  }

  function addMissile(x, y, target, damage, missileType = "homing_gold", options = {}) {
    const profile = missileProfiles[missileType] || missileProfiles.homing_gold;
    state.playerBullets.push({
      x,
      y,
      vx: (rng() - 0.5) * 70,
      vy: -340 - rng() * 60,
      r: profile.r * (options.mini ? 0.72 : 1),
      damage,
      color: profile.color,
      life: 4,
      missile: true,
      missileType,
      speed: profile.speed + (options.speedBonus || 0),
      turn: profile.turn + (options.turnBonus || 0),
      splash: profile.splash + (options.splashBonus || 0),
      pierce: profile.pierce || 0,
      visualScale: options.visualScale || 1,
      powerTier: options.powerTier || 0,
      mini: Boolean(options.mini),
      target,
      trail: [],
    });
  }

  function updateBullets(dt) {
    for (let i = state.playerBullets.length - 1; i >= 0; i--) {
      const b = state.playerBullets[i];
      b.life -= dt;
      if (b.missile && b.target && state.enemies.includes(b.target)) {
        const dx = b.target.x - b.x;
        const dy = b.target.y - b.y;
        const len = Math.max(1, Math.hypot(dx, dy));
        b.vx += ((dx / len) * b.speed - b.vx) * Math.min(1, dt * b.turn);
        b.vy += ((dy / len) * b.speed - b.vy) * Math.min(1, dt * b.turn);
        b.trail.push({ x: b.x, y: b.y });
        const trailMax = Math.min(30, 14 + (b.powerTier || 0) * 2);
        if (b.trail.length > trailMax) b.trail.shift();
      }
      b.x += b.vx * dt;
      b.y += b.vy * dt;
      if (b.life <= 0 || b.y < -80 || b.y > H + 80 || b.x < -80 || b.x > W + 80) state.playerBullets.splice(i, 1);
    }
    for (let i = state.enemyBullets.length - 1; i >= 0; i--) {
      const b = state.enemyBullets[i];
      b.life -= dt;
      b.x += b.vx * dt;
      b.y += b.vy * dt;
      if (b.life <= 0 || b.y > H + 80 || b.x < -80 || b.x > W + 80) state.enemyBullets.splice(i, 1);
    }
  }

  function updateEnemies(dt) {
    for (let i = state.enemies.length - 1; i >= 0; i--) {
      const e = state.enemies[i];
      e.age += dt;
      e.y += e.speed * dt;
      e.x += Math.sin(e.age * (1.4 + e.type * 0.3) + e.phase) * W * 0.035 * dt;
      e.x = clamp(e.x, e.r + 8, W - e.r - 8);
      e.fire -= dt;
      if (e.fire <= 0 && e.y > 40 && e.y < H * 0.72) {
        fireEnemy(e);
        e.fire = e.type === 3 ? 0.78 : e.type === 2 ? 1.45 : 1.1 + rng() * 0.45;
      }
      if (e.y > H + 80) state.enemies.splice(i, 1);
    }
  }

  function fireEnemy(e) {
    if (e.type === 4) return;
    const dx = state.player.x - e.x;
    const dy = state.player.y - e.y;
    const len = Math.max(1, Math.hypot(dx, dy));
    const speed = e.type === 3 ? 236 : 190;
    addBullet(state.enemyBullets, e.x, e.y + e.r * 0.6, (dx / len) * speed, (dy / len) * speed, 5.8, e.damage, e.type === 3 ? colors.magenta : colors.red);
    if (e.type === 3) {
      addBullet(state.enemyBullets, e.x - 10, e.y + e.r * 0.4, -55, 198, 5.2, e.damage * 0.75, colors.orange);
      addBullet(state.enemyBullets, e.x + 10, e.y + e.r * 0.4, 55, 198, 5.2, e.damage * 0.75, colors.orange);
    }
  }

  function updatePickups(dt) {
    for (let i = state.pickups.length - 1; i >= 0; i--) {
      const p = state.pickups[i];
      const dx = state.player.x - p.x;
      const dy = state.player.y - p.y;
      const dist = Math.max(1, Math.hypot(dx, dy));
      const magnetRadius = pickupMagnetRadius();
      if (dist < magnetRadius) {
        p.vx += (dx / dist) * 300 * dt;
        p.vy += (dy / dist) * 530 * dt;
      }
      p.x += p.vx * dt;
      p.y += p.vy * dt;
      p.life -= dt;
      if (dist < state.player.r + 12) {
        collectPickup(p);
        state.pickups.splice(i, 1);
      } else if (p.life <= 0 || p.y > H + 40) {
        state.pickups.splice(i, 1);
      }
    }
  }

  function pickupMagnetRadius() {
    return 72 + (save.magnet || 1) * 20 + save.drone * 4;
  }

  function collectPickup(p) {
    if (p.type === "energy") {
      gainFieldEnergy(p.value, "에너지 셀");
      addText(state.player.x, state.player.y - 42, `개조 +${p.value}`, colors.cyan);
      burst(p.x, p.y, colors.cyan, 8, 80);
      return;
    }
    if (p.type === "chip") {
      state.run.chips++;
      addText(state.player.x, state.player.y - 42, "개조 칩", colors.magenta);
      burst(p.x, p.y, colors.magenta, 16, 120);
      openUpgradeChoice("개조 칩 획득", true);
      return;
    }
    if (p.type === "repair") {
      state.player.hp = Math.min(state.player.maxHp, state.player.hp + p.value);
      addText(state.player.x, state.player.y - 42, `수리 +${p.value}`, colors.cyan);
      burst(p.x, p.y, colors.cyan, 10, 90);
      return;
    }
    if (p.type === "cash") {
      state.score += p.value;
      addText(state.player.x, state.player.y - 42, `+${p.value}`, colors.green);
      burst(p.x, p.y, colors.green, 10, 95);
      return;
    }
    state.score += p.value;
    addText(state.player.x, state.player.y - 42, `+${p.value}`, colors.gold);
  }

  function updateParticles(dt) {
    state.flash = Math.max(0, state.flash - dt * 1.9);
    for (let i = state.particles.length - 1; i >= 0; i--) {
      const p = state.particles[i];
      p.life -= dt;
      p.x += (p.vx || 0) * dt;
      p.y += (p.vy || 0) * dt;
      p.vx = (p.vx || 0) * (1 - Math.min(0.7, dt * (p.drag || 2.2)));
      p.vy = (p.vy || 0) * (1 - Math.min(0.7, dt * (p.drag || 2.2)));
      p.r += (p.growth || 0) * dt;
      if (p.life <= 0) state.particles.splice(i, 1);
    }
    for (let i = state.texts.length - 1; i >= 0; i--) {
      const t = state.texts[i];
      t.life -= dt;
      t.y -= 34 * dt;
      if (t.life <= 0) state.texts.splice(i, 1);
    }
  }

  function collide() {
    for (let bi = state.playerBullets.length - 1; bi >= 0; bi--) {
      const b = state.playerBullets[bi];
      for (let ei = state.enemies.length - 1; ei >= 0; ei--) {
        const e = state.enemies[ei];
        if (dist2(b.x, b.y, e.x, e.y) <= sq(b.r + e.r)) {
          e.hp -= b.damage;
          burst(b.x, b.y, b.color, 4, 70);
          let consumeBullet = true;
          if (b.missile) {
            consumeBullet = applyMissileImpact(b, e);
          }
          if (consumeBullet) {
            state.playerBullets.splice(bi, 1);
          } else {
            b.target = nearestEnemy(e);
          }
          if (e.hp <= 0) killEnemy(ei);
          break;
        }
      }
    }
    if (state.player.invuln <= 0) {
      for (let i = state.enemyBullets.length - 1; i >= 0; i--) {
        const b = state.enemyBullets[i];
        if (dist2(b.x, b.y, state.player.x, state.player.y) <= sq(b.r + playerHitRadius())) {
          state.enemyBullets.splice(i, 1);
          damagePlayer(b.damage);
          break;
        }
      }
    }
    if (state.player.invuln <= 0) {
      for (let i = state.enemies.length - 1; i >= 0; i--) {
        const e = state.enemies[i];
        if (dist2(e.x, e.y, state.player.x, state.player.y) <= sq(e.r + playerHitRadius())) {
          damagePlayer(e.damage * 1.4);
          explosion(e.x, e.y, colors.red, 0.7);
          state.enemies.splice(i, 1);
          break;
        }
      }
    }
  }

  function damagePlayer(amount) {
    const p = state.player;
    if (state.run.shieldReady > 0) {
      state.run.shieldReady--;
      p.invuln = 0.72;
      shieldBurst(p.x, p.y, currentShip().accent);
      setStatus("보호막 방어");
      return;
    }
    p.hp -= amount;
    p.invuln = 1.05;
    burst(p.x, p.y, colors.red, 14, 130);
    shake(0.16, 5);
    if (p.drone && rng() < 0.16) {
      p.drone = false;
      explosion(p.x + (rng() < 0.5 ? -34 : 34), p.y + 18, colors.cyan, 0.75);
      setStatus("드론 파괴");
    }
    if (p.hp <= 0) {
      explosion(p.x, p.y, colors.cyan, 1.35);
      shake(0.42, 10);
      endRun();
    }
  }

  function killEnemy(index) {
    const e = state.enemies.splice(index, 1)[0];
    if (!e) return;
    state.kills++;
    const value = e.type === 4 ? 18 : e.type === 3 ? 95 : e.type === 2 ? 46 : e.type === 1 ? 32 : 22;
    state.score += value;
    const boomColor = e.type === 4 ? colors.cyan : e.type === 3 ? colors.magenta : colors.orange;
    explosion(e.x, e.y, boomColor, e.type === 3 ? 1.05 : 0.72);
    addText(e.x, e.y - e.r, `+${value}`, colors.gold);
    gainFieldEnergy(e.type === 4 ? 32 : e.type === 3 ? 52 : e.type === 2 ? 30 : e.type === 1 ? 22 : 16, "처치");
    const count = e.type === 4 ? 4 : e.type === 3 ? 8 : e.type === 2 ? 5 : e.type === 1 ? 4 : 3;
    for (let i = 0; i < count; i++) {
      state.pickups.push({
        type: "coin",
        x: e.x + (rng() - 0.5) * e.r,
        y: e.y + (rng() - 0.5) * e.r,
        vx: (rng() - 0.5) * 120,
        vy: 34 + rng() * 96,
        value: e.type === 3 ? 7 : e.type === 4 ? 5 : 3,
        life: 6.2,
      });
    }
    const cashCount = e.type === 4 ? 2 : e.type === 3 ? 2 : e.type === 2 ? (rng() < 0.55 ? 1 : 0) : rng() < 0.32 ? 1 : 0;
    for (let i = 0; i < cashCount; i++) {
      spawnPickup("cash", e.x, e.y);
    }
    if (e.type === 4 || e.type === 3 || rng() < 0.18) {
      spawnPickup(e.type === 4 || e.type === 3 ? "chip" : "energy", e.x, e.y);
    }
    if (state.player.hp < state.player.maxHp * 0.45 && rng() < 0.12) {
      spawnPickup("repair", e.x, e.y);
    }
  }

  function spawnPickup(type, x, y) {
    const data = {
      energy: { value: 28, life: 6.5 },
      chip: { value: 1, life: 7.5 },
      repair: { value: 18 + save.core * 2, life: 5.5 },
      cash: { value: 12 + Math.min(18, Math.floor(state.time / 25) * 2), life: 6.2 },
    }[type] || { value: 5, life: 5 };
    state.pickups.push({
      type,
      x: x + (rng() - 0.5) * 18,
      y: y + (rng() - 0.5) * 18,
      vx: (rng() - 0.5) * 80,
      vy: 34 + rng() * 70,
      value: data.value,
      life: data.life,
    });
  }

  function nearestEnemy(ignore = null) {
    let best = null;
    let bestDist = Infinity;
    for (const e of state.enemies) {
      if (e === ignore) continue;
      const d = dist2(state.player.x, state.player.y, e.x, e.y);
      if (d < bestDist) {
        best = e;
        bestDist = d;
      }
    }
    return best;
  }

  function gainFieldEnergy(amount, source) {
    if (state.mode !== "playing") return;
    state.run.fieldEnergy += amount;
    if (state.run.fieldEnergy >= state.run.fieldEnergyNeeded) {
      state.run.fieldEnergy -= state.run.fieldEnergyNeeded;
      openUpgradeChoice(source === "처치" ? "전장 에너지 충전 완료" : `${source} 획득`, false);
    }
  }

  function openUpgradeChoice(reason, rareBoost) {
    if (state.mode !== "playing") return;
    state.run.upgradeLevel++;
    state.run.fieldEnergyNeeded = Math.round(100 + state.run.upgradeLevel * 65 + state.run.upgradeLevel * state.run.upgradeLevel * 12);
    state.run.choices = buildUpgradeChoices(rareBoost);
    state.mode = "upgrade";
    screenFlash(rareBoost ? colors.magenta : colors.cyan, 0.18);
    shockwave(state.player.x, state.player.y, rareBoost ? colors.magenta : colors.cyan);
    setStatus(reason);
  }

  function buildUpgradeChoices(rareBoost) {
    const available = runUpgradePool.filter((u) => getRunStack(u.id) < u.max);
    const chosen = [];
    const ship = currentShip();
    const weighted = [];
    for (const u of available) {
      let weight = 3;
      if (u.tags.includes("missile")) weight += 2;
      if (u.tags.includes("ship")) weight += 2;
      if (u.rarity === "희귀" && (rareBoost || state.time > 85)) weight += 2;
      if (u.rarity === "특급" && (rareBoost || state.time > 150)) weight += 3;
      if (ship.missile === "micro_cyan" && (u.id === "up_missile_reload" || u.id === "up_missile_extra")) weight += 2;
      if (ship.missile === "plasma_magenta" && (u.id === "up_missile_damage" || u.id === "up_missile_split")) weight += 2;
      if (ship.bonus.defenseBias && u.tags.includes("defense")) weight += 2;
      for (let i = 0; i < weight; i++) weighted.push(u);
    }
    while (chosen.length < 3 && weighted.length) {
      const pick = weighted[Math.floor(rng() * weighted.length)];
      if (!chosen.includes(pick)) chosen.push(pick);
      for (let i = weighted.length - 1; i >= 0; i--) {
        if (weighted[i] === pick) weighted.splice(i, 1);
      }
    }
    return chosen;
  }

  function chooseRunUpgrade(index) {
    const choice = state.run.choices[index];
    if (!choice) return;
    state.run.stacks[choice.id] = getRunStack(choice.id) + 1;
    state.run.lastUpgrade = choice.label;
    if (choice.id === "up_drone_wing") {
      state.player.drone = true;
      state.player.droneTime += 18;
    }
    if (choice.id === "up_shield_regen") {
      state.run.shieldReady = Math.max(state.run.shieldReady, 1);
      state.player.hp = Math.min(state.player.maxHp, state.player.hp + 18);
    }
    if (choice.id === "up_nova_charge") {
      state.player.nova = Math.min(1, state.player.nova + 0.16);
    }
    if (choice.tags.includes("missile")) {
      state.timers.missile = Math.min(state.timers.missile, 0.08);
      missileUpgradeShowcase(choice);
    }
    state.mode = "playing";
    state.run.choices = [];
    screenFlash(rarityColor(choice.rarity), 0.16);
    burst(state.player.x, state.player.y, rarityColor(choice.rarity), 24, 150);
    setStatus(`${choice.label} 적용`);
  }

  function missileUpgradeShowcase(choice) {
    const color = rarityColor(choice.rarity);
    shockwave(state.player.x, state.player.y, color);
    for (let i = 0; i < 8; i++) {
      const a = -Math.PI / 2 + (i - 3.5) * 0.22;
      const sx = state.player.x + Math.cos(a) * 18;
      const sy = state.player.y + Math.sin(a) * 18;
      state.particles.push({
        line: true,
        x: sx,
        y: sy,
        vx: Math.cos(a) * 220,
        vy: Math.sin(a) * 220,
        r: 3.5,
        color,
        life: 0.34,
        max: 0.34,
        alpha: 0.72,
        drag: 1.6,
      });
    }
  }

  function getRunStack(id) {
    return state.run.stacks[id] || 0;
  }

  function updateRunShield(dt) {
    const stacks = getRunStack("up_shield_regen");
    if (!stacks || state.run.shieldReady > 0) return;
    state.run.shieldCooldown = (state.run.shieldCooldown || (12 - stacks * 1.8)) - dt;
    if (state.run.shieldCooldown <= 0) {
      state.run.shieldReady = 1;
      state.run.shieldCooldown = 12 - stacks * 1.8;
      shieldBurst(state.player.x, state.player.y, currentShip().accent);
      setStatus("보호막 충전");
    }
  }

  function applyMissileImpact(b, hitEnemy) {
    const color = b.color || colors.gold;
    const supernova = getRunStack("up_missile_supernova");
    const powerTier = b.powerTier || 0;
    const impactScale = (b.missileType === "plasma_magenta" ? 0.72 : 0.52) + Math.min(0.55, powerTier * 0.045) + supernova * 0.12;
    explosion(b.x, b.y, color, impactScale);
    if (supernova > 0) shockwave(b.x, b.y, color);
    shake(0.08 + Math.min(0.12, powerTier * 0.01), b.missileType === "plasma_magenta" ? 4.8 : 3.4 + powerTier * 0.22);
    const splashDamage = b.damage * (b.missileType === "cluster_orange" ? 0.55 : 0.34 + supernova * 0.08);
    if (b.splash > 20) {
      for (const e of state.enemies) {
        if (e === hitEnemy) continue;
        const d = Math.sqrt(dist2(b.x, b.y, e.x, e.y));
        if (d < b.splash + e.r) {
          e.hp -= splashDamage * (1 - d / Math.max(1, b.splash + e.r));
          burst(e.x, e.y, color, 5, 75);
        }
      }
    }
    if (getRunStack("up_missile_split") > 0) {
      const shards = 3 + getRunStack("up_missile_split") * 2;
      for (let i = 0; i < shards; i++) {
        const angle = -Math.PI / 2 + (i - (shards - 1) / 2) * 0.3;
        addBullet(state.playerBullets, b.x, b.y, Math.cos(angle) * 430, Math.sin(angle) * 430, 3.7, b.damage * 0.26, color);
        const shard = state.playerBullets[state.playerBullets.length - 1];
        shard.energyShard = true;
      }
      burst(b.x, b.y, color, 8 + shards, 120);
    }
    if (getRunStack("up_signature") > 0 && b.missileType === "emp_blue") {
      for (const e of state.enemies) e.speed *= 0.985;
      shockwave(b.x, b.y, colors.blue);
    }
    if (b.pierce > 0) {
      b.pierce--;
      b.life = Math.max(b.life, 0.7);
      return false;
    }
    return true;
  }

  function render() {
    ctx.save();
    ctx.clearRect(0, 0, W, H);
    drawBackground();
    if (state.shake > 0) ctx.translate((rng() - 0.5) * state.shakePower, (rng() - 0.5) * state.shakePower);
    drawWorld();
    ctx.restore();
    drawScreenFlash();
    drawOverlay();
  }

  function drawBackground() {
    const phase = currentPhase();
    const stage = currentStageKey();
    const far = assetImage(backgroundAssetId(stage, "far"));
    const mid = assetImage(backgroundAssetId(stage, "mid"));
    const near = assetImage(backgroundAssetId(stage, "near"));
    if (far) {
      drawScrollingCover(far, 12 * phase.speed, 1);
      ctx.fillStyle = colorToAlpha(phase.top, 0.16);
      ctx.fillRect(0, 0, W, H);
      if (mid) drawScrollingCover(mid, 30 * phase.speed, 0.34);
      if (near) drawScrollingCover(near, 58 * phase.speed, 0.24);
    } else {
      const gradient = ctx.createLinearGradient(0, 0, W, H);
      gradient.addColorStop(0, phase.top);
      gradient.addColorStop(1, "#02040b");
      ctx.fillStyle = gradient;
      ctx.fillRect(0, 0, W, H);
    }
    for (const s of state.stars) {
      ctx.globalAlpha = s.alpha;
      ctx.fillStyle = phase.star;
      circle(s.x, s.y, s.r, true);
    }
    ctx.globalAlpha = 1;
    ctx.strokeStyle = colorToAlpha(phase.accent, 0.12);
    ctx.lineWidth = 1;
    const grid = 58;
    const offset = (state.time * 28 * phase.speed) % grid;
    for (let y = offset; y < H; y += grid) {
      line(W * 0.08, y, W * 0.92, y + 18);
    }
    ctx.strokeStyle = colorToAlpha(phase.alt, 0.14);
    line(W * 0.08, 0, W * 0.03, H);
    line(W * 0.92, 0, W * 0.97, H);
    ctx.globalAlpha = 0.22;
    ctx.strokeStyle = colorToAlpha(phase.accent, 0.35);
    for (let i = 0; i < 5; i++) {
      const y = ((state.time * (44 + i * 8) * phase.speed) + i * 170) % (H + 220) - 120;
      line(W * (0.18 + i * 0.16), y, W * (0.12 + i * 0.16), y + 96);
    }
    ctx.globalAlpha = 1;
  }

  function drawWorld() {
    for (const p of state.pickups) drawPickup(p);
    for (const b of state.playerBullets) drawBullet(b);
    for (const e of state.enemies) drawEnemy(e);
    for (const b of state.enemyBullets) drawBullet(b);
    for (const p of state.particles) drawParticle(p);
    if (state.mode === "playing" || state.mode === "upgrade" || state.mode === "gameover") drawPlayer();
  }

  function drawOverlay() {
    buttons.length = 0;
    if (state.mode === "splash") drawSplash();
    if (state.mode === "title") drawTitle();
    if (state.mode === "hangar") drawHangar();
    if (state.mode === "ships") drawShipSelect();
    if (state.mode === "ship_detail") drawShipDetail();
    if (state.mode === "leaderboard") drawLeaderboard();
    if (state.mode === "playing" || state.mode === "upgrade") drawHud();
    if (state.mode === "upgrade") drawUpgradeOverlay();
    if (state.mode === "gameover") drawGameOver();
    if (state.mode !== "splash") drawStatus();
    for (const t of state.texts) {
      const a = clamp(t.life / t.max, 0, 1);
      ctx.globalAlpha = a;
      ctx.fillStyle = t.color;
      ctx.font = "bold 12px \"Malgun Gothic\", Arial";
      ctx.textAlign = "center";
      ctx.fillText(t.text, t.x, t.y);
    }
    ctx.globalAlpha = 1;
  }

  function drawSplash() {
    const opening = assetImage("ui_opening_splash");
    if (opening) {
      drawCoverImage(opening, 0.94);
      vignette(0.5);
    } else {
      vignette(0.72);
      ctx.save();
      ctx.globalCompositeOperation = "lighter";
      ctx.strokeStyle = colorToAlpha(colors.cyan, 0.28);
      ctx.lineWidth = 1.4;
      const scan = (Math.sin(performance.now() * 0.0016) * 0.5 + 0.5) * H;
      line(W * 0.12, scan, W * 0.88, scan + 24);
      ctx.restore();
      drawHeroShip(W / 2, H * 0.3, 1.34);
    }
    const titleLogo = assetImage("ui_title_logo_en");
    if (titleLogo) {
      const logoW = Math.min(306, W * 0.78);
      const logoH = Math.min(80, logoW * (titleLogo.naturalHeight / titleLogo.naturalWidth));
      drawImageAnchored(titleLogo, W / 2, H * 0.46, logoW, logoH, 0.5, 0.5, 1);
    } else {
      ctx.textAlign = "center";
      ctx.fillStyle = colors.white;
      ctx.font = "bold 42px Arial, \"Malgun Gothic\"";
      ctx.fillText("NEON WING", W / 2, H * 0.46);
    }
    const pulse = 0.55 + 0.45 * Math.sin(performance.now() * 0.004);
    ctx.textAlign = "center";
    ctx.fillStyle = `rgba(235,248,255,${0.78 + pulse * 0.18})`;
    ctx.font = "bold 18px \"Malgun Gothic\", Arial";
    ctx.fillText("클릭하여 실행", W / 2, H * 0.72);
    ctx.strokeStyle = colorToAlpha(colors.cyan, 0.26 + pulse * 0.3);
    ctx.lineWidth = 1.4;
    line(W * 0.32, H * 0.742, W * 0.68, H * 0.742);
    buttons.push({ id: "splash_start", x: 0, y: 0, w: W, h: H, label: "클릭하여 실행" });
  }

  function drawTitle() {
    vignette(0.68);
    drawHeroShip(W / 2, H * 0.27, 1.25);
    const titleLogo = assetImage("ui_title_logo_en");
    if (titleLogo) {
      const logoW = Math.min(292, W * 0.76);
      const logoH = Math.min(76, logoW * (titleLogo.naturalHeight / titleLogo.naturalWidth));
      ctx.save();
      ctx.shadowColor = colorToAlpha(colors.cyan, 0.5);
      ctx.shadowBlur = 12;
      drawImageAnchored(titleLogo, W / 2, H * 0.385, logoW, logoH, 0.5, 0.5, 1);
      ctx.restore();
    } else {
      ctx.fillStyle = colors.white;
      ctx.font = "bold 40px Arial, \"Malgun Gothic\"";
      ctx.textAlign = "center";
      ctx.fillText("NEON WING", W / 2, H * 0.39);
    }
    ctx.fillStyle = "rgba(178,237,255,0.86)";
    ctx.font = "12px \"Malgun Gothic\", Arial";
    ctx.fillText(`${currentShip().label} / ${missileProfiles[currentShip().missile].label} / 전장 개조`, W / 2, H * 0.43);
    button("출격 시작", W / 2 - 151, H * 0.58 - 24, 302, 48, colors.cyan, "start", true);
    button("모의 광고: 시작 버프", W / 2 - 151, H * 0.655 - 24, 302, 48, colors.gold, "buff");
    button("격납고 / 상점", W / 2 - 151, H * 0.73 - 24, 302, 48, colors.magenta, "hangar");
    button("랭킹 TOP 50", W / 2 - 151, H * 0.805 - 24, 302, 48, colors.white, "leaderboard");
    topEconomy();
  }

  function drawHangar() {
    vignette(0.45);
    topEconomy();
    ctx.textAlign = "left";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 25px \"Malgun Gothic\", Arial";
    ctx.fillText("격납고", W * 0.07, H * 0.15);
    ctx.fillStyle = "rgba(178,237,255,0.82)";
    ctx.font = "12px \"Malgun Gothic\", Arial";
    ctx.fillText(`${currentShip().label} 선택 중 / ${missileProfiles[currentShip().missile].label}`, W * 0.07, H * 0.18);
    button("기체 선택", W * 0.64, H * 0.115, W * 0.29, 34, colors.cyan, "ships");
    upgradeButton("코어", save.core, upgradeCost(save.core, 170), 0.22, colors.cyan, "core");
    upgradeButton("미사일", save.missile, upgradeCost(save.missile, 210), 0.305, colors.gold, "missile");
    upgradeButton("드론", save.drone, upgradeCost(save.drone, 240), 0.39, colors.white, "drone");
    upgradeButton("자석", save.magnet, upgradeCost(save.magnet, 150), 0.475, colors.blue, "magnet");
    purchaseButton("아스트라 기체", save.astra ? "보유 중" : { gems: 120 }, 0.565, colors.magenta, "astra");
    purchaseButton("스타터 패키지", "$1.99 모의", 0.65, colors.gold, "starter");
    purchaseButton("광고 제거", save.removeAds ? "보유 중" : "$2.99 모의", 0.735, colors.cyan, "remove_ads");
    purchaseButton("월간 보급 패스", save.supplyPass ? "활성화" : "$3.99 모의", 0.82, colors.magenta, "supply_pass");
    button("뒤로", W / 2 - 110, H * 0.945 - 22, 220, 44, colors.white, "back");
  }

  function drawShipSelect() {
    vignette(0.48);
    topEconomy();
    ctx.textAlign = "left";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 24px \"Malgun Gothic\", Arial";
    ctx.fillText("기체 선택", W * 0.07, H * 0.13);
    ctx.fillStyle = "rgba(178,237,255,0.78)";
    ctx.font = "12px \"Malgun Gothic\", Arial";
    ctx.fillText("기체마다 기본 미사일과 전용 개조 후보가 다릅니다.", W * 0.07, H * 0.162);
    const cardW = W * 0.41;
    const cardH = 68;
    const gap = 10;
    for (let i = 0; i < shipOrder.length; i++) {
      const id = shipOrder[i];
      const ship = ships[id];
      const col = i % 2;
      const row = Math.floor(i / 2);
      const x = W * 0.07 + col * (cardW + gap);
      const y = H * 0.2 + row * (cardH + gap);
      const owned = ownsShip(id);
      const selected = save.selectedShip === id;
      panel(x, y, cardW, cardH, selected ? colors.gold : ship.accent);
      ctx.textAlign = "left";
      ctx.fillStyle = owned ? colors.white : "rgba(210,226,236,0.62)";
      ctx.font = "bold 13px \"Malgun Gothic\", Arial";
      ctx.fillText(ship.label, x + 13, y + 22);
      ctx.fillStyle = selected ? colors.gold : colorToAlpha(ship.accent, owned ? 0.9 : 0.55);
      ctx.font = "11px \"Malgun Gothic\", Arial";
      ctx.fillText(selected ? "선택됨" : owned ? ship.role : "잠김", x + 13, y + 42);
      ctx.fillStyle = "rgba(235,248,255,0.72)";
      ctx.font = "10px \"Malgun Gothic\", Arial";
      ctx.fillText(missileProfiles[ship.missile].label, x + 13, y + 58);
      drawShipPreview(ship, x + cardW - 31, y + 39, 0.36, owned ? 1 : 0.44);
      buttons.push({ id: `ship_${id}`, x, y, w: cardW, h: cardH, label: ship.label });
    }
    const selectedShip = ships[state.selectedShopShip] || currentShip();
    panel(W * 0.07, H * 0.615, W * 0.86, 96, selectedShip.accent);
    ctx.textAlign = "left";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 15px \"Malgun Gothic\", Arial";
    ctx.fillText(`${selectedShip.label} / ${selectedShip.role}`, W * 0.07 + 16, H * 0.615 + 24);
    ctx.fillStyle = "rgba(190,235,255,0.86)";
    ctx.font = "12px \"Malgun Gothic\", Arial";
    ctx.fillText(`미사일: ${missileProfiles[selectedShip.missile].label}`, W * 0.07 + 16, H * 0.615 + 48);
    ctx.fillText(`패시브: ${selectedShip.passive}`, W * 0.07 + 16, H * 0.615 + 70);
    drawShipPreview(selectedShip, W * 0.82, H * 0.615 + 58, 0.52, ownsShip(selectedShip.id) ? 1 : 0.48);
    button("격납고", W * 0.07, H * 0.78, W * 0.4, 44, colors.white, "hangar_back");
    button("출격", W * 0.53, H * 0.78, W * 0.4, 44, colors.cyan, "start", true);
  }

  function drawShipDetail() {
    const ship = ships[state.selectedShopShip] || ships.neon_wing;
    vignette(0.56);
    topEconomy();
    ctx.textAlign = "left";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 24px \"Malgun Gothic\", Arial";
    ctx.fillText(ship.label, W * 0.07, H * 0.13);
    drawShipPreview(ship, W / 2, H * 0.27, 0.92, 0.95);
    panel(W * 0.07, H * 0.39, W * 0.86, 166, ship.accent);
    ctx.fillStyle = colors.white;
    ctx.font = "bold 15px \"Malgun Gothic\", Arial";
    ctx.fillText("구매 조건", W * 0.07 + 16, H * 0.39 + 28);
    ctx.fillStyle = "rgba(216,240,255,0.88)";
    ctx.font = "12px \"Malgun Gothic\", Arial";
    ctx.fillText(`해금: ${ship.unlock}`, W * 0.07 + 16, H * 0.39 + 56);
    ctx.fillText(`진행: 최고 점수 ${save.best}`, W * 0.07 + 16, H * 0.39 + 80);
    ctx.fillText("가격:", W * 0.07 + 16, H * 0.39 + 104);
    drawPriceIcons(ship.price, W * 0.07 + 58, H * 0.39 + 104, "left", 13);
    ctx.fillText(`미사일: ${missileProfiles[ship.missile].label}`, W * 0.07 + 16, H * 0.39 + 128);
    ctx.fillText(`패시브: ${ship.passive}`, W * 0.07 + 16, H * 0.39 + 150);
    const canBuy = canBuyShip(ship.id);
    button(canBuy ? "구매 / 해금" : "조건 미달", W * 0.07, H * 0.66, W * 0.86, 48, canBuy ? colors.gold : colors.red, `buy_${ship.id}`, canBuy);
    button("기체 목록", W * 0.07, H * 0.755, W * 0.4, 44, colors.white, "ships");
    button("격납고", W * 0.53, H * 0.755, W * 0.4, 44, colors.cyan, "hangar_back");
  }

  function drawHud() {
    topEconomy();
    healthBar(W * 0.06, H * 0.055, W * 0.38, 8, state.player.hp / state.player.maxHp, colors.red, colors.cyan);
    const energyRatio = state.run.fieldEnergy / Math.max(1, state.run.fieldEnergyNeeded);
    healthBar(W * 0.06, H * 0.073, W * 0.38, 5, energyRatio, colors.magenta, colors.gold);
    ctx.textAlign = "right";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 18px \"Malgun Gothic\", Arial";
    ctx.fillText(String(state.score).padStart(6, "0"), W * 0.94, H * 0.068);
    ctx.fillStyle = "rgba(190,244,255,0.74)";
    ctx.font = "11px \"Malgun Gothic\", Arial";
    ctx.fillText(currentPhase().label, W * 0.94, H * 0.09);
    const x = W * 0.13;
    const y = H * 0.88;
    buttons.push({ id: "nova", x: x - 33, y: y - 33, w: 66, h: 66, label: "노바" });
    ctx.fillStyle = "rgba(8,14,30,0.72)";
    circle(x, y, 33, true);
    ctx.strokeStyle = state.player.nova >= 1 ? colors.gold : "rgba(120,160,180,0.7)";
    ctx.lineWidth = 4;
    ctx.beginPath();
    ctx.arc(x, y, 29, -Math.PI / 2, -Math.PI / 2 + TAU * state.player.nova);
    ctx.stroke();
    ctx.fillStyle = state.player.nova >= 1 ? "rgba(255,220,94,0.88)" : "rgba(82,244,255,0.42)";
    circle(x, y, 18, true);
    ctx.fillStyle = "#05070f";
    ctx.font = "bold 11px \"Malgun Gothic\", Arial";
    ctx.textAlign = "center";
    ctx.fillText("노바", x, y + 4);
    if (state.mode === "playing") {
      button("나가기", W * 0.72, H * 0.112, W * 0.22, 34, colors.white, "exit_run");
    }
    if (!state.player.drone) {
      button("광고 드론", W * 0.06, H * 0.13 - 18, W * 0.34, 36, colors.cyan, "restore_drone");
    } else {
      ctx.textAlign = "left";
      ctx.fillStyle = "rgba(190,244,255,0.76)";
      ctx.font = "11px \"Malgun Gothic\", Arial";
      ctx.fillText(`드론 ${Math.ceil(state.player.droneTime)}초  개조 Lv.${state.run.upgradeLevel}`, W * 0.06, H * 0.095);
    }
    if (state.run.shieldReady > 0) {
      ctx.fillStyle = colorToAlpha(currentShip().accent, 0.75);
      ctx.font = "bold 11px \"Malgun Gothic\", Arial";
      ctx.textAlign = "left";
      ctx.fillText("보호막 준비", W * 0.06, H * 0.115);
    }
    drawMissileUpgradeMeter();
  }

  function drawMissileUpgradeMeter() {
    const missileStacks = Object.entries(state.run.stacks)
      .filter(([id]) => id.startsWith("up_missile") || id === "up_signature")
      .reduce((sum, [, value]) => sum + value, 0);
    if (!missileStacks) return;
    const ship = currentShip();
    const x = W * 0.56;
    const y = H * 0.105;
    const w = W * 0.14;
    ctx.textAlign = "left";
    ctx.fillStyle = colorToAlpha(ship.accent, 0.82);
    ctx.font = "bold 10px \"Malgun Gothic\", Arial";
    ctx.fillText(`미사일 +${missileStacks}`, x, y);
    ctx.strokeStyle = colorToAlpha(ship.accent, 0.6);
    ctx.lineWidth = 1.2;
    roundRect(x, y + 6, w, 5, 3, "rgba(8,14,29,0.7)", true);
    roundRect(x, y + 6, Math.min(w, missileStacks * 8), 5, 3, ship.accent, true);
  }

  function drawUpgradeOverlay() {
    ctx.fillStyle = "rgba(2,5,12,0.66)";
    ctx.fillRect(0, 0, W, H);
    ctx.textAlign = "center";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 24px \"Malgun Gothic\", Arial";
    ctx.fillText("전장 개조 선택", W / 2, H * 0.23);
    ctx.fillStyle = "rgba(190,235,255,0.86)";
    ctx.font = "12px \"Malgun Gothic\", Arial";
    ctx.fillText("이번 출격 동안만 적용됩니다", W / 2, H * 0.265);
    const startY = H * 0.32;
    for (let i = 0; i < state.run.choices.length; i++) {
      const u = state.run.choices[i];
      const y = startY + i * 88;
      const color = rarityColor(u.rarity);
      panel(W * 0.08, y, W * 0.84, 74, color);
      ctx.textAlign = "left";
      ctx.fillStyle = color;
      ctx.font = "bold 11px \"Malgun Gothic\", Arial";
      ctx.fillText(u.rarity, W * 0.08 + 18, y + 20);
      ctx.fillStyle = colors.white;
      ctx.font = "bold 16px \"Malgun Gothic\", Arial";
      ctx.fillText(`${u.label}  Lv.${getRunStack(u.id) + 1}`, W * 0.08 + 18, y + 42);
      ctx.fillStyle = "rgba(220,242,255,0.82)";
      ctx.font = "12px \"Malgun Gothic\", Arial";
      ctx.fillText(u.desc, W * 0.08 + 18, y + 61);
      buttons.push({ id: `runup_${i}`, x: W * 0.08, y, w: W * 0.84, h: 74, label: u.label });
    }
  }

  function drawGameOver() {
    vignette(0.76);
    ctx.textAlign = "center";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 34px \"Malgun Gothic\", Arial";
    ctx.fillText(state.runExited ? "전투 종료" : "임무 실패", W / 2, H * 0.23);
    ctx.fillStyle = "rgba(178,237,255,0.9)";
    ctx.font = "bold 16px \"Malgun Gothic\", Arial";
    ctx.fillText(`점수 ${state.score}   최고 ${save.best}`, W / 2, H * 0.29);
    ctx.fillStyle = colors.gold;
    ctx.font = "14px \"Malgun Gothic\", Arial";
    ctx.fillText("보상", W / 2 - 44, H * 0.335);
    drawPriceIcons({ coins: state.pendingCoins, gems: state.pendingGems }, W / 2 - 12, H * 0.335, "left", 15);
    if (state.lastRank) {
      ctx.fillStyle = colors.cyan;
      ctx.font = "bold 13px \"Malgun Gothic\", Arial";
      ctx.fillText(`로컬 랭킹 ${state.lastRank}위 기록`, W / 2, H * 0.37);
    }
    button(state.rewardClaimed ? "보상 수령 완료" : "보상 받기", W / 2 - 151, H * 0.43 - 24, 302, 48, colors.white, "claim");
    button(state.rewardClaimed ? "2배 보상 불가" : "모의 광고: 보상 2배", W / 2 - 151, H * 0.505 - 24, 302, 48, colors.gold, "double");
    button(state.usedRevive ? "부활 사용 완료" : "모의 광고: 부활", W / 2 - 151, H * 0.58 - 24, 302, 48, colors.cyan, "revive");
    button("다시 도전", W / 2 - 151, H * 0.67 - 24, 302, 48, colors.magenta, "retry", true);
    button("격납고", W / 2 - 151, H * 0.745 - 24, 302, 48, colors.white, "gameover_hangar");
    button("랭킹 TOP 50", W / 2 - 151, H * 0.82 - 24, 302, 48, colors.cyan, "leaderboard");
  }

  function drawLeaderboard() {
    vignette(0.58);
    topEconomy();
    const pageSize = 10;
    const totalPages = Math.max(1, Math.ceil(leaderboard.length / pageSize));
    state.leaderboardPage = clamp(Math.floor(state.leaderboardPage), 0, totalPages - 1);
    const start = state.leaderboardPage * pageSize;
    const rows = leaderboard.slice(start, start + pageSize);
    ctx.textAlign = "left";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 25px \"Malgun Gothic\", Arial";
    ctx.fillText("랭킹 TOP 50", W * 0.07, H * 0.12);
    ctx.fillStyle = "rgba(190,235,255,0.82)";
    ctx.font = "12px \"Malgun Gothic\", Arial";
    ctx.fillText("이 기기에 저장된 로컬 기록입니다.", W * 0.07, H * 0.152);
    if (!rows.length) {
      panel(W * 0.07, H * 0.25, W * 0.86, 106, colors.cyan);
      ctx.textAlign = "center";
      ctx.fillStyle = colors.white;
      ctx.font = "bold 16px \"Malgun Gothic\", Arial";
      ctx.fillText("아직 기록이 없습니다", W / 2, H * 0.25 + 46);
      ctx.fillStyle = "rgba(220,242,255,0.8)";
      ctx.font = "12px \"Malgun Gothic\", Arial";
      ctx.fillText("출격 후 점수를 남겨보세요", W / 2, H * 0.25 + 72);
    } else {
      for (let i = 0; i < rows.length; i++) {
        const entry = rows[i];
        const rank = start + i + 1;
        const y = H * 0.185 + i * 47;
        const color = rank === 1 ? colors.gold : rank <= 3 ? colors.cyan : colors.white;
        panel(W * 0.07, y, W * 0.86, 39, color);
        ctx.textAlign = "left";
        ctx.fillStyle = color;
        ctx.font = "bold 13px \"Malgun Gothic\", Arial";
        ctx.fillText(`${rank}`, W * 0.07 + 14, y + 25);
        ctx.fillStyle = colors.white;
        ctx.font = "bold 14px \"Malgun Gothic\", Arial";
        ctx.fillText(String(entry.score || 0).padStart(6, "0"), W * 0.07 + 50, y + 17);
        ctx.fillStyle = "rgba(210,235,248,0.78)";
        ctx.font = "10px \"Malgun Gothic\", Arial";
        ctx.fillText(`${entry.shipName || "기체"}  ${entry.kills || 0}킬  ${formatDuration(entry.time || 0)}  ${shortDate(entry.at)}`, W * 0.07 + 50, y + 32);
      }
    }
    const footerY = H * 0.84;
    ctx.textAlign = "center";
    ctx.fillStyle = "rgba(235,248,255,0.88)";
    ctx.font = "bold 12px \"Malgun Gothic\", Arial";
    if (totalPages > 1) {
      button("이전", W * 0.07, footerY, W * 0.25, 42, colors.white, "leaderboard_prev");
      ctx.fillText(`${state.leaderboardPage + 1} / ${totalPages}`, W / 2, footerY + 27);
      button("다음", W * 0.68, footerY, W * 0.25, 42, colors.white, "leaderboard_next");
    } else {
      ctx.fillText("1 / 1", W / 2, footerY + 10);
      ctx.font = "11px \"Malgun Gothic\", Arial";
      ctx.fillStyle = "rgba(210,235,248,0.68)";
      ctx.fillText("기록이 10개를 넘으면 페이지 버튼이 표시됩니다", W / 2, footerY + 31);
    }
    button("돌아가기", W / 2 - 110, H * 0.91 - 22, 220, 44, colors.cyan, "leaderboard_back", true);
  }

  function topEconomy() {
    drawCurrencyPill("coin", save.coins, W * 0.06, H * 0.018, 86, colors.gold);
    drawCurrencyPill("gem", save.gems, W * 0.285, H * 0.018, 70, colors.magenta);
    drawCurrencyPill("trophy", save.best, W * 0.94 - 96, H * 0.018, 96, colors.cyan);
  }

  function drawCurrencyPill(kind, amount, x, y, w, accent) {
    roundRect(x, y, w, 24, 7, "rgba(7,12,25,0.64)", true);
    ctx.strokeStyle = colorToAlpha(accent, 0.36);
    ctx.lineWidth = 1;
    roundRect(x, y, w, 24, 7, accent, false);
    drawCurrencyAmount(kind, amount, x + 8, y + 16.5, 13, "left");
  }

  function drawCurrencyAmount(kind, amount, x, y, size = 14, align = "left") {
    const text = String(amount || 0);
    ctx.save();
    ctx.font = `bold ${Math.max(10, size - 1)}px "Malgun Gothic", Arial`;
    const gap = Math.max(4, size * 0.36);
    const total = size + gap + ctx.measureText(text).width;
    const left = align === "center" ? x - total / 2 : align === "right" ? x - total : x;
    drawCurrencyIcon(kind, left + size / 2, y - size * 0.32, size);
    ctx.textAlign = "left";
    ctx.fillStyle = kind === "cash" ? colors.green : "rgba(246,247,255,0.94)";
    ctx.fillText(text, left + size + gap, y);
    ctx.restore();
    return total;
  }

  function drawPriceIcons(price, x, y, align = "left", size = 14) {
    const entries = [];
    if (price && price.coins) entries.push(["coin", price.coins]);
    if (price && price.gems) entries.push(["gem", price.gems]);
    if (!entries.length) {
      ctx.save();
      ctx.textAlign = align === "right" ? "right" : align === "center" ? "center" : "left";
      ctx.fillStyle = "rgba(236,247,255,0.88)";
      ctx.font = `bold ${Math.max(10, size - 1)}px "Malgun Gothic", Arial`;
      ctx.fillText("무료", x, y);
      ctx.restore();
      return 0;
    }
    ctx.save();
    ctx.font = `bold ${Math.max(10, size - 1)}px "Malgun Gothic", Arial`;
    const gap = Math.max(4, size * 0.36);
    const between = Math.max(9, size * 0.72);
    const widths = entries.map(([, value]) => size + gap + ctx.measureText(String(value)).width);
    const total = widths.reduce((sum, w) => sum + w, 0) + between * (entries.length - 1);
    ctx.restore();
    let cursor = align === "center" ? x - total / 2 : align === "right" ? x - total : x;
    for (let i = 0; i < entries.length; i++) {
      drawCurrencyAmount(entries[i][0], entries[i][1], cursor, y, size, "left");
      cursor += widths[i] + between;
    }
    return total;
  }

  function drawCurrencyIcon(kind, x, y, size, alpha = 1) {
    if (drawAssetIcon(`icon_${kind}`, x, y, size, alpha, kind === "cash" ? 1.35 : 1.18)) return;
    const s = size;
    ctx.save();
    ctx.globalAlpha *= alpha;
    ctx.translate(x, y);
    ctx.shadowBlur = s * 0.35;
    if (kind === "gem") {
      ctx.shadowColor = colorToAlpha(colors.magenta, 0.75);
      ctx.fillStyle = colorToAlpha(colors.magenta, 0.9);
      ctx.strokeStyle = colors.white;
      ctx.lineWidth = Math.max(1, s * 0.1);
      ctx.beginPath();
      ctx.moveTo(0, -s * 0.55);
      ctx.lineTo(s * 0.5, -s * 0.08);
      ctx.lineTo(s * 0.25, s * 0.55);
      ctx.lineTo(-s * 0.25, s * 0.55);
      ctx.lineTo(-s * 0.5, -s * 0.08);
      ctx.closePath();
      ctx.fill();
      ctx.stroke();
      ctx.fillStyle = "rgba(255,255,255,0.62)";
      circle(-s * 0.13, -s * 0.12, s * 0.11, true);
    } else if (kind === "cash") {
      ctx.shadowColor = colorToAlpha(colors.green, 0.75);
      roundRect(-s * 0.58, -s * 0.36, s * 1.16, s * 0.72, s * 0.12, colorToAlpha(colors.green, 0.88), true);
      ctx.strokeStyle = "rgba(6,24,14,0.78)";
      ctx.lineWidth = Math.max(1, s * 0.08);
      roundRect(-s * 0.58, -s * 0.36, s * 1.16, s * 0.72, s * 0.12, colors.green, false);
      ctx.fillStyle = "rgba(255,255,255,0.78)";
      circle(0, 0, s * 0.18, true);
      ctx.fillStyle = "rgba(6,24,14,0.55)";
      ctx.fillRect(-s * 0.45, -s * 0.21, s * 0.16, s * 0.42);
      ctx.fillRect(s * 0.29, -s * 0.21, s * 0.16, s * 0.42);
    } else if (kind === "trophy") {
      ctx.shadowColor = colorToAlpha(colors.cyan, 0.65);
      ctx.fillStyle = colorToAlpha(colors.cyan, 0.88);
      ctx.beginPath();
      ctx.moveTo(-s * 0.38, -s * 0.38);
      ctx.lineTo(s * 0.38, -s * 0.38);
      ctx.lineTo(s * 0.26, s * 0.08);
      ctx.quadraticCurveTo(0, s * 0.34, -s * 0.26, s * 0.08);
      ctx.closePath();
      ctx.fill();
      ctx.strokeStyle = colors.white;
      ctx.lineWidth = Math.max(1, s * 0.08);
      line(-s * 0.18, s * 0.33, s * 0.18, s * 0.33);
      line(0, s * 0.1, 0, s * 0.33);
    } else {
      ctx.shadowColor = colorToAlpha(colors.gold, 0.75);
      ctx.fillStyle = colorToAlpha(colors.gold, 0.95);
      regularPolygon(0, 0, s * 0.5, 10, Math.PI / 10, true);
      ctx.strokeStyle = "#fff2a4";
      ctx.lineWidth = Math.max(1, s * 0.09);
      regularPolygon(0, 0, s * 0.5, 10, Math.PI / 10, false);
      ctx.fillStyle = "rgba(255,255,255,0.58)";
      circle(-s * 0.13, -s * 0.15, s * 0.12, true);
    }
    ctx.restore();
  }

  function upgradeButton(label, level, cost, yRatio, color, id) {
    panel(W * 0.07, H * yRatio, W * 0.86, 58, color);
    ctx.textAlign = "left";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 15px \"Malgun Gothic\", Arial";
    ctx.fillText(`${label}  Lv.${level}`, W * 0.07 + 18, H * yRatio + 25);
    ctx.fillStyle = "rgba(190,235,255,0.86)";
    ctx.font = "12px \"Malgun Gothic\", Arial";
    ctx.fillText("업그레이드", W * 0.07 + 18, H * yRatio + 45);
    drawCurrencyAmount("coin", cost, W * 0.07 + 88, H * yRatio + 45, 13, "left");
    buttons.push({ id, x: W * 0.07, y: H * yRatio, w: W * 0.86, h: 58, label });
  }

  function purchaseButton(label, price, yRatio, color, id) {
    panel(W * 0.07, H * yRatio, W * 0.86, 58, color);
    ctx.textAlign = "left";
    ctx.fillStyle = colors.white;
    ctx.font = "bold 14px \"Malgun Gothic\", Arial";
    ctx.fillText(label, W * 0.07 + 18, H * yRatio + 34);
    ctx.textAlign = "right";
    ctx.fillStyle = color;
    ctx.font = "12px \"Malgun Gothic\", Arial";
    if (typeof price === "object") {
      drawPriceIcons(price, W * 0.93 - 18, H * yRatio + 35, "right", 13);
    } else {
      ctx.fillText(price, W * 0.93 - 18, H * yRatio + 35);
    }
    buttons.push({ id, x: W * 0.07, y: H * yRatio, w: W * 0.86, h: 58, label });
  }

  function button(label, x, y, w, h, color, id, primary = false) {
    buttons.push({ id, label, x, y, w, h });
    roundRect(x, y, w, h, 8, primary ? color : colors.panel, true);
    ctx.strokeStyle = color;
    ctx.globalAlpha = 0.8;
    roundRect(x, y, w, h, 8, color, false);
    ctx.globalAlpha = 1;
    ctx.fillStyle = primary ? "#05070f" : colors.white;
    ctx.font = "bold 14px \"Malgun Gothic\", Arial";
    ctx.textAlign = "center";
    ctx.fillText(label, x + w / 2, y + h / 2 + 5);
  }

  function panel(x, y, w, h, color) {
    roundRect(x, y, w, h, 7, colors.panel, true);
    ctx.strokeStyle = color;
    ctx.globalAlpha = 0.65;
    roundRect(x, y, w, h, 7, color, false);
    ctx.globalAlpha = 1;
    ctx.fillStyle = colorToAlpha(color, 0.18);
    ctx.fillRect(x, y, 5, h);
  }

  function spriteFilter(profile, base) {
    return [profile && profile.filter, base].filter(Boolean).join(" ");
  }

  function drawShipSprite(ship, x, y, scale, alpha, baseFilter) {
    const profile = shipSpriteProfiles[ship.id];
    const img = profile ? assetImage(profile.asset) : null;
    if (!img) return false;
    ctx.save();
    ctx.shadowColor = colorToAlpha(ship.accent, 0.68 * alpha);
    ctx.shadowBlur = 15 * scale;
    ctx.filter = spriteFilter(profile, baseFilter);
    drawImageAnchored(img, x, y, profile.width * scale, profile.height * scale, profile.anchorX, profile.anchorY, alpha);
    ctx.restore();
    return true;
  }

  function drawShipPreview(ship, x, y, scale, alpha) {
    if (drawShipSprite(ship, x, y, scale, alpha, "brightness(1.22) contrast(1.1) saturate(1.18)")) return;
    drawMiniShip(x, y, ship.accent, alpha);
  }

  function drawHeroShip(x, y, scale) {
    const ship = currentShip();
    if (drawShipSprite(ship, x, y, scale, 1, "brightness(1.2) contrast(1.08) saturate(1.12)")) return;
    drawShip(x, y, scale, ship.accent, 1);
  }

  function drawPlayer() {
    const blink = state.player.invuln > 0 ? 0.55 + 0.45 * Math.sin(state.time * 28) : 1;
    const ship = currentShip();
    drawPlayerAura(ship);
    if (!drawShipSprite(ship, state.player.x, state.player.y, 1, blink, "brightness(1.24) contrast(1.1) saturate(1.14)")) {
      drawShip(state.player.x, state.player.y, 1, ship.accent, blink);
    }
    if (state.mode === "playing" || state.mode === "upgrade") {
      ctx.strokeStyle = colorToAlpha(ship.accent, 0.34);
      ctx.lineWidth = 1.2;
      circle(state.player.x, state.player.y, playerHitRadius() + 2, false);
      ctx.fillStyle = colorToAlpha(colors.white, 0.78);
      circle(state.player.x, state.player.y, 2.4, true);
    }
    if (state.run.shieldReady > 0) {
      ctx.strokeStyle = colorToAlpha(ship.accent, 0.48 + Math.sin(state.time * 8) * 0.12);
      ctx.lineWidth = 2;
      circle(state.player.x, state.player.y, 34 + Math.sin(state.time * 6) * 2, false);
    }
    if (state.player.drone && state.mode === "playing") {
      drawDrone(state.player.x - 42, state.player.y + 18);
      drawDrone(state.player.x + 42, state.player.y + 18);
    }
  }

  function drawPlayerAura(ship) {
    if (state.mode !== "playing" && state.mode !== "upgrade") return;
    const x = state.player.x;
    const y = state.player.y;
    const pulse = 0.5 + Math.sin(state.time * 8) * 0.12;
    ctx.save();
    ctx.globalCompositeOperation = "lighter";
    ctx.strokeStyle = colorToAlpha(ship.accent, 0.2 + pulse * 0.18);
    ctx.lineWidth = 1.2;
    circle(x, y, 28 + pulse * 4, false);
    ctx.strokeStyle = colorToAlpha(ship.accent, 0.28);
    line(x - 37, y + 33, x - 18, y + 48);
    line(x + 37, y + 33, x + 18, y + 48);
    ctx.fillStyle = colorToAlpha(ship.accent, 0.22);
    circle(x, y + 45, 12 + pulse * 5, true);
    ctx.restore();
  }

  function drawShip(x, y, scale, accent, alpha) {
    const s = scale;
    ctx.globalAlpha = alpha;
    ctx.beginPath();
    ctx.moveTo(x, y - 46 * s);
    ctx.lineTo(x + 25 * s, y + 30 * s);
    ctx.lineTo(x + 8 * s, y + 21 * s);
    ctx.lineTo(x, y + 48 * s);
    ctx.lineTo(x - 8 * s, y + 21 * s);
    ctx.lineTo(x - 25 * s, y + 30 * s);
    ctx.closePath();
    const g = ctx.createLinearGradient(x, y - 48 * s, x, y + 48 * s);
    g.addColorStop(0, "#e8f7ff");
    g.addColorStop(1, "#20304a");
    ctx.fillStyle = g;
    ctx.fill();
    ctx.strokeStyle = accent;
    ctx.lineWidth = 2 * s;
    ctx.stroke();
    ctx.fillStyle = colorToAlpha(accent, 0.72);
    circle(x, y - 4 * s, 8 * s, true);
    circle(x, y + 35 * s, 12 * s, true);
    ctx.globalAlpha = 1;
  }

  function drawDrone(x, y) {
    ctx.fillStyle = "rgba(82,244,255,0.38)";
    circle(x, y, 15, true);
    ctx.fillStyle = "#d6f7ff";
    circle(x, y, 6, true);
    ctx.strokeStyle = colors.cyan;
    ctx.lineWidth = 1.6;
    circle(x, y, 13, false);
  }

  function drawEnemy(e) {
    const accent = e.type === 4 ? colors.cyan : e.type === 0 ? colors.red : e.type === 1 ? colors.orange : e.type === 2 ? colors.magenta : "#be52ff";
    const r = e.r;
    const profile = enemySpriteProfiles[e.type];
    const img = profile ? assetImage(profile.asset) : null;
    if (img) {
      const h = r * profile.heightScale;
      const w = h * (img.naturalWidth / img.naturalHeight);
      ctx.save();
      ctx.shadowColor = colorToAlpha(accent, 0.55);
      ctx.shadowBlur = e.type >= 2 ? 14 : 10;
      ctx.filter = "brightness(1.16) contrast(1.08) saturate(1.1)";
      drawImageAnchored(img, e.x, e.y, w, h, 0.5, 0.5, 1);
      ctx.restore();
      ctx.fillStyle = colorToAlpha(accent, 0.36);
      circle(e.x, e.y, r * 0.34, true);
      if (e.type >= 2) healthBar(e.x - r, e.y - r - 8, r * 2, 4, e.hp / e.maxHp, colors.red, e.type === 4 ? colors.cyan : colors.magenta);
      return;
    }
    ctx.beginPath();
    if (e.type === 4) {
      for (let i = 0; i < 6; i++) {
        const a = -Math.PI / 2 + i * TAU / 6;
        const px = e.x + Math.cos(a) * r;
        const py = e.y + Math.sin(a) * r;
        if (i === 0) ctx.moveTo(px, py);
        else ctx.lineTo(px, py);
      }
    } else if (e.type === 0) {
      ctx.moveTo(e.x, e.y + r);
      ctx.lineTo(e.x - r * 0.85, e.y - r * 0.6);
      ctx.lineTo(e.x + r * 0.85, e.y - r * 0.6);
    } else if (e.type === 1) {
      ctx.moveTo(e.x, e.y + r);
      ctx.lineTo(e.x - r, e.y);
      ctx.lineTo(e.x - r * 0.35, e.y - r);
      ctx.lineTo(e.x + r * 0.35, e.y - r);
      ctx.lineTo(e.x + r, e.y);
    } else {
      ctx.moveTo(e.x, e.y + r);
      ctx.lineTo(e.x - r * 1.15, e.y + r * 0.2);
      ctx.lineTo(e.x - r * 0.6, e.y - r);
      ctx.lineTo(e.x + r * 0.6, e.y - r);
      ctx.lineTo(e.x + r * 1.15, e.y + r * 0.2);
    }
    ctx.closePath();
    ctx.fillStyle = "#461f36";
    ctx.fill();
    ctx.strokeStyle = accent;
    ctx.lineWidth = e.type === 3 ? 2.2 : 1.6;
    ctx.stroke();
    ctx.fillStyle = colorToAlpha(accent, 0.55);
    circle(e.x, e.y, r * 0.28, true);
    if (e.type >= 2) healthBar(e.x - r, e.y - r - 8, r * 2, 4, e.hp / e.maxHp, colors.red, e.type === 4 ? colors.cyan : colors.magenta);
  }

  function drawBullet(b) {
    if (b.missile) {
      for (let i = 0; i < b.trail.length; i++) {
        const t = b.trail[i];
        const ratio = i / b.trail.length;
        ctx.fillStyle = colorToAlpha(b.color, 0.1 + ratio * 0.35);
        circle(t.x, t.y, b.r * ratio * 1.6, true);
      }
      const img = assetImage(`missile_${b.missileType || "homing_gold"}`);
      if (img) {
        const visualScale = b.visualScale || 1;
        const h = Math.max(34, b.r * 7.8 * visualScale);
        const w = h * (img.naturalWidth / img.naturalHeight);
        const angle = Math.atan2(b.vy, b.vx) + Math.PI / 2;
        const moveAngle = Math.atan2(b.vy, b.vx);
        if ((b.powerTier || 0) > 0) {
          ctx.save();
          ctx.globalCompositeOperation = "lighter";
          ctx.strokeStyle = colorToAlpha(b.color, Math.min(0.48, 0.18 + (b.powerTier || 0) * 0.03));
          ctx.lineWidth = Math.max(1.5, b.r * 0.32);
          circle(b.x, b.y, b.r * (1.9 + Math.min(1.8, (b.powerTier || 0) * 0.18)), false);
          ctx.restore();
        }
        ctx.save();
        ctx.lineCap = "round";
        ctx.strokeStyle = colorToAlpha(b.color, 0.42 + Math.min(0.22, (b.powerTier || 0) * 0.018));
        ctx.lineWidth = Math.max(3, b.r * 0.8 * visualScale);
        line(b.x - Math.cos(moveAngle) * h * 0.18, b.y - Math.sin(moveAngle) * h * 0.18, b.x - Math.cos(moveAngle) * h * 0.62, b.y - Math.sin(moveAngle) * h * 0.62);
        ctx.strokeStyle = colorToAlpha(colors.white, 0.52);
        ctx.lineWidth = Math.max(1.5, b.r * 0.36);
        line(b.x - Math.cos(moveAngle) * h * 0.12, b.y - Math.sin(moveAngle) * h * 0.12, b.x - Math.cos(moveAngle) * h * 0.42, b.y - Math.sin(moveAngle) * h * 0.42);
        ctx.lineCap = "butt";
        ctx.restore();
        ctx.save();
        ctx.shadowColor = colorToAlpha(b.color, 0.7);
        ctx.shadowBlur = 9;
        ctx.filter = "brightness(1.2) saturate(1.15)";
        drawImageRotated(img, b.x, b.y, w, h, angle, 0.5, 0.5, 1);
        ctx.restore();
        return;
      }
      ctx.save();
      ctx.translate(b.x, b.y);
      const angle = Math.atan2(b.vy, b.vx) + Math.PI / 2;
      ctx.rotate(angle);
      ctx.fillStyle = b.color;
      ctx.beginPath();
      ctx.moveTo(0, -b.r * 1.35);
      ctx.lineTo(b.r * 0.72, b.r * 1.1);
      ctx.lineTo(0, b.r * 0.62);
      ctx.lineTo(-b.r * 0.72, b.r * 1.1);
      ctx.closePath();
      ctx.fill();
      ctx.fillStyle = colors.white;
      circle(0, -b.r * 0.35, b.r * 0.34, true);
      ctx.restore();
      return;
    }
    if (b.energyShard) {
      ctx.fillStyle = colorToAlpha(b.color, 0.32);
      circle(b.x, b.y, b.r * 2.2, true);
      ctx.fillStyle = b.color;
      circle(b.x, b.y, b.r, true);
      return;
    }
    ctx.fillStyle = colorToAlpha(b.color, 0.25);
    circle(b.x, b.y, b.r * 2.4, true);
    ctx.fillStyle = b.color;
    circle(b.x, b.y, b.r, true);
    ctx.fillStyle = colors.white;
    circle(b.x, b.y, b.r * 0.42, true);
  }

  function drawParticle(p) {
    const a = clamp(p.life / p.max, 0, 1);
    if (p.beam) {
      ctx.lineCap = "round";
      ctx.strokeStyle = colorToAlpha(p.color, 0.22 * a);
      ctx.lineWidth = p.r * 2.8;
      line(p.x, p.y, p.x2, p.y2);
      ctx.strokeStyle = colorToAlpha(p.color, 0.65 * a);
      ctx.lineWidth = p.r * 1.2;
      line(p.x, p.y, p.x2, p.y2);
      ctx.strokeStyle = `rgba(250,255,255,${0.9 * a})`;
      ctx.lineWidth = Math.max(1, p.r * 0.26);
      line(p.x, p.y, p.x2, p.y2);
      ctx.lineCap = "butt";
      return;
    }
    if (p.ring) {
      ctx.strokeStyle = colorToAlpha(p.color, p.alpha * a);
      ctx.lineWidth = Math.max(1, p.stroke * a);
      circle(p.x, p.y, p.r, false);
      return;
    }
    ctx.fillStyle = colorToAlpha(p.color, p.alpha * a);
    if (p.line) {
      ctx.strokeStyle = colorToAlpha(p.color, p.alpha * a);
      ctx.lineWidth = Math.max(1, p.r * 0.45);
      line(p.x, p.y, p.x - p.vx * 0.045, p.y - p.vy * 0.045);
    } else {
      circle(p.x, p.y, p.r * (0.45 + a), true);
    }
  }

  function drawPickup(p) {
    const type = p.type || "coin";
    const color = type === "chip" ? colors.magenta : type === "energy" ? colors.cyan : type === "repair" ? colors.blue : type === "cash" ? colors.green : colors.gold;
    const pulse = 0.88 + 0.12 * Math.sin(state.time * 8 + p.x * 0.05);
    ctx.save();
    ctx.translate(p.x, p.y);
    ctx.shadowColor = colorToAlpha(color, 0.72);
    ctx.shadowBlur = type === "coin" ? 7 : type === "cash" ? 9 : 11;
    ctx.fillStyle = colorToAlpha(color, 0.18);
    circle(0, 0, type === "chip" ? 14 : type === "cash" ? 13 : 12, true);
    ctx.strokeStyle = colorToAlpha(color, 0.9);
    ctx.fillStyle = colorToAlpha(color, 0.55);
    ctx.lineWidth = 1.8;
    const iconId = {
      coin: "icon_coin",
      cash: "icon_cash",
      chip: "icon_upgrade_chip",
      energy: "icon_field_energy",
      repair: "icon_repair",
    }[type];
    if (iconId && drawAssetIcon(iconId, 0, 0, type === "cash" ? 25 * pulse : type === "coin" ? 20 * pulse : 24 * pulse, 1, type === "cash" ? 1.45 : 1.2)) {
      ctx.restore();
      return;
    }
    if (type === "coin") {
      ctx.rotate(state.time * 2.8 + p.x);
      regularPolygon(0, 0, 8.4 * pulse, 6, Math.PI / 6, true);
      ctx.strokeStyle = colors.gold;
      regularPolygon(0, 0, 8.4 * pulse, 6, Math.PI / 6, false);
      ctx.fillStyle = "#fff1a8";
      regularPolygon(0, 0, 3.2, 4, Math.PI / 4, true);
    } else if (type === "energy") {
      ctx.rotate(Math.PI / 4);
      regularPolygon(0, 0, 9.2 * pulse, 4, 0, true);
      ctx.strokeStyle = colors.cyan;
      regularPolygon(0, 0, 9.2 * pulse, 4, 0, false);
      ctx.rotate(-Math.PI / 4);
      ctx.strokeStyle = colors.white;
      ctx.lineWidth = 1.4;
      ctx.beginPath();
      ctx.moveTo(-2, -6);
      ctx.lineTo(2, -1);
      ctx.lineTo(-1, -1);
      ctx.lineTo(3, 6);
      ctx.stroke();
    } else if (type === "repair") {
      ctx.fillStyle = colorToAlpha(colors.blue, 0.62);
      regularPolygon(0, 0, 9.5 * pulse, 8, Math.PI / 8, true);
      ctx.strokeStyle = colors.white;
      ctx.lineWidth = 2.2;
      line(-5.5, 0, 5.5, 0);
      line(0, -5.5, 0, 5.5);
    } else if (type === "cash") {
      ctx.rotate(Math.sin(state.time * 5 + p.x) * 0.12);
      drawCurrencyIcon("cash", 0, 0, 18 * pulse);
    } else {
      ctx.rotate(state.time * 1.4);
      regularPolygon(0, 0, 10.5 * pulse, 6, Math.PI / 6, true);
      ctx.strokeStyle = colors.magenta;
      regularPolygon(0, 0, 10.5 * pulse, 6, Math.PI / 6, false);
      ctx.fillStyle = colors.white;
      regularPolygon(0, 0, 4.2, 6, Math.PI / 6, true);
    }
    ctx.restore();
  }

  function healthBar(x, y, w, h, ratio, low, high) {
    roundRect(x, y, w, h, h / 2, "rgba(10,14,26,0.72)", true);
    roundRect(x, y, w * clamp(ratio, 0, 1), h, h / 2, ratio < 0.3 ? low : high, true);
  }

  function drawStatus() {
    if (state.statusTimer <= 0) return;
    const a = clamp(state.statusTimer / 0.45, 0, 1);
    roundRect(W * 0.16, H * 0.105, W * 0.68, H * 0.04, 8, `rgba(9,15,29,${0.58 * a})`, true);
    ctx.textAlign = "center";
    ctx.fillStyle = `rgba(235,248,255,${0.92 * a})`;
    ctx.font = "12px \"Malgun Gothic\", Arial";
    ctx.fillText(state.status, W / 2, H * 0.13);
  }

  function vignette(alpha) {
    const g = ctx.createRadialGradient(W / 2, H * 0.42, H * 0.12, W / 2, H * 0.42, H * 0.62);
    g.addColorStop(0, "rgba(0,0,0,0)");
    g.addColorStop(1, `rgba(0,0,0,${alpha})`);
    ctx.fillStyle = g;
    ctx.fillRect(0, 0, W, H);
  }

  function engineTrail(x, y, color) {
    for (let i = 0; i < 2; i++) {
      state.particles.push({
        x: x + (rng() - 0.5) * 16,
        y: y + rng() * 8,
        vx: (rng() - 0.5) * 24,
        vy: 120 + rng() * 80,
        r: 2.4 + rng() * 2.6,
        growth: 8,
        life: 0.2 + rng() * 0.14,
        max: 0.34,
        alpha: 0.42,
        color,
        drag: 1.2,
        line: true,
      });
      state.particles[state.particles.length - 1].max = state.particles[state.particles.length - 1].life;
    }
  }

  function muzzleFlash(x, y, color, scale) {
    state.particles.push({ x, y, r: 4 * scale, growth: 46 * scale, life: 0.08, max: 0.08, alpha: 0.8, color, ring: true, stroke: 3 * scale });
    burst(x, y, color, Math.round(4 * scale), 60 * scale);
  }

  function shieldBurst(x, y, color) {
    state.particles.push({ x, y, r: 28, growth: 120, life: 0.34, max: 0.34, alpha: 0.82, color, ring: true, stroke: 4 });
    screenFlash(color, 0.14);
  }

  function screenFlash(color, amount) {
    state.flash = Math.max(state.flash, amount);
    state.flashColor = color;
  }

  function drawScreenFlash() {
    if (state.flash <= 0) return;
    ctx.fillStyle = colorToAlpha(state.flashColor, state.flash);
    ctx.fillRect(0, 0, W, H);
  }

  function explosion(x, y, color, scale) {
    burst(x, y, color, Math.round(16 * scale), 190 * scale);
    state.particles.push({ x, y, r: 8 * scale, growth: 96 * scale, life: 0.18, max: 0.18, alpha: 0.82, color: colors.white, ring: true, stroke: 5 * scale });
    state.particles.push({ x, y, r: 10 * scale, growth: 142 * scale, life: 0.42, max: 0.42, alpha: 0.82, color, ring: true, stroke: 4 * scale });
  }

  function shockwave(x, y, color) {
    state.particles.push({ x, y, r: 24, growth: 620, life: 0.48, max: 0.48, alpha: 0.9, color, ring: true, stroke: 7 });
  }

  function burst(x, y, color, count, speed) {
    for (let i = 0; i < count; i++) {
      const angle = rng() * TAU;
      const v = speed * (0.28 + rng() * 0.72);
      state.particles.push({
        x,
        y,
        vx: Math.cos(angle) * v,
        vy: Math.sin(angle) * v,
        r: 1.6 + rng() * 3.4,
        life: 0.28 + rng() * 0.42,
        max: 0.7,
        alpha: 0.75 + rng() * 0.2,
        color: rng() < 0.2 ? colors.white : color,
        drag: 2.4,
        line: rng() < 0.5,
      });
      const p = state.particles[state.particles.length - 1];
      p.max = p.life;
    }
  }

  function addText(x, y, text, color) {
    state.texts.push({ x, y, text, color, life: 0.75, max: 0.75 });
  }

  function shake(time, power) {
    state.shake = Math.max(state.shake, time);
    state.shakePower = Math.max(state.shakePower, power);
  }

  function handleButton(id) {
    if (id === "splash_start") {
      state.mode = "title";
      screenFlash(colors.cyan, 0.16);
      return;
    }
    if (id.startsWith("runup_")) {
      chooseRunUpgrade(Number(id.slice(6)));
      return;
    }
    if (id.startsWith("ship_")) {
      const shipId = id.slice(5);
      state.selectedShopShip = shipId;
      if (ownsShip(shipId)) {
        save.selectedShip = shipId;
        persist();
        setStatus(`${ships[shipId].label} 선택`);
      } else {
        state.mode = "ship_detail";
      }
      return;
    }
    if (id.startsWith("buy_")) {
      buyShip(id.slice(4));
      return;
    }
    if (id === "leaderboard") {
      state.leaderboardBackMode = state.mode === "leaderboard" ? "title" : state.mode;
      state.leaderboardPage = 0;
      state.mode = "leaderboard";
      return;
    }
    if (id === "leaderboard_back") {
      state.mode = state.leaderboardBackMode || "title";
      return;
    }
    if (id === "leaderboard_prev") {
      state.leaderboardPage = Math.max(0, state.leaderboardPage - 1);
      return;
    }
    if (id === "leaderboard_next") {
      state.leaderboardPage = Math.min(Math.max(0, Math.ceil(leaderboard.length / 10) - 1), state.leaderboardPage + 1);
      return;
    }
    if (id === "start") startRun(false);
    if (id === "buff") mockAd("START_BUFF", () => startRun(true));
    if (id === "hangar") state.mode = "hangar";
    if (id === "ships") state.mode = "ships";
    if (id === "hangar_back") state.mode = "hangar";
    if (id === "back") state.mode = "title";
    if (id === "exit_run" && state.mode === "playing") endRun("exit");
    if (id === "core") tryUpgrade("core", 170);
    if (id === "missile") tryUpgrade("missile", 210);
    if (id === "drone") tryUpgrade("drone", 240);
    if (id === "magnet") tryUpgrade("magnet", 150);
    if (id === "astra") {
      if (save.astra) setStatus("아스트라를 이미 보유 중입니다");
      else if (save.gems >= 120) {
        save.gems -= 120;
        save.astra = true;
        save.ships.astra = true;
        persist();
        setStatus("아스트라 해금 완료");
      } else {
        setStatus("보석 120개가 필요합니다");
      }
    }
    if (id === "starter" || id === "remove_ads" || id === "supply_pass") mockPurchase(id);
    if (id === "nova") useNova();
    if (id === "restore_drone") {
      mockAd("RESTORE_DRONE", () => {
        state.player.drone = true;
        state.player.droneTime = 32 + save.drone * 4;
        burst(state.player.x, state.player.y + 18, colors.cyan, 18, 140);
        setStatus("드론이 복구되었습니다");
      });
    }
    if (id === "claim") claimReward(1);
    if (id === "double" && !state.rewardClaimed) mockAd("DOUBLE_REWARD", () => {
      state.doubleRewardClaimed = true;
      claimReward(2);
    });
    if (id === "revive" && !state.usedRevive) mockAd("REVIVE", () => {
      state.usedRevive = true;
      state.mode = "playing";
      state.player.hp = Math.max(state.player.maxHp * 0.4, 36);
      state.player.invuln = 1.6;
      state.pendingCoins = 0;
      state.pendingGems = 0;
      state.rewardClaimed = false;
      setStatus("부활했습니다");
    });
    if (id === "retry") {
      if (!state.rewardClaimed) claimReward(1);
      startRun(false);
    }
    if (id === "gameover_hangar") {
      if (!state.rewardClaimed) claimReward(1);
      state.mode = "hangar";
    }
  }

  function tryUpgrade(key, base) {
    const cost = upgradeCost(save[key], base);
    if (save.coins < cost) {
      setStatus("코인이 부족합니다");
      return;
    }
    save.coins -= cost;
    save[key]++;
    persist();
    setStatus(`${upgradeLabel(key)} 업그레이드 완료`);
  }

  function upgradeLabel(key) {
    const labels = {
      core: "코어",
      missile: "미사일",
      drone: "드론",
      magnet: "자석",
    };
    return labels[key] || "장비";
  }

  function currentShip() {
    return ships[save.selectedShip] || ships.neon_wing;
  }

  function loadImageAssets() {
    for (const [stage, layers] of Object.entries(imageSources.backgrounds)) {
      for (const [layer, src] of Object.entries(layers)) {
        registerImage(backgroundAssetId(stage, layer), src);
      }
    }
    for (const [id, src] of Object.entries(imageSources.ships)) registerImage(`ship_${id}`, src);
    for (const [id, src] of Object.entries(imageSources.enemies)) registerImage(`enemy_${id}`, src);
    for (const [id, src] of Object.entries(imageSources.missiles)) registerImage(`missile_${id}`, src);
    for (const [id, src] of Object.entries(imageSources.icons || {})) registerImage(`icon_${id}`, src);
    loadOptionalAssetManifest();
  }

  function registerImage(id, src) {
    const img = new Image();
    assetStats.total++;
    img.onload = () => {
      assetStats.loaded++;
      render();
    };
    img.onerror = () => {
      assetStats.failed++;
    };
    img.src = src;
    imageAssets[id] = img;
  }

  function loadOptionalAssetManifest() {
    if (typeof fetch !== "function") return;
    fetch("assets/asset_manifest.json", { cache: "no-store" })
      .then((response) => response.ok ? response.json() : { images: {} })
      .then((manifest) => {
        const images = manifest && manifest.images ? manifest.images : {};
        for (const [id, src] of Object.entries(images)) {
          if (!imageAssets[id]) registerImage(id, src);
        }
      })
      .catch(() => {});
  }

  function assetImage(id) {
    const img = imageAssets[id];
    return img && img.complete && img.naturalWidth > 0 ? img : null;
  }

  function currentShipSpriteProfile() {
    return shipSpriteProfiles[save.selectedShip] || null;
  }

  function currentShipHitRadius() {
    const profile = currentShipSpriteProfile();
    return profile ? profile.hitR : 12;
  }

  function playerHitRadius() {
    return state.player.hitR || currentShipHitRadius();
  }

  function currentStageKey() {
    if (state.time >= 240) return "void_citadel";
    if (state.time >= 170) return "frost_relay";
    if (state.time >= 100) return "crimson_foundry";
    if (state.time >= 30) return "scrap_belt";
    return "neon_orbit";
  }

  function backgroundAssetId(stage, layer) {
    return `bg_${stage}_${layer}`;
  }

  function drawImageAnchored(img, x, y, w, h, anchorX = 0.5, anchorY = 0.5, alpha = 1) {
    ctx.save();
    ctx.globalAlpha *= alpha;
    ctx.drawImage(img, x - w * anchorX, y - h * anchorY, w, h);
    ctx.restore();
  }

  function drawImageRotated(img, x, y, w, h, angle, anchorX = 0.5, anchorY = 0.5, alpha = 1) {
    ctx.save();
    ctx.translate(x, y);
    ctx.rotate(angle);
    ctx.globalAlpha *= alpha;
    ctx.drawImage(img, -w * anchorX, -h * anchorY, w, h);
    ctx.restore();
  }

  function drawAssetIcon(id, x, y, size, alpha = 1, maxWidthScale = 1.2) {
    const img = assetImage(id);
    if (!img) return false;
    let h = size;
    let w = h * (img.naturalWidth / img.naturalHeight);
    const maxW = size * maxWidthScale;
    if (w > maxW) {
      h *= maxW / w;
      w = maxW;
    }
    drawImageAnchored(img, x, y, w, h, 0.5, 0.5, alpha);
    return true;
  }

  function drawScrollingCover(img, speed, alpha) {
    const scale = Math.max(W / img.naturalWidth, H / img.naturalHeight);
    const drawW = img.naturalWidth * scale;
    const drawH = img.naturalHeight * scale;
    const x = (W - drawW) / 2;
    const offset = ((state.time * speed) % drawH + drawH) % drawH;
    ctx.save();
    ctx.globalAlpha *= alpha;
    for (let y = -offset; y < H; y += drawH) {
      ctx.drawImage(img, x, y, drawW, drawH);
    }
    ctx.restore();
  }

  function drawCoverImage(img, alpha = 1) {
    const scale = Math.max(W / img.naturalWidth, H / img.naturalHeight);
    const drawW = img.naturalWidth * scale;
    const drawH = img.naturalHeight * scale;
    drawImageAnchored(img, W / 2, H / 2, drawW, drawH, 0.5, 0.5, alpha);
  }

  function ownsShip(id) {
    if (id === "neon_wing") return true;
    if (id === "astra" && save.astra) return true;
    return Boolean(save.ships && save.ships[id]);
  }

  function canBuyShip(id) {
    const ship = ships[id];
    if (!ship || ownsShip(id)) return false;
    if (id === "raptor" && save.best < 250) return false;
    if (id === "bastion" && save.best < 650) return false;
    if (id === "seraph" && save.best < 1100) return false;
    if (id === "phantom" && save.best < 1600) return false;
    if (id === "nova_x" && save.best < 2500) return false;
    return hasPrice(ship.price);
  }

  function buyShip(id) {
    const ship = ships[id];
    if (!ship) return;
    if (ownsShip(id)) {
      save.selectedShip = id;
      persist();
      state.mode = "ships";
      setStatus(`${ship.label} 선택`);
      return;
    }
    if (!canBuyShip(id)) {
      setStatus("해금 조건 또는 재화가 부족합니다");
      return;
    }
    payPrice(ship.price);
    save.ships[id] = true;
    if (id === "astra") save.astra = true;
    save.selectedShip = id;
    persist();
    state.mode = "ships";
    screenFlash(ship.accent, 0.2);
    setStatus(`${ship.label} 해금 완료`);
  }

  function hasPrice(price) {
    return save.coins >= (price.coins || 0) && save.gems >= (price.gems || 0);
  }

  function payPrice(price) {
    save.coins -= price.coins || 0;
    save.gems -= price.gems || 0;
  }

  function formatDuration(seconds) {
    const total = Math.max(0, Math.floor(seconds));
    const min = Math.floor(total / 60);
    const sec = String(total % 60).padStart(2, "0");
    return `${min}:${sec}`;
  }

  function shortDate(value) {
    if (!value) return "";
    const date = new Date(value);
    if (Number.isNaN(date.getTime())) return "";
    return `${String(date.getMonth() + 1).padStart(2, "0")}.${String(date.getDate()).padStart(2, "0")}`;
  }

  function rarityColor(rarity) {
    if (rarity === "특급") return colors.magenta;
    if (rarity === "희귀") return colors.gold;
    return colors.cyan;
  }

  function currentPhase() {
    const t = state.time;
    if (t >= 240) return { label: "보스 접근", top: "#120712", accent: colors.magenta, alt: colors.red, star: "#ffb8ef", speed: 0.55 };
    if (t >= 170) return { label: "핵심부", top: "#22102b", accent: colors.magenta, alt: colors.gold, star: "#ffd8fa", speed: 1.45 };
    if (t >= 100) return { label: "돌파", top: "#141b31", accent: colors.gold, alt: colors.cyan, star: "#ffe8a2", speed: 1.25 };
    if (t >= 30) return { label: "외곽", top: "#0b1c2c", accent: colors.cyan, alt: colors.magenta, star: "#a8efff", speed: 1.0 };
    return { label: "진입", top: "#08101d", accent: colors.cyan, alt: colors.magenta, star: "#82dcff", speed: 0.78 };
  }

  function drawMiniShip(x, y, accent, alpha) {
    ctx.globalAlpha = alpha;
    ctx.beginPath();
    ctx.moveTo(x, y - 18);
    ctx.lineTo(x + 12, y + 14);
    ctx.lineTo(x, y + 8);
    ctx.lineTo(x - 12, y + 14);
    ctx.closePath();
    ctx.fillStyle = "#20304a";
    ctx.fill();
    ctx.strokeStyle = accent;
    ctx.lineWidth = 1.4;
    ctx.stroke();
    ctx.globalAlpha = 1;
  }

  function bindInput() {
    canvas.tabIndex = 0;
    canvas.addEventListener("pointerdown", (event) => {
      canvas.focus();
      const pos = pointerPos(event);
      pointer.down = true;
      pointer.x = pos.x;
      pointer.y = pos.y;
      for (let i = buttons.length - 1; i >= 0; i--) {
        const b = buttons[i];
        if (pos.x >= b.x && pos.x <= b.x + b.w && pos.y >= b.y && pos.y <= b.y + b.h) {
          handleButton(b.id);
          render();
          return;
        }
      }
      if (state.mode === "playing") {
        pointer.dragging = true;
        setPlayerTarget(pos.x, pos.y);
      }
    });
    canvas.addEventListener("pointermove", (event) => {
      const pos = pointerPos(event);
      pointer.x = pos.x;
      pointer.y = pos.y;
      if (pointer.dragging && state.mode === "playing") setPlayerTarget(pos.x, pos.y);
    });
    window.addEventListener("pointerup", () => {
      pointer.down = false;
      pointer.dragging = false;
    });
    window.addEventListener("keydown", (event) => {
      keys.add(event.code);
      if ((event.code === "Enter" || event.code === "Space") && state.mode === "splash") {
        event.preventDefault();
        state.mode = "title";
        screenFlash(colors.cyan, 0.16);
        return;
      }
      if (event.code === "Space") {
        event.preventDefault();
        useNova();
      }
      if (event.code === "Enter" && state.mode === "title") startRun(false);
      if (event.code === "KeyH" && state.mode !== "playing") state.mode = state.mode === "hangar" ? "title" : "hangar";
      if (event.code === "KeyF") toggleFullscreen();
    });
    window.addEventListener("keyup", (event) => keys.delete(event.code));
  }

  function pointerPos(event) {
    const rect = canvas.getBoundingClientRect();
    return {
      x: clamp(((event.clientX - rect.left) / rect.width) * W, 0, W),
      y: clamp(((event.clientY - rect.top) / rect.height) * H, 0, H),
    };
  }

  function setPlayerTarget(x, y) {
    state.player.tx = clamp(x, 24, W - 24);
    state.player.ty = clamp(y - 72, H * 0.18, H - 64);
  }

  function toggleFullscreen() {
    if (document.fullscreenElement) document.exitFullscreen();
    else document.documentElement.requestFullscreen?.();
  }

  function loop(now) {
    const last = loop.last || now;
    loop.last = now;
    const dt = Math.min(0.034, (now - last) / 1000 || 1 / 60);
    update(dt);
    render();
    requestAnimationFrame(loop);
  }

  window.advanceTime = (ms) => {
    const steps = Math.max(1, Math.round(ms / (1000 / 60)));
    for (let i = 0; i < steps; i++) update(1 / 60);
    render();
  };

  window.render_game_to_text = () => JSON.stringify({
    coordinateSystem: "캔버스 420x760, 원점은 좌상단, +x는 오른쪽, +y는 아래쪽",
    mode: state.mode,
    status: state.statusTimer > 0 ? state.status : "",
    score: state.score,
    kills: state.kills,
    phase: currentPhase().label,
    ship: { id: save.selectedShip, name: currentShip().label, missile: missileProfiles[currentShip().missile].label },
    assets: {
      loaded: assetStats.loaded,
      total: assetStats.total,
      failed: assetStats.failed,
      stage: currentStageKey(),
      playerSprite: Boolean(currentShipSpriteProfile() && assetImage(currentShipSpriteProfile().asset)),
      titleLogo: Boolean(assetImage("ui_title_logo_en")),
      openingSplash: Boolean(assetImage("ui_opening_splash")),
    },
    run: {
      fieldEnergy: round(state.run.fieldEnergy),
      fieldEnergyNeeded: round(state.run.fieldEnergyNeeded),
      upgradeLevel: state.run.upgradeLevel,
      stacks: state.run.stacks,
      choices: state.run.choices.map((u) => ({ id: u.id, label: u.label, rarity: u.rarity })),
      shieldReady: state.run.shieldReady,
    },
    leaderboard: {
      storage: "localStorage: neon-wing-web-leaderboard-v1",
      count: leaderboard.length,
      lastRank: state.lastRank,
      page: state.leaderboardPage,
      top: leaderboard.slice(state.leaderboardPage * 10, state.leaderboardPage * 10 + 10).map((e, i) => ({
        rank: state.leaderboardPage * 10 + i + 1,
        score: e.score,
        kills: e.kills,
        ship: e.shipName,
        time: round(e.time || 0),
      })),
    },
    economy: { coins: save.coins, gems: save.gems, best: save.best, core: save.core, missile: save.missile, drone: save.drone, magnet: save.magnet, astra: save.astra, selectedShip: save.selectedShip, ships: save.ships },
    player: {
      x: round(state.player.x),
      y: round(state.player.y),
      hp: round(state.player.hp),
      maxHp: round(state.player.maxHp),
      hitR: round(playerHitRadius()),
      magnetRadius: round(pickupMagnetRadius()),
      nova: round(state.player.nova),
      drone: state.player.drone,
      droneTime: round(state.player.droneTime),
    },
    counts: {
      enemies: state.enemies.length,
      playerBullets: state.playerBullets.length,
      enemyBullets: state.enemyBullets.length,
      pickups: state.pickups.length,
      particles: state.particles.length,
    },
    enemies: state.enemies.slice(0, 8).map((e) => ({ type: e.type, x: round(e.x), y: round(e.y), hp: round(e.hp), r: e.r })),
    pickups: state.pickups.slice(0, 12).map((p) => ({ type: p.type, x: round(p.x), y: round(p.y), value: p.value })),
    buttons: buttons.map((b) => ({ id: b.id, label: b.label, x: round(b.x), y: round(b.y), w: round(b.w), h: round(b.h) })),
  });

  function seedStars() {
    for (let i = 0; i < 120; i++) {
      state.stars.push({ x: rng() * W, y: rng() * H, speed: 30 + rng() * 145, r: 0.7 + rng() * 2.1, alpha: i % 3 === 0 ? 0.7 : 0.35 });
    }
  }

  function line(x1, y1, x2, y2) {
    ctx.beginPath();
    ctx.moveTo(x1, y1);
    ctx.lineTo(x2, y2);
    ctx.stroke();
  }

  function circle(x, y, r, fill) {
    ctx.beginPath();
    ctx.arc(x, y, Math.max(0.1, r), 0, TAU);
    if (fill) ctx.fill();
    else ctx.stroke();
  }

  function regularPolygon(x, y, r, sides, rotation, fill) {
    ctx.beginPath();
    for (let i = 0; i < sides; i++) {
      const a = rotation + (i / sides) * TAU;
      const px = x + Math.cos(a) * r;
      const py = y + Math.sin(a) * r;
      if (i === 0) ctx.moveTo(px, py);
      else ctx.lineTo(px, py);
    }
    ctx.closePath();
    if (fill) ctx.fill();
    else ctx.stroke();
  }

  function roundRect(x, y, w, h, r, fillStyle, fill) {
    ctx.beginPath();
    ctx.moveTo(x + r, y);
    ctx.lineTo(x + w - r, y);
    ctx.quadraticCurveTo(x + w, y, x + w, y + r);
    ctx.lineTo(x + w, y + h - r);
    ctx.quadraticCurveTo(x + w, y + h, x + w - r, y + h);
    ctx.lineTo(x + r, y + h);
    ctx.quadraticCurveTo(x, y + h, x, y + h - r);
    ctx.lineTo(x, y + r);
    ctx.quadraticCurveTo(x, y, x + r, y);
    if (fill) {
      ctx.fillStyle = fillStyle;
      ctx.fill();
    } else {
      ctx.stroke();
    }
  }

  function colorToAlpha(color, alpha) {
    if (!color.startsWith("#")) return color;
    const n = Number.parseInt(color.slice(1), 16);
    const r = (n >> 16) & 255;
    const g = (n >> 8) & 255;
    const b = n & 255;
    return `rgba(${r},${g},${b},${alpha})`;
  }

  function trim(list, max) {
    while (list.length > max) list.shift();
  }

  function round(v) {
    return Math.round(v * 10) / 10;
  }

  function clamp(v, min, max) {
    return Math.max(min, Math.min(max, v));
  }

  function sq(v) {
    return v * v;
  }

  function dist2(ax, ay, bx, by) {
    return sq(ax - bx) + sq(ay - by);
  }

  function mulberry32(seed) {
    return function next() {
      let t = (seed += 0x6d2b79f5);
      t = Math.imul(t ^ (t >>> 15), t | 1);
      t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
      return ((t ^ (t >>> 14)) >>> 0) / 4294967296;
    };
  }
})();
