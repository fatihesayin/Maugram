package com.example.maugramsocial.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity {
    EditText editAddComment;
    ImageView profilePhoto;
    TextView txtPost;
    String postID, commentSenderID;

    FirebaseUser fUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);

        Toolbar toolbar = findViewById(R.id.toolBarComments);
        editAddComment = findViewById(R.id.editTxtAddComment);
        profilePhoto = findViewById(R.id.imgCommentsProfilePhoto);
        txtPost = findViewById(R.id.txtSendComment);


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fUser = FirebaseAuth.getInstance().getCurrentUser();
        Intent intent = getIntent();
        postID = intent.getStringExtra("postID");
        commentSenderID = intent.getStringExtra("commentSenderID");

        txtPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editAddComment.getText().toString().equals("")){
                    Toast.makeText(getApplicationContext(),"You have to type something!",Toast.LENGTH_SHORT).show();
                }
                else
                    addComment();
            }
        });
        getImage();
    }
    public void addComment(){
        DatabaseReference referenceComment = FirebaseDatabase.getInstance().getReference("Comments").child("postID");
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("comment",editAddComment.getText().toString());
        hashMap.put("sender",fUser.getUid());

        referenceComment.setValue(hashMap);
        editAddComment.setText("");
    }
    public void getImage(){
        DatabaseReference referenceImage = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        referenceImage.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Glide.with(getApplicationContext()).load(user.getPhotoURL()).into(profilePhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}