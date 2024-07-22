package com.example.fragments

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.fragments.DatabaseAdapter.Companion.DATABASE_NAME
import com.example.fragments.DatabaseAdapter.Companion.DATABASE_TABLE
import com.example.fragments.DatabaseAdapter.Companion.DATABASE_VERSION

/**
 * DatabaseHelper class to create the database and upgrade the database
 * @param context The context of the database
 */
internal class DatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {   // create database

    /**
     * onCreate method to create the database
     */
        override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "create table " + DATABASE_TABLE + "(" +
                    "_id integer primary key autoincrement," +
                    "date text not null," +
                     "title text," +
                     "media text," +
                    "entry text not null);"
        )
    }

    /**
     * onUpgrade method to upgrade the database
     * @param db The database
     * @param oldVersion The old version of the database
     * @param newVersion The new version of the database
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $DATABASE_TABLE")
        onCreate(db)
    }

    /**
     * onOpen method to open the database
     * @param db The database
     */
    override fun onOpen(db: SQLiteDatabase?) {
        super.onOpen(db)
    }
}