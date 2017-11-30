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
import android.os.AsyncTask;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;

// Hoàn thành (2) import jobdispatcher.JobService, không phải job.JobService

// Hoàn thành (3) Viết class WeatherFirebaseJobService extends từ jobdispatcher.JobService
public class WeatherFirebaseJobService extends JobService {

    //  Hoàn thành (4) Khai báo một trường ASyncTask tên là mFetchWeatherTask
    private AsyncTask<Void, Void, Void> mFetchWeatherTask;

    //  Hoàn thành (5) Override phương thức onStartJob và trong đó, tạo ra một ASyncTask riêng để đồng bộ dữ liệu thời tiết
    @Override
    public boolean onStartJob(final JobParameters jobParameters) {

        mFetchWeatherTask = new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... voids) {
                Context context = getApplicationContext();
                WeatherSyncTask.syncWeather(context);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                // Hoàn thành (6) Khi các dữ liệu thời tiết được đồng bộ, gọi jobFinished với các đối số thích hợp
                jobFinished(jobParameters, false);
            }
        };

        mFetchWeatherTask.execute();
        return true;
    }

    // Hoàn thành (7) Override phương thức onStopJob, hủy ASyncTask nếu nó không null và trả về true
    @Override
    public boolean onStopJob(JobParameters jobParameters) {
        if (mFetchWeatherTask != null) {
            mFetchWeatherTask.cancel(true);
        }
        return true;
    }
}