package fr.neamar.kiss.gestures;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.widget.Toast;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.neamar.kiss.db.CoreDatabase;
import fr.neamar.kiss.db.GestureAssignment;

/**
 * Executes actions associated with gestures.
 */
public class GestureManager {
    private final Context context;
    private final CoreDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public GestureManager(Context context) {
        this.context = context;
        this.db = CoreDatabase.getInstance(context);
    }

    public void executeAction(GestureType type) {
        executor.execute(() -> {
            String action = db.coreDao().getGestureAction(type.name());
            if (action == null) {
                // Default assignments if none exist
                action = getDefaultAction(type);
            }
            
            final String finalAction = action;
            if (context instanceof android.app.Activity) {
                ((android.app.Activity) context).runOnUiThread(() -> performAction(finalAction));
            }
        });
    }

    private String getDefaultAction(GestureType type) {
        switch (type) {
            case SWIPE_UP: return GestureAction.OPEN_APP_DRAWER;
            case SWIPE_DOWN: return GestureAction.PULL_NOTIFICATIONS;
            case LONG_PRESS: return GestureAction.OPEN_SETTINGS;
            default: return GestureAction.DO_NOTHING;
        }
    }

    private void performAction(String action) {
        if (action.equals(GestureAction.DO_NOTHING)) return;

        switch (action) {
            case GestureAction.OPEN_APP_DRAWER:
                Toast.makeText(context, "Action: Open App Drawer", Toast.LENGTH_SHORT).show();
                break;
            case GestureAction.OPEN_SETTINGS:
                Toast.makeText(context, "Action: Open Launcher Settings", Toast.LENGTH_SHORT).show();
                break;
            case GestureAction.PULL_NOTIFICATIONS:
                expandNotifications();
                break;
            case GestureAction.LOCK_SCREEN:
                Toast.makeText(context, "Action: Lock Screen (Requires Accessibility)", Toast.LENGTH_SHORT).show();
                break;
            default:
                if (action.startsWith(GestureAction.OPEN_APP)) {
                    String pkg = action.substring(GestureAction.OPEN_APP.length());
                    Toast.makeText(context, "Action: Open App " + pkg, Toast.LENGTH_SHORT).show();
                } else if (action.startsWith(GestureAction.PLUGIN_ACTION)) {
                    Toast.makeText(context, "Action: Trigger Plugin " + action, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    private void expandNotifications() {
        try {
            Object service = context.getSystemService("statusbar");
            Class<?> statusbarManager = Class.forName("android.app.StatusBarManager");
            Method expand = statusbarManager.getMethod("expandNotificationsPanel");
            expand.invoke(service);
        } catch (Exception e) {
            Toast.makeText(context, "Action: Pull Notifications", Toast.LENGTH_SHORT).show();
        }
    }
    
    public void setGestureAction(GestureType type, String action) {
        executor.execute(() -> {
            db.coreDao().saveGestureAssignment(new GestureAssignment(type.name(), action));
        });
    }
}
