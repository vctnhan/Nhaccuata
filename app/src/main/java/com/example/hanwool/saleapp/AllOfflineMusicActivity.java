package com.example.hanwool.saleapp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hanwool.saleapp.adapter.SongAdapter;
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.hanwool.saleapp.AddImageRegisterActivity.CHOOSER_IMAGE;
import static com.example.hanwool.saleapp.PlayerActivity.mp;

public class AllOfflineMusicActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    private static final int MY_PERMISSON_REQUEST = 1;
    public static final int REQ_CODE_SPEECH_INPUT = 100;
    de.hdodenhof.circleimageview.CircleImageView imgAvatar;
    TextView txtDisplayname, txtEmail;
    ArrayList<Song> arraySong;
    View header;
    ListView lvAllsongs;
    SongAdapter songAdapter;
    SearchView searchView;
    FirebaseAuth mAuth;
    ImageView imgUpdatePhoto;
    Uri uriProfileImage;
    String profileImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_offline_music);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (ContextCompat.checkSelfPermission(AllOfflineMusicActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(AllOfflineMusicActivity.this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)) {
                ActivityCompat.requestPermissions(AllOfflineMusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSON_REQUEST);
            } else {
                ActivityCompat.requestPermissions(AllOfflineMusicActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, MY_PERMISSON_REQUEST);
            }
        } else {
            Anhxa();

        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//
//                finish();
//                startActivity(getIntent());
                if (PlayerActivity.mp != null) {
                    PlayerActivity.mp.pause();
                }

                searchView.setIconified(false);
                promptSpeechInput();


            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        imgAvatar = header.findViewById(R.id.imgAvarta);
        txtDisplayname = header.findViewById(R.id.txtDisplayname);
        txtEmail = header.findViewById(R.id.txtEmail);
        imgUpdatePhoto = header.findViewById(R.id.imgUpdatePhoto);
        loadUserInformation();
    }

    private void Anhxa() {
        mAuth = FirebaseAuth.getInstance();
        lvAllsongs = findViewById(R.id.lvAllOfflineSongs);
        arraySong = new ArrayList<>();

        songAdapter = new SongAdapter(AllOfflineMusicActivity.this, arraySong);
        lvAllsongs.setAdapter(songAdapter);

        getMusic();


    }

    public void getMusic() {
//        arraySong = new ArrayList<>();
        ContentResolver contentResolver = getContentResolver();
        Uri songUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        Uri songUri2 = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
        String[] projection = {MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.ArtistColumns.ARTIST,
                MediaStore.Audio.AudioColumns.DATA};
        Cursor songCursor = contentResolver.query(songUri, projection, null, null, null);
        Cursor songCursor2 = contentResolver.query(songUri2, projection, null, null, null);
        if (songCursor != null && songCursor.moveToFirst()) {
            int duration = songCursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int title = songCursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int artist = songCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int location = songCursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            do {
                String currentTitle = songCursor.getString(title);
                String currentArtist = songCursor.getString(artist);
                int currentDuration = songCursor.getInt(duration);
                String currentLocation = songCursor.getString(location);
                arraySong.add(new Song(currentTitle, currentArtist, currentDuration, currentLocation));
                songAdapter.notifyDataSetChanged();
            } while (songCursor.moveToNext());

        }
//    if (songCursor2 != null && songCursor2.moveToFirst()){
//        int duration = songCursor2.getColumnIndex(MediaStore.Audio.Media.DURATION);
//        int title = songCursor2.getColumnIndex(MediaStore.Audio.Media.TITLE);
//        int artist = songCursor2.getColumnIndex(MediaStore.Audio.Media.ARTIST);
//        int location = songCursor2.getColumnIndex(MediaStore.Audio.Media.DATA);
//        do {
//            String currentTitle = songCursor2.getString(title);
//            String currentArtist = songCursor2.getString(artist);
//            int currentDuration = songCursor2.getInt(duration);
//            final String  currentLocation = songCursor2.getString(location);
//            arraySong.add(new Song(currentTitle, currentArtist, currentDuration, currentLocation));
//
//        } while (songCursor2.moveToNext());
    }
//    songAdapter.notifyDataSetChanged();

    public void imgUpdatePhotoClick(View view) {
        showImageChooser();
    }

    private void showImageChooser() {
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i, "Select Profile Image"), CHOOSER_IMAGE);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSON_REQUEST: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (ContextCompat.checkSelfPermission(AllOfflineMusicActivity.this,
                            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "Permisson granted", Toast.LENGTH_SHORT).show();
                        Anhxa();
                    }
                } else {
                    Toast.makeText(this, "No permisson granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
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
            if (user.getDisplayName() != null) {
                String name = user.getDisplayName().toString();
                txtDisplayname.setText(name);
            }
            if (user.getEmail() != null) {
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
        getMenuInflater().inflate(R.menu.search_main, menu);
        MenuItem itemSearch = menu.findItem(R.id.action_search);
        searchView = (SearchView) itemSearch.getActionView();
//        action_mic = (Button) itemSearch.getActionView();
        //set OnQueryTextListener cho search view Ä‘á»ƒ thá»±c hiá»‡n search theo text

        searchView.setQueryHint("Type song or singer ...");
        searchView.setOnQueryTextListener(this);
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
            // Handle the camera action
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_gallery) {
            //storage songs
        } else if (id == R.id.nav_slideshow) {
            // favorite song
            Intent i = new Intent(getApplicationContext(), FavouriteMusicActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(getApplicationContext(), YourProfileActivity.class);
            startActivity(i);
            finish();
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            songAdapter.getFilter().filter("");
//            onRestart();


        } else {
            songAdapter.getFilter().filter(s.toString());


        }
        return true;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Trả lại dữ liệu sau khi nhập giọng nói vào
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CHOOSER_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uriProfileImage);
                imgAvatar.setImageBitmap(bitmap);
                uploadImageToFirebaseStorage();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    searchView.setQuery(result.get(0).toLowerCase(), true);
                }
                break;
            }

        }
    }
}
