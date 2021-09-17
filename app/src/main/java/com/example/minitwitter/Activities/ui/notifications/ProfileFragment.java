package com.example.minitwitter.Activities.ui.notifications;

import android.Manifest;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.databinding.FragmentProfileBinding;
import com.example.minitwitter.retrofit.request.RequestUserProfile;
import com.example.minitwitter.retrofit.response.ResponseUserProfile;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.single.CompositePermissionListener;
import com.karumi.dexter.listener.single.DialogOnDeniedPermissionListener;
import com.karumi.dexter.listener.single.PermissionListener;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private ProfileViewModel profileViewModel;
    private ImageView ivAvatar;
    private EditText etUsername, etEmail, etPass, etWebsite, etDesc;
    private Button btnSave, btnChangePass;
    private FragmentProfileBinding binding;
    private PermissionListener allPermisionsListener;
    private boolean loadingData = true;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Deprcated Use of ViewModelProviders.of
        // profileViewModel = ViewModelProviders.of(this).get(ProfileViewModel.class);
        // TODO revisar que este correcion funciona
        profileViewModel = new ViewModelProvider(getActivity()).get(ProfileViewModel.class);
    }

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO esta frase Funcionaba bien. Ahora ha sido movida al oncreate.
        // TODO deberia seguir funcionando. COMPROBAR
        // profileViewModel = new ViewModelProvider(getActivity()).get(ProfileViewModel.class);

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ivAvatar = binding.ivAvatarProfile;
        etUsername = binding.etUserProfile;
        etEmail = binding.etUserEmailProfile;
        etPass = binding.etPassProfile;
        etWebsite = binding.etUserWebProfile;
        etDesc = binding.etUserDescriptionProfile;
        btnSave = binding.btnSaveProfile;
        btnChangePass = binding.btnChangepassProfile;
        // Eventos botones
        btnSave.setOnClickListener(this);
        btnChangePass.setOnClickListener(this);
        ivAvatar.setOnClickListener(this);



        // ViewModel
        profileViewModel.userProfile.observe(getActivity(), new Observer<ResponseUserProfile>() {
            @Override
            public void onChanged(ResponseUserProfile responseUserProfile) {
                loadingData = false;
                etUsername.setText(responseUserProfile.getUsername());
                etEmail.setText(responseUserProfile.getEmail());
                etWebsite.setText(responseUserProfile.getWebsite());
                etDesc.setText(responseUserProfile.getDescripcion());

                if (!responseUserProfile.getPhotoUrl().isEmpty()) {
                    Glide.with(getActivity())
                            .load(Constantes.API_MINITWITTER_FILES + responseUserProfile.getPhotoUrl())
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
                            .skipMemoryCache(true)
                            .into(ivAvatar);
                }

                if (!loadingData) {
                    btnSave.setEnabled(true);
                    Toast.makeText(getActivity(), "Datos Guardados correctamente", Toast.LENGTH_SHORT).show();
                }

            }
        });

        profileViewModel.photoProfile.observe(getActivity(), new Observer<String>() {
            @Override
            public void onChanged(String photo) {
                if (!photo.isEmpty()) {
                    Glide.with(getActivity())
                            .load(Constantes.API_MINITWITTER_FILES + photo)
                            .dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .centerCrop()
                            .skipMemoryCache(true)
                            .into(ivAvatar);
                }
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save_profile:
                profileSave();
                // Toast.makeText(getActivity(), "Pulsaste en guardar datos", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_changepass_profile:
                Toast.makeText(getActivity(), "Pulsaste en Cambiar COntraseña", Toast.LENGTH_SHORT).show();
                break;
            case R.id.iv_avatar_profile:

                checkPermissions();
                // Invocar a la seleccion de la fotografia
                Toast.makeText(getActivity(), "Pulsaste en Cambiar la Foto", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void checkPermissions() {

        PermissionListener dialogDenied = DialogOnDeniedPermissionListener.Builder
                .withContext(getActivity())
                .withTitle("Permisos")
                .withMessage("Los permisos solicitados son necesarios para solicitar una foto de perfil")
                .withButtonText("Aceptar")
                .withIcon(R.mipmap.ic_launcher)
                .build();

        allPermisionsListener = new CompositePermissionListener((PermissionListener) getActivity(), dialogDenied);

        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(allPermisionsListener)
                .check();

    }

    private void profileSave() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String descripcion = etDesc.getText().toString();
        String website = etWebsite.getText().toString();
        String password = etPass.getText().toString();

        if (username.isEmpty()) {
            etUsername.setError("El nombre de usuario es requerido");
        } else if (email.isEmpty()) {
            etEmail.setError("El Email del usuario es requerido");
        } else if (password.isEmpty()) {
            etPass.setError("La contraseña es requerida para hacer cambios");
        } else {
            RequestUserProfile requestProfile = new RequestUserProfile(username, email, descripcion, website, password);
            profileViewModel.updateProfile(requestProfile);
            Toast.makeText(getActivity(), "Enviando informacion al servidor", Toast.LENGTH_SHORT).show();
            btnSave.setEnabled(false);
        }

    }
}