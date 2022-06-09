package com.example.maugramsocial.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Activity.TimelineActivity;
import com.example.maugramsocial.Model.Comment;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Objects;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{

    private Context mContext;
    private List<Comment> mCommentList;
    private FirebaseUser fUser;

    public CommentAdapter(Context mContext, List<Comment> mCommentList) {
        this.mContext = mContext;
        this.mCommentList = mCommentList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.comment_element,parent,false);
        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        final Comment comment = mCommentList.get(position);
        holder.txtComment.setText(comment.getComment());
        getUserInformation(holder.profilePhoto, holder.txtUsername, comment.getSender());

        holder.txtComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TimelineActivity.class);
                intent.putExtra("senderID",comment.getSender());
                mContext.startActivity(intent);
            }
        });
        holder.profilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, TimelineActivity.class);
                intent.putExtra("senderID",comment.getSender());
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mCommentList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profilePhoto;
        public TextView txtUsername,txtComment;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profilePhoto = itemView.findViewById(R.id.profilePhotoCommentElement);
            txtUsername = itemView.findViewById(R.id.txtUsernameCommentElement);
            txtComment = itemView.findViewById(R.id.txtCommentCommentElement);
        }
    }
    private void getUserInformation(final ImageView imageView,final TextView userName, String senderID){
        DatabaseReference senderReference = FirebaseDatabase.getInstance().getReference().child("Users").child(senderID);
        senderReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(mContext).load(Objects.requireNonNull(user).getPhotoURL()).into(imageView);
                userName.setText(user.getFullName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
