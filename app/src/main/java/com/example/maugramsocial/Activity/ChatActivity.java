package com.example.maugramsocial.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.util.Log;

import com.example.maugramsocial.Adapter.ChatUsersAdapter;
import com.example.maugramsocial.Adapter.UserAdapter;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.example.maugramsocial.databinding.ActivityChatBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    ArrayList<User> mUsers;
    ChatUsersAdapter chatUserAdapter;

    ActivityChatBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mUsers = new ArrayList<>();
        chatUserAdapter = new ChatUsersAdapter(this, mUsers);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        binding.chatActivityRecycler.setLayoutManager(linearLayoutManager);
        binding.chatActivityRecycler.setAdapter(chatUserAdapter);

        FirebaseDatabase.getInstance().getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    mUsers.add(user);
                }
                chatUserAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}