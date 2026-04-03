package fr.neamar.kiss.security;

import android.content.Context;
import fr.neamar.kiss.db.CoreDatabase;
import fr.neamar.kiss.db.PluginPermission;
import fr.neamar.kiss.log.LogKeeper;

/**
 * Manages the "Internet Firewall" for plugins.
 * Controls whether a specific plugin is allowed to access the network.
 */
public class FirewallManager {
    private static final String TAG = "FirewallManager";
    public static final String PERMISSION_INTERNET = "INTERNET";
    
    private final Context context;
    private final CoreDatabase db;
    private final LogKeeper logKeeper;

    public FirewallManager(Context context) {
        this.context = context.getApplicationContext();
        this.db = CoreDatabase.getInstance(this.context);
        this.logKeeper = LogKeeper.getInstance(this.context);
    }

    /**
     * Checks if a plugin is allowed to access the internet.
     * @param pluginId The unique ID of the plugin.
     * @return true if internet access is granted, false otherwise.
     */
    public boolean isInternetAllowed(String pluginId) {
        boolean allowed = db.coreDao().hasPluginPermission(pluginId, PERMISSION_INTERNET);
        logKeeper.d(TAG, "Checking firewall for plugin [" + pluginId + "]: " + (allowed ? "ALLOWED" : "BLOCKED"));
        return allowed;
    }

    /**
     * Grants or revokes internet access for a plugin.
     * @param pluginId The unique ID of the plugin.
     * @param allowed true to grant, false to revoke.
     */
    public void setInternetAllowed(String pluginId, boolean allowed) {
        PluginPermission permission = new PluginPermission(pluginId, PERMISSION_INTERNET, allowed);
        db.coreDao().savePluginPermission(permission);
        logKeeper.i(TAG, "Firewall update for plugin [" + pluginId + "]: " + (allowed ? "GRANTED" : "REVOKED"));
    }
}
