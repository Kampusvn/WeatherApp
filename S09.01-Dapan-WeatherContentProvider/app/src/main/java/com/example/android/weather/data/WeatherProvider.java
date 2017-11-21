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
package com.example.android.weather.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;

/**
 * Lớp này làm việc như ContentProvider cho tất cả dữ liệu của Weather.
 * Lớp này cho phép ta bulkInsert, truy vấn và xóa dữ liệu.
 *
 * Mặc dù việc triển khai ContentProvider đòi hỏi phải thực hiện các phương thức bổ sung để
 * thực hiện việc thêm, cập nhật và khả năng nhận được loại dữ liệu từ một URI.
 * Tuy nhiên, ở đây chúng không được thực hiện để cho ngắn gọn và đơn giản.
 * Nếu bạn muốn, bạn có thể tự làm việc này.
 */
public class WeatherProvider extends ContentProvider {

    //  Hoàn thành (5) Khai báo các giá trị static constant integer tên là CODE_WEATHER & CODE_WEATHER_WITH_DATE để xác định các URI mà ContentProvider này có thể xử lý

    public static final int CODE_WEATHER = 100;
    public static final int CODE_WEATHER_WITH_DATE = 101;

    //  Hoàn thành (8) Khởi tạo một UriMatcher tĩnh bằng cách sử dụng phương pháp buildUriMatcher
    /*
	 * URI Matcher được sử dụng bởi content provider này. Tên "s" trước tên biến cho biết rằng
	 * UriMatcher này là một biến thành viên tĩnh của WeatherProvider và là quy ước chung
	 * trong lập trình Android.
     */
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    //  Hoàn thành (1) Khai báo nhưng không khởi tạo một đối tượng WeatherDbHelper tên là mOpenHelper
    private WeatherDbHelper mOpenHelper;

//  Hoàn thành (7) Viết một phương thức gọi là buildUriMatcher để match URI với ID
    public static UriMatcher buildUriMatcher() {

        /*
		 * Tất cả các đường dẫn được thêm vào UriMatcher có một code tương ứng để trả về khi có thể match.
		 * Code này được truyền vào constructor của UriMatcher đại diện cho code để trả về cho URI gốc.
		 * Thường sử dụng NO_MATCH làm code cho trường hợp này.
         */
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = WeatherContract.CONTENT_AUTHORITY;

        /*
		 * Đối với mỗi loại URI bạn muốn thêm vào, hãy tạo một code tương ứng. Tốt hơn hết là những
		 * trường constant trong lớp của bạn để ta có thể sử dụng chúng trong cả một lớp, và không đổi
		 * giá trị. Trong Weather, ta sử dụng CODE_WEATHER hoặc CODE_WEATHER_WITH_DATE.
         */

        /* URI này là content://com.example.android.weather/weather/ */
        matcher.addURI(authority, WeatherContract.PATH_WEATHER, CODE_WEATHER);

        /*
		 * URI này sẽ giống kiểu content: //com.example.android.weather/weather/1472214172
		 * Ở đây "/ #" biểu thị cho UriMatcher: nếu có bất kì số nào đằng sau PATH_WEATHER, nó sẽ trả về
		 * code "CODE_WEATHER_WITH_DATE"
         */
        matcher.addURI(authority, WeatherContract.PATH_WEATHER + "/#", CODE_WEATHER_WITH_DATE);

        return matcher;
    }

	//  Hoàn thành (2) Override onCreate
    @Override
    public boolean onCreate() {
        /*
         * onCreate được chạy trên main thread, do đó, hoạt động nào tốn thời gian đều sẽ gây ra lag
		 * ứng dụng. Vì constructor của WeatherDbHelper rất nhẹ nên vẫn được đặt ở đây.
         */
//      Hoàn thành (3) Trong onCreate, instantiate mOpenHelper
        mOpenHelper = new WeatherDbHelper(getContext());

//      Hoàn thành (4) Trả về true từ onCreate để biểu thị thiết lập thành công
        return true;
    }

    @Override
    public int bulkInsert(@NonNull Uri uri, @NonNull ContentValues[] values) {
        throw new RuntimeException("Student, you need to implement the bulkInsert method!");
    }

//  Hoàn thành (9) Cung cấp implementation cho phương thức truy vấn
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor cursor;

//      COMPLETED (10) Xử lý các URI truy vấn về thời tiết và thời tiết trong ngày
        /*
		 * Dưới đây là câu lệnh switch, với URI, sẽ xác định loại yêu cầu đang được thực hiện
		 * và truy vấn cơ sở dữ liệu phù hợp.
         */
        switch (sUriMatcher.match(uri)) {
            case CODE_WEATHER_WITH_DATE: {

                /*
				 * Để xác định ngày gắn với URI này, chúng ta nhìn vào đoạn đường dẫn cuối cùng.
                 */
                String normalizedUtcDateString = uri.getLastPathSegment();

                /*
				 * Phương thức truy vấn chấp nhận một chuỗi các đối số, vì có thể có nhiều hơn một "?"
				 * trong statement đã chọn. Mặc dù trong trường hợp của chúng ta, chúng ta chỉ có một "?",
				 * nhưng vẫn phải tạo ra một mảng chuỗi chỉ chứa một phần tử vì phương thức này chỉ
				 * chấp nhận một mảng chuỗi.
                 */
                String[] selectionArguments = new String[]{normalizedUtcDateString};

                cursor = mOpenHelper.getReadableDatabase().query(
                        /* Bảng sẽ truy vấn */
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        /*
						 * Một phép chiếu chỉ định các cột mà ta muốn trả về trong con trỏ.
						 * Chuyền vào null sẽ trả về tất cả các cột của dữ liệu bên trong con trỏ.
						 * Tuy nhiên, nếu không cần tất cả dữ liệu từ bảng, tốt nhất bạn nên giới
						 * hạn các cột được trả về bằng con trỏ với một phép chiếu.
                         */
                        projection,
                        /*
						 * URI khớp với CODE_WEATHER_WITH_DATE chứa một ngày ở cuối (của URI).
						 * Ta trích xuất ngày đó và sử dụng nó với hai dòng tiếp theo này để xác định
						 * hàng muốn trả về trong con trỏ. Ta sử dụng dấu chấm hỏi ở đây và sau đó
						 * chỉ định lựa chọnArguments làm đối số tiếp theo để đảm bảo hiệu suất.
						 * Bất cứ chuỗi nào được chứa trong mảng selectionArguments sẽ được chèn
						 * vào câu lệnh select của SQLite.
                         */
                        WeatherContract.WeatherEntry.COLUMN_DATE + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        sortOrder);

                break;
            }

            case CODE_WEATHER: {
                cursor = mOpenHelper.getReadableDatabase().query(
                        WeatherContract.WeatherEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);

                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

//      Hoàn thành (11) Gọi setNotificationUri trên con trỏ và trả về con trỏ
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Xóa dữ liệu tại một URI nhất định với các đối số tùy chọn.
     *
     * @param uri           URI truy vấn
     * @param selection     Giới hạn tùy chọn để áp dụng cho hàng khi xóa.
     * @param selectionArgs Được sử dụng cùng với statement lựa chọn
     * @return số hàng bị xóa
     */
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        throw new RuntimeException("Student, you need to implement the delete method!");
    }

    /**
	 * Trong Weather, ta sẽ không sử dụng phương thức này. Tuy nhiên, ta vẫn phải ghi đè nó
	 * vì WeatherProvider mở rộng ContentProvider và getType là một phương pháp trừu tượng
	 * trong ContentProvider. Thông thường, phương thức này xử lý yêu cầu cho loại MIME của
	 * dữ liệu tại URI nhất định. Ví dụ: nếu ứng dụng của bạn cung cấp hình ảnh tại một URI
	 * cụ thể, thì bạn sẽ trả lại URI hình ảnh từ phương thức này.
     *
     * @param uri URI truy vấn.
     * @return không trả về gì trong Weather, nhưng thường là một chuỗi kiểu MIME, hoặc null nếu không thuộc loại nào.
     */
    @Override
    public String getType(@NonNull Uri uri) {
        throw new RuntimeException("We are not implementing getType in Weather.");
    }

    /**
     * Trong Weather, ta không làm bất cứ điều gì với phương thức này. Tuy nhiên, ta vẫn
	 * phải ghi đè nó, như chú thích bên trên.
     */
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        throw new RuntimeException(
                "We are not implementing insert in Weather. Use bulkInsert instead");
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        throw new RuntimeException("We are not implementing update in Weather");
    }

    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }
}