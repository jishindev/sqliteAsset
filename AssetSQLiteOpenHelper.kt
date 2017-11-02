package com.jaus.albertogiunta.justintrain_oraritreni.db.sqliteAsset

import android.arch.persistence.db.SupportSQLiteDatabase
import android.arch.persistence.db.SupportSQLiteOpenHelper
import android.content.Context
import android.database.DatabaseErrorHandler
import android.database.sqlite.SQLiteDatabase
import android.os.Build
import android.support.annotation.RequiresApi

internal class AssetSQLiteOpenHelper(context: Context, name: String,
                                     factory: SQLiteDatabase.CursorFactory, version: Int,
                                     errorHandler: DatabaseErrorHandler,
                                     callback: Callback) : SupportSQLiteOpenHelper {
		private val mDelegate: OpenHelper

		val databaseName: String
				get() = mDelegate.getDatabaseName()

		val writableDatabase: SupportSQLiteDatabase
				get() = mDelegate.writableSupportDatabase

		val readableDatabase: SupportSQLiteDatabase
				get() = mDelegate.readableSupportDatabase

		init {
				mDelegate = createDelegate(context, name, factory, version, errorHandler, callback)
		}

		private fun createDelegate(context: Context, name: String,
		                           factory: SQLiteDatabase.CursorFactory, version: Int, errorHandler: DatabaseErrorHandler,
		                           callback: Callback): OpenHelper {
				return object : OpenHelper(context, name, factory, version, errorHandler) {
						
						fun onCreate(sqLiteDatabase: SQLiteDatabase) {
								mWrappedDb = FrameworkSQLiteDatabase(sqLiteDatabase)
								callback.onCreate(mWrappedDb)
						}

						
						fun onUpgrade(sqLiteDatabase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
								callback.onUpgrade(getWrappedDb(sqLiteDatabase), oldVersion, newVersion)
						}

						
						fun onConfigure(db: SQLiteDatabase) {
								callback.onConfigure(getWrappedDb(db))
						}

						
						fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
								callback.onDowngrade(getWrappedDb(db), oldVersion, newVersion)
						}

						
						fun onOpen(db: SQLiteDatabase) {
								callback.onOpen(getWrappedDb(db))
						}
				}
		}

		
		@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
		fun setWriteAheadLoggingEnabled(enabled: Boolean) {
				mDelegate.setWriteAheadLoggingEnabled(enabled)
		}

		
		fun close() {
				mDelegate.close()
		}

		internal abstract class OpenHelper(context: Context, name: String,
		                                   factory: SQLiteDatabase.CursorFactory, version: Int,
		                                   errorHandler: DatabaseErrorHandler) : SQLiteAssetHelper(context, name, null, factory, version, errorHandler) {

				var mWrappedDb: FrameworkSQLiteDatabase? = null

				val writableSupportDatabase: SupportSQLiteDatabase
						get() {
								val db = super.getWritableDatabase()
								return getWrappedDb(db)
						}

				val readableSupportDatabase: SupportSQLiteDatabase
						get() {
								val db = super.getReadableDatabase()
								return getWrappedDb(db)
						}

				fun getWrappedDb(sqLiteDatabase: SQLiteDatabase): FrameworkSQLiteDatabase {
						if (mWrappedDb == null) {
								mWrappedDb = FrameworkSQLiteDatabase(sqLiteDatabase)
						}
						return mWrappedDb
				}

				
				@Synchronized
				fun close() {
						super.close()
						mWrappedDb = null
				}
		}
}
