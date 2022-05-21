package com.example.maugramsocial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = Objects.requireNonNull(textInputEmail.getText()).toString();
                final String password = Objects.requireNonNull(textInputPassword.getText()).toString();
                loadingDialog.startLoadingDialog();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this,"Please fill all fields !",Toast.LENGTH_SHORT).show();
                    loadingDialog.stopLoadingDialog();
                }
                else{
                    fAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                updateUI();
                            }
                            else
                                Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
                }
            }
        });


    }
    public void updateUI(){
        Intent intentToTimeline = new Intent(getApplicationContext(),TimelineActivity.class);
        startActivity(intentToTimeline);
    }

    public void LoginToRegisterIntentText(View view) {
        Intent intentToRegister = new Intent(getApplicationContext(),RegisterActivity.class);
        startActivity(intentToRegister);
    }
}