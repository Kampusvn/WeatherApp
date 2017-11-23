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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * {@link ForecastAdapter} exposes a list of weather forecasts to a
 * {@link android.support.v7.widget.RecyclerView}
 */
public class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    private String[] mWeatherData;

    /*
     * Trình xử lý giúp activity có thể giao tiếp với RecyclerView dễ dàng
     */
    final private ForecastAdapterOnClickHandler mClickHandler;

    /**
     * Interface that nhận onClick messages.
     */
    public interface ForecastAdapterOnClickHandler {
        void onClick(String weatherForDay);
    }

    /**
     * Tạo ForecastAdapter.
     *
     * @param clickHandler Trình quản lý on-click cho adapter này. Được gọi khi người dùng bấm vào một item
     */
    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    /**
     * Bộ nhớ cache của view con cho item.
     */
    public class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public final TextView mWeatherTextView;

        public ForecastAdapterViewHolder(View view) {
            super(view);
            mWeatherTextView = (TextView) view.findViewById(R.id.tv_weather_data);
            view.setOnClickListener(this);
        }

        /**
         * Được gọi bởi view con khi bấm nút.
         *
         * @param v View được bấm vào
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String weatherForDay = mWeatherData[adapterPosition];
            mClickHandler.onClick(weatherForDay);
        }
    }

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