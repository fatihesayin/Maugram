package com.example.maugramsocial.Adapter;

import android.content.Context;
import android.content.Intent;
import android.renderscript.ScriptGroup;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Activity.MessageActivity;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.example.maugramsocial.databinding.DialogElementBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatUsersAdapter extends RecyclerView.Adapter<ChatUsersAdapter.ChatUsersViewHolder>{

    Context mContext;
    ArrayList<User> mUsers;
    DialogElementBinding binding;


    public ChatUsersAdapter(Context mContext, ArrayList<User> mUsers) {

        this.mContext=mContext;
        this.mUsers=mUsers;

    }

    @NonNull
    @Override
    public ChatUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_element, parent, false);

        return new ChatUsersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatUsersViewHolder holder, int position) {
        User user = mUsers.get(position);

        String senderId = FirebaseAuth.getInstance().getUid();

        String senderRoom = senderId + user.getId();

        FirebaseDatabase.getInstance().getReference()
                        .child("Chats")
                        .child(senderRoom)
                                .addValueEventListener(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        if (snapshot.exists()) {
                                            String lastMsg = snapshot.child("lastMsg").getValue(String.class);
                                            long time = snapshot.child("lastMsgTime").getValue(Long.class);
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
                                            holder.time.setText(dateFormat.format(new Date(time)));
                                            holder.message.setText(lastMsg);
                                        }
                                        else {
                                            holder.time.setVisibility(View.GONE);
                                            holder.message.setText("Tap to chat");
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });

        holder.userName.setText(user.getUserName());

        Glide.with(mContext).load(user.getPhotoURL()).into(holder.profilePhotoElement);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                intent.putExtra("userName", user.getUserName());
                intent.putExtra("id", user.getId());
                mContext.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public static class ChatUsersViewHolder extends RecyclerView.ViewHolder{
        public TextView userName, message, time;
        public CircleImageView profilePhotoElement;

        public ChatUsersViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.dialog_element_username);
            message = itemView.findViewById(R.id.dialog_element_message);
            time = itemView.findViewById(R.id.dialog_element_time);
            profilePhotoElement = itemView.findViewById(R.id.dialog_element_profilePhoto);

        }
    }
}
