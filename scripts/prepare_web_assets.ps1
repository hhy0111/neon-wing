param(
    [string]$Root = (Resolve-Path ".").Path
)

$ErrorActionPreference = "Stop"

$sourceRoot = Join-Path $Root "image\ASSET_PROMPTS"
$effectSourceRoot = Join-Path $Root "image\EFFECT_ASSET_PROMPTS"
$assetRoot = Join-Path $Root "web\assets"

$processorSource = @"
using System;
using System.Collections.Generic;
using System.Drawing;
using System.Drawing.Imaging;
using System.IO;

public static class WebAssetProcessor
{
    private struct PointI
    {
        public int X;
        public int Y;
        public PointI(int x, int y) { X = x; Y = y; }
    }

    public static void MakeTransparentCrop(string inputPath, string outputPath)
    {
        using (var input = new Bitmap(inputPath))
        using (var bitmap = new Bitmap(input.Width, input.Height, PixelFormat.Format32bppArgb))
        {
            using (var g = Graphics.FromImage(bitmap))
            {
                g.DrawImage(input, 0, 0, input.Width, input.Height);
            }

            int width = bitmap.Width;
            int height = bitmap.Height;
            bool[,] background = new bool[width, height];
            var queue = new Queue<PointI>();

            Action<int, int> trySeed = (x, y) =>
            {
                if (x < 0 || y < 0 || x >= width || y >= height || background[x, y]) return;
                Color c = bitmap.GetPixel(x, y);
                if (!IsBackgroundCandidate(c)) return;
                background[x, y] = true;
                queue.Enqueue(new PointI(x, y));
            };

            for (int x = 0; x < width; x++)
            {
                trySeed(x, 0);
                trySeed(x, height - 1);
            }
            for (int y = 0; y < height; y++)
            {
                trySeed(0, y);
                trySeed(width - 1, y);
            }

            int[] dx = new int[] { 1, -1, 0, 0 };
            int[] dy = new int[] { 0, 0, 1, -1 };
            while (queue.Count > 0)
            {
                PointI p = queue.Dequeue();
                for (int i = 0; i < 4; i++)
                {
                    int nx = p.X + dx[i];
                    int ny = p.Y + dy[i];
                    if (nx < 0 || ny < 0 || nx >= width || ny >= height || background[nx, ny]) continue;
                    Color c = bitmap.GetPixel(nx, ny);
                    if (!IsBackgroundCandidate(c)) continue;
                    background[nx, ny] = true;
                    queue.Enqueue(new PointI(nx, ny));
                }
            }

            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    if (!background[x, y]) continue;
                    Color c = bitmap.GetPixel(x, y);
                    bitmap.SetPixel(x, y, Color.FromArgb(0, c.R, c.G, c.B));
                }
            }

            RemoveTinyDetachedForeground(bitmap);

            int minX = width;
            int minY = height;
            int maxX = -1;
            int maxY = -1;
            for (int y = 0; y < height; y++)
            {
                for (int x = 0; x < width; x++)
                {
                    if (bitmap.GetPixel(x, y).A <= 8) continue;
                    if (x < minX) minX = x;
                    if (y < minY) minY = y;
                    if (x > maxX) maxX = x;
                    if (y > maxY) maxY = y;
                }
            }

            Directory.CreateDirectory(Path.GetDirectoryName(outputPath));
            if (maxX < minX || maxY < minY)
            {
                bitmap.Save(outputPath, ImageFormat.Png);
                return;
            }

            int margin = 12;
            minX = Math.Max(0, minX - margin);
            minY = Math.Max(0, minY - margin);
            maxX = Math.Min(width - 1, maxX + margin);
            maxY = Math.Min(height - 1, maxY + margin);
            Rectangle crop = Rectangle.FromLTRB(minX, minY, maxX + 1, maxY + 1);
            using (var output = bitmap.Clone(crop, PixelFormat.Format32bppArgb))
            {
                output.Save(outputPath, ImageFormat.Png);
            }
        }
    }

    private static void RemoveTinyDetachedForeground(Bitmap bitmap)
    {
        int width = bitmap.Width;
        int height = bitmap.Height;
        bool[,] visited = new bool[width, height];
        var components = new List<List<PointI>>();
        int largest = 0;
        int[] dx = new int[] { 1, -1, 0, 0 };
        int[] dy = new int[] { 0, 0, 1, -1 };

        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                if (visited[x, y] || bitmap.GetPixel(x, y).A <= 8) continue;

                var component = new List<PointI>();
                var queue = new Queue<PointI>();
                visited[x, y] = true;
                queue.Enqueue(new PointI(x, y));

                while (queue.Count > 0)
                {
                    PointI p = queue.Dequeue();
                    component.Add(p);
                    for (int i = 0; i < 4; i++)
                    {
                        int nx = p.X + dx[i];
                        int ny = p.Y + dy[i];
                        if (nx < 0 || ny < 0 || nx >= width || ny >= height || visited[nx, ny]) continue;
                        if (bitmap.GetPixel(nx, ny).A <= 8) continue;
                        visited[nx, ny] = true;
                        queue.Enqueue(new PointI(nx, ny));
                    }
                }

                components.Add(component);
                if (component.Count > largest) largest = component.Count;
            }
        }

        int keepThreshold = Math.Max(80, largest / 900);
        foreach (var component in components)
        {
            if (component.Count >= keepThreshold) continue;
            foreach (PointI p in component)
            {
                Color c = bitmap.GetPixel(p.X, p.Y);
                bitmap.SetPixel(p.X, p.Y, Color.FromArgb(0, c.R, c.G, c.B));
            }
        }
    }

    private static bool IsBackgroundCandidate(Color c)
    {
        if (c.A < 24) return true;
        int max = Math.Max(c.R, Math.Max(c.G, c.B));
        int min = Math.Min(c.R, Math.Min(c.G, c.B));
        int spread = max - min;
        int brightness = (c.R + c.G + c.B) / 3;
        bool whiteOrChecker = brightness >= 190 && spread <= 36;
        bool neutralGray = brightness >= 22 && brightness <= 190 && spread <= 48;
        bool veryDimEdge = brightness < 22 && spread <= 24;
        return whiteOrChecker || neutralGray || veryDimEdge;
    }
}
"@

Add-Type -TypeDefinition $processorSource -ReferencedAssemblies System.Drawing

function Copy-Asset {
    param([string]$Source, [string]$Dest, [string]$SourceBase = $sourceRoot)
    $destPath = Join-Path $assetRoot $Dest
    New-Item -ItemType Directory -Force -Path (Split-Path $destPath -Parent) | Out-Null
    Copy-Item -LiteralPath (Join-Path $SourceBase $Source) -Destination $destPath -Force
}

function Convert-Sprite {
    param([string]$Source, [string]$Dest, [string]$SourceBase = $sourceRoot)
    $destPath = Join-Path $assetRoot $Dest
    New-Item -ItemType Directory -Force -Path (Split-Path $destPath -Parent) | Out-Null
    [WebAssetProcessor]::MakeTransparentCrop((Join-Path $SourceBase $Source), $destPath)
}

function Copy-OptionalAsset {
    param([string]$Source, [string]$Dest, [string]$SourceBase = $sourceRoot)
    if (Test-Path -LiteralPath (Join-Path $SourceBase $Source)) {
        Copy-Asset -Source $Source -Dest $Dest -SourceBase $SourceBase
    }
}

function Convert-OptionalSprite {
    param([string]$Source, [string]$Dest, [string]$SourceBase = $sourceRoot)
    if (Test-Path -LiteralPath (Join-Path $SourceBase $Source)) {
        Convert-Sprite -Source $Source -Dest $Dest -SourceBase $SourceBase
    }
}

$backgrounds = @{
    "22-neon-orbit-far.png" = "bg/neon_orbit_far.png"
    "23-neon-orbit-mid.png" = "bg/neon_orbit_mid.png"
    "24-neon-orbit-near.png" = "bg/neon_orbit_near.png"
    "25-scrap-belt-far.png" = "bg/scrap_belt_far.png"
    "26-scrap-belt-mid.png" = "bg/scrap_belt_mid.png"
    "27-scrap-belt-near.png" = "bg/scrap_belt_near.png"
    "28-crimson-foundry-far.png" = "bg/crimson_foundry_far.png"
    "29-crimson-foundry-mid.png" = "bg/crimson_foundry_mid.png"
    "30-crimson-foundry-near.png" = "bg/crimson_foundry_near.png"
    "31-frost-relay-far.png" = "bg/frost_relay_far.png"
    "32-frost-relay-mid.png" = "bg/frost_relay_mid.png"
    "33-frost-relay-near.png" = "bg/frost_relay_near.png"
    "34-void-citadel-far.png" = "bg/void_citadel_far.png"
    "35-void-citadel-mid.png" = "bg/void_citadel_mid.png"
    "36-void-citadel-near.png" = "bg/void_citadel_near.png"
}

$sprites = @{
    "04-neon-wing.png" = "ships/player_neon_wing.png"
    "05-astra.png" = "ships/player_astra.png"
    "07-1-scout.png" = "enemies/enemy_scout.png"
    "08-2-striker.png" = "enemies/enemy_striker.png"
    "09-3-wraith.png" = "enemies/enemy_wraith.png"
    "10-4-elite.png" = "enemies/enemy_elite.png"
    "37-shield-guard.png" = "enemies/enemy_shield_guard.png"
    "40-repair-pod.png" = "enemies/supply_drone.png"
    "14-homing-gold.png" = "missiles/missile_homing_gold.png"
    "15-micro-cyan.png" = "missiles/missile_micro_cyan.png"
    "16-cluster-orange.png" = "missiles/missile_cluster_orange.png"
    "17-plasma-magenta.png" = "missiles/missile_plasma_magenta.png"
    "18-emp-emp-blue.png" = "missiles/missile_emp_blue.png"
    "19-rail-white.png" = "missiles/missile_rail_white.png"
    "20-nova-gold.png" = "missiles/missile_nova_gold.png"
}

$effectSprites = @{
    "13-upgrade-chip-pickup.png" = "icons/icon_upgrade_chip.png"
    "14-field-energy-cell.png" = "icons/icon_field_energy.png"
    "15-repair-pickup.png" = "icons/icon_repair.png"
    "19-coin-pickup-gold.png" = "icons/icon_coin.png"
    "20-gem-currency-magenta.png" = "icons/icon_gem.png"
    "21-cash-pickup-green.png" = "icons/icon_cash.png"
    "22-best-score-trophy-cyan.png" = "icons/icon_trophy.png"
    "23-ship-unlock-burst.png" = "effects/ship_unlock_burst.png"
}

$optionalSprites = @{
    "62-title-logo-neon-wing-en.png" = "ui/title_logo_neon_wing_en.png"
    "63-player-raptor.png" = "ships/player_raptor.png"
    "64-player-bastion.png" = "ships/player_bastion.png"
    "65-player-seraph.png" = "ships/player_seraph.png"
    "66-player-phantom.png" = "ships/player_phantom.png"
    "67-player-nova-x.png" = "ships/player_nova_x.png"
    "70-ship-unlock-panel-bg.png" = "ui/ship_unlock_panel_bg.png"
    "71-upgrade-choice-panel-bg.png" = "ui/upgrade_choice_panel_bg.png"
}

$optionalCopies = @{
    "61-opening-splash.png" = "ui/opening_splash.png"
    "68-hangar-shop-bg.png" = "ui/hangar_shop_bg.png"
    "69-ship-select-bg.png" = "ui/ship_select_bg.png"
    "72-map-thumb-scrap-belt.png" = "maps/map_thumb_scrap_belt.png"
    "73-map-thumb-crimson-foundry.png" = "maps/map_thumb_crimson_foundry.png"
    "74-map-thumb-frost-relay.png" = "maps/map_thumb_frost_relay.png"
    "75-map-thumb-void-citadel.png" = "maps/map_thumb_void_citadel.png"
}

foreach ($entry in $backgrounds.GetEnumerator()) {
    Copy-Asset -Source $entry.Key -Dest $entry.Value
}

foreach ($entry in $sprites.GetEnumerator()) {
    Convert-Sprite -Source $entry.Key -Dest $entry.Value
}

foreach ($entry in $effectSprites.GetEnumerator()) {
    Convert-Sprite -Source $entry.Key -Dest $entry.Value -SourceBase $effectSourceRoot
}

foreach ($entry in $optionalSprites.GetEnumerator()) {
    Convert-OptionalSprite -Source $entry.Key -Dest $entry.Value
}

foreach ($entry in $optionalCopies.GetEnumerator()) {
    Copy-OptionalAsset -Source $entry.Key -Dest $entry.Value
}

$manifest = [ordered]@{
    images = [ordered]@{}
}

$optionalManifestEntries = [ordered]@{
    "ui_opening_splash" = "ui/opening_splash.png"
    "ui_title_logo_en" = "ui/title_logo_neon_wing_en.png"
    "ui_hangar_shop_bg" = "ui/hangar_shop_bg.png"
    "ui_ship_select_bg" = "ui/ship_select_bg.png"
    "ui_ship_unlock_panel_bg" = "ui/ship_unlock_panel_bg.png"
    "ui_upgrade_choice_panel_bg" = "ui/upgrade_choice_panel_bg.png"
    "map_scrap_belt" = "maps/map_thumb_scrap_belt.png"
    "map_crimson_foundry" = "maps/map_thumb_crimson_foundry.png"
    "map_frost_relay" = "maps/map_thumb_frost_relay.png"
    "map_void_citadel" = "maps/map_thumb_void_citadel.png"
}

foreach ($entry in $optionalManifestEntries.GetEnumerator()) {
    $candidate = Join-Path $assetRoot $entry.Value
    if (Test-Path -LiteralPath $candidate) {
        $manifest.images[$entry.Key] = "assets/" + $entry.Value.Replace("\", "/")
    }
}

$manifestPath = Join-Path $assetRoot "asset_manifest.json"
$manifest | ConvertTo-Json -Depth 4 | Set-Content -Path $manifestPath -Encoding UTF8

Write-Host "Prepared web assets in $assetRoot"
