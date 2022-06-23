package com.example.maugramsocial.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Activity.EditProfileActivity;
import com.example.maugramsocial.Activity.FollowersActivity;
import com.example.maugramsocial.Activity.OptionsActivity;
import com.example.maugramsocial.Adapter.MyPhotoAdapter;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView imgOptions, profilePhoto;
    TextView txt_Posts, txt_Followers, txt_Followings, txt_Name, txt_Bio, txt_Username;
    Button btn_Edit_Profile;
    ImageButton imgbtn_MyPhotos, imgbtn_SavedPosts;


    FirebaseUser fUser;
    String profileId;

    RecyclerView recyclerViewPhotos;
    MyPhotoAdapter myPhotoAdapter;
    List<Post> postList;

    private List<String> mySaves;
    RecyclerView recyclerViewSaves;
    MyPhotoAdapter myPhotoAdapterSaves;
    List<Post> postListSaves;

    public ProfileFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        fUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("id", "none");


        //Showing posts in profile fragment
        recyclerViewPhotos = view.findViewById(R.id.recycler_view_profileFragment);
        recyclerViewPhotos.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerViewPhotos.setLayoutManager(linearLayoutManager);
        postList= new ArrayList<>();
        myPhotoAdapter = new MyPhotoAdapter(getContext(),postList);
        recyclerViewPhotos.setAdapter(myPhotoAdapter);

        imgOptions = view.findViewById(R.id.options_profileFragment);
        profilePhoto = view.findViewById(R.id.profile_photo_profileFragment);

        txt_Posts = view.findViewById(R.id.txt_posts_profileFragment);
        txt_Followers = view.findViewById(R.id.txt_followers_profileFragment);
        txt_Followings = view.findViewById(R.id.txt_following_profileFragment);
        txt_Bio = view.findViewById(R.id.txt_bio_profileFragment);
        txt_Name= view.findViewById(R.id.txt_name_profileFragment);
        txt_Username= view.findViewById(R.id.txt_username_profileFragment);

        btn_Edit_Profile = view.findViewById(R.id.btn_editProfile_profileFragment);
        imgbtn_MyPhotos = view.findViewById(R.id.imgbtn_photos_profileFragment);
        imgbtn_SavedPosts = view.findViewById(R.id.imgbtn_savedPhotos_profileFragment);

        //Saving posts seen in profile fragment
        recyclerViewSaves = view.findViewById(R.id.recycler_view_saved_profileFragment);
        recyclerViewSaves.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManagerSaves = new GridLayoutManager(getContext(),3);
        recyclerViewSaves.setLayoutManager(linearLayoutManagerSaves);
        postListSaves= new ArrayList<>();
        myPhotoAdapterSaves = new MyPhotoAdapter(getContext(),postListSaves);
        recyclerViewSaves.setAdapter(myPhotoAdapterSaves);
        recyclerViewPhotos.setVisibility(View.VISIBLE);
        recyclerViewSaves.setVisibility(View.GONE);
        userInfo();
        followCount();
        postCount();
        myPhotos();
        mySaves();

        if (profileId.equals(fUser.getUid())){
            btn_Edit_Profile.setText("Edit Profile");
        }
        else
        {
            followControl();
            imgbtn_SavedPosts.setVisibility(View.GONE);
        }

        txt_Followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);

                intent.putExtra("id", profileId);
                intent.putExtra("title", "Followers");
                startActivity(intent);
            }
        });


        txt_Followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), FollowersActivity.class);

                intent.putExtra("id", profileId);
                intent.putExtra("title", "Following");
                startActivity(intent);
            }
        });

        btn_Edit_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = btn_Edit_Profile.getText().toString();

                if (btn.equals("Edit Profile")){
                    startActivity(new Intent(getContext(),EditProfileActivity.class));
                }
                else if (btn.equals("Follow")){
                    //takip et
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("Following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("Follower").child(fUser.getUid()).setValue(true);
                }
                else if (btn.equals("Following")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("Following").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId)
                            .child("Follower").child(fUser.getUid()).removeValue();
                }

            }
        });
        imgOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToOptions = new Intent(getContext(), OptionsActivity.class);
                startActivity(intentToOptions);
            }
        });
        imgbtn_MyPhotos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewPhotos.setVisibility(View.VISIBLE);
                recyclerViewSaves.setVisibility(View.GONE);
            }
        });
        imgbtn_SavedPosts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recyclerViewPhotos.setVisibility(View.GONE);
                recyclerViewSaves.setVisibility(View.VISIBLE);
            }
        });
        return view;

    }
    private void userInfo(){
        DatabaseReference userPath = FirebaseDatabase.getInstance().getReference("Users").child(profileId);

        userPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (getContext() == null){
                    return;
                }
                User user = snapshot.getValue(User.class);

                try {
                    Glide.with(getContext()).load(user.getPhotoURL()).into(profilePhoto);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    txt_Username.setText(user.getUserName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    txt_Name.setText(user.getFullName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    txt_Bio.setText(user.getBio());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void followControl(){
        DatabaseReference followPath = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(fUser.getUid()).child("Following");

        followPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileId).exists()){
                    btn_Edit_Profile.setText("Following");
                }
                else {
                    btn_Edit_Profile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void followCount() {

        //Follower Count
        DatabaseReference followerPath = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileId).child("Follower");

        followerPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt_Followers.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Following Count
        DatabaseReference followingPath = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(profileId).child("Following");

        followingPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txt_Followings.setText(""+snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void postCount(){
        DatabaseReference postPath = FirebaseDatabase.getInstance().getReference("Posts");

        postPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i = 0;
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);

                    if (post.getPostUser().equals(profileId)){
                        i++;
                    }
                }
                txt_Posts.setText(""+i);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void myPhotos(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snapshot3:snapshot.getChildren()){
                    Post post = snapshot3.getValue(Post.class);
                    if (post.getPostUser().equals(profileId)){
                        postList.add(post);
                    }
                }
                Collections.reverse(postList);
                myPhotoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void mySaves(){
        mySaves = new ArrayList<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Saves").child(fUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    mySaves.add(dataSnapshot.getKey());
                }
                readSaves();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void readSaves(){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postListSaves.clear();
                for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                    Post post = dataSnapshot.getValue(Post.class);
                    for (String id:mySaves){
                        if (post.getPostId().equals(id))
                            postListSaves.add(post);
                    }
                }
                myPhotoAdapterSaves.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}