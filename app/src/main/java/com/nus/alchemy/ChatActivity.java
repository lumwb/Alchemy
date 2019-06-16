package com.nus.alchemy;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.nus.alchemy.Model.MessageAdapter;
import com.nus.alchemy.Model.MessageObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mChat;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private Button mSend;
    ArrayList<MessageObject> messageList;
    String chatID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        mSend = (Button) findViewById(R.id.send);
        mSend.setOnClickListener(this);
        chatID = getIntent().getExtras().getString("chatID");
        initRecyclerView();
    }

    @Override
    public void onClick(View v) {
        if (v == mSend) {
            EditText mMessage = findViewById(R.id.message);
            if (!mMessage.getText().toString().isEmpty()) {
                DatabaseReference newMessageDb = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatID).push();
                Map newMessageMap = new HashMap<>();
                newMessageMap.put("text", mMessage.getText().toString());
                newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
                newMessageDb.updateChildren(newMessageMap);
            }
            mMessage.setText(null);
        }
    }

    private void initRecyclerView() {
        messageList = new ArrayList<>();
        mChat = findViewById(R.id.messageList);
        mChat.setNestedScrollingEnabled(false);
        mChat.setHasFixedSize(false);
        mChatLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mChat.setLayoutManager(mChatLayoutManager);
        mChatAdapter = new MessageAdapter(messageList);
        mChat.setAdapter(mChatAdapter);
    }
}
