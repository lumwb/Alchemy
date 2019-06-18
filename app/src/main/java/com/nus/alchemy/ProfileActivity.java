package com.nus.alchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    TextView username;
    TextView sex;
    TextView age;
    EditText myStory;
    Button updateSettings;
    TextView logoutTextView;
    CircleImageView userProfileImage;
    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        username = (TextView) findViewById(R.id.set_user_name);
        age = (TextView) findViewById(R.id.set_user_age);
        sex = (TextView) findViewById(R.id.set_user_sex);
        myStory = (EditText) findViewById(R.id.set_profile_status);
        myStory.setText("");
        updateSettings =(Button) findViewById(R.id.update_settings_button);
        updateSettings.setOnClickListener(this);
        logoutTextView = (TextView) findViewById(R.id.logoutTextView);
        logoutTextView.setOnClickListener(this);
        userProfileImage = (CircleImageView) findViewById(R.id.user_profile_image);

        initUserProfile();
        setUpBottomNavBar();


    }

    @Override
    public void onClick(View v) {
        if (v == logoutTextView) {
            Intent logout = new Intent(this, MainActivity.class);
            FirebaseAuth.getInstance().signOut();
            logout.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(logout);
            finish();
            return;
        }
        if (v == updateSettings) {
            updateUserSettings();
        }
    }

    private void initUserProfile() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getUid());
        boolean helper = true;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 username.setText(dataSnapshot.child("Name").getValue().toString());
                 sex.setText(dataSnapshot.child("Sex").getValue().toString());
                 age.setText(dataSnapshot.child("Age").getValue().toString());
                 myStory.setText(dataSnapshot.child("Story").getValue().toString());
                //userprofile image here
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.child("Helper").setValue(helper);
    }

    private void updateUserSettings() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getUid());
        final String userStory = myStory.getText().toString();
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myStory.setText(userStory);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        ref.child("Story").setValue(userStory);
    }

    private void setUpBottomNavBar() {
        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationView);
        Menu menu = bottomNavigationView.getMenu();
        MenuItem menuItem = menu.getItem(0);
        menuItem.setChecked(true);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.myProfile:
                        //already in profile
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
