package com.example.volaan.Adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.volaan.Main;
import com.example.volaan.Models.Post;
import com.example.volaan.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class NewPostAdapter extends RecyclerView.Adapter<NewPostAdapter.ViewHolder> {

    private List<Post> postList=new ArrayList<>();
    private DatabaseReference db;
    private Main activity;

    public NewPostAdapter(DatabaseReference db, Main activity) {
        this.db = db;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_layout, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post item = postList.get(position);
        holder.textpost.setText(item.getText());
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        DateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());

        holder.timepost.setText(timeFormat.format(item.getTime())+" "+dateFormat.format(item.getTime()));

        switch(item.getType()){

            case "Bad": holder.relativepost.setBackgroundColor(activity.getResources().getColor(R.color.red)); break;
            case "Okay": holder.relativepost.setBackgroundColor(activity.getResources().getColor(R.color.orange)); break;
            case "Good": holder.relativepost.setBackgroundColor(activity.getResources().getColor(R.color.yellow)); break;
            case "Excellent": holder.relativepost.setBackgroundColor(activity.getResources().getColor(R.color.green)); break;
            case "Task": holder.relativepost.setBackgroundColor(activity.getResources().getColor(R.color.blue)); break;
            default: break;

        }


    }


    @Override
    public int getItemCount() {
        return postList.size();
    }

    public Context getContext() {
        return activity;
    }

    public void setPosts(List<Post> postList) {
        this.postList = postList;
        notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textpost;
        TextView timepost;
        RelativeLayout relativepost;

        ViewHolder(View view) {
            super(view);
            textpost = view.findViewById(R.id.TextPost);
            timepost = view.findViewById(R.id.TimePost);
            relativepost = view.findViewById(R.id.RelativePost);
        }
    }
}

