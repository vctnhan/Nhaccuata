package com.example.hanwool.saleapp;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.hanwool.saleapp.adapter.FavouriteAdapter;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import static com.example.hanwool.saleapp.AddImageRegisterActivity.CHOOSER_IMAGE;
import static com.example.hanwool.saleapp.AllOfflineMusicActivity.REQ_CODE_SPEECH_INPUT;

public class FavouriteMusicActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {
    FirebaseAuth mAuth;
    de.hdodenhof.circleimageview.CircleImageView imgAvatar;
    TextView txtDisplayname, txtEmail;
    Song song;
    ListView lvFavouriteSongs;
    FavouriteAdapter favouriteAdapter;
    SearchView searchView;
    View header;
    Uri uriProfileImage;
    ImageView imgUpdatePhoto;
    String profileImageUrl;
public static ArrayList<Song> arrayFavourite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favourite_music);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Anhxa();
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (  PlayerActivity.mp != null){
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
        header= navigationView.getHeaderView(0);
        imgAvatar = header.findViewById(R.id.imgAvarta);
        txtDisplayname = header.findViewById(R.id.txtDisplayname);
        txtEmail = header.findViewById(R.id.txtEmail);
        imgUpdatePhoto = header.findViewById(R.id.imgUpdatePhoto);
        loadUserInformation();
    }

    private void Anhxa() {
        mAuth = FirebaseAuth.getInstance();
        lvFavouriteSongs = findViewById(R.id.lvFavouriteSongs);

if (arrayFavourite !=null){

}
else {
    arrayFavourite= new ArrayList<>();
}
        Toast.makeText(getApplicationContext(),String.valueOf(arrayFavourite.size()),Toast.LENGTH_SHORT).show();
        favouriteAdapter = new FavouriteAdapter(getApplicationContext(),arrayFavourite);
        lvFavouriteSongs.setAdapter(favouriteAdapter);
        lvFavouriteSongs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), PlayerActivity.class);
                intent.putExtra("arraySong",arrayFavourite);
                intent.putExtra("index", i);
                startActivity(intent);
//                Uri uriSong = Uri.parse(arrayFavourite.get(i).getLocation());

//                Toast.makeText(getApplicationContext(),uriSong.toString(), Toast.LENGTH_SHORT).show();
//                mp = MediaPlayer.create(AllOfflineMusicActivity.this, uriSong);
//                mp.start();
            }
        });
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
    public void imgUpdatePhotoClick(View view){
        showImageChooser();
    }
    private void showImageChooser(){
        Intent i = new Intent();
        i.setType("image/*");
        i.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(i,"Select Profile Image"), CHOOSER_IMAGE);
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
            // Homepage
            Intent i = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_gallery) {
            // Storage songs
            Intent i = new Intent(getApplicationContext(), AllOfflineMusicActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.nav_slideshow) {
//Favorite
        } else if (id == R.id.nav_manage) {
            Intent i = new Intent(getApplicationContext(), YourProfileActivity.class);
            startActivity(i);
            finish();
        } else if (id == R.id.signOut) {
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

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            favouriteAdapter.getFilter().filter("");
//            onRestart();


        } else {
            favouriteAdapter.getFilter().filter(s.toString());


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
     * */
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
