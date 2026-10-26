# Lumen Breaker prototype

This is a private original autonomous brick-breaker prototype for request R083.
It is not released, integrated or visually accepted.

The game uses 120 Hz fixed-step circle/rectangle collisions with interpolation,
a predictive paddle with finite reaction time and travel speed, real misses and
relaunches, four authored formation families, progressive durable bricks and
bounded impact fragments. Paddle return angles target surviving bricks rather
than running forever through a cleared lane. Geometry is drawn at actual native
View dimensions with restrained ceramic gradients, edge lighting and short ball
trails. There is no gameplay text, score HUD, watermark, runtime SDK or network.

Settings offer ball speed, four palettes, formation progression or fixed families,
three densities, one to three balls, impact effects, lighting and slow board drift.
Every choice supports Random, with an independent randomize-all option. The
app-owned provider is `content://com.jeremykenedy.lumenbreaker.settings/settings`;
`/schema` describes choices and defaults. Settings apply to the next Preview or
Dream attachment.

Build with `bash build.sh`; run `bash test.sh`. The unique signing key and password
are stored outside Git under `~/.android/lumen-breaker.*`. The APK uses minSdk23
and targetSdk36. These are build declarations, not tested device support.

Coverage includes all owned Android-free physics and option logic through explicit
source inclusion in build.gradle. Android Canvas, View/Dream lifecycle, provider
and settings adapters require native validation and are excluded from host JVM
coverage. The vector app banner is original launcher artwork, not a native scene
capture or an accepted ecosystem thumbnail.

Pending gates: actual native moving visual quality, provider and D-pad persistence,
real View dimensions and pacing, pause/resume/detach, physical idle Dream behavior,
memory/thermal behavior, independent visual review, final captures, public repository
and release checks. No TV, emulator, browser, shared host or memory calls were made
for this prototype. Native device access requires a fresh exclusive grant.
