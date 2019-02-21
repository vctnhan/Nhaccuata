package com.example.hanwool.saleapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hanwool.saleapp.modal.Song;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.hanwool.saleapp.AddImageRegisterActivity.CHOOSER_IMAGE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth mAuth;
    de.hdodenhof.circleimageview.CircleImageView imgAvatar;
    TextView txtDisplayname, txtEmail;
    View header;
    ImageView imgUpdatePhoto;
    Uri uriProfileImage;
    String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        header= navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        Anhxa();
        FirebaseUser user = mAuth.getCurrentUser();
//        if (user.getDisplayName() == null){
//            String name = "";
//            Toast.makeText(getApplicationContext(), "Welcome "+name+" !", Toast.LENGTH_SHORT).show();
//        }else {
//            String name = user.getDisplayName().toString();
//            Toast.makeText(getApplicationContext(), "Welcome "+name+" !", Toast.LENGTH_SHORT).show();
//        }

    }

    private void Anhxa() {

        Navigate_AccountActivity.player.stop();
        imgAvatar = header.findViewById(R.id.imgAvarta);
        txtDisplayname = header.findViewById(R.id.txtDisplayname);
        txtEmail = header.findViewById(R.id.txtEmail);

        mAuth = FirebaseAuth.getInstance();
        imgUpdatePhoto = header.findViewById(R.id.imgUpdatePhoto);
        loadUserInformation();


    }
    public void imgUpdatePhotoClick(View view){
        showImageChooser();
    }
    private void showImageChooser(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Profile Image"), CHOOSER_IMAGE);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSER_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null){
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imgAvatar.setImageBitmap(bitmap);
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
                        FirebaseUser user = mAuth.getCurrentUser();
                        profileImageUrl = task.getResult().toString();
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                .setPhotoUri(Uri.parse(profileImageUrl))
                                .build();
                        user.updateProfile(profileChangeRequest)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            Toast.makeText(getApplicationContext(), "Update profile successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    } else {
                        // Handle failures
                        // ...
                    }
                }
            });
        }

    }
    private void loadUserInformation() {
        FirebaseUser user = mAuth.getCurrentUser();


        if (user != null) {
            if (user.getPhotoUrl() != null) {
                String photoUrl = user.getPhotoUrl().toString();
                Glide.with(this).load(photoUrl)
                        .into(imgAvatar);

            }
            if (user.getDisplayName() != null){
                String name = user.getDisplayName().toString();
                txtDisplayname.setText(name);
            }
            if (user.getEmail() != null){
                String email = user.getEmail().toString();
               txtEmail.setText(email);
            }
        }
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Trang chu

        } else if (id == R.id.nav_gallery) {
        // storage songs
            Intent i = new Intent(this, AllOfflineMusicActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_slideshow) {
            Intent i = new Intent(this, FavouriteMusicActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_manage) {
            //your profile
            Intent i = new Intent(this, YourProfileActivity.class);
            startActivity(i);
            finish();
        }
        else if (id == R.id.signOut) {
            // sign out
            PlayerActivity.mp.stop();
            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getApplicationContext(),LoginActivity.class);
            startActivity(i);
            Navigate_AccountActivity.player = MediaPlayer.create(this, R.raw.sofaview);
            Navigate_AccountActivity.player.setLooping(true);
            Navigate_AccountActivity.player.start();
            finish();
            Toast.makeText(getApplicationContext(),"See you again!", Toast.LENGTH_SHORT).show();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
