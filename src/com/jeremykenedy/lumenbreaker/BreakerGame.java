package com.jeremykenedy.lumenbreaker;

import java.util.Random;

/** Fixed-step autonomous physics in a one-unit-wide landscape playfield. */
public final class BreakerGame {
  public static final double STEP = 1.0 / 120;
  public static final float LEFT = .045f, RIGHT = .955f, TOP = .045f, BOTTOM = .545f;
  public static final float RADIUS = .006f, PADDLE_Y = .49f, PADDLE_WIDTH = .145f;

  public static final class Brick {
    public float x, y, width, height, flash;
    public int health, color;
  }

  public static final class Ball {
    public float x, y, previousX, previousY, vx, vy, wait;
    public final float[] trailX = new float[18], trailY = new float[18];
    public int head;
  }

  public static final class Fragment {
    public float x, y, vx, vy, life, size;
    public int color;
  }

  public final Brick[] bricks;
  public final Ball[] balls;
  public final Fragment[] fragments = new Fragment[96];
  public final BreakerOptions options;
  public float paddle = .5f, previousPaddle = .5f, paddleVelocity, paddlePulse;
  public int remaining, round, hits, paddleHits, misses, clears;
  public long ticks;
  private final Random random;
  private double accumulator;
  private float target = .5f, reaction, clearWait;
  private int fragmentCursor;
  private final Brick paddleShape = new Brick();

  public BreakerGame(BreakerOptions options, Random random) {
    if (options == null || random == null)
      throw new IllegalArgumentException("Options and seed required");
    this.options = options;
    this.random = random;
    bricks = new Brick[options.columns * options.rows];
    for (int i = 0; i < bricks.length; i++) bricks[i] = new Brick();
    balls = new Ball[options.ballCount];
    for (int i = 0; i < balls.length; i++) balls[i] = new Ball();
    for (int i = 0; i < fragments.length; i++) fragments[i] = new Fragment();
    paddleShape.y = PADDLE_Y;
    paddleShape.width = PADDLE_WIDTH;
    paddleShape.height = .012f;
    formation();
    for (int i = 0; i < balls.length; i++) hold(balls[i], .4f + i * .25f);
  }

  public void advance(double elapsed) {
    if (Double.isNaN(elapsed) || Double.isInfinite(elapsed) || elapsed < 0)
      throw new IllegalArgumentException("Invalid elapsed time");
    accumulator += Math.min(.1, elapsed);
    while (accumulator >= STEP) {
      tick();
      accumulator -= STEP;
    }
  }

  public float interpolation() {
    return (float) (accumulator / STEP);
  }

  static float reflected(float x) {
    float span = RIGHT - LEFT - 2 * RADIUS;
    double value = (x - LEFT - RADIUS) / span;
    value -= Math.floor(value / 2) * 2;
    return LEFT + RADIUS + (float) (value <= 1 ? value : 2 - value) * span;
  }

  private float aim(float landing) {
    int selection = paddleHits % Math.max(1, remaining);
    for (Brick brick : bricks) {
      if (brick.health <= 0) continue;
      if (selection-- != 0) continue;
      float dx = brick.x + brick.width / 2 - landing;
      float dy = PADDLE_Y - brick.y - brick.height;
      float horizontal = (float) (dx / Math.hypot(dx, dy));
      horizontal = Math.max(-.72f, Math.min(.72f, horizontal));
      return landing - horizontal * (PADDLE_WIDTH / 2) / .8f;
    }
    return landing;
  }

  private void tick() {
    ticks++;
    previousPaddle = paddle;
    reaction -= STEP;
    if (reaction <= 0) {
      reaction = .105f;
      target = .5f;
      float earliest = Float.POSITIVE_INFINITY;
      for (Ball ball : balls) {
        if (ball.wait > 0 || ball.vy <= 0) continue;
        float time = (PADDLE_Y - RADIUS - ball.y) / ball.vy;
        if (time > 0 && time < earliest) {
          earliest = time;
          target = aim(reflected(ball.x + ball.vx * time));
        }
      }
    }
    float desired = Math.max(-.50f, Math.min(.50f, (target - paddle) * 9));
    paddleVelocity += Math.max(-.035f, Math.min(.035f, desired - paddleVelocity));
    paddle =
        Math.max(
            LEFT + PADDLE_WIDTH / 2,
            Math.min(RIGHT - PADDLE_WIDTH / 2, paddle + paddleVelocity * (float) STEP));
    paddleShape.x = paddle - PADDLE_WIDTH / 2;
    paddlePulse = Math.max(0, paddlePulse - (float) STEP);
    for (Fragment fragment : fragments) {
      if (fragment.life <= 0) continue;
      fragment.life -= STEP;
      fragment.x += fragment.vx * STEP;
      fragment.y += fragment.vy * STEP;
      fragment.vy += .30f * STEP;
    }
    for (Brick brick : bricks) brick.flash = Math.max(0, brick.flash - (float) STEP);
    if (clearWait > 0) {
      clearWait -= STEP;
      if (clearWait <= 0) formation();
    }
    for (Ball ball : balls) update(ball);
    if (remaining == 0 && clearWait <= 0) {
      clears++;
      round++;
      clearWait = 1;
      for (int i = 0; i < balls.length; i++) hold(balls[i], 1.25f + i * .2f);
    }
  }

  private void update(Ball ball) {
    ball.previousX = ball.x;
    ball.previousY = ball.y;
    if (ball.wait > 0) {
      ball.wait -= STEP;
      ball.x = paddle;
      ball.y = PADDLE_Y - .022f;
      if (ball.wait <= 0) {
        float horizontal = (.3f + random.nextFloat() * .3f) * (random.nextBoolean() ? 1 : -1);
        ball.vx = horizontal * options.speed;
        ball.vy = -(float) Math.sqrt(1 - horizontal * horizontal) * options.speed;
      }
    } else {
      ball.x += ball.vx * STEP;
      ball.y += ball.vy * STEP;
      if (ball.x < LEFT + RADIUS) {
        ball.x = LEFT + RADIUS;
        ball.vx = Math.abs(ball.vx);
      }
      if (ball.x > RIGHT - RADIUS) {
        ball.x = RIGHT - RADIUS;
        ball.vx = -Math.abs(ball.vx);
      }
      if (ball.y < TOP + RADIUS) {
        ball.y = TOP + RADIUS;
        ball.vy = Math.abs(ball.vy);
      }
      if (ball.vy > 0 && ball.previousY + RADIUS <= PADDLE_Y && collide(ball, paddleShape)) {
        float offset = (ball.x - paddle) / (PADDLE_WIDTH / 2);
        float horizontal = Math.max(-.86f, Math.min(.86f, offset * .8f + paddleVelocity * .12f));
        if (Math.abs(horizontal) < .16f) horizontal = horizontal < 0 ? -.16f : .16f;
        ball.vx = horizontal * options.speed;
        ball.vy = -(float) Math.sqrt(1 - horizontal * horizontal) * options.speed;
        ball.y = PADDLE_Y - RADIUS;
        paddleHits++;
        paddlePulse = .12f;
      }
      for (Brick brick : bricks) {
        if (brick.health > 0 && collide(ball, brick)) {
          brick.health--;
          brick.flash = .16f;
          hits++;
          if (brick.health == 0) remaining--;
          burst(ball.x, ball.y, brick.color);
          break;
        }
      }
      if (ball.y - RADIUS > BOTTOM) {
        misses++;
        hold(ball, .8f);
      }
    }
    ball.head = (ball.head + 1) % ball.trailX.length;
    ball.trailX[ball.head] = ball.x;
    ball.trailY[ball.head] = ball.y;
  }

  static boolean collide(Ball ball, Brick brick) {
    float nearestX = Math.max(brick.x, Math.min(brick.x + brick.width, ball.x));
    float nearestY = Math.max(brick.y, Math.min(brick.y + brick.height, ball.y));
    float dx = ball.x - nearestX, dy = ball.y - nearestY;
    float distance = (float) Math.sqrt(dx * dx + dy * dy);
    if (distance >= RADIUS) return false;
    float nx, ny, penetration;
    if (distance > .000001f) {
      nx = dx / distance;
      ny = dy / distance;
      penetration = RADIUS - distance;
    } else {
      float horizontal = ball.x < brick.x + brick.width / 2 ? -1 : 1;
      float vertical = ball.y < brick.y + brick.height / 2 ? -1 : 1;
      float xDepth = horizontal < 0 ? ball.x - brick.x : brick.x + brick.width - ball.x;
      float yDepth = vertical < 0 ? ball.y - brick.y : brick.y + brick.height - ball.y;
      nx = xDepth < yDepth ? horizontal : 0;
      ny = xDepth < yDepth ? 0 : vertical;
      penetration = RADIUS + Math.min(xDepth, yDepth);
    }
    ball.x += nx * (penetration + .00001f);
    ball.y += ny * (penetration + .00001f);
    float approach = ball.vx * nx + ball.vy * ny;
    if (approach >= 0) return false;
    ball.vx -= 2 * approach * nx;
    ball.vy -= 2 * approach * ny;
    return true;
  }

  private void hold(Ball ball, float delay) {
    ball.wait = delay;
    ball.vx = ball.vy = 0;
    ball.x = ball.previousX = paddle;
    ball.y = ball.previousY = PADDLE_Y - .022f;
    java.util.Arrays.fill(ball.trailX, ball.x);
    java.util.Arrays.fill(ball.trailY, ball.y);
  }

  private void burst(float x, float y, int color) {
    for (int i = 0; i < options.effects * 2; i++) {
      Fragment part = fragments[fragmentCursor];
      fragmentCursor = (fragmentCursor + 1) % fragments.length;
      double angle = random.nextDouble() * Math.PI * 2;
      float velocity = .045f + random.nextFloat() * .08f;
      part.x = x;
      part.y = y;
      part.vx = (float) Math.cos(angle) * velocity;
      part.vy = (float) Math.sin(angle) * velocity - .05f;
      part.life = .3f + random.nextFloat() * .25f;
      part.size = .0015f + random.nextFloat() * .0015f;
      part.color = color;
    }
  }

  private void formation() {
    int design = options.layout < 0 ? round % 4 : options.layout;
    float stepX = .78f / options.columns;
    remaining = 0;
    for (int i = 0; i < bricks.length; i++) {
      int column = i % options.columns, row = i / options.columns;
      float center = Math.abs(column - (options.columns - 1) / 2f);
      boolean occupied;
      switch (design) {
        case 0:
          occupied = row >= center * .45f && row < center * .45f + 3;
          break;
        case 1:
          occupied = row >= (options.rows - 1 - center * .55f) - 2;
          break;
        case 2:
          occupied = (column + row * 2) % 5 != 0;
          break;
        default:
          occupied =
              center + Math.abs(row - (options.rows - 1) / 2f) * 1.5f < options.columns * .48f;
      }
      Brick brick = bricks[i];
      brick.x = .11f + column * stepX;
      brick.y = .085f + row * .034f;
      brick.width = stepX - .007f;
      brick.height = .026f;
      brick.color = (row + round) % 5;
      brick.health = occupied ? 1 + ((round > 1 && i % 9 == 0) ? 1 : 0) : 0;
      brick.flash = 0;
      if (occupied) remaining++;
    }
  }
}
