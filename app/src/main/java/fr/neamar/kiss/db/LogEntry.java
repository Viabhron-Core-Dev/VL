package fr.neamar.kiss.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "logs")
public class LogEntry {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public long timestamp;
    public String level;
    public String tag;
    public String message;

    public LogEntry(long timestamp, String level, String tag, String message) {
        this.timestamp = timestamp;
        this.level = level;
        this.tag = tag;
        this.message = message;
    }
}
