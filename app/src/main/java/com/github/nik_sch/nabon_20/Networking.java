package com.github.nik_sch.nabon_20;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

public class Networking {
  public static final String EVENT_BROADCAST_RESTAURANTS_AVAILABLE = "com.github.nik_sch" +
      ".nabon_20.EVENT_BROADCAST_RESTAURANTS_AVAILABLE";

  private static final String TAG = "NB_Networking";
  private static final String SHARED_PREF = "SHARED_PREF_NETWORKING";
  // String constant for the ETag. The ETag is for the "If-None-Match" header of the http request.
  private static final String PREF_ETAG = "PREF_ETAG";
  private static final String RESTAURANTS_FILE = "restaurants.json";
  private Context context;

  public Networking(Context context) {
    this.context = context;
  }


  public void downloadRestaurants() {
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

        // the json is of wrong syntax/cannot be interpreted by gson
        response = "{restaurants: " + response.replace("false", "[]") + "}";
        // save the restaurant json
        try {
          FileOutputStream fos = context.openFileOutput(RESTAURANTS_FILE, Context.MODE_PRIVATE);
          fos.write(response.getBytes());
          fos.close();
        } catch (java.io.IOException e) {
          e.printStackTrace();
        }
        Log.i(TAG, "saved restaurants json.");
        // notify that the restaurants are finished
        broadcastFinished();
      }

    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        // if the error is a Http 304 Not Modified return the saved restaurants
        if (error.networkResponse.statusCode == 304) {
          long millis2 = System.currentTimeMillis();
          Log.i(TAG, "304 Not Modified response in " + (millis2 - millis1) + "ms.");
          // defValue is just an empty array of restaurants
          broadcastFinished();
          return;
        } else if (context.getFileStreamPath(RESTAURANTS_FILE).exists()) {
          // TODO: do something (not downloaded)
          Toast.makeText(context, "The server couldn't be reached. Old data will be shown.",
              Toast.LENGTH_LONG);
          broadcastFinished();
        } else
          Toast.makeText(context, "The server couldn't be reached. Try again.",
              Toast.LENGTH_LONG);
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

  private void broadcastFinished() {
    Log.i(TAG, "sending finished broadcast");
    Intent intent = new Intent(EVENT_BROADCAST_RESTAURANTS_AVAILABLE);
    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
  }

  public Restaurants getRestaurantsFromFile() {
    String restString = "{restaurants: []}";
//    try {
//      FileInputStream fis = context.openFileInput(RESTAURANTS_FILE);
//      StringBuilder builder = new StringBuilder();
//      int c;
//      while ((c = fis.read()) != -1) {
//        builder.append((char) c);
//      }
//      restString = builder.toString();
//    } catch (java.io.IOException e) {
//      e.printStackTrace();
//    }
    try {
      BufferedReader br = new BufferedReader(new InputStreamReader(context.openFileInput
          (RESTAURANTS_FILE), "utf8"), 8192);
      StringBuilder builder = new StringBuilder();
      String str;
      while ((str = br.readLine()) != null)
        builder.append(str);
      restString = builder.toString();
      br.close();
    } catch (java.io.IOException e) {
      e.printStackTrace();
    }
    return new Gson().fromJson(restString, Restaurants.class);
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
