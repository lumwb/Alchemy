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
 import android.widget.TextView;
 import android.widget.Toast;

 import com.google.android.gms.tasks.OnCompleteListener;
 import com.google.android.gms.tasks.Task;
 import com.google.firebase.auth.AuthResult;
 import com.google.firebase.auth.FirebaseAuth;

//the login Activity

 public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private TextView registrationPageTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        registrationPageTextView = (TextView) findViewById(R.id.registrationPageTextView);
        emailEditText = (EditText) findViewById(R.id.emailEditText);
        passwordEditText = (EditText) findViewById(R.id.passwordEditText);
        loginButton = (Button) findViewById(R.id.loginButton);
        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            //user already logged in
            //go to Profile if already registered properly, else go to the requirements page
        }

        registrationPageTextView.setOnClickListener(this);
        loginButton.setOnClickListener(this);
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

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(
                this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        progressDialog.dismiss();
                        if (task.isSuccessful()) {

                        }
                        else {
                            Toast.makeText(getApplicationContext(), "invalid account", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
     }

 }
