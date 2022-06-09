package com.example.maugramsocial.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.maugramsocial.Dialog.LoadingDialog;
import com.example.maugramsocial.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;


import java.util.HashMap;
import java.util.Objects;

public class PostActivity extends AppCompatActivity {

    Uri imageUri;
    String myUri = "";

    StorageTask uploadTask;
    StorageReference imagePostRef;

    ImageView image_Close, image_Posted;
    TextView txtSend;
    EditText edt_about_post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        image_Close = findViewById(R.id.close_Post);
        image_Posted = findViewById(R.id.posted_photo);

        txtSend = findViewById(R.id.txtSend);
        edt_about_post = findViewById(R.id.edt_about_post);

        imagePostRef = FirebaseStorage.getInstance().getReference("Posts");

        image_Close.setOnClickListener(view -> {
            startActivity(new Intent(PostActivity.this, TimelineActivity.class));
            finish();
        });

        txtSend.setOnClickListener(view -> photoPost());

        CropImage.activity()
                .setAspectRatio(1,1)
                .start(PostActivity.this);

    }

    private String urlGet(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void photoPost(){
        //Post Photo Codes
        LoadingDialog loadingDialog = new LoadingDialog(PostActivity.this);
        loadingDialog.startLoadingDialog();

        if (imageUri != null){
            StorageReference fileUrl = imagePostRef.child(System.currentTimeMillis()
                    + "." + urlGet(imageUri));

            uploadTask = fileUrl.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileUrl.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        myUri = downloadUri.toString();

                        DatabaseReference dataPath = FirebaseDatabase.getInstance().getReference("Posts");

                        String postId = dataPath.push().getKey();

                        HashMap<String, Object> hashMap = new HashMap<>();

                        hashMap.put("postId", postId);
                        hashMap.put("postImage", myUri);
                        hashMap.put("postAbout", edt_about_post.getText().toString());
                        hashMap.put("postUser", Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());

                        if (postId != null) {
                            dataPath.child(postId).setValue(hashMap);
                        }

                        loadingDialog.stopLoadingDialog();

                        startActivity(new Intent(PostActivity.this, TimelineActivity.class));

                        finish();
                    }

                    else {
                        Toast.makeText(PostActivity.this, "Post Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this,""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            Toast.makeText(this, "Photo not selected!!!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = Objects.requireNonNull(result).getUri();
            image_Posted.setImageURI(imageUri);
        }
        else{
            Toast.makeText(this,"Photo cannot selected!!",Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, TimelineActivity.class));
            finish();
        }
    }
}