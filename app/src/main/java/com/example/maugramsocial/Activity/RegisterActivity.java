package com.example.maugramsocial.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.maugramsocial.Dialog.LoadingDialog;
import com.example.maugramsocial.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText editTxtFullname,editTxtUsername,editTxtMail,editTxtPassword,editTxtConfirmPassword;
    AutoCompleteTextView facultyNames;
    DatabaseReference dbPath = FirebaseDatabase.getInstance().getReferenceFromUrl("https://maugram-social-default-rtdb.firebaseio.com/");
    FirebaseAuth fAuth;
    Boolean isClub = false;


    final LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);
    String fullName,email,username,password, confirmPassword,faculties;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        //Widget Declarations
        btnRegister = findViewById(R.id.btnRegister);
        editTxtFullname = findViewById(R.id.textInputNameInRegister);
        editTxtUsername = findViewById(R.id.textInputUserNameInRegister);
        editTxtMail = findViewById(R.id.textInputEmailInRegister);
        editTxtPassword = findViewById(R.id.textInputPasswordInRegister);
        editTxtConfirmPassword = findViewById(R.id.textInputConfirmPasswordInRegister);
        facultyNames = findViewById(R.id.autoCompleteTextView);
        //DropDown Menu Implementation
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.faculties, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        facultyNames.setAdapter(adapter);

        fAuth = FirebaseAuth.getInstance();


        if(fAuth.getCurrentUser() != null){
            updateUI();
            finish();
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialog.startLoadingDialog();
                fullName = editTxtFullname.getText().toString();
                email = editTxtMail.getText().toString();
                username = editTxtUsername.getText().toString();
                password = editTxtPassword.getText().toString();
                confirmPassword = editTxtConfirmPassword.getText().toString();
                faculties = facultyNames.getText().toString();

                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(fullName)||TextUtils.isEmpty(email)||TextUtils.isEmpty(faculties)||TextUtils.isEmpty(password)||TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(RegisterActivity.this,"Please fill all fields !",Toast.LENGTH_SHORT).show();
                    loadingDialog.stopLoadingDialog();
                }
                else if(!password.equals(confirmPassword)){
                    Toast.makeText(RegisterActivity.this,"Password are not same !",Toast.LENGTH_SHORT).show();
                    loadingDialog.stopLoadingDialog();
                }
                else{
                    save(username, fullName, email, password);
                };

            }
        });

    }

    private void save(final String UserName, final String Name, String email, String password){
        //New User Saving Codes
        fAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()){
                            FirebaseUser fUser = fAuth.getCurrentUser();

                            String userId = fUser.getUid();
                            dbPath=FirebaseDatabase.getInstance().getReference().child("Users").child(userId);

                            HashMap<String, Object> hashMap = new HashMap<>();

                            hashMap.put("id", userId);
                            hashMap.put("userName", username.toLowerCase(Locale.ROOT));
                            hashMap.put("fullName", fullName);
                            hashMap.put("bio", "");
                            hashMap.put("isClub", isClub);
                            hashMap.put("photourl", "https://firebasestorage.googleapis.com/v0/b/maugram-social.appspot.com/o/placeholder.png?alt=media&token=28e5a9ee-3b01-45d8-ac72-7b091aca3c52");

                            dbPath.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if(task.isSuccessful()){
                                        loadingDialog.stopLoadingDialog();
                                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);

                                    }

                                }
                            });
                        }
                        else {
                            loadingDialog.stopLoadingDialog();
                            Toast.makeText(RegisterActivity.this, "Bu mail veya şifre ile kayıt başarısız...", Toast.LENGTH_SHORT).show();

                        }

                    }
                });
    }

    public void updateUI(){
        Intent intentRegisterToLogin = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intentRegisterToLogin);
    }
    public void RegisterToLoginIntentText(View view) {
        Intent intentRegisterToLogin = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intentRegisterToLogin);
    }
}