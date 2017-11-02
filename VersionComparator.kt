package com.jaus.albertogiunta.justintrain_oraritreni.db.sqliteAsset

import android.util.Log

import com.jaus.albertogiunta.justintrain_oraritreni.db.sqliteAsset.SQLiteAssetHelper.SQLiteAssetException

import java.util.Comparator
import java.util.regex.Matcher
import java.util.regex.Pattern

/**
 * Compare paths by their upgrade version numbers, instead of using
 * alphanumeric comparison on plain file names. This prevents the upgrade
 * scripts from being applied out of order when they first move to double-,
 * triple-, etc. digits.
 *
 *
 * For example, this fixes an upgrade that would apply 2 different upgrade
 * files from version 9 to 11 (`..._updated_9_10` and
 * `..._updated_10_11`) from using the *incorrect*
 * alphanumeric order of `10_11` before `9_10`.
 *
 */
internal class VersionComparator : Comparator<String> {
		companion object {
				private val TAG = SQLiteAssetHelper::class.java.getSimpleName()
		}
		private val pattern = Pattern.compile(".*_upgrade_([0-9]+)-([0-9]+).*")

		/**
		 * Compares the two specified upgrade script strings to determine their
		 * relative ordering considering their two version numbers. Assumes all
		 * database names used are the same, as this function only compares the
		 * two version numbers.
		 *
		 * @param file0 an upgrade script file name
		 * @param file1 a second upgrade script file name to compare with file0
		 * @return an integer < 0 if file0 should be applied before file1, 0 if
		 * they are equal (though that shouldn't happen), and > 0 if
		 * file0 should be applied after file1.
		 * @throws SQLiteAssetException thrown if the strings are not in the correct upgrade
		 * script format of:
		 * `databasename_fromVersionInteger_toVersionInteger`
		 */
		@Override
		fun compare(file0: String, file1: String): Int {
				val m0 = pattern.matcher(file0)
				val m1 = pattern.matcher(file1)

				if (!m0.matches()) {
						Log.w(TAG, "could not parse upgrade script file: " + file0)
						throw SQLiteAssetException("Invalid upgrade script file")
				}

				if (!m1.matches()) {
						Log.w(TAG, "could not parse upgrade script file: " + file1)
						throw SQLiteAssetException("Invalid upgrade script file")
				}

				val v0_from = Integer.valueOf(m0.group(1))
				val v1_from = Integer.valueOf(m1.group(1))
				val v0_to = Integer.valueOf(m0.group(2))
				val v1_to = Integer.valueOf(m1.group(2))

				if (v0_from == v1_from) {
						// 'from' versions match for both; check 'to' version next

						if (v0_to == v1_to) {
								return 0
						}

						return if (v0_to < v1_to) -1 else 1
				}

				return if (v0_from < v1_from) -1 else 1
		}
}
