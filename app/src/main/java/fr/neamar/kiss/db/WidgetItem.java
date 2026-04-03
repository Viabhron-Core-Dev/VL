package fr.neamar.kiss.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "widgets")
public class WidgetItem {
    @PrimaryKey
    public int appWidgetId;

    @NonNull
    public String providerPackage;
    
    @NonNull
    public String providerClass;

    public int x;
    public int y;
    public int spanX;
    public int spanY;

    public WidgetItem(int appWidgetId, @NonNull String providerPackage, @NonNull String providerClass, int x, int y, int spanX, int spanY) {
        this.appWidgetId = appWidgetId;
        this.providerPackage = providerPackage;
        this.providerClass = providerClass;
        this.x = x;
        this.y = y;
        this.spanX = spanX;
        this.spanY = spanY;
    }
}
