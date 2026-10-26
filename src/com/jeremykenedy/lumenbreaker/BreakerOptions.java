package com.jeremykenedy.lumenbreaker;

import java.util.Arrays;
import java.util.Random;

public final class BreakerOptions {
  public static final String[][] SCHEMA = {
    {"speed", "Ball speed", "steady", "relaxed", "steady", "brisk"},
    {"palette", "Palette", "porcelain", "porcelain", "lagoon", "ember", "iris"},
    {"layout", "Formations", "gallery", "gallery", "arches", "chevron", "terraces"},
    {"density", "Brick density", "balanced", "airy", "balanced", "dense"},
    {"balls", "Balls", "solo", "solo", "duet", "trio"},
    {"effects", "Impact effects", "refined", "clean", "refined", "lively"},
    {"brightness", "Lighting", "balanced", "dim", "balanced", "bright"},
    {"drift", "Slow board drift", "slow", "none", "slow"}
  };

  public final int palette, layout, columns, rows, ballCount, effects;
  public final float speed, brightness, drift;

  public BreakerOptions(String[] values, Random random, boolean randomize) {
    if (values == null || values.length != SCHEMA.length || random == null)
      throw new IllegalArgumentException("One value per option and a random source are required");
    int[] choice = new int[SCHEMA.length];
    for (int i = 0; i < choice.length; i++) {
      String value = validated(SCHEMA[i][0], randomize ? "random" : values[i]);
      choice[i] =
          "random".equals(value)
              ? random.nextInt(SCHEMA[i].length - 3)
              : Arrays.asList(SCHEMA[i]).subList(3, SCHEMA[i].length).indexOf(value);
    }
    speed = new float[] {.42f, .57f, .72f}[choice[0]];
    palette = choice[1];
    layout = choice[2] - 1;
    columns = new int[] {9, 11, 13}[choice[3]];
    rows = new int[] {5, 6, 7}[choice[3]];
    ballCount = choice[4] + 1;
    effects = choice[5];
    brightness = new float[] {.62f, .84f, 1}[choice[6]];
    drift = choice[7] * .008f;
  }

  public static int row(String key) {
    for (int i = 0; i < SCHEMA.length; i++) if (SCHEMA[i][0].equals(key)) return i;
    return -1;
  }

  public static boolean supported(String key, String value) {
    if ("randomize_all".equals(key)) return "true".equals(value) || "false".equals(value);
    int row = row(key);
    if (row < 0 || value == null) return false;
    if ("random".equals(value)) return true;
    return Arrays.asList(SCHEMA[row]).subList(3, SCHEMA[row].length).contains(value);
  }

  public static String validated(String key, String value) {
    int row = row(key);
    if (row < 0) throw new IllegalArgumentException("Unknown setting");
    return supported(key, value) ? value : SCHEMA[row][2];
  }
}
