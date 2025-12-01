package com.example.nguyen_project3;

import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.icu.text.StringPrepParseException;
import android.nfc.FormatException;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class EventActivity extends AppCompatActivity {
    // Inner Class to filter out bad values for the edit text
    // Adapted from GeeksforGeeks + stackoverflow
    // TODO: Implement. Might save this for later tho im so tired
    class DateFilter implements InputFilter {
        MONTH month;
        int max;
        DateFilter(MONTH month)
        {
            this.month = month;
            if(month == MONTH.JANUARY);
        }

        // https://stackoverflow.com/questions/35496841/set-edittext-filter-to-numbers-within-a-custom-range
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            try {
                String replacement = source.subSequence(start, end).toString();

                String newVal = dest.toString().substring(0, dstart) + replacement +dest.toString().substring(dend);

                int input = Integer.parseInt(newVal);

                if (input<=max) {
                    return null;
                }

            } catch(NumberFormatException e) {
                e.printStackTrace();
            }
            return "";
        }
    }

    // For populating the fields (if necessary)
    private int eventID;
    private int characterID;
    private boolean editMode;

    // Views
    Toolbar toolbar;
    ImageButton deleteEventButton;
    EditText eventNameInput, dayInput, charactersInput, synopsisInput;
    FloatingActionButton editEventButton;
    Spinner monthSpinner, timeSpinner;
    CheckBox finishedCheckBox;

    // Database stuff
    EventDatabase eventDB;
    CharacterDatabase characterDB;

    Event event;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_event);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.eventNameInput), (v, insets) -> {
            // Set the inset to "ime" for text applications apparently
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.ime());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        eventID = getIntent().getIntExtra("eventID", -1);
        characterID = getIntent().getIntExtra("characterID", 0);


        toolbar = findViewById(R.id.eventToolbar);

        // Logic for the back button (easy)
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // setting up the views
        eventNameInput = findViewById(R.id.eventNameInput);
        monthSpinner = findViewById(R.id.monthSpinner);
        dayInput = findViewById(R.id.dayInput);
        timeSpinner = findViewById(R.id.timeSpinner);
        charactersInput = findViewById(R.id.charactersInput);
        synopsisInput = findViewById(R.id.synopsisInput);
        finishedCheckBox = findViewById(R.id.finishedCheckBox);

        deleteEventButton = findViewById(R.id.deleteEventButton);

        // Realtime checks to ensure dayInput is valid

        // Create Database to use
        characterDB = Room.databaseBuilder(getApplicationContext(), CharacterDatabase.class, "CharacterDatabase")
                .fallbackToDestructiveMigration(true).allowMainThreadQueries().build();
        eventDB = Room.databaseBuilder(getApplicationContext(), EventDatabase.class, "EventDatabase")
                .fallbackToDestructiveMigration(true).allowMainThreadQueries().build();

        // Stuff with the edit button
        editEventButton = findViewById(R.id.editEventButton);
        Drawable drawIcon = editEventButton.getDrawable();  // HOW DO U GET THE FREE DRAWABLES IM SCREAMING
        Drawable doneIcon = AppCompatResources.getDrawable(this, R.drawable.ic_checkmark);

        editEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editMode)    // If edit mode
                {
                    // Set title to Character's Event (for new Event case)
                    toolbar.setTitle(characterDB.characterDAO().
                            getCharacter(characterID).name + "'s Event");

                    String newName;
                    MONTH newMonth;
                    int newDay;
                    TIME newTime;
                    String newCharacters;
                    String newSynopsis;
                    boolean completed;

                    // Error handling for cheeky users
                    // Ensure the fields aren't null/empty
                    try
                    {
                        // GRAB EVERYTHING (plus extra stuff for silly enum things)
                        newName = eventNameInput.getText().toString();
                        newMonth = MONTH.values()[monthSpinner.getSelectedItemPosition()];
                        newDay = Integer.parseInt(dayInput.getText().toString());
                        newTime = TIME.values()[timeSpinner.getSelectedItemPosition()];
                        newCharacters = charactersInput.getText().toString();
                        newSynopsis = synopsisInput.getText().toString();
                        completed = finishedCheckBox.isChecked();

                        // Throw exception if anything is blank
                        if(newName.isBlank() || newDay == 0 || newCharacters.isBlank() || newSynopsis.isBlank()) throw new FormatException("Don't be Lazy :3");

                        // Throw exception if date is invalid
                        if (newMonth == MONTH.FEBRUARY){
                            if(newDay < 0 || newDay > 28) throw new NumberFormatException();
                        } else if(newMonth == MONTH.APRIL || newMonth == MONTH.JUNE || newMonth == MONTH.SEPTEMBER || newMonth == MONTH.NOVEMBER)
                        {
                            if(newDay < 0 || newDay > 30) throw new NumberFormatException();
                        } else {
                            if(newDay < 0 || newDay > 31) throw new NumberFormatException();
                        }

                    } catch (FormatException e) {
                        Toast.makeText(EventActivity.this, "Error! Please input all fields and try again!", Toast.LENGTH_LONG).show();
                        return;
                    } catch (NumberFormatException e) {
                        Toast.makeText(EventActivity.this, "Error! Please input a valid date!", Toast.LENGTH_LONG).show();
                        return;
                    }



                    // Update event if it's been changed
                    if(eventID != -1)
                    {
                        event.name = newName;
                        event.month = newMonth;
                        // TODO: Make sure this isn't fucking things up. I'm sure it's not tho
                        // also TODO: Failsafes for bad numbers (Note: this is time consuming)

                        Log.i("PROJ3", "Editing event");
                        event.day = newDay;

                        event.time = newTime;
                        event.characters = newCharacters;
                        event.synopsis = newSynopsis;
                        event.completed = completed;

                        eventDB.eventDAO().updateEvent(event);
                    } else {
                        // Create new event
                        Log.i("PROJ3", "Created new event");
                        Event newEvent = new Event(characterID, newMonth, newDay, newTime, newName, newCharacters, newSynopsis);
                        // Add to the database and update the activity's event id
                        eventDB.eventDAO().addEvent(newEvent);
                        eventID = newEvent.id;
                        event = newEvent; // wait did i just forget to put this in? lol

                        // And a toast :3, for fun
                        Toast.makeText(EventActivity.this, "Event successfully created!", Toast.LENGTH_SHORT).show();
                    }

                    setToViewMode(event);
                    // Show draw Icon
                    editEventButton.setImageDrawable(drawIcon);
                }
                else
                {
                    editEventButton.setImageDrawable(doneIcon);
                    setToEditMode();
                }

                editMode = !editMode;
            }
        });

        // Handles coming from a preexisting event or make new event fragment
        if(eventID != -1)   // Preexisting Event
        {
            // Set event
            event = eventDB.eventDAO().getEvent(eventID);

            // Set toolbar title
            toolbar.setTitle(characterDB.characterDAO().
                    getCharacter(characterID).name + "'s Event");

            // Don't let people edit it
            setToViewMode(event);
            editMode = false;
            //Log.i("PROJ3", eventID + "");
        } else {    // New Event
            // Set toolbar title and the floating action bar to done
            toolbar.setTitle("New Event");
            editEventButton.setImageDrawable(doneIcon);

            // ALlow user to edit, but don't let them delete
            editMode = true;
            setToEditMode();
            deleteEventButton.setVisibility(View.GONE);
            finishedCheckBox.setClickable(false);
        }

        // Stuff with the delete button
        deleteEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(EventActivity.this);
                builder.setMessage("You will not be able to get it back!");
                builder.setTitle("DELETE EVENT");

                // Adapted from geeks for geeks

                // Set the positive button with yes name Lambda
                // OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {

                    // When the user click yes button then app will close
                    eventDB.eventDAO().deleteEvent(event);
                    finish();
                });

                // Set the Negative button with No name Lambda
                // OnClickListener method is use of DialogInterface interface.
                builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {

                    // If user click no then dialog box is canceled.
                    dialog.cancel();
                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();

                // Show the Alert Dialog box
                alertDialog.show();
            }
        });
    }

    // Allows user to edit
    private void setToEditMode()
    {
        // Enable all edit fields
        eventNameInput.setEnabled(true);
        monthSpinner.setEnabled(true);
        dayInput.setEnabled(true);
        timeSpinner.setEnabled(true);
        charactersInput.setEnabled(true);
        synopsisInput.setEnabled(true);
        finishedCheckBox.setClickable(true);

        // set edit field text colors
        eventNameInput.setTextColor(getResources().getColor(R.color.brightLavender, getTheme()));
        dayInput.setTextColor(getResources().getColor(R.color.brightLavender, getTheme()));
        eventNameInput.setTextColor(getResources().getColor(R.color.brightLavender, getTheme()));
        charactersInput.setTextColor(getResources().getColor(R.color.brightLavender, getTheme()));
        synopsisInput.setTextColor(getResources().getColor(R.color.brightLavender, getTheme()));

        // Show Delete Button
        deleteEventButton.setVisibility(View.VISIBLE);

    }

    private void setToViewMode(Event event)
    {
        // Set fields
        eventNameInput.setText(event.name);
        monthSpinner.setSelection(event.month.ordinal());
        dayInput.setText(getResources().getString(R.string.numString, event.day));
        timeSpinner.setSelection(event.time.ordinal());
        charactersInput.setText(event.characters);
        synopsisInput.setText(event.synopsis);
        finishedCheckBox.setChecked(event.completed);

        // Disable all edit fields
        eventNameInput.setEnabled(false);
        monthSpinner.setEnabled(false);
        dayInput.setEnabled(false);
        timeSpinner.setEnabled(false);
        charactersInput.setEnabled(false);
        synopsisInput.setEnabled(false);
        finishedCheckBox.setClickable(false);

        // set edit field text colors
        eventNameInput.setTextColor(getResources().getColor(R.color.dustyGrape, getTheme()));
        dayInput.setTextColor(getResources().getColor(R.color.dustyGrape, getTheme()));
        eventNameInput.setTextColor(getResources().getColor(R.color.dustyGrape, getTheme()));
        charactersInput.setTextColor(getResources().getColor(R.color.dustyGrape, getTheme()));
        synopsisInput.setTextColor(getResources().getColor(R.color.dustyGrape, getTheme()));

        // Hide Delete Button
        deleteEventButton.setVisibility(View.GONE);

    }
}