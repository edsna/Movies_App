package com.blogspot.progectoscaseiros.movies_app;

//import android.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blogspot.progectoscaseiros.movies_app.adapter.MoviesAdapter;
import com.blogspot.progectoscaseiros.movies_app.api.Client;
import com.blogspot.progectoscaseiros.movies_app.api.Service;
import com.blogspot.progectoscaseiros.movies_app.model.Movie;
import com.blogspot.progectoscaseiros.movies_app.model.MoviesResponse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    //Fields/views
    private RecyclerView recyclerView;
    private MoviesAdapter adapter;
    private List<Movie> movieList;
    private ProgressDialog pd;
    private SwipeRefreshLayout swipeContainer;
    private AppCompatActivity activity = MainActivity.this;
    public static final String LOG_TAG = MoviesAdapter.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();    //grabs attributes and styles fro the view
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.main_content);  //refresh when u swipe main_activity
        swipeContainer.setColorSchemeResources(android.R.color.holo_orange_dark);   //when u swipe show this orange dark color
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {    //Then do the following
            @Override
            public void onRefresh() {
                initViews();
                Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.RefreshMoviesFeedback), Snackbar.LENGTH_LONG);
                snackbar.show();
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
        pd.setMessage(getString(R.string.Fetching_Movies));
        pd.setCancelable(false);
        pd.show();

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        movieList = new ArrayList<>();
        //if landscape is on point 2/4grid
        adapter = new MoviesAdapter(this, movieList);

        if(getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            recyclerView.setLayoutManager(new GridLayoutManager(this,2));
        }else{
            recyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        checkSortOrder();
    }

    private void loadJSON(){
        //Getting the values from JSON
        try{
            if(BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){ //If there's no API key
                Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.AddAPIKey), Snackbar.LENGTH_LONG);
                Snackbar okay = snackbar.setAction("Okay", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this,"Action Clicked",Toast.LENGTH_LONG);
                    }
                });
                snackbar.show();
                pd.dismiss(); //No data, then we're out
                return;//Get out or return
            }
            //However if there is an API key:
            Client Client = new Client();   //Call the client
            Service apiService = //Call the service
                    Client.getClient().create(Service.class); //Get the client method from client class
            Call<MoviesResponse> call = apiService.getPopularMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();//gets all the movies list and stores them in the result page
                    Collections.sort(movies, Movie.BY_NAME_ALPHABETICAL);
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.ErrFetchingData), Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }

    //Using the API to call the most_rated movies
    private void loadJSONTRM(){
        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.ObtainAPI), Snackbar.LENGTH_LONG);
                Snackbar okay = snackbar.setAction("Okay", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(MainActivity.this,"Action Clicked",Toast.LENGTH_LONG);
                    }
                });
                snackbar.show();
                pd.dismiss();
                return;
            }
            Client Client = new Client();
            Service apiService =
                    Client.getClient().create(Service.class);
            Call<MoviesResponse> call = apiService.getTopRatedMovies(BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<MoviesResponse>() {
                @Override
                public void onResponse(Call<MoviesResponse> call, Response<MoviesResponse> response) {
                    List<Movie> movies = response.body().getResults();
                    recyclerView.setAdapter(new MoviesAdapter(getApplicationContext(), movies));
                    recyclerView.smoothScrollToPosition(0);
                    if (swipeContainer.isRefreshing()){
                        swipeContainer.setRefreshing(false);
                    }
                    pd.dismiss();
                }

                @Override
                public void onFailure(Call<MoviesResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.ErrFetchingData), Snackbar.LENGTH_LONG);
                    Snackbar okay = snackbar.setAction("Okay", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(MainActivity.this,"Action Clicked",Toast.LENGTH_LONG);
                        }
                    });
                    snackbar.show();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage()); //Only shows the error message for the developer
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menu_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);//launch settings when menu is clicked
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s){
        Log.d(LOG_TAG, getString(R.string.Pref_update));
        checkSortOrder();
    }
    private void checkSortOrder(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String sortOrder = preferences.getString(
                this.getString(R.string.pref_sort_order_key),
                this.getString(R.string.pref_most_popular)
        );
        if(sortOrder.equals(this.getString(R.string.pref_most_popular))){
            Log.d(LOG_TAG, getString(R.string.Sorting_M_Pop_Mov));
            loadJSON();
        }else{
            Log.d(LOG_TAG, getString(R.string.SortingMoviesByRatingScale));
            loadJSONTRM();
        }
    }
    @Override
    //Checks sort order if the movie list is empty
    public void onResume(){
        super.onResume();
        if(movieList.isEmpty()){
            checkSortOrder();
        }else{

        }
    }
}
