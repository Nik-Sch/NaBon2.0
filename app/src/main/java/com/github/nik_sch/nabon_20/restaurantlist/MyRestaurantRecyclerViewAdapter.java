package com.github.nik_sch.nabon_20.restaurantlist;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.nik_sch.nabon_20.R;
import com.github.nik_sch.nabon_20.restaurantlist.RestaurantListFragment.OnRestaurantListListener;
import com.simplecityapps.recyclerview_fastscroll.views.FastScrollRecyclerView;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Restaurant} and makes a call to the
 * specified {@link OnRestaurantListListener}.
 */
public class MyRestaurantRecyclerViewAdapter extends RecyclerView
    .Adapter<MyRestaurantRecyclerViewAdapter.ViewHolder> implements FastScrollRecyclerView.SectionedAdapter {

  private final List<Restaurant> mValues;
  private final OnRestaurantListListener mListener;

  public MyRestaurantRecyclerViewAdapter(List<Restaurant> items, OnRestaurantListListener
      listener) {
    mValues = items;
    mListener = listener;
  }

  @Override
  public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
        .inflate(R.layout.content_restaurant, parent, false);
    return new ViewHolder(view);
  }

  @Override
  public void onBindViewHolder(final ViewHolder holder, int position) {
    holder.mItem = mValues.get(position);
    holder.mNameView.setText(mValues.get(position).name);
    holder.mAddressView.setText(mValues.get(position).address);
    holder.mPriceView.setText(mValues.get(position).price);

    holder.mView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        if (null != mListener) {
          mListener.onRestaurantClick(holder.mItem);
        }
      }
    });
  }

  @Override
  public int getItemCount() {
    return mValues.size();
  }

  @NonNull
  @Override
  public String getSectionName(int position) {
    // TODO: reimplement fastscroll?!
    position = (int) (((float) position / (float) getItemCount()) * 100);
    return String.valueOf(position) + "%";
  }

  public class ViewHolder extends RecyclerView.ViewHolder {
    public final View mView;
    public final TextView mNameView;
    public final TextView mAddressView;
    public final TextView mPriceView;
    public Restaurant mItem;

    public ViewHolder(View view) {
      super(view);
      mView = view;
      mNameView = view.findViewById(R.id.name);
      mAddressView = view.findViewById(R.id.address);
      mPriceView = view.findViewById(R.id.price);
    }

    @Override
    public String toString() {
      return mItem.toString();
    }
  }
}
