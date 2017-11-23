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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import static com.example.android.weather.data.TestUtilities.getStaticIntegerField;
import static com.example.android.weather.data.TestUtilities.getStaticStringField;
import static com.example.android.weather.data.TestUtilities.studentReadableClassNotFound;
import static com.example.android.weather.data.TestUtilities.studentReadableNoSuchField;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TestWeatherDatabase {

    private final Context context = InstrumentationRegistry.getTargetContext();

    private static final String packageName = "com.example.android.weather";
    private static final String dataPackageName = packageName + ".data";

    private Class weatherEntryClass;
    private Class weatherDbHelperClass;
    private static final String weatherContractName = ".WeatherContract";
    private static final String weatherEntryName = weatherContractName + "$WeatherEntry";
    private static final String weatherDbHelperName = ".WeatherDbHelper";

    private static final String databaseNameVariableName = "DATABASE_NAME";
    private static String REFLECTED_DATABASE_NAME;

    private static final String databaseVersionVariableName = "DATABASE_VERSION";
    private static int REFLECTED_DATABASE_VERSION;

    private static final String tableNameVariableName = "TABLE_NAME";
    private static String REFLECTED_TABLE_NAME;

    private static final String columnDateVariableName = "COLUMN_DATE";
    static String REFLECTED_COLUMN_DATE;

    private static final String columnWeatherIdVariableName = "COLUMN_WEATHER_ID";
    static String REFLECTED_COLUMN_WEATHER_ID;

    private static final String columnMinVariableName = "COLUMN_MIN_TEMP";
    static String REFLECTED_COLUMN_MIN;

    private static final String columnMaxVariableName = "COLUMN_MAX_TEMP";
    static String REFLECTED_COLUMN_MAX;

    private static final String columnHumidityVariableName = "COLUMN_HUMIDITY";
    static String REFLECTED_COLUMN_HUMIDITY;

    private static final String columnPressureVariableName = "COLUMN_PRESSURE";
    static String REFLECTED_COLUMN_PRESSURE;

    private static final String columnWindSpeedVariableName = "COLUMN_WIND_SPEED";
    static String REFLECTED_COLUMN_WIND_SPEED;

    private static final String columnWindDirVariableName = "COLUMN_DEGREES";
    static String REFLECTED_COLUMN_WIND_DIR;

    private SQLiteDatabase database;
    private SQLiteOpenHelper dbHelper;

    @Before
    public void before() {
        try {

            weatherEntryClass = Class.forName(dataPackageName + weatherEntryName);
            if (!BaseColumns.class.isAssignableFrom(weatherEntryClass)) {
                String weatherEntryDoesNotImplementBaseColumns = "WeatherEntry class needs to " +
                        "implement the interface BaseColumns, but does not.";
                fail(weatherEntryDoesNotImplementBaseColumns);
            }

            REFLECTED_TABLE_NAME = getStaticStringField(weatherEntryClass, tableNameVariableName);
            REFLECTED_COLUMN_DATE = getStaticStringField(weatherEntryClass, columnDateVariableName);
            REFLECTED_COLUMN_WEATHER_ID = getStaticStringField(weatherEntryClass, columnWeatherIdVariableName);
            REFLECTED_COLUMN_MIN = getStaticStringField(weatherEntryClass, columnMinVariableName);
            REFLECTED_COLUMN_MAX = getStaticStringField(weatherEntryClass, columnMaxVariableName);
            REFLECTED_COLUMN_HUMIDITY = getStaticStringField(weatherEntryClass, columnHumidityVariableName);
            REFLECTED_COLUMN_PRESSURE = getStaticStringField(weatherEntryClass, columnPressureVariableName);
            REFLECTED_COLUMN_WIND_SPEED = getStaticStringField(weatherEntryClass, columnWindSpeedVariableName);
            REFLECTED_COLUMN_WIND_DIR = getStaticStringField(weatherEntryClass, columnWindDirVariableName);

            weatherDbHelperClass = Class.forName(dataPackageName + weatherDbHelperName);
            Class weatherDbHelperSuperclass = weatherDbHelperClass.getSuperclass();

            if (weatherDbHelperSuperclass == null || weatherDbHelperSuperclass.equals(Object.class)) {
                String noExplicitSuperclass =
                        "WeatherDbHelper needs to extend SQLiteOpenHelper, but yours currently doesn't extend a class at all.";
                fail(noExplicitSuperclass);
            } else if (weatherDbHelperSuperclass != null) {
                String weatherDbHelperSuperclassName = weatherDbHelperSuperclass.getSimpleName();
                String doesNotExtendOpenHelper =
                        "WeatherDbHelper needs to extend SQLiteOpenHelper but yours extends "
                                + weatherDbHelperSuperclassName;

                assertTrue(doesNotExtendOpenHelper,
                        SQLiteOpenHelper.class.isAssignableFrom(weatherDbHelperSuperclass));
            }

            REFLECTED_DATABASE_NAME = getStaticStringField(
                    weatherDbHelperClass, databaseNameVariableName);

            REFLECTED_DATABASE_VERSION = getStaticIntegerField(
                    weatherDbHelperClass, databaseVersionVariableName);

            int expectedDatabaseVersion = 1;
            String databaseVersionShouldBe1 = "Database version should be "
                    + expectedDatabaseVersion + " but isn't.";

            assertEquals(databaseVersionShouldBe1,
                    expectedDatabaseVersion,
                    REFLECTED_DATABASE_VERSION);

            Constructor weatherDbHelperCtor = weatherDbHelperClass.getConstructor(Context.class);

            dbHelper = (SQLiteOpenHelper) weatherDbHelperCtor.newInstance(context);

            context.deleteDatabase(REFLECTED_DATABASE_NAME);

            Method getWritableDatabase = SQLiteOpenHelper.class.getDeclaredMethod("getWritableDatabase");
            database = (SQLiteDatabase) getWritableDatabase.invoke(dbHelper);

        } catch (ClassNotFoundException e) {
            fail(studentReadableClassNotFound(e));
        } catch (NoSuchFieldException e) {
            fail(studentReadableNoSuchField(e));
        } catch (IllegalAccessException e) {
            fail(e.getMessage());
        } catch (NoSuchMethodException e) {
            fail(e.getMessage());
        } catch (InstantiationException e) {
            fail(e.getMessage());
        } catch (InvocationTargetException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testIntegerAutoincrement() {

        testInsertSingleRecordIntoWeatherTable();

        ContentValues testWeatherValues = TestUtilities.createTestWeatherContentValues();

        long originalDate = testWeatherValues.getAsLong(REFLECTED_COLUMN_DATE);

        long firstRowId = database.insert(
                REFLECTED_TABLE_NAME,
                null,
                testWeatherValues);

        database.delete(
                REFLECTED_TABLE_NAME,
                "_ID == " + firstRowId,
                null);

        long dayAfterOriginalDate = originalDate + TimeUnit.DAYS.toMillis(1);
        testWeatherValues.put(REFLECTED_COLUMN_DATE, dayAfterOriginalDate);

        long secondRowId = database.insert(
                REFLECTED_TABLE_NAME,
                null,
                testWeatherValues);

        String sequentialInsertsDoNotAutoIncrementId =
                "IDs were reused and shouldn't be if autoincrement is setup properly.";
        assertNotSame(sequentialInsertsDoNotAutoIncrementId,
                firstRowId, secondRowId);
    }

    @Test
    public void testCreateDb() {
        final HashSet<String> tableNameHashSet = new HashSet<>();

        tableNameHashSet.add(REFLECTED_TABLE_NAME);
        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen,
                true,
                database.isOpen());

        Cursor tableNameCursor = database.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table'",
                null);

        String errorInCreatingDatabase =
                "Error: This means that the database has not been created correctly";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        do {
            tableNameHashSet.remove(tableNameCursor.getString(0));
        } while (tableNameCursor.moveToNext());

        assertTrue("Error: Your database was created without the expected tables.",
                tableNameHashSet.isEmpty());

        tableNameCursor.close();
    }

    @Test
    public void testInsertSingleRecordIntoWeatherTable() {

        ContentValues testWeatherValues = TestUtilities.createTestWeatherContentValues();

        long weatherRowId = database.insert(
                REFLECTED_TABLE_NAME,
                null,
                testWeatherValues);

        String insertFailed = "Unable to insert into the database";
        assertTrue(insertFailed, weatherRowId != -1);

        Cursor weatherCursor = database.query(
                REFLECTED_TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                null);

        String emptyQueryError = "Error: No Records returned from weather query";
        assertTrue(emptyQueryError,
                weatherCursor.moveToFirst());

        String expectedWeatherDidntMatchActual =
                "Expected weather values didn't match actual values.";
        TestUtilities.validateCurrentRecord(expectedWeatherDidntMatchActual,
                weatherCursor,
                testWeatherValues);

        assertFalse("Error: More than one record returned from weather query",
                weatherCursor.moveToNext());

        weatherCursor.close();
    }
}