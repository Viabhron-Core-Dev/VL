package fr.neamar.kiss.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CoreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveSetting(Setting setting);

    @Query("SELECT value FROM settings WHERE `key` = :key LIMIT 1")
    String getSetting(String key);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveAppLock(AppLock appLock);

    @Query("SELECT isLocked FROM app_locks WHERE packageName = :packageName LIMIT 1")
    boolean isAppLocked(String packageName);

    @Query("SELECT * FROM app_locks WHERE isLocked = 1")
    List<AppLock> getLockedApps();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void savePluginPermission(PluginPermission permission);

    @Query("SELECT isGranted FROM plugin_permissions WHERE pluginId = :pluginId AND permissionName = :permissionName LIMIT 1")
    boolean hasPluginPermission(String pluginId, String permissionName);

    // Folder operations
    @Insert
    long createFolder(Folder folder);

    @Query("UPDATE folders SET name = :name WHERE id = :folderId")
    void updateFolderName(int folderId, String name);

    @Query("DELETE FROM folders WHERE id = :folderId")
    void deleteFolder(int folderId);

    @Query("SELECT * FROM folders")
    List<Folder> getAllFolders();

    @Insert
    void addFolderItem(FolderItem item);

    @Query("DELETE FROM folder_items WHERE folderId = :folderId AND packageName = :packageName")
    void removeFolderItem(int folderId, String packageName);

    @Query("SELECT * FROM folder_items WHERE folderId = :folderId")
    List<FolderItem> getFolderItems(int folderId);

    @Query("SELECT COUNT(*) FROM folder_items WHERE folderId = :folderId")
    int getFolderItemCount(int folderId);

    // Gesture assignments
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveGestureAssignment(GestureAssignment assignment);

    @Query("SELECT action FROM gesture_assignments WHERE gestureType = :gestureType LIMIT 1")
    String getGestureAction(String gestureType);

    // Widget operations
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveWidget(WidgetItem widget);

    @Query("DELETE FROM widgets WHERE appWidgetId = :appWidgetId")
    void deleteWidget(int appWidgetId);

    @Query("SELECT * FROM widgets")
    List<WidgetItem> getAllWidgets();

    @Query("UPDATE widgets SET x = :x, y = :y WHERE appWidgetId = :appWidgetId")
    void updateWidgetPosition(int appWidgetId, int x, int y);

    @Query("UPDATE widgets SET spanX = :spanX, spanY = :spanY WHERE appWidgetId = :appWidgetId")
    void updateWidgetSize(int appWidgetId, int spanX, int spanY);

    // Log operations
    @Insert
    void insertLog(LogEntry log);

    @Query("SELECT * FROM logs ORDER BY timestamp DESC LIMIT 100")
    List<LogEntry> getRecentLogs();

    @Query("DELETE FROM logs")
    void clearLogs();
}
