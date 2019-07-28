package com.nus.alchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.nus.alchemy.Model.EventObject;
import com.nus.alchemy.Model.MyEventAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MyEventsActivity extends AppCompatActivity implements View.OnClickListener {

    private BottomNavigationView bottomNavigationView;
    private Button newEventButton;
    private String userID;
    private ArrayList<EventObject> myEventList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        setUpBottomNavBar();

        //init myEventList
        myEventList = new ArrayList<>();

        //get userID
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        newEventButton = (Button) findViewById(R.id.newEventButton);
        newEventButton.setOnClickListener(this);

        //get today date in ISO8601 format
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = dateFormat.format(new Date());

        //get today dateTime in ISO8601 format
        final SimpleDateFormat dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm");


        Query ref = FirebaseDatabase.getInstance().getReference().child("User_Events")
                .child(userID).child(todayDate).orderByChild("startTime");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Date currentTime = new Date();
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            EventObject event = childSnapshot.getValue(EventObject.class);
                            Date eventDateTime = null;
                            try {
                                eventDateTime = dateTimeFormat.parse(
                                        event.getDateTime());
                            } catch (java.text.ParseException e) {}
                            if (currentTime.compareTo(eventDateTime) < 0) {
                                //event is after current time
                                myEventList.add(event);
                            }
                        }
                        //build Reycler View inside here to prevent null eventHandler
                        buildMyEventRecyclerView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //log error
                    }
                });
    }

    public void buildMyEventRecyclerView() {
        //check that myEventList has been read from firebase
        if (!this.myEventList.isEmpty()) {
            RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
            recList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);
            MyEventAdapter eventAdapter = new MyEventAdapter(this.myEventList);
            recList.setAdapter(eventAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == newEventButton) {
            //redirect to createEventPage
            Intent goToCreateNewEvent = new Intent(getApplicationContext(), CreateEventActivity.class);
            startActivity(goToCreateNewEvent);
            finish();
            return;
        }
    }

    private void setUpBottomNavBar() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(1);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.myProfile:
                        Intent toMyProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                        startActivity(toMyProfile);
                        finish();
                        break;
                    case R.id.myEvents:
                        break;
                    case R.id.todayEvents:
                        Intent toTodaysEvents = new Intent(getApplicationContext(), TodaysEventsActivity.class);
                        startActivity(toTodaysEvents);
                        finish();
                        break;
                    case R.id.myChats:
                        Intent toMyChats = new Intent(getApplicationContext(), MyChatsActivity.class);
                        startActivity(toMyChats);
                        finish();
                        break;
                }
                return true;
            }
        });
    }
}