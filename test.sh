#!/usr/bin/env bash
set -euo pipefail
ROOT="$(cd "$(dirname "$0")" && pwd)"
VERSION=8.14.3
EXPECTED=bd71102213493060956ec229d946beee57158dbd89d0e62b91bca0fa2c5f3531
TOOLS="$ROOT/build/tools"
ARCHIVE="$TOOLS/gradle-$VERSION-bin.zip"
mkdir -p "$TOOLS"
if [[ ! -f "$ARCHIVE" ]]; then
  curl --proto '=https' --proto-redir '=https' -fsSL --retry 3 \
    "https://services.gradle.org/distributions/gradle-$VERSION-bin.zip" -o "$ARCHIVE"
fi
printf '%s  %s\n' "$EXPECTED" "$ARCHIVE" | shasum -a 256 -c - >/dev/null
if [[ ! -x "$TOOLS/gradle-$VERSION/bin/gradle" ]]; then unzip -q "$ARCHIVE" -d "$TOOLS"; fi
bash -n "$ROOT/build.sh"
python3 "$ROOT/tests/check_artifact.py"
"$TOOLS/gradle-$VERSION/bin/gradle" --no-daemon --console=plain -p "$ROOT" check jacocoTestReport
