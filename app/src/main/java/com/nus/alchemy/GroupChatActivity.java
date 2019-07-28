package com.nus.alchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class GroupChatActivity extends AppCompatActivity implements View.OnClickListener {

    private Toolbar mToolbar;
    private ImageButton SendMessageButton;
    private EditText userMessageInput;
    private ScrollView mScrollView;
    private TextView displayTextMessages;
    private String currentGroupName;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    private DatabaseReference groupMessagesRef;
    private DatabaseReference groupMessageKeyRef;
    private String currentUserID;
    private String currentUserName;
    private String groupHost;
    private String currentDate;
    private String currentTime;
    private String eventName;
    private Button chooseSuitorButton;
    private Button closeDoorButton;
    private Button leaveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);
        currentGroupName = getIntent().getExtras().get("currentGroupName").toString();
        groupHost = getIntent().getExtras().get("groupHost").toString();
        eventName = getIntent().getExtras().get("eventName").toString();
        initAttributes();
        getUserInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        groupMessagesRef.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    DisplayMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == SendMessageButton) {
            sendMessage();
        }
        if (v == chooseSuitorButton) {
            Intent intent = new Intent(getApplicationContext(), SuitorActivity.class);
            intent.putExtra("groupId", currentGroupName);
            startActivity(intent);
            finish();
            return;
        }
        if (v == closeDoorButton) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String todayDate = dateFormat.format(new Date());
            //set current event to not active
            FirebaseDatabase.getInstance().getReference().child("Events")
                    .child(currentGroupName).child("active").setValue(false);
            FirebaseDatabase.getInstance().getReference().child("User_Events")
                    .child(currentUserID).child(todayDate).child(currentGroupName).child("active").setValue(false);
            FirebaseDatabase.getInstance().getReference().child("Date_Events")
                    .child(todayDate).child(currentGroupName).child("active").setValue(false);

        }
        if (v == leaveButton) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String todayDate = dateFormat.format(new Date());
            //delete event after host leaves the event
            FirebaseDatabase.getInstance().getReference().child("Events")
                    .child(currentGroupName).removeValue();
            FirebaseDatabase.getInstance().getReference().child("User_Events")
                    .child(currentUserID).child(todayDate).child(currentGroupName).removeValue();
            FirebaseDatabase.getInstance().getReference().child("Date_Events")
                    .child(todayDate).child(currentGroupName).removeValue();
            Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
            startActivity(intent);
            finish();
            return;
        }
    }

    private void sendMessage() {
        String message = userMessageInput.getText().toString();
        String messageKey = groupMessagesRef.push().getKey();
        if (TextUtils.isEmpty(message)) {
            return;
        }
        else {
            Calendar calForDate = Calendar.getInstance();
            SimpleDateFormat currentDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            currentDate = currentDateFormat.format(calForDate.getTime());

            Calendar calForTime = Calendar.getInstance();
            SimpleDateFormat currentTimeFormat = new SimpleDateFormat("hh:mm a");
            currentTime = currentTimeFormat.format(calForTime.getTime());

            HashMap<String,Object> groupMessageKey = new HashMap<>();
            groupMessagesRef.updateChildren(groupMessageKey);
            groupMessageKeyRef = groupMessagesRef.child(messageKey);
            HashMap<String, Object> messageInfoMap = new HashMap<>();
            messageInfoMap.put("name", currentUserName);
            messageInfoMap.put("message", message);
            messageInfoMap.put("date",currentDate);
            messageInfoMap.put("time", currentTime);
            groupMessageKeyRef.updateChildren(messageInfoMap);
            userMessageInput.setText("");
            mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }


    private void DisplayMessages(DataSnapshot dataSnapshot) {
        String chatDate = dataSnapshot.child("date").getValue().toString();
        String chatMessage = dataSnapshot.child("message").getValue().toString();
        String chatName = dataSnapshot.child("name").getValue().toString();
        String chatTime = dataSnapshot.child("time").getValue().toString();
        displayTextMessages.append(chatName+ ":\n" + chatMessage + "\n" + chatTime + " " + chatDate + "\n\n\n\n");
        mScrollView.fullScroll(ScrollView.FOCUS_DOWN);
    }

    private void getUserInfo() {
        userRef.child(currentUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    currentUserName = dataSnapshot.child("Name").getValue().toString();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }


    private void initAttributes() {
        mToolbar = (Toolbar) findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle(eventName);
        SendMessageButton = (ImageButton) findViewById(R.id.send_message_button);
        SendMessageButton.setOnClickListener(this);
        userMessageInput = (EditText) findViewById(R.id.input_group_message);
        displayTextMessages = (TextView) findViewById(R.id.group_chat_text_display);
        mScrollView = (ScrollView) findViewById(R.id.my_scroll_view);
        mAuth = FirebaseAuth.getInstance();
        currentUserID = mAuth.getCurrentUser().getUid();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users");
        groupMessagesRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(currentGroupName).child("Messages");
        chooseSuitorButton = (Button) findViewById(R.id.chooseSuitorButton);
        closeDoorButton = (Button) findViewById(R.id.closeDoorButton);
        leaveButton = (Button) findViewById(R.id.leaveGroupButton);
        chooseSuitorButton.setOnClickListener(this);
        closeDoorButton.setOnClickListener(this);
        leaveButton.setOnClickListener(this);
        if (!groupHost.equals(mAuth.getUid())) {
            chooseSuitorButton.setVisibility(View.GONE);
            closeDoorButton.setVisibility(View.GONE);
        }
    }
}
