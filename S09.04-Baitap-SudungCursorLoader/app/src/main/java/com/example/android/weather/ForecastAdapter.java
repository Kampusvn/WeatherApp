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

class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

    //  TODO (14) Xóa khai báo mWeatherData và phương thức setWeatherData
    private String[] mWeatherData;
    //  TODO (1) Khai báo trường private final Context tên là mContext

    /*
	 * Dưới đây, ta định nghĩa một giao diện để xử lý khi bấm vào các item trong Adapter này.
	 * Trong constructor của ForecastAdapter, ta nhận được một thể hiện của một lớp đã thực hiện
	 * giao diện nói trên. Ta lưu trữ trường hợp đó trong biến này để gọi phương thức onClick
	 * bất cứ khi nào một item được bấm trong danh sách.
     */
    final private ForecastAdapterOnClickHandler mClickHandler;

    /**
     * Giao diện nhận thông báo onClick
     */
    public interface ForecastAdapterOnClickHandler {
        void onClick(String weatherForDay);
    }

//  TODO (2) Khai báo private Cursor field tên là mCursor
//  TODO (3) Thêm trường Context vào constructor và lưu trữ context đó vào mContext

    /**
     * Tạo ForecastAdapter.
     *
     * @param clickHandler Trình quản lý on-click cho adapter này. Được gọi khi người dùng bấm vào một item
     */
    public ForecastAdapter(ForecastAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
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
//      TODO (5) Xóa phần thân của onBindViewHolder
//      TODO (6) Di chuyển con trỏ đến vị trí thích hợp
//      TODO (7) Tạo bản tóm tắt thời tiết với ngày, mô tả, cao và thấp
        String weatherForThisDay = mWeatherData[position];
//      TODO (8) Hiển thị bản tóm tắt mà bạn đã tạo ở trên
        forecastAdapterViewHolder.weatherSummary.setText(weatherForThisDay);
    }

    /**
     * Phương thức này chỉ đơn giản trả về số lượng các item hiển thị. Nó được sử dụng ngầm
     * để giúp bố trí Views
     *
     * @return Số lượng item được hiển thị
     */
    @Override
    public int getItemCount() {
//      TODO (9) Xóa thân của getItemCount
//      TODO (10) Nếu mCursor là null, trả về 0. Nếu không, trả lại mcursor
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

//  TODO (11) Tạo một phương thức mới cho phép bạn đổi con trỏ.
//      TODO (12) Sau khi con trỏ mới được thiết lập, hãy gọi notifyDataSetChanged

    /**
     * ViewHolder là một phần bắt buộc của mô hình cho RecyclerViews. Nó chủ yếu hoạt động như
     * một bộ nhớ cache của các view con cho một item dự báo. Nó cũng là một nơi thuận tiện để
     * thiết lập OnClickListener, vì nó có quyền truy cập vào adapter và các view.
     */
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final TextView weatherSummary;

        ForecastAdapterViewHolder(View view) {
            super(view);
            weatherSummary = (TextView) view.findViewById(R.id.tv_weather_data);
            view.setOnClickListener(this);
        }

        /**
         * Được gọi bởi view con khi được bấm.
         *
         * @param v Viwe được bấm
         */
        @Override
        public void onClick(View v) {
            //  TODO (13) Thay vì chuyển string từ mảng dữ liệu, hãy sử dụng weatherSummary text
            int adapterPosition = getAdapterPosition();
            String weatherForDay = mWeatherData[adapterPosition];
            mClickHandler.onClick(weatherForDay);
        }
    }
}