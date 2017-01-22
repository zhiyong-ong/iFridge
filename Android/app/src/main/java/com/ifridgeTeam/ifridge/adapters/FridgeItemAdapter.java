package com.ifridgeTeam.ifridge.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ifridgeTeam.ifridge.R;
import com.ifridgeTeam.ifridge.models.FridgeItem;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Abel on 1/21/2017.
 */

public class FridgeItemAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<FridgeItem> mFridgeItems;

    public FridgeItemAdapter(Context context, List<FridgeItem> items) {
        mContext = context;
        mFridgeItems = items;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View itemView = null;
        switch (viewType) {
            case 0:
                itemView = inflater.inflate(R.layout.item_fridge, parent, false);
                return new ItemViewHolder(itemView);
            case 1:
                itemView = inflater.inflate(R.layout.item_last_updated, parent, false);
                return new LastItemViewHolder(itemView);
            case 2:
                itemView = inflater.inflate(R.layout.header_item_fridge, parent, false);
                return new HeaderViewHolder(itemView);
        }

        // Return a new holder instance
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (position == getItemCount() - 1) {
            onBindLastViewHolder((LastItemViewHolder) holder, position);
            return;
        } else if (position == 0) {
            return;
        }

        ItemViewHolder itemViewHolder = (ItemViewHolder) holder;

        // Get corresponding data
        FridgeItem item = mFridgeItems.get(position - 1);
        String itemName = item.getItemName();
        int itemCount = item.getItemCount();

        // Bind data to fields
        if (itemCount < 2) {
            itemViewHolder.itemCountText.setTextColor(itemViewHolder.lowCountColor);
        } else {
            itemViewHolder.itemCountText.setTextColor(itemViewHolder.itemColor);
        }

        itemViewHolder.itemNameText.setText(itemName);
        itemViewHolder.itemCountText.setText(String.valueOf(itemCount));
    }

    private void onBindLastViewHolder(LastItemViewHolder holder, int position) {
        Date time = Calendar.getInstance().getTime();
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        String formattedTime = formatter.format(time);
        formattedTime = String.format("Last updated at %1$s", formattedTime);
        holder.updatedTimeText.setText(formattedTime);
    }


    @Override
    public int getItemViewType(int position) {
        if (position == mFridgeItems.size() + 1) {
            return 1;
        } else if (position == 0) {
            return 2;
        }

        return 0;
    }

    @Override
    public int getItemCount() {
        return mFridgeItems.size() + 2;
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        @BindColor(R.color.colorLowCount)
        int lowCountColor;

        @BindColor(R.color.colorItem)
        int itemColor;

        @BindView(R.id.text_item_count)
        TextView itemCountText;

        @BindView(R.id.text_item_name)
        TextView itemNameText;

        ItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    class LastItemViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.text_updatedTime)
        TextView updatedTimeText;

        LastItemViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }


    class HeaderViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.header_name)
        TextView headerNameText;

        @BindView(R.id.header_count)
        TextView headerCountText;

        HeaderViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

}
