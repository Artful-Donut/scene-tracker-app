package com.example.nguyen_project3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Comparator;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterOverviewFragment extends Fragment {

    private MainActivity main;
    private String newCharacterName;

    public CharacterOverviewFragment() {
        // Required empty public constructor
    }

    public static CharacterOverviewFragment newInstance() {
        CharacterOverviewFragment fragment = new CharacterOverviewFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        main = (MainActivity) getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_overview, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        RecyclerView rv = view.findViewById(R.id.recyclerView);
        rv.setLayoutManager(new LinearLayoutManager(main.getApplicationContext()));
        CharacterAdapter adapter = new CharacterAdapter(getContext(), main.characterList);
        rv.setAdapter(adapter);

        // Set the recycler view for main
        main.curRV = rv;

        // Dialog + Edit text adapted from https://stackoverflow.com/questions/10903754/input-text-dialog-android
        FloatingActionButton fab = view.findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Enter New Character Name:");

                View inflatedEditText = LayoutInflater.from(getContext())
                        .inflate(R.layout.new_character_dialog, (ViewGroup) getView(), false);

                // Set up the input
                final EditText input = inflatedEditText.findViewById(R.id.newCharacterNameInput);
                builder.setView(inflatedEditText);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        newCharacterName = input.getText().toString();
                        if(newCharacterName.isBlank())
                        {
                            Toast.makeText(getContext(), "Please input a name!", Toast.LENGTH_LONG).show();
                            dialog.cancel();
                        } else {
                            // TODO: Implement the recycler view changing real-time
                            Character newChar = new Character(newCharacterName);
                            Log.i("PROJ3", newChar.name + "Createdwith ID " + newChar.id);
                            main.characterList.add(0, newChar);
                            main.characterDB.characterDAO().addCharacter(newChar);

                            // Tell adapter to change and rv to scroll up
                            adapter.notifyItemInserted(0);
                            rv.scrollToPosition(0);
                            // adapter.notifyItemRangeChanged(0, adapter.getItemCount());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });

        // TODO: Will not work when activity first runs but will work afterwards
        // not sure what to do about this. I think I need an async task but idk

        // set up search thing too
        if(main.searchView != null)
        {
            main.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    main.searchView.clearFocus();
                    //Log.i("PROJ3", "queery text listener set");
                    return false;
                }
                @Override
                public boolean onQueryTextChange(String newText) {
                    // Log.i("PROJ3", main.searchView.getQuery().toString());
                    List<Character> newList;

                    if(newText.isBlank())
                    {
                        newList = main.characterDB.characterDAO().getAllCharacters();
                    } else {
                        newList = main.characterDB.characterDAO().findCharacters(newText);
                    }

                    adapter.updateList(newList);
                    return false;
                }
            });
        }
    }
}