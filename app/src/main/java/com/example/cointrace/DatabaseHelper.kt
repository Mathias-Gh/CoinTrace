package com.example.cointrace

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import com.example.cointrace.models.Trade
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        private const val DATABASE_NAME = "cointrace_bdd"
        private const val DATABASE_VERSION = 4

        // Tables and columns
        private const val TABLE_USER = "user_data"
        private const val TABLE_WALLET = "wallet"
        private const val TABLE_NOTES = "notes"
        private const val TABLE_TRADER = "trader" // New table
        private const val COLUMN_ID = "id"
        private const val COLUMN_BALANCE = "balance"
        private const val COLUMN_USER_ID = "user_id"

        data class User(
            val id: Long,
            val email: String,
            val password: String,
            val pseudo: String
        )


        // User table columns
        private const val COLUMN_EMAIL = "email"
        private const val COLUMN_PASSWORD = "password"
        private const val COLUMN_PSEUDO = "pseudo"

        // Simulation table columns
        private const val COLUMN_CRYPTO_NAME = "crypto_name"
        private const val COLUMN_DATE = "date"
        private const val COLUMN_AMOUNT = "amount"
        private const val COLUMN_RESULT = "result"

        // Notes table columns
        private const val COLUMN_NOTE = "note"
    }

    override fun onCreate(db: SQLiteDatabase) {
        // Create tables with a single method
        val createTables = listOf(
            "CREATE TABLE $TABLE_USER ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_EMAIL TEXT, $COLUMN_PASSWORD TEXT, $COLUMN_PSEUDO TEXT)",
            "CREATE TABLE $TABLE_WALLET ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USER_ID INTEGER, $COLUMN_BALANCE REAL NOT NULL, FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USER($COLUMN_ID))",
            "CREATE TABLE $TABLE_NOTES ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USER_ID INTEGER, $COLUMN_NOTE TEXT, FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USER($COLUMN_ID))",
            "CREATE TABLE $TABLE_TRADER ($COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, $COLUMN_USER_ID INTEGER, $COLUMN_CRYPTO_NAME TEXT, $COLUMN_AMOUNT REAL, $COLUMN_DATE TEXT, FOREIGN KEY($COLUMN_USER_ID) REFERENCES $TABLE_USER($COLUMN_ID))",
        )
        createTables.forEach { db.execSQL(it) }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Drop existing tables and recreate them when upgrading the database version
        db.execSQL("DROP TABLE IF EXISTS $TABLE_USER")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_WALLET")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NOTES")
        db.execSQL("DROP TABLE IF EXISTS $TABLE_TRADER")
        onCreate(db)
    }
    fun insertTrade(userId: Long, cryptoName: String, amount: Double, date: String): Long {
        return insertData(TABLE_TRADER, ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_CRYPTO_NAME, cryptoName)
            put(COLUMN_AMOUNT, amount)
            put(COLUMN_DATE, date)
        })
    }
    fun getTradesByUser(userId: Long): List<Trade> {
        val trades = mutableListOf<Trade>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM trader WHERE user_id = ?", arrayOf(userId.toString()))

        if (cursor.moveToFirst()) {
            do {
                val cryptoName = cursor.getString(cursor.getColumnIndexOrThrow("crypto_name"))
                val amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount"))
                val date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                trades.add(Trade(cryptoName, amount, date))
            } while (cursor.moveToNext())
        }
        cursor.close()
        return trades
    }

    fun deleteTrade(userId: Long, trade: Trade): Int {
        val db = writableDatabase
        return db.delete(
            "trader",
            "user_id = ? AND crypto_name = ? AND amount = ? AND date = ?",
            arrayOf(userId.toString(), trade.cryptoName, trade.amount.toString(), trade.date)
        )
    }

    // User methods
    fun insertUser(email: String, password: String, pseudo: String, notes: String): Long {
        return insertData(TABLE_USER, ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_PSEUDO, pseudo)
        })
    }

    // Notes methods
    fun insertNote(userId: Long, note: String): Long {
        return insertData(TABLE_NOTES, ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_NOTE, note)
        })
    }

    // Méthode pour mettre à jour une note par son ID
    fun updateNote(noteId: Long, newNote: String): Int {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_NOTE, newNote)
        }
        return db.update(TABLE_NOTES, contentValues, "$COLUMN_ID = ?", arrayOf(noteId.toString()))
    }

    // Méthode pour supprimer une note par son ID
    fun deleteNoteById(noteId: Long): Int {
        val db = writableDatabase
        return db.delete(TABLE_NOTES, "$COLUMN_ID = ?", arrayOf(noteId.toString()))
    }

    fun getNotesByUser(userId: Long): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_NOTES WHERE $COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }

    fun checkUser(pseudo: String, password: String): Boolean {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT * FROM $TABLE_USER WHERE $COLUMN_PASSWORD = ? AND $COLUMN_PSEUDO = ?",
            arrayOf(password, pseudo)
        )
        val userExists = cursor.count > 0
        cursor.close()
        return userExists
    }

    fun getAllUsers(): Cursor = getAllData(TABLE_USER)

    // Dans DatabaseHelper.kt

    fun getUserId(pseudo: String, password: String): Long {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT $COLUMN_ID FROM $TABLE_USER WHERE $COLUMN_PSEUDO = ? AND $COLUMN_PASSWORD = ?",
            arrayOf(pseudo, password)
        )
        return if (cursor.moveToFirst()) {
            val userId = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID))
            cursor.close()
            userId
        } else {
            cursor.close()
            -1
        }
    }

    // Mettre à jour les méthodes liées au portefeuille pour inclure user_id
    fun insertWallet(userId: Long, balance: Double): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_BALANCE, balance)
        }
        return db.insert(TABLE_WALLET, null, contentValues)
    }

    fun updateWallet(id: Long, userId: Long, balance: Double): Int {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_USER_ID, userId)
            put(COLUMN_BALANCE, balance)
        }
        return db.update(TABLE_WALLET, contentValues, "$COLUMN_ID = ?", arrayOf(id.toString()))
    }

    fun updateWalletBalance(userId: Long, newBalance: Double): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put("balance", newBalance)
        }
        val rowsAffected = db.update("wallet", contentValues, "user_id = ?", arrayOf(userId.toString()))
        return rowsAffected > 0
    }

    fun getWalletByUser(userId: Long): Cursor {
        val db = this.readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_WALLET WHERE $COLUMN_USER_ID = ?", arrayOf(userId.toString()))
    }

    // Generic methods
    private fun insertData(table: String, values: ContentValues): Long {
        val db = writableDatabase
        return db.insert(table, null, values)
    }

    private fun getAllData(table: String): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $table", null)
    }

    fun getUserData(userId: Long): Cursor {
        val db = readableDatabase
        return db.rawQuery("SELECT * FROM $TABLE_USER WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
    }

    fun updateUser(userId: Long, email: String, password: String, pseudo: String): Boolean {
        val db = writableDatabase
        val contentValues = ContentValues().apply {
            put(COLUMN_EMAIL, email)
            put(COLUMN_PASSWORD, password)
            put(COLUMN_PSEUDO, pseudo)
        }

        val result = db.update(TABLE_USER, contentValues, "$COLUMN_ID = ?", arrayOf(userId.toString()))
        db.close()
        return result > 0
    }

    fun getUserById(userId: Long): User? {
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_USER WHERE $COLUMN_ID = ?", arrayOf(userId.toString()))
        var user: User? = null

        if (cursor.moveToFirst()) {
            val email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL))
            val password = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PASSWORD))
            val pseudo = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_PSEUDO))
            user = User(userId, email, password, pseudo)
        }
        cursor.close()
        return user
    }

}