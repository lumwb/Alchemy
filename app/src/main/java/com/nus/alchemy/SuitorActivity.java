package com.nus.alchemy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nus.alchemy.Model.SuitorObject;

import java.util.ArrayList;

public class SuitorActivity extends AppCompatActivity {

    private RecyclerView mSuitorList;
    private RecyclerView.Adapter mSuitorListAdapter;
    private RecyclerView.LayoutManager mSuitorListLayoutManager;
    ArrayList<SuitorObject> suitorList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suitor);

        suitorList = new ArrayList<>();
        getUserSuitorList();
    }

    private void getUserSuitorList() {
        String groupId = getIntent().getExtras().get("groupId").toString();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("participants");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot particpants : dataSnapshot.getChildren()) {
                        String suitorId = particpants.getKey().toString();
                        SuitorObject suitorObject = new SuitorObject(suitorId);
                        boolean exists = true;
//                        for (SuitorObject mSuitorIterator : suitorList) {
//                            if (mSuitorIterator.getSuitorId().equals(suitorObject.getSuitorId())) {
//                                exists = true;
//                            }
//                        }
//                        if (exists) {
//                            continue;
//                        }
                        suitorList.add(suitorObject);
                        mSuitorListAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
