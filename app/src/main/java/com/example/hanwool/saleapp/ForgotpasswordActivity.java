package com.example.hanwool.saleapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ForgotpasswordActivity extends AppCompatActivity {
    ScrollView bgForgot;
    AnimationDrawable animationDrawable;
    EditText edtEmail;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    Button btnSubmit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgotpassword);
        Anhxa();
        animationDrawable=(AnimationDrawable) bgForgot.getBackground();
        animationDrawable.setEnterFadeDuration(3500);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        catchEventTextChange();
    }



    private void Anhxa() {
        bgForgot = findViewById(R.id.bgForgot);
        edtEmail = findViewById(R.id.edtEmail);
        mAuth= FirebaseAuth.getInstance();
        btnSubmit= findViewById(R.id.btnSubmit);
        progressBar = findViewById(R.id.progressBar);

    }
    private void catchEventTextChange() {
        final String email = edtEmail.getText().toString();
        if (email.length() == 0){
            btnSubmit.setEnabled(false);
            btnSubmit.setTextColor(Color.parseColor("#999999"));
        }
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0){
                    btnSubmit.setEnabled(false);
                    btnSubmit.setTextColor(Color.parseColor("#999999"));
                }
                else {
                    btnSubmit.setEnabled(true);
                    btnSubmit.setTextColor(Color.parseColor("#ffffff"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public void btnSubmitClick(View view) {

        final String email= edtEmail.getText().toString();
        if (email.length()==0){
            edtEmail.setError("Email require");
            edtEmail.requestFocus();
            return;
        }
        else {
            progressBar.setVisibility(View.VISIBLE);
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressBar.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(),"Submit successfully. Please check your email!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(),LoginActivity.class);
                                i.putExtra("email", email);
                                startActivity(i);
                                finish();
                            }
                        }
                    });
        }

    }

    public void btnGotoLogin(View view) {
        Intent i = new Intent(this, LoginActivity.class);
        startActivity(i);
        finish();
    }
}
