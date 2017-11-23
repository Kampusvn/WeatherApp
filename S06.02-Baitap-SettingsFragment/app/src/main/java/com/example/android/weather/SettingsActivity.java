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
package com.example.android.weather;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

/**
 * Tải SettingsFragment để quản lý hành vi của up button.
 */
public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_settings);
        this.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // TODO (2) Tạo thư mục xml resource
        // TODO (3) Thêm PreferenceScreen với một EditTextPreference và ListPreference trong thư mục resource xml được tạo mới

        // TODO (4) Tạo SettingsFragment và extend từ PreferenceFragmentCompat

        // Thực hiện bước 5 - 11 trong SettingsFragment
        // TODO (10) Implement OnSharedPreferenceChangeListener ở SettingsFragment

        // TODO (8) Viết phương thức setPreferenceSummary nhận một Preference và một Object và thiết lập summary preference

        // TODO (5) Override phương thức onCreatePreferences và thêm file preference xml sử dụng addPreferencesFromResource

        // Thực hiện bước 9 ở onCreatePreference
        // TODO (9) Dùng preference summary ở preference không phải CheckBoxPreference

        // TODO (13) Hủy đăng ký SettingsFragment(this) như SharedPreferenceChangedListener trong phương thức onStop

        // TODO (12) Đăng ký SettingsFragment(this) như SharedPreferenceChangedListener trong phương thức onStart

        // TODO (11) Override phương thức onSharedPreferenceChanged để cập nhật các preference không phải CheckBoxPreferences khi chúng thay đổi
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
        }

        return super.onOptionsItemSelected(item);
    }
}