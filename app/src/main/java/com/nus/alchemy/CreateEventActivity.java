package com.nus.alchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import de.hdodenhof.circleimageview.CircleImageView;

public class CreateEventActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth firebaseAuth;
    private EditText roomSizeEditText;
    private EditText startTimeEditText;
    private RadioGroup eventSexRadioGroup;
    private Button createEventButton;
    private TextView cancelEventTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        firebaseAuth = FirebaseAuth.getInstance();
        roomSizeEditText = findViewById(R.id.roomSizeEditText);
        startTimeEditText = findViewById(R.id.startTimeEditText);
        eventSexRadioGroup = (RadioGroup) findViewById(R.id.eventSexRadioGroup);
        createEventButton = findViewById(R.id.createEventButton);
        cancelEventTextView = findViewById(R.id.cancelEventTextView);
    }

    @Override
    public void onClick(View v) {
        if (v == createEventButton) {
            createEvent();

        }
        if (v == cancelEventTextView) {
            resetCreatePage();
        }
    }

    public void createEvent() {
        //push event to firebase
    }

    public void resetCreatePage() {

    }
}
