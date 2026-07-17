package com.omninet.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.omninet.data.models.Chat;
import java.util.List;

@Dao
public interface ChatDao {
    @Insert
    long insert(Chat chat);

    @Update
    void update(Chat chat);

    @Delete
    void delete(Chat chat);

    @Query("SELECT * FROM chats ORDER BY lastMessageTime DESC")
    List<Chat> getAllChats();

    @Query("SELECT * FROM chats WHERE id = :id")
    Chat getChatById(int id);

    @Query("SELECT * FROM chats WHERE chatName LIKE '%' || :name || '%'")
    List<Chat> searchChats(String name);

    @Query("SELECT COUNT(*) FROM chats WHERE unreadCount > 0")
    int getTotalUnreadCount();

    @Query("UPDATE chats SET unreadCount = :count WHERE id = :chatId")
    void updateUnreadCount(int chatId, int count);

    @Query("DELETE FROM chats")
    void deleteAll();
}
