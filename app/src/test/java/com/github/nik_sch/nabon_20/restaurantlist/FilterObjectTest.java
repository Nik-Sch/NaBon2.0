package com.github.nik_sch.nabon_20.restaurantlist;

import org.junit.Test;

import static org.junit.Assert.*;

import com.github.nik_sch.nabon_20.restaurantlist.RestaurantRecyclerViewAdapter.FilterObject;

public class FilterObjectTest {
  @Test
  public void applyFilterOn() throws Exception {
    FilterObject filter = new FilterObject("", "", 0, 437);
    Restaurant restaurant = new Restaurant();
    // empty test
    assertFalse(filter.applyFilterOn(restaurant));
    restaurant.price = "3,20 EUR";
    restaurant.name = "Restaurant";
    restaurant.address = "An address, 1000 Ljubljana";
    // filled restaurant1
    assertTrue(filter.applyFilterOn(restaurant));

    filter.name = "Rest";
    filter.address = "Ljubljana";
    assertTrue(filter.applyFilterOn(restaurant));

    filter.name = "Apfel";
    assertFalse(filter.applyFilterOn(restaurant));
    filter.name = "Rest";
    filter.address = "Koper";
    assertFalse(filter.applyFilterOn(restaurant));
  }

}