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
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nus.alchemy.Model.ChatListAdapter;
import com.nus.alchemy.Model.ChatObject;

import java.util.ArrayList;

public class MyChatsActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    private RecyclerView mChatList;
    private RecyclerView.Adapter mChatListAdapter;
    private RecyclerView.LayoutManager mChatListLayoutManager;
    ArrayList<ChatObject> chatList;
    private StorageReference userProfileImagesRef;
    private String currentUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_chats);
        chatList = new ArrayList<>();
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
        setUpBottomNavBar();
        initRecyclerView();
        getUserChatList();
    }

    private void getUserChatList() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Users");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot chats : dataSnapshot.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("chat").getChildren()) {
                        String otherUserID = chats.getValue().toString();
                        String otherUserName = dataSnapshot.child(otherUserID).child("Name").getValue().toString();
                        String otherUserProfImg = dataSnapshot.child(otherUserID).child("Image").getValue().toString();
                        ChatObject chatObject = new ChatObject(chats.getKey(), chats.getValue().toString(), otherUserName, otherUserProfImg);
                        boolean exists = false;
                        for (ChatObject mChatIterator : chatList) {
                            if (mChatIterator.getChatID().equals(chatObject.getChatID())) {
                                exists = true;
                            }
                        }
                        if (exists) {
                            continue;
                        }
                        chatList.add(chatObject);
                        mChatListAdapter.notifyDataSetChanged();
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void initRecyclerView() {
        mChatList = findViewById(R.id.chatListRecyclerView);
        mChatList.setNestedScrollingEnabled(false);
        mChatList.setHasFixedSize(false);
        mChatListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mChatList.setLayoutManager(mChatListLayoutManager);
        mChatListAdapter = new ChatListAdapter(chatList);
        mChatList.setAdapter(mChatListAdapter);
    }

    private void setUpBottomNavBar() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(3);
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
                        Intent toTodaysEvents = new Intent(getApplicationContext(), TodaysEventsActivity.class);
                        startActivity(toTodaysEvents);
                        finish();
                        break;
                    case R.id.myChats:
                        break;
                }
                return true;
            }
        });
    }
}
