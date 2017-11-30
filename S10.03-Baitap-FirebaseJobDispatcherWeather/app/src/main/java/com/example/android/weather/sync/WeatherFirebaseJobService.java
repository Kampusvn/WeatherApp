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
// TODO (2) Hãy import jobdispatcher.JobService, không phải job.JobService

// TODO (3) Viết class WeatherFirebaseJobService extends từ jobdispatcher.JobService

//  TODO (4) Khai báo một trường ASyncTask tên là mFetchWeatherTask

//  TODO (5) Override phương thức onStartJob và trong đó, tạo ra một ASyncTask riêng để đồng bộ dữ liệu thời tiết
//              TODO (6) Khi các dữ liệu thời tiết được đồng bộ, gọi jobFinished với các đối số thích hợp

//  TODO (7) Override phương thức onStopJob, hủy ASyncTask nếu nó không null và trả về true