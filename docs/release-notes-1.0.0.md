# Lumen Breaker 1.0.0

Draft release notes. No release/tag is published. Final native packaging, independent moving review and release CI remain pending.

An original offline brick-breaker screensaver with restrained ceramic formations, luminous ball trails and autonomous play. The paddle has limited travel and reaction time; balls can miss, recover and clear a board.

## Upgrade guide

This is the first public release preparation. Install the signed APK with the guided installer or the reviewed Toolkit entry after publication, then select Lumen separately in a compatible picker. Installation does not alter screensaver selection or system timers. Existing private Lumen builds signed with the same certificate can update in place and retain options. Select another saver before removing Lumen; removal deletes its local preferences.

## Breaking changes

No previous public release. The declared minimum is Android 6.0/API 23 with DreamService support; only the matrix in [verification](verification.md) is actually tested. No timer, permission, account or network configuration migration is required.

## Major changes

- Autonomous fixed-step brick collisions, finite-speed paddle, misses/relaunches and continuously refreshed rounds.
- Four authored formation families and four palettes with density, speed and one-to-three-ball choices.
- Remote settings, fullscreen Preview and discoverable DreamService with offline persisted options.
- Per-choice Random and randomize-all, restrained effects, lighting and optional slow board drift.

## Minor changes and fixes

- Restore opaque tile bodies after drawing their shadows.
- Capture-derived native preview, device-specific screenshots and documented installer failure handling.

## Verification and limits

Runtime code 2 passed100% line/branch host gameplay/options coverage and bounded native tests on API 34 emulator and physical AFTDEC012E API 30 at1920x1080. Three separate clean physical presentation windows each read about 60 FPS in default, maximum load and the normal Dream. Ordinary five-minute idle activation was observed without changing the delay. Recording frame rates are not clean pacing evidence. Read [the exact observations and limits](verification.md).

API 28 hardware, native 4K, sustained thermal behavior and other devices are untested. Final packaging and publication checks remain pending. There is no previous public tag to compare.
