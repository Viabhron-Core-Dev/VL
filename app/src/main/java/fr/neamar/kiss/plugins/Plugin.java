package fr.neamar.kiss.plugins;

import androidx.annotation.NonNull;

/**
 * Represents a sandboxed JavaScript plugin.
 */
public class Plugin {
    @NonNull
    public final String id;
    @NonNull
    public final String name;
    @NonNull
    public final String script;

    public Plugin(@NonNull String id, @NonNull String name, @NonNull String script) {
        this.id = id;
        this.name = name;
        this.script = script;
    }
}
