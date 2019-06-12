package com.nus.alchemy;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class ProfileActivity extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth firebaseAuth;
    TextView logoutTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        logoutTextView = (TextView) findViewById(R.id.logoutTextView);
        logoutTextView.setOnClickListener(this);


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
    }
}
