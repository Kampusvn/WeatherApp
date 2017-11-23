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

import android.provider.BaseColumns;

/**
 * Xác định tên bảng và cột cho cơ sở dữ liệu thời tiết. Lớp này không cần thiết, nhưng sẽ làm cho code có tổ chức.
 */
public class WeatherContract {

    // Hoàn thành (1) Trong WeatherContract, tạo một lớp public static final tên là WeatherEntry và implements BaseColumns
    /* Lớp bên trong xác định nội dung bảng của bảng thời tiết */
    public static final class WeatherEntry implements BaseColumns {

        // Thực hiện bươc 2 - 10 trong lớp WeatherEntry

        // Hoàn thành (2) Khai báo public static final String TABLE_NAME và gán giá trị "weather"

        /* Tên của bảng thời tiết */
        public static final String TABLE_NAME = "weather";

        // Hoàn thành (3) Khai báo public static final String call COLUMN_DATE và gán giá trị "date"
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

        // Hoàn thành (4) Khai báo public static final String call COLUMN_WEATHER_ID và gán giá trị "weather_id"
        /* ID thời tiết được trả về bởi API, được sử dụng để xác định icon sẽ được sử dụng */
        public static final String COLUMN_WEATHER_ID = "weather_id";

        // Hoàn thành (5) Khai báo public static final String call COLUMN_MIN_TEMP và gán giá trị "min"
        /* Nhiệt độ cao nhất và thấp nhất trong ngày, tính bằng °C (lưu trữ bằng số float trong cơ sở dữ liệu) */
        public static final String COLUMN_MIN_TEMP = "min";
        // Hoàn thành (6) Khai báo public static final String call COLUMN_MAX_TEMP và gán giá trị "max"
        public static final String COLUMN_MAX_TEMP = "max";

        // Hoàn thành (7) Khai báo public static final String call COLUMN_HUMIDITY và gán giá trị "humidity"
        /* Độ ẩm được lưu trữ dưới dạng float đại diện cho phần trăm */
        public static final String COLUMN_HUMIDITY = "humidity";

        // Hoàn thành (8) Khai báo public static final String call COLUMN_PRESSURE và gán giá trị "pressure"
        /* Áp suất được lưu giữ dưới dạng float đại diện cho phần trăm */
        public static final String COLUMN_PRESSURE = "pressure";

        // Hoàn thành (9) Khai báo public static final String call COLUMN_WIND_SPEED và gán giá trị "wind"
        /* Tốc độ gió được lưu trữ dưới dạng float thể hiện tốc độ gió theo mph */
        public static final String COLUMN_WIND_SPEED = "wind";

        // Hoàn thành (10) Khai báo public static final String call COLUMN_DEGREES và gán giá trị "degrees"
        public static final String COLUMN_DEGREES = "degrees";
    }
}