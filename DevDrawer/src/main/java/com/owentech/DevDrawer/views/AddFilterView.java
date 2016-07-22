package com.owentech.DevDrawer.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;

import com.owentech.DevDrawer.R;
import com.owentech.DevDrawer.interfaces.ContainerViewChild;
import com.owentech.DevDrawer.interfaces.ContainerViewChildListener;

public class AddFilterView extends LinearLayout implements ContainerViewChild {

    private CardView cardView;
    private AutoCompleteTextView packageFilter;
    private Button save;
    private ContainerViewChildListener listener;

    public AddFilterView(Context context) {
        super(context);
        init();
    }

    public AddFilterView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public AddFilterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        inflate(getContext(), R.layout.overlay_add_filter, this);
        cardView = (CardView) findViewById(R.id.addFilterCard);
        packageFilter = (AutoCompleteTextView) findViewById(R.id.packageFilter);
        save = (Button) findViewById(R.id.saveFilter);

    }

    @Override
    public int childId() {
        return ContainerView.ADD_FILTER;
    }

    @Override
    public void animateAway() {
        cardView.animate().translationY(-cardView.getMeasuredHeight()).withEndAction(new Runnable() {
            @Override
            public void run() {
                getListener().endAnimationComplete();
            }
        });
    }

    public ContainerViewChildListener getListener() {
        return listener;
    }

    public void setListener(ContainerViewChildListener listener) {
        this.listener = listener;
    }
}
