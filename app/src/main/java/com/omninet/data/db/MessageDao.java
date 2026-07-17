package com.omninet.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.omninet.data.models.Message;
import java.util.List;

@Dao
public interface MessageDao {
    @Insert
    long insert(Message message);

    @Update
    void update(Message message);

    @Delete
    void delete(Message message);

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp ASC")
    List<Message> getMessagesByChat(int chatId);

    @Query("SELECT * FROM messages WHERE id = :id")
    Message getMessageById(int id);

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC LIMIT 1")
    Message getLastMessage(int chatId);

    @Query("SELECT COUNT(*) FROM messages WHERE chatId = :chatId AND isRead = 0")
    int getUnreadCount(int chatId);

    @Query("UPDATE messages SET isRead = 1 WHERE chatId = :chatId")
    void markChatAsRead(int chatId);

    @Query("SELECT * FROM messages WHERE chatId = :chatId AND messageText LIKE '%' || :query || '%' ORDER BY timestamp DESC")
    List<Message> searchMessages(int chatId, String query);

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    void deleteMessagesByChat(int chatId);
}
