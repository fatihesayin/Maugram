package com.example.maugramsocial.Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.maugramsocial.Adapter.ClubAdapter;
import com.example.maugramsocial.Adapter.UserAdapter;
import com.example.maugramsocial.Model.Club;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;


public class ClubsFragment extends Fragment {

    private RecyclerView clubsRecyclerView;
    private ClubAdapter clubAdapter;
    private ArrayList<Club> mClubs;
    private String userID;
    private FirebaseAuth fAuth;

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
        clubAdapter= new ClubAdapter(getContext(), mClubs);
        clubsRecyclerView.setAdapter(clubAdapter);

        return view;
    }




}