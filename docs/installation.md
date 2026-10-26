# Installation and removal

Use the signed APK and sibling SHA-256 file from the same [release](https://github.com/jeremykenedy/lumen-breaker/releases/latest), or the repository installer. Do not install an unsigned review artifact over a signed app.

```sh
python3 install.py --serial YOUR_TV_IP:5555
python3 install.py --serial YOUR_TV_IP:5555 --apk build/lumen-breaker.apk
```

The guided installer asks before changing the TV. `--yes` confirms install/update for scripts. It verifies downloaded release bytes against the published checksum and limits release URLs and asset redirects. A local APK is an explicit override; verify its origin yourself. Updates use the same package and signing certificate and retain saved options.

Installation does not select a screensaver or alter TV timers. Select Lumen Breaker separately in a compatible picker, such as Fire TV UI. Fire OS stock Settings may hide third-party dreams. A Preview alone does not prove automatic idle activation.

Select another screensaver before removal:

```sh
python3 install.py --serial YOUR_TV_IP:5555 --uninstall
```

Removal deletes Lumen's local options. Non-interactive removal requires both `--yes --force`. The installer does not modify other apps or disable platform updates. Restore the previous selection using your picker; uninstall does not guess the previous component. Cancellation or a nonzero ADB result is reported instead of silently continuing.
