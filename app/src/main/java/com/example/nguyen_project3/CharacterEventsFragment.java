package com.example.nguyen_project3;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CharacterEventsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CharacterEventsFragment extends Fragment {

    // Keep tracking of ints and stuff
    private int characterID;

    // Meta stuff ig
    private MainActivity main;
    EventAdapter adapter;

    public CharacterEventsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static CharacterEventsFragment newInstance(int characterID) {
        CharacterEventsFragment fragment = new CharacterEventsFragment();
        Bundle args = new Bundle();
        args.putInt("characterID", characterID);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            characterID = getArguments().getInt("characterID");
        }

        main = (MainActivity) getContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_character_events, container, false);
    }

    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        List<Event> eventList = main.eventDB.eventDAO().getEventByCharacter(characterID);

        RecyclerView rv = view.findViewById(R.id.characterEventsRV);
        rv.setLayoutManager(new LinearLayoutManager(main));
        adapter = new EventAdapter(getContext(), eventList);
        rv.setAdapter(adapter);

        FloatingActionButton newEvent = view.findViewById(R.id.newEventButton);

        newEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: set up a proper create event thing
                Intent eventActivity = new Intent(getContext(), EventActivity.class);

                // Add extras here
                eventActivity.putExtra("characterID", characterID);

                main.startActivity(eventActivity);
                // Leaving the test events I made here for fun
                /*
                if(eventList.isEmpty())
                {
                    Event iceCream = new Event(characterID, MONTH.JANUARY, 1,
                            TIME.EARLY_MORNING, "Ice Cream Social", "Frederic, Helena, Astrid",
                            "Fun in the sun plus some ice cream!");
                    main.eventDB.eventDAO().addEvent(iceCream);
                    eventList.add(iceCream);
                    adapter.notifyItemInserted(eventList.size() - 1);
                } else if (eventList.size() == 1)
                {
                    Event jellyfish = new Event(characterID, MONTH.JANUARY, 13,
                            TIME.EARLY_MORNING, "Aquarium Date", "Koibito",
                            "I LOVE JELLYFISH!!");
                    main.eventDB.eventDAO().addEvent(jellyfish);
                    eventList.add(jellyfish);
                    adapter.notifyItemInserted(eventList.size() - 1);
                }
                 */


            }
        });

        if(eventList.isEmpty()){
            // TODO: Make a new toast instead of whatever this is lol
            Toast noEventNotif = Toast.makeText(view.getContext(), "Character has no events, make one!", Toast.LENGTH_SHORT);
            noEventNotif.setGravity(Gravity.START, 20, 20);
            noEventNotif.show();
        }
        /*
        for(Event e : eventList)
        {
            Log.i("PROJ3", e.name);
        }
         */

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
                    List<Event> newList;

                    if(newText.isBlank())
                    {
                        newList = main.eventDB.eventDAO().getEventByCharacter(characterID);
                    } else {
                        newList = main.eventDB.eventDAO().searchEventsByCharacter(characterID,
                                newText);
                    }

                    adapter.updateList(newList);
                    return false;
                }
            });
        }
    }


}