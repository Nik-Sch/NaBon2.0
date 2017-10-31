package net.ddns.raspi_server.na_bon20;

import android.content.Context;
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

import java.util.HashMap;
import java.util.Map;

public class Networking {
  private static final String TAG = "Networking";
  private Context context;

  public Networking(Context context) {
    this.context = context;
  }


  public void getRestaurants(final resultListener listener) {
    RequestQueue queue = Volley.newRequestQueue(context);
    String url = "http://na-bon.herokuapp.com/api/Restaurants";
    final long millis1 = System.currentTimeMillis();
    StringRequest request = new StringRequest(Request.Method.GET, url, new Response
        .Listener<String>() {
      @Override
      public void onResponse(String response) {
        long millis2 = System.currentTimeMillis();
        Log.i(TAG, "received response after " + (millis2 - millis1) + "ms.");
        Gson gson = new Gson();
        response = "{restaurants: " + response.replace("false", "[]") + "}";
        Restaurants restaurants = gson.fromJson(response, Restaurants.class);
        listener.restaurantsReceived(restaurants);
        Log.i(TAG, "json conversion done in " + (System.currentTimeMillis() - millis2) + "ms.");
      }
    }, new Response.ErrorListener() {
      @Override
      public void onErrorResponse(VolleyError error) {
        Log.e(TAG, error.toString());
      }
    }) {
      @Override
      public Map<String, String> getHeaders() throws AuthFailureError {
        Map<String, String> headers = new HashMap<>();
        headers.put("If-None-Match", "W/\"bd451-wTwUh/+70K7mC7m6iWYMzg\"");
        return headers;
      }

      @Override
      protected Response<String> parseNetworkResponse(NetworkResponse response) {
        Log.i(TAG, response.toString());
        return super.parseNetworkResponse(response);
      }
    };
    queue.add(request);
  }

  class Restaurants {
    Restaurant[] restaurants;

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

    @Override
    public String toString() {
      return "total: " + restaurants.length + "\nfirst: " + (restaurants.length > 0 ?
          restaurants[0].toString() : "None");
    }
  }

  public interface resultListener {
    void restaurantsReceived(Restaurants restaurants);
  }

}
