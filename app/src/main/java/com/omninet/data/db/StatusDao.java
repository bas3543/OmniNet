package com.omninet.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.omninet.data.models.Status;
import java.util.List;

@Dao
public interface StatusDao {
    @Insert
    long insert(Status status);

    @Update
    void update(Status status);

    @Delete
    void delete(Status status);

    @Query("SELECT * FROM statuses WHERE expiresAt > :currentTime ORDER BY createdAt DESC")
    List<Status> getValidStatuses(long currentTime);

    @Query("SELECT * FROM statuses WHERE userId = :userId ORDER BY createdAt DESC")
    List<Status> getStatusesByUser(String userId);

    @Query("DELETE FROM statuses WHERE expiresAt < :currentTime")
    void deleteExpiredStatuses(long currentTime);
}
