# Neon Wing

Neon Wing is a vertical 2D mobile shooter prototype for Android, with a browser test harness and release support documents.

## Public Pages

- Privacy policy: `https://hhy0111.github.io/neon-wing/`
- app-ads.txt: `https://hhy0111.github.io/neon-wing/app-ads.txt`

## Project Layout

- `app/` - Android project
- `web/` - browser test version
- `docs/` - GitHub Pages documents, asset prompts, release notes
- `image/` - generated source/reference art assets

## Build

```powershell
.\gradlew.bat :app:assembleDebug
```

## Web Test

```powershell
npm run web
```
