package com.example.flixter.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.example.flixter.DetailActivity;
import com.example.flixter.R;
import com.example.flixter.databinding.ItemMovieBinding;
import com.example.flixter.models.Movie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import okhttp3.Headers;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    public static final String NOW_PLAYING_URL = "https://api.themoviedb.org/3/movie/now_playing?api_key=a07e22bc18f5cb106bfe4cc1f83ad8ed";
    public static final String TAG = "MovieAdapter";
    Context context;
    List<Movie> movies;

    public MovieAdapter(Context context) {
        this.context = context;
        getMovies();
    }

    // fetches movies using AsyncHttpClient library
    private void getMovies() {
        movies = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(NOW_PLAYING_URL, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Headers headers, JsonHttpResponseHandler.JSON json) {
                Log.d(TAG, "onSuccess");
                JSONObject jsonObject = json.jsonObject;
                try {
                    JSONArray results = jsonObject.getJSONArray("results");
                    Log.i(TAG, "Results " + results.toString());
                    movies.addAll(Movie.fromJsonArray(results));
                    notifyDataSetChanged();
                    Log.i(TAG, "Movies " + movies.size());
                } catch (JSONException e) {
                    Log.e(TAG, "Hit json exception", e);
                }
            }

            @Override
            public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                Log.d(TAG, "onFailure: " + statusCode + ", " + response);
            }
        });
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d("MovieAdapter", "onCreateViewHolder");
        View movieView = LayoutInflater.from(context).inflate(R.layout.item_movie, parent, false);
        return new ViewHolder(movieView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.d("MovieAdapter", "onBindViewHolder: " + position);
        Movie movie = movies.get(position);
        holder.bind(movie);
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final ItemMovieBinding binding;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = ItemMovieBinding.bind(itemView);
        }

        public void bind(Movie movie) {
            // if phone is in landscape
            // then imageUrl = back drop image
            // else imageUrl = poster image
            String imageUrl;
            binding.setMovie(movie);
            imageUrl = (context
                    .getResources()
                    .getConfiguration()
                    .orientation == Configuration.ORIENTATION_LANDSCAPE) ?
                    movie.getBackdropPath()
                    : movie.getPosterPath();
            final int RADIUS = 30;
            final int MARGIN = 10;
            Glide
                    .with(context)
                    .load(imageUrl)
                    .fitCenter()
                    .transform(new RoundedCornersTransformation(RADIUS, MARGIN))
                    //.skipMemoryCache(true)
                    //.diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.ic_baseline_image_24)
                    .error(R.drawable.ic_baseline_image_not_supported_24)
                    .dontAnimate()
                    .into(binding.ivPoster);

            binding.container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, DetailActivity.class);
                    i.putExtra("movie", Parcels.wrap(movie));
                    Pair<View, String> p1 = Pair.create(binding.tvTitle, "title");
                    Pair<View, String> p2 = Pair.create(binding.tvOverview, "overview");
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation((Activity) context, p1, p2);
                    context.startActivity(i, options.toBundle());
                }
            });
            binding.executePendingBindings();
        }
    }
}
