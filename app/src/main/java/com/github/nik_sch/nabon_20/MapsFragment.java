package com.github.nik_sch.nabon_20;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.github.nik_sch.nabon_20.restaurantlist.Restaurant;
import com.github.nik_sch.nabon_20.restaurantlist.RestaurantContent;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

  private static final int FINE_LOCATION_REQUEST = 42;
  private static final String TAG = "NB_MapsFragment";
  MapView mMapView;
  private GoogleMap googleMap;

  private boolean initializedMap = false;
  private boolean broadcastReceived = false;

  public static MapsFragment newInstance() {
    return new MapsFragment();
  }

  @Nullable
  @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable
      Bundle savedInstanceState) {
    View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
    mMapView = (MapView) rootView.findViewById(R.id.mapView);
    mMapView.onCreate(savedInstanceState);
    mMapView.onResume();
    MapsInitializer.initialize(getActivity().getApplicationContext());
    mMapView.getMapAsync(this);
    // TODO: this should be somewhere in activity after fragments had the possibilty to register
    // broadcastReceiver
    new Networking(getContext()).downloadRestaurants();

    return rootView;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received broadcast");
        showRestaurants();
      }
    }, new IntentFilter(Networking.EVENT_BROADCAST_RESTAURANTS_AVAILABLE));
    Log.i(TAG, "broadcastReceiver registered");
  }

//  class GetRestaurantsTask extends AsyncTask<Integer, Integer, Restaurant> {
//
//    @Override
//    protected Restaurant doInBackground(Integer... integers) {
//      return new Networking(getContext()).getRestaurantsFromFile();
//      Log.i(TAG, "loaded restaurants from file");
//    }
//
//    @Override
//    protected void onPostExecute(Restaurant restaurants) {
//      Log.i(TAG, "placing restaurant markers on map");
//      int c = 0;
//      long time = System.currentTimeMillis();
//      for (Restaurant.Restaurant restaurant : restaurants.restaurants) {
//        try {
//          LatLng pos = new LatLng(Double.valueOf(restaurant.coordinates[0]), Double.valueOf(restaurant
//              .coordinates[1]));
//          googleMap.addMarker(new MarkerOptions()
//              .position(pos)
//              .title(restaurant.name)
//              .snippet(restaurant.address)).setTag(restaurant);
//          c++;
//        } catch (NumberFormatException | NullPointerException e) {
//          e.printStackTrace();
//        }
//      }
//      Log.i(TAG, "placed " + c + " markers on map in " + (System.currentTimeMillis() - time) +
//          "ms.");
//    }
//  }

  private void showRestaurants() {
    if (initializedMap) {
      Log.i(TAG, "placing restaurant markers on map");
      int c = 0;
      long time = System.currentTimeMillis();
      for (Restaurant restaurant : RestaurantContent.ITEMS) {
        try {
          LatLng pos = new LatLng(Double.valueOf(restaurant.coordinates[0]), Double.valueOf(restaurant
              .coordinates[1]));
          googleMap.addMarker(new MarkerOptions()
              .position(pos)
              .title(restaurant.name)
              .snippet(restaurant.address)).setTag(restaurant);
          c++;
        } catch (NumberFormatException | NullPointerException e) {
          e.printStackTrace();
        }
      }
      Log.i(TAG, "placed " + c + " markers on map in " + (System.currentTimeMillis() - time) +
          "ms.");
    }
    broadcastReceived = true;
  }

  @SuppressLint("MissingPermission")
  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    Log.i(TAG, "onPermissionResult");
    switch (requestCode) {
      case FINE_LOCATION_REQUEST:
        init();
    }
  }

  @Override
  public void onMapReady(GoogleMap googleMap) {
    Log.i(TAG, "map ready.");
    this.googleMap = googleMap;
    initializedMap = false;
    if (ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android
        .Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(getActivity().getApplicationContext(), android
            .Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

      Log.i(TAG, "requesting location permission");
      ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission
          .ACCESS_FINE_LOCATION}, FINE_LOCATION_REQUEST);
      return;
    }
    init();
  }


  private void init() {
    if (initializedMap) {
      Log.i(TAG, "map already initialized");
      return;
    }
    Log.i(TAG, "initializing map");
    boolean locationPermission = ActivityCompat.checkSelfPermission(getActivity()
        .getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(getActivity()
        .getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED;
    LatLng zoomLocation;
    if (locationPermission) {
      Log.i(TAG, "location permission granted, enabling location");
      googleMap.setMyLocationEnabled(true);

      LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context
          .LOCATION_SERVICE);
      if (locationManager == null)
        return;
      Location currentLocation = locationManager.getLastKnownLocation(locationManager
          .getBestProvider(new Criteria(), false));
      if (currentLocation == null)
        return;
      zoomLocation = new LatLng(currentLocation.getLatitude(), currentLocation
          .getLongitude());
    } else {
      Log.i(TAG, "location permission not granted, zooming on Ljubljana");
      zoomLocation = new LatLng(46.051394, 14.506169);
    }

    googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(zoomLocation, 14f));
    googleMap.setOnInfoWindowClickListener(this);
    initializedMap = true;
    if (broadcastReceived)
      showRestaurants();
  }

  @Override
  public void onInfoWindowClick(Marker marker) {
    Toast.makeText(getContext(), marker.getTag().toString(),
        Toast.LENGTH_LONG).show();
  }


  @Override
  public void onResume() {
    super.onResume();
    mMapView.onResume();
  }

  @Override
  public void onLowMemory() {
    super.onLowMemory();
    mMapView.onLowMemory();
  }

  @Override
  public void onPause() {
    super.onPause();
    mMapView.onPause();
  }

  @Override
  public void onDestroy() {
    super.onDestroy();
    mMapView.onDestroy();
  }
}
