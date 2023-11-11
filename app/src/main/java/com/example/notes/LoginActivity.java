package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    EditText emailEdit,passwordEdit;
    Button loginBtn;
    ProgressBar progressBar;
    TextView cacctBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailEdit = findViewById(R.id.email_id);
        passwordEdit = findViewById(R.id.pd_id);
        loginBtn = findViewById(R.id.login_btn);
        progressBar = findViewById(R.id.progressbar);
        cacctBtn = findViewById(R.id.cacct_text_btn);

        loginBtn.setOnClickListener((v -> loginUser()));
        cacctBtn.setOnClickListener((v -> startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class))));
    }

    void loginUser(){
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();

        boolean validated = validateDate(email,password);

        if(!validated) return;

        loginAccountInFirebase(email,password);
    }

    void loginAccountInFirebase(String email, String password){
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        changeInProgress(true);
        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                changeInProgress(false);
                if(task.isSuccessful()){
                    //login Success
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        //go to mainactivity
                        startActivity(new Intent(LoginActivity.this,MainActivity.class));
                        finish();
                    }
                    else{
                        Utility.showToast(LoginActivity.this,"Email not Verified,Please verify your email.");
                    }
                }
                else{
                    //login fail
                    Utility.showToast(LoginActivity.this,task.getException().getLocalizedMessage());
                }
            }
        });
    }

    void changeInProgress(boolean inprogress){
        if(inprogress){
            progressBar.setVisibility(View.VISIBLE);
            loginBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            loginBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateDate(String email, String password){
        //validate the data that are input by user

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.setError("Email is invalid");
            return false;
        }

        if(password.length()<6){
            passwordEdit.setError("Password Length is invalid");
            return false;
        }
        return true;
    }
}