package com.example.flixter;

import android.os.Bundle;

import com.example.flixter.adapters.MovieAdapter;
import com.example.flixter.databinding.ActivityMainBinding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {
    ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        RecyclerView rvMovies = binding.rvMovies;

        MovieAdapter movieAdapter = new MovieAdapter(this);

        rvMovies.setAdapter(movieAdapter);

        rvMovies.setLayoutManager(new LinearLayoutManager(this));

        LinearLayoutManager layoutManager = (LinearLayoutManager) rvMovies.getLayoutManager();

        assert layoutManager != null;
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(rvMovies.getContext(),
                layoutManager.getOrientation());

        rvMovies.addItemDecoration(dividerItemDecoration);
    }
}