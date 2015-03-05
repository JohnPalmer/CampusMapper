/*
 * Mobility Mapper is a mobile phone app for studying activity spaces on campuses. It is based in part on code from the Human Mobility Project.
 *
 * Copyright (c) 2015 John R.B. Palmer.
 *
 * Mobility Mapper is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Mobility Mapper is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
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

import net.movelab.cmlibrary.R;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

/**
 * The message displayed for user once he or she has withdrawn. Shows user the
 * userid that was used prior to withdrawal.
 * 
 * @author John R.B. Palmer
 * 
 */
public class WithdrawLock extends Activity {
	Context context;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		context = getApplicationContext();

		if (PropertyHolder.isInit() == false)
			PropertyHolder.init(context);
		
		setContentView(R.layout.withdraw_lock);

		String userID = PropertyHolder.getUserId();

		final TextView mUserID = (TextView) findViewById(R.id.userIDText);
		mUserID.setText(userID);

	}

}
