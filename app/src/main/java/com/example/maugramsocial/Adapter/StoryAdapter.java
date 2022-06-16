package com.example.maugramsocial.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Activity.AddStoryActivity;
import com.example.maugramsocial.Activity.StoryActivity;
import com.example.maugramsocial.Model.Story;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.ViewHolder>{

    private Context mContext;
    private List<Story> mStory;

    public StoryAdapter(Context mContext, List<Story> mStory){
        this.mContext=mContext;
        this.mStory =mStory;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType==0){
            View view = LayoutInflater.from(mContext).inflate(R.layout.add_story_item,parent, false);
            return new StoryAdapter.ViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(mContext).inflate(R.layout.story_item,parent, false);
            return new StoryAdapter.ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Story story = mStory.get(position);
        userInfo(holder, story.getUserId(),position);
        if (holder.getAdapterPosition() != 0){
            storySeen(holder, story.getUserId());
        }
        if (holder.getAdapterPosition() == 0){
            myStory(holder.addStoryText, holder.storyPlus, false);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.getAdapterPosition() == 0){
                    myStory(holder.addStoryText, holder.storyPlus, true);
                }
                else{
                    Intent intent = new Intent(mContext, StoryActivity.class);
                    intent.putExtra("storyId",story.getUserId());
                    mContext.startActivity(intent);

                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mStory.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView storyPhoto, storySeen, storyPlus;
        public TextView storyUsername, addStoryText;

        public ViewHolder(@NonNull View itemView){
            super(itemView);

            storyPhoto = itemView.findViewById(R.id.story_photo);
            storySeen = itemView.findViewById(R.id.story_photo_seen);
            storyPlus = itemView.findViewById(R.id.story_plus);
            storyUsername = itemView.findViewById(R.id.story_username);
            addStoryText = itemView.findViewById(R.id.addStoryText);


        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position==0){
            return 0;
        }
        return 1;

    }
    private void userInfo(ViewHolder viewHolder,String userId, int position){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(user.getPhotoURL()).into(viewHolder.storyPhoto);
                if (position!=0){
                    Glide.with(mContext).load(user.getPhotoURL()).into(viewHolder.storySeen);
                    viewHolder.storyUsername.setText(user.getUserName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void myStory(TextView textView, ImageView imageView, Boolean click){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Story")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int count=0;
                long timeCurrent = System.currentTimeMillis();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Story story = dataSnapshot.getValue(Story.class);

                    if (timeCurrent > story.getTimeStart() && timeCurrent < story.getTimeEnd()){
                        count++;
                    }
                }
                if (click){
                    if (count>0){
                        AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "View Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mContext, StoryActivity.class);
                                        intent.putExtra("storyId",
                                                FirebaseAuth.getInstance().getCurrentUser().getUid());
                                        Log.i("msg", String.valueOf(intent));
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                  });

                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Add Story",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                                        mContext.startActivity(intent);
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                    else {
                        Intent intent = new Intent(mContext, AddStoryActivity.class);
                        mContext.startActivity(intent);
                    }

                }
                else {
                    if (count > 0){
                        textView.setText("My Story");
                        imageView.setVisibility(View.GONE);
                    }else {
                        textView.setText("Add Story");
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void storySeen(ViewHolder viewHolder, String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Story").child(userId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i =0;
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    if (!snapshot.child("views").child(FirebaseAuth.getInstance().getCurrentUser().getUid()
                    ).exists() && System.currentTimeMillis() < snapshot.getValue(Story.class).getTimeEnd()){
                        i++;
                    }
                }
                if (i>0){
                    viewHolder.storyPhoto.setVisibility(View.VISIBLE);
                    viewHolder.storySeen.setVisibility(View.GONE);
                }
                else {
                    viewHolder.storyPhoto.setVisibility(View.GONE);
                    viewHolder.storySeen.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
