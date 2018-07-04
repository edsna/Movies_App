package com.blogspot.progectoscaseiros.movies_app;

import android.app.ProgressDialog;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import com.blogspot.progectoscaseiros.movies_app.adapter.MoviesAdapter;
import com.blogspot.progectoscaseiros.movies_app.model.Movie;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    //Fields/views
    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    public static final String LOG_TAG = MoviesAdapter.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();


    }
}
