package com.example.maugramsocial.Adapter;

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

    private final Context mContext;
    private final List<User> mUsers;

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

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = mUsers.get(position);
        viewHolder.btnFollow.setVisibility(View.VISIBLE);
        viewHolder.btnFollow.setText(user.getUserName());
        viewHolder.fullName.setText(user.getFullName());

        Glide.with(mContext).load(user.getPhotoURL()).into(viewHolder.profilePhotoElement);
        following(user.getId(), viewHolder.btnFollow);

        if (user.getId().equals(fUser.getUid())) {
            viewHolder.btnFollow.setVisibility(View.GONE);
        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("id", user.getId());
                editor.apply();

                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.timeLineFrameLayout,new ProfileFragment()).commit();
            }
        });

        viewHolder.btnFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewHolder.btnFollow.getText().toString().equals("Follow Now")){
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("Following").child(user.getId()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("Follower").child(fUser.getUid()).setValue(true);
                }
                else {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(fUser.getUid())
                            .child("Following").child(user.getId()).removeValue();

                    FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId())
                            .child("Follower").child(fUser.getUid()).removeValue();
                }
            }

        });



    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder{
        public TextView userName,fullName;
        public CircleImageView profilePhotoElement;
        public Button btnFollow;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.txtUsernameElement);
            fullName = itemView.findViewById(R.id.txtFullnameElement);
            profilePhotoElement = itemView.findViewById(R.id.profilePhotoElement);
            btnFollow = itemView.findViewById(R.id.btnFollowElement);
        }
    }
    private void following(String userID, final Button button){
        DatabaseReference followerPath = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(fUser.getUid()).child("Following");
        followerPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.child(userID).exists()){
                    button.setText("Following");
                }
                else {
                    button.setText("Follow Now");
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}
