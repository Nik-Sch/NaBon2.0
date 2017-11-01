package com.github.nik_sch.nabon_20;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

public class Networking {
  private static final String TAG = "Networking";
  private static final String SHARED_PREF = "SHARED_PREF_NETWORKING";
  // String constant for the ETag. The ETag is for the "If-None-Match" header of the http request.
  private static final String PREF_ETAG = "PREF_ETAG";
  private static final String RESTAURANTS_FILE = "restaurants.json";
  private Context context;

  public Networking(Context context) {
    this.context = context;
  }


  public void getRestaurants(final resultListener listener) {
    // the preferences storing the ETag and the json string of the restaurants
    final SharedPreferences preferences = context.getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
    final String etag = preferences.getString(PREF_ETAG, null);
    Log.d(TAG, "stored etag is: " + etag);

    // creating the volley request
    RequestQueue queue = Volley.newRequestQueue(context);
    String url = "http://na-bon.herokuapp.com/api/Restaurants";

    // timing for debug
    final long millis1 = System.currentTimeMillis();

    StringRequest request = new StringRequest(Request.Method.GET, url, new Response
        .Listener<String>() {

      @Override
      public void onResponse(String response) {
        long millis2 = System.currentTimeMillis();
        Log.i(TAG, "received response after " + (millis2 - millis1) + "ms.");

        // converting the received json into the data structure
        Gson gson = new Gson();
        // the json is of wrong syntax/cannot be interpreted by gson
        response = "{restaurants: " + response.replace("false", "[]") + "}";
        Restaurants restaurants = gson.fromJson(response, Restaurants.class);
        //notify the listener of received response
        listener.restaurantsReceived(restaurants);
        Log.i(TAG, "json conversion done in " + (System.currentTimeMillis() - millis2) + "ms. " +
            "String was " + response.length() + " chars long.");
        // save the restaurant json (reconvert it, to make sure the same data is stored as it is
        // displayed.
        try {
          FileOutputStream fos = context.openFileOutput(RESTAURANTS_FILE, Context.MODE_PRIVATE);
          fos.write(gson.toJson(restaurants, Restaurants.class).getBytes());
          fos.close();
        } catch (java.io.IOException e) {
          e.printStackTrace();
        }
        Log.i(TAG, "saved restaurants json.");
      }

    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        // if the error is a Http 304 Not Modified return the saved restaurants
        if (error.networkResponse.statusCode == 304) {
          long millis2 = System.currentTimeMillis();
          Log.i(TAG, "304 Not Modified response in " + (millis2 - millis1) + "ms.");
          // defValue is just an empty array of restaurants
          String restString = "{restaurants: []}";
          try {
            FileInputStream fis = context.openFileInput(RESTAURANTS_FILE);
            StringBuilder builder = new StringBuilder();
            int c;
            while ((c = fis.read()) != -1) {
              builder.append((char) c);
            }
            restString = builder.toString();
          } catch (java.io.IOException e) {
            e.printStackTrace();
          }
          Log.i(TAG, "read restaurants from file. Size: " + restString.length());
          listener.restaurantsReceived(new Gson().fromJson(restString, Restaurants.class));
          return;
        }
        Log.e(TAG, error.toString());
      }
    }) {

      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        // if there is an ETag send it as "If-None-Match" header
        Map<String, String> headers = new HashMap<>();
        if (etag != null) {
//          headers.put("If-None-Match", "W/\"bd451-wTwUh/+70K7mC7m6iWYMzg\"");
          headers.put("If-None-Match", etag);
          Log.d(TAG, "added header If-None-Match: " + etag);
        } else {
          Log.d(TAG, "no header to add");
        }
        return headers;
      }

      @Override
      protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Log.i(TAG, response.statusCode + " response received.");
        // save the ETag to be able to send it with the next request
        if (response.headers.containsKey("ETag")) {
          preferences.edit().putString(PREF_ETAG, response.headers.get("ETag")).apply();
          Log.i(TAG, "saved ETag to preferences: " + etag);
        }
        if (response.statusCode == 304)
          return Response.error(new VolleyError(response));
        return super.parseNetworkResponse(response);
      }
    };

    // finally send the request
    queue.add(request);
    Log.i(TAG, "request added to queue.");
  }

  public interface resultListener {
    void restaurantsReceived(Restaurants restaurants);
  }

  // the data structure for the restaurants
  class Restaurants {
    Restaurant[] restaurants;

    @Override
    public String toString() {
      return "total: " + restaurants.length + "\nfirst: " + (restaurants.length > 0 ?
          restaurants[0].toString() : "None");
    }

    public class Restaurant {
      String name;
      int id;
      String address;
      String[] telephone;
      String price;
      Opening opening;
      String[][] menu;
      String[] coordinates;
//    Collection<String>[] features;

      @Override
      public String toString() {
        return "name: " + name + " (id: " + id + ")";
      }
    }

    public class Opening {
      String[] week;
      String[] saturday;
      String[] sunday;
      String notes;
    }
  }

}
