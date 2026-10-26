# Configuration

Open Lumen Breaker from your TV's app list. Up and Down move focus; Left, Right or Select change a choice. Preview animation opens fullscreen play. Back returns to settings. Home leaves the app. Changes apply to the next Preview or Dream attachment.

| Setting | Default | Choices |
| --- | --- | --- |
| Ball speed | Steady | Relaxed, Steady, Brisk |
| Palette | Porcelain | Porcelain, Lagoon, Ember, Iris |
| Formations | Gallery | Gallery, Arches, Chevron, Terraces |
| Brick density | Balanced | Airy, Balanced, Dense |
| Balls | Solo | Solo, Duet, Trio |
| Impact effects | Refined | Clean, Refined, Lively |
| Lighting | Balanced | Dim, Balanced, Bright |
| Slow board drift | Slow | None, Slow |

Every choice also supports Random. Randomize all on start chooses all eight options each time a new scene starts without overwriting the saved individual choices. Gallery progresses through the four authored formation families; a named formation keeps that family as rounds advance.

The exported app-owned provider is `content://com.jeremykenedy.lumenbreaker.settings/settings`; `/schema` returns keys, allowed values and defaults. It accepts only the declared keys and values. It contains visual preferences, not credentials. Other installed apps can read and change these preferences. [Integration contract](integrations.md).
