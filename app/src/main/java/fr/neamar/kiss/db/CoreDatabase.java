package fr.neamar.kiss.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import net.sqlcipher.database.SupportFactory;

@Database(entities = {AppLock.class, Setting.class, PluginPermission.class, Folder.class, FolderItem.class, GestureAssignment.class, WidgetItem.class, LogEntry.class}, version = 5, exportSchema = false)
public abstract class CoreDatabase extends RoomDatabase {
    private static final String DB_NAME = "vian_core.db";
    private static volatile CoreDatabase instance;

    public abstract CoreDao coreDao();

    public static CoreDatabase getInstance(Context context) {
        if (instance == null) {
            synchronized (CoreDatabase.class) {
                if (instance == null) {
                    byte[] passphrase = SecurityUtils.getDatabasePassphrase();
                    SupportFactory factory = new SupportFactory(passphrase);
                    
                    instance = Room.databaseBuilder(context.getApplicationContext(),
                                    CoreDatabase.class, DB_NAME)
                            .openHelperFactory(factory)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return instance;
    }
}
