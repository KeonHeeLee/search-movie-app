package keonheelee.github.io.searchmovie.components;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import keonheelee.github.io.searchmovie.R;

public class MovieInfoViewHolder extends RecyclerView.ViewHolder{
    public NetworkImageView image;
    public TextView txTitle;
    public RatingBar userRating;
    public TextView txYear;
    public TextView txDirector;
    public TextView txActor;

    public MovieInfoViewHolder(View itemView) {
        super(itemView);
        this.image = (NetworkImageView) itemView.findViewById(R.id.image);
        this.txTitle = (TextView) itemView.findViewById(R.id.title);
        this.userRating = (RatingBar) itemView.findViewById(R.id.userRating);
        this.txYear = (TextView) itemView.findViewById(R.id.year);
        this.txDirector = (TextView) itemView.findViewById(R.id.director);
        this.txActor = (TextView) itemView.findViewById(R.id.actor);
    }
}
