package com.example.testfinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessagesHolder>{
    private ArrayList<Messages> mMessagesList;

    public static class MessagesHolder extends RecyclerView.ViewHolder {
        public TextView mTextView;

        public MessagesHolder(View itemView) {
            super(itemView);
            mTextView = itemView.findViewById(R.id.r);
        }
    }

    public MessagesAdapter(ArrayList<Messages> mMessagesList) {this.mMessagesList = mMessagesList;}
    @NonNull
    @Override
    public MessagesAdapter.MessagesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.reviews, parent, false);
        MessagesHolder mh = new MessagesHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(@NonNull MessagesAdapter.MessagesHolder holder, int position) {
        Messages currMessage = mMessagesList.get(position);
        holder.mTextView.setText(String.valueOf(currMessage.getMessage()));
    }

    @Override
    public int getItemCount() {
        return mMessagesList.size();
    }
}
