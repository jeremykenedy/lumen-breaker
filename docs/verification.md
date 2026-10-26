# Device verification

## Status

Private release preparation. No published release or owner acceptance is claimed. Runtime code 2 was tested; independent moving review passed for runtime code 2; final production packaging and CI on the release commit remain pending.

| Device | Platform | Output | Evidence |
| --- | --- | --- | --- |
| Physical Amazon AFTDEC012E | Android 11/API 30 | 1920x 1080 | Native Preview/default and maximum load, D-pad/persistence, same-PID pause/resume and normal five-minute idle Dream |
| Google TV emulator | Android 14/API 34 | 1920x 1080 | D-pad/schema/invalid values, persistence, pause/resume/recreation, default89.70s and max 14.73s recordings |
| Other Fire TV/Android TV/Google TV | Untested | Unverified | Volunteers needed; report model, API/OS, resolution, behavior and results through the repository issue tracker |

On physical hardware, three clean SurfaceFlinger new-presentation windows each measured approximately 60 FPS for default Preview, maximum-load Preview and actual Dream. Each window was about1.5s and excluded recording load. These are bounded observations, not a sustained thermal or universal 60 FPS claim. A gfx counter divided by a narrower host query bracket read61.2-61.3; that proxy is qualified and is not used as the frame rate.

Actual PID memory snapshots: default77,675KiB PSS, max78,909KiB, Dream52,081KiB. They are snapshots, not peak-memory bounds. The renderer used actual 1920x 1080 Views. Native 4K, other hardware, reboot persistence and long thermal behavior remain untested.

The original 300000ms idle delay was unchanged. Last observed unbound at290.47s, first normal non-test Dream bound at301.00s; no input stamp changed during idle. Dream's separate moving observation lasted54.63s, with a49.55s native recording. Preview recordings lasted24.52s(default) and 14.54s(max). Recording frame rate is not clean application pacing.

All59 preexisting APK hashes, UIDs, runtime grants, scoped appops, provider values,51UI preferences, external bytes/existence and tracked OS/network configuration were restored. Original Aerial selection was restored. Final Main was awake with no Dream, two bound accessibility services and guard present. Lumen2 was retained stopped after final provider reads. Normal live memory counters and IP lifetime passage were qualified rather than written back.

No API 28/ARM32, all-device compatibility, final production package or owner visual acceptance is inferred from this gate. Capture provenance is in [the gallery](screenshots.md).

## Independent moving review and browser documentation

A separate reviewer played the physical default, maximum-load and normal Dream
recordings at normal speed through their endings and sampled actual frames. The
longer emulator default recording was also reviewed. No new sample fault was
identified. This supports final packaging validation, not owner acceptance or
an every-frame certificate.

Local rendered README checks covered light/dark themes, desktop 1280px and
mobile 320px, each with normal and reduced motion. All eight banner sources
loaded; normal versions changed after 1.4 seconds and still versions remained identical.
Real-time 8.3-second sampled loops covered the 7-second cycle at desktop and mobile size without
illustration escape or text clipping. The native screenshot gallery loaded all
three original 1920x1080 captures. These browser checks validate documentation,
not native gameplay performance. Final code 3 packaging and screenshots remain
pending the separate native gate.
