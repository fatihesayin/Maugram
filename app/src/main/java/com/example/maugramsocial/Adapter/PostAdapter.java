package com.example.maugramsocial.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Activity.CommentsActivity;
import com.example.maugramsocial.Fragment.PostDetailsFragment;
import com.example.maugramsocial.Fragment.ProfileFragment;
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
        liked(post.getPostId(), holder.image_like);
        likeCount(holder.txt_Likes, post.getPostId());
        getComments(post.getPostId(), holder.txt_Comments);
        isSaved(post.getPostId(), holder.image_saved);

        holder.image_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (holder.image_like.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(currentFU.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId())
                            .child(currentFU.getUid()).removeValue();

                }

            }
        });
        holder.image_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postID",post.getPostId());
                intent.putExtra("senderID",post.getPostUser());
                mContext.startActivity(intent);
            }
        });
        holder.txt_Comments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, CommentsActivity.class);
                intent.putExtra("postID",post.getPostId());
                intent.putExtra("senderID",post.getPostUser());
                mContext.startActivity(intent);
            }
        });

        holder.profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("id", post.getPostUser());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.action_container,
                        new ProfileFragment()).commit();

            }
        });

        holder.txt_Username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("id", post.getPostUser());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.action_container,
                        new ProfileFragment()).commit();

            }
        });

        holder.txt_Sender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("id", post.getPostUser());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.action_container,
                        new ProfileFragment()).commit();

            }
        });

        holder.post_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("postId", post.getPostUser());
                editor.apply();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.action_container,
                        new PostDetailsFragment()).commit();

            }
        });

        holder.image_saved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (holder.image_saved.getTag().equals("save")){
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(currentFU.getUid()).child(post.getPostId()).setValue(true);
                }
                else
                    FirebaseDatabase.getInstance().getReference().child("Saves").child(currentFU.getUid()).child(post.getPostId()).removeValue();
            }
        });
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
    private void getComments(String postID,TextView comments){
        DatabaseReference referenceGettingComments = FirebaseDatabase.getInstance().getReference("Comments").child(postID);
        referenceGettingComments.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                comments.setText("See all "+ snapshot.getChildrenCount()+" comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void liked(String postId, ImageView imageView){
        FirebaseUser currentFU = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference likePath = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);

        likePath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(currentFU.getUid()).exists()){
                    imageView.setImageResource(R.drawable.ic_like_full);
                    imageView.setTag("liked");
                }
                else {
                    imageView.setImageResource(R.drawable.ic_like_border);
                    imageView.setTag("like");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void likeCount(TextView likes, String postId){
        DatabaseReference likePath = FirebaseDatabase.getInstance().getReference()
                .child("Likes")
                .child(postId);

        likePath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                likes.setText(snapshot.getChildrenCount()+" like");

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
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
    private void isSaved(String postID,ImageView imageView){
        FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Saves").child(fUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.child(postID).exists()){
                    imageView.setImageResource(R.drawable.ic_save_black);
                    imageView.setTag("saved");
                }
                else{
                    imageView.setImageResource(R.drawable.ic_saved);
                    imageView.setTag("save");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
