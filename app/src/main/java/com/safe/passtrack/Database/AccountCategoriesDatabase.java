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

public class AccountCategoriesDatabase extends SQLiteOpenHelper {
    private static final String DB_NAME = "Categories.db";
    private static final String TABLE_NAME = "AccountCategories";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "NAME";

    public AccountCategoriesDatabase(@Nullable Context context) {
        super(context, DB_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT,NAME TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean addCategory(String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME + " where NAME = ?", new String[]{category});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false;
        } else {
            ContentValues values = new ContentValues();
            values.put(COL_2, category);
            long result = db.insert(TABLE_NAME, null, values);
            if (result == -1) {
                return false;
            }
        }
        return true;
    }

    public List<Account> getCategories() {
        SQLiteDatabase db = this.getWritableDatabase();
        ArrayList<Account> categoryNameList = new ArrayList();

        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME, null);
        if (cursor.moveToFirst()) {
            do {
                categoryNameList.add(new Account(cursor.getString(cursor.getColumnIndex("NAME"))));
            } while (cursor.moveToNext());
        }
        return categoryNameList;
    }

    public void deleteCategory(String catName){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME,"NAME = ? ",new String[]{catName});
    }

}
