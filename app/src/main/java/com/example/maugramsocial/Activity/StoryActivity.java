package com.example.maugramsocial.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Model.Story;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import jp.shts.android.storiesprogressview.StoriesProgressView;

public class StoryActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener {

    int counter=0;
    long pressTime=0L;
    long limit = 500L;
    ImageView image, story_photo;
    TextView story_username;

    StoriesProgressView storiesProgressView;

    LinearLayout story_seen;
    TextView seen_number;
    ImageView story_delete;

    List<String> images;
    List<String> storyIds;
    String userId;
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()){
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgressView.pause();
                    return false;
                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgressView.resume();
                    return limit < now-pressTime;
            }
        return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story);

        story_seen = findViewById(R.id.seen_activity_story);
        seen_number = findViewById(R.id.story_seen_number);
        story_delete = findViewById(R.id.story_delete);

        storiesProgressView = findViewById(R.id.stories);
        image = findViewById(R.id.image_activity_story);
        story_photo = findViewById(R.id.story_photo_story_activity);
        story_username = findViewById(R.id.story_username_story_activity);

        story_seen.setVisibility(View.GONE);
        story_delete.setVisibility(View.GONE);

        userId = getIntent().getStringExtra("storyId");
        if (userId.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())){
            story_seen.setVisibility(View.VISIBLE);
            story_delete.setVisibility(View.VISIBLE);
        }
        getStories(userId);
        userInfo(userId);

        View reverse  = findViewById(R.id.reverse);
        reverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.reverse();
            }
        });
        reverse.setOnTouchListener(onTouchListener);
        View skip  = findViewById(R.id.skip);
        skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgressView.skip();
            }
        });
        reverse.setOnTouchListener(onTouchListener);

        story_seen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StoryActivity.this, FollowersActivity.class);
                intent.putExtra("id",userId);
                intent.putExtra("storyId", storyIds.get(counter));
                intent.putExtra("title","Views");
                startActivity(intent);
            }
        });

        story_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatabaseReference reference = FirebaseDatabase
                        .getInstance().getReference("Story").child(userId).child(storyIds.get(counter));
                reference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(StoryActivity.this, "Story Deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onNext() {
        Glide.with(getApplicationContext()).load(images.get(++counter)).into(image);

        addView(storyIds.get(counter));
        seenNumber(storyIds.get(counter));
    }

    @Override
    public void onPrev() {
        if ((counter-1)<0 ){
            return;
        }
        Glide.with(getApplicationContext()).load(images.get(--counter)).into(image);
        seenNumber(storyIds.get(counter));
    }

    @Override
    protected void onDestroy() {
        storiesProgressView.destroy();
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        storiesProgressView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        storiesProgressView.resume();
        super.onResume();
    }

    @Override
    public void onComplete() {
        finish();
    }

    private void getStories (String userId){
        images = new ArrayList<>();
        storyIds = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Story").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                images.clear();
                storyIds.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Story story = dataSnapshot.getValue(Story.class);
                    long timeCurrent = System.currentTimeMillis();
                    if(timeCurrent > story.getTimeStart()&&timeCurrent< story.getTimeEnd()){
                        images.add(story.getImageUrl());
                        storyIds.add(story.getStoryId());
                    }
                }
                storiesProgressView.setStoriesCount(images.size());
                storiesProgressView.setStoryDuration(5000L);
                storiesProgressView.setStoriesListener(StoryActivity.this);
                storiesProgressView.startStories(counter);
                Glide.with(getApplicationContext()).load(images.get(counter)).into(image);

                addView(storyIds.get(counter));
                seenNumber(storyIds.get(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void userInfo(String userId){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Users").child(userId);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getPhotoURL()).into(story_photo);
                story_username.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void addView(String storyId){
        FirebaseDatabase.getInstance().getReference("Story").child(userId)
                .child(storyId).child("Views")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(true);

    }
    private void seenNumber(String storyId){
        DatabaseReference reference = FirebaseDatabase.getInstance()
                .getReference("Story").child(userId).child(storyId).child("Views");
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                seen_number.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}