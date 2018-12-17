package keonheelee.github.io.searchmovie.components;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;

import io.reactivex.observers.DefaultObserver;
import keonheelee.github.io.searchmovie.R;
import keonheelee.github.io.searchmovie.data.Movie;

public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder> {

    private Context context;
    private ArrayList<Movie> movieList;
    private ImageLoader mImageLoader;

    public MovieAdapter(Context context){
        this.context = context;
    }

    public void setViewHolder(ArrayList<Movie> movieList, ImageLoader mImageLoader){
        this.movieList = movieList;
        this.mImageLoader = mImageLoader;
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

        // RxJava를 통한 링크 이동 및 에러처리
        RxView.clicks(holder.itemView).subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(Object o) {
                Uri uri = Uri.parse(movie.getLink());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(context,
                        "링크 오류: "+e.toString(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onComplete() {

            }
        });
    }

    @Override
    public int getItemCount() {
        return (movieList == null ? 0 : movieList.size());
    }
}