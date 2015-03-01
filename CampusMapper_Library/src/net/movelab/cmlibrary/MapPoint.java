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

package net.movelab.cmlibrary;

public class MapPoint {

	int lat;
	int lon;
	float acc;
	long entryTime;
	long exitTime;

	int iconFlag;

	public static final int ICON_NORMAL = 0;

	public static final int ICON_CURRENT_LOCATION = 1;

	public static final int ICON_SELECTED_LOCATION = 2;

	public MapPoint(int _lat, int _lon, float _acc, long _entryTime,
			long _exitTime, int _iconFlag) {

		lat = _lat;
		lon = _lon;
		acc = _acc;
		entryTime = _entryTime;
		exitTime = _exitTime;
		iconFlag = _iconFlag;

	}

	public MapPoint copy() {

		return new MapPoint(lat, lon, acc, entryTime, exitTime, iconFlag);

	}

	public void setIconFlag(int _iconFlag) {

		iconFlag = _iconFlag;
	}

}
