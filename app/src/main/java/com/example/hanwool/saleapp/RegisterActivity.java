package com.example.hanwool.saleapp;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.support.annotation.NonNull;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Toast;

import com.example.hanwool.saleapp.modal.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {
EditText edtName, edtEmail, edtPassword, edtRepassword, edtPhonenumber;
Button btnRegister;
ProgressBar progressBar;
ScrollView bgRegister;
AnimationDrawable animationDrawable;
ImageView imgClickDisplayPassword, imgnClickDisplayRepassword;
String name, email, password, repassword, phonenumber;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Anhxa();
        mAuth = FirebaseAuth.getInstance();
        //background animated
        animationDrawable=(AnimationDrawable) bgRegister.getBackground();
        animationDrawable.setEnterFadeDuration(0);
        animationDrawable.setExitFadeDuration(3000);
        animationDrawable.start();


    }



    private void Anhxa() {
        edtName = findViewById(R.id.edtName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtRepassword = findViewById(R.id.edtRepassword);
        edtPhonenumber = findViewById(R.id.edtPhone);
        btnRegister= findViewById(R.id.btnRegister);
        bgRegister = findViewById(R.id.bgRegister);
        progressBar = findViewById(R.id.progressBar);
        imgClickDisplayPassword= findViewById(R.id.btnDisplayPasswordClick);
        imgnClickDisplayRepassword= findViewById(R.id.btnDisplayRepasswordClick);
        imgClickDisplayPassword.setOnTouchListener(new View.OnTouchListener() {
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

        imgnClickDisplayRepassword.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch ( event.getAction() ) {

                    case MotionEvent.ACTION_UP:
                        edtRepassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        edtRepassword.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.tabithafull));

                        break;

                    case MotionEvent.ACTION_DOWN:
                        edtRepassword.setInputType(InputType.TYPE_CLASS_TEXT);
                        edtRepassword.setTypeface(ResourcesCompat.getFont(getApplicationContext(), R.font.tabithafull));

                        break;

                }
                return true;
            }
        });
        edtName.setFilters(new InputFilter[] {
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if(cs.equals("")){ // for backspace
                            return cs;
                        }
                        if(cs.toString().matches("[a-zA-Z ]+")){
                            return cs;
                        }
                        return "";
                    }
                }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (mAuth.getCurrentUser() != null){

        }


    }

    public void btnRegisterClick(View view) {
        final String name = edtName.getText().toString();
        final String email = edtEmail.getText().toString().trim();
        final String password = edtPassword.getText().toString().trim();
        final String phone = edtPhonenumber.getText().toString().trim();
        final String repassword = edtRepassword.getText().toString().trim();
//        if (name.length() != 0){
//            edtName.setEnabled(false);
//
//        }
//        if (email.length() != 0){
//            edtEmail.setEnabled(false);
//
//        }
//        if (password.length() != 0){
//            edtPassword.setEnabled(false);
//        }
//        if (phone.length() != 0){
//            edtPhonenumber.setEnabled(false);
//
//        }
        if (name.length() == 0){
            edtName.setError("Name required");
            edtName.requestFocus();
            return;
        }
        if (email.length() == 0){
            edtEmail.setError("Email require");
            edtEmail.requestFocus();
            return;
        }
        if (password.length() == 0){
            edtPassword.setError("Password required");
            edtPassword.requestFocus();
            return;
        }
        if (repassword.equals(password) == false){
            edtRepassword.setError("Password not match!");
            edtRepassword.requestFocus();
            return;
        }

        if (phone.length() == 0){
            edtPhonenumber.setError("Phone number required");
            edtPhonenumber.requestFocus();
            return;
        }
        if(phone.length() <10 || phone.length()>15){
            edtPhonenumber.setError("Phone number must be between 10 and 15 number ");
            edtPhonenumber.requestFocus();
            return;
        }

        else {
            progressBar.setVisibility(View.VISIBLE);
       mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
           @Override
           public void onComplete(@NonNull Task<AuthResult> task) {
               if (task.isSuccessful()){
                   progressBar.setVisibility(View.INVISIBLE);
                   User user = new User(name,email,password,phone);
                   FirebaseDatabase.getInstance().getReference("Users")
                           .child(mAuth.getCurrentUser().getUid())
                           .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {

                           if (task.isSuccessful()){
                               Toast.makeText(getApplicationContext(), "Register successfully!", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                                i.putExtra("emailReg",email);
                                i.putExtra("passwordReg",password);
                                startActivity(i);
                                finish();
                           }else {
                               progressBar.setVisibility(View.GONE);
                               Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                           }
                       }
                   });
               }
               else {
                   progressBar.setVisibility(View.GONE);
                   Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

               }
           }
       });
        }
    }

    public void btnGotoLogin(View view) {
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        finish();

    }
}
