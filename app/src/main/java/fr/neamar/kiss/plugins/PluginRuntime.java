package fr.neamar.kiss.plugins;

import android.content.Context;
import com.eclipsesource.v8.V8;
import com.eclipsesource.v8.V8Object;
import fr.neamar.kiss.db.CoreDatabase;
import fr.neamar.kiss.db.Setting;
import fr.neamar.kiss.log.LogKeeper;
import fr.neamar.kiss.security.FirewallManager;

/**
 * A sandboxed JavaScript execution environment for a single plugin.
 * Uses J2V8 for high-performance script execution.
 */
public class PluginRuntime {
    private static final String TAG = "PluginRuntime";
    private final Context context;
    private final Plugin plugin;
    private final LogKeeper logKeeper;
    private final FirewallManager firewallManager;
    private final CoreDatabase db;
    private V8 v8;

    public PluginRuntime(Context context, Plugin plugin) {
        this.context = context.getApplicationContext();
        this.plugin = plugin;
        this.logKeeper = LogKeeper.getInstance(this.context);
        this.firewallManager = new FirewallManager(this.context);
        this.db = CoreDatabase.getInstance(this.context);
    }

    /**
     * Initializes the V8 environment and registers the Java-JS bridge.
     */
    public void start() {
        try {
            v8 = V8.createV8Runtime();
            registerBridge();
            v8.executeScript(plugin.script);
            logKeeper.i(TAG, "Plugin [" + plugin.name + "] started successfully.");
        } catch (Exception e) {
            logKeeper.e(TAG, "Error starting plugin [" + plugin.name + "]: " + e.getMessage());
        }
    }

    /**
     * Stops the V8 environment and releases native resources.
     */
    public void stop() {
        if (v8 != null) {
            v8.release();
            v8 = null;
            logKeeper.i(TAG, "Plugin [" + plugin.name + "] stopped.");
        }
    }

    private void registerBridge() {
        // --- console.log bridge ---
        V8Object console = new V8Object(v8);
        v8.add("console", console);
        console.registerJavaMethod((receiver, parameters) -> {
            if (parameters.length() > 0) {
                logKeeper.d("Plugin:" + plugin.name, parameters.get(0).toString());
            }
        }, "log");
        console.release();

        // --- storage bridge ---
        V8Object storage = new V8Object(v8);
        v8.add("storage", storage);
        
        storage.registerJavaMethod((receiver, parameters) -> {
            if (parameters.length() >= 2) {
                String key = parameters.get(0).toString();
                String value = parameters.get(1).toString();
                db.coreDao().saveSetting(new Setting("plugin_" + plugin.id + "_" + key, value));
            }
        }, "set");

        storage.registerJavaMethod((receiver, parameters) -> {
            if (parameters.length() >= 1) {
                String key = parameters.get(0).toString();
                return db.coreDao().getSetting("plugin_" + plugin.id + "_" + key);
            }
            return null;
        }, "get");
        
        storage.release();

        // --- network bridge (firewall-protected) ---
        V8Object network = new V8Object(v8);
        v8.add("network", network);
        network.registerJavaMethod((receiver, parameters) -> {
            if (!firewallManager.isInternetAllowed(plugin.id)) {
                logKeeper.w(TAG, "Plugin [" + plugin.name + "] attempted unauthorized network access.");
                return "BLOCKED";
            }
            // Mock network call for now
            return "SUCCESS: Data fetched from network";
        }, "fetch");
        network.release();
    }
}
