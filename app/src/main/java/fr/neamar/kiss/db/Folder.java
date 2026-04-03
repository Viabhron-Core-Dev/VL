package fr.neamar.kiss.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "folders")
public class Folder {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @NonNull
    public String name;

    public Folder(@NonNull String name) {
        this.name = name;
    }
}
