package com.github.nik_sch.nabon_20.restaurantlist;

import com.github.nik_sch.nabon_20.Networking;
import com.google.gson.Gson;

import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import static org.junit.Assert.*;

/**
 * Created by niklas on 06.11.17.
 */
public class RestaurantTest {
  private Restaurant restaurant1;
  private Restaurant restaurant2;
  private Restaurant restaurant3;


  @Before
  @Test
  public void convertGson() throws Exception {
    Networking.GsonRestaurants gsonRestaurants = new Gson().fromJson
      ("{\"restaurants\":[{\"address\":\"Metelkova 8, " +
          "Ljubljana\",\"coordinates\":[\"46.05660309048518\",\"14.51653723679579\"],\"id\":10978," +
          "\"menu\":[],\"name\":\"Celica hostel\",\"opening\":{\"saturday\":[],\"sunday\":[]," +
          "\"week\":[\"11:00\",\"16:00\"]},\"price\":\"3,10 EUR\",\"telephone\":[]}," +
          "{\"address\":\"Leskoškova 9e, Ljubljana\",\"coordinates\":[\"46.06273574848173\"," +
          "\"14.56035268756902\"],\"id\":10986,\"menu\":[],\"name\":\"Antaro\"," +
          "\"opening\":{\"saturday\":[],\"sunday\":[],\"week\":[\"10:30\",\"14:30\"]}," +
          "\"price\":\"3,37 EUR\",\"telephone\":[]},{\"address\":\"Kongresni trg 3, Ljubljana\"," +
          "\"coordinates\":[\"46.05060464617008\",\"14.50458431341142\"],\"id\":10990,\"menu\":[]," +
          "\"name\":\"Azijska restavracija Han\",\"opening\":{\"saturday\":[\"11:00\",\"20:00\"]," +
          "\"sunday\":[],\"week\":[\"11:00\",\"20:00\"]},\"price\":\"3,67 EUR\"," +
          "\"telephone\":[]}]}", Networking.GsonRestaurants.class);
    assertEquals(3, gsonRestaurants.restaurants.length);
    restaurant1 = gsonRestaurants.restaurants[0];
    assertEquals("Metelkova 8, Ljubljana", restaurant1.address);
    assertArrayEquals(new String[] {"46.05660309048518","14.51653723679579"},
        restaurant1.coordinates);
    assertEquals(10978, restaurant1.id);
    assertArrayEquals(new String[0][0],restaurant1.menu);
    assertEquals("Celica hostel", restaurant1.name);
    assertEquals("3,10 EUR", restaurant1.price);
//    assertEquals(310, restaurant1.getPrice());
    assertArrayEquals(new String[0], restaurant1.telephone);
    restaurant2 = gsonRestaurants.restaurants[1];
    restaurant3 = gsonRestaurants.restaurants[2];
  }

  @Test
  public void getPrice() throws Exception {
    assertEquals(310, restaurant1.getPrice());
    assertEquals(337, restaurant2.getPrice());
    assertEquals(367, restaurant3.getPrice());

  }

  @Test
  public void equals() throws Exception {
    assertTrue(restaurant1.equals(restaurant1));
    assertFalse(restaurant1.equals(restaurant2));
    int id = restaurant2.id;
    restaurant2.id = restaurant1.id;
    assertFalse(restaurant1.equals(restaurant2));
    restaurant2.id = id;
  }


}