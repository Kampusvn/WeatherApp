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
import android.support.annotation.NonNull;

import com.example.android.weather.data.WeatherContract;

public class WeatherSyncUtils {

//  TODO (10) Thêm hằng số để đồng bộ Weather mỗi 3 - 4 giờ

    private static boolean sInitialized;

//  TODO (11) Thêm một tag đồng bộ để xác định công việc đồng bộ hóa

//  TODO (12) Viết một phương thức lên lịch đồng bộ thời tiết

    /**
     * Tạo các nhiệm vụ đồng bộ định kỳ và kiểm tra xem liệu có cần phải đồng bộ hóa ngay lập tức hay không.
     * Nếu yêu cầu đồng bộ hóa lập tức, phương thức này sẽ đảm bảo rằng sự đồng bộ xảy ra.
     *
     * @param context Context truyền vào phương thức và dùng để truy cập ContentResolver
     */
    synchronized public static void initialize(@NonNull final Context context) {

        /*
         * Chỉ thực hiện khởi tạo một lần cho mỗi vòng đời ứng dụng. Nếu khởi tạo đã được thực hiện,
		 * Không làm gì trong phương thức
         */
        if (sInitialized) return;

        sInitialized = true;

//      TODO (13) Gọi phương thức đã tạo để lên kế hoạch đồng bộ hóa thời tiết định kỳ

        /*
         * Chúng ta cần kiểm tra xem liệu ContentProvider có dữ liệu để hiển thị trong danh sách hay không.
		 * Tuy nhiên, thực hiện một truy vấn trên main thread là một ý tưởng tồi vì điều này có thể gây lag UI.
		 * Do đó, ta tạo một thread để chạy truy vấn để kiểm tra nội dung của ContentProvider.
         */
        Thread checkForEmpty = new Thread(new Runnable() {
            @Override
            public void run() {

                /* URI cho mỗi hàng dữ liệu trong bảng*/
                Uri forecastQueryUri = WeatherContract.WeatherEntry.CONTENT_URI;

                /*
                 * Vì truy vấn này sẽ chỉ được sử dụng như một kiểm tra để xem liệu chúng ta có bất kỳ dữ liệu nào
				 * (thay vì hiển thị dữ liệu), chúng ta chỉ cần PROJECT ID của mỗi hàng. Trong các truy vấn, nơi
				 * chúng ta hiển thị dữ liệu, ta cần PROJECT thêm các cột để xác định xem những chi tiết thời tiết
				 * nào cần được hiển thị.
                 */
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
                /*
                 * Một đối tượng Cursor có thể là null vì nhiều lý do khác nhau. Một số được liệt kê dưới đây.
                 *
                 *   1) URI không hợp lệ
                 *   2) Một phương thức truy vấn ContentProvider nào đó trả về null
                 *   3) Có ngoại lệ RemoteException.
                 *
                 * Ta cũng nên kiểm tra xem con trỏ trả về từ ContentResolver có null hay không
                 *
				 * Nếu Cursor null hoặc rỗng, chúng ta cần phải đồng bộ ngay lập tức để có thể
				 * hiển thị dữ liệu cho người dùng.
                 */
                if (null == cursor || cursor.getCount() == 0) {
                    startImmediateSync(context);
                }

                /* Đóng con trỏ để tránh thất thoát bộ nhớ */
                cursor.close();
            }
        });

        /* Cuối cùng, một khi các thread đã được chuẩn bị, triển khai để thực hiện kiểm tra. */
        checkForEmpty.start();
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