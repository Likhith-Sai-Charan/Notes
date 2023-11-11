package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class CreateAccountActivity extends AppCompatActivity {

    EditText emailEdit,passwordEdit,cpasswordEdit;
    Button createAcctBtn;
    ProgressBar progressBar;
    TextView loginBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        emailEdit = findViewById(R.id.email_id);
        passwordEdit = findViewById(R.id.pd_id);
        cpasswordEdit = findViewById(R.id.cpd_id);
        createAcctBtn = findViewById(R.id.cacct_btn);
        progressBar = findViewById(R.id.progressbar);
        loginBtn = findViewById(R.id.login_text_btn);

        createAcctBtn.setOnClickListener(v -> createAccount());
        loginBtn.setOnClickListener(v-> finish());
    }

    void createAccount(){
        String email = emailEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        String cpassword = cpasswordEdit.getText().toString();

        boolean validated = validateDate(email,password,cpassword);

        if(!validated) return;

        createAccountInFirebase(email,password);
    }

    void createAccountInFirebase(String email,String password){
        changeInProgress(true);

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(CreateAccountActivity.this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        changeInProgress(false);
                        if(task.isSuccessful()){
                            //Create Acct is Done
                            Utility.showToast(CreateAccountActivity.this, "Successfully Create Account");
                            firebaseAuth.getCurrentUser().sendEmailVerification();
                            firebaseAuth.signOut();
                            finish();
                        }
                        else{
                            //failure
                            Utility.showToast(CreateAccountActivity.this, task.getException().getLocalizedMessage());
                        }
                    }
                });
    }

    void changeInProgress(boolean inprogress){
        if(inprogress){
            progressBar.setVisibility(View.VISIBLE);
            createAcctBtn.setVisibility(View.GONE);
        }
        else{
            progressBar.setVisibility(View.GONE);
            createAcctBtn.setVisibility(View.VISIBLE);
        }
    }

    boolean validateDate(String email, String password, String cpassword){
        //validate the data that are input by user

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailEdit.setError("Email is invalid");
            return false;
        }

        if(password.length()<6){
            passwordEdit.setError("Password Length is invalid");
            return false;
        }

        if(!password.equals(cpassword)) {
            cpasswordEdit.setError("Password not match");
            return false;
        }
        return true;
    }
}