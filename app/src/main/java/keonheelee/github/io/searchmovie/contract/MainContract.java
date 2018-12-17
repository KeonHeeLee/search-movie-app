package keonheelee.github.io.searchmovie.contract;

import java.util.ArrayList;

import keonheelee.github.io.searchmovie.components.MovieAdapter;
import keonheelee.github.io.searchmovie.data.Movie;

public interface MainContract {
    public void setRequest(String keyword, ArrayList<Movie> movieList, MovieAdapter mAdapter);
    public void onStop();
}
