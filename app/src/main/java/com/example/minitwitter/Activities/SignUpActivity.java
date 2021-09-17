package com.example.minitwitter.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.minitwitter.MainActivity;
import com.example.minitwitter.retrofit.request.RequestSignup;
import com.example.minitwitter.retrofit.response.ResponseAuth;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.MiniTwitterClient;
import com.example.minitwitter.retrofit.MiniTwitterService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnSign;
    private TextView tvGoLogin;
    private EditText etUser, etEmail, etPass;
    private MiniTwitterClient client;
    private MiniTwitterService service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        retrofitInit();
        getSupportActionBar().hide();
        setUpElements();

    }

    private void retrofitInit() {
        client = MiniTwitterClient.getInstance();
        service = client.getMiniTwitterService();
    }

    private void setUpElements() {
        etUser = findViewById(R.id.et_name_sign);
        etEmail = findViewById(R.id.et_email_sign);
        etPass = findViewById(R.id.et_pass_sign);
        btnSign = findViewById(R.id.btn_sign);
        tvGoLogin = findViewById(R.id.tv_goLogin);
        btnSign.setOnClickListener(this);
        tvGoLogin.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sign:
                goToSignUp();
                break;
            case R.id.tv_goLogin:
                goToLogin();
                break;
        }
    }

    private void goToSignUp() {
        String name = etUser.getText().toString();
        String email = etEmail.getText().toString();
        String pass = etPass.getText().toString();
        String code = "UDEMYANDROID";
        if (name.isEmpty()) {
            etUser.setError("Se requiere nombre de usuario");
        } else if (email.isEmpty()) {
            etEmail.setError("Se Requiere Email Valido");
        } else if (pass.isEmpty() || pass.length() < 4) {
            etPass.setError("Se requiere una contraseña válida");
        } else {
            RequestSignup signup = new RequestSignup(name, email, pass, code);
            Call<ResponseAuth> call = service.doSingUp(signup);
            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    if (response.isSuccessful()) {
                        // gUARDAMOS LOS DATOS DEL USER
                        SharedPreferencesManager.setStringValue(Constantes.PREF_TOKEN, response.body().getToken());
                        SharedPreferencesManager.setStringValue(Constantes.PREF_USERNAME, response.body().getUsername());
                        SharedPreferencesManager.setStringValue(Constantes.PREF_EMAIL, response.body().getEmail());
                        SharedPreferencesManager.setStringValue(Constantes.PREF_PHOTOURL, response.body().getPhotoUrl());
                        SharedPreferencesManager.setStringValue(Constantes.PREF_CREATED, response.body().getCreated());
                        SharedPreferencesManager.setBooleanValue(Constantes.PREF_ACTIVE, response.body().getActive());

                        Intent in = new Intent(SignUpActivity.this, DashboardActivity.class);
                        startActivity(in);
                        finish();
                    } else {
                        Toast.makeText(SignUpActivity.this, "Algo a ido mal. revise los datos", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "No hay conexion, pruebe de nuevo", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void goToLogin() {
        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

}