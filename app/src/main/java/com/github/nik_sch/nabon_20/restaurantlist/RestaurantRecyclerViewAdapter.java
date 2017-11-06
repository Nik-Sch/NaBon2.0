package com.github.nik_sch.nabon_20.restaurantlist;

import android.support.v7.util.SortedList;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.futuremind.recyclerviewfastscroll.SectionTitleProvider;
import com.github.nik_sch.nabon_20.R;
import com.github.nik_sch.nabon_20.restaurantlist.RestaurantListFragment.OnRestaurantListListener;
import com.google.gson.Gson;

import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link Restaurant} and makes a call to the
 * specified {@link OnRestaurantListListener}.
 */
public class RestaurantRecyclerViewAdapter extends RecyclerView
    .Adapter<RestaurantRecyclerViewAdapter.ViewHolder> implements SectionTitleProvider,
    Filterable {

  private final List<Restaurant> mAllValues;
  private final OnRestaurantListListener mListener;
  private final SortedList.Callback<Restaurant> sortedListCallback =
      new SortedList.Callback<Restaurant>() {
        @Override
        public int compare(Restaurant o1, Restaurant o2) {
          return o1.compareTo(o2);
        }

        @Override
        public void onChanged(int position, int count) {

        }

        @Override
        public boolean areContentsTheSame(Restaurant oldItem, Restaurant newItem) {
          return oldItem.equals(newItem);
        }

        @Override
        public boolean areItemsTheSame(Restaurant item1, Restaurant item2) {
          return item1.id == item2.id;
        }

        @Override
        public void onInserted(int position, int count) {
          notifyItemRangeInserted(position, count);
        }

        @Override
        public void onRemoved(int position, int count) {
          notifyItemRangeRemoved(position, count);
        }

        @Override
        public void onMoved(int fromPosition, int toPosition) {
          notifyItemMoved(fromPosition, toPosition);
        }
      };

  private SortedList<Restaurant> mValues;

  public RestaurantRecyclerViewAdapter(List<Restaurant> items, OnRestaurantListListener
      listener) {
    // save all items for the filter
    mAllValues = items;
    mValues = new SortedList<>(Restaurant.class, sortedListCallback);

    mValues.addAll(items);
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

  @Override
  public String getSectionTitle(int position) {
    return mValues.get(position).name.substring(0, 1);
  }

  @Override
  public Filter getFilter() {
    return new Filter() {
      @Override
      protected FilterResults performFiltering(CharSequence constraint) {
        FilterResults results = new FilterResults();
        SortedList<Restaurant> values = new SortedList<>(Restaurant.class, sortedListCallback);

        FilterObject filter = new Gson().fromJson(constraint.toString(), FilterObject.class);
        for (Restaurant restaurant : mAllValues)
          if (filter.applyFilterOn(restaurant))
            values.add(restaurant);
        results.values = values;
        return results;
      }

      @Override
      protected void publishResults(CharSequence constraint, FilterResults results) {
        try {
          mValues = (SortedList<Restaurant>) results.values;
        } catch (Exception e) {
          mValues = new SortedList<>(Restaurant.class, sortedListCallback);
          mValues.addAll(mAllValues);
        }
        notifyDataSetChanged();
      }
    };
  }

  public static class FilterObject {

    String name = "";
    String address = "";
    int min_price;
    int max_price;

    public FilterObject(String name, String address, int min_price, int max_price) {
      this.name = name;
      this.address = address;
      this.min_price = min_price;
      this.max_price = max_price;
    }

    public boolean applyFilterOn(Restaurant restaurant) {
      int price = restaurant.getPrice();
      return !(price < min_price || price > max_price)
          && restaurant.name.toLowerCase().contains(name.toLowerCase())
          && restaurant.address.toLowerCase().contains(address.toLowerCase());
    }
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
