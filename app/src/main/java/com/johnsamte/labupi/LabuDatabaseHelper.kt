package com.johnsamte.labupi

import android.content.ContentValues
import android.content.Context
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

class LabuDatabaseHelper(private val context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "labupi.db"
        const val DATABASE_VERSION = 1

        const val TABLE_BNL = "BNL"
        const val TABLE_PNL = "PNL"
        const val TABLE_RECENT = "Recent"
        const val COLUMN_TIMESTAMP = "timestamp"

        private const val TAG = "LabuDatabaseHelper"
    }

    private val dbPath: String = context.getDatabasePath(DATABASE_NAME).absolutePath

    override fun onCreate(db: SQLiteDatabase?) {
        // No need to create tables here since we’re using a preloaded database.
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            Log.i(TAG, "Database upgrade detected: $oldVersion → $newVersion")
            try {
                File(dbPath).delete()
                copyDatabase()
            } catch (e: Exception) {
                Log.e(TAG, "Failed to upgrade database", e)
            }
        }
    }

    /**
     * Copies the database from assets to the app’s database directory.
     */
    private fun copyDatabase() {
        try {
            context.assets.open(DATABASE_NAME).use { inputStream ->
                FileOutputStream(dbPath).use { outputStream ->
                    val buffer = ByteArray(1024)
                    var length: Int
                    while (inputStream.read(buffer).also { length = it } > 0) {
                        outputStream.write(buffer, 0, length)
                    }
                }
            }
            Log.i(TAG, "Database copied successfully")
        } catch (e: IOException) {
            Log.e(TAG, "Error copying database from assets", e)
            throw RuntimeException("Error copying database from assets", e)
        }
    }

    /**
     * Checks if the database already exists.
     */
    private fun checkDatabase(): Boolean {
        var db: SQLiteDatabase? = null
        try {
            db = SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
        } catch (_: SQLiteException) {
            // Database doesn’t exist yet
        } finally {
            db?.close()
        }
        return db != null
    }

    /**
     * Opens the database, copying it if necessary.
     */
    @Throws(SQLException::class)
    @Synchronized
    fun openDatabase(): SQLiteDatabase {
        if (!checkDatabase()) {
            File(dbPath).parentFile?.mkdirs()
            copyDatabase()
        }
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READWRITE)
    }

    override fun close() {
        try {
            writableDatabase.close()
        } catch (e: Exception) {
            Log.w(TAG, "Error closing database", e)
        }
        super.close()
    }

    fun getBNL(): List<LabuData> {
        val db  = openDatabase()
        val cursor = db.rawQuery("SELECT * FROM BNL", null)
        val bnlList = mutableListOf<LabuData>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val laNumber = cursor.getString(cursor.getColumnIndexOrThrow("la_number"))
            val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))
            val laThupineu = cursor.getString(cursor.getColumnIndexOrThrow("la_thupineu"))
            val laPhuaktu1 = cursor.getString(cursor.getColumnIndexOrThrow("la_phuaktu1"))
            val laPhuaktu2 = cursor.getString(cursor.getColumnIndexOrThrow("la_phuaktu2"))
            val laPhuaktu3 = cursor.getString(cursor.getColumnIndexOrThrow("la_phuaktu3"))
            val laPhuaktu4 = cursor.getString(cursor.getColumnIndexOrThrow("la_phuaktu4"))
            val laKey = cursor.getString(cursor.getColumnIndexOrThrow("la_key"))
            val laText1 = cursor.getString(cursor.getColumnIndexOrThrow("la_text1"))
            val chorus1 = cursor.getString(cursor.getColumnIndexOrThrow("chorus1"))
            val laText2 = cursor.getString(cursor.getColumnIndexOrThrow("la_text2"))
            val chorus2 = cursor.getString(cursor.getColumnIndexOrThrow("chorus2"))
            val laText3 = cursor.getString(cursor.getColumnIndexOrThrow("la_text3"))
            val chorus3 = cursor.getString(cursor.getColumnIndexOrThrow("chorus3"))
            val laText4 = cursor.getString(cursor.getColumnIndexOrThrow("la_text4"))
            val chorus4 = cursor.getString(cursor.getColumnIndexOrThrow("chorus4"))
            val laText5 = cursor.getString(cursor.getColumnIndexOrThrow("la_text5"))
            val chorus5 = cursor.getString(cursor.getColumnIndexOrThrow("chorus5"))
            val laText6 = cursor.getString(cursor.getColumnIndexOrThrow("la_text6"))
            val chorus6 = cursor.getString(cursor.getColumnIndexOrThrow("chorus6"))
            val laText7 = cursor.getString(cursor.getColumnIndexOrThrow("la_text7"))
            val chorus7 = cursor.getString(cursor.getColumnIndexOrThrow("chorus7"))
            val laText8 = cursor.getString(cursor.getColumnIndexOrThrow("la_text8"))
            val chorus8 = cursor.getString(cursor.getColumnIndexOrThrow("chorus8"))
            val laText9 = cursor.getString(cursor.getColumnIndexOrThrow("la_text9"))
            val laText10 = cursor.getString(cursor.getColumnIndexOrThrow("la_text10"))

            bnlList.add(LabuData(id, laNumber, laThulu, laThupineu, laPhuaktu1,
                laPhuaktu2, laPhuaktu3, laPhuaktu4, laKey, laText1, chorus1, laText2, chorus2, laText3, chorus3, laText4, chorus4, laText5, chorus5, laText6,
                chorus6, laText7, chorus7, laText8, chorus8, laText9, laText10))
        }
        cursor.close()
        return bnlList
    }

    fun getPNL(): List<LabuData> {
        val db = openDatabase()
        val cursor = db.rawQuery("SELECT * FROM PNL", null)
        val labuList = mutableListOf<LabuData>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val laNumber = cursor.getString(cursor.getColumnIndexOrThrow("la_number"))
            val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))
            val laThupineu = cursor.getString(cursor.getColumnIndexOrThrow("la_thupineu"))
            val laPhuaktu1 = cursor.getString(cursor.getColumnIndexOrThrow("la_phuaktu1"))
            val laPhuaktu2 = cursor.getString(cursor.getColumnIndexOrThrow("la_phuaktu2"))
            val laPhuaktu3 = cursor.getString(cursor.getColumnIndexOrThrow("la_phuaktu3"))
            val laPhuaktu4 = cursor.getString(cursor.getColumnIndexOrThrow("la_phuaktu4"))
            val laKey = cursor.getString(cursor.getColumnIndexOrThrow("la_key"))
            val laText1 = cursor.getString(cursor.getColumnIndexOrThrow("la_text1"))
            val chorus1 = cursor.getString(cursor.getColumnIndexOrThrow("chorus1"))
            val laText2 = cursor.getString(cursor.getColumnIndexOrThrow("la_text2"))
            val chorus2 = cursor.getString(cursor.getColumnIndexOrThrow("chorus2"))
            val laText3 = cursor.getString(cursor.getColumnIndexOrThrow("la_text3"))
            val chorus3 = cursor.getString(cursor.getColumnIndexOrThrow("chorus3"))
            val laText4 = cursor.getString(cursor.getColumnIndexOrThrow("la_text4"))
            val chorus4 = cursor.getString(cursor.getColumnIndexOrThrow("chorus4"))
            val laText5 = cursor.getString(cursor.getColumnIndexOrThrow("la_text5"))
            val chorus5 = cursor.getString(cursor.getColumnIndexOrThrow("chorus5"))
            val laText6 = cursor.getString(cursor.getColumnIndexOrThrow("la_text6"))
            val chorus6 = cursor.getString(cursor.getColumnIndexOrThrow("chorus6"))
            val laText7 = cursor.getString(cursor.getColumnIndexOrThrow("la_text7"))
            val chorus7 = cursor.getString(cursor.getColumnIndexOrThrow("chorus7"))
            val laText8 = cursor.getString(cursor.getColumnIndexOrThrow("la_text8"))
            val chorus8 = cursor.getString(cursor.getColumnIndexOrThrow("chorus8"))
            val laText9 = cursor.getString(cursor.getColumnIndexOrThrow("la_text9"))
            val laText10 = cursor.getString(cursor.getColumnIndexOrThrow("la_text10"))


            labuList.add(LabuData(id, laNumber, laThulu, laThupineu, laPhuaktu1,
                laPhuaktu2, laPhuaktu3, laPhuaktu4, laKey, laText1, chorus1, laText2, chorus2, laText3, chorus3, laText4, chorus4, laText5, chorus5, laText6,
                chorus6, laText7, chorus7, laText8, chorus8, laText9, laText10))
        }

        cursor.close()
        return labuList
    }

    fun getBNLContent(): List<LabuContent> {
        val db = openDatabase()
        val cursor = db.rawQuery("SELECT id, la_number, la_thulu FROM $TABLE_BNL", null)
        val labuList = mutableListOf<LabuContent>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val laNumber = cursor.getString(cursor.getColumnIndexOrThrow("la_number"))
            val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))

            labuList.add(LabuContent(id, laNumber, laThulu))
        }

        cursor.close()
        return labuList
    }
    fun getPNLContent(): List<LabuContent> {
        val db = openDatabase()
        val cursor = db.rawQuery("SELECT id, la_number, la_thulu FROM $TABLE_PNL", null)
        val labuList = mutableListOf<LabuContent>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val laNumber = cursor.getString(cursor.getColumnIndexOrThrow("la_number"))
            val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))

            labuList.add(LabuContent(id, laNumber, laThulu))
        }

        cursor.close()
        return labuList
    }

    fun getRecent(): List<HistoryData> {
        val recentViews = mutableListOf<HistoryData>()
        val db = openDatabase()

        val query = "SELECT * FROM Recent ORDER BY timestamp DESC"
        db.rawQuery(query, null).use { cursor ->
            if (cursor.moveToFirst()) {
                do {
                    val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                    val laNumber = cursor.getString(cursor.getColumnIndexOrThrow("la_number"))
                    val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))
                    val labuMin = cursor.getString(cursor.getColumnIndexOrThrow("labu_min"))
                    val timestamp = cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))
                    recentViews.add(HistoryData(id, laNumber, laThulu, labuMin, timestamp))
                } while (cursor.moveToNext())
            }
        }

        db.close()
        return recentViews
    }

    fun saveRecentView(id: Int, laNumber: String, laThulu: String, labuMin: String, timestamp: Long) {
        val db = writableDatabase

        val query = "SELECT la_number FROM Recent WHERE la_number = ? AND la_thulu = ? AND labu_min = ?"
        db.rawQuery(query, arrayOf(laNumber, laThulu, labuMin)).use { cursor ->
            if (cursor.count == 0) {
                val contentValues = ContentValues().apply {
                    put("id", id) // still stored, but not used for uniqueness
                    put("la_number", laNumber)
                    put("la_thulu", laThulu)
                    put("labu_min", labuMin)
                    put("timestamp", timestamp)
                }
                db.insert("Recent", null, contentValues)
            } else {
                val contentValues = ContentValues().apply {
                    put("timestamp", timestamp)
                }
                db.update(
                    "Recent",
                    contentValues,
                    "la_number = ? AND la_thulu = ? AND labu_min = ?",
                    arrayOf(laNumber, laThulu, labuMin)
                )
            }
        }

        db.close()
    }


    fun clearRecentViews() {
        val db = writableDatabase
        db.delete("Recent", null, null)
        db.close()
    }

    fun getCombinedLabuContent(): List<HistoryData> {
        val db = openDatabase()
        val query = """
        SELECT id, la_number, la_thulu, NULL AS timestamp, 'BNL' AS labu_min FROM BNL
        UNION ALL
        SELECT id, la_number, la_thulu, NULL AS timestamp, 'PNL' AS labu_min FROM PNL
    """
        val cursor = db.rawQuery(query, null)
        val labuList = mutableListOf<HistoryData>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val laNumber = cursor.getString(cursor.getColumnIndexOrThrow("la_number"))
            val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))
            val labuMin = cursor.getString(cursor.getColumnIndexOrThrow("labu_min"))
            val timestamp = if (cursor.isNull(cursor.getColumnIndexOrThrow("timestamp"))) null else cursor.getLong(cursor.getColumnIndexOrThrow("timestamp"))


            labuList.add(HistoryData(id, laNumber, laThulu, labuMin, timestamp))
        }

        cursor.close()
        return labuList
    }

    fun searchLabu(query: String): List<SearchData> {
        val db = openDatabase()
        val searchQuery = "%$query%"
        val sql = """
        SELECT id, la_number, la_thulu, la_text1, chorus1, 'BNL' AS labu_min
        FROM BNL
        WHERE la_thulu LIKE ? OR la_text1 LIKE ? OR chorus1 LIKE ?
        UNION ALL
        SELECT id, la_number, la_thulu, la_text1, chorus1, 'PNL' AS labu_min
        FROM PNL
        WHERE la_thulu LIKE ? OR la_text1 LIKE ? OR chorus1 LIKE ?
        ORDER BY la_number
    """.trimIndent()

        val cursor = db.rawQuery(sql, arrayOf(searchQuery, searchQuery, searchQuery, searchQuery, searchQuery, searchQuery))
        val results = mutableListOf<SearchData>()

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val laNumber = cursor.getString(cursor.getColumnIndexOrThrow("la_number"))
            val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))
            val laText1 = cursor.getString(cursor.getColumnIndexOrThrow("la_text1"))
            val chorus1 = cursor.getString(cursor.getColumnIndexOrThrow("chorus1"))
            val labuMin = cursor.getString(cursor.getColumnIndexOrThrow("labu_min"))

            results.add(
                SearchData(
                    id = id,
                    la_number = laNumber,
                    la_thulu = laThulu,
                    labu_min = labuMin,
                    la_text1 = laText1,
                    chorus1 = chorus1
                )
            )
        }

        cursor.close()
        db.close()
        return results
    }



    fun getCategories(): List<CategoriesData> {
        val db = openDatabase()
        val cursor = db.rawQuery("SELECT * FROM CategoryMin", null)
        val categories = mutableListOf<CategoriesData>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val min = cursor.getString(cursor.getColumnIndexOrThrow("Acategory_min"))

                // fetch related laCategories for this category
                val laCategories = getLaCategoriesByCategoryId(id)

                // add to the main list
                categories.add(
                    CategoriesData(
                        id = id,
                        Acategory_min = min,
                        laCategories = laCategories   // <-- assuming your data class has this field
                    )
                )

            } while (cursor.moveToNext())
        }

        cursor.close()
        return categories
    }

    fun getLaCategoriesByCategoryId(categoryId: Int): List<LaCategoryData> {
        val laCategoryList = mutableListOf<LaCategoryData>()
        val db = openDatabase()

        val cursor = db.rawQuery(
            """
        SELECT l.id, l.labu_min, l.la_id, l.category_id, b.la_thulu
        FROM LaCategories l
        JOIN BNL b ON l.la_id = b.la_number
        WHERE l.category_id = ?
        """,
            arrayOf(categoryId.toString())
        )

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val labuMin = cursor.getString(cursor.getColumnIndexOrThrow("labu_min"))
                val laId = cursor.getString(cursor.getColumnIndexOrThrow("la_id"))
                val catId = cursor.getInt(cursor.getColumnIndexOrThrow("category_id"))
                val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))

                val item = LaCategoryData(
                    id = id,
                    labu_min = labuMin,
                    la_id = laId,
                    category_id = catId,
                    la_thulu = laThulu
                )
                laCategoryList.add(item)

            } while (cursor.moveToNext())
        }
        cursor.close()

        return laCategoryList
    }

    fun getAllBnlBookmarks(): List<BookmarkData> {
        val bookmarkViews = mutableListOf<BookmarkData>()
        val db = openDatabase()
        val query = "SELECT * FROM BNLbookmark ORDER BY createdAt DESC"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val laNumber = cursor.getString(cursor.getColumnIndexOrThrow("la_number"))
                val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))
                val note = cursor.getString(cursor.getColumnIndexOrThrow("note")) // ✅ new

                bookmarkViews.add(
                    BookmarkData(id, laNumber, laThulu, createdAt, note)
                )
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return bookmarkViews
    }


    fun addBnlBookmark(laNumber: String, laThulu: String, createdAt: String, note: String? = null): Boolean {
        val db = writableDatabase

        val query = "SELECT la_number FROM BNLbookmark WHERE la_number = ?"
        val cursor = db.rawQuery(query, arrayOf(laNumber))

        val isAdded = if (cursor.count == 0) {
            val contentValues = ContentValues().apply {
                put("la_number", laNumber)
                put("la_thulu", laThulu)
                put("createdAt", createdAt)
                put("note", note) // ✅ new
            }
            db.insert("BNLbookmark", null, contentValues) != -1L
        } else {
            false
        }

        cursor.close()
        db.close()
        return isAdded
    }


    fun updateBnlNote(laNumber: String, newNote: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("note", newNote)
        }
        db.update("BNLBookmark", values, "la_number = ?", arrayOf(laNumber))
        db.close()
    }


    fun deleteBnlBookmark(bookmarkId: String): Boolean {
        val db = writableDatabase
        val rowsAffected = db.delete("BNLbookmark", "la_number = ?", arrayOf(bookmarkId))
        db.close()
        return rowsAffected > 0
    }


    fun getAllPnlBookmarks(): List<BookmarkData> {
        val bookmarkViews = mutableListOf<BookmarkData>()
        val db = openDatabase()
        val query = "SELECT * FROM PNLbookmark ORDER BY createdAt DESC"
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
                val laNumber = cursor.getString(cursor.getColumnIndexOrThrow("la_number"))
                val laThulu = cursor.getString(cursor.getColumnIndexOrThrow("la_thulu"))
                val createdAt = cursor.getString(cursor.getColumnIndexOrThrow("createdAt"))
                val note = cursor.getString(cursor.getColumnIndexOrThrow("note"))

                bookmarkViews.add(BookmarkData(id, laNumber, laThulu, createdAt, note))
            } while (cursor.moveToNext())
        }

        cursor.close()
        db.close()
        return bookmarkViews
    }

    fun addPnlBookmark(laNumber: String, laThulu: String, createdAt: String, note: String? = null): Boolean {
        val db = writableDatabase

        // Check if the bookmark already exists
        val query = "SELECT la_number FROM PNLbookmark WHERE la_number = ?"
        val cursor = db.rawQuery(query, arrayOf(laNumber))

        val isAdded = if (cursor.count == 0) {
            // Insert new bookmark if it doesn't exist
            val contentValues = ContentValues().apply {
                put("la_number", laNumber)
                put("la_thulu", laThulu)
                put("createdAt", createdAt)
                put("note", note)
            }
            db.insert("PNLbookmark", null, contentValues) != -1L
        } else {
            false // Bookmark already exists
        }

        cursor.close()
        db.close()
        return isAdded
    }

    fun updatePnlNote(laNumber: String, newNote: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("note", newNote)
        }
        db.update("PNLBookmark", values, "la_number = ?", arrayOf(laNumber))
        db.close()
    }
    fun deletePnlBookmark(bookmarkId: String): Boolean {
        val db = writableDatabase
        val rowsAffected = db.delete("PNLbookmark", "la_number = ?", arrayOf(bookmarkId))
        db.close()
        return rowsAffected > 0
    }

    fun isPagePnlBookmarked(laNumber: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM PNLbookmark WHERE la_number = ?"
        val cursor = db.rawQuery(query, arrayOf(laNumber))
        var isBookmarked = false
        if (cursor.moveToFirst()) {
            isBookmarked = cursor.getInt(0) > 0
        }
        cursor.close()
        db.close()
        return isBookmarked
    }

    fun isPageBnlBookmarked(laNumber: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM BNLbookmark WHERE la_number = ?"
        val cursor = db.rawQuery(query, arrayOf(laNumber))
        var isBookmarked = false
        if (cursor.moveToFirst()) {
            isBookmarked = cursor.getInt(0) > 0
        }
        cursor.close()
        db.close()
        return isBookmarked
    }



}

