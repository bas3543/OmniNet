package com.omninet.data.db;

import android.content.Context;
import androidx.room.*;
import com.omninet.data.models.Contact;
import com.omninet.data.models.Message;

@Database(
    entities = {Contact.class, Message.class},
    version  = 1,
    exportSchema = false
)
public abstract class OmniDatabase extends RoomDatabase {

    private static volatile OmniDatabase instance;

    public abstract ContactDao contactDao();
    public abstract MessageDao messageDao();

    public static OmniDatabase get(Context ctx) {
        if (instance == null) {
            synchronized (OmniDatabase.class) {
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        ctx.getApplicationContext(),
                        OmniDatabase.class,
                        "omninet.db"
                    )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
                }
            }
        }
        return instance;
    }
}
