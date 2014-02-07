/*
 * Space Mapper
 * Copyright (C) 2012, 2013 John R.B. Palmer
 * Contact: jrpalmer@princeton.edu
 * 
 * This file is part of Space Mapper.
 * 
 * Space Mapper is free software: you can redistribute it and/or modify 
 * it under the terms of the GNU General Public License as published by 
 * the Free Software Foundation, either version 3 of the License, or (at 
 * your option) any later version.
 * 
 * Space Mapper is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of 
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  
 * See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along 
 * with Space Mapper.  If not, see <http://www.gnu.org/licenses/>.
 */

package edu.princeton.jrpalmer.asmlibrary;

import android.content.ContentValues;
import edu.princeton.jrpalmer.asmlibrary.Upload.Uploads;

public class UploadContentValues {
	
	
	public static ContentValues createUpload(String prefix, String data) {

		ContentValues initialValues = new ContentValues();
		initialValues.put(Uploads.KEY_PREFIX, prefix);
		initialValues.put(Uploads.KEY_DATA, data);

		return initialValues;
	}

	


}