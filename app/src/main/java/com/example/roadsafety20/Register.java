package com.example.roadsafety20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class Register extends AppCompatActivity {

    TextInputEditText memail, mpassword, mnumber, musername;
    Button buttonReg;
    FirebaseAuth mAuth;
    FirebaseDatabase db;
    DatabaseReference reference;
    ProgressBar progressBar;
    TextView textView;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mAuth = FirebaseAuth.getInstance();
        memail = findViewById(R.id.email);
        mpassword = findViewById(R.id.password);
        musername =findViewById(R.id.userName);
        mnumber = findViewById(R.id.mobileNumber);
        buttonReg = findViewById(R.id.btn_register);
        progressBar = findViewById(R.id.progressBar);
        textView = findViewById(R.id.loginNow);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Users");
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);

                String email = memail.getText().toString();
                String password = mpassword.getText().toString();
                String mobileNumber = mnumber.getText().toString();
                String userName = musername.getText().toString();


                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(Register.this, "Enter email", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(userName)) {
                    Toast.makeText(Register.this, "Enter username", Toast.LENGTH_SHORT).show();
                    return;
                }


                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(Register.this, "Enter password", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(mobileNumber.length() != 10){
                    Toast.makeText(Register.this, "Enter valid mobile number", Toast.LENGTH_SHORT).show();
                    return;
                }


                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    progressBar.setVisibility(View.GONE);
                                    String userId = mAuth.getCurrentUser().getUid();
                                    Users users = new Users(email, mobileNumber, userName);
                                    reference.child("users").child(userId).setValue(users)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Toast.makeText(Register.this,
                                                            "Registration successful!",
                                                            Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(getApplicationContext(), Login.class);
                                                    startActivity(intent);
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(Register.this,
                                                            "Failed to register user: " + e.getMessage(),
                                                            Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }else {
                                    Toast.makeText(Register.this,
                                            "Failed to register user: " + task.getException().getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                }
//                                if (task.isSuccessful()) {
////                                    if(!email.isEmpty() && !mobileNumber.isEmpty() && !password.isEmpty() && !userName.isEmpty()){
//
//                                    Toast.makeText(Register.this, "Account created.",
//                                            Toast.LENGTH_SHORT).show();
////                                    Intent intent = new Intent(getApplicationContext(), Login.class);
////                                    startActivity(intent);
////                                    finish();
//                                } else {
//                                    Toast.makeText(Register.this, "Authentication failed.",
//                                            Toast.LENGTH_SHORT).show();
//
//                                }
                            }
                        });

            }
        });
    }
}