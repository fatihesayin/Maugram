package com.example.maugramsocial.Adapter;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Model.Club;
import com.example.maugramsocial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ViewHolder>{
        private Context context;
        private ArrayList<Club> clubs;

        private FirebaseUser fUser;

        // Constructor
        public ClubAdapter(Context context, ArrayList<Club> clubs) {
            this.context = context;
            this.clubs = clubs;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.club_element, parent, false);
            return new ClubAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            fUser = FirebaseAuth.getInstance().getCurrentUser();
            final Club club = clubs.get(position);
            holder.clubName.setText(club.getClubName());
            holder.clubInfo.setText(club.getClubInfo());
            Glide.with(context).load(club.getClubImage()).into(holder.clubImage);

        }


        @Override
        public int getItemCount() {return clubs.size();}

        // View holder class for initializing of
        // your views such as TextView and Imageview.
        public class ViewHolder extends RecyclerView.ViewHolder {
            private ImageView clubImage;
            private TextView clubName, clubInfo;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                clubImage = itemView.findViewById(R.id.imgClub);
                clubName = itemView.findViewById(R.id.txtClubName);
                clubInfo = itemView.findViewById(R.id.txtClubInfo);
            }
        }

}
