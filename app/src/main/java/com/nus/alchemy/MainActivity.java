 package com.nus.alchemy;

 import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


 public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView registrationPageTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private TextView forgotPasswordTextView;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initAttributes();
        if (firebaseAuth.getCurrentUser() != null) {
            Intent goToProfileActivity = new Intent(getApplicationContext(), ProfileActivity.class);
            finish();
            startActivity(goToProfileActivity);
        }
        registrationPageTextView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        forgotPasswordTextView.setOnClickListener(this);
    }

     @Override
     public void onClick(View v) {
         if (v == registrationPageTextView) {
             Intent goToRegistrationPage = new Intent(this, RegistrationActivity.class);
             finish();
             startActivity(goToRegistrationPage);
         }
         if (v == loginButton) {
             userLogin();
         }
         if (v == forgotPasswordTextView) {
             Intent intent = new Intent(this, ForgotActivity.class);
             startActivity(intent);
             finish();
             return;
         }
     }

     private void userLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "incomplete user details", Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setMessage("Wait for Login");
        progressDialog.show();
        signIn(email, password);
     }

     private void signIn(String email, String password) {
         firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                 this, new OnCompleteListener<AuthResult>() {
                     @Override
                     public void onComplete(@NonNull Task<AuthResult> task) {
                         progressDialog.dismiss();
                         if (task.isSuccessful()) {
                             Intent goToProfilePage = new Intent(getApplicationContext(), ProfileActivity.class);
                             finish();
                             startActivity(goToProfilePage);
                         }
                         else {
                             Toast.makeText(getApplicationContext(), "unable to sign in.", Toast.LENGTH_SHORT).show();
                         }
                     }
                 }
         );
     }

     private void initAttributes() {
         registrationPageTextView = (TextView) findViewById(R.id.registrationPageTextView);
         registrationPageTextView.setPaintFlags(registrationPageTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
         emailEditText = (EditText) findViewById(R.id.emailEditText);
         passwordEditText = (EditText) findViewById(R.id.passwordEditText);
         loginButton = (Button) findViewById(R.id.loginButton);
         forgotPasswordTextView = (TextView) findViewById(R.id.forgotPasswordTextView);
         forgotPasswordTextView.setPaintFlags(forgotPasswordTextView.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
         progressDialog = new ProgressDialog(this);
         firebaseAuth = FirebaseAuth.getInstance();
     }
 }
