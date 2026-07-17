package com.omninet.data.repository;

import android.app.Application;
import com.omninet.data.db.ChatDao;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Chat;
import java.util.List;

public class ChatRepository {
    private ChatDao chatDao;
    private OmniDatabase database;

    public ChatRepository(Application application) {
        database = OmniDatabase.getInstance(application);
        chatDao = database.chatDao();
    }

    public void insert(Chat chat) {
        new Thread(() -> chatDao.insert(chat)).start();
    }

    public void update(Chat chat) {
        new Thread(() -> chatDao.update(chat)).start();
    }

    public void delete(Chat chat) {
        new Thread(() -> chatDao.delete(chat)).start();
    }

    public List<Chat> getAllChats() {
        return chatDao.getAllChats();
    }

    public Chat getChatById(int id) {
        return chatDao.getChatById(id);
    }

    public List<Chat> searchChats(String name) {
        return chatDao.searchChats(name);
    }

    public int getTotalUnreadCount() {
        return chatDao.getTotalUnreadCount();
    }

    public void updateUnreadCount(int chatId, int count) {
        new Thread(() -> chatDao.updateUnreadCount(chatId, count)).start();
    }
}
