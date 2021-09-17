package com.example.minitwitter.data;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.minitwitter.Activities.ui.dashboard.BottomModalFragment;
import com.example.minitwitter.retrofit.response.Tweet;

import java.util.List;

public class TweetViewModel extends AndroidViewModel {

    private TweetRepository repository;
    private LiveData<List<Tweet>> tweets;
    private LiveData<List<Tweet>> favTweets;

    public TweetViewModel(@NonNull Application application) {
        super(application);
        repository = new TweetRepository();
        tweets = repository.getAlltweets();


    }

    public LiveData<List<Tweet>> getTweets() {
        return tweets;
    }

    public LiveData<List<Tweet>> getFavTweets() {
        favTweets = repository.getFavtweets();
        return favTweets;
    }

    public LiveData<List<Tweet>> getNewTweets() {
        tweets = repository.getAlltweets();
        return tweets;
    }

    public LiveData<List<Tweet>> getNewFavTweets() {
        getNewTweets();
        return getFavTweets();
    }

    public void insertTweet(String mensaje) {
        repository.createTweet(mensaje);
    }

    public void openDialogMenu(Context ctx, int idTweet) {
        BottomModalFragment dialogTweet = BottomModalFragment.newInstance(idTweet);
        dialogTweet.show(((AppCompatActivity)ctx).getSupportFragmentManager(), "BottomModalFragment");
    }

    public void deleteTweet(int idTweet) {
        repository.deleteTweet(idTweet);
    }

    public void likeTweet(int idTweet) {
        repository.likeTweet(idTweet);
    }

}
