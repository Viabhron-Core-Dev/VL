package fr.neamar.kiss.plugins;

import android.content.Context;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.neamar.kiss.log.LogKeeper;

/**
 * Manages the lifecycle of all plugins.
 * Ensures each plugin runs in its own sandboxed environment.
 */
public class PluginManager {
    private static final String TAG = "PluginManager";
    private static PluginManager instance;
    private final Context context;
    private final Map<String, PluginRuntime> activePlugins = new HashMap<>();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final LogKeeper logKeeper;

    private PluginManager(Context context) {
        this.context = context.getApplicationContext();
        this.logKeeper = LogKeeper.getInstance(this.context);
    }

    public static synchronized PluginManager getInstance(Context context) {
        if (instance == null) {
            instance = new PluginManager(context.getApplicationContext());
        }
        return instance;
    }

    /**
     * Loads and starts a plugin.
     * @param plugin The plugin to start.
     */
    public void startPlugin(Plugin plugin) {
        executor.execute(() -> {
            if (activePlugins.containsKey(plugin.id)) {
                stopPlugin(plugin.id);
            }
            PluginRuntime runtime = new PluginRuntime(context, plugin);
            activePlugins.put(plugin.id, runtime);
            runtime.start();
            logKeeper.i(TAG, "Plugin [" + plugin.name + "] is now active.");
        });
    }

    /**
     * Stops an active plugin.
     * @param pluginId The ID of the plugin to stop.
     */
    public void stopPlugin(String pluginId) {
        executor.execute(() -> {
            PluginRuntime runtime = activePlugins.remove(pluginId);
            if (runtime != null) {
                runtime.stop();
                logKeeper.i(TAG, "Plugin [" + pluginId + "] stopped.");
            }
        });
    }

    /**
     * Stops all active plugins.
     */
    public void stopAll() {
        executor.execute(() -> {
            for (PluginRuntime runtime : activePlugins.values()) {
                runtime.stop();
            }
            activePlugins.clear();
            logKeeper.i(TAG, "All plugins stopped.");
        });
    }

    /**
     * Returns a sample plugin for demonstration.
     */
    public Plugin getSamplePlugin() {
        String script = "console.log('Hello from Vian Plugin!');\n" +
                        "storage.set('last_run', new Date().toString());\n" +
                        "var lastRun = storage.get('last_run');\n" +
                        "console.log('Last run was: ' + lastRun);\n" +
                        "var networkResult = network.fetch('https://api.example.com');\n" +
                        "console.log('Network result: ' + networkResult);";
        return new Plugin("sample_plugin", "Sample Plugin", script);
    }
}
