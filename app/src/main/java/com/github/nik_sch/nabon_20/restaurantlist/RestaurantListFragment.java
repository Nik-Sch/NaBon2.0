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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.futuremind.recyclerviewfastscroll.FastScroller;
import com.github.nik_sch.nabon_20.Networking;
import com.github.nik_sch.nabon_20.restaurantlist.RestaurantRecyclerViewAdapter.FilterObject;
import com.github.nik_sch.nabon_20.R;
import com.google.gson.Gson;

public class RestaurantListFragment extends Fragment {
  private static final String TAG = "NB_RestaurantList";

  private OnRestaurantListListener mListener;
  private RestaurantRecyclerViewAdapter adapter;


  public RestaurantListFragment() {
  }

  public static RestaurantListFragment newInstance() {
    RestaurantListFragment fragment = new RestaurantListFragment();
    Bundle args = new Bundle();
    fragment.setArguments(args);
    return fragment;
  }

  private static void expand(final View v) {
    v.measure(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    final int targetHeight = v.getMeasuredHeight();

    // Older versions of android (pre API 21) cancel animations for views with a height of 0.
    v.getLayoutParams().height = 1;
    v.setVisibility(View.VISIBLE);
    Animation a = new Animation() {
      @Override
      protected void applyTransformation(float interpolatedTime, Transformation t) {
        v.getLayoutParams().height = (interpolatedTime == 1)
            ? ViewGroup.LayoutParams.WRAP_CONTENT
            : (int) (targetHeight * interpolatedTime);
        v.requestLayout();
      }

      @Override
      public boolean willChangeBounds() {
        return true;
      }
    };

    // 1dp/ms
    a.setDuration((int) (targetHeight / v.getContext().getResources().getDisplayMetrics().density));
    v.startAnimation(a);
  }

  private static void collapse(final View v) {
    final int initialHeight = v.getMeasuredHeight();

    Animation a = new Animation() {
      @Override
      protected void applyTransformation(float interpolatedTime, Transformation t) {
        if (interpolatedTime == 1) {
          v.setVisibility(View.GONE);
        } else {
          v.getLayoutParams().height = initialHeight - (int) (initialHeight * interpolatedTime);
          v.requestLayout();
        }
      }

      @Override
      public boolean willChangeBounds() {
        return true;
      }
    };

    // 1dp/ms
    a.setDuration((int) (initialHeight / v.getContext().getResources().getDisplayMetrics().density));
    v.startAnimation(a);
  }

  @Override
  public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                           Bundle savedInstanceState) {
    final View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);

    // Set the adapter
    final RecyclerView recyclerView = view.findViewById(R.id.list);
    if (recyclerView != null) {
      Context context = recyclerView.getContext();
      recyclerView.setLayoutManager(new LinearLayoutManager(context));
      ((FastScroller) view.findViewById(R.id.fastscroller)).setRecyclerView(recyclerView);
    }

    // make the switch work
    ((Switch) view.findViewById(R.id.filter_switch))
        .setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      @Override
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
          expand(view.findViewById(R.id.filter));
        else
          collapse(view.findViewById(R.id.filter));
      }
    });

    // make the filter register
    ((EditText) view.findViewById(R.id.name)).addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        applyFilter();
      }
    });
    ((EditText) view.findViewById(R.id.address)).addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        applyFilter();
      }
    });
    ((EditText) view.findViewById(R.id.price_minimum)).addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() > 0)
          applyFilter();
      }
    });
    ((EditText) view.findViewById(R.id.price_maximum)).addTextChangedListener(new TextWatcher() {
      @Override
      public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override
      public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override
      public void afterTextChanged(Editable s) {
        if (s.length() > 0)
          applyFilter();
      }
    });


    // register the BroadcastManager
    LocalBroadcastManager.getInstance(getContext()).registerReceiver(new BroadcastReceiver() {
      @Override
      public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "received broadcast");
        RecyclerView recycleView = view.findViewById(R.id.list);
        adapter = new RestaurantRecyclerViewAdapter(RestaurantContent.ITEMS, mListener, recyclerView);
        recycleView.setAdapter(adapter);
        ((FastScroller) view.findViewById(R.id.fastscroller)).setRecyclerView(recyclerView);
      }
    }, new IntentFilter(Networking.EVENT_BROADCAST_RESTAURANTS_AVAILABLE));
    Log.i(TAG, "broadcastReceiver registered");
    return view;
  }

  private void applyFilter() {
    EditText min = getView().findViewById(R.id.price_minimum);
    EditText max = getView().findViewById(R.id.price_maximum);
    if ((adapter == null)
        || (min.getText().toString().length() == 0)
        || (max.getText().toString().length() == 0))
      return;
    FilterObject filterObject = new FilterObject(
        ((EditText) getView().findViewById(R.id.name)).getText().toString(),
        ((EditText) getView().findViewById(R.id.address)).getText().toString(),
        (int) (Float.valueOf(min.getText().toString()) * 100),
        (int) (Float.valueOf(max.getText().toString()) * 100)
    );
    String filterString = new Gson().toJson(filterObject, FilterObject.class);
    adapter.getFilter().filter(filterString);
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
