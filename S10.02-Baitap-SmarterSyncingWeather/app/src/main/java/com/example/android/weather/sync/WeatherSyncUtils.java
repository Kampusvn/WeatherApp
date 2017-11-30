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
import android.support.annotation.NonNull;


public class WeatherSyncUtils {

//  TODO (1) Khai báo trường private static boolean tên là sInitialized

    //  TODO (2) Tạo phương thức đồng bộ public static void tên là initialize
    //  TODO (3) Thực thi phương thức khi sInitialized bằng false
    //  TODO (4) Nếu phương thức được thực thi, đặt sInitialized thành true
    //  TODO (5) Kiểm tra ContentProvider có rỗng hay không
    //  TODO (6) Nếu rỗng hoặc có Cursor null, hãy đồng bộ thời tiết

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