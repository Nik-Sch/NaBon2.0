package com.github.nik_sch.nabon_20.restaurantlist;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.nik_sch.nabon_20.Networking;
import com.github.nik_sch.nabon_20.R;

public class RestaurantListFragment extends Fragment {
  private static final String TAG = "NB_RestaurantList";

  private OnRestaurantListListener mListener;


  public RestaurantListFragment() {
  }

  public static RestaurantListFragment newInstance() {
    RestaurantListFragment fragment = new RestaurantListFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  @Override
  public View onCreateView(LayoutInflater inflater, ViewGroup container,
                           Bundle savedInstanceState) {
    View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

    // Set the adapter
    if (view instanceof RecyclerView) {
      Context context = view.getContext();
      RecyclerView recyclerView = (RecyclerView) view;
      recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }


    // register the BroadcastManager
    LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        View view = getView();
        if (view instanceof RecyclerView)
          ((RecyclerView) view).setAdapter(new MyRestaurantRecyclerViewAdapter(RestaurantContent
              .ITEMS, mListener));
        Log.i(TAG, "received broadcast");
      }
    }, new IntentFilter(Networking.EVENT_BROADCAST_RESTAURANTS_AVAILABLE));
    Log.i(TAG, "broadcastReceiver registered");
    return view;
  }

  @Override
  public void onAttach(Context context) {
    super.onAttach(context);
    if (context instanceof OnRestaurantListListener) {
      mListener = (OnRestaurantListListener) context;
    } else {
      throw new RuntimeException(context.toString()
          + " must implement OnRestaurantListListener");
    }
  }

  @Override
  public void onDetach() {
    super.onDetach();
    mListener = null;
  }

  public interface OnRestaurantListListener {
    void onRestaurantClick(Restaurant restaurant);
  }
}
