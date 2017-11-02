package com.github.nik_sch.nabon_20.restaurantlist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class RestaurantContent {

  /**
   * An array of sample (dummy) items.
   */
  public static final List<Restaurant> ITEMS = new ArrayList<Restaurant>();

  /**
   * A map of sample (dummy) items, by ID.
   */
  //TODO look into sparseArray
  public static final Map<Integer, Restaurant> ITEM_MAP = new HashMap<Integer, Restaurant>();

//  private static final int COUNT = 25;

//  static {
//    // Add some sample items.
//    for (int i = 1; i <= COUNT; i++) {
//      addItem(createDummyItem(i));
//    }
//  }

  public static void addItem(Restaurant item) {
    ITEMS.add(item);
    ITEM_MAP.put(item.id, item);
  }
}
