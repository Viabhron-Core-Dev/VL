package fr.neamar.kiss.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "app_locks")
public class AppLock {
    @PrimaryKey
    @NonNull
    public String packageName;
    
    public boolean isLocked;

    public AppLock(@NonNull String packageName, boolean isLocked) {
        this.packageName = packageName;
        this.isLocked = isLocked;
    }
}
