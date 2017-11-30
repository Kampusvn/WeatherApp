package com.example.android.weather.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.ContextCompat;

import com.example.android.weather.DetailActivity;
import com.example.android.weather.R;
import com.example.android.weather.data.WeatherPreferences;
import com.example.android.weather.data.WeatherContract;

public class NotificationUtils {

    /*
     * Các cột dữ liệu mà chúng ta muốn hiển thị trong thông báo để cho người dùng biết có dữ liệu thời tiết mới.
     */
    public static final String[] WEATHER_NOTIFICATION_PROJECTION = {
            WeatherContract.WeatherEntry.COLUMN_WEATHER_ID,
            WeatherContract.WeatherEntry.COLUMN_MAX_TEMP,
            WeatherContract.WeatherEntry.COLUMN_MIN_TEMP,
    };

    /*
     * Lưu trữ các chỉ số của các giá trị trong mảng String phía trên để có thể truy cập dữ liệu nhanh chóng từ truy vấn.
	 * Nếu thứ tự của Strings ở trên thay đổi, các chỉ số này phải được điều chỉnh để phù hợp.
     */
    public static final int INDEX_WEATHER_ID = 0;
    public static final int INDEX_MAX_TEMP = 1;
    public static final int INDEX_MIN_TEMP = 2;

    //  Hoàn thành (1) Tạo một hằng int để xác định thông báo
    private static final int WEATHER_NOTIFICATION_ID = 3004;

    /**
     * Xây dựng và hiển thị thông báo cho thời tiết vừa được cập nhật cho hôm nay.
     *
     * @param context Context dùng để truy vấn ContentProvider và dùng trong các phương thức Utility
     */
    public static void notifyUserOfNewWeather(Context context) {

        /* Xây dựng URI cho thời tiết hôm nay để hiển thị dữ liệu cập nhật trong thông báo */
        Uri todaysWeatherUri = WeatherContract.WeatherEntry
                .buildWeatherUriWithDate(WeatherDateUtils.normalizeDate(System.currentTimeMillis()));

        /*
         * MAIN_FORECAST_PROJECTION được truyền vào làm tham số thứ hai đã được định nghĩa trong lớp WeatherContract
		 * và được sử dụng để giới hạn các cột được trả lại trong con trỏ.
         */
        Cursor todayWeatherCursor = context.getContentResolver().query(
                todaysWeatherUri,
                WEATHER_NOTIFICATION_PROJECTION,
                null,
                null,
                null);

        /*
         * Nếu todayWeatherCursor rỗng, moveToFirst sẽ trả về false. Nếu con trỏ không rỗng, ta sẽ hiển thị thông báo.
         */
        if (todayWeatherCursor.moveToFirst()) {

            /* ID thời tiết do API trả lại, dùng để xác định biểu tượng sẽ được sử dụng */
            int weatherId = todayWeatherCursor.getInt(INDEX_WEATHER_ID);
            double high = todayWeatherCursor.getDouble(INDEX_MAX_TEMP);
            double low = todayWeatherCursor.getDouble(INDEX_MIN_TEMP);

            Resources resources = context.getResources();
            int largeArtResourceId = WeatherUtils
                    .getLargeArtResourceIdForWeatherCondition(weatherId);

            Bitmap largeIcon = BitmapFactory.decodeResource(
                    resources,
                    largeArtResourceId);

            String notificationTitle = context.getString(R.string.app_name);

            String notificationText = getNotificationText(context, weatherId, high, low);

            /* getSmallArtResourceIdForWeatherCondition trả về hình ảnh phù hợp để hiển thị với ID */
            int smallArtResourceId = WeatherUtils
                    .getSmallArtResourceIdForWeatherCondition(weatherId);

//          Hoàn thành (2) Sử dụng NotificationCompat.Builder để bắt đầu xây dựng thông báo
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                    .setColor(ContextCompat.getColor(context,R.color.colorPrimary))
                    .setSmallIcon(smallArtResourceId)
                    .setLargeIcon(largeIcon)
                    .setContentTitle(notificationTitle)
                    .setContentText(notificationText)
                    .setAutoCancel(true);

//          Hoàn thành (3) Tạo một Intent với URI thích hợp để start DetailActivity
            Intent detailIntentForToday = new Intent(context, DetailActivity.class);
            detailIntentForToday.setData(todaysWeatherUri);

//          Hoàn thành (4) Sử dụng TaskStackBuilder để tạo PendingIntent thích hợp
            TaskStackBuilder taskStackBuilder = TaskStackBuilder.create(context);
            taskStackBuilder.addNextIntentWithParentStack(detailIntentForToday);
            PendingIntent resultPendingIntent = taskStackBuilder
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

//          Hoàn thành (5) Thiết lập nội dung cho Intent của NotificationBuilder
            notificationBuilder.setContentIntent(resultPendingIntent);

//          Hoàn thành (6) Lấy tham chiếu đến NotificationManager
            NotificationManager notificationManager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);

//          Hoàn thành (7) Thông báo cho người dùng bằng ID WEATHER_NOTIFICATION_ID
            /* WEATHER_NOTIFICATION_ID allows you to update or cancel the notification later on */
            notificationManager.notify(WEATHER_NOTIFICATION_ID, notificationBuilder.build());

//          Hoàn thành (8) Lưu thời gian gửi thông báo bằng WeatherPreferences
            WeatherPreferences.saveLastNotificationTime(context, System.currentTimeMillis());
        }

        /* Đóng con trỏ để tránh thất thoát bộ nhớ. */
        todayWeatherCursor.close();
    }

    /**
     * Xây dựng và trả về bản tóm tắt dự báo thời tiết cụ thể bằng cách sử dụng các phương thức và
     * tài nguyên hữu ích khác nhau để định dạng. Phương thức này chỉ được sử dụng để tạo văn bản
     * cho thông báo xuất hiện khi thời tiết được refresh.
     *
     * String trả về từ phương thức này sẽ trông như sau:
     *
     * Forecast: Sunny - High: 14°C Low 7°C
     *
     * @param context   Dùng truy cập các phương thức tiện ích và tài nguyên
     * @param weatherId ID Open Weather Map
     * @param high      Nhiệt độ cao nhất
     * @param low       Nhiệt độ thấp nhất
     */
    private static String getNotificationText(Context context, int weatherId, double high, double low) {

        String shortDescription = WeatherUtils
                .getStringForWeatherCondition(context, weatherId);

        String notificationFormat = context.getString(R.string.format_notification);

        String notificationText = String.format(notificationFormat,
                shortDescription,
                WeatherUtils.formatTemperature(context, high),
                WeatherUtils.formatTemperature(context, low));

        return notificationText;
    }
}