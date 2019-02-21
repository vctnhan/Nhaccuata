package com.example.hanwool.saleapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
EditText edtEmail, edtPassword;
Button btnLogin;
FirebaseAuth mAuth;
ScrollView bgLogin;
AnimationDrawable animationDrawable;
ProgressBar progressBar;
ImageView imgDisplayPasswordClick;
CheckBox saveLoginCheckBox;
    private SharedPreferences loginPreferences;
    private SharedPreferences.Editor loginPrefsEditor;
    private Boolean saveLogin;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Anhxa();
        // animated background
        animationDrawable=(AnimationDrawable) bgLogin.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();
        if (getIntent().getStringExtra("email") != null){
            edtEmail.setText(getIntent().getStringExtra("email"));
        }
        if (getIntent().getStringExtra("emailReg") != null &&
                getIntent().getStringExtra("passwordReg") != null){
            edtEmail.setText(getIntent().getStringExtra("emailReg"));
            edtPassword.setText(getIntent().getStringExtra("passwordReg"));
        }
    }

    private void Anhxa() {
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword= findViewById(R.id.edtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        bgLogin= findViewById(R.id.bgLogin);
        progressBar= findViewById(R.id.progressBar);
        saveLoginCheckBox = findViewById(R.id.saveLoginCheckBox);
        saveLoginCheckBox.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.tabithafull));
        loginPreferences = getSharedPreferences("loginPrefs", MODE_PRIVATE);
        loginPrefsEditor = loginPreferences.edit();

        saveLogin = loginPreferences.getBoolean("saveLogin", false);
        if (saveLogin == true) {
            edtEmail.setText(loginPreferences.getString("email", ""));
            edtPassword.setText(loginPreferences.getString("password", ""));
            saveLoginCheckBox.setChecked(true);
        }
        imgDisplayPasswordClick= findViewById(R.id.btnDisplayPasswordClick);
        imgDisplayPasswordClick.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {

                switch ( event.getAction() ) {

                    case MotionEvent.ACTION_UP:
                        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        edtPassword.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.tabithafull));

                        break;

                    case MotionEvent.ACTION_DOWN:
                        edtPassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        edtPassword.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.tabithafull));

                        break;

                }
                return true;
            }
            });

        mAuth = FirebaseAuth.getInstance();
     catchEventTextChange();
    }

    private void catchEventTextChange() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        if (email.length() == 0 || password.length() == 0){
            btnLogin.setEnabled(false);
            btnLogin.setTextColor(Color.parseColor("#999999"));
        }
        edtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0){
                    btnLogin.setEnabled(false);
                    btnLogin.setTextColor(Color.parseColor("#999999"));
                }
                else {
                    btnLogin.setEnabled(true);
                    btnLogin.setTextColor(Color.parseColor("#ffffff"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().length() == 0){
                    btnLogin.setEnabled(false);
                    btnLogin.setTextColor(Color.parseColor("#999999"));
                }
                else {
                    btnLogin.setEnabled(true);
                    btnLogin.setTextColor(Color.parseColor("#ffffff"));
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//if (mAuth.getCurrentUser() !=null){
//    Intent i = new Intent(getApplicationContext(), MainActivity.class);
//    startActivity(i);
//}

    }

    public void btnLoginClick(View view) {
     String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (email.length() == 0){
            edtEmail.setError("Email require");
            edtEmail.requestFocus();

            return;
        }
        if (password.length() == 0){
            edtPassword.setError("Password require");
            edtPassword.requestFocus();

            return;
        }

        else {
            progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.INVISIBLE);
                            // progress checkbox
                            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(edtEmail.getWindowToken(), 0);
                            String email = edtEmail.getText().toString().trim();
                            String password = edtPassword.getText().toString().trim();

                            if (saveLoginCheckBox.isChecked()) {
                                loginPrefsEditor.putBoolean("saveLogin", true);
                                loginPrefsEditor.putString("email", email);
                                loginPrefsEditor.putString("password", password);
                                loginPrefsEditor.commit();
                            } else {
                                loginPrefsEditor.clear();
                                loginPrefsEditor.commit();
                            }
                            //
                            Toast.makeText(getApplicationContext(), "Login successfully!", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getApplicationContext(), AddImageRegisterActivity.class);
                            startActivity(i);
                            finish();


                        } else {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();

                        }
                    }
                });
        }


    }

    public void btnGotoRegister(View view) {
        Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(i);
        finish();
    }

    public void btnGotoForgot(View view) {
        Intent i = new Intent(this, ForgotpasswordActivity.class);
        startActivity(i);
        finish();

    }




}
