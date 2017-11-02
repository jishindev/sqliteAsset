package com.jaus.albertogiunta.justintrain_oraritreni.db.sqliteAsset

import android.arch.persistence.db.SupportSQLiteProgram
import android.database.sqlite.SQLiteProgram

/**
 * An wrapper around [SQLiteProgram] to implement [SupportSQLiteProgram] API.
 */
internal class FrameworkSQLiteProgram(private val mDelegate: SQLiteProgram) : SupportSQLiteProgram {


		fun bindNull(index: Int) {
				mDelegate.bindNull(index)
		}


		fun bindLong(index: Int, value: Long) {
				mDelegate.bindLong(index, value)
		}


		fun bindDouble(index: Int, value: Double) {
				mDelegate.bindDouble(index, value)
		}


		fun bindString(index: Int, value: String) {
				mDelegate.bindString(index, value)
		}


		fun bindBlob(index: Int, value: ByteArray) {
				mDelegate.bindBlob(index, value)
		}


		fun clearBindings() {
				mDelegate.clearBindings()
		}
}
