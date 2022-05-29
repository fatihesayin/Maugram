package com.example.maugramsocial.Adapter;

import android.content.Context;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Model.Post;
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

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context mContext;
    public List<Post> mPosts;

    private FirebaseUser currentFU;

    public PostAdapter(Context mContext, List<Post> mPosts) {
        this.mContext = mContext;
        this.mPosts = mPosts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.post_element, parent, false);

        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        currentFU = FirebaseAuth.getInstance().getCurrentUser();

        Post post = mPosts.get(position);

        Glide.with(mContext).load(post.getPostImage()).into(holder.post_photo);

        if (post.getPostAbout().equals("")){
            holder.txt_Postabout.setVisibility(View.GONE);
        }
        else {
            holder.txt_Postabout.setVisibility(View.VISIBLE);
            holder.txt_Postabout.setText(post.getPostAbout());
        }

        senderInfo(holder.profile_photo,holder.txt_Username,holder.txt_Sender, post.getPostUser());
    }

    @Override
    public int getItemCount() {
        return mPosts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profile_photo, post_photo, image_like, image_comment, image_saved;

        public TextView txt_Username, txt_Likes, txt_Postabout, txt_Comments, txt_Sender;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profile_photo = itemView.findViewById(R.id.profile_photo_Post_Element);
            post_photo = itemView.findViewById(R.id.post_photo_Post_Element);
            image_like = itemView.findViewById(R.id.like_Post_Element);
            image_comment = itemView.findViewById(R.id.comment_Post_Element);
            image_saved = itemView.findViewById(R.id.saved_Post_Element);

            txt_Username = itemView.findViewById(R.id.txt_username_Post_Element);
            txt_Postabout = itemView.findViewById(R.id.txt_postabout_Post_Element);
            txt_Sender = itemView.findViewById(R.id.txt_sender_Post_Element);
            txt_Likes= itemView.findViewById(R.id.txt_likes_Post_Element);
            txt_Comments=itemView.findViewById(R.id.txt_comments_Post_Element);
        }
    }

    private void senderInfo(ImageView profile_photo, TextView username, TextView sender, String id){
        DatabaseReference dataPath= FirebaseDatabase.getInstance().getReference("Users").child(id);

        dataPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);

                Glide.with(mContext).load(user.getPhotoURL()).into(profile_photo);
                username.setText(user.getUserName());
                sender.setText(user.getUserName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
