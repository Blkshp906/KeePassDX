/*
 * Copyright 2009-2011 Brian Pellin.
 *     
 * This file is part of KeePassDroid.
 *
 *  KeePassDroid is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 2 of the License, or
 *  (at your option) any later version.
 *
 *  KeePassDroid is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with KeePassDroid.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.keepassdroid.search;

import java.util.ArrayList;
import java.util.Iterator;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;
import android.util.Log;

import com.android.keepass.R;
import com.keepassdroid.Database;
import com.keepassdroid.database.PwDatabaseV3;
import com.keepassdroid.database.PwDatabaseV4;
import com.keepassdroid.database.PwEntry;
import com.keepassdroid.database.PwGroup;
import com.keepassdroid.database.PwGroupV3;
import com.keepassdroid.database.PwGroupV4;

public class SearchDbHelper {
	private static final String DATABASE_NAME = "search";
	private static final String SEARCH_TABLE = "entries";
	private static final int DATABASE_VERSION = 4;
	
	private static final String DATABASE_DROP =
		"drop table " + SEARCH_TABLE;
	
	private final Context mCtx;
	
	private static class DatabaseHelper extends SQLiteOpenHelper {
		
		DatabaseHelper(Context ctx) {
			super(ctx, DATABASE_NAME, null, DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			if (oldVersion != DATABASE_VERSION) {
				db.execSQL(DATABASE_DROP);
			}
		}
		
	}
	
	public SearchDbHelper(Context ctx) {
		mCtx = ctx;
		
		// Delete old database if it is still there
		DatabaseHelper dbHelper = new DatabaseHelper(ctx);
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		db.close();
	}
	
	private boolean omitBackup() {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mCtx);
		return prefs.getBoolean(mCtx.getString(R.string.omitbackup_key), mCtx.getResources().getBoolean(R.bool.omitbackup_default));
		
	}
	
	public PwGroup search(Database db, String qStr) {

		PwGroup group;
		if ( db.pm instanceof PwDatabaseV3 ) {
			group = new PwGroupV3();
		} else if ( db.pm instanceof PwDatabaseV4 ) {
			group = new PwGroupV4();
		} else {
			Log.d("SearchDbHelper", "Tried to search with unknown db");
			return null;
		}
		group.name = "Search results";
		group.childEntries = new ArrayList<PwEntry>();
		
		// Search all entries
		qStr = qStr.toLowerCase();
		boolean isOmitBackup = omitBackup();
		for (PwEntry entry : db.pm.getEntries()) {
			
			if (!isOmitBackup || !db.pm.isBackup(entry.getParent())) {
				// Search all strings in the entry
				Iterator<String> iter = entry.stringIterator();
				while (iter.hasNext()) {
					String str = iter.next().toLowerCase();
					if (str.contains(qStr)) {
						group.childEntries.add(entry);
						break;
					}
				}
			}
			
		}
		
		return group;
	}
}
