package com.nus.alchemy.Model;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nus.alchemy.R;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private List<EventObject> eventObjectList;

    public EventAdapter(List<EventObject> eventList) {
        this.eventObjectList = eventList;
    }

    @Override
    public int getItemCount() {
        return eventObjectList.size();
    }

    @Override
    public void onBindViewHolder(EventViewHolder contactViewHolder, int i) {
        EventObject ci = eventObjectList.get(i);
        contactViewHolder.vName.setText(ci.getCreatorName());
        contactViewHolder.vPreferredSex.setText(ci.getPreferredSex());
        contactViewHolder.vStartTime.setText(ci.getStartTime());
        contactViewHolder.vMaxRoomSize.setText(String.valueOf(ci.getMaxRoomSize()));
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView = LayoutInflater.
                from(viewGroup.getContext()).
                inflate(R.layout.event_card, viewGroup, false);

        return new EventViewHolder(itemView);
    }

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        protected TextView vName;
        protected TextView vPreferredSex;
        protected TextView vStartTime;
        protected TextView vMaxRoomSize;

        public EventViewHolder(View v) {
            super(v);
            vName =  (TextView) v.findViewById(R.id.txtName);
            vPreferredSex = (TextView)  v.findViewById(R.id.txtPreferredSex);
            vStartTime = (TextView)  v.findViewById(R.id.txtStartTime);
            vMaxRoomSize = (TextView) v.findViewById(R.id.txtMaxRoomSize);
        }
    }
}
