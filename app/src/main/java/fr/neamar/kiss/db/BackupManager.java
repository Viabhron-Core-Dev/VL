package fr.neamar.kiss.db;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * Manages database backup and restore.
 * Note: Since the database is encrypted with a Keystore-backed key, 
 * simple file-based backups are only restorable on the same device/install 
 * unless a master password system is implemented.
 */
public class BackupManager {
    private static final String TAG = "BackupManager";
    private final Context context;

    public BackupManager(Context context) {
        this.context = context;
    }

    /**
     * Backs up the encrypted database file to a local backup file.
     */
    public boolean backup(String backupFileName) {
        CoreDatabase.getInstance(context).close();
        File dbFile = context.getDatabasePath("vian_core.db");
        File backupFile = new File(context.getExternalFilesDir(null), backupFileName);

        try {
            copyFile(dbFile, backupFile);
            Log.d(TAG, "Backup successful: " + backupFile.getAbsolutePath());
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Backup failed", e);
            return false;
        }
    }

    /**
     * Restores the database from a backup file.
     */
    public boolean restore(String backupFileName) {
        CoreDatabase.getInstance(context).close();
        File dbFile = context.getDatabasePath("vian_core.db");
        File backupFile = new File(context.getExternalFilesDir(null), backupFileName);

        if (!backupFile.exists()) {
            Log.e(TAG, "Backup file not found");
            return false;
        }

        try {
            copyFile(backupFile, dbFile);
            Log.d(TAG, "Restore successful");
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Restore failed", e);
            return false;
        }
    }

    private void copyFile(File source, File dest) throws IOException {
        try (FileChannel sourceChannel = new FileInputStream(source).getChannel();
             FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            destChannel.transferFrom(sourceChannel, 0, sourceChannel.size());
        }
    }
}
