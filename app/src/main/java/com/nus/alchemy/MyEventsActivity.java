package com.nus.alchemy;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nus.alchemy.Model.EventAdapter;
import com.nus.alchemy.Model.EventObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class MyEventsActivity extends AppCompatActivity implements View.OnClickListener {

    BottomNavigationView bottomNavigationView;
    TextView tempMatchTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_events);
        setUpBottomNavBar();
        tempMatchTextView = (TextView) findViewById(R.id.tempMatchTextView);
        tempMatchTextView.setOnClickListener(this);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        EventAdapter eventAdapter = new EventAdapter(createFakeList(30));
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

    @TargetApi(26)
    private List<EventObject> createFakeList(int size) {

        List<EventObject> result = new ArrayList<EventObject>();
        for (int i=1; i <= size; i++) {
            EventObject ci = new EventObject();
            ci.name = "test name " + i;
            ci.eventName = "test event " + i;
            ci.startTime = LocalDateTime.of(2015, Month.JULY, 29, 19, 30);
            if (i % 2 == 0)
            {
                ci.preferred_gender = "Male";
            } else {
                ci.preferred_gender = "Female";
            }
            result.add(ci);

        }

        return result;
    }
}

