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

import android.app.IntentService;
import android.content.Intent;

// Hoàn thành (5) Tạo lớp WeatherSyncIntentService extends từ IntentService

/**
 * {@link IntentService} phân lớp để xử lý các yêu cầu công việc không đồng bộ trong một service
 * trên một trình xử lý riêng.
 */
public class WeatherSyncIntentService extends IntentService {

    //  Hoàn thành (6) Tạo constructor gọi đến super và truyền vào tên lớp
    public WeatherSyncIntentService() {
        super("WeatherSyncIntentService");
    }

    //  Hoàn thành (7) Override onHandleIntent, bên trong đó, gọi WeatherSyncTask.syncWeather
    @Override
    protected void onHandleIntent(Intent intent) {
        WeatherSyncTask.syncWeather(this);
    }
}