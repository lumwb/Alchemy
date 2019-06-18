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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;
import com.nus.alchemy.Model.EventAdapter;
import com.nus.alchemy.Model.EventObject;

import java.util.ArrayList;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity implements View.OnClickListener {

    BottomNavigationView bottomNavigationView;
    TextView tempMatchTextView;
    TextView tempGroupMatch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        setUpBottomNavBar();


        tempMatchTextView = (TextView) findViewById(R.id.tempMatchTextView);
        tempMatchTextView.setOnClickListener(this);
        tempGroupMatch = (TextView) findViewById(R.id.createGrouptemp);
        tempGroupMatch.setOnClickListener(this);


        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        EventAdapter eventAdapter = new EventAdapter(createList(30));
        recList.setAdapter(eventAdapter);

    }

    @Override
    public void onClick(View v) {
        if (v == tempMatchTextView) {
            Intent goToMatch = new Intent(getApplicationContext(), MatchActivity.class);
            startActivity(goToMatch);
            finish();
            return;
        }
        if (v == tempGroupMatch) {
            createNewGroup();
        }
    }

    private void createNewGroup() {
        String key = FirebaseDatabase.getInstance().getReference().child("Groups").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("Groups").child(key).setValue(true);
    }

    private void CreateNewGroup(String groupName) {
        FirebaseDatabase.getInstance().getReference().child("Groups").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Created group successfully", Toast.LENGTH_SHORT);
                        }
                    }
                });
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
    private List<EventObject> createList(int size) {

        List<EventObject> result = new ArrayList<EventObject>();
        for (int i=1; i <= size; i++) {
            EventObject ci = new EventObject();
            ci.name = EventObject.NAME_PREFIX + i;
            ci.surname = EventObject.SURNAME_PREFIX + i;
            ci.email = EventObject.EMAIL_PREFIX + i + "@test.com";

            result.add(ci);

        }

        return result;
    }
}

