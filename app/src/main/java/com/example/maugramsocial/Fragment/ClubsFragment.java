package com.example.maugramsocial.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Adapter.ClubAdapter;
import com.example.maugramsocial.Adapter.UserAdapter;
import com.example.maugramsocial.Model.Post;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class ClubsFragment extends Fragment {

    private RecyclerView clubsRecyclerView;
    private ClubAdapter clubAdapter;
    private List<User> mClubs;
    private String clubId;

    TextView txt_Username;
    ImageView clubImage;
    LinearLayout linearLayout;

    public ClubsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_clubs, container, false);

        clubsRecyclerView = view.findViewById(R.id.clubsRecyclerView);
        clubsRecyclerView.setHasFixedSize(true);
        clubsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mClubs = new ArrayList<>();
        clubAdapter = new ClubAdapter(getContext(), mClubs);
        clubsRecyclerView.setAdapter(clubAdapter);

        clubImage = view.findViewById(R.id.profile_photo_club_element);
        txt_Username= view.findViewById(R.id.txt_username_club_element);

        linearLayout = view.findViewById(R.id.linear_layout_club_element);

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        clubId = prefs.getString("id", "none");

        readClubs();

        DatabaseReference userPath = FirebaseDatabase.getInstance().getReference("Users");
        userPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    User user = dataSnapshot.getValue(User.class);
                    mClubs.add(user);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        readClubs();

        return view;
    }

    private void readClubs(){
        DatabaseReference userPath = FirebaseDatabase.getInstance().getReference("Users");

        userPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mClubs.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    mClubs.add(user);
                }
                clubAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}