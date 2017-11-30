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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.weather.data.WeatherContract.WeatherEntry;

/**
 * Quản lí local database cho dữ liệu thời tiết.
 */
public class WeatherDbHelper extends SQLiteOpenHelper {

    /*
     * Tên database, ngắn gọn và kết thúc bằng .db
     */
    public static final String DATABASE_NAME = "weather.db";

    //  Tăng phiên bản database sau khi thay đổi hành vi của bảng
    /*
     * Nếu bạn thay đổi lược đồ db, bạn phải tăng phiên bản db nếu không phương thức onUpgrade
	 * sẽ không được gọi.
     *
     * DATABASE_VERSION bắt đầu từ 3 vì Weather đã được sử dụng kết hợp với khóa học Android trong một thời gian.
	 * Các phiên bản cũ của Weather vẫn tồn tại. Nếu DATABASE_VERSION bắt đầu từ 1, việc nâng cấp các phiên
	 * bản cũ của Weather có thể khiến mọi thứ bị phá vỡ. Mặc dù chuyện đó là rất hiếm, nhưng chúng tôi muốn
	 * theo dõi và cảnh báo bạn về những gì có thể xảy ra nếu nhầm lẫn phiên bản db.
     */
    private static final int DATABASE_VERSION = 3;

    public WeatherDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Được gọi khi db được tạo lần đầu tiên.
     *
     * @param sqLiteDatabase database.
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * String này chứa một câu lệnh SQL đơn giản để tạo ra một bảng sẽ cache dữ liệu thời tiết.
         */
        final String SQL_CREATE_WEATHER_TABLE =

                "CREATE TABLE " + WeatherEntry.TABLE_NAME + " (" +

                /*
				 * WeatherEntry không khai báo rõ ràng cột "_ID". Tuy nhiên, WeatherEntry thực hiện
				 * giao diện, "BaseColumns", mà không có trường"_ID". Chúng tôi sử dụng ở đây để
				 * chỉ ra khóa chính của bảng.
                 */
                        WeatherEntry._ID               + " INTEGER PRIMARY KEY AUTOINCREMENT, " +

                        WeatherEntry.COLUMN_DATE       + " INTEGER NOT NULL, "                 +

                        WeatherEntry.COLUMN_WEATHER_ID + " INTEGER NOT NULL,"                  +

                        WeatherEntry.COLUMN_MIN_TEMP   + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_MAX_TEMP   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_HUMIDITY   + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_PRESSURE   + " REAL NOT NULL, "                    +

                        WeatherEntry.COLUMN_WIND_SPEED + " REAL NOT NULL, "                    +
                        WeatherEntry.COLUMN_DEGREES    + " REAL NOT NULL, "                    +

                // Thêm một ràng buộc duy nhất (UNIQUE) trong cột date để thay thế khi có xung đột
                /*
                 * Để đảm bảo bảng này chỉ có thể chứa một mục thời tiết mỗi ngày, chúng ta khai báo
				 * cột ngày là duy nhất. Ta cũng chỉ rõ "ON CONFLICT REPLACE". Điều này cho
				 * SQLite biết rằng nếu chúng ta có mục nhập thời tiết cho một ngày nào đó và
				 * cố gắng thêm một mục nhập thời tiết khác có ngày đó, thì ta sẽ thay thế mục nhập thời tiết cũ.
                 */
                        " UNIQUE (" + WeatherEntry.COLUMN_DATE + ") ON CONFLICT REPLACE);";

        /*
         * Sau khi khai báo câu lệnh tạo SQLite ở trên, ta khởi chạy SQL
		 * với phương thức execSQL của đối tượng cơ sở dữ liệu SQLite.
         */
        sqLiteDatabase.execSQL(SQL_CREATE_WEATHER_TABLE);
    }

    /**
     * db này chỉ là bộ nhớ cache cho dữ liệu trực tuyến, do đó, cách nâng cấp của nó chỉ đơn giản là
     * loại bỏ dữ liệu và gọi qua cho onCreate để tạo lại bảng. Lưu ý rằng điều này chỉ thực hiện được
     * nếu bạn thay đổi số phiên bản cho cơ sở dữ liệu (trong bài này là DATABASE_VERSION). Nó không
     * phụ thuộc vào số phiên bản cho ứng dụng của bạn được tìm thấy trong tập tin app / build.gradle.
     * Nếu bạn muốn cập nhật lược đồ mà không cần xóa dữ liệu, hãy comment lại phần thân code của
     * phương thức này trước khi sửa đổi.
     *
     * @param sqLiteDatabase Database được nâng cấp
     * @param oldVersion     Phiên bản database cũ
     * @param newVersion     Phiên bản database mới
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + WeatherEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}