"""Verify the app's identity and offline Android packaging contract."""
from pathlib import Path
import os
import subprocess
import unittest
import xml.etree.ElementTree as ET
from zipfile import ZipFile

ROOT = Path(__file__).resolve().parents[1]
ANDROID = '{http://schemas.android.com/apk/res/android}'


class PackagingTest(unittest.TestCase):
    def test_unique_dream_settings_and_provider_contract(self):
        manifest = ET.parse(ROOT / 'AndroidManifest.xml').getroot()
        self.assertEqual('com.jeremykenedy.lumenbreaker', manifest.get('package'))
        self.assertFalse(manifest.findall('uses-permission'))
        app = manifest.find('application')
        self.assertEqual('false', app.get(ANDROID + 'usesCleartextTraffic'))
        service = app.find('service')
        self.assertEqual('.LumenDreamService', service.get(ANDROID + 'name'))
        self.assertEqual('android.permission.BIND_DREAM_SERVICE', service.get(ANDROID + 'permission'))
        dream = ET.parse(ROOT / 'res/xml/dream.xml').getroot()
        self.assertEqual('com.jeremykenedy.lumenbreaker.SettingsActivity', dream.get(ANDROID + 'settingsActivity'))
        self.assertEqual('com.jeremykenedy.lumenbreaker.settings', app.find('provider').get(ANDROID + 'authorities'))
        activities = {x.get(ANDROID + 'name') for x in app.findall('activity')}
        self.assertEqual({'.PreviewActivity', '.SettingsActivity'}, activities)

    def test_runtime_is_framework_only_and_gameplay_has_no_text(self):
        sources = list((ROOT / 'src').rglob('*.java'))
        for path in sources:
            text = path.read_text()
            self.assertNotIn('java.net.', text)
            self.assertNotIn('android.webkit.', text)
            self.assertNotIn('android.telephony.', text)
        scene = (ROOT / 'src/com/jeremykenedy/lumenbreaker/BreakerSceneView.java').read_text()
        self.assertNotIn('drawText', scene)
        self.assertNotIn('setFixedSize', scene)
        self.assertIn('getWidth()', scene)
        self.assertIn('getHeight()', scene)
        self.assertIn('removeFrameCallback', scene)

    def test_apk_has_only_local_code_and_no_runtime_permissions(self):
        apk = Path(os.environ.get('LUMEN_TEST_APK', str(ROOT / 'build/lumen-breaker.apk')))
        if not apk.exists():
            self.fail('Build the APK before its artifact check.')
        with ZipFile(apk) as archive:
            self.assertIn('classes.dex', archive.namelist())
            self.assertEqual((ROOT / 'LICENSE').read_bytes(), archive.read('assets/LICENSE.txt'))
            self.assertEqual((ROOT / 'NOTICE').read_bytes(), archive.read('assets/NOTICE.txt'))
            self.assertFalse(any(name.startswith('lib/') or name.endswith('.jar') for name in archive.namelist()))
        sdk = Path(os.environ.get('ANDROID_HOME', str(Path.home() / 'Library/Android/sdk'))) / 'build-tools/36.0.0/aapt'
        permissions = subprocess.check_output([str(sdk), 'dump', 'permissions', str(apk)], text=True)
        self.assertNotIn('uses-permission:', permissions)
        badging = subprocess.check_output([str(sdk), 'dump', 'badging', str(apk)], text=True)
        self.assertIn("name='com.jeremykenedy.lumenbreaker'", badging)
        self.assertIn("versionCode='3'", badging)


if __name__ == '__main__':
    unittest.main()
