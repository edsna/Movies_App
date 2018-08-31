package com.blogspot.progectoscaseiros.movies_app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.progectoscaseiros.movies_app.model.Movie;
import com.bumptech.glide.Glide;

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
            //movie = getIntent().getParcelableExtra("movies");
            //movie = getIntent().getParcelableExtra("movies");
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
}