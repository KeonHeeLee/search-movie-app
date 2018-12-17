package keonheelee.github.io.searchmovie.model;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import keonheelee.github.io.searchmovie.model.data.Movie;

public class MainModel {
    private Activity activity;

    public MainModel(Activity activity){
        this.activity = activity;
    }

    public void setMovieList(JSONObject mResult,
                             ArrayList<Movie> movieList){
        movieList.clear();
        try{
            JSONArray list = mResult.getJSONArray("items");
            int len = list.length();
            if(len == 0)
                Toast.makeText(activity,
                        "검색 결과가 없습니다 :(", Toast.LENGTH_SHORT).show();

            else {
                for (int i = 0; i < list.length(); ++i) {
                    JSONObject node = list.getJSONObject(i);
                    String image = node.getString("image");
                    String title = node.getString("title");
                    int userRating = node.getInt("userRating");
                    int year = node.getInt("pubDate");
                    String director = node.getString("director");
                    String actor = node.getString("actor");
                    String link = node.getString("link");

                    movieList.add(new Movie(
                            image, title, userRating, year, director, actor, link));
                }
            }
        } catch(JSONException | NullPointerException e){
            e.printStackTrace();
            Toast.makeText(activity,
                    "Error:" +e.toString(), Toast.LENGTH_SHORT).show();
            mResult = null;
        }
    }
}
