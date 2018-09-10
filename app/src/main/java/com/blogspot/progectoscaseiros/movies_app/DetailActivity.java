package com.blogspot.progectoscaseiros.movies_app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.progectoscaseiros.movies_app.api.Client;
import com.blogspot.progectoscaseiros.movies_app.api.Service;
import com.blogspot.progectoscaseiros.movies_app.model.Movie;
import com.blogspot.progectoscaseiros.movies_app.model.Trailer;
import com.blogspot.progectoscaseiros.movies_app.model.TrailerResponse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static java.util.Objects.requireNonNull;

public class DetailActivity extends AppCompatActivity {
    //Brings the details of the movie
    TextView nameOfMovie;
    TextView plotSynopsis;
    TextView userRating;
    TextView releaseDate;
    ImageView imageView;
    private RecyclerView recyclerView;
    private Movie favorite;
    private final AppCompatActivity activity = DetailActivity.this;
    private TrailerAdapter adapter;
    private List<Trailer> trailerList;
    private FavoriteDbHelper favoriteDbHelper;

    Movie movie;
    String thumbnail;
    String movieName;
    String synopsis;
    String rating;
    String dateOfRelease;
    String test;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //ButterKnife.bind(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        initCollapsingToolbar();
        //Initalizes views

        //@BindView(R.id.thumbnail_image_header) TextView imageView;
        imageView = (ImageView) findViewById(R.id.thumbnail_image_header);
        nameOfMovie = (TextView) findViewById(R.id.title);
        plotSynopsis = (TextView) findViewById(R.id.plotsynopsis);
        userRating = (TextView) findViewById(R.id.userating);
        releaseDate = (TextView) findViewById(R.id.releasedate);

        //Testing if the intent for a particular activity has data
        Intent intentForActivity = getIntent(); //Getting intent from DetailsActivity
        if (intentForActivity.hasExtra("movies")){
            movie = intentForActivity.getParcelableExtra("movies");
            //gets Intents
            thumbnail = movie.getPosterPath();
            movieName = movie.getOriginalTitle();
            synopsis = movie.getOverview();
            rating = Double.toString(movie.getVoteAverage());
            dateOfRelease = movie.getReleaseDate();
            String poster = "https://image.tmdb.org/t/p/w500" + thumbnail;
            Glide.with(this)
                    .load(poster)
                    .placeholder(R.drawable.load)
                    .into(imageView);

            nameOfMovie.setText(movieName);
            plotSynopsis.setText(synopsis);
            userRating.setText(rating);
            releaseDate.setText(dateOfRelease);
        }else{
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.NoDataOnAPI), Snackbar.LENGTH_LONG);
            Snackbar okay = snackbar.setAction("Okay", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailActivity.this,"Action Clicked",Toast.LENGTH_LONG);
                }
            });
            snackbar.show();
        }
        initViews();
    }
//Toolbar shows and hides toolbar tittle on scroll
    private void initCollapsingToolbar(){
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1){
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if(scrollRange + verticalOffset == 0){
                    collapsingToolbarLayout.setTitle(getString(R.string.movie_details));
                    isShow = true;
                }else if (isShow){
                    collapsingToolbarLayout.setTitle("");
                    isShow = false;
                }
            }
        });
    }

    private void initViews() {
        trailerList = new ArrayList<>();
        adapter = new TrailerAdapter(this, trailerList);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view1);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        loadJSON();

    }
    private void loadJSON(){
        //Get the movie ID
        try{
            if (BuildConfig.THE_MOVIE_DB_API_TOKEN.isEmpty()){
                Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.ObtainAPI), Snackbar.LENGTH_LONG);
                Snackbar okay = snackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Toast.makeText(DetailActivity.this,"Action Clicked",Toast.LENGTH_LONG);
                    }
                });
                snackbar.show();
                return;
            }
            Client Client = new Client();
            Service apiService = Client.getClient().create(Service.class);  //Call client
            Call<TrailerResponse> call = apiService.getMovieTrailer(movie_id, BuildConfig.THE_MOVIE_DB_API_TOKEN);
            call.enqueue(new Callback<TrailerResponse>() {
                @Override
                public void onResponse(Call<TrailerResponse> call, Response<TrailerResponse> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                    recyclerView.smoothScrollToPosition(0);
                }
                @Override
                public void onFailure(Call<TrailerResponse> call, Throwable t) {
                    Log.d("Error", t.getMessage());
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.ErrFetchingData), Snackbar.LENGTH_LONG);
                    Snackbar okay = snackbar.setAction("Ok", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(DetailActivity.this,"Action Clicked",Toast.LENGTH_LONG);
                        }
                    });
                    snackbar.show();
                }
            });
        }catch (Exception e){
            Log.d("Error", e.getMessage());
            Snackbar snackbar = Snackbar.make(findViewById(R.id.main_content), getString(R.string.catchInTrailer), Snackbar.LENGTH_LONG);
            Snackbar okay = snackbar.setAction("Ok", new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(DetailActivity.this,"Action Clicked",Toast.LENGTH_LONG);
                }
            });
            snackbar.show();
        }
    }

    public void saveFavorite() {
        favoriteDbHelper = new FavoriteDbHelper(activity);
        favorite = new Movie();

        Double rate = movie.getVoteAverage();

        favorite.setId(movie_id);
        favorite.setOriginalTitle(movieName);
        favorite.setPosterPath(thumbnail);
        favorite.setVoteAverage(rate);
        favorite.setOverview(synopsis);
        favoriteDbHelper.addFavorite(favorite);
    }
}