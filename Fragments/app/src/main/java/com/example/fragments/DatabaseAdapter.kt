package com.example.fragments

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import java.sql.SQLException

/**
 * DatabaseAdapter class
 * This class is used to create, update, delete and query the database
 * @param dbContext The context of the database
 */

class DatabaseAdapter(private val dbContext: Context) {

    private var dbHelper: DatabaseHelper = DatabaseHelper(dbContext)
    private var database: SQLiteDatabase? = null

    /**
     * Companion object
     * This object is used to store constants
     */
    companion object {
        const val DATABASE_NAME = "DiaryEntries.db"
        const val DATABASE_VERSION = 4
        const val DATABASE_TABLE = "diary"
        const val COLUMN_ID = "_id"
        const val COLUMN_DATE = "date"
        const val COLUMN_TITLE = "title"
        const val COLUMN_MEDIA = "media"
        const val COLUMN_ENTRY = "entry"
    }

    /**
     * open method
     * This method is used to open the database
     * @return DatabaseAdapter
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun open(): DatabaseAdapter {
        dbHelper = DatabaseHelper(dbContext)
        database = dbHelper.writableDatabase
        return this
    }

    /**
     * close method
     * This method is used to close the database
     */
    fun close() {
        dbHelper.close()
    }

    /**
     * insertDiaryEntry method
     * This method is used to insert a new diary entry into the database
     * @param diaryEntry
     * @return Long
     */
    fun insertDiaryEntry(diaryEntry: DiaryEntry): Long {
        val values = ContentValues().apply {
            put(COLUMN_DATE, diaryEntry.date)
            put(COLUMN_TITLE, diaryEntry.title)
            put(COLUMN_MEDIA, diaryEntry.media)
            put(COLUMN_ENTRY, diaryEntry.entry)
        }
        return database?.insert(DATABASE_TABLE, null, values) ?: -1
    }

    /**
     * getAllDiaryEntries method
     * This method is used to get all diary entries from the database
     * @return Cursor
     */
    fun getAllDiaryEntries(): Cursor? {
        return database?.query(
            DATABASE_TABLE,
            arrayOf(COLUMN_ID, COLUMN_DATE, COLUMN_TITLE, COLUMN_MEDIA, COLUMN_ENTRY),
            null,
            null,
            null,
            null,
            null,
            null
        )
    }

    /**
     * getAllDiaryEntriesForDate method
     * This method is used to get all diary entries for a specific date from the database
     * @param date
     * @return Cursor
     * @throws SQLException
     */
    @Throws(SQLException::class)
    fun getAllDiaryEntriesForDate(date: String): Cursor? {
        val query = "SELECT * FROM $DATABASE_TABLE WHERE $COLUMN_DATE = ?"
        return database?.rawQuery(query, arrayOf(date))
    }

    /**
     * updateDiaryEntry method
     * This method is used to update a specific diary entry in the database
     * @param id
     * @param date
     * @param title
     * @param media
     * @param entry
     * @return Int
     */
    fun updateDiaryEntry(id: Int, date: String, title: String?, media: String?, entry: String): Int {
        val values = ContentValues().apply {
            put(COLUMN_DATE, date)
            put(COLUMN_TITLE, title)
            put(COLUMN_MEDIA, media)
            put(COLUMN_ENTRY, entry)
        }
        return database?.update(
            DATABASE_TABLE,
            values,
            "$COLUMN_ID=?",
            arrayOf(id.toString())
        ) ?: -1
    }

    /**
     * deleteDiaryEntry method
     * This method is used to delete a specific diary entry from the database
     * @param id
     * @return Int
     */
    fun deleteDiaryEntry(id: String?): Int {
        return database?.delete(
            DATABASE_TABLE,
            "$COLUMN_ID=?",
            arrayOf(id.toString())
        ) ?: -1
    }

    /**
     * deleteAllDiaryEntries method
     * This method is used to delete all diary entries from the database
     * @return Int
     */
    fun deleteAllDiaryEntries(): Int {
        return database?.delete(
            DATABASE_TABLE,
            null,
            null
        ) ?: -1
    }


}