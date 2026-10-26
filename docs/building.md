# Build and tests

Requirements: a JDK with Java 8 source support, Android SDK platform 36 and build-tools 36.0.0, Python 3.10+, curl, unzip, zip and OpenSSL. Host checks download checksum-pinned Gradle. Python development dependencies are pinned separately.

```sh
bash build.sh --unsigned
LUMEN_TEST_APK=build/lumen-breaker-unsigned.apk bash test.sh
bash scripts/test-python-coverage.sh
python3 scripts/check-docs.py
```

Unsigned CI artifacts are for review. Release signing uses the unique Lumen Breaker key outside Git, defaulting to `~/.android/lumen-breaker.jks` and its separate password file. Preserve both securely; another certificate cannot update an installed build. Override paths with `LUMEN_BREAKER_KEYSTORE` and `LUMEN_BREAKER_KEYPASS`, then run `bash build.sh` locally. Never upload signing material to CI.

The JVM gate requires 100% line and branch coverage of all owned Android-free physics and options. Android Canvas/View, activities, provider and Dream framework adapters are explicitly outside JVM coverage and require native behavior checks. Installer tests enforce 100% branch coverage of its download, confirmation, device selection and failure paths. Manifest/APK checks verify identity, permissions, notices and offline packaging. CI produces only unsigned review APKs.

Native validation is recorded separately in [verification](verification.md). Neither coverage nor a successful build establishes visual quality or every-device compatibility.
