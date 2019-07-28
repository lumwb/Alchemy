package com.nus.alchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class MatchActivity extends AppCompatActivity implements View.OnClickListener {

    Button sendTextButton;
    Button closeButton;
    String myID;
    String otherUserID;
    String image1;
    String image2;
    String eventID;
    CircleImageView myImage;
    CircleImageView otherImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match);

        sendTextButton = (Button) findViewById(R.id.sendTextButton);
        closeButton = (Button) findViewById(R.id.closeButton);
        sendTextButton.setOnClickListener(this);
        closeButton.setOnClickListener(this);
        myID = getIntent().getExtras().get("user1").toString();
        otherUserID = getIntent().getExtras().get("user2").toString();
        eventID = getIntent().getExtras().get("eventID").toString();
        myImage = findViewById(R.id.myImage);
        otherImage = findViewById(R.id.otherImage);

        loadImages();
    }

    private void loadImages() {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(myID).hasChild("Image")) {
                    image1 = dataSnapshot.child(myID).child("Image").getValue().toString();
                    Picasso.get().load(image1).placeholder(R.drawable.icon).into(myImage);
                }
                if (dataSnapshot.child(otherUserID).hasChild("Image")) {
                    image2 = dataSnapshot.child(otherUserID).child("Image").getValue().toString();
                    Picasso.get().load(image2).placeholder(R.drawable.icon).into(otherImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == sendTextButton) {
            String key = FirebaseDatabase.getInstance().getReference().child("Chats").push().getKey();
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String todayDate = dateFormat.format(new Date());
        FirebaseDatabase.getInstance().getReference().child("Events")
                .child(eventID).removeValue();
        FirebaseDatabase.getInstance().getReference().child("User_Events")
                .child(myID).child(todayDate).child(eventID).removeValue();
        FirebaseDatabase.getInstance().getReference().child("Date_Events")
                .child(todayDate).child(eventID).removeValue();
    }
}
