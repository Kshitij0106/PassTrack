package com.safe.passtrack.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.safe.passtrack.Model.Text_Details;

import java.util.ArrayList;
import java.util.List;

public class Account_TextDetailsDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "Details.db";
    private static final String TABLE_NAME = "AccountDetails";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "ACCOUNTNAME";
    private static final String COL_3 = "USERNAME";
    private static final String COL_4 = "PASSWORD";

    public Account_TextDetailsDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,ACCOUNTNAME TEXT,USERNAME TEXT,PASSWORD TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addDetails(String type, String userName, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_2, type);
        values.put(COL_3, userName);
        values.put(COL_4, password);

        long result = db.insert(TABLE_NAME, null, values);
        if (result == -1) {
            return false;
        }

        return true;
    }

    public List<Text_Details> showDetails(String accName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Text_Details> detailsList = new ArrayList<>();

        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where ACCOUNTNAME = ? ", new String[]{accName});
        if (cursor.moveToFirst()) {
            do {
                detailsList.add(new Text_Details(cursor.getString(cursor.getColumnIndex("USERNAME")),
                        cursor.getString(cursor.getColumnIndex("PASSWORD")),
                        cursor.getString(cursor.getColumnIndex("ACCOUNTNAME"))));
            } while (cursor.moveToNext());
        }
        return detailsList;
    }

    public String getPass(String type, String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        String encText = "";

        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where USERNAME = ? and ACCOUNTNAME = ? ", new String[]{name,type});
        if (cursor.moveToFirst()) {
            do {
                encText = cursor.getString(cursor.getColumnIndex("PASSWORD"));
            } while (cursor.moveToNext());
        }
        return encText;
    }

    public boolean removeAccount(String accName, String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "ACCOUNTNAME = ? and USERNAME = ?", new String[]{accName, username});
        return true;
    }

    public boolean checkDetails(String accName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where ACCOUNTNAME = ?",new String[]{accName});

        if(cursor.getCount() > 0){
            return true;
        }
        return false;
    }

}
