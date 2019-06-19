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
import android.widget.TextView;

import com.nus.alchemy.Model.EventAdapter;
import com.nus.alchemy.Model.EventObject;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class TodaysEventsActivity extends AppCompatActivity implements View.OnClickListener{

    BottomNavigationView bottomNavigationView;
    TextView tempMatchTextView;
    TextView tempJoinGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todays_events);
        setUpBottomNavBar();
        tempMatchTextView = (TextView) findViewById(R.id.tempMatchTextView);
        tempMatchTextView.setOnClickListener(this);
        RecyclerView recList = (RecyclerView) findViewById(R.id.cardList);
        recList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        recList.setLayoutManager(llm);

        EventAdapter eventAdapter = new EventAdapter(createFakeEventList(30));
        recList.setAdapter(eventAdapter);
        tempJoinGroup = (TextView) findViewById(R.id.joinGroup);
        tempJoinGroup.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if (v == tempJoinGroup) {
            //get the td of the chat from the card
            String tempChatName = "Lhf5YQ1MKxhXIUshs3I";
            Intent intent = new Intent(getApplicationContext(), GroupChatActivity.class);
            intent.putExtra("groupName", tempChatName);
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

    @TargetApi(26)
    private List<EventObject> createFakeEventList(int size) {

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
