/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.weather;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class DetailActivity extends AppCompatActivity {
//      TODO (21) Implement LoaderManager.LoaderCallbacks<Cursor>

    /* Trong Activity này, bạn có thể chia sẻ dự báo ngày đã chọn. Ta sẽ dùng Hashtag. */
    private static final String FORECAST_SHARE_HASHTAG = " #WeatherApp";

//  TODO (18) Tạo một mảng String có chứa tên của các cột dữ liệu bạn muốn từ ContentProvider
//  TODO (19) Tạo các hằng int biểu diễn vị trí của mỗi cột ở trên
//  TODO (20) Tạo một hằng int để xác định loader được sử dụng trong DetailActivity

    /* Bản tóm tắt dự báo có thể được chia sẻ bằng cách nhấp vào nút chia sẻ trong ActionBar */
    private String mForecastSummary;

//  TODO (15) Khai báo trường private Uri tên mUri

    //  TODO (10) Xóa khai báo mWeatherDisplay TextView
    private TextView mWeatherDisplay;

//  TODO (11) Khai báo TextViews cho ngày, mô tả, nhiệt độ cao thấp, độ ẩm, gió và áp suất

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//      TODO (12) Xóa mWeatherDisplay TextView
        mWeatherDisplay = (TextView) findViewById(R.id.tv_display_weather);
//      TODO (13) Tìm các TextView bằng ID

//      TODO (14) Xóa code kiểm tra extra text
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mForecastSummary = intentThatStartedThisActivity.getStringExtra(Intent.EXTRA_TEXT);
                mWeatherDisplay.setText(mForecastSummary);
            }
        }
//      TODO (16) Sử dụng getData để lấy tham chiếu đến URI được chuyền vào với intent của Activity
//      TODO (17) Ném ngoại lệ nếu URI is null
//      TODO (35) Khởi tạo loader cho DetailActivity
    }

    /**
     * Sử dụng Intent builder của ShareCompat để tạo ra Intent dự báo thời tiết để chia sẻ.
     * Chúng ta thiết lập loại nội dung đang chia sẻ (chỉ là text), bản thân text đó, và
     * trả về Intent mới tạo ra.
     *
     * @return The Intent to use to start our share.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Sử dụng phương thức getMenuInflater của AppCompatActivity để có được một trình xử lý trên trình đơn inflater */
        MenuInflater inflater = getMenuInflater();
        /* Sử dụng phương thức inflate của Inflater để inflate menu layout vào menu này */
        inflater.inflate(R.menu.detail, menu);
        /* Trả về true để menu được hiển thị trong Toolbar */
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        /* Lấy ID của item được bấm */
        int id = item.getItemId();

        /* Item Setting menu được bấm */
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }

        /* Share menu item được bấm */
        if (id == R.id.action_share) {
            Intent shareIntent = createShareForecastIntent();
            startActivity(shareIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private Intent createShareForecastIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mForecastSummary + FORECAST_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

//  TODO (22) Override onCreateLoader
//          TODO (23) Nếu loader được yêu cầu là detail loader, trả về CursorLoader thích hợp

//  TODO (24) Override onLoadFinished
//      TODO (25) Kiểm tra Cursor có dữ liệu hợp lệ không trước khi làm việc khác
//      TODO (26) Hiển thị data string
//      TODO (27) Hiển thị mô tả (dùng WeatherUtils)
//      TODO (28) Hiển thị nhiệt độ cao
//      TODO (29) Hiển thị nhiệt độ thấp
//      TODO (30) Hiển thị độ ẩm
//      TODO (31) Hiển thị sức gió và hướng gió
//      TODO (32) Hiển thị áp suất
//      TODO (33) Lưu trữ bản tóm tắt dự báo trong mForecastSummary


//  TODO (34) Override onLoaderReset, nhưng chưa làm gì ở đây

}