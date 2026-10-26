package com.jeremykenedy.lumenbreaker;

import android.service.dreams.DreamService;

public final class LumenDreamService extends DreamService {
  private BreakerSceneView scene;

  @Override
  public void onAttachedToWindow() {
    super.onAttachedToWindow();
    setInteractive(false);
    setFullscreen(true);
    setScreenBright(false);
    scene = new BreakerSceneView(this);
    setContentView(scene);
  }

  @Override
  public void onDreamingStarted() {
    super.onDreamingStarted();
    if (scene != null) scene.start();
  }

  @Override
  public void onDreamingStopped() {
    if (scene != null) scene.stop();
    super.onDreamingStopped();
  }

  @Override
  public void onDetachedFromWindow() {
    if (scene != null) scene.release();
    scene = null;
    super.onDetachedFromWindow();
  }

  @Override
  protected void dump(java.io.FileDescriptor fd, java.io.PrintWriter out, String[] args) {
    if (scene != null) scene.dumpScene(out);
  }
}
