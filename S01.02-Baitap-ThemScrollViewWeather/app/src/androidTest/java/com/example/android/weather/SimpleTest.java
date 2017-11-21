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
package com.example.android.weather;

import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class SimpleTest {

    /**
     * Việc này sẽ kiểm tra để đảm bảo rằng phiên bản hiện tại đang chạy ứng dụng sẽ
     * lớn hơn Gingerbread. Chúng tôi biết điều này sẽ đúng vì chúng tôi đã chỉ định SDK
     * tối thiểu cho ứng dụng của chúng tôi là API 15 và Gingerbread là API cấp 9,
     * nhưng ta muốn chứng minh một bài kiểm tra đơn giản rất dễ hiểu và chạy trên bộ
     * mô phỏng Android. Để chạy thử nghiệm này, nhấp chuột phải vào tệp này và nhấp vào
     * Chạy 'testAndroidVersion .....' Mặc định là sẽ pass. Để kiểm tra thất bại,
     * hãy đổi dấu '<' thành '>'.
     */
    @Test
    public void testAndroidVersionGreaterThanGingerbread() {
        int currentAndroidVersion = Build.VERSION.SDK_INT;
        int gingerbread = Build.VERSION_CODES.GINGERBREAD;
        assertTrue(currentAndroidVersion > gingerbread);
    }
}