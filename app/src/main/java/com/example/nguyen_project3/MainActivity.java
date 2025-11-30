package com.example.nguyen_project3;

import static androidx.core.app.NotificationCompat.getAction;

import android.app.Notification;
import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionProvider;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.core.view.KeyEventDispatcher;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;

import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    // Code to create navigation drawer adapted from GeeksforGeeks
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private FragmentContainerView fragmentContainerView;

    // For searches
    SearchView searchView;
    RecyclerView curRV;
    MONTH curMonth = MONTH.JANUARY;

    // Keeps track of which screen is which
    private int state = 0;
    public int curCharacter;

    public CharacterDatabase characterDB;
    public List<Character> characterList;

    public EventDatabase eventDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.characterMain);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.navigation_view);

        // ActionBarDrawerToggle handles the drawer's open/close state
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.nav_open, R.string.nav_close);
        // Add the toggle as a listener to the DrawerLayout
        drawerLayout.addDrawerListener(toggle);
        // Synchronize the toggle's state with the linked DrawerLayout
        toggle.syncState();

        // Set a listener for when an item in the NavigationView is selected
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                // Handle the selected item based on its ID
                if (menuItem.getItemId() == R.id.characters) {
                    restoreCharacterView();
                    
                    //Toast.makeText(MainActivity.this, "Character List", Toast.LENGTH_SHORT).show();
                }

                if (menuItem.getItemId() == R.id.events) {
                    restoreEventView();
                    //Toast.makeText(MainActivity.this, "Events List", Toast.LENGTH_SHORT).show();
                }

                // Close the drawer after selection
                drawerLayout.closeDrawers();

                // Hide + clear the search bar
                // (Null error when i put it in the restoreView functions)
                searchView.setIconified(true);
                searchView.setQuery("", false);

                // Indicate that the item selection has been handled
                return true;
            }
        });


        // callback for pressing the back button
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // If drawer is open, close it. If not, close the app.
                // Maybe replace closing it with a "are u sure" dialog
                if(drawerLayout.isDrawerOpen(GravityCompat.START))
                {
                    drawerLayout.close();
                } else if(state == 1 || state == 2) {
                    restoreCharacterView();
                }
                else if(state == 0){
                    finish();
                }

                if(searchView != null) {
                    // Hide + clear the search bar
                    searchView.setIconified(true);
                    searchView.setQuery("", false);
                }
            }
        });

        // Setting fragment container view for fragment switching ease
        fragmentContainerView = findViewById(R.id.fragmentContainerView);

        // Handle databases
        characterDB = Room.databaseBuilder(getApplicationContext(), CharacterDatabase.class, "CharacterDatabase")
                .fallbackToDestructiveMigration(true).allowMainThreadQueries().build();
        eventDB = Room.databaseBuilder(getApplicationContext(), EventDatabase.class, "EventDatabase")
                .fallbackToDestructiveMigration(true).allowMainThreadQueries().build();

        //Character test = new Character("Test");
        //characterDB.characterDAO().addCharacter(test);

        characterList = characterDB.characterDAO().getAllCharacters();
        //Log.i("CHARACTER TEST", characterList.get(0).name);

    }

    // Delete character properly (get rid of all their events too)
    public void deleteCharacter(Character character)
    {
        List<Event> characterEvents = eventDB.eventDAO().getEventByCharacter(character.id);
        for(Event e : characterEvents)
        {
            eventDB.eventDAO().deleteEvent(e);
        }
    }

    // Updates activity to show character + their events
    public void viewCharacter(Character character)
    {
        curCharacter = character.id;
        state = 1;
        toolbar.setTitle(character.name);

        FragmentManager fm = getSupportFragmentManager();

        CharacterEventsFragment cef = new CharacterEventsFragment();
        Bundle args = new Bundle();
        args.putInt("characterID", character.id);
        cef.setArguments(args);

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(fragmentContainerView.getId(), cef);
        ft.commit();

        // Hide + clear the search bar
        if(searchView != null)
        {
            searchView.setIconified(true);
            searchView.setQuery("", false);
        }

    }

    // Restores homepage
    public void restoreCharacterView()
    {
        toolbar.setTitle("Characters");
        curCharacter = -1;

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(fragmentContainerView.getId(), new CharacterOverviewFragment());
        ft.commit();

    }

    public void restoreEventView()
    {
        toolbar.setTitle("Events");
        state = 2;
        curCharacter = -2;

        FragmentManager fm = getSupportFragmentManager();

        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(fragmentContainerView.getId(), new EventOverviewFragment());
        ft.commit();

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("characterID", curCharacter);
    }

    // Ensures that user stays on same fragment when rotating device
    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // curCharacter saved for character screens
        curCharacter = savedInstanceState.getInt("characterID");
        if(curCharacter == -2){
            //restoreEventView();
        } else if(curCharacter != -1 && characterDB.characterDAO().getCharacter(curCharacter)!= null)
            viewCharacter(characterDB.characterDAO().getCharacter(curCharacter));

        // TODO: event screen (curCharacter == -2?)
    }

    // To cheese adapter functions (could've been done with notifyDatasetChanged but I don't feel like it lol)
    public void onResume()
    {
        super.onResume();
        Log.i("LIFECYCLE", "Resumed");
        if(state == 2){
            restoreEventView();
        }else if(curCharacter != -1 && characterDB.characterDAO().getCharacter(curCharacter)!= null)
            viewCharacter(characterDB.characterDAO().getCharacter(curCharacter));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = this.getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        MenuItem searchViewItem = menu.findItem(R.id.app_bar_search);

        searchView = (SearchView) searchViewItem.getActionView();

        return super.onCreateOptionsMenu(menu);
    }

}