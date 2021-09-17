package com.example.minitwitter.Activities;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.minitwitter.Activities.ui.dashboard.NuevoTweetDialogFragment;
import com.example.minitwitter.Activities.ui.dashboard.TweetListFragment;
import com.example.minitwitter.Activities.ui.notifications.ProfileFragment;
import com.example.minitwitter.Activities.ui.notifications.ProfileViewModel;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.databinding.ActivityDashboardBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;


public class DashboardActivity extends AppCompatActivity implements PermissionListener {

    private ActivityDashboardBinding binding;
    private FloatingActionButton fab;
    private BottomNavigationView navView;
    private ProfileViewModel model;
    private ImageView ivAvatar;
    private Fragment manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // View binding
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        fab = binding.fab;
        ivAvatar = binding.ivToolbarPhoto;
        model = new ViewModelProvider(this).get(ProfileViewModel.class);

        getSupportActionBar().hide();

        // Equivalencia al findviewbyId()
        navView = binding.navView;

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_tweets, R.id.navigation_profile)
                .build();

        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        // NavController navController = Navigation.findNavController(this, R.id.fragmentContainer);
        // NavController navController = Navigation.findNavController(this, R.id.mobile_navigation);
        // NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        // NavigationUI.setupWithNavController(navView, navController);


        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragmentContainer, TweetListFragment.newInstance(Constantes.TWEET_LIST_ALL))
                .commit();

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NuevoTweetDialogFragment dialog = new NuevoTweetDialogFragment();
                dialog.show(getSupportFragmentManager(), "NuevoTweetDialogFragment");
            }
        });

        // Seteamos la imagen de perfil de usuario
        String photoUrl = SharedPreferencesManager.getStringValue(Constantes.PREF_PHOTOURL);
        if (!photoUrl.isEmpty()) {
            Glide.with(this)
                    .load(Constantes.API_MINITWITTER_FILES + photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .centerCrop()
                    .skipMemoryCache(true)
                    .into(ivAvatar);
        }

        model.photoProfile.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String photo) {
                Glide.with(DashboardActivity.this)
                        .load(Constantes.API_MINITWITTER_FILES + photo)
                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                        .centerCrop()
                        .skipMemoryCache(true)
                        .into(ivAvatar);
            }
        });

    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {

            manager = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    // manager = new TweetListFragment();
                    // fragmentChanger(manager);
                    manager = TweetListFragment.newInstance(Constantes.TWEET_LIST_ALL);
                    fab.show();
                    // navView.setSelected(true);
                    Toast.makeText(DashboardActivity.this, "Pulsaste home", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.navigation_tweets:
                    // manager = new HomeFragment();
                    // fragmentChanger(manager);
                    manager = TweetListFragment.newInstance(Constantes.TWEET_LIST_FAVS);
                    fab.hide();
                    navView.getSelectedItemId();
                    navView.setSelected(true);
                    Toast.makeText(DashboardActivity.this, "Pulsaste Tweets", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.navigation_profile:
                    // manager = new NotificationsFragment();
                    // fragmentChanger(manager);
                    manager = new ProfileFragment();
                    fab.hide();
                    Toast.makeText(DashboardActivity.this, "Pulsaste profile", Toast.LENGTH_SHORT).show();
                    break;
            }

            if (manager != null) {
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, manager)
                        .commit();
                return true;
            }

            return false;
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        // super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_CANCELED) {
            if (requestCode == Constantes.SELECT_PHOTO_GALLERY) {
                if (data != null) {
                    Uri imagenSelec = data.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};

                    Cursor cursor = getContentResolver().query(imagenSelec, filePathColumn, null, null, null);

                    if (cursor != null) {
                        cursor.moveToFirst();
                        int imagenIndex = cursor.getColumnIndex(filePathColumn[0]);
                        String fotopath = cursor.getString(imagenIndex);
                        model.uploadPhoto(fotopath);
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
        // Invocamops la selecccion de fotos de la galeria
        Intent seleccionarFoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(seleccionarFoto, Constantes.SELECT_PHOTO_GALLERY);

        Toast.makeText(this, "Se han concedido permisos necesarios", Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

        Toast.makeText(this, "No se puede seleccionar la foto, no se han concedido permisos necesarios", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {

    }
}