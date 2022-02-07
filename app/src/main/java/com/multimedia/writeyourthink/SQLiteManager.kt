package com.multimedia.writeyourthink

import android.content.Context
import android.database.sqlite.SQLiteDatabase.CursorFactory
import android.database.sqlite.SQLiteOpenHelper
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import org.json.JSONObject
import java.lang.Exception
import java.util.ArrayList

class SQLiteManager(context: Context?, name: String?, factory: CursorFactory?, version: Int) :
    SQLiteOpenHelper(context, name, factory, version) {
    private val tableName = "Diary"
    private val tableNameUser = "User"
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE $tableName (_id INTEGER PRIMARY KEY AUTOINCREMENT, userName TEXT, title TEXT, contents INTEGER, profile TEXT, date TEXT, time TEXT, address TEXT);")
        db.execSQL("CREATE TABLE $tableNameUser (_id INTEGER PRIMARY KEY AUTOINCREMENT, userUID TEXT, userName TEXT, userProfile TEXT, userEmail TEXT);")
    }

    override fun onUpgrade(sqLiteDatabase: SQLiteDatabase, i: Int, i1: Int) {}
    fun insert(
        userName: String?,
        title: String?,
        contents: String?,
        profile: String?,
        date: String?,
        time: String?,
        address: String?
    ) {
        val db = writableDatabase
        val query =
            "INSERT INTO $tableName VALUES(null,'$userName', '$title', '$contents', '$profile', '$date', '$time', '$address');"
        db.execSQL(query)
    }

    fun insert2(
        userName: String?,
        title: String?,
        contents: String?,
        profile: String?,
        date: String?,
        time: String?,
        address: String?
    ) {
        val db = writableDatabase
        val query =
            "INSERT INTO $tableName SELECT null, '$userName', '$title','$contents','$profile','$date','$time','$address'  WHERE NOT EXISTS (SELECT * FROM $tableName WHERE date = '$date' AND time = '$time')"
        db.execSQL(query)
    }


    fun insertUser2(userUID: String?, userName: String?, userProfile: String?, userEmail: String?) {
        val db = writableDatabase
        val query =
            "INSERT INTO $tableNameUser SELECT null, '$userUID', '$userName','$userProfile','$userEmail'  WHERE NOT EXISTS (SELECT * FROM $tableNameUser WHERE userUID = '$userUID')"
        db.execSQL(query)
    }

    // id 값에 맞는 DB row 업데이트
    fun update(
        id: Int,
        userName: String,
        title: String,
        contents: String,
        profile: String?,
        date: String?,
        time: String?,
        address: String?
    ) {
        val db = writableDatabase
        val query =
            "UPDATE $tableName SET title='$title', userName='$userName', contents='$contents', profile='$profile', date='$date', time='$time', address ='$address' WHERE _id=$id;"
        db.execSQL(query)
    }


    // id에 맞는 DB row 삭제
    fun delete(id: String) {
        val db = writableDatabase
        val query = "DELETE FROM $tableName WHERE id='$id';"
        db.delete(tableName, "_id= '$id';", null)
    }

    fun deleteAll() {
        val db = writableDatabase
        val query = "DELETE FROM $tableName';"
        db.delete(tableName, "", null)
        db.delete(tableNameUser, "", null)
    }

    // table 내용 전부 삭제
    fun clear(date: String) {
        val db = writableDatabase
        db.delete(tableName, "date= '$date';", null)
    }

    fun getProfilesCount(date: String): Int {
        val countQuery = "SELECT * FROM $tableName WHERE date='$date';"
        val db = this.readableDatabase
        val cursor = db.rawQuery(countQuery, null)
        val count = cursor.count
        cursor.close()
        return count
    }

    /**
     * getResult : table에 있는 모든 내용을 조회해서, jsonObject 단위의 배열을 리턴한다.
     * @return ArrayList<jsonArray>
    </jsonArray> */
    fun getResult(date: String): ArrayList<JSONObject> {
        val array = ArrayList<JSONObject>()
        try {
            val db = readableDatabase
            val cursor = db.rawQuery("SELECT * FROM $tableName WHERE date='$date';", null)
            while (cursor.moveToNext()) {
                val jsonObject = JSONObject()
                jsonObject.put("id", cursor.getString(0))
                jsonObject.put("userName", cursor.getString(1))
                jsonObject.put("title", cursor.getString(2))
                jsonObject.put("contents", cursor.getString(3))
                jsonObject.put("profile", cursor.getString(4))
                jsonObject.put("date", cursor.getString(5))
                jsonObject.put("time", cursor.getString(6))
                jsonObject.put("address", cursor.getString(7))
                array.add(jsonObject)
            }
        } catch (e: Exception) {
            Log.i("seo", "error : $e")
        }
        return array
    }

    val result2: ArrayList<JSONObject>
        get() {
            val array = ArrayList<JSONObject>()
            try {
                val db = readableDatabase
                val cursor = db.rawQuery("SELECT * FROM $tableName';'", null)
                while (cursor.moveToNext()) {
                    val jsonObject = JSONObject()
                    jsonObject.put("id", cursor.getString(0))
                    jsonObject.put("userName", cursor.getString(1))
                    jsonObject.put("title", cursor.getString(2))
                    jsonObject.put("contents", cursor.getString(3))
                    jsonObject.put("profile", cursor.getString(4))
                    jsonObject.put("date", cursor.getString(5))
                    jsonObject.put("time", cursor.getString(6))
                    jsonObject.put("address", cursor.getString(7))
                    array.add(jsonObject)
                }
            } catch (e: Exception) {
                Log.i("seo", "error : $e")
            }
            return array
        }
    val resultUser: ArrayList<JSONObject>
        get() {
            val array = ArrayList<JSONObject>()
            try {
                val db = readableDatabase
                val cursor = db.rawQuery("SELECT * FROM $tableNameUser';'", null)
                while (cursor.moveToNext()) {
                    val jsonObject = JSONObject()
                    jsonObject.put("id", cursor.getString(0))
                    jsonObject.put("userUID", cursor.getString(1))
                    jsonObject.put("userName", cursor.getString(2))
                    jsonObject.put("userProfile", cursor.getString(3))
                    jsonObject.put("userEmail", cursor.getString(4))
                    array.add(jsonObject)
                }
            } catch (e: Exception) {
                Log.i("seo", "error : $e")
            }
            return array
        }

    fun setResult(
        userName: String,
        title: String,
        contents: String,
        profile: String,
        date: String,
        time: String,
        address: String
    ) {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $tableName';'", null)
        if (cursor.moveToFirst()) {
        } else {
            insert(userName, title, contents, profile, date, time, address)
        }
    }

    override fun onConfigure(db: SQLiteDatabase) {
        super.onConfigure(db)
        db.disableWriteAheadLogging()
    }
}