package com.omninet.data.repository;

import android.app.Application;
import com.omninet.data.db.MessageDao;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Message;
import java.util.List;

public class MessageRepository {
    private MessageDao messageDao;
    private OmniDatabase database;

    public MessageRepository(Application application) {
        database = OmniDatabase.getInstance(application);
        messageDao = database.messageDao();
    }

    public void insert(Message message) {
        new Thread(() -> messageDao.insert(message)).start();
    }

    public void update(Message message) {
        new Thread(() -> messageDao.update(message)).start();
    }

    public void delete(Message message) {
        new Thread(() -> messageDao.delete(message)).start();
    }

    public List<Message> getMessagesByChat(int chatId) {
        return messageDao.getMessagesByChat(chatId);
    }

    public Message getLastMessage(int chatId) {
        return messageDao.getLastMessage(chatId);
    }

    public int getUnreadCount(int chatId) {
        return messageDao.getUnreadCount(chatId);
    }

    public void markChatAsRead(int chatId) {
        new Thread(() -> messageDao.markChatAsRead(chatId)).start();
    }

    public List<Message> searchMessages(int chatId, String query) {
        return messageDao.searchMessages(chatId, query);
    }
}
