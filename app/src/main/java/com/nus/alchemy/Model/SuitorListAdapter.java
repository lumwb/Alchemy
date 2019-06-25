package com.nus.alchemy.Model;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nus.alchemy.MatchActivity;
import com.nus.alchemy.R;

import java.util.ArrayList;

public class SuitorListAdapter extends RecyclerView.Adapter<SuitorListAdapter.SuitorListViewHolder>{

    private ArrayList<SuitorObject> suitorList;

    public SuitorListAdapter(ArrayList<SuitorObject> suitorList) {
        this.suitorList = suitorList;
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
        final String suitorName = suitorList.get(position).getSuitorName();
        holder.mSuitor.setText(suitorName);
        holder.mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), MatchActivity.class);
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
        public LinearLayout mSuitorLayout;
        public SuitorListViewHolder(View view) {
            super(view);
            mSuitor = view.findViewById(R.id.suitorName);
            mSuitorLayout = view.findViewById(R.id.suitorItemLayout);
            mButton = view.findViewById(R.id.selectSuitor);
        }
    }
}
