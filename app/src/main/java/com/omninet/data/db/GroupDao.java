package com.omninet.data.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import com.omninet.data.models.Group;
import java.util.List;

@Dao
public interface GroupDao {
    @Insert
    long insert(Group group);

    @Update
    void update(Group group);

    @Delete
    void delete(Group group);

    @Query("SELECT * FROM groups ORDER BY createdAt DESC")
    List<Group> getAllGroups();

    @Query("SELECT * FROM groups WHERE id = :id")
    Group getGroupById(int id);

    @Query("SELECT * FROM groups WHERE groupName LIKE '%' || :name || '%'")
    List<Group> searchGroups(String name);

    @Query("SELECT * FROM groups WHERE groupAdmin = :adminId")
    List<Group> getGroupsByAdmin(String adminId);
}
