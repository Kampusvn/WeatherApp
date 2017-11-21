/*
 * Copyright (C) 2015 The Android Open Source Project
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

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Lớp này làm việc như ContentProvider cho tất cả dữ liệu của Weather.
 * Lớp này cho phép ta bulkInsert, truy vấn và xóa dữ liệu.
 *
 * Mặc dù việc triển khai ContentProvider đòi hỏi phải thực hiện các phương thức bổ sung để
 * thực hiện việc thêm, cập nhật và khả năng nhận được loại dữ liệu từ một URI.
 * Tuy nhiên, ở đây chúng không được thực hiện để cho ngắn gọn và đơn giản.
 * Nếu bạn muốn, bạn có thể tự làm việc này.
 */
public class WeatherProvider extends ContentProvider {

//  TODO (5) Khai báo các giá trị static constant integer tên là CODE_WEATHER & CODE_WEATHER_WITH_DATE để xác định các URI mà ContentProvider này có thể xử lý

//  TODO (7) Khởi tạo một UriMatcher tĩnh bằng cách sử dụng phương thức buildUriMatcher

    WeatherDbHelper mOpenHelper;

//  TODO (6) Viết phương thức buildUriMatcher để match URI với ID

//  TODO (1) Implement onCreate
    @Override
    public boolean onCreate() {
//      TODO (2) Trong onCreate, hãy instantiate mOpenHelper

//      TODO (3) Trả về true từ onCreate để biểu thị thiết lập thành công
        return false;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        throw new RuntimeException("Student, you need to implement the bulkInsert mehtod!");
    }

//  TODO (8) Cung cấp implementation cho phương thức truy vấn
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        throw new RuntimeException("Student, implement the query method!");

//      TODO (9) Xử lý các URI truy vấn về thời tiết và thời tiết trong ngày

//      TODO (10) Gọi setNotificationUri trên con trỏ và trả về con trỏ
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new RuntimeException("Student, you need to implement the delete method!");
    }

    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Weather.");
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(
                "We are not implementing insert in Weather. Use bulkInsert instead");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in Weather");
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}