package com.omninet.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.omninet.data.models.Message;
import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Message message);

    @Update
    void update(Message message);

    @Query("SELECT * FROM messages WHERE threadId = :number " +
           "OR fromNumber = :number ORDER BY timestamp ASC")
    LiveData<List<Message>> getThread(String number);

    @Query("SELECT * FROM messages WHERE threadId = :number " +
           "OR fromNumber = :number ORDER BY timestamp ASC")
    List<Message> getThreadSync(String number);

    @Query("SELECT * FROM messages WHERE msgId = :id LIMIT 1")
    Message getById(String id);

    @Query("SELECT * FROM messages WHERE sent = 0")
    List<Message> getPending();

    @Query("UPDATE messages SET sent = 1 WHERE msgId = :id")
    void markSent(String id);

    @Query("UPDATE messages SET delivered = 1 WHERE msgId = :id")
    void markDelivered(String id);

    @Query("UPDATE messages SET read = 1 WHERE threadId = :number")
    void markAllRead(String number);

    @Query("SELECT COUNT(*) FROM messages WHERE threadId = :number AND read = 0 AND fromNumber != :myNumber")
    int getUnreadCount(String number, String myNumber);

    @Query("SELECT * FROM messages GROUP BY threadId ORDER BY timestamp DESC")
    LiveData<List<Message>> getLatestPerThread();

    @Query("DELETE FROM messages WHERE threadId = :number")
    void deleteThread(String number);
}
