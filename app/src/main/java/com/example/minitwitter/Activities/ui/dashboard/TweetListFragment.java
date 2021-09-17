package com.example.minitwitter.Activities.ui.dashboard;

import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.minitwitter.retrofit.response.Tweet;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.data.TweetViewModel;

import java.util.List;


public class TweetListFragment extends Fragment {

    private int tweetListType = 1;
    private MyTweetRecyclerViewAdapter  adapter;
    private List<Tweet> tweetList;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeLayout;
    private TweetViewModel viewModel;

    public TweetListFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static TweetListFragment newInstance(int tweetListType) {
        TweetListFragment fragment = new TweetListFragment();
        Bundle args = new Bundle();
        args.putInt(Constantes.TWEET_LIST_TYPE, tweetListType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO revisar esta linea que marca como obsoleta
        // viewModel = ViewModelProviders.of(getActivity()).get(TweetViewModel.class);
        // TODO revisar que esta correccion de la linea anterior obsoleta
        viewModel = new ViewModelProvider(getActivity()).get(TweetViewModel.class);
        if (getArguments() != null) {
            tweetListType = getArguments().getInt(Constantes.TWEET_LIST_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tweet_list_list, container, false);

        // Set the adapter
            Context context = view.getContext();
            recyclerView = view.findViewById(R.id.list);
            swipeLayout = view.findViewById(R.id.swiperefresh);
            swipeLayout.setColorSchemeColors(getResources().getColor(R.color.azul));

            // Evento de refresco con gesto
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeLayout.setRefreshing(true);

                if (tweetListType == Constantes.TWEET_LIST_ALL) {
                    loadNewData();

                } else if (tweetListType == Constantes.TWEET_LIST_FAVS) {
                    loadNewFavData();

                }

            }
        });


        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new MyTweetRecyclerViewAdapter(getActivity(), tweetList);
        recyclerView.setAdapter(adapter);

        if (tweetListType == Constantes.TWEET_LIST_ALL) {
            loadTweetData();
        } else if (tweetListType == Constantes.TWEET_LIST_FAVS) {
            loadFavTweetData();
        }

        return view;
    }

    private void loadNewFavData() {
        viewModel.getNewFavTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                swipeLayout.setRefreshing(false);
                adapter.setData(tweetList);
                viewModel.getNewFavTweets().removeObserver(this);
            }
        });
    }

    private void loadFavTweetData() {

        viewModel.getFavTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                adapter.setData(tweetList);
            }
        });

    }

    private void loadTweetData() {

        viewModel.getTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                // swipeLayout.setRefreshing(false);
                adapter.setData(tweetList);
            }
        });


    }

    private void loadNewData() {

        viewModel.getNewTweets().observe(getActivity(), new Observer<List<Tweet>>() {
            @Override
            public void onChanged(List<Tweet> tweets) {
                tweetList = tweets;
                swipeLayout.setRefreshing(false);
                adapter.setData(tweetList);
                viewModel.getNewTweets().removeObserver(this);
            }
        });


    }
}