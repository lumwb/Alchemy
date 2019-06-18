package com.nus.alchemy;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText nameEditText;
    private EditText ageEditText;
    private RadioGroup sexRadioGroup;
    private Button registerButton;
    private TextView loginPageTextView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        initAttributes();
        registerButton.setOnClickListener(this);
        loginPageTextView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == registerButton) {
            registerUser();

        }
        if (v == loginPageTextView) {
            Intent goToLoginPage = new Intent(this, MainActivity.class);
            startActivity(goToLoginPage);
            finish();
        }
    }

    private void initAttributes() {
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        nameEditText = (EditText) findViewById(R.id.nameEditText);
        ageEditText = (EditText) findViewById(R.id.ageEditText);
        sexRadioGroup = (RadioGroup) findViewById(R.id.sexRadioGroup);
        registerButton = (Button) findViewById(R.id.registerButton);
        loginPageTextView = (TextView) findViewById(R.id.loginPageTextView);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        final String name = nameEditText.getText().toString().trim();
        final String age = ageEditText.getText().toString().trim();
        int radioButtonID = sexRadioGroup.getCheckedRadioButtonId();
        RadioButton rb = (RadioButton) findViewById(radioButtonID);
        final String sex = rb.getText().toString();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name) || TextUtils.isEmpty(sex)) {
            Toast.makeText(this, "User details are incomplete.", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Registering...");
        progressDialog.show();
        createAccount(email, password, name, age, sex);
    }

    private void createAccount(String email, String password, final String name, final String age, final String sex) {
        firebaseAuth.createUserWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressDialog.dismiss();
                            String userID = firebaseAuth.getCurrentUser().getUid();
                            createUserDb(userID, name, age, sex);
                            Intent goToAboutPage = new Intent(getApplicationContext(), ProfileActivity.class);
                            startActivity(goToAboutPage);
                            finish();
                        }
                        else {
                            Toast.makeText(getApplicationContext(), "failed to register", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    //check here
    private void createUserDb(String userID, String name, String age, String sex) {
        firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference().child("Users").child(userID);
        databaseReference.child("Name").setValue(name);
        databaseReference.child("Age").setValue(age);
        databaseReference.child("Sex").setValue(sex);
        databaseReference.child("Story").setValue("");
    }
}