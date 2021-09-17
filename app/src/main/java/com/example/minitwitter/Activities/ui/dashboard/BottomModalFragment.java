package com.example.minitwitter.Activities.ui.dashboard;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.data.TweetViewModel;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;

public class BottomModalFragment extends BottomSheetDialogFragment {

    private TweetViewModel tweetViewModel;
    private int idTweetEliminar;

    public static BottomModalFragment newInstance(int idTweet) {
        BottomModalFragment fragment = new BottomModalFragment();
        Bundle args = new Bundle();
        args.putInt(Constantes.ARG_TWEET_ID, idTweet);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
            idTweetEliminar = getArguments().getInt(Constantes.ARG_TWEET_ID);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View v =  inflater.inflate(R.layout.bottom_modal_fragment, container, false);

        final NavigationView nav = v.findViewById(R.id.navigation_view_bottom_tweet);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull @NotNull MenuItem item) {
                int id = item.getItemId();

                if (id == R.id.action_delete_tweet) {
                    tweetViewModel.deleteTweet(idTweetEliminar);
                    getDialog().dismiss();

                }

                return false;
            }
        });



        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tweetViewModel = new ViewModelProvider(getActivity()).get(TweetViewModel.class);
        // TODO: Use the ViewModel
    }

}