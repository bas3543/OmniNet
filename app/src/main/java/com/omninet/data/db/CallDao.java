package com.omninet.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.omninet.data.models.Call;
import java.util.List;

@Dao
public interface CallDao {
    @Insert
    long insert(Call call);

    @Update
    void update(Call call);

    @Delete
    void delete(Call call);

    @Query("SELECT * FROM calls ORDER BY startTime DESC")
    List<Call> getAllCalls();

    @Query("SELECT * FROM calls WHERE callerId = :userId OR calleeId = :userId ORDER BY startTime DESC")
    List<Call> getCallsByUser(String userId);

    @Query("SELECT * FROM calls WHERE status = 'missed' AND (calleeId = :userId) ORDER BY startTime DESC")
    List<Call> getMissedCalls(String userId);

    @Query("SELECT * FROM calls WHERE callType = :type ORDER BY startTime DESC")
    List<Call> getCallsByType(String type);
}
