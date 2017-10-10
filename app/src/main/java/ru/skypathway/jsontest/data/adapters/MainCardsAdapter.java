package ru.skypathway.jsontest.data.adapters;

import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import ru.skypathway.jsontest.R;

/**
 * Created by samsmariya on 10.10.17.
 */

public class MainCardsAdapter extends RecyclerView.Adapter<MainCardsAdapter.ViewHolder> {
    protected List<CardItem> mItems;

    public MainCardsAdapter(List<CardItem> items) {
        mItems = items;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_main_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        CardItem cardItem = getItem(position);
        holder.textTitle.setText(cardItem.titleId);
    }

    @Override
    public int getItemCount() {
        return (mItems == null) ? 0 : mItems.size();
    }

    public CardItem getItem(int position) {
        return (mItems == null) ? null : mItems.get(position);
    }

    public List<CardItem> getItems() {
        return mItems;
    }

    public void setItems(List<CardItem> items) {
        this.mItems = items;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView textTitle;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card_view);
            textTitle = (TextView) itemView.findViewById(R.id.text_title);
        }
    }

    public static class CardItem {
        public final String value;
        public final int titleId;

        public CardItem(@NonNull String value, @StringRes int titleId) {
            this.titleId = titleId;
            this.value = value;
        }
    }
}
