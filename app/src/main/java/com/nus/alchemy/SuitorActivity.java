package com.nus.alchemy;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nus.alchemy.Model.SuitorListAdapter;
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
        initRecyclerView();
        getUserSuitorList();
    }

    private void initRecyclerView() {
        mSuitorList = findViewById(R.id.suitorListRecyclerView);
        mSuitorList.setNestedScrollingEnabled(false);
        mSuitorList.setHasFixedSize(false);
        mSuitorListLayoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayout.VERTICAL, false);
        mSuitorList.setLayoutManager(mSuitorListLayoutManager);
        mSuitorListAdapter = new SuitorListAdapter(suitorList);
        mSuitorList.setAdapter(mSuitorListAdapter);
    }

    private void getUserSuitorList() {
        String groupId = getIntent().getExtras().get("groupId").toString();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("Groups").child(groupId).child("Participants");
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Iterable<DataSnapshot> participants = dataSnapshot.getChildren();
                    for (DataSnapshot suitor : participants) {
                        String name = suitor.getValue().toString();
                        SuitorObject suitorObject = new SuitorObject(name);
                        suitorList.add(suitorObject);
                        mSuitorListAdapter.notifyDataSetChanged();

                    }
//                    for (DataSnapshot participants : dataSnapshot.getChildren()) {
//
//                        String suitorId = participants.getKey().toString();
//                        SuitorObject suitorObject = new SuitorObject(suitorId);
//                        boolean exists = true;
//                        for (SuitorObject mSuitorIterator : suitorList) {
//                            if (mSuitorIterator.getSuitorId().equals(suitorObject.getSuitorId())) {
//                                exists = true;
//                            }
//                        }
//                        if (exists) {
//                            continue;
//                        }
//                        suitorList.add(suitorObject);
//                        mSuitorListAdapter.notifyDataSetChanged();
//                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
