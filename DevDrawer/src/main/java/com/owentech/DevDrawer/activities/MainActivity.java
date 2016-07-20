package com.owentech.DevDrawer.activities;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.RemoteViews;

import com.owentech.DevDrawer.R;
import com.owentech.DevDrawer.adapters.WidgetCardAdapter;
import com.owentech.DevDrawer.appwidget.DDWidgetProvider;
import com.owentech.DevDrawer.fragments.ShortcutFragment;
import com.owentech.DevDrawer.listeners.CardItemClickListener;
import com.owentech.DevDrawer.model.WidgetCard;
import com.owentech.DevDrawer.utils.OttoManager;
import com.owentech.DevDrawer.fragments.NotificationsFragment;
import com.owentech.DevDrawer.fragments.WidgetsFragment;
import com.owentech.DevDrawer.utils.AppConstants;
import com.owentech.DevDrawer.utils.AppWidgetUtil;
import com.owentech.DevDrawer.utils.Database;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends AppCompatActivity implements TextWatcher, CardItemClickListener {

    private static final int VIEW_LIST = 0;
    private static final int VIEW_ADD_WIDGET = 1;
    private static final int VIEW_ADD_FILTER = 2;

    @InjectView(R.id.recyclerView) RecyclerView recyclerView;
    @InjectView(R.id.addWidgetOverlay) FrameLayout addWidgetOverlay;
    @InjectView(R.id.widgetName) EditText widgetName;
    @InjectView(R.id.save) Button save;
    @InjectView(R.id.addFilterOverlay) FrameLayout addFilterOverlay;
    @InjectView(R.id.addFilterCard) CardView addFilterCard;
    @InjectView(R.id.packageFilter) AutoCompleteTextView packageFilter;
    @InjectView(R.id.saveFilter) Button saveFilter;
    @InjectView(R.id.addButton) FloatingActionButton addButton;
    private WidgetCardAdapter widgetCardAdapter;

    WidgetsFragment widgetsFragment;
    NotificationsFragment notificationsFragment;
    ShortcutFragment shortcutFragment;
    private int[] mAppWidgetIds;
    private int currentView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        currentView = VIEW_LIST;

        Database.getInstance(this).createTables();
        mAppWidgetIds = AppWidgetUtil.findAppWidgetIds(this);

        List<WidgetCard> recyclerItems = Database.getInstance(this).getAllWidgets();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        widgetCardAdapter = new WidgetCardAdapter(this, recyclerItems, this);
        recyclerView.setAdapter(widgetCardAdapter);

        checkForNewWidget();

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentView = VIEW_ADD_FILTER;
                showAddFilter();
            }
        });
    }

    private void checkForNewWidget(){
        if (getIntent() != null && getIntent().hasExtra(AppWidgetManager.EXTRA_APPWIDGET_ID)) {
            final int appWidgetId = getIntent().getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            Snackbar.make(recyclerView, "New Widget : " + appWidgetId, Snackbar.LENGTH_LONG).show();
            currentView = VIEW_ADD_WIDGET;
            addWidgetOverlay.setAlpha(0f);
            addWidgetOverlay.setVisibility(View.VISIBLE);
            addWidgetOverlay.animate().alpha(1f).setDuration(500);
            widgetName.requestFocus();
            save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (widgetName.getText().length() != 0){
                        Database.getInstance(MainActivity.this).addWidgetToDatabase(appWidgetId,
                                widgetName.getText().toString());
                        onBackPressed();
                    }
                    else{
                        Snackbar.make(addWidgetOverlay, "Please enter a widget name", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void showAddFilter(){
        addFilterOverlay.setAlpha(0f);
        addFilterCard.setTranslationY(-addFilterCard.getMeasuredHeight());
        addFilterOverlay.setVisibility(View.VISIBLE);
        addFilterOverlay.animate().alpha(1f).setDuration(300);
        addFilterCard.animate().setStartDelay(300).translationY(1f).setDuration(500);
        packageFilter.requestFocus();
    }

    private void hideAddFilter(){
        addFilterCard.animate().translationY(-addFilterCard.getMeasuredHeight()).setDuration(500);
        addFilterOverlay.animate().setStartDelay(500).alpha(0f).setDuration(300).withEndAction(new Runnable() {
            @Override
            public void run() {
                addFilterOverlay.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onBackPressed() {

        switch (currentView){
            case VIEW_ADD_FILTER:{
                hideAddFilter();
                currentView = VIEW_LIST;
                break;
            }
            case VIEW_LIST:{
                super.onBackPressed();
                break;
            }
            case VIEW_ADD_WIDGET:{
                Intent intent = getIntent();
                Bundle extras = intent.getExtras();
                int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
                if (extras != null) {
                    appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
                }

                if (appWidgetId != AppWidgetManager.INVALID_APPWIDGET_ID) {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
                    RemoteViews widget = DDWidgetProvider.getRemoteViews(this, appWidgetId);
                    appWidgetManager.updateAppWidget(appWidgetId, widget);
                    Intent resultValue = new Intent();
                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
                    setResult(RESULT_OK, resultValue);
                    finish();
                }

                super.onBackPressed();
            }
        }


    }

    @Override
    protected void onPause() {
        OttoManager.getInstance().unregister(this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        OttoManager.getInstance().register(this);
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Catch the return from the EditDialog
        if (resultCode == AppConstants.EDIT_DIALOG_CHANGE) {
            Bundle bundle = data.getExtras();
            Database.getInstance(this).amendFilterEntryTo(bundle.getString("id"), bundle.getString("newText"));
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menu_shortcut: {
                addShortcut(this);
                return true;
            }
            case R.id.menu_settings: {
                startActivity(new Intent(MainActivity.this, PrefActivity.class));
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void addShortcut(Context context) {
        Intent shortcutIntent = new Intent(this, LegacyDialog.class);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        shortcutIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Intent intent = new Intent();
        intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);
        intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, "^DevDrawer");
        intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, Intent.ShortcutIconResource.fromContext(context, R.drawable.shortcut_icon));
        intent.setAction(getString(R.string.action_install_shortcut));
        context.sendBroadcast(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
    }

    @Override
    public void afterTextChanged(Editable editable) {
    }

    // Method to get all apps installed and return as List
    private static List<String> getExistingPackages(Context context) {
        // get installed applications
        PackageManager pm = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.PERMISSION_GRANTED);

        Set<String> appSet = new HashSet<String>();

        for (ResolveInfo rInfo : list) {
            String appName = rInfo.activityInfo.applicationInfo.packageName;
            appSet.add(appName);
            while (appName.length() > 0) {
                int lastIndex = appName.lastIndexOf(".");
                if (lastIndex > 0) {
                    appName = appName.substring(0, lastIndex);
                    appSet.add(appName + ".*");
                } else {
                    appName = "";
                }
            }
        }

        Collator collator = Collator.getInstance();
        ArrayList<String> appList = new ArrayList<String>(appSet);
        Collections.sort(appList, collator);
        return appList;
    }

    @Override
    public void itemClicked(int filterId) {
        Snackbar.make(recyclerView, "Clicked filterId " + filterId, Snackbar.LENGTH_SHORT).show();
    }


}