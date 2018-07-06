package com.blogspot.progectoscaseiros.movies_app;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.blogspot.progectoscaseiros.movies_app.adapter.MoviesAdapter;
import com.blogspot.progectoscaseiros.movies_app.model.Movie;

import java.util.ArrayList;
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

        initViews();    //grabs attributes and styles fro the view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);  //refresh when u swype main_activity
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);   //when u swipe show this orange dark color
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {    //Then do the following
            @Override
            public void onRefresh() {
                initViews();
                Toast.makeText(MainActivity.this,"Movies has been Refreshed", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Activity getActivity(){
        Context context = this;
        while(context instanceof ContextWrapper){
            if(context instanceof Activity){
                return (Activity)context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }


    private void initViews(){
        pd = new ProgressDialog(this);
        pd.setMessage("Fetching Movies...");
        pd.setCancelable(false);
        pd.show();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        movieList = new ArrayList<>();
        adapter = new MoviesAdapter(this, movieList);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        }else{
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        loadJSON();
    }
    private void loadJSON(){
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Toast.makeText(getApplicationContext()."Please obtain the API key", Toast.LENGTH_SHORT).show();
                pd.dismiss();
                return;
            }

        }
    }
}
