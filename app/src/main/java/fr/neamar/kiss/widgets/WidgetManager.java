package fr.neamar.kiss.widgets;

import android.app.Activity;
import android.appwidget.AppWidgetHost;
import android.appwidget.AppWidgetHostView;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProviderInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.neamar.kiss.db.CoreDatabase;
import fr.neamar.kiss.db.WidgetItem;

/**
 * Manages Android App Widgets: picking, binding, and rendering.
 */
public class WidgetManager {
    private static final String TAG = "WidgetManager";
    public static final int HOST_ID = 1024;
    public static final int REQUEST_PICK_WIDGET = 100;
    public static final int REQUEST_CREATE_WIDGET = 101;
    public static final int REQUEST_BIND_WIDGET = 102;

    private final Activity activity;
    private final AppWidgetHost appWidgetHost;
    private final AppWidgetManager appWidgetManager;
    private final CoreDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public WidgetManager(Activity activity) {
        this.activity = activity;
        this.appWidgetHost = new AppWidgetHost(activity, HOST_ID);
        this.appWidgetManager = AppWidgetManager.getInstance(activity);
        this.db = CoreDatabase.getInstance(activity);
    }

    public void startListening() {
        appWidgetHost.startListening();
    }

    public void stopListening() {
        appWidgetHost.stopListening();
    }

    /**
     * Opens the system widget picker.
     */
    public void pickWidget() {
        int appWidgetId = appWidgetHost.allocateAppWidgetId();
        Intent pickIntent = new Intent(AppWidgetManager.ACTION_APPWIDGET_PICK);
        pickIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        activity.startActivityForResult(pickIntent, REQUEST_PICK_WIDGET);
    }

    /**
     * Handles the result from the widget picker or configuration activity.
     */
    public void handleActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_WIDGET) {
                configureWidget(data);
            } else if (requestCode == REQUEST_CREATE_WIDGET) {
                createWidget(data);
            }
        } else if (resultCode == Activity.RESULT_CANCELED && data != null) {
            int appWidgetId = data.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
            if (appWidgetId != -1) {
                appWidgetHost.deleteAppWidgetId(appWidgetId);
            }
        }
    }

    private void configureWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);

        if (appWidgetInfo.configure != null) {
            Intent intent = new Intent(AppWidgetManager.ACTION_APPWIDGET_CONFIGURE);
            intent.setComponent(appWidgetInfo.configure);
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            activity.startActivityForResult(intent, REQUEST_CREATE_WIDGET);
        } else {
            createWidget(data);
        }
    }

    private void createWidget(Intent data) {
        Bundle extras = data.getExtras();
        int appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID, -1);
        AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(appWidgetId);

        WidgetItem item = new WidgetItem(
                appWidgetId,
                appWidgetInfo.provider.getPackageName(),
                appWidgetInfo.provider.getClassName(),
                0, 0, 4, 2 // Default position and size
        );

        executor.execute(() -> {
            db.coreDao().saveWidget(item);
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "Widget Added: " + appWidgetInfo.label, Toast.LENGTH_SHORT).show();
                // In a real implementation, we would trigger a UI refresh here
            });
        });
    }

    /**
     * Creates a view for a widget.
     */
    public View createWidgetView(WidgetItem item) {
        AppWidgetProviderInfo appWidgetInfo = appWidgetManager.getAppWidgetInfo(item.appWidgetId);
        if (appWidgetInfo == null) return null;

        AppWidgetHostView hostView = appWidgetHost.createView(activity, item.appWidgetId, appWidgetInfo);
        hostView.setAppWidget(item.appWidgetId, appWidgetInfo);
        
        // Add long press to remove
        hostView.setOnLongClickListener(v -> {
            removeWidget(item.appWidgetId);
            return true;
        });

        return hostView;
    }

    public void removeWidget(int appWidgetId) {
        executor.execute(() -> {
            db.coreDao().deleteWidget(appWidgetId);
            appWidgetHost.deleteAppWidgetId(appWidgetId);
            activity.runOnUiThread(() -> {
                Toast.makeText(activity, "Widget Removed", Toast.LENGTH_SHORT).show();
            });
        });
    }

    public void loadWidgets(OnWidgetsLoadedListener listener) {
        executor.execute(() -> {
            List<WidgetItem> widgets = db.coreDao().getAllWidgets();
            activity.runOnUiThread(() -> listener.onWidgetsLoaded(widgets));
        });
    }

    public interface OnWidgetsLoadedListener {
        void onWidgetsLoaded(List<WidgetItem> widgets);
    }
}
