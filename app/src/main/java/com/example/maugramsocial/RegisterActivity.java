package com.example.maugramsocial;

import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    Button btnRegister;
    EditText editTxtFullname,editTxtUsername,editTxtMail,editTxtPassword,editTxtConfirmPassword;
    AutoCompleteTextView facultyNames;
    DatabaseReference dbReference;
    FirebaseAuth mAuth;

    final LoadingDialog loadingDialog = new LoadingDialog(RegisterActivity.this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        String fatih;
        //Widget Declarations
        btnRegister = findViewById(R.id.btnRegister);
        editTxtFullname = findViewById(R.id.textInputNameInRegister);
        editTxtUsername = findViewById(R.id.textInputUserNameInRegister);
        editTxtMail = findViewById(R.id.textInputEmail);
        editTxtPassword = findViewById(R.id.textInputPassword);
        editTxtConfirmPassword = findViewById(R.id.textInputConfirmPasswordInRegister);
        facultyNames = findViewById(R.id.autoCompleteTextView);
        //DropDown Menu Implementation

        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.faculties, android.R.layout.simple_dropdown_item_1line);
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        facultyNames.setAdapter(adapter);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                loadingDialog.startLoadingDialog();
                final String fullName = editTxtFullname.getText().toString();
                final String email = editTxtMail.getText().toString();
                final String username = editTxtUsername.getText().toString();
                final String password = editTxtPassword.getText().toString();
                final String confirmPassword = editTxtConfirmPassword.getText().toString();
                final String faculties = facultyNames.getText().toString();
                if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email)||TextUtils.isEmpty(password)||TextUtils.isEmpty(confirmPassword)){
                    Toast.makeText(RegisterActivity.this,"Please fill all fields !",Toast.LENGTH_SHORT).show();
                    loadingDialog.stopLoadingDialog();
                }
                else if(!password.equals(confirmPassword)){
                    Toast.makeText(RegisterActivity.this,"Password are not same !",Toast.LENGTH_SHORT).show();
                    loadingDialog.stopLoadingDialog();
                }
                else{
                    dbReference.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            //Checking if mail is registered before
                            if (snapshot.hasChild(email)){
                                Toast.makeText(RegisterActivity.this,"This mail was registered before!",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                dbReference.child("users").child(email).child("fullName").setValue(fullName);
                                dbReference.child("users").child(email).child("userName").setValue(username);
                                dbReference.child("users").child(email).child("faculties").setValue(faculties);
                                dbReference.child("users").child(email).child("password").setValue(password);

                                Toast.makeText(RegisterActivity.this,"Registered Successfully!",Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });

    }



    public void RegisterToLoginIntentText(View view) {
        Intent intentRegisterToLogin = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intentRegisterToLogin);
    }
}