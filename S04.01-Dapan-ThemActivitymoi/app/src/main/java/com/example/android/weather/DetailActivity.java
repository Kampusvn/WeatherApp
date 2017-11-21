package com.example.android.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

// Hoàn thành (1) Tạo Activity tên là DetailActivity
public class DetailActivity extends AppCompatActivity {

    private static final String FORECAST_SHARE_HASHTAG = " #WeatherApp";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
    }
}