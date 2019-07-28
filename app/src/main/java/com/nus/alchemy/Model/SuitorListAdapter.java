package com.nus.alchemy.Model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.nus.alchemy.MatchActivity;
import com.nus.alchemy.R;

import java.util.ArrayList;

public class SuitorListAdapter extends RecyclerView.Adapter<SuitorListAdapter.SuitorListViewHolder>{

    private ArrayList<SuitorObject> suitorList;
    private String eventID;

    public SuitorListAdapter(ArrayList<SuitorObject> suitorList, String eventID) {
        this.suitorList = suitorList;
        this.eventID = eventID;
    }

    @NonNull
    @Override
    public SuitorListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_suitor, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        SuitorListViewHolder cvh = new SuitorListViewHolder(layoutView);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final SuitorListViewHolder holder, final int position) {
        final String suitorName = suitorList.get(position).getSuitorName(); //for now, should change to id
        final String suitorId = suitorList.get(position).getSuitorId();
        holder.mSuitor.setText(suitorName);
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MatchActivity.class);
                String user1 = FirebaseAuth.getInstance().getUid();
                String user2 = suitorId;
                intent.putExtra("user1", user1);
                intent.putExtra("user2", user2);
                intent.putExtra("eventID", eventID);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return suitorList.size();
    }


    public class SuitorListViewHolder extends RecyclerView.ViewHolder{
        public TextView mSuitor;
        public Button mButton;
        public ConstraintLayout mSuitorLayout;
        public SuitorListViewHolder(View view) {
            super(view);
            mSuitor = view.findViewById(R.id.suitorName);
            mSuitorLayout = view.findViewById(R.id.suitorItemLayout);
            mButton = view.findViewById(R.id.selectSuitor);
        }
    }
}
