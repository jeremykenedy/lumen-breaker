# Release procedure

1. Finish independent native moving review and final packaging checks. Freeze the exact APK and source; preserve the existing unique signing key.
2. Complete README/banner browser review at desktop and320px, both themes, motion and reduced motion. Review actual native screenshots and final embedded preview.
3. Run `bash build.sh`, `bash test.sh`, `bash scripts/test-python-coverage.sh`, `python3 scripts/check-docs.py` and `git diff --check`. Verify signer, identity, notices, permissions and dependency boundary.
4. Create the public repository only with publication authorization and finished source. Configure About/website/topics, sponsor metadata and actual available CI providers. Do not add paid/unavailable provider badges or fabricated grades.
5. Merge through the normal protected primary-branch workflow. Fetch and verify remote primary tip, then run final required CI on that exact commit. Do not publish from this preparation branch.
6. Use SemVer `v1.0.0` for the initial public release; verify it resolves to the exact verified primary tip. Attach signed `lumen-breaker.apk` plus its SHA-256 file. Keep notes in a file and use `--notes-file`.
7. Download published artifacts and verify checksum/signer. Update About/topics, owner star and All Activity watch, then hand exact metadata to serialized Toolkit/UI integration. Add the completed public URL to the completed inventory only after both integrations pass.

Current staging has no public tag or release. No primary-branch claim is made. Do not treat a prototype's physical gate as validation of a modified APK.
