package com.example.nguyen_project3;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.method.TextKeyListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.MyHandler> {

    public static class MyHandler extends RecyclerView.ViewHolder {
        // Handler variables, constructor, and getters
        TextView eventName, monthText, dayText, timeText;
        RelativeLayout eventParent;

        public MyHandler(@NonNull View itemView) {
            super(itemView);

            eventName = itemView.findViewById(R.id.eventName);
            monthText = itemView.findViewById(R.id.monthText);
            dayText = itemView.findViewById(R.id.dayText);
            timeText = itemView.findViewById(R.id.timeText);
            eventParent = itemView.findViewById(R.id.eventParent);
        }

        public TextView getEventName()
        {
            return eventName;
        }
        public TextView getMonthText()
        {
            return monthText;
        }
        public TextView getDayText()
        {
            return dayText;
        }
        public TextView getTimeText()
        {
            return timeText;
        }

        public RelativeLayout getEventParent()
        {
            return eventParent;
        }
    }

    // Get context (for grabbing resources and stuff) and event list
    List<Event> eventList;
    Context context;

    public EventAdapter(Context context, List<Event> events)
    {
        this.eventList = events;
        this.context = context;
    }

    @NonNull
    @Override
    public MyHandler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View inflatedLayout = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.event_layout, parent,false);

        return new MyHandler(inflatedLayout);
    }

    @Override
    public void onBindViewHolder(@NonNull MyHandler holder, int position) {
        Event event = eventList.get(position);  // Grab event


        // Go to event activity when any part of the event layout is clicked on
        // TODO: that part is broken rn
        holder.getEventName().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(context, "TODO: FINISH EVENT VIEW SCREEN", Toast.LENGTH_SHORT).show();
                //Log.i("PROJ3", event.id + " clicked.");
                // Create intent
                Intent eventActivity = new Intent(context, EventActivity.class);

                // Add extras here
                eventActivity.putExtra("eventID", event.id);
                eventActivity.putExtra("characterID", event.characterID);

                context.startActivity(eventActivity);
            }
        });

        // Had a null pointer error earlier, turns out I used the wrong layout by accident lol
        // Keeping the try catch because it'd be a shame to take it out
        // Also I'm very proud of my enums :3, they'll probably be useless eventually
        // but for my prototype this is pretty awesome
        try // Set text for the event view
        {
            holder.getEventName().setText(event.name);
            holder.getEventName().setKeyListener(null);

            holder.getMonthText().setText(event.month.toString().substring(0, 3));
            holder.getMonthText().setKeyListener(null);

            holder.getDayText().setText(context.getString(R.string.numString, event.day));
            holder.getDayText().setKeyListener(null);

            holder.getTimeText().setText(event.time.toString().substring(0,3));
            holder.getTimeText().setKeyListener(null);
        } catch (NullPointerException e)
        {
            //Log.i("PROJ3", "ONE OF THE TEXT VIEWS ARE NULL");
        }


        if(event.completed) // make the event a different colour if it's completed :3
        {
            holder.getEventName().setBackgroundColor(context.getColor(R.color.softPeriwinkle));
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    // For search functions (adapted from https://stackoverflow.com/questions/40754174/android-implementing-search-filter-to-a-recyclerview)
    public void updateList(List<Event> list){
        eventList = list;
        notifyDataSetChanged();
    }

}
