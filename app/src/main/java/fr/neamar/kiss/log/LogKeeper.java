package fr.neamar.kiss.log;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.neamar.kiss.db.CoreDatabase;
import fr.neamar.kiss.db.LogEntry;

/**
 * Internal log keeper for debugging and system monitoring.
 * Persists logs to the encrypted database.
 */
public class LogKeeper {
    private static final String TAG = "LogKeeper";
    private static LogKeeper instance;
    private final CoreDatabase db;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    private LogKeeper(Context context) {
        this.db = CoreDatabase.getInstance(context);
    }

    public static synchronized LogKeeper getInstance(Context context) {
        if (instance == null) {
            instance = new LogKeeper(context.getApplicationContext());
        }
        return instance;
    }

    public void d(String tag, String message) {
        log("DEBUG", tag, message);
        Log.d(tag, message);
    }

    public void i(String tag, String message) {
        log("INFO", tag, message);
        Log.i(tag, message);
    }

    public void w(String tag, String message) {
        log("WARN", tag, message);
        Log.w(tag, message);
    }

    public void e(String tag, String message) {
        log("ERROR", tag, message);
        Log.e(tag, message);
    }

    private void log(String level, String tag, String message) {
        executor.execute(() -> {
            LogEntry entry = new LogEntry(System.currentTimeMillis(), level, tag, message);
            db.coreDao().insertLog(entry);
        });
    }

    public void getRecentLogs(OnLogsLoadedListener listener) {
        executor.execute(() -> {
            List<LogEntry> logs = db.coreDao().getRecentLogs();
            listener.onLogsLoaded(logs);
        });
    }

    public void clearLogs(Runnable onComplete) {
        executor.execute(() -> {
            db.coreDao().clearLogs();
            if (onComplete != null) {
                onComplete.run();
            }
        });
    }

    public interface OnLogsLoadedListener {
        void onLogsLoaded(List<LogEntry> logs);
    }
}
