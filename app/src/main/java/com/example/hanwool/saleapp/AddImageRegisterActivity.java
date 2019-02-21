package com.example.hanwool.saleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

public class AddImageRegisterActivity extends AppCompatActivity {
ImageView imgProfile;
EditText edtDisplayname;
TextView txtSkip;
Button btnSave;
Uri uriProfileImage;
String profileImageUrl;
FirebaseAuth mAuth;
ProgressBar progressBar, progressBarSave;

public   static  final int CHOOSER_IMAGE = 101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_image_register);
        Anhxa();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser().getDisplayName() != null && mAuth.getCurrentUser().getPhotoUrl() != null) {
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
            Navigate_AccountActivity.player.stop();
            String name = mAuth.getCurrentUser().getDisplayName();

                Toast.makeText(getApplicationContext(), "Welcome " + name + " !", Toast.LENGTH_SHORT).show();


        }
    }

    private void Anhxa() {
        imgProfile = findViewById(R.id.imgProfile);
        edtDisplayname= findViewById(R.id.edtDisplayname);
        btnSave= findViewById(R.id.btnSave);
       txtSkip= findViewById(R.id.txtSkip);
        progressBar= findViewById(R.id.progressBar);
        progressBarSave= findViewById(R.id.progressBarSave);
        mAuth= FirebaseAuth.getInstance();
        loadUserInformation();
    }



    public void imgProfileClick(View view) {
        progressBar.setVisibility(View.VISIBLE);
        showImageChooser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSER_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imgProfile.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("profileimages/"+System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null){
            progressBar.setVisibility(View.VISIBLE);
        profileImageRef.putFile(uriProfileImage).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL
                    return profileImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                         profileImageUrl = task.getResult().toString();
                        progressBar.setVisibility(View.GONE);
                    } else {
                       Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }
                }
            });
        }

    }

    private void showImageChooser(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
       startActivityForResult(Intent.createChooser(i,"Select Profile Image"), CHOOSER_IMAGE);
    }


    public void btnSaveClick(View view) {

        saveUserInformation();
        String Displayname = edtDisplayname.getText().toString();
        if (Displayname.length() != 0){
            edtDisplayname.setEnabled(false);
        }


    }

    private void saveUserInformation() {

        String displayName = edtDisplayname.getText().toString();
        if (displayName.isEmpty()){
            edtDisplayname.setError("Display name required");
            edtDisplayname.requestFocus();
            return;
        }
        else {
            progressBarSave.setVisibility(View.VISIBLE);

        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null && uriProfileImage == null){

            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();
            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBarSave.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Save profile information successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                                Navigate_AccountActivity.player.stop();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        if (user != null && profileImageUrl != null){

            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressBarSave.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Save profile information successfully", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(i);
                                finish();
                                Navigate_AccountActivity.player.stop();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        }
        }
    }
    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null) {
            if (user.getPhotoUrl() != null) {
                String photoUrl = user.getPhotoUrl().toString();
                Glide.with(this).load(photoUrl)
                        .into(imgProfile);

            }

        }
    }
    public void txtSkip(View view) {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        finish();
        Toast.makeText(getApplicationContext(), "Welcome !", Toast.LENGTH_SHORT).show();
    }
}
