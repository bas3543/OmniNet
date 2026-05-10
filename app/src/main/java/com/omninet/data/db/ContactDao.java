package com.omninet.data.db;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import com.omninet.data.models.Contact;
import java.util.List;

@Dao
public interface ContactDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Contact contact);

    @Update
    void update(Contact contact);

    @Delete
    void delete(Contact contact);

    @Query("SELECT * FROM contacts ORDER BY lastSeen DESC")
    LiveData<List<Contact>> getAll();

    @Query("SELECT * FROM contacts ORDER BY lastSeen DESC")
    List<Contact> getAllSync();

    @Query("SELECT * FROM contacts WHERE omniNumber = :number LIMIT 1")
    Contact getByNumber(String number);

    @Query("SELECT * FROM contacts WHERE isOnline = 1 ORDER BY lastSeen DESC")
    LiveData<List<Contact>> getOnline();

    @Query("UPDATE contacts SET isOnline = :online, lastSeen = :time WHERE omniNumber = :number")
    void updateOnlineStatus(String number, boolean online, long time);

    @Query("UPDATE contacts SET hopDistance = :hops WHERE omniNumber = :number")
    void updateHopDistance(String number, int hops);

    @Query("SELECT COUNT(*) FROM contacts")
    int getCount();

    @Query("DELETE FROM contacts")
    void deleteAll();
}
