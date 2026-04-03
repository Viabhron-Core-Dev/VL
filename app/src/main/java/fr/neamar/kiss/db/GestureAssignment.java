package fr.neamar.kiss.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "gesture_assignments")
public class GestureAssignment {
    @PrimaryKey
    @NonNull
    public String gestureType; // Use GestureType.name()
    
    @NonNull
    public String action; // Use GestureAction constants

    public GestureAssignment(@NonNull String gestureType, @NonNull String action) {
        this.gestureType = gestureType;
        this.action = action;
    }
}
