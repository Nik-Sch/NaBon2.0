package com.github.nik_sch.nabon_20.restaurantlist;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RestaurantContent {

  public static final List<Restaurant> ITEMS = new ArrayList<>();


  public static void addAll(Restaurant... items) {
    ITEMS.addAll(Arrays.asList(items));
  }
}
