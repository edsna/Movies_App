package com.blogspot.progectoscaseiros.movies_app.api;

import com.blogspot.progectoscaseiros.movies_app.model.MoviesResponse;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface Service {

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .build();
    Service apiService = retrofit.create(Service.class);

    @GET("movie/popular")   //Opens the popular movies page in moviesdb
    Call<MoviesResponse>getPopularMovies(@Query("api_key")String apiKey);

    @GET("movie/top_rated")   //Retrieves top rated movies page in moviesdb using
    Call<MoviesResponse>getTopRatedMovies(@Query("api_key")String apiKey);

    @GET("movie/{movie_id}/videos")
    Call<TrailerResponse> getMovieTrailer(@Path("movie_id") int id, @Query("api_key") String apiKey);
}
