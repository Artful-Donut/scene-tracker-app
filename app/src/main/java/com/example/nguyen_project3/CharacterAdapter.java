package com.example.nguyen_project3;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CharacterAdapter extends RecyclerView.Adapter<CharacterAdapter.MyHandler> {

    public static class MyHandler extends RecyclerView.ViewHolder {
        EditText characterName;
        ImageButton editCharacterButton;
        ImageButton deleteCharacterButton;

        // Handler Constructor
        public MyHandler(@NonNull View itemView) {
            super(itemView);

            characterName = itemView.findViewById(R.id.characterName);
            editCharacterButton = itemView.findViewById(R.id.editCharacterButton);
            deleteCharacterButton = itemView.findViewById(R.id.deleteCharacterButton);
        }

        // Getters
        public EditText getCharacterName()
        {
            return characterName;
        }
        public ImageButton getEditCharacterButton()
        {
            return editCharacterButton;
        }
        public ImageButton getDeleteCharacterButton()
        {
            return deleteCharacterButton;
        }
    }

    // Class-specific variables + Adapter-specific
    List<Character> characterList;
    Context context;
    public CharacterAdapter(Context context, List<Character> characters)
    {
        this.characterList = characters;
        this.context = context;
    }

    // Handles creating the viewholders (character_layout.xml in this case)
    @NonNull
    @Override
    public MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.character_layout, parent,false);

        return new MyHandler(inflatedLayout);
    }

    // Called when viewholders are binded to the list items
    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, @SuppressLint("RecyclerView") int position) {
        // Initialize character
        Character character = characterList.get(position);

        // Set text and don't allow typing
        EditText characterName = holder.getCharacterName();
        characterName.setText(character.name);
        characterName.setKeyListener(null);

        // Open character events fragment when clicked
        characterName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(v.getContext(), "TODO: VIEW CHARACTER", Toast.LENGTH_SHORT).show();
                MainActivity main = (MainActivity) v.getContext();

                main.viewCharacter(character);
            }
        });

        // Edit character when edit button is pressed
        Drawable originalImage = holder.getEditCharacterButton().getBackground(); // Save original image

        //Log.i("PROJ3", originalImage.toString());
        holder.getEditCharacterButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(characterName.getKeyListener() != null)  // If editable
                {
                    // TODO: There's a bug where sometimes the checkmark never goes away and the program is generally buggy
                    // not sure how to replicate it tho.
                    Log.i("PROJ3", "Uneditable Now");
                    // hide delete button
                    holder.getDeleteCharacterButton().setVisibility(View.GONE);

                    // Get context of view
                    // change character name, update it in the DAO
                    MainActivity main = (MainActivity) v.getContext();
                    character.name = characterName.getText().toString();
                    main.characterDB.characterDAO().updateCharacter(character);

                    // Restore button image
                    holder.getEditCharacterButton().setBackground(originalImage);

                    // Make text not editable again
                    characterName.setKeyListener(null);
                    characterName.setClickable(true);
                    characterName.setBackgroundColor(v.getContext().getColor(R.color.softBlush));
                    characterName.setTextColor(v.getContext().getColor(R.color.dustyGrape));

                    notifyItemMoved(holder.getBindingAdapterPosition(), 0);


                } else {    // if not editable
                    Log.i("PROJ3", "Editable Now");
                    //Toast.makeText(v.getContext(), "TODO: EDIT CHARACTER", Toast.LENGTH_SHORT).show();
                    // disallow clicking, but allow typing + change appearances to indicate editability
                    // show delete button
                    holder.getDeleteCharacterButton().setVisibility(View.VISIBLE);

                    characterName.setClickable(false);
                    characterName.setKeyListener(new TextKeyListener(TextKeyListener.Capitalize.WORDS, true));
                    characterName.setBackgroundColor(v.getContext().getColor(R.color.lavenderVeil));
                    characterName.setTextColor(v.getContext().getColor(R.color.brightLavender));

                    holder.getEditCharacterButton()
                            .setBackground(AppCompatResources.getDrawable(v.getContext(), R.drawable.ic_checkmark));
                }

            }
        });

        // Delete character when delete button is pressed
        holder.getDeleteCharacterButton().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setMessage("You will not be able to get it back!");
                builder.setTitle("DELETE Character");

                // Adapted from geeks for geeks

                // Set the positive button with yes name Lambda
                // OnClickListener method is use of DialogInterface interface.
                builder.setPositiveButton("Yes", (DialogInterface.OnClickListener) (dialog, which) -> {
                    // Grab the main activity and delete the character, also making sure to notify the adapter
                    MainActivity main = (MainActivity) v.getContext();
                    // (don't forget to delete associated events as well)
                    // Note: If I properly set up the eventDB before then I could've just done it thru the character db but nah,, nah,,,
                    // TODO: Fix bug where bottom layer multiplies when character is deleted
                    // Fix: I didn't edit the character list lol
                    // Log.i("PROJ3", "position before removing is: "+ holder.getBindingAdapterPosition() + "Size of itemcount is: " + getItemCount());
                    main.characterDB.characterDAO().deleteCharacter(character);
                    main.deleteCharacter(character);
                    characterList.remove(position);

                    // Log.i("PROJ3", "position after removing is: "+ holder.getBindingAdapterPosition() + "Size of itemcount is: " + getItemCount());
                    notifyItemRemoved(holder.getBindingAdapterPosition());
                    // Log.i("PROJ3", "position after notify removing is: "+ holder.getBindingAdapterPosition() + "Size of itemcount is: " + getItemCount());

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


    @Override
    public int getItemCount() {
        return characterList.size();
    }

    public void updateList(List<Character> newList)
    {
        characterList = newList;
        notifyDataSetChanged();
    }


}
