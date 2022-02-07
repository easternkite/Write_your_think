package com.multimedia.writeyourthink;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONObject;

import java.util.ArrayList;

public class SQLiteManager extends SQLiteOpenHelper {
    private String tableName = "Diary";
    private String tableNameUser = "User";
    public SQLiteManager(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + tableName + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, userName TEXT, title TEXT, contents INTEGER, profile TEXT, date TEXT, time TEXT, address TEXT);");
        db.execSQL("CREATE TABLE " + tableNameUser + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, userUID TEXT, userName TEXT, userProfile TEXT, userEmail TEXT);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public void insert(String userName, String title, String contents, String profile, String date, String time, String address) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO " + tableName + " VALUES(null,'" + userName + "', '" + title + "', '" + contents+ "', '" + profile+ "', '" + date + "', '" + time + "', '" + address + "');";
        db.execSQL(query);
    }

    public void insert2(String userName, String title, String contents, String profile, String date, String time, String address) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO " + tableName + " SELECT null, '" + userName + "', '" + title + "','" + contents + "','" + profile + "','" + date + "','" + time + "','" + address + "'  WHERE NOT EXISTS (SELECT * FROM " +  tableName + " WHERE date = '" + date + "' AND time = '" + time + "')";
        db.execSQL(query);
    }

    public void insertUser(String userUID, String userName, String userProfile, String userEmail) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO " + tableNameUser + " VALUES(null,'" + userUID + "', '" + userName + "', '" + userProfile+ "', '" + userEmail+  "');";
        db.execSQL(query);
    }
    public void insertUser2(String userUID, String userName, String userProfile, String userEmail) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "INSERT INTO " + tableNameUser + " SELECT null, '" + userUID + "', '" + userName + "','" + userProfile + "','" + userEmail + "'  WHERE NOT EXISTS (SELECT * FROM " +  tableNameUser + " WHERE userUID = '" + userUID + "')";
        db.execSQL(query);
    }



    // id 값에 맞는 DB row 업데이트
    public void update(int id,String userName, String title, String contents, String profile, String date, String time, String address) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + tableName +" SET title='" + title + "', userName='" + userName + "', contents='" + contents + "', profile='" + profile + "', date='" + date +"', time='" + time + "', address ='" + address + "' WHERE _id=" + id + ";";
        db.execSQL(query);
    }
    public void updateUser(int id,String userUID, String userName, String userProfile, String userEmail) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "UPDATE " + tableNameUser +" SET userUID='" + userUID + "', userName='" + userName + "', userProfile='" + userProfile + "', userEmail='" + userEmail +"' WHERE id=" + id + ";";
        db.execSQL(query);
    }
    // id에 맞는 DB row 삭제
    public void delete(String id) {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + tableName + " WHERE id='" + id + "';";
        db.delete(tableName,"_id= '" + id + "';", null);
    }

    public void deleteAll() {
        SQLiteDatabase db = getWritableDatabase();
        String query = "DELETE FROM " + tableName + "';";
        db.delete(tableName,"", null);
        db.delete(tableNameUser,"", null);
    }

    // table 내용 전부 삭제
    public void clear(String date){
        SQLiteDatabase db = getWritableDatabase();


        db.delete(tableName,"date= '" + date + "';",null);
    }

    public int getProfilesCount(String date) {
        String countQuery = "SELECT * FROM " + tableName + " WHERE date='" + date + "';";
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }
    /**
     * getResult : table에 있는 모든 내용을 조회해서, jsonObject 단위의 배열을 리턴한다.
     * @return ArrayList<jsonArray>
     */
    public ArrayList<JSONObject> getResult(String date) {
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        try{
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + " WHERE date='" + date + "';", null);
            while (cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",cursor.getString(0));
                jsonObject.put("userName",cursor.getString(1));
                jsonObject.put("title",cursor.getString(2));
                jsonObject.put("contents",cursor.getString(3));
                jsonObject.put("profile",cursor.getString(4));
                jsonObject.put("date",cursor.getString(5));
                jsonObject.put("time",cursor.getString(6));
                jsonObject.put("address",cursor.getString(7));
                array.add(jsonObject);
            }
        }
        catch (Exception e){
            Log.i("seo","error : " + e);
        }

        return array;
    }

    public ArrayList<JSONObject> getResult2() {
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableName + "';'", null);
            while (cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", cursor.getString(0));
                jsonObject.put("userName", cursor.getString(1));
                jsonObject.put("title", cursor.getString(2));
                jsonObject.put("contents", cursor.getString(3));
                jsonObject.put("profile", cursor.getString(4));
                jsonObject.put("date", cursor.getString(5));
                jsonObject.put("time", cursor.getString(6));
                jsonObject.put("address", cursor.getString(7));
                array.add(jsonObject);
            }
        }
        catch (Exception e){
            Log.i("seo","error : " + e);
        }

        return array;
    }

    public ArrayList<JSONObject> getResultUser() {
        ArrayList<JSONObject> array = new ArrayList<JSONObject>();
        try {
            SQLiteDatabase db = getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM " + tableNameUser + "';'", null);
            while (cursor.moveToNext()) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", cursor.getString(0));
                jsonObject.put("userUID", cursor.getString(1));
                jsonObject.put("userName", cursor.getString(2));
                jsonObject.put("userProfile", cursor.getString(3));
                jsonObject.put("userEmail", cursor.getString(4));
                array.add(jsonObject);
            }
        }
        catch (Exception e){
            Log.i("seo","error : " + e);
        }

        return array;
    }


    public void setResult(String userName, String title, String contents, String profile, String date, String time, String address) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + tableName  + "';'", null);
        if (cursor.moveToFirst()){

        }else{
            insert(userName, title, contents, profile, date, time, address);
        }
    }


    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.disableWriteAheadLogging();
    }
}