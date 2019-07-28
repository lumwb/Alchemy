package com.nus.alchemy;

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
import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener{

    TextView backToLoginTextView;
    EditText enterEmailEditText;
    Button resetPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);
        backToLoginTextView = (TextView) findViewById(R.id.backToLoginTextView);
        backToLoginTextView.setOnClickListener(this);
        enterEmailEditText = findViewById(R.id.EnterEmailEditText);
        resetPassword= findViewById(R.id.resetButton);
        resetPassword.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v == backToLoginTextView) {
            Intent backToLogin = new Intent(getApplicationContext(), MainActivity.class);
            FirebaseAuth.getInstance().signOut();
            backToLogin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(backToLogin);
            finish();
            return;
        }
        if (v == resetPassword) {
            final String email = enterEmailEditText.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Invalid input for email", Toast.LENGTH_SHORT).show();
                return;
            }
            FirebaseAuth.getInstance().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(getApplicationContext(), "An email has been sent to: " + email, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(intent);
                        finish();
                        return;
                    }
                    else {
                        Toast.makeText(getApplicationContext(), task.getException().toString(), Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
            });
        }
    }
}
