package com.omninet.data.db;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import com.omninet.data.models.Call;
import com.omninet.data.models.Chat;
import com.omninet.data.models.Contact;
import com.omninet.data.models.Group;
import com.omninet.data.models.Message;
import com.omninet.data.models.Status;

@Database(entities = {Contact.class, Chat.class, Message.class, Group.class, Status.class, Call.class}, version = 1)
public abstract class OmniDatabase extends RoomDatabase {
    public abstract ContactDao contactDao();
    public abstract ChatDao chatDao();
    public abstract MessageDao messageDao();
    public abstract GroupDao groupDao();
    public abstract StatusDao statusDao();
    public abstract CallDao callDao();

    private static OmniDatabase instance;

    public static synchronized OmniDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            OmniDatabase.class, "omninet_database")
                    .allowMainThreadQueries()
                    .build();
        }
        return instance;
    }
}
