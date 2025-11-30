package com.example.nguyen_project3;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface CharacterDAO {

    @Insert
    void addCharacter(Character c);

    @Update
    void updateCharacter(Character c);

    @Delete
    void deleteCharacter(Character c);

    @Query("SELECT * FROM Character Where id = :id")
    Character getCharacter(int id);

    // Gets all characters
    @Query("SELECT * FROM Character ORDER BY name")
    List<Character> getAllCharacters();

    // Finds character from a search
    // TODO: might be wrong
    @Query("SELECT * FROM Character WHERE name LIKE '%' || :search || '%' ORDER BY name")
    List<Character> findCharacters(String search);



}
