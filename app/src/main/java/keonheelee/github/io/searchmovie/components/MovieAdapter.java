package keonheelee.github.io.searchmovie.components;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import java.util.ArrayList;

import keonheelee.github.io.searchmovie.R;
import keonheelee.github.io.searchmovie.data.Movie;
import keonheelee.github.io.searchmovie.databinding.ListComponentBinding;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{
    private Context context;
    private ArrayList<Movie> movieList;
    private ImageLoader mImageLoader;
    private RequestQueue mQueue;

    public MovieAdapter(Context context, ArrayList<Movie> movieList){
        this.movieList = movieList;
        this.context = context;
        mQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(context));
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_component, parent, false);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieViewHolder holder, int position) {
        final Movie movie = movieList.get(position);
        holder.binding.setMovie(movie);

        holder.binding.userRating.setRating((float)movie.getUserRating() / 2);
        holder.binding.image.setImageUrl(movie.getImageUrl(), mImageLoader);
        holder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(movie.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }
        });
    }

    public void stop(){
        if(mQueue != null)
            mQueue.cancelAll("MovieAdapter Class");
    }

    @Override
    public int getItemCount() {
        return (movieList == null ? 0 : movieList.size());
    }

    class MovieViewHolder extends RecyclerView.ViewHolder{
        ListComponentBinding binding;

         MovieViewHolder(View itemView){
            super(itemView);
            binding = DataBindingUtil.bind(itemView);
        }
    }
}
