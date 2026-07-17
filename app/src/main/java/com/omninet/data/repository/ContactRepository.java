package com.omninet.data.repository;

import android.app.Application;
import com.omninet.data.db.ContactDao;
import com.omninet.data.db.OmniDatabase;
import com.omninet.data.models.Contact;
import java.util.List;

public class ContactRepository {
    private ContactDao contactDao;
    private OmniDatabase database;

    public ContactRepository(Application application) {
        database = OmniDatabase.getInstance(application);
        contactDao = database.contactDao();
    }

    public void insert(Contact contact) {
        new Thread(() -> contactDao.insert(contact)).start();
    }

    public void update(Contact contact) {
        new Thread(() -> contactDao.update(contact)).start();
    }

    public void delete(Contact contact) {
        new Thread(() -> contactDao.delete(contact)).start();
    }

    public List<Contact> getAllContacts() {
        return contactDao.getAllContacts();
    }

    public Contact getContactById(int id) {
        return contactDao.getContactById(id);
    }

    public Contact getContactByPhone(String phoneNumber) {
        return contactDao.getContactByPhone(phoneNumber);
    }

    public List<Contact> searchContacts(String name) {
        return contactDao.searchContacts(name);
    }

    public List<Contact> getOnlineContacts() {
        return contactDao.getOnlineContacts();
    }
}
