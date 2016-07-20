package com.owentech.DevDrawer.model;

import android.util.SparseArray;

/**
 * Created by tonyowen on 20/07/2016.
 */
public class WidgetCard {

    private int widgetId;
    private String widgetName;
    private SparseArray<String> filterItems;

    public WidgetCard(int widgetId, String widgetName, SparseArray<String> filterItems) {
        this.widgetId = widgetId;
        this.widgetName = widgetName;
        this.filterItems = filterItems;
    }

    public WidgetCard (int widgetId, String widgetName){
        this(widgetId, widgetName, null);
    }

    public int getWidgetId() {
        return widgetId;
    }

    public void setWidgetId(int widgetId) {
        this.widgetId = widgetId;
    }

    public SparseArray<String> getFilterItems() {
        return filterItems;
    }

    public void setFilterItems(SparseArray<String> filterItems) {
        this.filterItems = filterItems;
    }

    public String getWidgetName() {
        return widgetName;
    }

    public void setWidgetName(String widgetName) {
        this.widgetName = widgetName;
    }
}
