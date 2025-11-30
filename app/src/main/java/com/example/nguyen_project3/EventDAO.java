package com.example.nguyen_project3;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface EventDAO {
    @Insert
    void addEvent(Event event);

    @Update
    void updateEvent(Event event);

    @Delete
    void deleteEvent(Event event);

    @Query("SELECT * FROM Event Where id = :id")
    Event getEvent(int id);

    // Gets all events from a certain month
    @Query("SELECT * FROM Event Where Month = :month ORDER BY day")
    List<Event> getEventByMonth(String month);

    // Gets all events from a certain character
    @Query("SELECT * FROM Event Where characterID = :character ORDER BY COALESCE(month, day)")
    List<Event> getEventByCharacter(int character);

    // Search event from a certain character
    @Query("SELECT * FROM Event Where characterID = :character AND name LIKE '%' || :eventName || '%' ORDER BY COALESCE(month, day)")
    List<Event> searchEventsByCharacter(int character, String eventName);

    // Search event from a certain month
    @Query("SELECT * FROM Event Where Month = :month AND name LIKE '%' || :eventName || '%' ORDER BY COALESCE(month, day)")
    List<Event> searchEventsByMonth(String month, String eventName);



}
