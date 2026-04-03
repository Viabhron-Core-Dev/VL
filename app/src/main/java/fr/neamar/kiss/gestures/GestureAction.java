package fr.neamar.kiss.gestures;

/**
 * Defines the built-in actions that can be triggered by gestures.
 */
public class GestureAction {
    public static final String DO_NOTHING = "do_nothing";
    public static final String OPEN_APP_DRAWER = "open_app_drawer";
    public static final String OPEN_SETTINGS = "open_settings";
    public static final String LOCK_SCREEN = "lock_screen";
    public static final String PULL_NOTIFICATIONS = "pull_notifications";
    public static final String OPEN_APP = "open_app:"; // Followed by package name
    public static final String PLUGIN_ACTION = "plugin:"; // Followed by pluginId:actionId
}
