package com.example.maugramsocial.Adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Fragment.ProfileFragment;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ClubAdapter extends RecyclerView.Adapter<ClubAdapter.ViewHolder>{

    private final Context mContext;
    private final List<User> mClubs;


    public ClubAdapter(Context mContext, List<User> mClubs) {
        this.mContext = mContext;
        this.mClubs = mClubs;
    }

    @NonNull
    @Override
    public ClubAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.club_element,parent,false);
        return new ClubAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final User user = mClubs.get(position);
        holder.userName.setText(user.getUserName());
        if (user.getBio()!=null){
            holder.txtBio.setText(user.getBio());
        }
        Glide.with(mContext).load(user.getPhotoURL()).into(holder.profilePhotoElement);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit();
                editor.putString("id", user.getId());
                editor.apply();

                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.timeLineFrameLayout, new ProfileFragment()).commit();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mClubs.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName, txtBio;
        public CircleImageView profilePhotoElement;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.txt_username_club_element);
            profilePhotoElement = itemView.findViewById(R.id.profile_photo_club_element);
            txtBio = itemView.findViewById(R.id.txtBioClubElement);
        }
    }
}
