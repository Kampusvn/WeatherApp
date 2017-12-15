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
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.android.weather.data.WeatherContract;
import com.example.android.weather.databinding.ActivityDetailBinding;
import com.example.android.weather.utilities.WeatherDateUtils;
import com.example.android.weather.utilities.WeatherUtils;

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /* Trong Activity này, bạn có thể chia sẻ dự báo ngày đã chọn. Ta sẽ dùng Hashtag. */
    private static final String FORECAST_SHARE_HASHTAG = " #WeatherApp";

    // Mảng String có chứa tên của các cột dữ liệu bạn muốn từ ContentProvider
    public static final String[] WEATHER_DETAIL_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_DATE,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
            WeatherContract.WeatherEntry.COLUMN_HUMIDITY,
            WeatherContract.WeatherEntry.COLUMN_PRESSURE,
            WeatherContract.WeatherEntry.COLUMN_WIND_SPEED,
            WeatherContract.WeatherEntry.COLUMN_DEGREES,
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID
    };

    // Tạo các hằng int biểu diễn vị trí của mỗi cột ở trên
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;

    // Tạo một hằng int để xác định loader được sử dụng trong DetailActivity
    private static final int ID_DETAIL_LOADER = 353;

    /* Bản tóm tắt dự báo có thể được chia sẻ bằng cách nhấp vào nút chia sẻ trong ActionBar */
    private String mForecastSummary;

    // Khai báo trường private Uri tên mUri
    private Uri mUri;


    //  Khai báo trường ActivityDetailBinding tên là mDetailBinding
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//      Xóa lời gọi setContentView
//      Xóa các lời gọi findViewById

//      Instantiate mDetailBinding bằng DataBindingUtil
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mUri = getIntent().getData();
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

        getSupportLoaderManager().initLoader(ID_DETAIL_LOADER, null, this);
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

    /**
     * Tạo và trả về CursorLoader để load dữ liệu URI và lưu vào con trỏ.
     *
     * @param loaderId Loader ID
     * @param loaderArgs Đối số
     *
     * @return thể hiện của Loader sẵn sàng được load
     */
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {

        switch (loaderId) {

            case ID_DETAIL_LOADER:

                return new CursorLoader(this,
                        mUri,
                        WEATHER_DETAIL_PROJECTION,
                        null,
                        null,
                        null);

            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    /**
     * Chạy trên main thread khi tải hoàn tất. Nếu initLoader được gọi (từ onCreate trong DetailActivity)
     * và LoaderManager đã hoàn thành việc load cho Loader này, onLoadFinished sẽ được gọi ngay lập tức.
     * Trong onLoadFinished, ta ràng buộc dữ liệu vào các view để người dùng có thể xem chi tiết về thời
     * tiết vào ngày họ chọn từ dự báo.
     *
     * @param loader cursor loader hoàn tất.
     * @param data   con trỏ được trả về.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        /*
         * Trước khi ràng buộc dữ liệu vào UI sẽ hiển thị dữ liệu đó, chúng ta cần kiểm tra con trỏ
		 * để chắc chắn rằng ta có kết quả mong đợi. Để làm điều đó, hãy kiểm tra để đảm bảo con trỏ
		 * không null và sau đó gọi moveToFirst trên con trỏ. Mặc dù ban đầu dường như không rõ ràng,
		 * moveToFirst sẽ trả về true nếu nó chứa một hàng dữ liệu đầu tiên hợp lệ.
         *
         * Nếu chúng ta có dữ liệu hợp lệ, ta sẽ tiếp tục ràng buộc dữ liệu đó với UI. Nếu
		 * chúng ta không có bất kỳ dữ liệu nào để ràng buộc, chúng ta chỉ cần quay về phương thức này.
         */
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            /* Có dữ liệu hợp lệ, ràng buộc dữ liệu vào UI */
            cursorHasValidData = true;
        }

        if (!cursorHasValidData) {
            /* Không có dữ liệu để hiển thị, trả về và không làm gì*/
            return;
        }

//      Hiển thị weather icon bằng mDetailBinding
        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        int weatherImageId = WeatherUtils.getLargeArtResourceIdForWeatherCondition(weatherId);

        mDetailBinding.primaryInfo.weatherIcon.setImageResource(weatherImageId);

        /****************
         * Weather Date *
         ****************/
        /*
         * Đọc ngày từ con trỏ. Điều quan trọng cần lưu ý là ngày từ con trỏ là cùng một ngày
		 * từ bảng thời tiết SQL. Ngày được lưu trữ tính theo giờ GMT vào lúc nửa đêm của ngày đó
		 * khi thông tin thời tiết được tải.
         *
         * Khi hiển thị ngày này, bạn phải thêm GMT offset (tính bằng mili giây) để có được ngày
		 * đại diện cho ngày địa phương theo giờ địa phương. getFriendlyDateString của WeatherDateUtils
		 * sẽ thực hiện việc này.
         */
        long localDateMidnightGmt = data.getLong(INDEX_WEATHER_DATE);
        String dateText = WeatherDateUtils.getFriendlyDateString(this, localDateMidnightGmt, true);

//      Dùng mDetailBinding để hiển thị ngày
        mDetailBinding.primaryInfo.date.setText(dateText);

        /***********************
         * Weather Description *
         ***********************/
        /* Đọc ID điều kiện thời tiết từ con trỏ (ID được cung cấp bởi Open Weather Map) */
        String description = WeatherUtils.getStringForWeatherCondition(this, weatherId);

//      Tạo mô tả nội dung cho a11y
        String descriptionA11y = getString(R.string.a11y_forecast, description);

//      Dùng mDetailBinding để hiển thị mô tả và đặt nội dung mô tả
        mDetailBinding.primaryInfo.weatherDescription.setText(description);
        mDetailBinding.primaryInfo.weatherDescription.setContentDescription(descriptionA11y);

//      Đặt mô tả nội dung của icon giống như mô tả thời tiết a11y text
        mDetailBinding.primaryInfo.weatherIcon.setContentDescription(descriptionA11y);

        /**************************
         *   Nhiệt độ cao nhất	  *
         **************************/
        /* Đọc nhiệt độ cao nhất từ con trỏ (độ C) */
        double highInCelsius = data.getDouble(INDEX_WEATHER_MAX_TEMP);
        /*
         * Nếu người dùng dùng độ Fahrenheit, formatTemperature sẽ chuyển đổi nhiệt độ. Phương thức
		 * này cũng sẽ thêm ° C hoặc ° F vào string.
         */
        String highString = WeatherUtils.formatTemperature(this, highInCelsius);

//      Tạo mô tả nội dung cho nhiệt độ cao a11y
        String highA11y = getString(R.string.a11y_high_temp, highString);

//      Use mDetailBinding to display the high temperature and set the content description
        mDetailBinding.primaryInfo.highTemperature.setText(highString);
        mDetailBinding.primaryInfo.highTemperature.setContentDescription(highA11y);

        /*************************
         *   Nhiệt độ thấp nhất  *
         *************************/
        /* Đọc nhiệt độ thấp nhất từ con trỏ (độ C) */
        double lowInCelsius = data.getDouble(INDEX_WEATHER_MIN_TEMP);
        /*
         * Nếu người dùng dùng độ Fahrenheit, formatTemperature sẽ chuyển đổi nhiệt độ. Phương thức
		 * này cũng sẽ thêm ° C hoặc ° F vào string.
         */
        String lowString = WeatherUtils.formatTemperature(this, lowInCelsius);

//      Tạo mô tả nội dung cho nhiệt độ thấp a11y
        String lowA11y = getString(R.string.a11y_low_temp, lowString);

//      Sử dụng mDetailBinding để hiển thị nhiệt độ thấp và đặt mô tả nội dung
        /* Đặt text */
        mDetailBinding.primaryInfo.lowTemperature.setText(lowString);
        mDetailBinding.primaryInfo.lowTemperature.setContentDescription(lowA11y);

        /************
         *  Độ ẩm   *
         ************/
        /* Đọc độ ẩm từ con trỏ */
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);

//      Tạo mô tả nội dung cho độ ẩm a11y
        String humidityA11y = getString(R.string.a11y_humidity, humidityString);

//      Use mDetailBinding to display the humidity and set the content description
        /* Đặt text */
        mDetailBinding.extraDetails.humidity.setText(humidityString);
        mDetailBinding.extraDetails.humidity.setContentDescription(humidityA11y);

        mDetailBinding.extraDetails.humidityLabel.setContentDescription(humidityA11y);

        /****************************
         *   Sức gió và hướng gió   *
         ****************************/
        /* Đọc tốc độ gió (theo MPH) và hướng (theo la bàn) từ con trỏ  */
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = WeatherUtils.getFormattedWind(this, windSpeed, windDirection);

//      Tạo mô tả nội dung cho sức gió cho a11y
        String windA11y = getString(R.string.a11y_wind, windString);

//      Sử dụng mDetailBinding để hiển thị sức gió và đặt mô tả nội dung
        /* Độ ẩm */
        mDetailBinding.extraDetails.windMeasurement.setText(windString);
        mDetailBinding.extraDetails.windMeasurement.setContentDescription(windA11y);

//      Đặt mô tả nội dung của nhãn sức gió cho chuỗi sức gió a11y
        mDetailBinding.extraDetails.windLabel.setContentDescription(windA11y);

        /************
         * Áp suất  *
         ************/
        /* Đọc áp suất từ con trỏ */
        float pressure = data.getFloat(INDEX_WEATHER_PRESSURE);

        /*
         * Định dạng văn bản áp suất sử dụng chuỗi từ resource. Lý do ta trực tiếp truy cập tài nguyên
		 * bằng cách sử dụng getString thay vì sử dụng một phương thức từ WeatherUtils như ta đã cho
		 * các dữ liệu khác được hiển thị trong Activity này là do không có logic bổ sung cần được xem
		 * xét để hiển thị đúng áp lực.
         */
        String pressureString = getString(R.string.format_pressure, pressure);

//      Tạo mô tả nội dung suất cho a11y
        String pressureA11y = getString(R.string.a11y_pressure, pressureString);

//      Sử dụng mDetailBinding để hiển thị áp suất và thiết lập mô tả nội dung
        mDetailBinding.extraDetails.pressure.setText(pressureString);
        mDetailBinding.extraDetails.pressure.setContentDescription(pressureA11y);

//      Đặt mô tả nội dung của nhãn áp suất cho chuỗi áp suất a11y
        mDetailBinding.extraDetails.pressureLabel.setContentDescription(pressureA11y);

//      Lưu trữ bản tóm tắt dự báo trong mForecastSummary
        mForecastSummary = String.format("%s - %s - %s/%s",
                dateText, description, highString, lowString);
    }

    /**
     * Được gọi khi loader đã tạo trước đây đang được thiết lập lại, do đó làm cho dữ liệu của nó
     * không khả dụng. Vì chúng ta không lưu trữ bất kỳ dữ liệu nào của con trỏ này nên không có
     * references nào cần phải xóa.
     *
     * @param loader The Loader được reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}