package com.example.nguyen_project3;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Character.class}, version = 1)
public abstract class CharacterDatabase extends RoomDatabase {
    public abstract CharacterDAO characterDAO();

}
