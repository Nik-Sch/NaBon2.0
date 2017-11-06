package com.github.nik_sch.nabon_20.restaurantlist;

import android.support.annotation.NonNull;

import java.util.Arrays;

// the data structure for the restaurants

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 */
public class Restaurant implements Comparable<Restaurant> {

  public String name;
  public int id;
  public String address;
  public String[] telephone;
  public String price;
  private int price_int = -2;
  public Opening opening;
  public String[][] menu;
  public String[] coordinates;
//  public Collection<String>[] features;

  public int getPrice() {
    if (price_int > -2)
      return price_int;
    try {
      String s = price.replace(" EUR", "").replace(",","");
      price_int = Integer.valueOf(s);
    } catch (NumberFormatException e) {
      price_int = -1;
    }
    return price_int;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj.getClass() != Restaurant.class)
      return false;
    Restaurant r = (Restaurant) obj;
    return (name.equals(r.name)
        && address.equals(r.address)
        && Arrays.equals(telephone, r.telephone)
        && price.equals(r.price)
        && opening.equals(r.opening)
        && Arrays.deepEquals(menu, r.menu)
        && Arrays.equals(coordinates, r.coordinates));
  }

  @Override
  public String toString() {
    return "name: " + name + " (id: " + id + ")";
  }

  @Override
  public int compareTo(@NonNull Restaurant o) {
    return this.name.compareTo(o.name);
  }

  public class Opening {
    public String[] week;
    public String[] saturday;
    public String[] sunday;
    public String notes;

    @Override
    public boolean equals(Object obj) {
      if (obj.getClass() != Opening.class)
        return false;
      Opening o = (Opening) obj;
      return (Arrays.equals(week, o.week)
          && Arrays.equals(saturday, o.saturday)
          && Arrays.equals(sunday, o.sunday)
          && notes.equals(o.notes));
    }
  }

}
