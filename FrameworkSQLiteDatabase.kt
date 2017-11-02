package com.jaus.albertogiunta.justintrain_oraritreni.db.sqliteAsset

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import android.arch.persistence.db.SimpleSQLiteQuery
import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.db.SupportSQLiteQuery
import android.arch.persistence.db.SupportSQLiteStatement
import android.content.ContentValues
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteCursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteTransactionListener
import android.os.Build
import android.os.CancellationSignal
import android.support.annotation.RequiresApi
import android.util.Pair

import java.io.IOException
import java.util.Locale

/**
 * Delegates all calls to an implementation of [SQLiteDatabase].
 */
internal class FrameworkSQLiteDatabase
/**
 * Creates a wrapper around [SQLiteDatabase].
 *
 * @param delegate The delegate to receive all calls.
 */
@SuppressWarnings("WeakerAccess")
constructor(private val mDelegate: SQLiteDatabase) : SupportSQLiteDatabase {

		companion object {
				private val CONFLICT_VALUES = arrayOf("", " OR ROLLBACK ", " OR ABORT ", " OR FAIL ", " OR IGNORE ", " OR REPLACE ")
				private val EMPTY_STRING_ARRAY = arrayOfNulls<String>(0)

				private fun isEmpty(input: String?): Boolean {
						return input == null || input.length() === 0
				}
		}

		val isDbLockedByCurrentThread: Boolean
				get() = mDelegate.isDbLockedByCurrentThread()

		var version: Int
				get() = mDelegate.getVersion()
				set(version) {
						mDelegate.setVersion(version)
				}

		val maximumSize: Long
				get() = mDelegate.getMaximumSize()

		var pageSize: Long
				get() = mDelegate.getPageSize()
				set(numBytes) {
						mDelegate.setPageSize(numBytes)
				}

		val isReadOnly: Boolean
				get() = mDelegate.isReadOnly()

		val isOpen: Boolean
				get() = mDelegate.isOpen()

		val path: String
				get() = mDelegate.getPath()

		val isWriteAheadLoggingEnabled: Boolean
				@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
				get() = mDelegate.isWriteAheadLoggingEnabled()

		val attachedDbs: List<Pair<String, String>>
				get() = mDelegate.getAttachedDbs()

		val isDatabaseIntegrityOk: Boolean
				get() = mDelegate.isDatabaseIntegrityOk()


		fun compileStatement(sql: String): SupportSQLiteStatement {
				return FrameworkSQLiteStatement(mDelegate.compileStatement(sql))
		}


		fun beginTransaction() {
				mDelegate.beginTransaction()
		}


		fun beginTransactionNonExclusive() {
				mDelegate.beginTransactionNonExclusive()
		}


		fun beginTransactionWithListener(transactionListener: SQLiteTransactionListener) {
				mDelegate.beginTransactionWithListener(transactionListener)
		}


		fun beginTransactionWithListenerNonExclusive(
				transactionListener: SQLiteTransactionListener) {
				mDelegate.beginTransactionWithListenerNonExclusive(transactionListener)
		}


		fun endTransaction() {
				mDelegate.endTransaction()
		}


		fun setTransactionSuccessful() {
				mDelegate.setTransactionSuccessful()
		}


		fun inTransaction(): Boolean {
				return mDelegate.inTransaction()
		}


		fun yieldIfContendedSafely(): Boolean {
				return mDelegate.yieldIfContendedSafely()
		}


		fun yieldIfContendedSafely(sleepAfterYieldDelay: Long): Boolean {
				return mDelegate.yieldIfContendedSafely(sleepAfterYieldDelay)
		}


		fun setMaximumSize(numBytes: Long): Long {
				return mDelegate.setMaximumSize(numBytes)
		}


		fun query(query: String): Cursor {
				return query(SimpleSQLiteQuery(query))
		}


		fun query(query: String, bindArgs: Array<Object>): Cursor {
				return query(SimpleSQLiteQuery(query, bindArgs))
		}


		fun query(supportQuery: SupportSQLiteQuery): Cursor {
				return mDelegate.rawQueryWithFactory({ db, masterQuery, editTable, query ->
						supportQuery.bindTo(FrameworkSQLiteProgram(query))
						SQLiteCursor(masterQuery, editTable, query)
				}, supportQuery.getSql(), EMPTY_STRING_ARRAY, null)
		}


		@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
		fun query(supportQuery: SupportSQLiteQuery,
		          cancellationSignal: CancellationSignal): Cursor {
				return mDelegate.rawQueryWithFactory({ db, masterQuery, editTable, query ->
						supportQuery.bindTo(FrameworkSQLiteProgram(query))
						SQLiteCursor(masterQuery, editTable, query)
				}, supportQuery.getSql(), EMPTY_STRING_ARRAY, null, cancellationSignal)
		}


		@Throws(SQLException::class)
		fun insert(table: String, conflictAlgorithm: Int, values: ContentValues): Long {
				return mDelegate.insertWithOnConflict(table, null, values,
						conflictAlgorithm)
		}


		fun delete(table: String, whereClause: String, whereArgs: Array<Object>): Int {
				val query = ("DELETE FROM " + table
						+ if (isEmpty(whereClause)) "" else " WHERE " + whereClause)
				val statement = compileStatement(query)
				SimpleSQLiteQuery.bind(statement, whereArgs)
				return statement.executeUpdateDelete()
		}


		fun update(table: String, conflictAlgorithm: Int, values: ContentValues?, whereClause: String,
		           whereArgs: Array<Object>?): Int {
				// taken from SQLiteDatabase class.
				if (values == null || values!!.size() === 0) {
						throw IllegalArgumentException("Empty values")
				}
				val sql = StringBuilder(120)
				sql.append("UPDATE ")
				sql.append(CONFLICT_VALUES[conflictAlgorithm])
				sql.append(table)
				sql.append(" SET ")

				// move all bind args to one array
				val setValuesSize = values!!.size()
				val bindArgsSize = if (whereArgs == null) setValuesSize else setValuesSize + whereArgs.size
				val bindArgs = arrayOfNulls<Object>(bindArgsSize)
				var i = 0
				for (colName in values!!.keySet()) {
						sql.append(if (i > 0) "," else "")
						sql.append(colName)
						bindArgs[i++] = values!!.get(colName)
						sql.append("=?")
				}
				if (whereArgs != null) {
						i = setValuesSize
						while (i < bindArgsSize) {
								bindArgs[i] = whereArgs[i - setValuesSize]
								i++
						}
				}
				if (!isEmpty(whereClause)) {
						sql.append(" WHERE ")
						sql.append(whereClause)
				}
				val stmt = compileStatement(sql.toString())
				SimpleSQLiteQuery.bind(stmt, bindArgs)
				return stmt.executeUpdateDelete()
		}


		@Throws(SQLException::class)
		fun execSQL(sql: String) {
				mDelegate.execSQL(sql)
		}


		@Throws(SQLException::class)
		fun execSQL(sql: String, bindArgs: Array<Object>) {
				mDelegate.execSQL(sql, bindArgs)
		}


		fun needUpgrade(newVersion: Int): Boolean {
				return mDelegate.needUpgrade(newVersion)
		}


		fun setLocale(locale: Locale) {
				mDelegate.setLocale(locale)
		}


		fun setMaxSqlCacheSize(cacheSize: Int) {
				mDelegate.setMaxSqlCacheSize(cacheSize)
		}


		@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
		fun setForeignKeyConstraintsEnabled(enable: Boolean) {
				mDelegate.setForeignKeyConstraintsEnabled(enable)
		}


		fun enableWriteAheadLogging(): Boolean {
				return mDelegate.enableWriteAheadLogging()
		}


		@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
		fun disableWriteAheadLogging() {
				mDelegate.disableWriteAheadLogging()
		}


		@Throws(IOException::class)
		fun close() {
				mDelegate.close()
		}
}
