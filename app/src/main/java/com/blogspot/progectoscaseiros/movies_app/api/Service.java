package com.blogspot.progectoscaseiros.movies_app.api;

import com.blogspot.progectoscaseiros.movies_app.model.MoviesResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Service {

    @GET("movie/popular")   //Opens the popular movies page in moviesdb
    Call<MoviesResponse>getPopularMovies(@Query("69f261e97f9ad49160b5fd0d7845a861")String apiKey);

    @GET("movie/top_rated")   //Retrieves top rated movies page in moviesdb using
    Call<MoviesResponse>getTopRatedMovies(@Query("69f261e97f9ad49160b5fd0d7845a861")String apiKey);
}
