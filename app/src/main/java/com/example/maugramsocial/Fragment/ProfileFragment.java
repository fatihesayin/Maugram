package com.example.maugramsocial.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

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

public class ProfileFragment extends Fragment {

    ImageView imgOptions, profilePhoto;

    TextView txt_Posts, txt_Followers, txt_Followings, txt_Name, txt_Bio, txt_Username;

    Button btn_Edit_Profile;

    ImageButton imgbtn_MyPhotos, imgbtn_SavedPosts;

    FirebaseUser fUser;
    String profileId;

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

        userInfo();
        followCount();
        postCount();

        if (profileId.equals(fUser.getUid())){

            btn_Edit_Profile.setText("Edit Profile");
        }
        else
        {
            followControl();
            imgbtn_SavedPosts.setVisibility(View.GONE);
        }

        btn_Edit_Profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = btn_Edit_Profile.getText().toString();

                if (btn.equals("Edit Profile")){
                //profili düzenle
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

                Glide.with(getContext()).load(user.getPhotoURL()).into(profilePhoto);
                txt_Username.setText(user.getUserName());
                txt_Name.setText(user.getFullName());
                txt_Bio.setText(user.getBio());
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


}