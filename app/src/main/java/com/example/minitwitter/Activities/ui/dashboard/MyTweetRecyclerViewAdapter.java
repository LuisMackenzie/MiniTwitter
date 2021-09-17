package com.example.minitwitter.Activities.ui.dashboard;

import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.minitwitter.retrofit.response.Like;
import com.example.minitwitter.retrofit.response.Tweet;
import com.example.minitwitter.R;
import com.example.minitwitter.common.Constantes;
import com.example.minitwitter.common.SharedPreferencesManager;
import com.example.minitwitter.data.TweetViewModel;
import com.example.minitwitter.databinding.FragmentTweetListBinding;

import java.util.List;


public class MyTweetRecyclerViewAdapter extends RecyclerView.Adapter<MyTweetRecyclerViewAdapter.ViewHolder> {

    private List<Tweet> mValues;
    private Context ctx;
    private FragmentTweetListBinding binding;
    private String username;
    private TweetViewModel tweetViewModel;


    public MyTweetRecyclerViewAdapter(Context contexto, List<Tweet> items) {
        ctx = contexto;
        mValues = items;
        username = SharedPreferencesManager.getStringValue(Constantes.PREF_USERNAME);

        // TODO Codigo obsoleto actualizado con exito al parecer
        // tweetViewModel = ViewModelProviders.of((FragmentActivity) ctx).get(TweetViewModel.class);
        // TODO Revisar correcioon de codigo obsoleto
        tweetViewModel = new ViewModelProvider((FragmentActivity) ctx).get(TweetViewModel.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        binding = FragmentTweetListBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);

        return new ViewHolder(binding);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        if (mValues != null) {

            holder.mItem = mValues.get(position);

            holder.tvUsername.setText("@" + holder.mItem.getUser().getUsername());
            holder.tvMessage.setText(holder.mItem.getMensaje());
            holder.tvLikesCount.setText(String.valueOf(holder.mItem.getLikes().size()));
            holder.showMenu.setVisibility(View.GONE);

            if (holder.mItem.getUser().getUsername().equals(username)) {
                holder.showMenu.setVisibility(View.VISIBLE);
            }

            holder.showMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetViewModel.openDialogMenu(ctx, holder.mItem.getId());

                }
            });

            holder.ivLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    tweetViewModel.likeTweet(holder.mItem.getId());
                }
            });

            String photo = holder.mItem.getUser().getPhotoUrl();
            if (!photo.equals("")) {
                Glide.with(ctx)
                        .load("https://www.minitwitter.com/apiv1/uploads/photos/" + photo)
                        .into(holder.ivAvatar);
            }

            Glide.with(ctx).load(R.drawable.ic_like).into(holder.ivLike);
            holder.tvLikesCount.setTextColor(ctx.getResources().getColor(R.color.black));
            holder.tvLikesCount.setTypeface(null, Typeface.NORMAL);

            for (Like like: holder.mItem.getLikes()) {
                if (like.getUsername().equals(username)) {

                    Glide.with(ctx).load(R.drawable.ic_like_pink).into(holder.ivLike);
                    holder.tvLikesCount.setTextColor(ctx.getResources().getColor(R.color.teal_200));
                    holder.tvLikesCount.setTypeface(null, Typeface.BOLD);
                    break;
                }
            }

        }

    }

    public void setData(List<Tweet> tweetList) {
        this.mValues = tweetList;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (mValues != null) {
            return mValues.size();
        } else {
            return 0;
        }

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public final ImageView ivAvatar;
        public final ImageView ivLike;
        public final ImageView showMenu;
        public final TextView tvLikesCount;
        public final TextView tvUsername;
        public final TextView tvMessage;
        public Tweet mItem;

        public ViewHolder(FragmentTweetListBinding binding) {
            super(binding.getRoot());
            ivAvatar = binding.ivAvatar;
            ivLike = binding.ivLikes;
            showMenu = binding.ivShowmenu;
            tvUsername = binding.tvUsername;
            tvMessage = binding.tvMensaje;
            tvLikesCount = binding.tvLikes;
        }

        @Override
        public String toString() {
            return super.toString() + " '" + tvUsername.getText() + "'";
        }
    }
}