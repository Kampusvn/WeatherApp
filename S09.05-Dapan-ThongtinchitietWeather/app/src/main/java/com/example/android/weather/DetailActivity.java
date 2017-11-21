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
import android.widget.TextView;

import com.example.android.weather.data.WeatherContract;
import com.example.android.weather.utilities.WeatherDateUtils;
import com.example.android.weather.utilities.WeatherUtils;

public class DetailActivity extends AppCompatActivity implements
//      Hoàn thành (21) Implement LoaderManager.LoaderCallbacks<Cursor>
        LoaderManager.LoaderCallbacks<Cursor> {

    /* Trong Activity này, bạn có thể chia sẻ dự báo ngày đã chọn. Ta sẽ dùng Hashtag. */
    private static final String FORECAST_SHARE_HASHTAG = " #WeatherApp";

    //  Hoàn thành (18) Tạo một mảng String có chứa tên của các cột dữ liệu bạn muốn từ ContentProvider
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

    //  Hoàn thành (19) Tạo các hằng int biểu diễn vị trí của mỗi cột ở trên
    public static final int INDEX_WEATHER_DATE = 0;
    public static final int INDEX_WEATHER_MAX_TEMP = 1;
    public static final int INDEX_WEATHER_MIN_TEMP = 2;
    public static final int INDEX_WEATHER_HUMIDITY = 3;
    public static final int INDEX_WEATHER_PRESSURE = 4;
    public static final int INDEX_WEATHER_WIND_SPEED = 5;
    public static final int INDEX_WEATHER_DEGREES = 6;
    public static final int INDEX_WEATHER_CONDITION_ID = 7;

    //  Hoàn thành (20) Tạo một hằng int để xác định loader được sử dụng trong DetailActivity
    private static final int ID_DETAIL_LOADER = 353;

    /* Bản tóm tắt dự báo có thể được chia sẻ bằng cách nhấp vào nút chia sẻ trong ActionBar */
    private String mForecastSummary;

    //  Hoàn thành (15) Khai báo trường private Uri tên mUri
    private Uri mUri;

    //  Hoàn thành (10) Xóa khai báo mWeatherDisplay TextView

    //  Hoàn thành (11) Khai báo TextViews cho ngày, mô tả, nhiệt độ cao thấp, độ ẩm, gió và áp suất
    private TextView mDateView;
    private TextView mDescriptionView;
    private TextView mHighTemperatureView;
    private TextView mLowTemperatureView;
    private TextView mHumidityView;
    private TextView mWindView;
    private TextView mPressureView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
//      Hoàn thành (12) Xóa mWeatherDisplay TextView
//      Hoàn thành (13) Tìm các TextView bằng ID
        mDateView = (TextView) findViewById(R.id.date);
        mDescriptionView = (TextView) findViewById(R.id.weather_description);
        mHighTemperatureView = (TextView) findViewById(R.id.high_temperature);
        mLowTemperatureView = (TextView) findViewById(R.id.low_temperature);
        mHumidityView = (TextView) findViewById(R.id.humidity);
        mWindView = (TextView) findViewById(R.id.wind);
        mPressureView = (TextView) findViewById(R.id.pressure);

//      Hoàn thành (14) Xóa code kiểm tra extra text

//      Hoàn thành (16) Sử dụng getData để lấy tham chiếu đến URI được chuyền vào với intent của Activity
        mUri = getIntent().getData();
//      Hoàn thành (17) Ném ngoại lệ nếu URI is null
        if (mUri == null) throw new NullPointerException("URI for DetailActivity cannot be null");

//      Hoàn thành (35) Khởi tạo loader cho DetailActivity
        /* This connects our Activity into the loader lifecycle. */
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

//  TODO (22) Override onCreateLoader
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

//          Hoàn thành (23) Nếu loader được yêu cầu là detail loader, trả về CursorLoader thích hợp
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

//  Hoàn thành (24) Override onLoadFinished
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

//      Hoàn thành (25) Kiểm tra Cursor có dữ liệu hợp lệ không trước khi làm việc khác
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

//      Hoàn thành (26) Hiển thị data string
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

        mDateView.setText(dateText);

//      Hoàn thành (27) Hiển thị mô tả (dùng WeatherUtils)
        /***********************
         * Weather Description *
         ***********************/
        /* Đọc ID điều kiện thời tiết từ con trỏ (ID được cung cấp bởi Open Weather Map) */
        int weatherId = data.getInt(INDEX_WEATHER_CONDITION_ID);
        /* Sử dụng weatherId để có được mô tả thích hợp */
        String description = WeatherUtils.getStringForWeatherCondition(this, weatherId);

        /* Đặt text */
        mDescriptionView.setText(description);

//      Hoàn thành (28) Hiển thị nhiệt độ cao
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

        /* Đặt text */
        mHighTemperatureView.setText(highString);

//      Hoàn thành (29) Hiển thị nhiệt độ thấp
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

        /* Đặt text */
        mLowTemperatureView.setText(lowString);

//      Hoàn thành (30) Hiển thị độ ẩm
        /************
         *  Độ ẩm   *
         ************/
        /* Đọc độ ẩm từ con trỏ */
        float humidity = data.getFloat(INDEX_WEATHER_HUMIDITY);
        String humidityString = getString(R.string.format_humidity, humidity);

        /* Đặt text */
        mHumidityView.setText(humidityString);

//      Hoàn thành (31) Hiển thị sức gió và hướng gió
        /****************************
         *   Sức gió và hướng gió   *
         ****************************/
        /* Đọc tốc độ gió (theo MPH) và hướng (theo la bàn) từ con trỏ  */
        float windSpeed = data.getFloat(INDEX_WEATHER_WIND_SPEED);
        float windDirection = data.getFloat(INDEX_WEATHER_DEGREES);
        String windString = WeatherUtils.getFormattedWind(this, windSpeed, windDirection);

        /* Độ ẩm */
        mWindView.setText(windString);

//      Hoàn thành (32) Hiển thị áp suất
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

        /* Đặt text */
        mPressureView.setText(pressureString);

//      Hoàn thành (33) Lưu trữ bản tóm tắt dự báo trong mForecastSummary
        mForecastSummary = String.format("%s - %s - %s/%s",
                dateText, description, highString, lowString);
    }

//  Hoàn thành (34) Override onLoaderReset, nhưng chưa làm gì ở đây
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