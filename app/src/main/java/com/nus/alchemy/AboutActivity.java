package com.nus.alchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;

public class AboutActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    private EditText nameEditText;
    private EditText ageEditText;
    private Spinner sexSpinner;
    private EditText descriptionEditText;
    private Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);


        firebaseAuth = FirebaseAuth.getInstance();
        nameEditText = findViewById(R.id.nameEditText);
        ageEditText = findViewById(R.id.ageEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        sexSpinner = findViewById(R.id.sexSpinner);
        submitButton = findViewById(R.id.submitButton);

        ArrayList<String> sexList = new ArrayList<>();
        sexList.add("Male");
        sexList.add("Female");
        ArrayAdapter<String> sexAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_dropdown_item, sexList);
        sexSpinner.setAdapter(sexAdapter);

        submitButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        if (v == submitButton) {
            String userID = firebaseAuth.getCurrentUser().getUid();
            String name = nameEditText.getText().toString().trim();
            String age = ageEditText.getText().toString();
            //get data from spinner or a radio button
            String description = descriptionEditText.getText().toString().trim();
            HashMap<String, String> typeToData = new HashMap<>();
            typeToData.put("name", name);
            typeToData.put("age", age);
            typeToData.put("description", description);

            firebaseDatabase = FirebaseDatabase.getInstance();
            DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(userID);
            databaseReference.setValue(typeToData).addOnCompleteListener(
                    this, new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Intent goToProfile = new Intent(getApplicationContext(), ProfileActivity.class);
                                finish();
                                startActivity(goToProfile);
                            }
                            else {
                                Toast.makeText(getApplicationContext(),
                                        "unable to set user profile", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }
}