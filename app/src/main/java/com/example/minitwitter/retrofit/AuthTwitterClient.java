package com.example.minitwitter.retrofit;

import com.example.minitwitter.common.Constantes;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthTwitterClient {

    private static AuthTwitterClient instance = null;
    private AuthTwitterService AuthTwitterService;
    private Retrofit retrofit;

    public AuthTwitterClient() {

        // Incluimos en la cabezera de la peticion el token que autoriza al usuario
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(new AuthInterceptor());
        OkHttpClient client = builder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.API_MINITWITTER_BASEURL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        AuthTwitterService = retrofit.create(AuthTwitterService.class);
    }

    // Patron Singleton se asegura de que solo se crea una instancia y siempre se recupera la misma instacia
    public static AuthTwitterClient getInstance() {
        if (instance == null) {
            instance = new AuthTwitterClient();
        }
        return instance;
    }

    public AuthTwitterService getAuthTwitterService() {
        return AuthTwitterService;
    }

}
