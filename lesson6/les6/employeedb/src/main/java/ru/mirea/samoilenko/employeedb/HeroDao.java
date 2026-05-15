package ru.mirea.samoilenko.employeedb;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HeroDao {

    @Query("SELECT * FROM hero")
    List<Hero> getAll();

    @Insert
    void insert(Hero hero);

    @Update
    void update(Hero hero);

    @Delete
    void delete(Hero hero);

    @Query("DELETE FROM hero")
    void deleteAll();
}