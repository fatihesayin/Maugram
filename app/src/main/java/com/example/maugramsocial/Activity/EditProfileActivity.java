package com.example.maugramsocial.Activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.maugramsocial.Dialog.LoadingDialog;
import com.example.maugramsocial.Model.User;
import com.example.maugramsocial.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Locale;

public class EditProfileActivity extends AppCompatActivity {
TextInputEditText fullName, userName, bio;
ImageView imgEditProfilePhoto,close;
TextView txtSave, txtChangePhoto;
FirebaseUser fUser;
private Uri imageUri;
private StorageTask uploadTask;
StorageReference storageReference;
LoadingDialog loadingDialog;

    public EditProfileActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        loadingDialog = new LoadingDialog(EditProfileActivity.this);

        fullName = findViewById(R.id.textInputNameInEditProfile);
        userName = findViewById(R.id.textInputUserNameInEditProfile);
        bio = findViewById(R.id.textInputBioInEditProfile);
        imgEditProfilePhoto = findViewById(R.id.editImageProfile);
        close = findViewById(R.id.close);
        txtChangePhoto = findViewById(R.id.txtEditChangeProfilePhoto);
        txtSave = findViewById(R.id.txtSaveInEditProfile);

        fUser = FirebaseAuth.getInstance().getCurrentUser();
        storageReference = FirebaseStorage.getInstance().getReference("Uploads");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                fullName.setText(user.getFullName());
                userName.setText(user.getUserName());
                bio.setText(user.getBio());
                Glide.with(getApplicationContext()).load(user.getPhotoURL()).into(imgEditProfilePhoto);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imgEditProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });
        txtChangePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CropImage.activity().setAspectRatio(1,1)
                        .setCropShape(CropImageView.CropShape.OVAL).start(EditProfileActivity.this);
            }
        });
        txtSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateProfile(fullName.getText().toString(),userName.getText().toString(),bio.getText().toString());
            }
        });
    }

    private void updateProfile(String fullName, String userName, String bio) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
        HashMap<String,Object> hashMap = new HashMap<>();
        hashMap.put("userName", userName.toLowerCase(Locale.ROOT));
        hashMap.put("fullName", fullName);
        hashMap.put("bio", bio);
        reference.updateChildren(hashMap);
    }
    private String getFileExtension(Uri uri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        loadingDialog.startLoadingDialog();
        if (imageUri != null){
            StorageReference fileReference = storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>(){
                @Override
                public void onComplete(@NonNull Task <Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        String myUrl = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(fUser.getUid());
                        HashMap<String,Object> hashMap = new HashMap<>();
                        hashMap.put("photourl",""+myUrl);
                        reference.updateChildren(hashMap);
                        loadingDialog.stopLoadingDialog();

                    }
                    else{
                        Toast.makeText(getApplicationContext(),"Failed",Toast.LENGTH_SHORT).show();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(),"No photo selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            imageUri = result.getUri();
            uploadImage();
        }
        else{
            Toast.makeText(getApplicationContext(),"Something gone wrong!!",Toast.LENGTH_SHORT).show();
        }
    }
}