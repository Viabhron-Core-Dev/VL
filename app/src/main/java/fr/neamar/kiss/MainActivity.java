package fr.neamar.kiss;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.Gravity;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;

import fr.neamar.kiss.db.AppLock;
import fr.neamar.kiss.db.BackupManager;
import fr.neamar.kiss.db.CoreDatabase;
import fr.neamar.kiss.db.WidgetItem;
import fr.neamar.kiss.db.LogEntry;
import fr.neamar.kiss.folders.FolderManager;
import fr.neamar.kiss.gestures.GestureAction;
import fr.neamar.kiss.gestures.GestureManager;
import fr.neamar.kiss.gestures.GestureType;
import fr.neamar.kiss.gestures.HomeGestureDetector;
import fr.neamar.kiss.log.LogKeeper;
import fr.neamar.kiss.plugins.Plugin;
import fr.neamar.kiss.plugins.PluginManager;
import fr.neamar.kiss.security.BiometricHelper;
import fr.neamar.kiss.security.FirewallManager;
import fr.neamar.kiss.stats.StorageManager;
import fr.neamar.kiss.stats.SystemStatsManager;
import fr.neamar.kiss.widgets.TransparentClockWidget;
import fr.neamar.kiss.widgets.WidgetManager;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler handler = new Handler(Looper.getMainLooper());
    private CoreDatabase db;
    private FolderManager folderManager;
    private GestureManager gestureManager;
    private WidgetManager widgetManager;
    private LogKeeper logKeeper;
    private FirewallManager firewallManager;
    private PluginManager pluginManager;
    private SystemStatsManager statsManager;
    private StorageManager storageManager;
    private HomeGestureDetector homeGestureDetector;
    private LinearLayout widgetContainer;
    private TextView logDisplay;
    private TextView ramDisplay;
    private TextView storageDisplay;
    private final Runnable ramUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            updateRamStats();
            updateStorageStats();
            handler.postDelayed(this, 5000);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = CoreDatabase.getInstance(this);
        folderManager = new FolderManager(this);
        gestureManager = new GestureManager(this);
        widgetManager = new WidgetManager(this);
        logKeeper = LogKeeper.getInstance(this);
        firewallManager = new FirewallManager(this);
        pluginManager = PluginManager.getInstance(this);
        statsManager = new SystemStatsManager(this);
        storageManager = new StorageManager(this);

        logKeeper.i("MainActivity", "Application started");

        // Main Layout
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL);
        layout.setBackgroundColor(Color.parseColor("#0D0D0D"));
        layout.setPadding(40, 40, 40, 40);

        // ScrollView for the whole content
        android.widget.ScrollView scrollView = new android.widget.ScrollView(this);
        scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        scrollView.addView(layout);

        // Transparent Clock Widget
        TransparentClockWidget clockWidget = new TransparentClockWidget(this);
        layout.addView(clockWidget);

        // Widget Container
        widgetContainer = new LinearLayout(this);
        widgetContainer.setOrientation(LinearLayout.VERTICAL);
        widgetContainer.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(widgetContainer);

        // Gesture Detection
        homeGestureDetector = new HomeGestureDetector(this, type -> {
            gestureManager.executeAction(type);
            Toast.makeText(this, "Gesture: " + type.name(), Toast.LENGTH_SHORT).show();
        });
        layout.setOnTouchListener(homeGestureDetector);

        TextView title = new TextView(this);
        title.setText("VibeForge Vian Launcher\nFull Feature Demo");
        title.setTextColor(Color.WHITE);
        title.setTextSize(24);
        title.setGravity(Gravity.CENTER);
        layout.addView(title);

        // --- Widget Demo Section ---
        addSectionHeader(layout, "Widget Support");
        Button addWidgetBtn = new Button(this);
        addWidgetBtn.setText("Add Android Widget");
        addWidgetBtn.setOnClickListener(v -> widgetManager.pickWidget());
        layout.addView(addWidgetBtn);

        // --- Biometric Demo Section ---
        addSectionHeader(layout, "Biometric App Locking");
        String demoAppPackage = "com.example.demoapp";
        TextView statusText = new TextView(this);
        statusText.setTextColor(Color.parseColor("#AAAAAA"));
        layout.addView(statusText);
        updateStatus(demoAppPackage, statusText);

        Button lockButton = new Button(this);
        lockButton.setText("Toggle Lock State");
        lockButton.setOnClickListener(v -> executor.execute(() -> {
            boolean isLocked = db.coreDao().isAppLocked(demoAppPackage);
            db.coreDao().saveAppLock(new AppLock(demoAppPackage, !isLocked));
            runOnUiThread(() -> {
                updateStatus(demoAppPackage, statusText);
                Toast.makeText(this, !isLocked ? "App Locked" : "App Unlocked", Toast.LENGTH_SHORT).show();
            });
        }));
        layout.addView(lockButton);

        // --- Folder Demo Section ---
        addSectionHeader(layout, "Folder Management");
        TextView folderStatus = new TextView(this);
        folderStatus.setTextColor(Color.parseColor("#AAAAAA"));
        folderStatus.setText("No active folder demo");
        layout.addView(folderStatus);

        final int[] activeFolderId = {-1};

        Button createFolderBtn = new Button(this);
        createFolderBtn.setText("Create Folder (App A + App B)");
        createFolderBtn.setOnClickListener(v -> {
            folderManager.createFolder("Social", "com.app.a", "com.app.b", id -> {
                activeFolderId[0] = id.intValue();
                runOnUiThread(() -> {
                    folderStatus.setText("Folder 'Social' created (ID: " + id + ")");
                });
            });
        });
        layout.addView(createFolderBtn);

        // --- Backup Demo Section ---
        addSectionHeader(layout, "Backup and Restore");
        BackupManager backupManager = new BackupManager(this);
        String backupName = "vian_backup.db";

        Button backupBtn = new Button(this);
        backupBtn.setText("Backup Database");
        backupBtn.setOnClickListener(v -> {
            if (backupManager.backup(backupName)) {
                Toast.makeText(this, "Backup saved to: " + backupName, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Backup failed", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(backupBtn);

        Button restoreBtn = new Button(this);
        restoreBtn.setText("Restore Database");
        restoreBtn.setOnClickListener(v -> {
            if (backupManager.restore(backupName)) {
                Toast.makeText(this, "Restore successful. Restarting app...", Toast.LENGTH_SHORT).show();
                // In a real app, we would restart the activity or the entire process
                recreate();
            } else {
                Toast.makeText(this, "Restore failed", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(restoreBtn);

        // --- Log Keeper Section ---
        addSectionHeader(layout, "Internal Log Keeper");
        logDisplay = new TextView(this);
        logDisplay.setTextColor(Color.parseColor("#00FF00")); // Terminal green
        logDisplay.setTextSize(12);
        logDisplay.setBackgroundColor(Color.parseColor("#1A1A1A"));
        logDisplay.setPadding(10, 10, 10, 10);
        logDisplay.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 400));
        layout.addView(logDisplay);

        Button refreshLogsBtn = new Button(this);
        refreshLogsBtn.setText("Refresh Logs");
        refreshLogsBtn.setOnClickListener(v -> refreshLogs());
        layout.addView(refreshLogsBtn);

        Button clearLogsBtn = new Button(this);
        clearLogsBtn.setText("Clear Logs");
        clearLogsBtn.setOnClickListener(v -> logKeeper.clearLogs(this::refreshLogs));
        layout.addView(clearLogsBtn);

        // --- Plugin Permission Monitor Section ---
        addSectionHeader(layout, "Plugin Permission Monitor");
        String[] mockPlugins = {"WeatherPlugin", "NewsPlugin", "StockPlugin"};
        for (String pluginId : mockPlugins) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(Gravity.CENTER_VERTICAL);
            
            TextView name = new TextView(this);
            name.setText(pluginId);
            name.setTextColor(Color.WHITE);
            name.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            row.addView(name);

            android.widget.Switch internetSwitch = new android.widget.Switch(this);
            executor.execute(() -> {
                boolean allowed = firewallManager.isInternetAllowed(pluginId);
                runOnUiThread(() -> {
                    internetSwitch.setChecked(allowed);
                    internetSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        executor.execute(() -> firewallManager.setInternetAllowed(pluginId, isChecked));
                    });
                });
            });
            row.addView(internetSwitch);

            Button revokeBtn = new Button(this);
            revokeBtn.setText("Revoke All");
            revokeBtn.setOnClickListener(v -> {
                executor.execute(() -> {
                    firewallManager.setInternetAllowed(pluginId, false);
                    runOnUiThread(() -> {
                        internetSwitch.setChecked(false);
                        Toast.makeText(this, "All permissions revoked for " + pluginId, Toast.LENGTH_SHORT).show();
                    });
                });
            });
            row.addView(revokeBtn);
            
            layout.addView(row);
        }

        // --- Plugin System Section ---
        addSectionHeader(layout, "Plugin System (J2V8)");
        Button runPluginBtn = new Button(this);
        runPluginBtn.setText("Run Sample Plugin");
        runPluginBtn.setOnClickListener(v -> {
            Plugin sample = pluginManager.getSamplePlugin();
            pluginManager.startPlugin(sample);
            Toast.makeText(this, "Plugin started. Check logs below.", Toast.LENGTH_SHORT).show();
            // Refresh logs after a short delay to see plugin output
            v.postDelayed(this::refreshLogs, 1000);
        });
        layout.addView(runPluginBtn);

        // --- RAM Dashboard Section ---
        addSectionHeader(layout, "RAM Dashboard");
        ramDisplay = new TextView(this);
        ramDisplay.setTextColor(Color.WHITE);
        ramDisplay.setTextSize(16);
        ramDisplay.setPadding(20, 20, 20, 20);
        ramDisplay.setBackgroundColor(Color.parseColor("#1A1A1A"));
        layout.addView(ramDisplay);
        handler.post(ramUpdateRunnable);

        // --- Storage Section ---
        addSectionHeader(layout, "Storage and Cache");
        storageDisplay = new TextView(this);
        storageDisplay.setTextColor(Color.WHITE);
        storageDisplay.setTextSize(16);
        storageDisplay.setPadding(20, 20, 20, 20);
        storageDisplay.setBackgroundColor(Color.parseColor("#1A1A1A"));
        layout.addView(storageDisplay);

        Button clearCacheBtn = new Button(this);
        clearCacheBtn.setText("Clear App Cache");
        clearCacheBtn.setOnClickListener(v -> {
            storageManager.clearCache();
            Toast.makeText(this, "Cache cleared", Toast.LENGTH_SHORT).show();
            updateStorageStats();
        });
        layout.addView(clearCacheBtn);

        setContentView(scrollView);
        
        refreshWidgets();
        refreshLogs();
    }

    private void updateRamStats() {
        SystemStatsManager.RamStats stats = statsManager.getRamStats();
        String text = "Total: " + stats.format(stats.total) + "\n" +
                      "Used: " + stats.format(stats.used) + "\n" +
                      "Free: " + stats.format(stats.free) + "\n" +
                      "Status: " + (stats.isLowMemory ? "LOW MEMORY" : "Normal");
        ramDisplay.setText(text);
    }

    private void updateStorageStats() {
        StorageManager.StorageStats stats = storageManager.getInternalStorageStats();
        long cacheSize = storageManager.getCacheSize();
        String text = "Internal Storage:\n" +
                      "Total: " + stats.format(stats.total) + "\n" +
                      "Used: " + stats.format(stats.used) + "\n" +
                      "Free: " + stats.format(stats.free) + "\n\n" +
                      "App Cache: " + (cacheSize / (1024.0 * 1024.0)) + " MB";
        storageDisplay.setText(text);
    }

    private void refreshLogs() {
        logKeeper.getRecentLogs(logs -> {
            StringBuilder sb = new StringBuilder();
            for (LogEntry entry : logs) {
                sb.append("[").append(entry.level).append("] ")
                  .append(entry.tag).append(": ")
                  .append(entry.message).append("\n");
            }
            runOnUiThread(() -> logDisplay.setText(sb.toString()));
        });
    }

    private void refreshWidgets() {
        widgetManager.loadWidgets(widgets -> {
            widgetContainer.removeAllViews();
            for (WidgetItem item : widgets) {
                View view = widgetManager.createWidgetView(item);
                if (view != null) {
                    widgetContainer.addView(view);
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        widgetManager.handleActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            refreshWidgets();
        }
    }

    private void addSectionHeader(LinearLayout layout, String text) {
        TextView header = new TextView(this);
        header.setText("\n" + text);
        header.setTextColor(Color.parseColor("#6200EE"));
        header.setTextSize(18);
        header.setPadding(0, 20, 0, 10);
        layout.addView(header);
    }

    private void updateStatus(String packageName, TextView statusText) {
        executor.execute(() -> {
            boolean isLocked = db.coreDao().isAppLocked(packageName);
            runOnUiThread(() -> statusText.setText("App Status: " + (isLocked ? "LOCKED 🔒" : "UNLOCKED 🔓")));
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        widgetManager.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        widgetManager.stopListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        widgetManager.stopListening();
        pluginManager.stopAll();
        handler.removeCallbacks(ramUpdateRunnable);
        executor.shutdown();
    }
}
