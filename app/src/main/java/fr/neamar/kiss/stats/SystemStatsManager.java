package fr.neamar.kiss.stats;

import android.app.ActivityManager;
import android.content.Context;

/**
 * Manages system statistics like RAM usage.
 */
public class SystemStatsManager {
    private final Context context;
    private final ActivityManager activityManager;

    public SystemStatsManager(Context context) {
        this.context = context.getApplicationContext();
        this.activityManager = (ActivityManager) this.context.getSystemService(Context.ACTIVITY_SERVICE);
    }

    /**
     * Returns the current RAM usage statistics.
     */
    public RamStats getRamStats() {
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);
        
        long total = memoryInfo.totalMem;
        long free = memoryInfo.availMem;
        long used = total - free;
        
        return new RamStats(total, used, free, memoryInfo.lowMemory);
    }

    public static class RamStats {
        public final long total;
        public final long used;
        public final long free;
        public final boolean isLowMemory;

        public RamStats(long total, long used, long free, boolean isLowMemory) {
            this.total = total;
            this.used = used;
            this.free = free;
            this.isLowMemory = isLowMemory;
        }

        public String format(long bytes) {
            return String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0));
        }
    }
}
