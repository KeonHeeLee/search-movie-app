package keonheelee.github.io.searchmovie.view;

import android.support.v7.app.AppCompatActivity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TableLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.jakewharton.rxbinding2.view.RxView;

import org.json.JSONObject;

import java.util.ArrayList;

import io.reactivex.observers.DefaultObserver;
import keonheelee.github.io.searchmovie.R;
import keonheelee.github.io.searchmovie.components.EndlessRecyclerViewScrollListener;
import keonheelee.github.io.searchmovie.components.MovieAdapter;
import keonheelee.github.io.searchmovie.databinding.ActivityMainBinding;
import keonheelee.github.io.searchmovie.data.Movie;
import keonheelee.github.io.searchmovie.viewmodel.MainViewModel;

public class MainActivity extends AppCompatActivity{
    // 테스트용
    private static final String TAG = "Hello";

    // 기타 데이터타입 선언
    private MovieAdapter mAdapter = null;
    private EndlessRecyclerViewScrollListener mEndlessScrollListener;

    private ActivityMainBinding activityMainBinding;
    private ArrayList<Movie> movieList;
    private String keyword;

    // MVVM
    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = DataBindingUtil
                .setContentView(this, R.layout.activity_main);

        mainViewModel = new MainViewModel(this);

        setRecyclerView();
        activityMainBinding.setActivity(this);

    }

    public void setRecyclerView(){
        movieList = new ArrayList<Movie>();
        mAdapter = new MovieAdapter(this);

        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                activityMainBinding.listMain.getContext(), linearLayoutManager.getOrientation());
        activityMainBinding.listMain.addItemDecoration(dividerItemDecoration);

        activityMainBinding.listMain.setLayoutManager(linearLayoutManager);

        mEndlessScrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                // 무한 스크롤 기능 구현(?)
            }
        };

        activityMainBinding.listMain.addOnScrollListener(mEndlessScrollListener);
        activityMainBinding.listMain.setAdapter(mAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mainViewModel.onStop();
    }

    public void onClickSearchButton(View view){
        RxView.clicks(activityMainBinding.searchBtn)
                .subscribe(new DefaultObserver<Object>() {
            @Override
            public void onNext(Object o) {
                keyword = activityMainBinding.searchText.getText().toString();

                // 검색어 입력여부 검사
                if (keyword.equals(""))
                    showToast("검색어를 입력해주세요.");

                else
                    renewRecyclerView();
            }

            @Override
            public void onError(Throwable e) {
                showToast("검색에 실패하였습니다.\n" + e.toString());
            }

            @Override
            public void onComplete() {

            }
        });
    }

    protected void renewRecyclerView(){
        mainViewModel.setRequest(keyword, movieList, mAdapter);
        mAdapter.notifyDataSetChanged();
    }

    protected void showToast(String content){
        Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
    }
}
