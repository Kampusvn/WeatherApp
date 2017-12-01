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
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.weather.utilities.WeatherDateUtils;
import com.example.android.weather.utilities.WeatherUtils;

class ForecastAdapter extends RecyclerView.Adapter<ForecastAdapter.ForecastAdapterViewHolder> {

//  TODO (1) Thêm layout list_item_forecast_today
//  TODO (2) Sử dụng ConstraintLayout, implement layout cho today list item

//  TODO (4) Tạo file resource bools.xml trong res/values-port
//  TODO (5) Bên trong bools.xml trong thư mục portrait, thêm bool tên là called use_today_layout và đặt giá trị false

//  TODO (6) Khai báo hằng ID cho ViewType hôm nay và một ngày trong tương lai

    /* Context sử dụng cho các phương pháp hữu ích, tài nguyên ứng dụng và layout inflater */
    private final Context mContext;

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
        void onClick(long date);
    }

//  TODO (7) Khai báo biến private boolean tên là mUseTodayLayout

    private Cursor mCursor;

    /**
     * Tạo ForecastAdapter.
     *
     * @param clickHandler Trình quản lý on-click cho adapter này. Được gọi khi người dùng bấm vào một item
     */
    public ForecastAdapter(@NonNull Context context, ForecastAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
//      TODO (8) Đặt mUseTodayLayout thành giá trị trong resources
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

//      TODO (12) Nếu view type của layout là hôm nay, dùng today layout

//      TODO (13) Nếu view type của layout là ngày trong tương lai, dùng future day layout

//      TODO (14) Nếu không, ném ngoại lệ IllegalArgumentException

        View view = LayoutInflater
                .from(mContext)
                .inflate(R.layout.forecast_list_item, viewGroup, false);

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
        mCursor.moveToPosition(position);

        /****************
         * Tóm tắt Weather *
         ****************/
        int weatherId = mCursor.getInt(MainActivity.INDEX_WEATHER_CONDITION_ID);
        int weatherImageId;

//      TODO (15) Nếu view type của layout là today, hiển thị icon lớn

//      TODO (16) Nếu view type của layout là ngày khác, hiển thị icon nhỏ

//      TODO (17) Nếu không, ném ngoại lệ IllegalArgumentException

        weatherImageId = WeatherUtils
                .getSmallArtResourceIdForWeatherCondition(weatherId);

        forecastAdapterViewHolder.iconView.setImageResource(weatherImageId);

        /****************
         * Weather Date *
         ****************/
         /* Đọc dữ liệu từ con trỏ */
        long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
        String dateString = WeatherDateUtils.getFriendlyDateString(mContext, dateInMillis, false);

         /* Hiển thị chuỗi date */
        forecastAdapterViewHolder.dateView.setText(dateString);

        /***********************
         * 		Mô tả Weather  *
         ***********************/
        String description = WeatherUtils.getStringForWeatherCondition(mContext, weatherId);
         /* Tạo chuỗi truy cập (a11y) từ mô tả thời tiết */
        String descriptionA11y = mContext.getString(R.string.a11y_forecast, description);

         /* Đặt text mô tả content (dùng để truy cập) */
        forecastAdapterViewHolder.descriptionView.setText(description);
        forecastAdapterViewHolder.descriptionView.setContentDescription(descriptionA11y);

        /**************************
         *    Nhiệt độ cao nhất   *
         **************************/
         /* Đọc nhiệt độ cao nhất từ con trỏ (Độ C) */
        double highInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MAX_TEMP);
         /*
          * Nếu người dùng lựa chọn độ F, định dạng Temperature sẽ chuyển đổi nhiệt độ.
		  * Phương thức này cũng sẽ nối thêm hoặc ° C hoặc ° F với chuỗi nhiệt độ.
          */
        String highString = WeatherUtils.formatTemperature(mContext, highInCelsius);
         /* Tạo chuỗi truy cập (a11y) từ mô tả thời tiết */
        String highA11y = mContext.getString(R.string.a11y_high_temp, highString);

         /* Đặt text mô tả content (dùng để truy cập) */
        forecastAdapterViewHolder.highTempView.setText(highString);
        forecastAdapterViewHolder.highTempView.setContentDescription(highA11y);

        /*************************
         *   Nhiệt độ thấp nhất  *
         *************************/
         /* Đọc nhiệt độ thấp nhất từ con trỏ (Độ C) */
        double lowInCelsius = mCursor.getDouble(MainActivity.INDEX_WEATHER_MIN_TEMP);
         /*
          * Nếu người dùng lựa chọn độ F, định dạng Temperature sẽ chuyển đổi nhiệt độ.
		  * Phương thức này cũng sẽ nối thêm hoặc ° C hoặc ° F với chuỗi nhiệt độ.
          */
        String lowString = WeatherUtils.formatTemperature(mContext, lowInCelsius);
        String lowA11y = mContext.getString(R.string.a11y_low_temp, lowString);

         /* Đặt text mô tả content (dùng để truy cập) */
        forecastAdapterViewHolder.lowTempView.setText(lowString);
        forecastAdapterViewHolder.lowTempView.setContentDescription(lowA11y);
    }

    /**
     * Phương thức này chỉ đơn giản trả về số lượng các item hiển thị. Nó được sử dụng ngầm
     * để giúp bố trí Views
     *
     * @return Số lượng item được hiển thị
     */
    @Override
    public int getItemCount() {
        if (null == mCursor) return 0;
        return mCursor.getCount();
    }

//  TODO (9) Override getItemViewType
//      TODO (10) Bên trong getItemViewType, nếu mUseTodayLayout là true và position là 0, trả về ID today viewType
//      TODO (11) Nếu không, trả về ID future day viewType

    void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    /**
     * ViewHolder là một phần bắt buộc của mô hình cho RecyclerViews. Nó chủ yếu hoạt động như
     * một bộ nhớ cache của các view con cho một item dự báo. Nó cũng là một nơi thuận tiện để
     * thiết lập OnClickListener, vì nó có quyền truy cập vào adapter và các view.
     */
    class ForecastAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        final ImageView iconView;

        final TextView dateView;
        final TextView descriptionView;
        final TextView highTempView;
        final TextView lowTempView;

        ForecastAdapterViewHolder(View view) {
            super(view);

            iconView = (ImageView) view.findViewById(R.id.weather_icon);
            dateView = (TextView) view.findViewById(R.id.date);
            descriptionView = (TextView) view.findViewById(R.id.weather_description);
            highTempView = (TextView) view.findViewById(R.id.high_temperature);
            lowTempView = (TextView) view.findViewById(R.id.low_temperature);

            view.setOnClickListener(this);
        }

        /**
         * Được gọi bởi view con khi được bấm.
         *
         * @param v View được bấm
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            long dateInMillis = mCursor.getLong(MainActivity.INDEX_WEATHER_DATE);
            mClickHandler.onClick(dateInMillis);
        }
    }
}