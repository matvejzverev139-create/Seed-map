# Seed Map

Fabric client mod for Minecraft 1.21.11.

## Current functionality

- Press `M` to open the in-game map.
- In singleplayer, the integrated server seed is filled automatically.
- A seed can be entered manually, which is required on multiplayer servers.
- Separate Overworld, Nether, and End tabs are available.
- The HUD shows the known seed and the `M` shortcut.

## Build

```bash
gradle build
```

The remapped jar is created in `build/libs/`.

## Important limitation

The current `SeedMapLocator` is a compile-tested placeholder that renders deterministic sample markers. It is not yet a Chunkbase-compatible structure calculator. Exact locations for all vanilla structures require porting the 1.21.11 structure placement rules, including spacing, separation, salts, biome checks, and dimension-specific rules, or integrating a compatible seed-mapping library.

The UI and seed/dimension plumbing are separated from that calculator so the exact locator can replace `SeedMapLocator.locate(...)` without rewriting the screen.
