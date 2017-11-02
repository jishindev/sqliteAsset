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


import android.arch.persistence.db.SupportSQLiteStatement
import android.database.sqlite.SQLiteStatement

/**
 * Delegates all calls to a [SQLiteStatement].
 */
internal class FrameworkSQLiteStatement
/**
 * Creates a wrapper around a framework [SQLiteStatement].
 *
 * @param delegate The SQLiteStatement to delegate calls to.
 */
@SuppressWarnings("WeakerAccess")
constructor(private val mDelegate: SQLiteStatement) : SupportSQLiteStatement {

		
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

		
		fun execute() {
				mDelegate.execute()
		}

		
		fun executeUpdateDelete(): Int {
				return mDelegate.executeUpdateDelete()
		}

		
		fun executeInsert(): Long {
				return mDelegate.executeInsert()
		}

		
		fun simpleQueryForLong(): Long {
				return mDelegate.simpleQueryForLong()
		}

		
		fun simpleQueryForString(): String {
				return mDelegate.simpleQueryForString()
		}
}
