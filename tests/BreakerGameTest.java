package com.jeremykenedy.lumenbreaker;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.junit.Test;

public class BreakerGameTest {
  private BreakerOptions options(String... pairs) {
    String[] values = new String[BreakerOptions.SCHEMA.length];
    for (int i = 0; i < values.length; i++) values[i] = BreakerOptions.SCHEMA[i][2];
    for (int i = 0; i < pairs.length; i += 2) values[BreakerOptions.row(pairs[i])] = pairs[i + 1];
    return new BreakerOptions(values, new Random(1), false);
  }

  private BreakerGame game(String... pairs) {
    return new BreakerGame(options(pairs), new Random(7));
  }

  private void seconds(BreakerGame game, double seconds) {
    for (int i = 0; i < Math.ceil(seconds * 120); i++) game.advance(BreakerGame.STEP);
  }

  private BreakerGame.Ball moving(BreakerGame g, float x, float y, float vx, float vy) {
    BreakerGame.Ball b = g.balls[0];
    b.wait = 0;
    b.x = b.previousX = x;
    b.y = b.previousY = y;
    b.vx = vx;
    b.vy = vy;
    return b;
  }

  @Test
  public void schemaRejectsUnknownValuesAndKeepsUsefulDefaults() {
    assertEquals(-1, BreakerOptions.row(null));
    assertFalse(BreakerOptions.supported("missing", "steady"));
    assertFalse(BreakerOptions.supported("speed", null));
    assertFalse(BreakerOptions.supported("randomize_all", "yes"));
    assertTrue(BreakerOptions.supported("randomize_all", "true"));
    assertTrue(BreakerOptions.supported("randomize_all", "false"));
    assertThrows(IllegalArgumentException.class, () -> BreakerOptions.validated("missing", "x"));
    for (String[] row : BreakerOptions.SCHEMA) {
      assertEquals(row[2], BreakerOptions.validated(row[0], "invalid"));
      assertTrue(BreakerOptions.supported(row[0], "random"));
      for (int i = 3; i < row.length; i++) assertTrue(BreakerOptions.supported(row[0], row[i]));
    }
    assertEquals(11, options().columns);
    assertEquals(1, options().ballCount);
    assertTrue(options("speed", "brisk").speed > options("speed", "relaxed").speed);
    assertTrue(options("density", "dense").columns > options("density", "airy").columns);
    assertEquals(3, options("balls", "trio").ballCount);
    assertEquals(0, options("effects", "clean").effects);
    assertEquals(0, options("drift", "none").drift, 0);
    assertTrue(
        options("brightness", "bright").brightness > options("brightness", "dim").brightness);
    assertThrows(
        IllegalArgumentException.class, () -> new BreakerOptions(null, new Random(), false));
    assertThrows(
        IllegalArgumentException.class,
        () -> new BreakerOptions(new String[1], new Random(), false));
    assertThrows(
        IllegalArgumentException.class, () -> new BreakerOptions(new String[8], null, false));
  }

  @Test
  public void randomChoicesAreSeededAndRemainInSchemaBounds() {
    String[] values = new String[8];
    Arrays.fill(values, "random");
    Set<Integer> palettes = new HashSet<>();
    for (int seed = 0; seed < 100; seed++) {
      BreakerOptions a = new BreakerOptions(values, new Random(seed), false);
      BreakerOptions b = new BreakerOptions(values, new Random(seed), true);
      assertEquals(a.palette, b.palette);
      assertEquals(a.layout, b.layout);
      assertTrue(a.columns >= 9 && a.columns <= 13);
      assertTrue(a.ballCount >= 1 && a.ballCount <= 3);
      assertTrue(a.palette >= 0 && a.palette < 4);
      palettes.add(a.palette);
    }
    assertEquals(4, palettes.size());
  }

  @Test
  public void fixedStepsAreIndependentOfPresentationRateAndBoundCatchup() {
    BreakerGame a = game(), b = game();
    for (int i = 0; i < 900; i++) a.advance(1.0 / 60);
    for (int i = 0; i < 1800; i++) b.advance(BreakerGame.STEP);
    assertEquals(a.ticks, b.ticks);
    assertEquals(a.hits, b.hits);
    assertEquals(a.balls[0].x, b.balls[0].x, 0);
    long before = a.ticks;
    a.advance(30);
    assertTrue(a.ticks - before <= 13);
    assertTrue(a.interpolation() >= 0 && a.interpolation() < 1);
    assertThrows(IllegalArgumentException.class, () -> a.advance(-1));
    assertThrows(IllegalArgumentException.class, () -> a.advance(Double.NaN));
    assertThrows(IllegalArgumentException.class, () -> a.advance(Double.POSITIVE_INFINITY));
    assertThrows(IllegalArgumentException.class, () -> new BreakerGame(null, new Random()));
    assertThrows(IllegalArgumentException.class, () -> new BreakerGame(options(), null));
  }

  @Test
  public void sideAndCeilingContactsReflectWithoutCrossingBounds() {
    BreakerGame g = game();
    BreakerGame.Ball b = moving(g, BreakerGame.LEFT + .003f, .4f, -.3f, -.3f);
    g.advance(BreakerGame.STEP);
    assertTrue(b.vx > 0);
    assertTrue(b.x >= BreakerGame.LEFT + BreakerGame.RADIUS);
    moving(g, BreakerGame.RIGHT - .003f, .4f, .3f, -.3f);
    g.advance(BreakerGame.STEP);
    assertTrue(b.vx < 0);
    assertTrue(b.x <= BreakerGame.RIGHT - BreakerGame.RADIUS);
    moving(g, .5f, BreakerGame.TOP + .003f, .3f, -.3f);
    g.advance(BreakerGame.STEP);
    assertTrue(b.vy > 0);
    float span = BreakerGame.RIGHT - BreakerGame.LEFT - 2 * BreakerGame.RADIUS;
    assertEquals(
        BreakerGame.LEFT + BreakerGame.RADIUS,
        BreakerGame.reflected(BreakerGame.LEFT + BreakerGame.RADIUS + 2 * span),
        .000001f);
    assertEquals(
        BreakerGame.RIGHT - BreakerGame.RADIUS,
        BreakerGame.reflected(BreakerGame.LEFT + BreakerGame.RADIUS + span),
        .000001f);
    for (int i = -20; i <= 20; i++) assertTrue(BreakerGame.reflected(i * .17f) >= BreakerGame.LEFT);
  }

  @Test
  public void paddleContactHasAngleControlAndCannotTrapAColumn() {
    for (float offset : new float[] {0, -.066f, .066f, -.074f, .074f}) {
      BreakerGame g = game();
      BreakerGame.Ball b =
          moving(g, .5f + offset, BreakerGame.PADDLE_Y - BreakerGame.RADIUS - .001f, 0, .57f);
      g.advance(BreakerGame.STEP);
      assertEquals(1, g.paddleHits);
      assertTrue(b.vy < 0);
      assertTrue(Math.abs(b.vx) >= g.options.speed * .159f);
      assertEquals(g.options.speed, Math.hypot(b.vx, b.vy), .00001);
    }
  }

  @Test
  public void anActualMissFallsPastPaddleThenRelaunchesWithTheBricksRetained() {
    BreakerGame g = game();
    int remaining = g.remaining;
    BreakerGame.Ball b = moving(g, .1f, BreakerGame.BOTTOM + .01f, -.2f, .5f);
    g.advance(BreakerGame.STEP);
    assertEquals(1, g.misses);
    assertTrue(b.wait > 0);
    assertEquals(remaining, g.remaining);
    assertEquals(g.paddle, b.x, 0);
    for (int i = 0; b.wait > 0 && i < 200; i++) g.advance(BreakerGame.STEP);
    assertTrue(b.wait <= 0);
    assertTrue(b.vy < 0);
    assertEquals(remaining, g.remaining);
  }

  @Test
  public void circleBrickNormalsReflectCornersAndSeparateInitialOverlaps() {
    BreakerGame.Brick brick = new BreakerGame.Brick();
    brick.x = .4f;
    brick.y = .2f;
    brick.width = .1f;
    brick.height = .04f;
    float[][] cases = {
      {.395f, .22f, .2f, 0},
      {.505f, .22f, -.2f, 0},
      {.45f, .195f, 0, .2f},
      {.45f, .245f, 0, -.2f},
      {.396f, .196f, .2f, .2f},
      {.401f, .22f, .2f, 0},
      {.499f, .22f, -.2f, 0},
      {.45f, .201f, 0, .2f},
      {.45f, .239f, 0, -.2f}
    };
    for (float[] c : cases) {
      BreakerGame.Ball b = new BreakerGame.Ball();
      b.x = c[0];
      b.y = c[1];
      b.vx = c[2];
      b.vy = c[3];
      double speed = Math.hypot(b.vx, b.vy);
      assertTrue(BreakerGame.collide(b, brick));
      assertEquals(speed, Math.hypot(b.vx, b.vy), .00001);
      assertFalse(BreakerGame.collide(b, brick));
    }
    BreakerGame.Ball away = new BreakerGame.Ball();
    away.x = .395f;
    away.y = .22f;
    away.vx = -.2f;
    assertFalse(BreakerGame.collide(away, brick));
    assertEquals(-.2f, away.vx, 0);
    away.x = .3f;
    assertFalse(BreakerGame.collide(away, brick));
  }

  @Test
  public void realBrickHitsDamageOnceEmitBoundedFragmentsAndDoNotTunnel() {
    for (String effects : new String[] {"clean", "lively"}) {
      BreakerGame g = game("effects", effects, "speed", "brisk");
      BreakerGame.Brick brick = Arrays.stream(g.bricks).filter(x -> x.health > 0).findFirst().get();
      int before = g.remaining;
      moving(
          g, brick.x + brick.width / 2, brick.y - BreakerGame.RADIUS - .001f, 0, g.options.speed);
      g.advance(BreakerGame.STEP);
      assertEquals(1, g.hits);
      assertEquals(before - 1, g.remaining);
      assertTrue(brick.flash > 0);
      assertTrue(g.balls[0].vy < 0);
      long particles = Arrays.stream(g.fragments).filter(x -> x.life > 0).count();
      assertEquals("clean".equals(effects) ? 0 : 4, particles);
    }
  }

  @Test
  public void simultaneousLastBrickContactsStartOnlyOneClearAndRecovery() {
    BreakerGame g = game("balls", "trio");
    BreakerGame.Brick last = Arrays.stream(g.bricks).filter(x -> x.health > 0).findFirst().get();
    for (BreakerGame.Brick b : g.bricks) b.health = 0;
    last.health = 1;
    g.remaining = 1;
    for (BreakerGame.Ball b : g.balls) {
      b.wait = 0;
      b.x = b.previousX = last.x + last.width / 2;
      b.y = b.previousY = last.y - BreakerGame.RADIUS - .001f;
      b.vx = 0;
      b.vy = g.options.speed;
    }
    g.advance(BreakerGame.STEP);
    assertEquals(1, g.hits);
    assertEquals(1, g.clears);
    assertEquals(0, g.remaining);
    for (BreakerGame.Ball b : g.balls) assertTrue(b.wait > 0);
    seconds(g, 1.1);
    assertEquals(1, g.clears);
    assertTrue(g.remaining > 0);
  }

  @Test
  public void clearingRoundsRebuildsDistinctFormationsAndProgressesDurability() {
    BreakerGame g = game();
    Set<String> shapes = new HashSet<>();
    for (int round = 0; round < 4; round++) {
      shapes.add(Arrays.toString(Arrays.stream(g.bricks).mapToInt(x -> x.health).toArray()));
      for (BreakerGame.Brick b : g.bricks) b.health = 0;
      g.remaining = 0;
      g.advance(BreakerGame.STEP);
      assertEquals(round + 1, g.clears);
      assertEquals(round + 1, g.round);
      assertTrue(g.balls[0].wait > 0);
      seconds(g, .5);
      assertEquals(0, g.remaining);
      seconds(g, .6);
      assertTrue(g.remaining > 0);
    }
    assertEquals(4, shapes.size());
    assertTrue(Arrays.stream(g.bricks).anyMatch(x -> x.health == 2));
  }

  @Test
  public void relaxedSoloTargetsSurvivingBricksInsteadOfOrbitingAnEmptyLane() {
    BreakerGame g = game("speed", "relaxed");
    seconds(g, 600);
    assertTrue(g.clears > 0);
    assertTrue(g.hits > 50);
    assertTrue(g.paddleHits > 10);
    assertTrue(g.misses > 0);
    BreakerGame empty = game();
    for (BreakerGame.Brick b : empty.bricks) b.health = 0;
    empty.remaining = 0;
    moving(empty, .5f, .4f, .1f, .3f);
    empty.advance(BreakerGame.STEP);
    assertEquals(1, empty.clears);
  }

  @Test
  public void seededAutonomousGamesStayBoundedAndIncludeHitsAndPhysicalMisses() {
    int hits = 0, misses = 0, paddles = 0;
    for (String layout : new String[] {"gallery", "arches", "chevron", "terraces"}) {
      BreakerGame g =
          game(
              "layout", layout, "balls", "trio", "effects", "lively", "speed", "brisk", "density",
              "dense");
      for (int tick = 0; tick < 120 * 180; tick++) {
        g.advance(BreakerGame.STEP);
        for (BreakerGame.Ball b : g.balls) {
          assertTrue(Float.isFinite(b.x) && Float.isFinite(b.y));
          assertTrue(b.x >= BreakerGame.LEFT && b.x <= BreakerGame.RIGHT);
          assertTrue(b.y >= BreakerGame.TOP && b.y <= BreakerGame.BOTTOM + .02f);
        }
        assertTrue(g.remaining >= 0 && g.remaining <= g.bricks.length);
      }
      hits += g.hits;
      misses += g.misses;
      paddles += g.paddleHits;
      assertTrue(Arrays.stream(g.fragments).filter(x -> x.life > 0).count() <= 96);
    }
    assertTrue(hits > 100);
    assertTrue(paddles > 10);
    assertTrue(misses > 0);
  }
}
