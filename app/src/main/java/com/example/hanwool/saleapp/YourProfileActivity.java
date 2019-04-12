package com.example.hanwool.saleapp;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.hanwool.saleapp.modal.User;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.hanwool.saleapp.AddImageRegisterActivity.CHOOSER_IMAGE;

public class YourProfileActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth mAuth;
    de.hdodenhof.circleimageview.CircleImageView imgAvatar, imgAvatarAc;
    TextView txtDisplayname, txtEmail;
    EditText edtDislaynameAc, edtEmail, edtPhonenumber, edtFullname;
    View header;
    ImageView imgUpdatePhoto;
    Uri uriProfileImage;
    String profileImageUrl;
    VideoView videoView;
    ArrayList<User> arrayUser;
    User user;
    String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_your_profile);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });
//video background
        videoView = findViewById(R.id.videoView);
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setLooping(true);
            }
        });
        Uri uri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.background_yourprofile);
//        MediaController mediaController = new MediaController(this);
//        mediaController.setAnchorView(videoView);
//        videoView.setMediaController(mediaController);
        videoView.setKeepScreenOn(true);
        videoView.setVideoURI(uri);
        videoView.start();
        //
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);
        Anhxa();
        readUserInformationFromDbRealtime();
    }


    private void Anhxa() {
        arrayUser = new ArrayList<>();
        imgAvatar = header.findViewById(R.id.imgAvarta);
        txtDisplayname = header.findViewById(R.id.txtDisplayname);
        txtEmail = header.findViewById(R.id.txtEmail);
        imgUpdatePhoto = header.findViewById(R.id.imgUpdatePhoto);
        imgAvatarAc = findViewById(R.id.imgAvartaAc);
        edtDislaynameAc = findViewById(R.id.edtDisplaynameAc);
        edtEmail = findViewById(R.id.edtEmail);
        edtPhonenumber = findViewById(R.id.edtPhonenumber);
        edtFullname = findViewById(R.id.edtFullname);
        edtEmail.setEnabled(false);
        edtDislaynameAc.setEnabled(false);
        mAuth = FirebaseAuth.getInstance();

        loadUserInformation();


    }

    public void imgUpdatePhotoClick(View view) {
        showImageChooser();
    }

    private void showImageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Profile Image"), CHOOSER_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSER_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imgAvatar.setImageBitmap(bitmap);
                imgAvatarAc.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage() {
        final StorageReference profileImageRef = FirebaseStorage.getInstance()
                .getReference("profileimages/" + System.currentTimeMillis() + ".jpg");
        if (uriProfileImage != null) {

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
                                        if (task.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "Update profile successfully", Toast.LENGTH_SHORT).show();
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
                Glide.with(this).load(photoUrl)
                        .into(imgAvatarAc);
            }
            if (user.getDisplayName() != null) {
                String name = user.getDisplayName().toString();
                txtDisplayname.setText(name);
                edtDislaynameAc.setText(name);
                edtDislaynameAc.setHint(name);
            }
            if (user.getEmail() != null) {
                String email = user.getEmail().toString();
                txtEmail.setText(email);
                edtEmail.setText(email);
                edtEmail.setHint(email);
            }
        }
    }

    private void readUserInformationFromDbRealtime() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Users");
        final FirebaseUser userFBase = mAuth.getCurrentUser();
//        Toast.makeText(getApplicationContext(), String.valueOf(userFB.getUid()), Toast.LENGTH_LONG).show();
        if (userFBase.getUid() != null) {
            myRef.child(String.valueOf(userFBase.getUid())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    user = dataSnapshot.getValue(User.class);
                    edtFullname.setText(user.getName());
                    edtPhonenumber.setText(user.getPhonenumber());
                    password = user.getPassword();

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value

                }
            });
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
            Intent i = new Intent(this, MainActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_gallery) {
//All offline song
            Intent i = new Intent(getApplicationContext(), AllOfflineMusicActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_slideshow) {
            //Favorite
            Intent i = new Intent(getApplicationContext(), FavouriteMusicActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_manage) {
            // your profile
        } else if (id == R.id.signOut) {
            // sign out

            FirebaseAuth.getInstance().signOut();
            Intent i = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(i);
            Navigate_AccountActivity.player = MediaPlayer.create(this, R.raw.sofaview);
            Navigate_AccountActivity.player.setLooping(true);
            Navigate_AccountActivity.player.start();
            finish();
            Toast.makeText(getApplicationContext(), "See you again!", Toast.LENGTH_SHORT).show();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void btnSaveClick(View view) {
        String displayName = edtDislaynameAc.getText().toString();
        String fullName = edtFullname.getText().toString();
        String email = edtEmail.getText().toString();
        String phoneNumber = edtPhonenumber.getText().toString();
        User userInfor = new User(fullName, email, password, phoneNumber);
        FirebaseUser user = mAuth.getCurrentUser();

//update firebase realtime database
        FirebaseDatabase.getInstance().getReference("Users")
                .child(mAuth.getCurrentUser().getUid())
                .setValue(userInfor).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                } else {
//                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();

                }
            }
        });
// update display name and url Avatar
        if (user != null) {

            if (profileImageUrl == null) {
                profileImageUrl = user.getPhotoUrl().toString();
            }

            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();
            user.updateProfile(profileChangeRequest)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
//                                progressBarSave.setVisibility(View.GONE);
                                Toast.makeText(getApplicationContext(), "Update profile successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
//                    progressBar.setVisibility(View.GONE);
                }
            });
        }
    }

    public void imgAvartaACClick(View view) {
        showImageChooser();
    }

    public void imgPencilClick(View view) {
        edtDislaynameAc.setEnabled(true);
        edtDislaynameAc.requestFocus();
    }
}
