package com.example.nguyen_project3;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link EventOverviewFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EventOverviewFragment extends Fragment {

    // List to keep track of months
    ArrayList<MONTH> monthList = new ArrayList<MONTH>(Arrays.asList(MONTH.values()));
    int curMonth = 0;

    // Views
    Button leftButton, rightButton;
    TextView monthText;
    RecyclerView rv;

    // Other administrative stuff
    MainActivity main;
    EventAdapter eventAdapter;

    public EventOverviewFragment() {
        // Required empty public constructor
    }

    public static EventOverviewFragment newInstance(String param1, String param2) {
        EventOverviewFragment fragment = new EventOverviewFragment();
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
        return inflater.inflate(R.layout.fragment_event_overview, container, false);
    }

    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        // Update the text thing lol
        monthText = view.findViewById(R.id.monthNumber);
        monthText.setText(monthList.get(0).toString());

        // Make buttons do stuff
        leftButton = view.findViewById(R.id.leftButton);
        rightButton = view.findViewById(R.id.rightButton);

        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMonth(false);
            }
        });
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeMonth(true);
            }
        });

        // recycler stuff
        rv = view.findViewById(R.id.eventOverviewRV);
        rv.setLayoutManager(new LinearLayoutManager(main.getApplicationContext()));

        showEvents(main.eventDB.eventDAO()
                .getEventByMonth(monthList.get(curMonth).name()));

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
                    List<Event> newList;

                    if(newText.isBlank())
                    {
                        newList = main.eventDB.eventDAO().getEventByMonth(
                                monthList.get(curMonth).name());
                    } else {
                        newList = main.eventDB.eventDAO().searchEventsByMonth(
                                monthList.get(curMonth).name(), newText);
                        //Log.i("PROJ3", "Queried " + monthList.get(curMonth).name() + ", newText");
                    }

                    eventAdapter.updateList(newList);
                    return false;
                }
            });
        }
    }

    // Logic to change month
    private void changeMonth(boolean increase)
    {
        if(increase)
        {
            if(curMonth == 11) {
                curMonth = 0;
            } else {
                curMonth++;
            }
        } else {
            if(curMonth == 0) {
                curMonth = 11;
            } else {
                curMonth--;
            }
        }

        showEvents(main.eventDB.eventDAO()
                .getEventByMonth(monthList.get(curMonth).name()));
        monthText.setText(monthList.get(curMonth).toString());
    }

    // Creates a new adapter and shows the events
    private void showEvents(List<Event> events)
    {
        // New adapter thing
        eventAdapter = new EventAdapter(main, events);
        rv.setAdapter(eventAdapter);

        //Log.i("PROJ3", "Show Event Attempt");
        for(Event e : events)
        {
            //Log.i("PROJ3", e.name);
        }
    }

}