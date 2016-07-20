package com.owentech.DevDrawer.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.owentech.DevDrawer.R;
import com.owentech.DevDrawer.listeners.CardItemClickListener;
import com.owentech.DevDrawer.model.WidgetCard;

import java.util.List;

public class WidgetCardAdapter extends RecyclerView.Adapter<WidgetCardAdapter.ViewHolder> {

    private List<WidgetCard> items;
    // TODO: Dont hold the activity
    private Activity activity;
    private CardItemClickListener cardItemClickListener;

    public WidgetCardAdapter(Activity activity, List<WidgetCard> items, CardItemClickListener cardItemClickListener){
        this.items = items;
        this.activity = activity;
        this.cardItemClickListener = cardItemClickListener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.recycler_widget_card, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        String widgetName = items.get(position).getWidgetName();
        if (widgetName == null || widgetName.length() == 0){
            widgetName = "WidgetId: " + items.get(position).getWidgetId();
        }
        holder.cardTitle.setText(widgetName);

        for (int i = 0; i < items.get(position).getFilterItems().size(); i++) {
            TextView cardItem = (TextView)activity.getLayoutInflater().inflate(R.layout.widget_card_item, null);
            cardItem.setText(items.get(position).getFilterItems().valueAt(i));
            final int filterId = items.get(position).getFilterItems().keyAt(i);
            cardItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (cardItemClickListener != null){
                        cardItemClickListener.itemClicked(filterId);
                    }
                }
            });
            holder.itemHolder.addView(cardItem);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public final static class ViewHolder extends RecyclerView.ViewHolder {
        TextView cardTitle;
        LinearLayout itemHolder;

        public ViewHolder(View itemView) {
            super(itemView);
            cardTitle = (TextView) itemView.findViewById(R.id.cardTitle);
            itemHolder = (LinearLayout) itemView.findViewById(R.id.itemHolder);
        }
    }
}
