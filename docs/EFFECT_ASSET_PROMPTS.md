# Neon Wing Effect Asset Prompts

이 문서는 전투감 강화를 위한 이펙트 이미지 생성 프롬프트 모음이다.
현재 웹 하네스는 Canvas 절차적 이펙트로 동작하지만, 추후 이미지/VFX 스프라이트를 받으면 아래 파일명 기준으로 교체하거나 합성할 수 있다.

## 공통 제작 기준

| 구분 | 기준 |
|---|---|
| 권장 해상도 | 단일 이펙트 512x512, 긴 빔/트레일 256x1024 또는 512x1024 |
| 배경 | 투명 PNG |
| 스타일 | premium 2D mobile arcade shooter VFX, neon sci-fi |
| 금지 | 텍스트, 숫자, 로고, UI 버튼, 실사 사진풍, 불투명 배경 |
| 사용 위치 | 웹 `web/assets/effects/`, 추후 Android drawable/texture atlas |

## 권장 파일 구조

```text
web/assets/effects/vfx_muzzle_cyan.png
web/assets/effects/vfx_muzzle_gold.png
web/assets/effects/vfx_missile_trail_gold.png
web/assets/effects/vfx_missile_trail_cyan.png
web/assets/effects/vfx_plasma_trail_magenta.png
web/assets/effects/vfx_explosion_orange.png
web/assets/effects/vfx_explosion_magenta.png
web/assets/effects/vfx_explosion_cyan.png
web/assets/effects/vfx_hit_spark_white.png
web/assets/effects/vfx_shield_burst_cyan.png
web/assets/effects/vfx_emp_ring_blue.png
web/assets/effects/vfx_nova_shockwave_gold.png
web/assets/effects/vfx_upgrade_chip_pickup.png
web/assets/effects/vfx_field_energy_cell.png
web/assets/effects/vfx_repair_pickup.png
web/assets/effects/vfx_phase_warp_lines.png
web/assets/effects/vfx_laser_beam_cyan.png
web/assets/effects/vfx_warning_line_magenta.png
web/assets/effects/vfx_coin_pickup_gold.png
web/assets/effects/vfx_ship_unlock_burst.png
```

## 공통 네거티브 프롬프트

```text
text, letters, numbers, logo, watermark, UI button, frame, border, opaque background, white background, realistic photograph, blurry, noisy, low resolution, character, pilot, cockpit.
```

## Muzzle Cyan

```text
Cyan muzzle flash VFX sprite for a premium 2D vertical sci-fi shooter, bright blue white core, sharp radial energy petals, small sparks, transparent background, centered composition, readable at small size, no text, no logo, no watermark.
```

## Muzzle Gold

```text
Gold missile launch muzzle flash VFX sprite for a premium 2D vertical sci-fi shooter, warm golden burst, white hot center, short radial streaks, transparent background, centered composition, no text, no logo, no watermark.
```

## Missile Trail Gold

```text
Golden homing missile trail VFX for a vertical 2D sci-fi shooter, tapered glowing smoke and energy streak, transparent background, vertical orientation, bright gold core fading to transparent, no text, no logo, no watermark.
```

## Missile Trail Cyan

```text
Cyan micro missile trail VFX for a vertical 2D sci-fi shooter, thin fast electric trail, blue white center, faint particle sparks, transparent background, vertical orientation, no text, no logo, no watermark.
```

## Plasma Trail Magenta

```text
Magenta plasma torpedo trail VFX for a vertical 2D sci-fi shooter, thick violet energy wake, glowing plasma wisps, transparent background, vertical orientation, premium arcade VFX, no text, no logo, no watermark.
```

## Explosion Orange

```text
Orange sci-fi explosion sprite for a 2D vertical shooter, hot white center, orange fire ring, metal sparks, neon arcade style, transparent background, centered composition, no text, no logo, no watermark.
```

## Explosion Magenta

```text
Magenta alien energy explosion sprite for a 2D vertical shooter, violet plasma burst, sharp energy shards, white core, transparent background, centered composition, premium sci-fi game VFX, no text, no logo, no watermark.
```

## Explosion Cyan

```text
Cyan friendly energy explosion sprite for a 2D vertical shooter, blue white plasma burst, circular shock ring, clean readable silhouette, transparent background, centered composition, no text, no logo, no watermark.
```

## Hit Spark White

```text
White metal hit spark VFX sprite for a 2D arcade shooter, sharp star-shaped impact, small cyan and gold particles, transparent background, centered composition, readable at tiny size, no text, no logo, no watermark.
```

## Shield Burst Cyan

```text
Cyan shield impact burst VFX sprite for a sci-fi mobile shooter, circular hex energy shield ripple, blue white arcs, transparent background, centered composition, no text, no logo, no watermark.
```

## EMP Ring Blue

```text
Blue EMP shockwave ring VFX sprite, circular expanding electric ring, blue white lightning arcs, transparent background, centered composition, premium 2D sci-fi shooter effect, no text, no logo, no watermark.
```

## Nova Shockwave Gold

```text
Golden nova shockwave VFX sprite for a vertical sci-fi shooter, large circular energy wave, white hot center glow, gold ring fading outward, transparent background, centered composition, no text, no logo, no watermark.
```

## Upgrade Chip Pickup

```text
Upgrade chip pickup sprite for a sci-fi mobile shooter, small hexagonal circuit chip, cyan magenta gold glow, premium item pickup, transparent background, centered icon, no text, no numbers, no logo, no watermark.
```

## Field Energy Cell

```text
Field energy cell pickup sprite for a sci-fi mobile shooter, small glowing blue energy capsule, cyan white core, subtle electric sparks, transparent background, centered icon, no text, no numbers, no logo, no watermark.
```

## Repair Pickup

```text
Repair kit pickup sprite for a sci-fi mobile shooter, compact blue white nanotech capsule, medical feeling without text or cross symbol, transparent background, centered icon, no text, no numbers, no logo, no watermark.
```

## Phase Warp Lines

```text
Vertical phase warp speed line overlay for a 2D sci-fi shooter, transparent foreground streaks, cyan magenta gold subtle energy lines, center mostly clear, designed for fast scrolling stage transition, no text, no logo, no watermark.
```

## Laser Beam Cyan

```text
Long cyan laser beam VFX sprite for a vertical sci-fi shooter, blue white core, soft cyan glow edges, vertical orientation, transparent background, seamless beam segment, no text, no logo, no watermark.
```

## Warning Line Magenta

```text
Magenta warning targeting line VFX sprite for a vertical sci-fi shooter, thin vertical laser warning beam, subtle pulse glow, transparent background, no text, no logo, no watermark.
```

## Coin Pickup Gold

```text
Gold coin pickup sprite for a sci-fi arcade shooter, small luminous energy coin, beveled rim, white highlight, transparent background, centered icon, readable at small size, no text, no numbers, no logo, no watermark.
```

## Gem Currency Magenta

```text
Magenta crystal gem currency icon for a premium 2D sci-fi shooter, faceted neon crystal, white specular highlights, subtle cyan edge glow, transparent background, centered icon, readable at 24 pixels, no text, no numbers, no logo, no watermark.
```

## Cash Pickup Green

```text
Green holographic cash bundle pickup icon for a sci-fi arcade shooter, stacked luminous credit notes, emerald glow, clean readable silhouette, transparent background, centered icon, readable at small size, no text, no numbers, no logo, no watermark.
```

## Best Score Trophy Cyan

```text
Cyan holographic trophy icon for best score UI in a mobile sci-fi shooter, compact cup silhouette, white highlights, premium glass energy material, transparent background, centered icon, readable at 24 pixels, no text, no numbers, no logo, no watermark.
```

## Ship Unlock Burst

```text
Ship unlock celebration burst VFX sprite for a sci-fi mobile shooter, cyan gold radial burst, small star sparks, premium reward feeling, transparent background, centered composition, no text, no logo, no watermark.
```
