package com.nus.alchemy;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nus.alchemy.Model.EventObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class UpdateEventActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private EditText eventTitleEditText;
    //private EditText roomSizeEditText;
    private TimePicker startTimePicker;
    private DatePicker eventDatePicker;
    private RadioGroup eventSexRadioGroup;
    private RadioButton maleButton;
    private RadioButton femaleButton;
    private Button updateEventButton;
    private TextView cancelEventTextView;
    private String userID;
    private String name;
    private String eventID;
    private EventObject eventToUpdate;
    private final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private final SimpleDateFormat TimeFormat = new SimpleDateFormat("HH:mm");

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_event);

        firebaseAuth = FirebaseAuth.getInstance();

        //get event from Firebase
        eventID = getIntent().getStringExtra("eventID");
        Query ref = FirebaseDatabase.getInstance().getReference().child("Events")
                .child(eventID);
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        eventToUpdate = dataSnapshot.getValue(EventObject.class);
                        bindElements();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void bindElements() {
        //set default values for  each widget
        eventTitleEditText = findViewById(R.id.eventTitleEditText);
        eventTitleEditText.setText(eventToUpdate.getEventTitle());

        eventSexRadioGroup = findViewById(R.id.eventSexRadioGroup);
        maleButton = findViewById(R.id.eventMaleRadioButton);
        femaleButton = findViewById(R.id.eventFemaleRadioButton);
        if (eventToUpdate.getPreferredSex().equals("Male")) {
            maleButton.setChecked(true);
        } else {
            femaleButton.setChecked(true);
        }

        eventDatePicker = findViewById(R.id.eventDatePicker);
        String eventDateString = eventToUpdate.getEventDate();
        try{
            eventDatePicker.setMinDate(dateTimeFormat.parse(eventDateString).getTime());
        }
        catch (java.text.ParseException e) {}


        startTimePicker= findViewById(R.id.timePicker);
        startTimePicker.setIs24HourView(true);
        String startTime = eventToUpdate.getStartTime();
        startTimePicker.setHour(Integer.parseInt(startTime.substring(0, 2)));
        startTimePicker.setMinute(Integer.parseInt(startTime.substring(3)));

        updateEventButton = findViewById(R.id.updateEventButton);
        updateEventButton.setOnClickListener(this);
        cancelEventTextView = findViewById(R.id.cancelEventTextView);
        cancelEventTextView.setOnClickListener(this);

        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        getNameFromUserID(userID);
    }

    @Override
    public void onClick(View v) {
        if (v == updateEventButton) {
            updateEvent();

        }
        if (v == cancelEventTextView) {
            Intent toMyEvents = new Intent(getApplicationContext(), MyEventsActivity.class);
            startActivity(toMyEvents);
            finish();
            return;
        }
    }

    public void updateEvent() {
        //construct event object

        //get data from widgets
        String eventTitle = eventTitleEditText.getText().toString();
        int radioButtonID = eventSexRadioGroup.getCheckedRadioButtonId();
        int hour, minute;
        if (Build.VERSION.SDK_INT >= 23 ){
            hour = startTimePicker.getHour();
            minute = startTimePicker.getMinute();
        }
        else{
            hour = startTimePicker.getCurrentHour();
            minute = startTimePicker.getCurrentMinute();
        }
        int day = eventDatePicker.getDayOfMonth();
        int month = eventDatePicker.getMonth() + 1;
        int year =  eventDatePicker.getYear();
        RadioButton rb = findViewById(radioButtonID);
        final String preferredSex = rb.getText().toString();

        //format time
        String formatted_hour = String.format("%02d", hour);
        String formatted_minute = String.format("%02d", minute);
        String eventStartTime = formatted_hour + ":" + formatted_minute;

        //format - follow ISO8601 standard
        String formatted_day = String.format("%02d", day);
        String formatted_month = String.format("%02d", month);
        String eventDate = year + "-" + formatted_month + "-" + formatted_day;

        //construct complete dateTime in ISO8601 format
        String dateTime = eventDate + "T" + eventStartTime;

        //validate fields
        boolean validInput = true;
        //eventTitle validation
        if(eventTitle.equalsIgnoreCase("") || eventTitle.isEmpty()) {
            validInput = false;
            Toast.makeText(this, "event title cannot be blank!",
                    Toast.LENGTH_LONG).show();
            eventTitleEditText.setError("please enter event title");
        }
        //eventDateValidation
        Date currentDate = new Date();
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");
        Date eventDateTime = null;
        try {
            eventDateTime = dateTimeFormat.parse(dateTime);
        } catch (java.text.ParseException e) {}
        if (currentDate.compareTo(eventDateTime) > 0) {
            validInput = false;
            Toast.makeText(this, "Event date cannot be in the past!",
                    Toast.LENGTH_LONG).show();
        }
        if (!validInput) {
            //redirect to itself
            return;
        }

        EventObject event = new EventObject(eventTitle, eventStartTime, eventDate, dateTime,
                preferredSex, this.userID, this.name, eventID);


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

