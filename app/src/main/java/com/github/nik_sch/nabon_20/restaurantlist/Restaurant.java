package com.github.nik_sch.nabon_20.restaurantlist;

// the data structure for the restaurants
public class Restaurant {

//  @Override
//  public String toString() {
//    return "total: " + restaurants.length + "\nfirst: " + (restaurants.length > 0 ?
//        restaurants[0].toString() : "None");
//  }

  public String name;
  public int id;
  public String address;
  public String[] telephone;
  public String price;
  public Opening opening;
  public String[][] menu;
  public String[] coordinates;
//  public Collection<String>[] features;

  @Override
  public String toString() {
    return "name: " + name + " (id: " + id + ")";
  }

  public class Opening {
    public String[] week;
    public String[] saturday;
    public String[] sunday;
    public String notes;
  }

}
