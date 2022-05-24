package com.example.maugramsocial.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.ProfileFragment;
import com.example.maugramsocial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{

    private Context mContext;
    private List<User> mUsers;
    private FirebaseUser fUser;

    public UserAdapter(Context mContext, List<User> mUsers) {
        this.mContext = mContext;
        this.mUsers = mUsers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_element,parent,false);
        return new UserAdapter.ViewHolder(view);
    }
    public void Following(final String userID,final Button button){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("followUser")
                .child(fUser.getUid()).child("followingUser");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(userID).exists())
                    button.setText("Following");
                else
                    button.setText("Follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(position);
        holder.btnFollow.setVisibility(View.VISIBLE);
        holder.btnFollow.setText(user.getUserName());
        holder.fullName.setText(user.getFullName());
        Glide.with(mContext).load(user.getPhotoURL()).into(holder.profilePhoto);
        Following(user.getId(), holder.btnFollow);

        if (user.getId().equals(fUser.getUid()))
            holder.btnFollow.setVisibility(View.GONE);
        else
        {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileID", user.getId());
                    editor.apply();

                    ((FragmentActivity) mContext).getSupportFragmentManager()
                            .beginTransaction().replace(R.id.timeLineFrameLayout,new ProfileFragment()).commit();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName,fullName;
        public CircleImageView profilePhoto;
        public Button btnFollow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.txtUsernameElement);
            fullName = itemView.findViewById(R.id.txtFullnameElement);
            profilePhoto = itemView.findViewById(R.id.profilePhoto);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }

}
