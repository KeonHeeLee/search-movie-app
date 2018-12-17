package keonheelee.github.io.searchmovie.viewmodel;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import keonheelee.github.io.searchmovie.viewmodel.components.LruBitmapCache;
import keonheelee.github.io.searchmovie.viewmodel.components.MovieAdapter;
import keonheelee.github.io.searchmovie.contract.MainContract;
import keonheelee.github.io.searchmovie.model.data.ClientKey;
import keonheelee.github.io.searchmovie.model.data.Movie;
import keonheelee.github.io.searchmovie.model.MainModel;

public class MainViewModel implements MainContract{
    // 테스트용
    private static final String TAG = "Hello";

    // 상수 선언
    private static final String MOVIE_API_URL
            = "https://openapi.naver.com/v1/search/movie.json";

    // 데이터 선언
    private Activity activity;
    private RequestQueue mQueue;
    private JSONObject mResult;
    private ImageLoader mImageLoader;

    // MVVM
    private MainModel mainModel;

    public MainViewModel(Activity activity){
        this.activity = activity;
        mQueue = Volley.newRequestQueue(activity);
        mainModel = new MainModel(activity);
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(activity));
    }

    @Override
    public void setRequest(String keyword,
                           final ArrayList<Movie> movieList,
                           final MovieAdapter mAdapter) {
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
                            mainModel.setMovieList(mResult, movieList);
                            mAdapter.setViewHolder(movieList, mImageLoader);
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    showToast("서버로부터 데이터를 얻어오는 데 실패하였습니다.\n"
                                    + error.toString());
                }
            }
            ){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("X-Naver-Client-Id", ClientKey.X_NAVER_CLIENT_ID);
                    params.put("X-Naver-Client-Secret", ClientKey.X_NAVER_CLIENT_SECRET);

                    return params;
                }
            };

            mQueue.add(request);

        } catch (UnsupportedEncodingException e){
            showToast("알 수 없는 url형식입니다.");
        }
    }

    @Override
    public void onStop() {
        if(mQueue != null)
            mQueue.cancelAll(TAG);
    }

    private void showToast(String content){
        Toast.makeText(activity.getApplicationContext(), content, Toast.LENGTH_SHORT).show();
    }
}
