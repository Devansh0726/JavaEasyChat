package com.example.javaeasychat.LoginThings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.example.javaeasychat.FirebaseThings.FirebaseUtil;
import com.example.javaeasychat.MainActivity;
import com.example.javaeasychat.R;
import com.example.javaeasychat.model.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.hbb20.CountryCodePicker;

public class LoginUsernameActivity extends AppCompatActivity {


    Button btnLetMeIn;
    EditText etUsername;

    String phoneNumber;
    UserModel userModel;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_username);
        phoneNumber = getIntent().getStringExtra("phone");

        btnLetMeIn = findViewById(R.id.let_me_in_btn);
        etUsername = findViewById(R.id.login_username);
        progressBar = findViewById(R.id.login_progress_bar);
        getUsername();

        btnLetMeIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setUsername();
            }
        });
    }

    void getUsername(){
        setInProgress(true);
        FirebaseUtil.currentUserDetails().get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                setInProgress(false);
                if (task.isSuccessful()){
                   userModel = task.getResult().toObject(UserModel.class);
                  if (userModel != null){
                      etUsername.setText(userModel.getUsername());
                  }
                }
            }
        });
    }

    void setUsername(){

        String username = etUsername.getText().toString();
        if (username.isEmpty() || username.length() < 3){
            etUsername.setError("Username length should be at least 3 chars");
            return;
        }
        setInProgress(true);

        if (userModel != null){
            userModel.setUsername(username);
        } else {
            userModel = new UserModel(phoneNumber, username, Timestamp.now(), FirebaseUtil.currentUserId());
        }

        FirebaseUtil.currentUserDetails().set(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
               setInProgress(false);
               if (task.isSuccessful()){
                   Intent intent = new Intent(LoginUsernameActivity.this, MainActivity.class);
                   intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                   startActivity(intent);
               }
            }
        });
    }


    void setInProgress(boolean inProgress) {
        if (inProgress) {
            progressBar.setVisibility(View.VISIBLE);
            btnLetMeIn.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            btnLetMeIn.setVisibility(View.VISIBLE);
        }
    }
}