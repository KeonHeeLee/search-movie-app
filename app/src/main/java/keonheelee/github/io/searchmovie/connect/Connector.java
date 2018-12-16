package keonheelee.github.io.searchmovie.connect;

import android.content.Context;
import android.databinding.ObservableArrayList;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import keonheelee.github.io.searchmovie.MainActivity;
import keonheelee.github.io.searchmovie.data.ClientKey;
import keonheelee.github.io.searchmovie.data.Movie;

public class Connector {
    private final String TAG = "Connector Class";

    private final String MOVIE_API_URL
            = "https://openapi.naver.com/v1/search/movie.json";
    private RequestQueue mQueue;
    private JSONObject mResult;
    private Context context;

    public Connector(Context context){
        this.context = context;
        mQueue = Volley.newRequestQueue(this.context);
    }

    public void requestMovieList(String keyword, final ArrayList<Movie> movieList){
        try{
            String url = MOVIE_API_URL + "?query="
                    + URLEncoder.encode(keyword, "UTF-8");

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            mResult = response;
                            setMovieList(movieList);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(context,
                            "서버로부터 데이터를 얻어오는 데 실패하였습니다.\n"
                                    + error.toString(), Toast.LENGTH_SHORT ).show();
                }
            }
            ){
              @Override
              public Map<String, String> getHeaders() throws AuthFailureError{
                  Map<String, String> params = new HashMap<String, String>();
                  params.put("X-Naver-Client-Id", ClientKey.X_NAVER_CLIENT_ID);
                  params.put("X-Naver-Client-Secret", ClientKey.X_NAVER_CLIENT_SECRET);

                  return params;
              }
            };

            mQueue.add(request);

        } catch (UnsupportedEncodingException e){
            Toast.makeText(context, "알 수 없는 url형식입니다.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setMovieList(ArrayList<Movie> movieList){
        movieList.clear();
        try{
            JSONArray list = mResult.getJSONArray("items");
            for(int i=0; i<list.length(); ++i) {
                JSONObject node = list.getJSONObject(i);
                String image = node.getString("image");
                String title = node.getString("title");
                int userRating = node.getInt("userRating");
                int year = node.getInt("pubDate");
                String director = node.getString("director");
                String actor = node.getString("actor");
                String link = node.getString("link");

                Log.i(TAG, title);
                movieList.add(new Movie(
                        image, title, userRating, year, director, actor, link));
            }
        } catch(JSONException | NullPointerException e){
            e.printStackTrace();
            Toast.makeText(context,
                    "Error:" +e.toString(), Toast.LENGTH_SHORT).show();
            mResult = null;
        }
    }

    public void stop(){
        if(mQueue != null)
            mQueue.cancelAll(TAG);
    }
}
