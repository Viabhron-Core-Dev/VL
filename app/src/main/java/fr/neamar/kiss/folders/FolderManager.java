package fr.neamar.kiss.folders;

import android.content.Context;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.neamar.kiss.db.CoreDatabase;
import fr.neamar.kiss.db.Folder;
import fr.neamar.kiss.db.FolderItem;

/**
 * Manages folder operations including the auto-deletion rule.
 */
public class FolderManager {
    private final CoreDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    public FolderManager(Context context) {
        this.db = CoreDatabase.getInstance(context);
    }

    public interface FolderCallback<T> {
        void onResult(T result);
    }

    /**
     * Creates a new folder with two initial apps.
     */
    public void createFolder(String name, String app1, String app2, FolderCallback<Long> callback) {
        executor.execute(() -> {
            long folderId = db.coreDao().createFolder(new Folder(name));
            db.coreDao().addFolderItem(new FolderItem((int) folderId, app1));
            db.coreDao().addFolderItem(new FolderItem((int) folderId, app2));
            if (callback != null) callback.onResult(folderId);
        });
    }

    /**
     * Adds an app to an existing folder.
     */
    public void addAppToFolder(int folderId, String packageName) {
        executor.execute(() -> {
            db.coreDao().addFolderItem(new FolderItem(folderId, packageName));
        });
    }

    /**
     * Removes an app from a folder and applies the auto-delete rule.
     */
    public void removeAppFromFolder(int folderId, String packageName, FolderCallback<Boolean> onFolderDeleted) {
        executor.execute(() -> {
            db.coreDao().removeFolderItem(folderId, packageName);
            int count = db.coreDao().getFolderItemCount(folderId);
            
            if (count < 2) {
                db.coreDao().deleteFolder(folderId);
                if (onFolderDeleted != null) onFolderDeleted.onResult(true);
            } else {
                if (onFolderDeleted != null) onFolderDeleted.onResult(false);
            }
        });
    }

    /**
     * Renames a folder.
     */
    public void renameFolder(int folderId, String newName) {
        executor.execute(() -> {
            db.coreDao().updateFolderName(folderId, newName);
        });
    }

    /**
     * Gets all items in a folder.
     */
    public void getFolderItems(int folderId, FolderCallback<List<FolderItem>> callback) {
        executor.execute(() -> {
            List<FolderItem> items = db.coreDao().getFolderItems(folderId);
            if (callback != null) callback.onResult(items);
        });
    }
}
