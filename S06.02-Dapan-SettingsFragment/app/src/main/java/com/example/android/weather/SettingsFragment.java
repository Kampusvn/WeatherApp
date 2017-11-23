
package com.example.android.weather;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.preference.CheckBoxPreference;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceFragmentCompat;
import android.support.v7.preference.PreferenceScreen;

// Hoàn thành (4) Tạo SettingsFragment và extend từ PreferenceFragmentCompat
public class SettingsFragment extends PreferenceFragmentCompat implements
        // Hoàn thành (10) Implement OnSharedPreferenceChangeListener từ SettingsFragment
        SharedPreferences.OnSharedPreferenceChangeListener {

    // Hoàn thành (8) Viết phương thức setPreferenceSummary nhận một Preference và một Object và thiết lập summary preference
    private void setPreferenceSummary(Preference preference, Object value) {
        String stringValue = value.toString();
        String key = preference.getKey();

        if (preference instanceof ListPreference) {
            ListPreference listPreference = (ListPreference) preference;
            int prefIndex = listPreference.findIndexOfValue(stringValue);
            if (prefIndex >= 0) {
                preference.setSummary(listPreference.getEntries()[prefIndex]);
            }
        } else {
            preference.setSummary(stringValue);
        }
    }

    // Hoàn thành (5) Override phương thức onCreatePreferences và thêm file preference xml sử dụng addPreferencesFromResource
    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        addPreferencesFromResource(R.xml.pref_general);

        // Hoàn thành (9) Dùng preference summary ở preference không phải CheckBoxPreference
        SharedPreferences sharedPreferences = getPreferenceScreen().getSharedPreferences();
        PreferenceScreen prefScreen = getPreferenceScreen();
        int count = prefScreen.getPreferenceCount();
        for (int i = 0; i < count; i++) {
            Preference p = prefScreen.getPreference(i);
            if (!(p instanceof CheckBoxPreference)) {
                String value = sharedPreferences.getString(p.getKey(), "");
                setPreferenceSummary(p, value);
            }
        }
    }

    // Hoàn thành (13) Hủy đăng ký SettingsFragment(this) như SharedPreferenceChangedListener trong phương thức onStop
    @Override
    public void onStop() {
        super.onStop();
        getPreferenceScreen().getSharedPreferences()
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    // Hoàn thành (12) Đăng ký SettingsFragment(this) như SharedPreferenceChangedListener trong phương thức onStart
    @Override
    public void onStart() {
        super.onStart();
        getPreferenceScreen().getSharedPreferences()
                .registerOnSharedPreferenceChangeListener(this);
    }

    // Hoàn thành (11) Override phương thức onSharedPreferenceChanged để cập nhật các preference không phải CheckBoxPreferences khi chúng thay đổi
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        Preference preference = findPreference(key);
        if (null != preference) {
            if (!(preference instanceof CheckBoxPreference)) {
                setPreferenceSummary(preference, sharedPreferences.getString(key, ""));
            }
        }
    }
}