package com.nus.alchemy.Model;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nus.alchemy.ChatActivity;
import com.nus.alchemy.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.LinkedList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> implements Filterable {

    private ArrayList<ChatObject> chatList;
    private ArrayList<ChatObject> chatListFull;

    public ChatListAdapter(ArrayList<ChatObject> chatList) {
        this.chatList = chatList;
        this.chatListFull = new ArrayList<>(chatList);
    }

    @Override
    public Filter getFilter() {
        return stockFilter;
    }

    private Filter stockFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            LinkedList<ChatObject> filteredList = new LinkedList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(chatListFull);
            }
            else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ChatObject item: chatListFull) {
                    if (item.getOtherUserName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            chatList.clear();
            chatList.addAll((LinkedList<ChatObject>) results.values);
            notifyDataSetChanged();
        }
    };

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat, null, false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatListViewHolder cvh = new ChatListViewHolder(layoutView);
        return cvh;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {
        final String otherUserName = chatList.get(position).getOtherUserName();
        final String otherUserProfImg = chatList.get(position).getOtherUserProfileImage();

        holder.mTitle.setText(otherUserName);
        holder.mLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID", chatList.get(holder.getAdapterPosition()).getChatID());
                bundle.putString("otherUserName", otherUserName);
                bundle.putString("otherUserProfileImg", otherUserProfImg);
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
        Picasso.get().load(otherUserProfImg).placeholder(R.drawable.icon).into(holder.mChatProfileImage);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }


    public class ChatListViewHolder extends RecyclerView.ViewHolder{
        public TextView mTitle;
        public LinearLayout mLayout;
        public CircleImageView mChatProfileImage;
        public ChatListViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.titleTextView);
            mLayout = view.findViewById(R.id.layout);
            mChatProfileImage = view.findViewById(R.id.chatProfileImage);
        }
    }
}
