package com.nus.alchemy.Model;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nus.alchemy.GroupChatActivity;
import com.nus.alchemy.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.MyEventViewHolder> {

    //custom interface to move onclick logic into activity
    public interface OnItemSelectedListener {

        void onSelected(EventObject object);

        void onMenuAction(EventObject object, MenuItem item);
    }

    private List<EventObject> myEventObjectList;
    private OnItemSelectedListener listener;
    private String userID;
    private String todayDate;
    protected Context context;

    public MyEventAdapter(List<EventObject> eventList) {
        this.myEventObjectList = eventList;
        this.userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.todayDate = dateFormat.format(new Date());
    }


    @Override
    public int getItemCount() {
        return myEventObjectList.size();
    }

    @Override
    public void onBindViewHolder(MyEventViewHolder eventViewHolder, int i) {
        EventObject currentEvent = myEventObjectList.get(i);

        eventViewHolder.setViewHolderEvent(currentEvent);

        //simple time manipulation
        String totalTime = currentEvent.getStartTime();
        String hourTime = totalTime.substring(0,2);
        String hourTimeWithColon = hourTime + ":";
        String minuteTime = totalTime.substring(3);

        //bindtoViewHolder
        eventViewHolder.vEventDescription.setText(currentEvent.getEventTitle());
        eventViewHolder.vHostName.setText(currentEvent.getCreatorName());
        eventViewHolder.vTxtHour.setText(hourTimeWithColon);
        eventViewHolder.vTxtMinute.setText(minuteTime);

    }

    @Override
    public MyEventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.event_card, viewGroup, false);

        return new MyEventViewHolder(itemView);
    }

    public class MyEventViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener {

        protected EventObject viewHolderEvent;
        protected TextView vEventDescription;
        protected TextView vTxtHour;
        protected TextView vTxtMinute;
        protected TextView vHostName;


        public MyEventViewHolder(View v) {
            super(v);
            v.setOnClickListener(this);
            vEventDescription =  (TextView) v.findViewById(R.id.txtEventName);
            vTxtHour =  (TextView) v.findViewById(R.id.txtHour);
            vTxtMinute =  (TextView) v.findViewById(R.id.txtMinute);
            vHostName =  (TextView) v.findViewById(R.id.txtHostName);
        }

        public void setViewHolderEvent(EventObject viewHolderEvent) {
            this.viewHolderEvent = viewHolderEvent;
        }

        @Override
        public void onClick(View view) {
            final Context context = view.getContext();
            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.inflate(R.menu.my_event_menu);
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.start_my_event:
                            //create group ID and go there
                            createNewGroup(viewHolderEvent);
                            break;
                        case R.id.edit_my_event:
                            Toast.makeText(context, " edit functionality not yet added ", Toast.LENGTH_LONG).show();
                            break;
                        case R.id.delete_my_event:
                            deleteEvent(viewHolderEvent);
                            break;
                    }
                    return false;
                }
            });
            popupMenu.show();

        }

    }

    private void createNewGroup(EventObject event) {
        //create new group
        String groupKey = FirebaseDatabase.getInstance().getReference().child("Groups").push().getKey();
        DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");

        //set relevant events chatID to groupKey
        String eventID = event.getEventID();
        FirebaseDatabase.getInstance().getReference().child("Events")
                .child(eventID).child("chatID").setValue(groupKey);
        FirebaseDatabase.getInstance().getReference().child("User_Events")
                .child(userID).child(todayDate).child(eventID).child("chatID").setValue(groupKey);
        FirebaseDatabase.getInstance().getReference().child("Date_Events")
                .child(todayDate).child(eventID).child("chatID").setValue(groupKey);

        //set events active status to true
        FirebaseDatabase.getInstance().getReference().child("Events")
                .child(eventID).child("active").setValue(true);
        FirebaseDatabase.getInstance().getReference().child("User_Events")
                .child(userID).child(todayDate).child(eventID).child("active").setValue(true);
        FirebaseDatabase.getInstance().getReference().child("Date_Events")
                .child(todayDate).child(eventID).child("active").setValue(true);

        //set default values for new group
        groupRef.child(groupKey).child("isOpen").setValue(true);
        groupRef.child(groupKey).child("Host").setValue(userID);
        groupRef.child(groupKey).child("Participants").setValue(true);
        groupRef.child(groupKey).child("Messages").setValue(true);

        //Set groupHost and currentGroupName and redirect
        Intent intent = new Intent(context, GroupChatActivity.class);
        intent.putExtra("groupHost", userID);
        intent.putExtra("currentGroupName", groupKey);
        context.startActivity(intent);
        ((Activity)context).finish();
    }

    private void deleteEvent(EventObject event) {
        //delete event object from user_event, event, today_event
        String eventID = event.getEventID();
        FirebaseDatabase.getInstance().getReference().child("Events")
                .child(eventID).removeValue();
        FirebaseDatabase.getInstance().getReference().child("User_Events")
                .child(userID).child(todayDate).child(eventID).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Date_Events")
                .child(todayDate).child(eventID).removeValue();
    }
}
