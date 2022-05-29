package com.example.maugramsocial.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.maugramsocial.Dialog.LoadingDialog;
import com.example.maugramsocial.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        Button btnLogin = findViewById(R.id.btnLogin);
        TextInputEditText textInputEmail = findViewById(R.id.textInputEmail);
        TextInputEditText textInputPassword = findViewById(R.id.textInputPassword);
        //DatabaseReference dbReference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://maugram-social-default-rtdb.firebaseio.com/");
        fAuth = FirebaseAuth.getInstance();
        final LoadingDialog loadingDialog = new LoadingDialog(LoginActivity.this);
        if(fAuth.getUid() != null){
            updateUI();
            finish();
        }

        btnLogin.setOnClickListener(v -> {
            final String email = Objects.requireNonNull(textInputEmail.getText()).toString();
            final String password = Objects.requireNonNull(textInputPassword.getText()).toString();
            loadingDialog.startLoadingDialog();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(LoginActivity.this,"Please fill all fields !",Toast.LENGTH_SHORT).show();
                loadingDialog.stopLoadingDialog();
            }
            else{
                fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        updateUI();
                    }
                    else
                        Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        loadingDialog.stopLoadingDialog();
                });
            }
        });
    }
    public void updateUI(){
        Intent intentToTimeline = new Intent(getApplicationContext(), TimelineActivity.class);
        startActivity(intentToTimeline);
    }

    public void LoginToRegisterIntentText(View view) {
        Intent intentToRegister = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(intentToRegister);
    }
}