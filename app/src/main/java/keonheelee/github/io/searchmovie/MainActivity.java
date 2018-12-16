package keonheelee.github.io.searchmovie;

import android.app.Activity;
import android.content.Intent;
import android.databinding.DataBindingComponent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
import keonheelee.github.io.searchmovie.components.MovieInfoViewHolder;
import keonheelee.github.io.searchmovie.data.ClientKey;
import keonheelee.github.io.searchmovie.data.Movie;
import keonheelee.github.io.searchmovie.databinding.ActivityMainBinding;

public class MainActivity extends Activity {
    // 테스트용
    private static final String TAG = "Hello";

    // 상수 선언
    private static final String MOVIE_API_URL
            = "https://openapi.naver.com/v1/search/movie.json";

    // 뷰 컴포넌트
    protected EditText searchText = null;
    protected Button searchBtn = null;
    protected RecyclerView listMain = null;
    protected LinearLayoutManager mLinearLayoutManager = null;

    // 기타 데이터타입 선언
    private ArrayList<Movie> mList;
    private MovieAdapter mAdapter = null;
    protected RequestQueue mQueue = null;
    protected ImageLoader mImageLoader = null;
    protected String searchKeyword = null;
    protected JSONObject mResult = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchText = (EditText) findViewById(R.id.searchText);
        searchBtn = (Button) findViewById(R.id.searchBtn);
        listMain = (RecyclerView) findViewById(R.id.listMain);

        mList = new ArrayList<Movie>();
        mAdapter = new MovieAdapter();

        mQueue = Volley.newRequestQueue(this);
        mImageLoader = new ImageLoader(mQueue, new LruBitmapCache(this));
        mLinearLayoutManager = new LinearLayoutManager(this);

        listMain.setLayoutManager(mLinearLayoutManager);
        listMain.setAdapter(mAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                listMain.getContext(), mLinearLayoutManager.getOrientation());
        listMain.addItemDecoration(dividerItemDecoration);

        ViewGroup.LayoutParams layoutParams = listMain.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        listMain.setLayoutParams(layoutParams);

    } // end onCreate

    public void onButtonClick(View view){
        searchKeyword = searchText.getText().toString();
        if(searchKeyword.equals("") || searchKeyword == null) {
            Toast.makeText(MainActivity.this,
                    "키워드를 입력해주세요.", Toast.LENGTH_SHORT).show();
        } else {
            requestJSON();
        }
    }

    protected void requestJSON(){
        try {
            String url = MOVIE_API_URL + "?query="
                    + URLEncoder.encode(searchKeyword, "UTF-8");

            JsonObjectRequest request = new JsonObjectRequest(
                    Request.Method.GET,
                    url,
                    null,
                    new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    mResult = response;
                    drawMovieList();
                }
            }
                    , new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(MainActivity.this
                            , error.toString(), Toast.LENGTH_SHORT).show();
                }
            }){
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
            Toast.makeText(MainActivity.this, e.toString(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void drawMovieList(){
        mList.clear();
        try{
            JSONArray list = mResult.getJSONArray("items");
            for(int i=0; i<list.length(); ++i){
                JSONObject node = list.getJSONObject(i);
                String image = node.getString("image");
                String title = node.getString("title");
                int userRating = node.getInt("userRating");
                int year = node.getInt("pubDate");
                String director = node.getString("director");
                String actor = node.getString("actor");
                String link = node.getString("link");

                mList.add(new Movie(
                        image, title, userRating, year, director, actor, link));
            }
        } catch (JSONException | NullPointerException e){
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

    public class MovieAdapter extends RecyclerView.Adapter<MovieInfoViewHolder>{
        @NonNull
        @Override
        public MovieInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_component, parent, false);

            return new MovieInfoViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MovieInfoViewHolder holder, int position) {
            final Movie info = mList.get(position);
            holder.image.setImageUrl(info.getImageUrl(), mImageLoader);
            holder.txTitle.setText(info.getTitle());
            holder.userRating.setRating((float)info.getUserRating() / 2);
            holder.txYear.setText(String.valueOf(info.getYear()));
            holder.txDirector.setText(info.getDirector());
            holder.txActor.setText(info.getActor());

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Uri uri = Uri.parse(info.getLink());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            return (mList == null ? 0 : mList.size());
        }
    }
}
