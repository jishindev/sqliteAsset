package com.jaus.albertogiunta.justintrain_oraritreni.db.sqliteAsset

import android.arch.persistence.db.SupportSQLiteOpenHelper

/**
 * Implements [SupportSQLiteOpenHelper.Factory] using the SQLite implementation in the
 * framework.
 */
@SuppressWarnings("unused")
class AssetSQLiteOpenHelperFactory : SupportSQLiteOpenHelper.Factory {
		@Override
		fun create(configuration: SupportSQLiteOpenHelper.Configuration): SupportSQLiteOpenHelper {
				return AssetSQLiteOpenHelper(
						configuration.context, configuration.name, null,
						configuration.version, configuration.errorHandler, configuration.callback
				)
		}
}
