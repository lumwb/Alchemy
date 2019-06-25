package com.nus.alchemy;

import android.annotation.TargetApi;
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
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nus.alchemy.Model.EventAdapter;
import com.nus.alchemy.Model.EventObject;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class TodaysEventsActivity extends AppCompatActivity implements View.OnClickListener{

    private BottomNavigationView bottomNavigationView;
    private TextView tempMatchTextView;
    private TextView tempJoinGroup;
    private ArrayList<EventObject> eventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_events);

        setUpBottomNavBar();

        //init eventList
        eventList = new ArrayList<>();

        tempMatchTextView = (TextView) findViewById(R.id.tempMatchTextView);
        tempMatchTextView.setOnClickListener(this);

        //get all event from Firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Events");
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            EventObject event = childSnapshot.getValue(EventObject.class);
                            eventList.add(event);
                        }
                        //build Reycler View inside here to prevent null eventHandler
                        buildEventRecyclerView();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //log error
                    }
                });

        tempJoinGroup = (TextView) findViewById(R.id.joinGroup);
        tempJoinGroup.setOnClickListener(this);
    }

    //custom method to build RecyclerView
    public void buildEventRecyclerView() {
        if (!this.eventList.isEmpty()) {
            RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
            recList.setHasFixedSize(true);
            LinearLayoutManager llm = new LinearLayoutManager(this);
            llm.setOrientation(LinearLayoutManager.VERTICAL);
            recList.setLayoutManager(llm);
            EventAdapter eventAdapter = new EventAdapter(this.eventList);
            recList.setAdapter(eventAdapter);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == tempJoinGroup) {
            //get the td of the chat from the card
            //gmailPhone
            String tempChatHost = "P5VGhmdxLhZmxnwlySYAtsgWTL43";
            String currentUserId = FirebaseAuth.getInstance().getUid();
            //technically the id of the chat should be generated at event creation but temp use chathost id
            DatabaseReference groupRef = FirebaseDatabase.getInstance().getReference().child("Groups");
            groupRef.child(tempChatHost).child("isOpen").setValue(true);
            groupRef.child(tempChatHost).child("Host").setValue(tempChatHost);
            groupRef.child(tempChatHost).child("Participants").child(currentUserId).setValue(true);
            Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
            intent.putExtra("groupHost", tempChatHost);




            startActivity(intent);
            finish();
        }
    }

    private void setUpBottomNavBar() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(2);
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
                        Intent toMyEvents = new Intent(getApplicationContext(), MyEventsActivity.class);
                        startActivity(toMyEvents);
                        finish();
                        break;
                    case R.id.todayEvents:
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
