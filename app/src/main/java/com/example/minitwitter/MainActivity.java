package com.example.minitwitter;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minitwitter.Activities.DashboardActivity;
import com.example.minitwitter.Activities.SignUpActivity;
import com.example.minitwitter.retrofit.request.RequestLogin;
import com.example.minitwitter.retrofit.response.ResponseAuth;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.databinding.ActivityMainBinding;
import com.example.minitwitter.retrofit.MiniTwitterClient;
import com.example.minitwitter.retrofit.MiniTwitterService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLogin;
    private TextView tvGoSignUp;
    private EditText etEmail, etPass;
    private MiniTwitterClient client;
    private MiniTwitterService service;
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Objetos del View Binding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        // setContentView(binding.getRoot());
        View view = binding.getRoot();
        setContentView(view);
        // Esto queda obsoleto
        // setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        retrofitInit();
        setUpElements();

        // goToDash();
        // gettingToken();

    }

    private void gettingToken() {
        String token = SharedPreferencesManager.getStringValue(Constantes.PREF_TOKEN);
        if (!token.equals("")) {
            goToDash();
        } else {
            Toast.makeText(this, "no hay token", Toast.LENGTH_SHORT).show();
        }
    }

    private void retrofitInit() {
        client = MiniTwitterClient.getInstance();
        service = client.getMiniTwitterService();
    }

    private void setUpElements() {
        etEmail = binding.etEmail;
        etPass = binding.etPass;
        btnLogin = binding.btnLogin;
        tvGoSignUp = binding.tvGosignup;
        /*btnLogin = findViewById(R.id.btn_login);
        tvGoSignUp = findViewById(R.id.tv_gosignup);
        etEmail = findViewById(R.id.et_email);
        etPass = findViewById(R.id.et_pass);*/
        btnLogin.setOnClickListener(this);
        tvGoSignUp.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_login:
                // Toast.makeText(this, "El boton funciona bien", Toast.LENGTH_SHORT).show();
                goToLogin();
                // goToDash();
                // Toast.makeText(this, etPass.getText().toString(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_gosignup:
                goToSignUp();
                break;
        }
    }

    private void goToLogin() {
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();
        if (email.isEmpty()) {
            etEmail.setError("Se Requiere Email Valido");
        } else if (pass.isEmpty()) {
            etPass.setError("Se Requiere una contraseña Valida");
        } else {
            RequestLogin login = new RequestLogin(email, pass);
            Call<ResponseAuth> call = service.doLogin(login);
            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Sesion iniciada con Exito", Toast.LENGTH_SHORT).show();

                        SharedPreferencesManager.setStringValue(Constantes.PREF_TOKEN, response.body().getToken());
                        SharedPreferencesManager.setStringValue(Constantes.PREF_USERNAME, response.body().getUsername());
                        SharedPreferencesManager.setStringValue(Constantes.PREF_EMAIL, response.body().getEmail());
                        SharedPreferencesManager.setStringValue(Constantes.PREF_PHOTOURL, response.body().getPhotoUrl());
                        SharedPreferencesManager.setStringValue(Constantes.PREF_CREATED, response.body().getCreated());
                        SharedPreferencesManager.setBooleanValue(Constantes.PREF_ACTIVE, response.body().getActive());

                        Intent in = new Intent(MainActivity.this, DashboardActivity.class);
                        startActivity(in);
                        // destruimos esta activity
                        finish();
                    } else {
                        Toast.makeText(MainActivity.this, "Algo Salio Mal. Revise sus datos de acceso", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Problemas de conexion, intentelo de nuevo", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goToDash() {
        Intent i = new Intent(MainActivity.this, DashboardActivity.class);
        startActivity(i);
        finish();
    }

    private void goToSignUp() {
        Intent i = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(i);
        finish();
    }
}