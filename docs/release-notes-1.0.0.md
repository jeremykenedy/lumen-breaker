# Lumen Breaker 1.0.0

An original offline brick-breaker screensaver with ceramic formations, luminous
ball trails and continuous autonomous play. The paddle has finite travel and
reaction time, so balls can miss, recover and clear a board.

## Upgrade guide

This is the first public release. Install the signed APK and select Lumen Breaker
separately using a compatible picker. Installation and updates do not alter the
selected Dream or TV timers. The guided installer verifies downloaded release
bytes against the published SHA-256.

Private code 1/code 2 builds signed with the same certificate can update in place;
all nine option values were retained in the verified code 2-to3 upgrade. A build
signed with another certificate cannot replace it. Select another screensaver
before removing Lumen; removal deletes its local options. There are no database
migrations, accounts, runtime dependencies or network credentials to configure.

## Breaking changes

None from a previous public release. The declared minimum is Android 6.0/API 23
with DreamService support; target SDK 36. Only the combinations in the
[verification matrix](verification.md) have actually been tested. Native 4K,
API 28 hardware, sustained thermal behavior and other devices remain untested.

## Major changes

- Real fixed-step brick contacts, a limited-speed paddle, misses, relaunches and fresh rounds.
- Four authored formation families, four palettes and one-to-three-ball choices.
- Eight remote-friendly choices with per-option Random and randomize-all.
- Fullscreen native Preview and discoverable DreamService with offline persisted settings.
- No ads, analytics, tracking, requested permissions or app-runtime network access.

## Minor changes and fixes

- Opaque tile bodies remain correct after drawing their shadows.
- Actual native capture-derived Dream preview, standalone guided installer and device-specific documentation.

## Verification

Gameplay/options passed 100% line and branch coverage (228 lines/168 branches).
The installer passed 25 behavioral tests and 100% line/branch coverage
(231 lines/80 branches). Signed and unsigned artifact, notices, privacy,
documentation and required GitHub CI checks passed.

The unchanged runtime was checked on an API 34 emulator and a physical
AFTDEC012E API 30 TV at 1920x1080. Three bounded clean presentation windows each
read about 60 FPS for physical default, maximum load and ordinary idle Dream.
Normal five-minute activation was observed without changing the delay.
Independent default/max/Dream movie review found no new sample fault.
These are bounded observations, not universal 60 FPS or owner-acceptance claims.

The final signed code 3 APK passed in-place upgrade/retention, actual native
Preview/settings and app-provided thumbnail checks in the UI 69 picker without
Select or a Dream change. Full state restoration passed. Read the exact
[observations and limitations](verification.md) and [capture provenance](screenshots.md).

## Artifact

- APK: `lumen-breaker.apk` (74,551 bytes), version 1.0.0/code 3.
- SHA-256: `fc0513f85fc8563cd8463546108bbf5ea9c459ab72465c669b126fcc6c14269c`.
- A sibling `lumen-breaker.apk.sha256` is provided for verification.
- Signing certificate SHA-256: `d3404786108a0b54fab7bedb1119d3851bd688cdea9c4712155a9582852182e4`.

Toolkit/UI catalog registration follows release verification through the
[shared integration contract](integrations.md). There is no previous public tag
to compare.
