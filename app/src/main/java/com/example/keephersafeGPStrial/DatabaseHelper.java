package com.example.keephersafeGPStrial;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME ="register.db";
    public static final String TABLE_NAME ="registeruser";
    public static final String COL_1 ="ID";
    public static final String COL_2 ="username";
    public static final String COL_3 ="password";
    public static final String COL_4 ="height";
    public static final String COL_5 ="weight";
    public static final String COL_6 ="emg1";
    public static final String COL_7 ="emg2";
    public static final String COL_8 ="age";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE registeruser (ID INTEGER PRIMARY  KEY AUTOINCREMENT, username TEXT, password TEXT, height TEXT, weight TEXT, emg1 TEXT, emg2 TEXT,age TEXT )");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(" DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public long addUser(String user, String password, String height, String weight, String emg1, String emg2, String s){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username",user);
        contentValues.put("password",password);
        contentValues.put("height",height);
        contentValues.put("weight",weight);
        contentValues.put("emg1",emg1);
        contentValues.put("emg2",emg2);
        contentValues.put("age", s);
        long res = db.insert("registeruser",null,contentValues);
//        sqLiteDatabase.execSQL();
        db.close();
        return  res;
    }

    public boolean checkUser(String username, String password){
        String[] columns = { COL_1 };
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?" + " and " + COL_3 + "=?";
        String[] selectionArgs = { username, password };
        Cursor cursor = db.query(TABLE_NAME,columns,selection,selectionArgs,null,null,null);
        int count = cursor.getCount();
        cursor.close();
        db.close();

        if(count>0)
            return  true;
        else
            return  false;
    }

    public String getPassword(String username)
    {
        Log.d("inside getPassword",username);
        String[] columns = {COL_3};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, columns, selection,selectionArgs,null,null,null);
        String ret = "";
        if( cursor != null && cursor.moveToFirst())
        {
            ret = cursor.getString(cursor.getColumnIndex(COL_3));
        }
        return ret;
    }

    public String getHeight(String username)
    {
        String[] columns = {COL_4};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, columns, selection,selectionArgs,null,null,null);
        String ret = "";
        if( cursor != null && cursor.moveToFirst())
        {
            ret = cursor.getString(cursor.getColumnIndex(COL_4));
        }
        return ret;
    }

    public String getWeight(String username)
    {
        String[] columns = {COL_5};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, columns, selection,selectionArgs,null,null,null);
        String ret = "";
        if( cursor != null && cursor.moveToFirst())
        {
            ret = cursor.getString(cursor.getColumnIndex(COL_5));
        }
        return ret;
    }

    public String getAge(String username)
    {
        String[] columns = {COL_8};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, columns, selection,selectionArgs,null,null,null);
        String ret = "";
        if( cursor != null && cursor.moveToFirst())
        {
            ret = cursor.getString(cursor.getColumnIndex(COL_8));
        }
        return ret;
    }

    public String getEmg1(String username)
    {
        String[] columns = {COL_6};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, columns, selection,selectionArgs,null,null,null);
        String ret = "";
        if( cursor != null && cursor.moveToFirst())
        {
            ret = cursor.getString(cursor.getColumnIndex(COL_6));
        }
        return ret;
    }

    public String getEmg2(String username)
    {
        String[] columns = {COL_7};
        SQLiteDatabase db = getReadableDatabase();
        String selection = COL_2 + "=?";
        String[] selectionArgs = {username};
        Cursor cursor = db.query(TABLE_NAME, columns, selection,selectionArgs,null,null,null);
        String ret = "";
        if( cursor != null && cursor.moveToFirst())
        {
            ret = cursor.getString(cursor.getColumnIndex(COL_7));
        }
        return ret;
    }


}

