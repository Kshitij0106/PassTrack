package com.safe.passtrack.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.safe.passtrack.Model.Account;

import java.util.ArrayList;
import java.util.List;

public class AccountNamesDatabase extends SQLiteOpenHelper {

    private static final String DB_NAME = "Account.db";
    private static final String TABLE_NAME = "AccountNames";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";
    private static final String COL_3 = "CATEGORY";

    public AccountNamesDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT,CATEGORY TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<Account> getList(String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Account> accountList = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where CATEGORY = ?", new String[]{category});
        if (cursor.moveToFirst()) {
            do {
                accountList.add(new Account(cursor.getString(cursor.getColumnIndex("NAME")),
                        cursor.getString(cursor.getColumnIndex("CATEGORY"))));
            } while (cursor.moveToNext());
        }
        return accountList;
    }

    public boolean addToList(String name, String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("select * from " + TABLE_NAME + " where NAME = ?", new String[]{name});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        } else {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_2, name);
            contentValues.put(COL_3, category);

            long result = db.insert(TABLE_NAME, null, contentValues);

            if (result == -1) {
                return false;
            }
        }
        return true;
    }

    public void removeFromList(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "NAME = ?", new String[]{name});
    }

    public boolean checkCategory(String catName){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from "+ TABLE_NAME + " where CATEGORY = ?",new String[]{catName});
        if(cursor.getCount() > 0){
            return true;
        }
        return false;
    }

}
