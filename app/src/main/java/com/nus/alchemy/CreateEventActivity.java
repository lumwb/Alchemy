package com.nus.alchemy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
    private EditText eventTitleEditText;
    private EditText roomSizeEditText;
    private TimePicker startTimePicker;
    private DatePicker eventDatePicker;
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
        eventTitleEditText = findViewById(R.id.eventTitleEditText);
        roomSizeEditText = findViewById(R.id.roomSizeEditText);
        roomSizeEditText.setFilters(new InputFilter[]{new InputFilterMinMax("2", "20")});
        eventSexRadioGroup = findViewById(R.id.eventSexRadioGroup);
        createEventButton = findViewById(R.id.createEventButton);
        createEventButton.setOnClickListener(this);
        cancelEventTextView = findViewById(R.id.cancelEventTextView);
        cancelEventTextView.setOnClickListener(this);
        eventDatePicker = findViewById(R.id.eventDatePicker);
        eventDatePicker.setMinDate(System.currentTimeMillis() - 1000);
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

        //get eventTitle
        String eventTitle = eventTitleEditText.getText().toString();

        //get maxRoomSize
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
        String formatted_hour = String.format("%02d", hour);
        String formatted_minute = String.format("%02d", minute);
        eventStartTime = formatted_hour + ":" + formatted_minute;

        //get date - follow ISO8601 standard
        int day = eventDatePicker.getDayOfMonth();
        int month = eventDatePicker.getMonth() + 1;
        int year =  eventDatePicker.getYear();
        String formatted_day = String.format("%02d", day);
        String formatted_month = String.format("%02d", month);
        String eventDate = year + "-" + formatted_month + "-" + formatted_day;

        //construct complete dateTime in ISO8601 format
        String dateTime = eventDate + "T" + eventStartTime;


        //push event event child
        String eventID = FirebaseDatabase.getInstance().getReference().child("Events").push().getKey();


        EventObject event = new EventObject(eventTitle, maxRoomSize, eventStartTime, eventDate, dateTime,
                preferredSex, this.userID, this.name, eventID);

        Map<String, EventObject> users = new HashMap<>();
        users.put(eventID, event);

        FirebaseDatabase.getInstance().getReference().child("Events").child(eventID).setValue(event);

        //write some validation here to check room size and date

        //push event to event by date
        FirebaseDatabase.getInstance().getReference().child("Date_Events")
                .child(eventDate).child(eventID).setValue(event);

        //push event to user_events
        FirebaseDatabase.getInstance().getReference().child("User_Events")
                .child(userID).child(eventDate).child(eventID).setValue(event);


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
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}

class InputFilterMinMax implements InputFilter {
    private int min, max;

    public InputFilterMinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public InputFilterMinMax(String min, String max) {
        this.min = Integer.parseInt(min);
        this.max = Integer.parseInt(max);
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            int input = Integer.parseInt(dest.toString() + source.toString());
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
