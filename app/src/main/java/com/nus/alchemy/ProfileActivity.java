package com.nus.alchemy;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    DatabaseReference RootRef;
    TextView username;
    TextView sex;
    TextView age;
    EditText myStory;
    Button updateSettings;
    TextView logoutTextView;
    CircleImageView userProfileImage;
    private String currentUserID;
    BottomNavigationView bottomNavigationView;
    private static final int GalleryPick = 1;
    private StorageReference userProfileImagesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firebaseAuth = FirebaseAuth.getInstance();
        userProfileImagesRef = FirebaseStorage.getInstance().getReference().child("Profile Images");
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
        userProfileImage.setOnClickListener(this);
        currentUserID = firebaseAuth.getCurrentUser().getUid().toString();
        RootRef = FirebaseDatabase.getInstance().getReference();
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
        if (v == userProfileImage) {
            Intent galleryIntent = new Intent();
            galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
            galleryIntent.setType("image/*");
            //startActivityForResult(galleryIntent, GalleryPick);
            startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), GalleryPick);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GalleryPick && resultCode == RESULT_OK && data != null) {
            Uri ImageUri = data.getData();
            CropImage.activity().setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                final StorageReference filePath = userProfileImagesRef.child(FirebaseAuth.getInstance().getUid() + ".jpg");
                filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        setPhoto(filePath);
                    }
                });
            }
        }
    }

    private void setPhoto(StorageReference filePath) {
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                final String downloadUrl = uri.toString();
                RootRef.child("Users").child(currentUserID).child("Image").setValue(downloadUrl).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successful", Toast.LENGTH_SHORT).show();
                            //dismiss pd
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "Error occured...", Toast.LENGTH_SHORT).show();
                            //dismiss pd
                        }
                    }
                });

            }
        });
    }

    private void initUserProfile() {
        final DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseAuth.getUid());
        //boolean helper = true;
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                 if (dataSnapshot.exists()) {
                     username.setText(dataSnapshot.child("Name").getValue().toString());
                     sex.setText(dataSnapshot.child("Sex").getValue().toString());
                     age.setText(dataSnapshot.child("Age").getValue().toString());
                     myStory.setText(dataSnapshot.child("Story").getValue().toString());
                 }
                 if (dataSnapshot.hasChild("Image")) {
                     String profImg = dataSnapshot.child("Image").getValue().toString();
                     Picasso.get().load(profImg).into(userProfileImage);
                 }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //ref.child("Helper").setValue(helper);
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
