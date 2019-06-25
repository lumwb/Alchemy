package com.nus.alchemy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nus.alchemy.Model.EventObject;

import java.util.HashMap;
import java.util.Map;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private EditText roomSizeEditText;
    private EditText startTimeEditText;
    private TimePicker startTimePicker;
    private RadioGroup eventSexRadioGroup;
    private Button createEventButton;
    private TextView cancelEventTextView;
    private String userID;
    private String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);

        firebaseAuth = FirebaseAuth.getInstance();
        roomSizeEditText = findViewById(R.id.roomSizeEditText);
        eventSexRadioGroup = findViewById(R.id.eventSexRadioGroup);
        createEventButton = findViewById(R.id.createEventButton);
        createEventButton.setOnClickListener(this);
        cancelEventTextView = findViewById(R.id.cancelEventTextView);
        cancelEventTextView.setOnClickListener(this);
        startTimePicker= findViewById(R.id.timePicker);
        startTimePicker.setIs24HourView(true);
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getNameFromUserID(userID);
    }

    @Override
    public void onClick(View v) {
        if (v == createEventButton) {
            createEvent();

        }
        if (v == cancelEventTextView) {
            Intent toMyEvents = new Intent(getApplicationContext(), MyEventsActivity.class);
            startActivity(toMyEvents);
            finish();
            return;
        }
    }

    public void createEvent() {
        //construct event object
        int maxRoomSize = Integer.parseInt(roomSizeEditText.getText().toString());

        //get preferred sex
        int radioButtonID = eventSexRadioGroup.getCheckedRadioButtonId();
        RadioButton rb = findViewById(radioButtonID);
        final String preferredSex = rb.getText().toString();

        //get time
        String eventStartTime = "";
        int hour, minute;
        if (Build.VERSION.SDK_INT >= 23 ){
            hour = startTimePicker.getHour();
            minute = startTimePicker.getMinute();
        }
        else{
            hour = startTimePicker.getCurrentHour();
            minute = startTimePicker.getCurrentMinute();
        }
        eventStartTime = hour + ":" + minute;


        //push event event child
        String eventID = FirebaseDatabase.getInstance().getReference().child("Events").push().getKey();


        EventObject event = new EventObject(maxRoomSize, eventStartTime, preferredSex,
                this.userID, this.name);

        Map<String, EventObject> users = new HashMap<>();
        users.put(eventID, event);

        FirebaseDatabase.getInstance().getReference().child("Events").child(eventID).setValue(event);

        //write some validation here to check room size and date

        //push event to user_events



        //redirect back to myEventPage, will reread all events required
        Intent toMyEvents = new Intent(getApplicationContext(), MyEventsActivity.class);
        startActivity(toMyEvents);
        finish();
        return;
    }

    private void getNameFromUserID(String userID) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        DatabaseReference nameRef = ref.child("Users").child(userID).child("Name");
        nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                name = dataSnapshot.getValue(String.class);
                //do what you want with the email
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
