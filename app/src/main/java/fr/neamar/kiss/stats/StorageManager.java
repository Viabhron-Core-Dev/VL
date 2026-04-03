package fr.neamar.kiss.stats;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import java.io.File;

/**
 * Manages storage and cache statistics.
 */
public class StorageManager {
    private final Context context;

    public StorageManager(Context context) {
        this.context = context.getApplicationContext();
    }

    /**
     * Returns the internal storage statistics.
     */
    public StorageStats getInternalStorageStats() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        long availableBlocks = stat.getAvailableBlocksLong();
        
        return new StorageStats(totalBlocks * blockSize, (totalBlocks - availableBlocks) * blockSize, availableBlocks * blockSize);
    }

    /**
     * Returns the total cache size of the app.
     */
    public long getCacheSize() {
        long size = 0;
        size += getDirSize(context.getCacheDir());
        size += getDirSize(context.getExternalCacheDir());
        return size;
    }

    /**
     * Clears the app's cache.
     */
    public void clearCache() {
        deleteDir(context.getCacheDir());
        deleteDir(context.getExternalCacheDir());
    }

    private long getDirSize(File dir) {
        if (dir == null || !dir.exists()) return 0;
        long size = 0;
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    size += getDirSize(file);
                } else {
                    size += file.length();
                }
            }
        }
        return size;
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    public static class StorageStats {
        public final long total;
        public final long used;
        public final long free;

        public StorageStats(long total, long used, long free) {
            this.total = total;
            this.used = used;
            this.free = free;
        }

        public String format(long bytes) {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
