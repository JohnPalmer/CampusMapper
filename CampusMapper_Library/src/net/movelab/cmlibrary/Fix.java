
/*
 * Campus Mobility is a mobile phone app for studying activity spaces on campuses. It is based in part on code from the Human Mobility Project.
 *
 * Copyright (c) 2015 John R.B. Palmer.
 *
 * Campus Mobility is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Campus Mobility is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program. If not, see http://www.gnu.org/licenses.
 *
 *
 * The code incorporated from the Human Mobility Project is subject to the following terms:
 *
 * Copyright 2010, 2011 Human Mobility Project
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated documentation files (the "Software"), to deal in the Software without restriction, including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package net.movelab.cmlibrary;

import android.provider.BaseColumns;

public class Fix {

	public Fix() {
	}

	public static final class Fixes implements BaseColumns {
		private Fixes() {
		}


		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.palmerasmlibrary.fixes";

		/** The row ID key name */
		public static final String KEY_ROWID = "_id";

		/** The timelong key name */
		public static final String KEY_TIMELONG = "timelong";

		public static final String KEY_ACCURACY = "accuracy";

		public static final String KEY_ALTITUDE = "altitude";

		public static final String KEY_LATITUDE = "latitude";

		public static final String KEY_LONGITUDE = "longitude";

		public static final String KEY_PROVIDER = "provider";
		
		public static final String KEY_STATION_DEPARTURE_TIMELONG = "sdtimelong";

		/** The names of all the fields contained in the location fix table */
		public static final String[] KEYS_ALL = { KEY_ROWID, KEY_ACCURACY,
				KEY_ALTITUDE, KEY_LATITUDE, KEY_LONGITUDE, KEY_PROVIDER,
				KEY_TIMELONG, KEY_STATION_DEPARTURE_TIMELONG };

		public static final String[] KEYS_SAVECSV = { KEY_ACCURACY,
				KEY_ALTITUDE, KEY_LATITUDE, KEY_LONGITUDE, KEY_PROVIDER,
				KEY_TIMELONG, KEY_STATION_DEPARTURE_TIMELONG};

		public static final String[] KEYS_LATLON = { KEY_ROWID, KEY_LATITUDE,
				KEY_LONGITUDE };

		public static final String[] KEYS_LATLONACC = { KEY_ROWID,
				KEY_LATITUDE, KEY_LONGITUDE, KEY_ACCURACY };

		public static final String[] KEYS_LATLONTIME = { KEY_ROWID,
				KEY_LATITUDE, KEY_LONGITUDE, KEY_TIMELONG };

		public static final String[] KEYS_LATLONACCTIMES = { KEY_ROWID,
			KEY_LATITUDE, KEY_LONGITUDE, KEY_ACCURACY, KEY_TIMELONG, KEY_STATION_DEPARTURE_TIMELONG };

	}

}
