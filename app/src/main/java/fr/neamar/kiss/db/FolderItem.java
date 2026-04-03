package fr.neamar.kiss.db;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "folder_items",
        foreignKeys = @ForeignKey(entity = Folder.class,
                parentColumns = "id",
                childColumns = "folderId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("folderId")})
public class FolderItem {
    @PrimaryKey(autoGenerate = true)
    public int id;

    public int folderId;

    @NonNull
    public String packageName;

    public FolderItem(int folderId, @NonNull String packageName) {
        this.folderId = folderId;
        this.packageName = packageName;
    }
}
