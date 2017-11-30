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
package com.example.android.weather.sync;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;

import com.example.android.weather.data.WeatherContract;

public class WeatherSyncUtils {

    //  Hoàn thành (1) Khai báo trường private static boolean tên là sInitialized
    private static boolean sInitialized;

    //  Hoàn thành (2) Tạo phương thức đồng bộ public static void tên là initialize
    synchronized public static void initialize(@NonNull final Context context) {

        //  Hoàn thành (3) Thực thi phương thức khi sInitialized bằng false
        if (sInitialized) return;

        //  Hoàn thành (4) Nếu phương thức được thực thi, đặt sInitialized thành true
        sInitialized = true;

        //  Hoàn thành (5) Kiểm tra ContentProvider có rỗng hay không
        new AsyncTask<Void, Void, Void>() {
            @Override
            public Void doInBackground( Void... voids ) {

                /* URI cho mỗi hàng dữ liệu trong bảng*/
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;

                String[] projectionColumns = {WeatherContract.WeatherEntry._ID};
                String selectionStatement = WeatherContract.WeatherEntry
                        .getSqlSelectForTodayOnwards();

                /* Truy vấn kiểm tra dữ liệu */
                Cursor cursor = context.getContentResolver().query(
                        forecastQueryUri,
                        projectionColumns,
                        selectionStatement,
                        null,
                        null);
                //  Hoàn thành (6) Nếu rỗng hoặc có Cursor null, hãy đồng bộ thời tiết
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                /* Đóng con trỏ để tránh thất thoát bộ nhớ */
                cursor.close();
                return null;
            }
        }.execute();
    }

    /**
     * Helper để thực hiện đồng bộ ngay lập tức bằng cách sử dụng một IntentService để thực hiện đồng bộ
     *
     * @param context Context được sử dụng để bắt đầu IntentService cho đồng bộ.
     */
    public static void startImmediateSync(@NonNull final Context context) {
        Intent intentToSyncImmediately = new Intent(context, WeatherSyncIntentService.class);
        context.startService(intentToSyncImmediately);
    }
}