package com.example.nguyen_project3;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.MonthDay;
import java.util.List;

@Entity
public class Event {

    @PrimaryKey(autoGenerate = true)
    int id;

    int characterID;
    int day;
    String name, characters, synopsis;
    MONTH month;
    TIME time;
    boolean completed = false;

    // TODO: make it possible to change associated character as well?
    public Event(int characterID, MONTH month, int day, TIME time, String name, String characters, String synopsis) {
        this.characterID = characterID;
        this.month = month;
        this.day = day;
        this.time = time;
        this.name = name;
        this.characters = characters;
        this.synopsis = synopsis;
    }

    public void setMonth(MONTH month)
    {
        this.month = month;
    }
    public void setDay(int day)
    {
        this.day = day;
    }
    public void setTime(TIME time)
    {
        this.time = time;
    }
    public void setName(String name)
    {
        this.name = name;
    }
    public void setCharacters(String characters)
    {
        this.characters = characters;
    }
    public void setSynopsis(String synopsis)
    {
        this.synopsis = synopsis;
    }
    public void setCompleted(boolean complete)
    {
        completed = complete;
    }

}
