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
package com.example.android.weather.data;

import android.net.Uri;
import android.provider.BaseColumns;

import com.example.android.weather.utilities.WeatherDateUtils;

/**
 * Xác định tên bảng và cột cho cơ sở dữ liệu thời tiết. Lớp này không cần thiết, nhưng sẽ làm cho code có tổ chức.
 */
public class WeatherContract {

    /*
	 * "Content authority" là tên của content provider, tương tự như mối quan hệ giữa tên miền
	 * và trang web. Chuỗi sử dụng cho content provider là tên gói của ứng dụng, được bảo đảm
	 * là duy nhất trên Play Store.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.weather";

    /*
	 * Sử dụng CONTENT_AUTHORITY để tạo ra cơ sở của tất cả các URI mà ứng dụng sẽ sử dụng
	 * để liên hệ với content provider trong Weather.
     */
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /*
     * Các đường dẫn có thể được nối vào BASE_CONTENT_URI để tạo URI hợp lệ
	 * mà Weather có thể xử lý. Ví dụ,
     *
     *     content://com.example.android.weather/weather/
     *     [           BASE_CONTENT_URI         ][ PATH_WEATHER ]
     *
     * là đường dẫn hợp lệ để lấy dữ liệu thời tiết, còn
     *
     *      content://com.example.android.weather/givemeroot/
     *
     * sẽ không hoạt động vì ContentProvider chưa được cung cấp bất kỳ thông tin nào về việc
	 * phải làm gì với "givemeroot".
     */
    public static final String PATH_WEATHER = "weather";

    /* Lớp bên trong xác định nội dung bảng của bảng thời tiết */
    public static final class WeatherEntry implements BaseColumns {

        /* Cơ sở CONTENT_URI đã sử dụng để truy vấn bảng Weather từ content provider */
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(PATH_WEATHER)
                .build();

        /* Tên của bảng thời tiết */
        public static final String TABLE_NAME = "weather";

        /*
         * Cột ngày tháng sẽ lưu ngày giờ UTC tương ứng với ngày địa phương mà mỗi hàng thời tiết cụ thể đại diện.
		 * Ví dụ: nếu bạn sống ở Giờ chuẩn Đông (EST) và tải dữ liệu thời tiết vào lúc 9:00 tối ngày 23 tháng 9 năm 2016,
		 * timestamp UTC cho thời gian cụ thể đó sẽ là 1474678800000 mili giây. Tuy nhiên, do tỷ lệ múi giờ,
		 * nó sẽ là ngày 24 tháng 9 năm 2016 theo múi giờ GMT, khi đó là lúc 23:00 theo múi giờ EST. Trong ví dụ này,
		 * cột ngày sẽ giữ ngày đại diện cho ngày 23 tháng 9 vào lúc nửa đêm theo giờ GMT. (1474588800000)
         *
         * Lý do chúng tôi lưu trữ giờ GMT chứ không phải giờ địa phương là để có "normalized", hoặc chuẩn khi lưu
		 * trữ ngày và điều chỉnh khi cần thiết khi hiển thị ngày. Việc chuẩn hoá ngày cũng cho phép ta dễ dàng
		 * chuyển đổi sang giờ địa phương vào lúc nửa đêm vì ta chỉ cần thêm một khoảng thời gian GMT của
		 * múi giờ cụ thể cho ngày này để có được giờ địa phương vào lúc nửa đêm vào ngày thích hợp.
         */
        public static final String COLUMN_DATE = "date";

        /* ID thời tiết được trả về bởi API, được sử dụng để xác định icon sẽ được sử dụng */
        public static final String COLUMN_WEATHER_ID = "weather_id";

        /* Nhiệt độ cao nhất và thấp nhất trong ngày, tính bằng °C (lưu trữ bằng số float trong cơ sở dữ liệu) */
        public static final String COLUMN_MIN_TEMP = "min";
        public static final String COLUMN_MAX_TEMP = "max";

        /* Độ ẩm được lưu trữ dưới dạng float đại diện cho phần trăm */
        public static final String COLUMN_HUMIDITY = "humidity";

        /* Áp suất được lưu giữ dưới dạng float đại diện cho phần trăm */
        public static final String COLUMN_PRESSURE = "pressure";

        /* Tốc độ gió được lưu trữ dưới dạng float thể hiện tốc độ gió theo mph */
        public static final String COLUMN_WIND_SPEED = "wind";

        /*
         * Cấp độ khí tượng (e.g, 0 is north, 180 is south).
         * Lưu trữ trong database dưới dạng float.
         */
        public static final String COLUMN_DEGREES = "degrees";

        /**
		 * Tạo một URI để thêm ngày vào cuối đường dẫn URI, để truy vấn chi tiết về thời tiết
		 * theo ngày. Ta sử dụng nó cho truy vấn xem chi tiết. Ta giả sử một ngày bình thường
		 * được chuyền vào phương thức này.
         *
         * @param date ngày tháng chuẩn hóa theo đơn vị milliseconds
         * @return Uri để truy vấn
         */
        public static Uri buildWeatherUriWithDate(long date) {
            return CONTENT_URI.buildUpon()
                    .appendPath(Long.toString(date))
                    .build();
        }

        /**
		 * Trả về một phần được chọn của truy vấn thời tiết với ngày được chuẩn hóa. Việc này là
		 * để lấy dự báo thời tiết cho ngày hôm nay. Để dễ dàng hơn, ta đặt ngày hôm nay
		 * là một đối số trong truy vấn.
         *
         * @return Phần lựa chọn của truy vấn thời tiết cho ngày hôm nay trở đi
         */
        public static String getSqlSelectForTodayOnwards() {
            long normalizedUtcNow = WeatherDateUtils.normalizeDate(System.currentTimeMillis());
            return WeatherContract.WeatherEntry.COLUMN_DATE + " >= " + normalizedUtcNow;
        }
    }
}