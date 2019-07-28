package com.nus.alchemy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.nus.alchemy.Model.MediaAdapter;
import com.nus.alchemy.Model.MessageAdapter;
import com.nus.alchemy.Model.MessageObject;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private RecyclerView mChat;
    private RecyclerView.Adapter mChatAdapter;
    private RecyclerView.LayoutManager mChatLayoutManager;
    private RecyclerView mMedia;
    private RecyclerView.Adapter mMediaAdapter;
    private RecyclerView.LayoutManager mMediaLayoutManager;
    private Button mSend;
    private Button mAddMedia;
    private ProgressDialog progressDialog;
    private CircleImageView userImage;
    private TextView userName;
    private Toolbar chatToolbar;
    private EditText mMessage;
    private ArrayList<MessageObject> messageList;
    private String chatID;
    private DatabaseReference mChatDb;
    private String currentUserID;
    private CircleImageView chatProfPic;

    int totalMediaUploaded = 0;
    ArrayList<String> mediaIdList = new ArrayList<>();
    int PICK_IMAGE_INTENT = 1;
    ArrayList<String> mediaURIList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        Fresco.initialize(this);
        initAttributes();
        initMessage();
        initMedia();
        getChatMessages();
    }

    @Override
    public void onClick(View v) {
        if (v == mSend) {
            sendMessageAndMedia();
        }
        if (v == mAddMedia) {
            openGallery();
        }
    }

    private void sendMessageAndMedia() {
        String messageID = mChatDb.push().getKey();
        final DatabaseReference newMessageDb = mChatDb.child(messageID);
        final Map newMessageMap = new HashMap<>();
        String creatorID = currentUserID;
        newMessageMap.put("creator", creatorID);
        if (!mMessage.getText().toString().isEmpty()) {
            newMessageMap.put("text", mMessage.getText().toString());
        }
        if (!mediaURIList.isEmpty()) {
            uploadMediaToStorage(messageID, newMessageDb, newMessageMap);
        } else {
            if (!mMessage.getText().toString().isEmpty()) {
                updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
            }
        }
    }


    private void uploadMediaToStorage(String messageID, final DatabaseReference newMessageDb, final Map newMessageMap) {
        progressDialog.setMessage("Sending Media...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
        for (String mediaURI : mediaURIList) {
            String mediaID = newMessageDb.child("media").push().getKey();
            mediaIdList.add(mediaID);
            final StorageReference filePath = FirebaseStorage.getInstance().getReference().child("chat").child(chatID).child(messageID).child(mediaID);
            UploadTask uploadTask = filePath.putFile(Uri.parse(mediaURI));
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            newMessageMap.put("/media/" + mediaIdList.get(totalMediaUploaded) + "/", uri.toString());
                            totalMediaUploaded++;
                            if (totalMediaUploaded == mediaURIList.size()) {
                                updateDatabaseWithNewMessage(newMessageDb, newMessageMap);
                            }
                        }
                    });
                }
            });
        }
    }

    private void updateDatabaseWithNewMessage(DatabaseReference newMessageDb, Map newMessageMap) {
        newMessageDb.updateChildren(newMessageMap).addOnCompleteListener(new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {
                progressDialog.dismiss();
                if (task.isSuccessful()) {
                }
                else {
                    Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        mMessage.setText(null);
        mediaURIList.clear();
        mediaIdList.clear();
        totalMediaUploaded = 0;
        mMediaAdapter.notifyDataSetChanged();
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
        intent.setAction(intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture(s)"), PICK_IMAGE_INTENT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE_INTENT) {
                if (data.getClipData() == null) {
                    mediaURIList.add(data.getData().toString());
                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        mediaURIList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                mMediaAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initAttributes() {
        mSend = (Button) findViewById(R.id.send);
        mSend.setOnClickListener(this);
        mAddMedia = (Button) findViewById(R.id.addMedia);
        mAddMedia.setOnClickListener(this);
        mMessage = (EditText) findViewById(R.id.messageInput);
        chatID = getIntent().getExtras().getString("chatID");
        mChatDb = FirebaseDatabase.getInstance().getReference().child("Chats").child(chatID);
        userImage = (CircleImageView) findViewById(R.id.custom_profile_image);
        userName = (TextView) findViewById(R.id.custom_profile_name);
        progressDialog = new ProgressDialog(this);
        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
        chatToolbar = (Toolbar) findViewById(R.id.chat_bar_layout);
        setSupportActionBar(chatToolbar);
        getSupportActionBar().setTitle("");
        TextView custom_name = findViewById(R.id.custom_profile_name);
        custom_name.setText(getIntent().getExtras().getString("otherUserName"));
        chatProfPic = (CircleImageView) findViewById(R.id.custom_profile_image);
        String otherUserProfImg = getIntent().getExtras().getString("otherUserProfileImg");
        if (!otherUserProfImg.equals("")) {
            Picasso.get().load(otherUserProfImg).into(chatProfPic);
        }
    }

    private void initMedia() {
        mediaURIList = new ArrayList<>();
        mMedia = findViewById(R.id.mediaList);
        mMedia.setNestedScrollingEnabled(false);
        mMedia.setHasFixedSize(false);
        mMediaLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.HORIZONTAL, false);
        mMedia.setLayoutManager(mMediaLayoutManager);
        mMediaAdapter = new MediaAdapter(getApplicationContext(), mediaURIList);
        mMedia.setAdapter(mMediaAdapter);
    }

    private void getChatMessages() {
        mChatDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot.exists()) {
                    String text = "";
                    String creatorID = "";
                    ArrayList<String> mediaUrlList = new ArrayList<>();
                    if (dataSnapshot.child("text").getValue() != null) {
                        text = dataSnapshot.child("text").getValue().toString();
                    }
                    if (dataSnapshot.child("creator").getValue() != null) {
                        creatorID = dataSnapshot.child("creator").getValue().toString();
                    }
                    if (dataSnapshot.child("media").getChildrenCount() > 0) {
                        for (DataSnapshot mediaSnapShot : dataSnapshot.child("media").getChildren()) {
                            mediaUrlList.add(mediaSnapShot.getValue().toString());
                        }
                    }
                    MessageObject myMessage = new MessageObject(creatorID, dataSnapshot.getKey(), text, mediaUrlList);
                    messageList.add(myMessage);
                    mChatLayoutManager.scrollToPosition(messageList.size() - 1);
                    mChatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
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

    private void initMessage() {
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