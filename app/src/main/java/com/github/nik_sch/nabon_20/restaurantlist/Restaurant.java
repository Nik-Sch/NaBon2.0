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
  public Opening opening;
  public String[][] menu;
  public String[] coordinates;
//  public Collection<String>[] features;

  public int getPrice() {
    try {
      String s = price.replace(" EUR", "").replace(",", "");
      return Integer.valueOf(s);
    } catch (Exception e) {
      return -1;
    }
  }

  @Override
  public boolean equals(Object obj) {
    if (obj.getClass() != Restaurant.class)
      return false;
    Restaurant r = (Restaurant) obj;
    return !(name == null && r.name != null)
        && ((name == null || name.equals(r.name))
        && !(address == null && r.address != null)
        && (address == null || address.equals(r.address))
        && !(price == null && r.price != null)
        && (price == null || price.equals(r.price))
        && !(opening == null && r.opening != null)
        && (opening == null || opening.equals(r.opening))
        && Arrays.equals(telephone, r.telephone)
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

  public static class Opening {
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
          && !(notes == null && o.notes != null)
          && (notes == null || notes.equals(o.notes)));
    }
  }

}
