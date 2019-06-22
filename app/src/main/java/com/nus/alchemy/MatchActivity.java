package com.nus.alchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class MatchActivity extends AppCompatActivity implements View.OnClickListener {

    Button sendTextButton;
    Button closeButton;
    String otherUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        sendTextButton = (Button) findViewById(R.id.sendTextButton);
        closeButton = (Button) findViewById(R.id.closeButton);
        sendTextButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == sendTextButton) {
            String key = FirebaseDatabase.getInstance().getReference().child("Chats").push().getKey();
            //need to get userID of the other person from the group chat
            String otherUserID = "B4ekukqePvcA7N5o3gZedzmEiC33";
            String myID = FirebaseAuth.getInstance().getUid();
            FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("chat").child(key).setValue(otherUserID);
            FirebaseDatabase.getInstance().getReference().child("Users").child(otherUserID).child("chat").child(key).setValue(myID);
            Intent goToMyChats = new Intent(getApplicationContext(), MyChatsActivity.class);
            startActivity(goToMyChats);
            finish();
        }
        if (v == closeButton) {
            Intent goToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(goToProfile);
            finish();
            return;
        }
    }
}
