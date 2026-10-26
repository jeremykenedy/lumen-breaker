package com.jeremykenedy.lumenbreaker;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

public final class PreviewActivity extends Activity {
  private BreakerSceneView scene;

  @Override
  public void onCreate(Bundle state) {
    super.onCreate(state);
    getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    getWindow()
        .getDecorView()
        .setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    scene = new BreakerSceneView(this);
    setContentView(scene);
  }

  @Override
  protected void onResume() {
    super.onResume();
    if (scene != null) scene.start();
  }

  @Override
  protected void onPause() {
    if (scene != null) scene.stop();
    super.onPause();
  }

  @Override
  protected void onDestroy() {
    if (scene != null) scene.release();
    scene = null;
    super.onDestroy();
  }

  @Override
  public void dump(
      String prefix, java.io.FileDescriptor fd, java.io.PrintWriter out, String[] args) {
    if (scene != null) scene.dumpScene(out);
  }
}
