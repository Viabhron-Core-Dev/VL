package fr.neamar.kiss.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "settings")
public class Setting {
    @PrimaryKey
    @NonNull
    public String key;
    
    public String value;

    public Setting(@NonNull String key, String value) {
        this.key = key;
        this.value = value;
    }
}
