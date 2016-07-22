package com.owentech.DevDrawer.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.owentech.DevDrawer.interfaces.ContainerViewChild;
import com.owentech.DevDrawer.interfaces.ContainerViewChildListener;

public class ContainerView extends FrameLayout implements ContainerViewChildListener {

    public static final int ADD_FILTER = 0;
    public static final int ADD_WIDGET = 1;

    public ContainerView(Context context) {
        super(context);
    }

    public ContainerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ContainerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean hasChildren(){
        return (this.getChildCount() > 0);
    }

    public void removeChild(){
        if (this.hasChildren()){
           getContainerChild().animateAway();
        }
        this.removeAllViews();
    }

    @Override
    public void addView(View child) {
        super.addView(child);
        ((ContainerViewChild)child)
    }

    public int getChildId(){
        return ((ContainerViewChild)getChildAt(0)).childId();
    }

    private ContainerViewChild getContainerChild(){
        return (ContainerViewChild)getChildAt(0);
    }

    @Override
    public void endAnimationComplete() {
        this.removeAllViews();
    }
}
