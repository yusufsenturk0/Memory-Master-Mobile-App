package com.example.memortymaster

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DataBase(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    companion object {
        private const val DATABASE_NAME = "memory_master.db"
        private const val DATABASE_VERSION = 1
        private const val TABLE_NAME = "LongValueTable"
        private const val COLUMN_ID = "id"
        private const val COLUMN_VALUE = "long_value"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = "CREATE TABLE $TABLE_NAME (" +
                "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "$COLUMN_VALUE LONG)"
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    // Veri ekleme metodu
    fun insertLongValue(value: Long): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_VALUE, value)
        }
        return db.insert(TABLE_NAME, null, contentValues)
    }

    // Veriyi güncelleme metodu
    fun updateLongValue(id: Long, value: Long) {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_VALUE, value)
        }
        db.update(TABLE_NAME, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    // Veriyi okuma metodu
    fun getLongValue(id: Long): Long? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME, arrayOf(COLUMN_VALUE), "$COLUMN_ID = ?",
            arrayOf(id.toString()), null, null, null
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getLong(it.getColumnIndexOrThrow(COLUMN_VALUE))
            }
        }
        return null
    }

    // Veritabanında mevcut olan tek kayıt
    fun getFirstRecordId(): Long? {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_NAME, arrayOf(COLUMN_ID),
            null, null, null, null, null, "1"
        )
        cursor.use {
            if (it.moveToFirst()) {
                return it.getLong(it.getColumnIndexOrThrow(COLUMN_ID))
            }
        }
        return null
    }

    // Veritabanında herhangi bir skor var mı kontrolü
    fun isScorePresent(): Boolean {
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT COUNT(*) FROM $TABLE_NAME", null)
        cursor.use {
            if (it.moveToFirst()) {
                return it.getInt(0) > 0
            }
        }
        return false
    }

}