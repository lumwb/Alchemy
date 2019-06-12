package com.nus.alchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ForgotActivity extends AppCompatActivity implements View.OnClickListener{

    TextView backToLoginTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot);

        backToLoginTextView = (TextView) findViewById(R.id.backToLoginTextView);
        backToLoginTextView.setOnClickListener(this);
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
    }
}
