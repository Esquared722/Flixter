package com.example.flixter.models;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Headers;

@Parcel
public class Movie {

    int movieId;
    String posterPath;
    String backdropPath;
    String title;
    String overview;
    String releaseDate;
    String[] genres;
    double rating;

    private static Map<Integer, String> genreMap;
    private static JSONArray genresJSONArray;
    private static int num_genres;

    private static final String GENRE_MAP = "https://api.themoviedb.org/3/genre/movie/list?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed&language=en-US";

    // empty constructor needed by Parcel library
    public Movie(){}

    public Movie(JSONObject jsonObject) throws JSONException {
        backdropPath = jsonObject.getString("backdrop_path");
        posterPath = jsonObject.getString("poster_path");
        title = jsonObject.getString("title");
        overview = jsonObject.getString("overview");
        rating = jsonObject.getDouble("vote_average");
        movieId = jsonObject.getInt("id");
        releaseDate = jsonObject.getString("release_date");
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(GENRE_MAP, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JSON json) {
                try {
                    genresJSONArray = json.jsonObject.getJSONArray("genres");
                    num_genres = genresJSONArray.length();
                    genreMap = getGenreMap();
                    JSONArray genreCodeArray = jsonObject.getJSONArray("genre_ids");
                    int numGenres = genreCodeArray.length();
                    genres = new String[numGenres];
                    for(int i = 0; i < numGenres; i++) {
                        genres[i] = genreMap.get(genreCodeArray.getInt(i));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {

            }
        });
    }

    private static Map<Integer, String> getGenreMap() throws JSONException {
        Map<Integer, String> genreMap = new HashMap<>();
        for(int i = 0; i < num_genres; i++) {
            JSONObject genre = genresJSONArray.getJSONObject(i);
            genreMap.put(genre.getInt("id"), genre.getString("name"));
        }
        return genreMap;
    }

    public static List<Movie> fromJsonArray(JSONArray movieJsonArray) throws JSONException {
        List<Movie> movies = new ArrayList<>();
        for (int i = 0; i < movieJsonArray.length(); i++) {
            movies.add(new Movie(movieJsonArray.getJSONObject(i)));
        }
        return movies;
    }

    public String getBackdropPath() {
        return String.format("https://image.tmdb.org/t/p/w780%s", backdropPath);
    }

    public String getPosterPath() {
        return String.format("https://image.tmdb.org/t/p/w342%s", posterPath);
    }

    public String getTitle() {
        return title;
    }

    public String getOverview() {
        return overview;
    }

    public double getRating() {
        return rating;
    }

    public int getMovieId() {
        return movieId;
    }

    public String getReleaseDate() {
        return releaseDate.split("-")[0];
    }

    public String getGenres() {
        StringBuilder genreString = new StringBuilder();
        for (int i = 0; i < genres.length; i++) {
            if (i == (genres.length - 1)) {
                genreString.append(genres[i]);
                break;
            }
            genreString.append(genres[i]).append(" | ");
        }
        return genreString.toString();
    }
}
