package com.omninet.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.omninet.data.models.Contact;
import java.util.List;

@Dao
public interface ContactDao {
    @Insert
    long insert(Contact contact);

    @Update
    void update(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("SELECT * FROM contacts ORDER BY displayName ASC")
    List<Contact> getAllContacts();

    @Query("SELECT * FROM contacts WHERE id = :id")
    Contact getContactById(int id);

    @Query("SELECT * FROM contacts WHERE phoneNumber = :phoneNumber")
    Contact getContactByPhone(String phoneNumber);

    @Query("SELECT * FROM contacts WHERE displayName LIKE '%' || :name || '%'")
    List<Contact> searchContacts(String name);

    @Query("SELECT * FROM contacts WHERE isOnline = 1 ORDER BY lastSeen DESC")
    List<Contact> getOnlineContacts();

    @Query("DELETE FROM contacts")
    void deleteAll();
}
