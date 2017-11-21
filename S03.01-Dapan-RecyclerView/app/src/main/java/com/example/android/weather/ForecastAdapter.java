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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

// Hoàn thành (15) Thêm lớp ForecastAdapter
// Hoàn thành (22) Extend RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> ở tên lớp
/**
 * {@link ForecastAdapter} exposes a list of weather forecasts to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    // Hoàn thành (23) Tạo biến private string array và đặt tên là mWeatherData
    private String[] mWeatherData;

    // Hoàn thành (47) Viết constructor mặc định (Ta sẽ chuyền tham số vào sau)
    public ForecastAdapter() {

    }

    // Hoàn thành (16) Thêm lớp ForecastAdapterViewHolder trong ForecastAdapter
    // Hoàn thành (17) Extend RecyclerView.ViewHolder ở tên lớp
    /**
     * Bộ nhớ cache của view con cho item.
     */
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder {

        // Trong ForecastAdapterViewHolder ///////////////////////////////////////////////////////
        // Hoàn thành (18) Tạo biến public final TextView tên là mWeatherTextView
        public final TextView mWeatherTextView;

        // Hoàn thành (19) Viết constructor nhận tham số là một View
        // Hoàn thành (20) Gọi super(view) trong constructor cho ForecastAdapterViewHolder
        // Hoàn thành (21) Dùng view.findViewById, lấy tham chiếu đến TextView của bố cục này và lưu nó vào mWeatherTextView
        public ForecastAdapterViewHolder(View view) {
            super(view);
            mWeatherTextView = (TextView) view.findViewById(R.id.tv_weather_data);
        }
        // Trong ForecastAdapterViewHolder ///////////////////////////////////////////////////////
    }

    // Hoàn thành (24) Override onCreateViewHolder
    // Hoàn thành (25) Trong onCreateViewHolder, inflate danh sách item xml vào một view
    // Hoàn thành (26) Trong onCreateViewHolder, trả về ForecastAdapterViewHolder với view được chuyền vào bằng tham số
    /**
     * Phương thức này được gọi mỗi khi ViewHolder được tạo ra. Điều này xảy ra khi RecyclerView
     * được đặt lên màn hình. ViewHolders sẽ được tạo ra đủ để lấp đầy màn hình và cho phép cuộn.
     *
     * @param viewGroup ViewGroup chứa ViewHolders.
     * @param viewType  Nếu RecyclerView có nhiều hơn một loại item (ở đây thì không hơn), bạn
     *                  có thể dùng viewType integer để cung cấp kiểu layout khác. Xem
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  để biết thêm chi tiết.
     * @return Một ForecastAdapterViewHolder mới chứa View cho mỗi item trong danh sách
     */
    @Override
    public ForecastAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.forecast_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, shouldAttachToParentImmediately);
        return new ForecastAdapterViewHolder(view);
    }

    // Hoàn thành (27) Override onBindViewHolder
    // Hoàn thành (28) Đặt text của TextView cho các vị trí item trong danh sách
    /**
     * OnBindViewHolder được gọi bởi RecyclerView để hiển thị dữ liệu ở vị trí xác định.
     * Trong phương thức này, chúng ta cập nhật nội dung của ViewHolder để hiển thị các
     * chi tiết thời tiết cho vị trí cụ thể này, sử dụng đối số "position" chuyền vào.
     *
     * @param forecastAdapterViewHolder ViewHolder phải được cập nhật để đại diện cho nội dung
     *                                  của mục tại vị trí đã cho trong bộ dữ liệu.
     * @param position                  Vị trí trong bộ dữ liệu..
     */
    @Override
    public void onBindViewHolder(ForecastAdapterViewHolder forecastAdapterViewHolder, int position) {
        String weatherForThisDay = mWeatherData[position];
        forecastAdapterViewHolder.mWeatherTextView.setText(weatherForThisDay);
    }

    // Hoàn thành (29) Override phương thức getItemCount
    // Hoàn thành (30) Trả về 0 nếu mWeatherData bằng null, hoặc kích cỡ của mWeatherData nếu không null
    /**
     * Phương thức này chỉ đơn giản trả về số lượng các item hiển thị. Nó được sử dụng ngầm
     * để giúp bố trí Views
     *
     * @return Số lượng item được hiển thị
     */
    @Override
    public int getItemCount() {
        if (null == mWeatherData) return 0;
        return mWeatherData.length;
    }

    // Hoàn thành (31) Tạo phương thức setWeatherData lưu dữ liệu thời tiết vào mWeatherData
    // Hoàn thành (32) Sau khi lưu mWeatherData, gọi notifyDataSetChanged
    /**
     * Phương thức này được sử dụng để thiết lập dự báo thời tiết trên ForecastAdapter
     * nếu chúng ta đã tạo dự báo trước. Điều này rất hữu ích khi lấy dữ liệu mới từ web nhưng
     * không muốn tạo một ForecastAdapter mới để hiển thị nó.
     *
     * @param weatherData Dữ liệu mới được hiển thị
     */
    public void setWeatherData(String[] weatherData) {
        mWeatherData = weatherData;
        notifyDataSetChanged();
    }
}