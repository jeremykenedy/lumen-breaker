# Ecosystem integration

Catalog registration follows the verified signed app release. Do not pin a private candidate checksum as a public release.

| Field | Contract |
| --- | --- |
| Saver ID / repository | `lumen-breaker` / `jeremykenedy/lumen-breaker` |
| Package | `com.jeremykenedy.lumenbreaker` |
| Dream | `com.jeremykenedy.lumenbreaker/.LumenDreamService` |
| Settings | `com.jeremykenedy.lumenbreaker/.SettingsActivity` |
| APK asset | `lumen-breaker.apk` and `lumen-breaker.apk.sha256` |
| Min / target SDK | 23 /36 |
| Settings | `content://com.jeremykenedy.lumenbreaker.settings/settings` and `/schema` |
| License | Apache-2.0 |

Fire TV UI discovers installed DreamServices, opens native settings and supports screensaver selection. Its bundled thumbnail should derive from an inspected actual capture and use the host's canonical component hashing helper. Toolkit's reviewed registry entry needs the final release tag, artifact and published checksum. Never add a fictitious release hash.

After registration, these existing Toolkit commands apply:

```sh
firetv-screensavers --install=lumen-breaker
screensaver --set=lumen-breaker
firetv-screensavers --install=lumen-breaker
firetv-screensavers --uninstall=lumen-breaker --yes --force
```

The repeated install command updates. Select another saver before uninstall. Toolkit can also manage supported launcher choices, timers and setting protection; consult its current platform-specific limitations. Shared host repository edits are serialized separately from this app's release preparation.
