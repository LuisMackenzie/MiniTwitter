package com.example.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.example.minitwitter.retrofit.response.Like;
import com.example.minitwitter.retrofit.request.RequestCreateTweet;
import com.example.minitwitter.retrofit.response.Tweet;
import com.example.minitwitter.retrofit.response.TweetDeleted;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.MyApp;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.retrofit.AuthTwitterClient;
import com.example.minitwitter.retrofit.AuthTwitterService;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TweetRepository {

    private AuthTwitterService service;
    private AuthTwitterClient client;
    private MutableLiveData<List<Tweet>> alltweets;
    private MutableLiveData<List<Tweet>> favTweets;
    private String username;

    public TweetRepository() {
        client = AuthTwitterClient.getInstance();
        service = client.getAuthTwitterService();
        alltweets = getAlltweets();
        username = SharedPreferencesManager.getStringValue(Constantes.PREF_USERNAME);
    }

    public MutableLiveData<List<Tweet>> getAlltweets() {
        // final MutableLiveData<List<Tweet>> data = new MutableLiveData<>();
        if (alltweets == null) {
            alltweets = new MutableLiveData<>();
        }


        Call<List<Tweet>> call = service.getAllTweets();
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {

                if (response.isSuccessful()) {
                    // guardamos los tweets en la lista de tweets
                    alltweets.setValue(response.body());

                } else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error de conexion!", Toast.LENGTH_SHORT).show();
            }
        });

        return alltweets;
    }

    public MutableLiveData<List<Tweet>> getFavtweets() {

        if (favTweets == null) {
            favTweets = new MutableLiveData<>();
        }

        List<Tweet> newFavList = new ArrayList<>();
        Iterator itTweets = alltweets.getValue().iterator();

        while (itTweets.hasNext()) {
            Tweet current = (Tweet) itTweets.next();
            Iterator itLikes = current.getLikes().iterator();
            boolean enc = false;
            while (itLikes.hasNext() && !enc) {
                Like like = (Like) itLikes.next();
                if (like.getUsername().equals(username)) {
                    enc = true;
                    newFavList.add(current);
                }
            }
        }

        favTweets.setValue(newFavList);
        return favTweets;

    }

    public void createTweet(String mensaje) {
        RequestCreateTweet request = new RequestCreateTweet(mensaje);
        Call<Tweet> call = service.createTweet(request);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()) {
                    List<Tweet> clone = new ArrayList<>();
                    // añadimos en primer lugar el nuevo tweet del servidor
                    clone.add(response.body());
                    for (int i = 0; i < alltweets.getValue().size(); i++) {
                        clone.add(new Tweet(alltweets.getValue().get(i)));
                    }

                    alltweets.setValue(clone);

                } else {
                    Toast.makeText(MyApp.getContext(), "Algo a ido mal publicando el tweeet!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {

                Toast.makeText(MyApp.getContext(), "Error de cdonexion.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void deleteTweet(final int idTweet) {
        Call<TweetDeleted> call = service.deleteTweet(idTweet);
        call.enqueue(new Callback<TweetDeleted>() {
            @Override
            public void onResponse(Call<TweetDeleted> call, Response<TweetDeleted> response) {
                if (response.isSuccessful()) {

                    List<Tweet> clonedTweets = new ArrayList<>();
                    for(int i = 0;i < alltweets.getValue().size(); i++) {
                        if (alltweets.getValue().get(i).getId() != idTweet) {
                            clonedTweets.add(new Tweet(alltweets.getValue().get(i)));
                        }
                    }


                    alltweets.setValue(clonedTweets);
                    getFavtweets();

                } else {
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TweetDeleted> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error de conexion.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void likeTweet(int idTweet) {
        // RequestCreateTweet request = new RequestCreateTweet(mensaje);
        Call<Tweet> call = service.likeTweet(idTweet);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if (response.isSuccessful()) {
                    List<Tweet> clone = new ArrayList<>();
                    // añadimos en primer lugar el nuevo tweet del servidor
                    // clone.add(response.body());
                    for (int i = 0; i < alltweets.getValue().size(); i++) {
                        if (alltweets.getValue().get(i).getId() == idTweet) {
                            // si hemos encontrado en la lista original el elemento sobre el que hemos hecho like,
                            //  introducimos el elemento que nos ha llegado del servidor
                            clone.add(response.body());
                        } else {
                            clone.add(new Tweet(alltweets.getValue().get(i)));
                        }

                    }

                    alltweets.setValue(clone);

                    getFavtweets();

                } else {
                    Toast.makeText(MyApp.getContext(), "Algo a ido mal publicando el tweeet!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {

                Toast.makeText(MyApp.getContext(), "Error de cdonexion.", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
