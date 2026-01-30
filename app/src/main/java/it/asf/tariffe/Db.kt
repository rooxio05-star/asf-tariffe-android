package it.asf.tariffe

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import java.io.File
import java.io.FileOutputStream

class Db(private val ctx: Context) {
    private val dbName = "asf_tariffe.db"
    private val dbPath: String by lazy {
        File(ctx.filesDir, dbName).absolutePath
    }

    private fun ensureDbCopied() {
        val outFile = File(dbPath)
        if (outFile.exists() && outFile.length() > 0) return

        ctx.assets.open(dbName).use { input ->
            FileOutputStream(outFile).use { output ->
                val buf = ByteArray(1024 * 64)
                while (true) {
                    val r = input.read(buf)
                    if (r <= 0) break
                    output.write(buf, 0, r)
                }
                output.flush()
            }
        }
    }

    fun open(): SQLiteDatabase {
        ensureDbCopied()
        return SQLiteDatabase.openDatabase(dbPath, null, SQLiteDatabase.OPEN_READONLY)
    }
}
