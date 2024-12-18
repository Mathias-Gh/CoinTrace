package com.example.cointrace

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cointrace_bdd"
        private const val DATABASE_VERSION = 1

        // Tables and columns
        private const val TABLE_USER = "user_data"
        private const val TABLE_SIMULATIONS = "simulations"
        private const val COLUMN_ID = "id"

        // User table columns
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_PSEUDO = "pseudo"
        private const val COLUMN_NOTES = "notes"

        // Simulation table columns
        private const val COLUMN_CRYPTO_NAME = "crypto_name"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_RESULT = "result"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables with a single method
        val createTables = listOf(
            "CREATE TABLE $TABLE_USER ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_EMAIL TEXT, $COLUMN_PASSWORD TEXT, $COLUMN_PSEUDO TEXT, $COLUMN_NOTES TEXT)",
            "CREATE TABLE $TABLE_SIMULATIONS ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_CRYPTO_NAME TEXT, $COLUMN_DATE TEXT, $COLUMN_AMOUNT REAL, $COLUMN_RESULT REAL)"
        )
       createTables.forEach { db.execSQL(it) }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop existing tables and recreate them when upgrading the database version
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_SIMULATIONS")
        onCreate(db)
    }

    //check user
    fun checkUser(email: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $COLUMN_PASSWORD = ?",
            arrayOf(password)
        )
        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    // Generic method to insert data into a table
    private fun insertData(table: String, values: ContentValues): Long {
        val db = writableDatabase
        return db.insert(table, null, values)
    }

    // Insert a user into the user table
    fun insertUser(email: String, password: String, pseudo: String, notes: String): Long {
        return insertData(TABLE_USER, ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_PSEUDO, pseudo)
            put(COLUMN_NOTES, notes)
        })
    }


    // Insert a simulation into the simulation table
    fun insertSimulation(cryptoName: String, date: String, amount: Double, result: Double): Long {
        return insertData(TABLE_SIMULATIONS, ContentValues().apply {
            put(COLUMN_CRYPTO_NAME, cryptoName)
            put(COLUMN_DATE, date)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_RESULT, result)
        })
    }

    // Generic method to retrieve all data from a table
    private fun getAllData(table: String): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $table", null)
    }

    // TEST BDD
    fun insertUser(email: String, password: String, pseudo: String): Long {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("email", email)
            put("password", password)
            put("pseudo", pseudo)
        }


        return db.insert("user_data", null, values)
    }
    ////////////////////

    // Retrieve all users from the user table
    fun getAllUsers(): Cursor = getAllData(TABLE_USER)

    // Retrieve all simulations from the simulation table
    fun getAllSimulations(): Cursor = getAllData(TABLE_SIMULATIONS)
}
