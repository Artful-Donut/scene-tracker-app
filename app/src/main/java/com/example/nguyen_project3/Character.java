package com.example.nguyen_project3;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.List;

@Entity
public class Character {
    // automatically assigns a number to the id
    @PrimaryKey(autoGenerate = true)
    int id;
    String name;
    public Character(String name)
    {
        this.name = name;
    }

    // Ensures that dependent events are deleted
    public void deleteCharacter(EventDatabase eventDB)
    {
        List<Event> characterEvents = eventDB.eventDAO().getEventByCharacter(id);
        for(Event e : characterEvents)
        {
            eventDB.eventDAO().deleteEvent(e);
        }
    }
}
