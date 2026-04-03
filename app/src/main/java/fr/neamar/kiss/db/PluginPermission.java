package fr.neamar.kiss.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "plugin_permissions")
public class PluginPermission {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String pluginId;
    
    @NonNull
    public String permissionName;
    
    public boolean isGranted;

    public PluginPermission(@NonNull String pluginId, @NonNull String permissionName, boolean isGranted) {
        this.pluginId = pluginId;
        this.permissionName = permissionName;
        this.isGranted = isGranted;
    }
}
