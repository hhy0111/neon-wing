# Neon Wing Release Content Scale Plan

이 문서는 `Neon Wing`을 출시 가능한 볼륨으로 확장하기 위한 전장, 악당, 미사일, 시간 기반 배경 전환 계획이다.
핵심 방향은 한 가지 배경에서 무한히 싸우는 형태가 아니라, 한 번의 출격 안에서도 시간이 지나며 전장이 이동하고 적 구성이 바뀌는 구조다.

## 출시 목표 스케일

| 항목 | 출시 최소 목표 | 확장 목표 |
|---|---:|---:|
| 전장 / 지도 | 5개 | 8~12개 |
| 전장별 구간 | 3구간 + 보스 구간 | 4구간 + 이벤트 구간 |
| 배경 레이어 | 전장별 3중 패럴랙스 | 구간별 컬러 그레이드/오버레이 추가 |
| 일반 적 | 12종 | 18~24종 |
| 엘리트 적 | 5종 | 10종 |
| 보스 | 5종 | 8~12종 |
| 플레이어 미사일 | 7종 | 10~12종 |
| 적 탄/미사일 | 6종 | 10종 |
| 한 판 길이 | 4~6분 | 6~8분 선택 모드 |

첫 출시에서는 `5개 전장 x 3개 배경 레이어 = 15개 배경 이미지`를 기본으로 둔다.
각 전장 안에서는 배경 레이어 속도, 컬러 오버레이, 안개/잔해 밀도, 적 스폰 테이블을 바꾸어 시간이 흐르는 느낌을 만든다.
전장마다 완전히 다른 배경 세트를 새로 로딩하면 스케일감은 크지만 제작 비용이 급격히 늘어나므로, 첫 출시에서는 전장 단위로 배경 세트를 나누고 구간 단위는 연출로 변화시킨다.

## 한 판 진행 구조

기본 출격은 약 5분을 목표로 한다.

| 시간 | 구간 | 연출 | 게임플레이 |
|---:|---|---|---|
| 0:00~0:30 | 진입 | 먼 배경 위주, 낮은 속도 | 약한 적, 조작 적응 |
| 0:30~1:40 | 외곽 | 중간 레이어가 선명해짐 | 기본 적 2~3종 조합 |
| 1:40~2:50 | 돌파 | 가까운 레이어 잔해 증가, 배경 속도 상승 | 엘리트 1종 등장, 적 탄 증가 |
| 2:50~4:00 | 핵심부 | 색상 톤 변화, 경고 라인/구조물 증가 | 전장 고유 적과 미니 패턴 |
| 4:00~5:00 | 보스 | 배경 스크롤 둔화, 보스 아레나 느낌 | 보스전, 클리어 보상 |

플레이어가 사망하지 않고 보스를 잡으면 `작전 클리어` 보상을 지급한다.
사망하면 현재처럼 보상형 광고 부활, 보상 2배, 재도전 루프를 유지한다.
클리어 후에는 다음 전장을 해금하고, 이미 클리어한 전장은 더 높은 난이도의 반복 플레이 대상으로 둔다.

## 전장 로드맵

### 1. Neon Orbit

튜토리얼 겸 첫 전장. 지구 궤도 위 네온 도시와 방어 라인을 배경으로 한다.

| 구분 | 내용 |
|---|---|
| 색상 | 어두운 남색, 청록, 마젠타 |
| 주요 적 | Scout, Striker, Shield Guard |
| 엘리트 | Orbital Enforcer |
| 보스 | Carrier |
| 배경 변화 | 성운 -> 궤도 도시 -> 방어 게이트 |
| 해금 | Homing Gold, Micro Cyan |

### 2. Scrap Belt

우주 잔해 지대와 폐선박 야드. 충돌 위험과 지뢰형 적이 특징이다.

| 구분 | 내용 |
|---|---|
| 색상 | 검은 우주, 녹슨 금속, 주황 불꽃 |
| 주요 적 | Rusher, Mine Layer, Repair Pod |
| 엘리트 | Scrap Reclaimer |
| 보스 | Reclaimer Titan |
| 배경 변화 | 소행성 잔해 -> 폐선 야드 -> 압축기 터널 |
| 해금 | Cluster Orange |

### 3. Crimson Foundry

적군 무기 공장. 뜨거운 용광로, 레일, 레이저 포탑이 중심이다.

| 구분 | 내용 |
|---|---|
| 색상 | 검은 금속, 붉은 열기, 오렌지 용광로 |
| 주요 적 | Bomber, Turret Node, Striker Mk2 |
| 엘리트 | Forge Breaker |
| 보스 | Forge Hydra |
| 배경 변화 | 외부 공장지대 -> 생산 레일 -> 반응로 내부 |
| 해금 | Plasma Magenta |

### 4. Frost Relay

얼음 성운과 통신 중계기. 저격형 적과 보호막 적이 등장한다.

| 구분 | 내용 |
|---|---|
| 색상 | 딥 블루, 화이트, 시안 전기 |
| 주요 적 | Sniper, Shield Guard Mk2, EMP Drone |
| 엘리트 | Relay Seraph |
| 보스 | Cryo Lancer |
| 배경 변화 | 얼음 성운 -> 중계 위성군 -> 전기 폭풍 게이트 |
| 해금 | EMP Blue, Rail White |

### 5. Void Citadel

최종 전장. 외계 요새와 중심 코어로 진입한다.

| 구분 | 내용 |
|---|---|
| 색상 | 검정, 보라, 마젠타, 적색 코어 |
| 주요 적 | Wraith, Splitter, Elite Gunship |
| 엘리트 | Void Arbiter |
| 보스 | Void Core |
| 배경 변화 | 암흑 성운 -> 외계 성벽 -> 중심 코어 챔버 |
| 해금 | Nova Gold |

## 악당 로스터

| ID | 이름 | 역할 | 첫 등장 | 시각 특징 |
|---|---|---|---|---|
| enemy_scout | Scout | 빠른 기본 적 | Neon Orbit | 작고 붉은 코어 |
| enemy_striker | Striker | 직선 탄막 | Neon Orbit | 오렌지 포드 2개 |
| enemy_shield_guard | Shield Guard | 보호막 제공 | Neon Orbit | 육각 방패, 청록 방어막 |
| enemy_rusher | Rusher | 돌진형 | Scrap Belt | 톱니형 전면 장갑 |
| enemy_mine_layer | Mine Layer | 지뢰 설치 | Scrap Belt | 둥근 선체, 노란 경고등 |
| enemy_repair_pod | Repair Pod | 적 회복 | Scrap Belt | 작은 수리 드론 |
| enemy_bomber | Bomber | 폭탄 투하 | Crimson Foundry | 두꺼운 장갑, 큰 탄창 |
| enemy_turret_node | Turret Node | 고정 포탑형 | Crimson Foundry | 위성 포탑, 회전포 |
| enemy_sniper | Sniper | 예고선 후 저격 | Frost Relay | 긴 포신, 푸른 조준 렌즈 |
| enemy_emp_drone | EMP Drone | 둔화/방해 | Frost Relay | 전기 코일 |
| enemy_wraith | Wraith | 회피형 고급 적 | Void Citadel | 마젠타 블레이드 |
| enemy_splitter | Splitter | 파괴 시 분열 | Void Citadel | 갈라진 외계 장갑 |

## 엘리트와 보스

| ID | 이름 | 역할 | 전장 |
|---|---|---|---|
| elite_orbital_enforcer | Orbital Enforcer | 첫 엘리트, 넓은 탄막 | Neon Orbit |
| elite_scrap_reclaimer | Scrap Reclaimer | 잔해 소환/흡수 | Scrap Belt |
| elite_forge_breaker | Forge Breaker | 돌진 + 폭발탄 | Crimson Foundry |
| elite_relay_seraph | Relay Seraph | 보호막 + 저격 | Frost Relay |
| elite_void_arbiter | Void Arbiter | 순간이동 + 분열탄 | Void Citadel |
| boss_carrier | Carrier | 소형 적 소환 | Neon Orbit |
| boss_reclaimer_titan | Reclaimer Titan | 잔해 방패, 지뢰 | Scrap Belt |
| boss_forge_hydra | Forge Hydra | 다중 포탑, 화염 레이저 | Crimson Foundry |
| boss_cryo_lancer | Cryo Lancer | 긴 레이저, EMP 링 | Frost Relay |
| boss_void_core | Void Core | 최종 코어, 회전 탄막 | Void Citadel |

## 미사일/무기 로드맵

| ID | 이름 | 역할 | 해금 |
|---|---|---|---|
| missile_homing_gold | Homing Gold | 기본 유도 미사일 | 기본 |
| missile_micro_cyan | Micro Cyan | 연발 소형 미사일 | Neon Orbit |
| missile_cluster_orange | Cluster Orange | 분열 폭발 | Scrap Belt |
| missile_plasma_magenta | Plasma Magenta | 높은 단일 피해 | Crimson Foundry |
| missile_emp_blue | EMP Blue | 적 둔화/보호막 약화 | Frost Relay |
| missile_rail_white | Rail White | 관통형 | Frost Relay |
| missile_nova_gold | Nova Gold | 광역 궁극기 | Void Citadel |

출시 버전에서는 모든 미사일을 동시에 장착시키기보다, `기본 미사일 + 선택 슬롯 1개 + 궁극기` 구조가 안전하다.
이렇게 하면 UI가 단순하고 밸런스 조정도 쉽다.

## 기체/미사일 차별화

기체는 단순 스킨이 아니라 기본 미사일과 전용 전장 개조 후보가 다른 플레이 스타일로 설계한다.
자세한 해금/구매 조건과 화면 구조는 `RUN_UPGRADE_AND_SHIP_PLAN.md`를 따른다.

| 기체 | 기본 미사일 | 플레이 감각 | 출시 우선순위 |
|---|---|---|---|
| Neon Wing | Homing Gold | 균형형, 기본 추적 | 1차 |
| Raptor | Micro Cyan | 빠른 연사와 이동 | 1차 |
| Astra | Plasma Magenta | 고화력 프리미엄 | 1차 |
| Bastion | Cluster Orange | 생존형, 분열 폭발 | 2차 |
| Seraph | EMP Blue | 둔화/보호막 약화 | 2차 |
| Phantom | Rail White | 관통 저격 | 3차 |
| Nova X | Nova Gold | 후반 광역 궁극기 | 3차 |

## 적 탄/위협 패턴

| ID | 역할 | 사용 적 |
|---|---|---|
| bullet_red_orb | 기본 탄 | Scout, Striker |
| bullet_orange_spread | 부채꼴 탄 | Striker, Bomber |
| missile_enemy_mine | 느린 지뢰 | Mine Layer, Reclaimer Titan |
| beam_magenta_warning | 예고선 후 레이저 | Sniper, Cryo Lancer |
| ring_emp_blue | 확장 링 | EMP Drone, Cryo Lancer |
| shard_void_split | 분열 파편 | Splitter, Void Core |

## 배경 전환 구현 기준

웹 하네스와 Android 모두 나중에 같은 스테이지 데이터를 읽도록 아래 구조로 분리하는 것이 좋다.

```json
{
  "id": "neon_orbit",
  "title": "Neon Orbit",
  "durationSec": 300,
  "background": {
    "far": "bg/neon_orbit_far.png",
    "mid": "bg/neon_orbit_mid.png",
    "near": "bg/neon_orbit_near.png"
  },
  "phases": [
    { "at": 0, "name": "approach", "tint": "#0a1020", "speed": 0.8, "enemyPool": ["enemy_scout"] },
    { "at": 30, "name": "perimeter", "tint": "#0b1c2c", "speed": 1.0, "enemyPool": ["enemy_scout", "enemy_striker"] },
    { "at": 100, "name": "breach", "tint": "#18122e", "speed": 1.25, "enemyPool": ["enemy_striker", "enemy_shield_guard"] },
    { "at": 170, "name": "core", "tint": "#230d24", "speed": 1.45, "enemyPool": ["enemy_scout", "enemy_striker", "elite_orbital_enforcer"] },
    { "at": 240, "name": "boss", "tint": "#120712", "speed": 0.45, "boss": "boss_carrier" }
  ]
}
```

## 제작 우선순위

1. `Neon Orbit` 배경 3장, 기본 기체, 적 3종, Carrier 보스, 기본 미사일 2종을 먼저 만든다.
2. 웹 하네스에 이미지 렌더링과 배경 전환을 붙여 5분 플레이가 성립하는지 확인한다.
3. `Scrap Belt`, `Crimson Foundry`를 추가해 중반 콘텐츠를 만든다.
4. `Frost Relay`, `Void Citadel`을 추가해 출시판의 끝까지 연결한다.
5. Android에는 웹에서 검증한 이미지 크기, 좌표, 밸런스를 기준으로 이식한다.
