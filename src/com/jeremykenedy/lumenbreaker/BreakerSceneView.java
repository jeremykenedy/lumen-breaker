package com.jeremykenedy.lumenbreaker;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader;
import android.preference.PreferenceManager;
import android.view.Choreographer;
import android.view.View;
import java.util.Random;

/** Crisp native-resolution ceramic forms; no gameplay text or overlays. */
public final class BreakerSceneView extends View implements Choreographer.FrameCallback {
  private static final int[][] PALETTES = {
    {0xff82bdc7, 0xffa1b69a, 0xffd2b382, 0xffc98d82, 0xffa79bc5},
    {0xff73bec7, 0xff6b9fba, 0xff8bacc4, 0xff9ccac0, 0xffc6c8a3},
    {0xffd6ae71, 0xffd08d70, 0xffb97071, 0xffc0a17f, 0xff94ac91},
    {0xffa99bd0, 0xffbf91bc, 0xff8fadd0, 0xffd0aaa8, 0xff8ebbb0}
  };
  private final BreakerOptions options;
  private final BreakerGame game;
  private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
  private final RectF rect = new RectF();
  private final Matrix shaderMatrix = new Matrix();
  private final int[] colors = new int[5];
  private final Shader[] ceramic = new Shader[5];
  private Shader background;
  private boolean running;
  private long previousFrame;
  private float scale, left, top;

  public BreakerSceneView(Context context) {
    super(context);
    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
    String[] selected = new String[BreakerOptions.SCHEMA.length];
    for (int i = 0; i < selected.length; i++) {
      String[] row = BreakerOptions.SCHEMA[i];
      selected[i] = preferences.getString(row[0], row[2]);
    }
    Random random = new Random();
    options = new BreakerOptions(selected, random, preferences.getBoolean("randomize_all", false));
    game = new BreakerGame(options, random);
    for (int i = 0; i < colors.length; i++) {
      colors[i] = light(PALETTES[options.palette][i], options.brightness);
      ceramic[i] =
          new LinearGradient(
              0, 0, 0, 1, light(colors[i], 1.08f), light(colors[i], .83f), Shader.TileMode.CLAMP);
    }
    paint.setDither(true);
    setFocusable(false);
    setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_NO);
  }

  public void dumpScene(java.io.PrintWriter out) {
    out.println("Lumen Breaker View=" + getWidth() + "x" + getHeight() + " running=" + running);
    out.println(
        "Fixed ticks="
            + game.ticks
            + " ballCount="
            + game.balls.length
            + " speed="
            + options.speed
            + " remaining="
            + game.remaining);
    out.println(
        "Brick hits="
            + game.hits
            + " paddle hits="
            + game.paddleHits
            + " misses="
            + game.misses
            + " clears="
            + game.clears);
  }

  public void start() {
    if (running) return;
    running = true;
    previousFrame = 0;
    Choreographer.getInstance().postFrameCallback(this);
  }

  public void stop() {
    running = false;
    previousFrame = 0;
    Choreographer.getInstance().removeFrameCallback(this);
  }

  public void release() {
    stop();
  }

  @Override
  protected void onDetachedFromWindow() {
    stop();
    super.onDetachedFromWindow();
  }

  @Override
  public void doFrame(long nanos) {
    if (!running) return;
    if (previousFrame != 0) game.advance(Math.max(0, (nanos - previousFrame) * .000000001));
    previousFrame = nanos;
    invalidate();
    Choreographer.getInstance().postFrameCallback(this);
  }

  @Override
  protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
    scale = Math.min(width / 1.08f, height / .62f);
    left = (width - scale) / 2;
    top = (height - scale * .56f) / 2;
    background =
        new RadialGradient(
            width * .5f,
            height * .25f,
            Math.max(width, height) * .8f,
            new int[] {0xff192634, 0xff0b141e, 0xff070c13},
            null,
            Shader.TileMode.CLAMP);
  }

  @Override
  protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);
    paint.setShader(background);
    canvas.drawRect(0, 0, getWidth(), getHeight(), paint);
    paint.setShader(null);
    if (scale <= 0) return;
    float time = game.ticks * (float) BreakerGame.STEP;
    float alpha = game.interpolation();
    canvas.save();
    canvas.translate(
        left + options.drift * scale * (float) Math.sin(time * .045),
        top + options.drift * scale * .45f * (float) Math.sin(time * .031));
    canvas.scale(scale, scale);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(.0007f);
    paint.setColor(0xff253544);
    rect.set(
        BreakerGame.LEFT - .013f,
        BreakerGame.TOP - .016f,
        BreakerGame.RIGHT + .013f,
        BreakerGame.BOTTOM + .006f);
    canvas.drawRoundRect(rect, .015f, .015f, paint);
    paint.setStyle(Paint.Style.FILL);
    for (BreakerGame.Brick brick : game.bricks) {
      if (brick.health == 0) continue;
      paint.setColor(0x42000000);
      rect.set(brick.x, brick.y + .002f, brick.x + brick.width, brick.y + brick.height + .002f);
      canvas.drawRoundRect(rect, .002f, .002f, paint);
      rect.offset(0, -.002f);
      shaderMatrix.setScale(1, brick.height);
      shaderMatrix.postTranslate(0, brick.y);
      ceramic[brick.color].setLocalMatrix(shaderMatrix);
      paint.setShader(ceramic[brick.color]);
      paint.setAlpha(255);
      canvas.drawRoundRect(rect, .002f, .002f, paint);
      paint.setShader(null);
      paint.setStrokeWidth(.00065f);
      paint.setColor(0x68ffffff);
      canvas.drawLine(
          brick.x + .002f, brick.y + .001f, brick.x + brick.width - .002f, brick.y + .001f, paint);
      if (brick.health > 1) {
        paint.setColor(0x360c1924);
        canvas.drawLine(
            brick.x + .004f,
            brick.y + brick.height - .004f,
            brick.x + brick.width - .004f,
            brick.y + brick.height - .004f,
            paint);
      }
      if (brick.flash > 0) {
        paint.setColor(Color.argb((int) (brick.flash * 550), 255, 255, 255));
        canvas.drawRoundRect(rect, .002f, .002f, paint);
      }
    }
    for (BreakerGame.Fragment fragment : game.fragments) {
      if (fragment.life <= 0) continue;
      paint.setColor(colors[fragment.color]);
      paint.setAlpha((int) (Math.min(1, fragment.life / .2f) * 190));
      rect.set(
          fragment.x - fragment.size,
          fragment.y - fragment.size,
          fragment.x + fragment.size,
          fragment.y + fragment.size);
      canvas.drawRect(rect, paint);
    }
    paint.setAlpha(255);
    float paddle = game.previousPaddle + (game.paddle - game.previousPaddle) * alpha;
    paint.setColor(0x65000000);
    rect.set(
        paddle - BreakerGame.PADDLE_WIDTH / 2,
        BreakerGame.PADDLE_Y + .002f,
        paddle + BreakerGame.PADDLE_WIDTH / 2,
        BreakerGame.PADDLE_Y + .015f);
    canvas.drawRoundRect(rect, .005f, .005f, paint);
    paint.setColor(light(0xffcad7db, options.brightness));
    rect.offset(0, -.003f);
    canvas.drawRoundRect(rect, .005f, .005f, paint);
    paint.setColor(game.paddlePulse > 0 ? 0xfff4ffff : 0xff8fb7c4);
    rect.bottom = rect.top + .0017f;
    canvas.drawRoundRect(rect, .001f, .001f, paint);
    for (BreakerGame.Ball ball : game.balls) {
      float x = ball.previousX + (ball.x - ball.previousX) * alpha;
      float y = ball.previousY + (ball.y - ball.previousY) * alpha;
      int length = options.effects == 0 ? 5 : options.effects == 1 ? 11 : 17;
      paint.setStrokeCap(Paint.Cap.ROUND);
      for (int i = length; i > 0; i--) {
        int a = (ball.head - i + ball.trailX.length) % ball.trailX.length;
        int b = (a + 1) % ball.trailX.length;
        paint.setColor(0xff9ad6df);
        paint.setAlpha((int) ((1 - i / (float) (length + 1)) * 100 * options.brightness));
        paint.setStrokeWidth(BreakerGame.RADIUS * 1.3f * (1 - i / (float) (length + 1)));
        canvas.drawLine(
            ball.trailX[a],
            ball.trailY[a],
            b == ball.head ? x : ball.trailX[b],
            b == ball.head ? y : ball.trailY[b],
            paint);
      }
      paint.setColor(0x149ad6df);
      if (options.effects > 0) canvas.drawCircle(x, y, BreakerGame.RADIUS * 1.65f, paint);
      paint.setColor(light(0xffedfafa, options.brightness));
      canvas.drawCircle(x, y, BreakerGame.RADIUS, paint);
      paint.setColor(0xfffeffff);
      canvas.drawCircle(x - .001f, y - .0015f, BreakerGame.RADIUS * .35f, paint);
    }
    paint.setAlpha(255);
    canvas.restore();
  }

  private static int light(int color, float value) {
    return Color.rgb(
        Math.min(255, Math.round(Color.red(color) * value)),
        Math.min(255, Math.round(Color.green(color) * value)),
        Math.min(255, Math.round(Color.blue(color) * value)));
  }
}
