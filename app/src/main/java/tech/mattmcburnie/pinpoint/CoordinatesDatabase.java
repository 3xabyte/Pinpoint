package tech.mattmcburnie.pinpoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class CoordinatesDatabase extends SQLiteOpenHelper {

    // CONSTANTS
    private static final String CLASS_NAME = "CoordinatesDatabase";

    // Database initialization
    private static final int DB_VERSION = 3;
    private static final String DB_NAME = "Coordinates.db";

    // Columns
    public static final String ID = "ID";                   // ID for the coordinate
    public static final String POINT = "Point";             // ID for the Point - Will be useful for averaging
    public static final String LATITUDE = "Latitude";       // Latitude
    public static final String LONGITUDE = "Longitude";     // Longitude
    public static final String POINT_TYPE = "Type";         // Point Type (point, 3-way, 4-way, other)
    public static final String LANDMARK = "Landmark";       // Landmark
    public static final String DATE = "RecordDate";

    // Table Name
    public static final String TABLE_NAME = "Points";

    // CONSTRUCTORS
    public CoordinatesDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // OVERRIDDEN METHODS
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create table with the following columns: ID, POINT, DATE, LATITUDE, LONGITUDE, POINT_TYPE, LANDMARK
        final String SQL = "CREATE TABLE " + TABLE_NAME + "(" +
                ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                POINT + " INTEGER," +
                DATE + " TINYTEXT," +
                LATITUDE + " DOUBLE," +
                LONGITUDE + " DOUBLE," +
                POINT_TYPE + " TINYTEXT," +
                LANDMARK + " TINYTEXT" +
                ")";

        db.execSQL(SQL);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(CLASS_NAME, "Upgrading the database from version " + oldVersion + " to version " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i(CLASS_NAME, "Downgrading the database from version " + oldVersion + " to version " + newVersion);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);
    }

    // METHODS
    /**
     * Adds GPS coordinates to the database. There may be multiple coordinates with the same point, which
     * will be used later on for calculating averages and outliers.
     * @param point The ID of the user's current location.
     * @param latitude Latitude of the device
     * @param longitude Longitude of the device
     */
    public void addNewCoordinates(long point, double latitude, double longitude, String pointType, String landmark) {

        ContentValues cv = new ContentValues();
        SQLiteDatabase db = this.getWritableDatabase();

        Calendar dayAndTime = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss aa", Locale.US);

        cv.put(POINT, point);
        cv.put(LATITUDE, latitude);
        cv.put(LONGITUDE, longitude);
        cv.put(DATE, df.format(dayAndTime.getTime()));
        cv.put(POINT_TYPE, pointType);
        cv.put(LANDMARK, landmark);

        db.insert(TABLE_NAME, null, cv);

        db.close();
    }


    public double[] getAvgCoordsAtPoint(long point) {
        SQLiteDatabase db = this.getReadableDatabase();

        String SQL = "SELECT " + LATITUDE + ", " + LONGITUDE +
                " FROM " + TABLE_NAME +
                " WHERE " + POINT + " = " + point;

        Cursor cursor = db.rawQuery(SQL, null);

        double latitude = 0;
        double longitude = 0;
        int count = cursor.getCount();

        cursor.moveToFirst();

        for(int i = 0; i < count; i++) {
            latitude += cursor.getDouble(0);
            longitude += cursor.getDouble(1);
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return new double[] {latitude / count, longitude / count};
    }

    public ArrayList<Coordinate> getCoordinatesAtPoint(long point) {
        ArrayList<Coordinate> coordinates = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        String SQL = "SELECT " + ID + ", " + LATITUDE + ", " + LONGITUDE + ", " + POINT_TYPE + ", " + LANDMARK +
                " FROM " + TABLE_NAME +
                " WHERE " + POINT + " = " + point;

        Cursor cursor = db.rawQuery(SQL, null);
        int count = cursor.getCount();

        cursor.moveToFirst();

        for(int i = 0; i < count; i++) {
            coordinates.add(new Coordinate(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4)));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return coordinates;
    }

    public String getDateAtPoint(long point) {
        SQLiteDatabase db = this.getReadableDatabase();

        String SQL = "SELECT " + DATE +
                " FROM " + TABLE_NAME +
                " WHERE " + POINT + " = " + point;

        Cursor cursor = db.rawQuery(SQL, null);

        cursor.moveToFirst();

        String date = cursor.getString(0);

        cursor.close();
        db.close();

        return date;
    }


    public long getCurrentPoint() {
        SQLiteDatabase db = this.getReadableDatabase();

        String SQL = "SELECT MAX(" + POINT + ")" +
                "FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(SQL, null);
        cursor.moveToFirst();

        long currentPoint = cursor.getLong(0) + 1;

        cursor.close();
        db.close();

        return currentPoint;
    }

    public long getStartingPoint() {
        SQLiteDatabase db = this.getReadableDatabase();

        String SQL = "SELECT MIN(" + POINT + ")" +
                " FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(SQL, null);
        cursor.moveToFirst();

        long currentPoint = cursor.getLong(0);

        cursor.close();
        db.close();

        return currentPoint;
    }

    public ArrayList<Long> getPoints() {

        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT DISTINCT " + POINT +
               " FROM " + TABLE_NAME;

        Cursor cursor = db.rawQuery(SQL, null);
        cursor.moveToFirst();
        int size = cursor.getCount();
        ArrayList<Long> output = new ArrayList<>();

        for(int i = 0; i < size; i++) {
            output.add(cursor.getLong(0));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return output;
    }

    public ArrayList<Coordinate> getAllPoints() {
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT " + ID + ", " + LATITUDE + ", " + LONGITUDE + ", " + POINT_TYPE + ", " + LANDMARK +
                " FROM " + TABLE_NAME;

        ArrayList<Coordinate> coordinates = new ArrayList<>();

        Cursor cursor = db.rawQuery(SQL, null);
        cursor.moveToFirst();
        int size = cursor.getCount();

        for (int i = 0; i < size; i++) {
            coordinates.add(new Coordinate(cursor.getInt(0), cursor.getDouble(1), cursor.getDouble(2), cursor.getString(3), cursor.getString(4)));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return coordinates;
    }

    public ArrayList<String> getPointType() {
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT " + POINT_TYPE +
                " FROM " + TABLE_NAME;

        ArrayList<String> pointTypes = new ArrayList<>();

        Cursor cursor = db.rawQuery(SQL, null);
        cursor.moveToFirst();
        int size = cursor.getCount();
        for(int i = 0; i < size; i++) {
            pointTypes.add(cursor.getString(3));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return pointTypes;
    }

    public ArrayList<String> getLandmark() {
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT " + LANDMARK +
                " FROM " + TABLE_NAME;

        ArrayList<String> landmarks = new ArrayList<>();

        Cursor cursor = db.rawQuery(SQL, null);
        cursor.moveToFirst();
        int size = cursor.getCount();
        for(int i = 0; i < size; i++) {
            landmarks.add(cursor.getString(4));
            cursor.moveToNext();
        }

        cursor.close();
        db.close();

        return landmarks;
    }

    public String getPointTypeAtPoint(long point) {
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT " + POINT_TYPE +
                " FROM " + TABLE_NAME +
                " WHERE " + POINT + " = " + point;



        Cursor cursor = db.rawQuery(SQL, null);
        cursor.moveToFirst();

        String type = cursor.getString(0);

        cursor.close();
        db.close();

        return type;
    }

    public String getLandmarkAtPoint(long point) {
        SQLiteDatabase db = this.getReadableDatabase();
        String SQL = "SELECT " + LANDMARK +
                " FROM " + TABLE_NAME +
                " WHERE " + POINT + " = " + point;


        Cursor cursor = db.rawQuery(SQL, null);
        cursor.moveToFirst();
        String landmark = cursor.getString(0);

        cursor.close();
        db.close();

        return landmark;
    }



    public void deleteTable() {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        this.onCreate(db);

        db.close();

    }

    public void deletePointID(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + ID + " = " + id);

        db.close();

    }

    public void deletePoint(long point) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL("DELETE FROM " + TABLE_NAME + " WHERE " + POINT + " = " + point);

        db.close();
    }



}
