package keonheelee.github.io.searchmovie;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
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

import keonheelee.github.io.searchmovie.components.LruBitmapCache;
import keonheelee.github.io.searchmovie.components.MovieViewHolder;
import keonheelee.github.io.searchmovie.data.ClientKey;
import keonheelee.github.io.searchmovie.databinding.ActivityMainBinding;
import keonheelee.github.io.searchmovie.data.Movie;

public class MainActivity extends AppCompatActivity {
    // 테스트용
    private static final String TAG = "Hello";

    // 상수 선언
    private static final String MOVIE_API_URL
            = "https://openapi.naver.com/v1/search/movie.json";

    // 기타 데이터타입 선언
    private MovieAdapter mAdapter = null;
    private ImageLoader mImageLoader;
    private RequestQueue mQueue;
    private JSONObject mResult;

    // 데이터바인딩
    private ActivityMainBinding activityMainBinding;
    private ArrayList<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);

        movieList = new ArrayList<Movie>();
        mAdapter = new MovieAdapter();
        mQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(this));

        setRecyclerView();

        activityMainBinding.setActivity(this);
    }

    private void setRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                activityMainBinding.listMain.getContext(), linearLayoutManager.getOrientation());
        activityMainBinding.listMain.addItemDecoration(dividerItemDecoration);

        activityMainBinding.listMain.setLayoutManager(linearLayoutManager);

        ViewGroup.LayoutParams layoutParams = activityMainBinding.listMain.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        activityMainBinding.listMain.setLayoutParams(layoutParams);


        activityMainBinding.listMain.setAdapter(mAdapter);
    }

    public void onClickSearchButton(View view) {
        String keyword = activityMainBinding.searchText.getText().toString();

        // 검색어 입력여부 검사
        if (keyword.equals(""))
            Toast.makeText(this,
                    "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
        else
            requestMovieList(keyword);

    }

    /* mAdapter.notifyDataSetChanged();*/

    public void requestMovieList(String keyword){
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
                            setMovieList();
                        }
                    }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this,
                            "서버로부터 데이터를 얻어오는 데 실패하였습니다.\n"
                                    + error.toString(), Toast.LENGTH_SHORT ).show();
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
            Toast.makeText(this, "알 수 없는 url형식입니다.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void setMovieList(){
        movieList.clear();
        try{
            JSONArray list = mResult.getJSONArray("items");
            int len = list.length();
            if(len == 0)
                Toast.makeText(this,
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
            Toast.makeText(this,
                    "Error:" +e.toString(), Toast.LENGTH_SHORT).show();
            mResult = null;
        }

        mAdapter.notifyDataSetChanged();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if(mQueue != null)
            mQueue.cancelAll(TAG);
    }

    public class MovieAdapter extends RecyclerView.Adapter<MovieViewHolder>{
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
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return (movieList == null ? 0 : movieList.size());
        }

    }
}
