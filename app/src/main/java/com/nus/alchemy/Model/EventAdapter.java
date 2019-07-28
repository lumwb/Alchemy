package com.nus.alchemy.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nus.alchemy.GroupChatActivity;
import com.nus.alchemy.R;

import java.util.List;

//actually the eventAdapter for today's events only
public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventObject> eventObjectList;
    protected Context context;
    private String userID;

    public EventAdapter(List<EventObject> eventList) {
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.eventObjectList = eventList;
    }

    @Override
    public int getItemCount() {
        return eventObjectList.size();
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        EventObject currentEvent = eventObjectList.get(i);

        //pass event into ViewHolder for manipulation
        eventViewHolder.setEvent(currentEvent);

        //simple time manipulation
        String totalTime = currentEvent.getStartTime();
        String hourTime = totalTime.substring(0,2);
        String minuteTime = totalTime.substring(3);

        //bindtoViewHolder
        eventViewHolder.vEventDescription.setText(currentEvent.getEventTitle());
        eventViewHolder.vHostName.setText(currentEvent.getCreatorName());
        eventViewHolder.vTxtHour.setText(hourTime);
        eventViewHolder.vTxtMinute.setText(minuteTime);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.event_card, viewGroup, false);

        return new EventViewHolder(itemView);
    }

    public class EventViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        protected TextView vEventDescription;
        protected TextView vTxtHour;
        protected TextView vTxtMinute;
        protected TextView vHostName;
        protected EventObject currentEvent;


        public EventViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            vEventDescription =  (TextView) v.findViewById(R.id.txtEventName);
            vTxtHour =  (TextView) v.findViewById(R.id.txtHour);
            vTxtMinute =  (TextView) v.findViewById(R.id.txtMinute);
            vHostName =  (TextView) v.findViewById(R.id.txtHostName);
        }

        @Override
        public void onClick(View view) {
            //check if can enter the event
            boolean isActiveChat = currentEvent.getActive();
            String chatID = currentEvent.getChatID();
            String hostID = currentEvent.getCreatorUserID();

            if (isActiveChat) {
                //get groupChat
                DatabaseReference groupChat = FirebaseDatabase.getInstance().getReference()
                        .child("Groups").child(chatID);

                //add current user to participant list
                groupChat.child("Participants").child(userID).setValue(true);

                //Set groupHost and currentGroupName and redirect
                Intent intent = new Intent(context, GroupChatActivity.class);
                intent.putExtra("groupHost", hostID);
                intent.putExtra("currentGroupName", chatID);
                intent.putExtra("eventName", currentEvent.getEventTitle());
                intent.putExtra("eventID", currentEvent.getEventID());
                context.startActivity(intent);
                ((Activity)context).finish();
            } else {
                //chat not avail just toast
                Toast toast = Toast.makeText(context, " Chat Room Not Available.", Toast.LENGTH_SHORT);
                TextView v = (TextView) toast.getView().findViewById(android.R.id.message);
                if( v != null) v.setGravity(Gravity.CENTER);
                toast.show();
            }
        }

        public void setEvent(EventObject currentEvent) {
            this.currentEvent = currentEvent;
        }
    }
}
