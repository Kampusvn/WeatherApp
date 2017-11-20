/*
 * Copyright (C) 2016 The Android Open Source Project
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

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private TextView mWeatherTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forecast);

        /*
         * Sử dụng findViewById, ta nhận được một tham chiếu đến TextView từ xml. Điều này
         * cho phép ta làm những việc như đặt text cho TextView.
         */
        mWeatherTextView = (TextView) findViewById(R.id.tv_weather_data);

        // TODO (4) Xóa dữ liệu giả để dùng dữ liệu thật.
        /*
         * Chuỗi String chứa dữ liệu thời tiết giả. Trong khóa học, chúng ta sẽ
	 	 * có được dữ liệu thời tiết thực. Bây giờ, chúng tôi muốn có được một cái gì
	 	 * đó trên màn hình càng nhanh càng tốt, vì vậy chúng tôi sẽ hiển thị dữ liệu giả này.
         */
        String[] dummyWeatherData = {
                "Today, May 17 - Clear - 17°C / 15°C",
                "Tomorrow - Cloudy - 19°C / 15°C",
                "Thursday - Rainy- 30°C / 11°C",
                "Friday - Thunderstorms - 21°C / 9°C",
                "Saturday - Thunderstorms - 16°C / 7°C",
                "Sunday - Rainy - 16°C / 8°C",
                "Monday - Partly Cloudy - 15°C / 10°C",
                "Tue, May 24 - Meatballs - 16°C / 18°C",
                "Wed, May 25 - Cloudy - 19°C / 15°C",
                "Thu, May 26 - Stormy - 30°C / 11°C",
                "Fri, May 27 - Hurricane - 21°C / 9°C",
                "Sat, May 28 - Meteors - 16°C / 7°C",
                "Sun, May 29 - Apocalypse - 16°C / 8°C",
                "Mon, May 30 - Post Apocalypse - 15°C / 10°C",
        };

        // TODO (3) Xóa vòng lặp dùng dữ liệu giả
        /*
         * Lặp đi lặp lại qua mảng và thêm các chuỗi vào TextView. Lý do
         * chúng ta thêm "\ n \ n \ n" sau String là để tách biệt
         * các String trong TextView. Sau đó, chúng ta sẽ tìm hiểu
         * về cách tốt hơn để hiển thị danh sách dữ liệu.
         */
        for (String dummyWeatherDay : dummyWeatherData) {
            mWeatherTextView.append(dummyWeatherDay + "\n\n\n");
        }

        // TODO (9) Gọi loadWeatherData để thực thi network request lấy dữ liệu về thời tiết
    }

    // TODO (8) Viết phương thức loadWeatherData lấy vị trí người dùng mong muốn và thực thi AsyncTask

    // TODO (5) Tạo một class extends AsyncTask để thực hiện network requests
    // TODO (6) Override phương thức doInBackground để thực hiện network requests
    // TODO (7) Override phương thức onPostExecute để hiển thị kết quả
}