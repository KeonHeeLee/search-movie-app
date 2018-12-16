package keonheelee.github.io.searchmovie;

import android.support.v7.app.AppCompatActivity;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableArrayList;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;

import keonheelee.github.io.searchmovie.connect.Connector;
import keonheelee.github.io.searchmovie.databinding.ActivityMainBinding;
import keonheelee.github.io.searchmovie.components.MovieAdapter;
import keonheelee.github.io.searchmovie.data.Movie;

public class MainActivity extends AppCompatActivity {
    // 테스트용
    private static final String TAG = "Hello";

    // 상수 선언
    private static final String MOVIE_API_URL
            = "https://openapi.naver.com/v1/search/movie.json";

    // 기타 데이터타입 선언
    private MovieAdapter mAdapter = null;
    private Connector conn;

    // 데이터바인딩
    private ActivityMainBinding binding;
    private ArrayList<Movie> movieList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);

        movieList = new ArrayList<Movie>();
        mAdapter = new MovieAdapter(this, movieList);

        setRecyclerView();

        binding.setActivity(this);
    }

    private void setRecyclerView(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                binding.listMain.getContext(), linearLayoutManager.getOrientation());
        binding.listMain.addItemDecoration(dividerItemDecoration);

        binding.listMain.setLayoutManager(linearLayoutManager);
        binding.listMain.setAdapter(mAdapter);

        ViewGroup.LayoutParams layoutParams = binding.listMain.getLayoutParams();
        layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        binding.listMain.setLayoutParams(layoutParams);
    }

    public void onClickSearchButton(View view) {
        String keyword = binding.searchText.getText().toString();

        // 검색어 입력여부 검사
        if (keyword.equals(""))
            Toast.makeText(this,
                    "검색어를 입력해주세요.", Toast.LENGTH_SHORT).show();
        else
            prepareMovieList(keyword);

    }

    private void prepareMovieList(String keyword){
        conn = new Connector(this);
        conn.requestMovieList(keyword, movieList);

        for(int i=0; i<movieList.size(); ++i)
            Log.i(TAG, movieList.get(i).getTitle());

        // 검색 결과 처리
        if(movieList.size() != 0)
            mAdapter.notifyDataSetChanged();

        else
            Toast.makeText(this,
                    "검색 결과가 없습니다.", Toast.LENGTH_SHORT).show();

    }

    @Override
    protected void onStop() {
        super.onStop();
        conn.stop();
        mAdapter.stop();
    }
}
