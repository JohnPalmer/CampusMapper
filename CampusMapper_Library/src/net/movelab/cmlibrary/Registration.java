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

import java.util.Calendar;
import java.util.Locale;
import java.util.UUID;
import net.movelab.cmlibrary.R;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Allows user to provide answers to survey before starting using the app. All
 * responses are encrypted and uploaded to the server.
 * 
 * @author John R.B. Palmer
 * 
 */
public class Registration extends Activity {
	// private static final String TAG = "Registration";

    public static String JSON_KEY_PHONE_LANG = "phone_language";
    public static String JSON_KEY_VERSION = "version";
    public static String JSON_KEY_SDK = "sdk";
    public static String JSON_KEY_USER_CODE = "user_code";

    String userId = UUID.randomUUID().toString();

	private EditText survey_code_entry;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.registration_new);

		Context context = getApplicationContext();
		PropertyHolder.init(context);

        survey_code_entry = (EditText) findViewById(R.id.surveyCodeEntry);

		final Button saveRegistrationButton = (Button) findViewById(R.id.save_reg_button);
		saveRegistrationButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Context context = getBaseContext();

				PropertyHolder.setUserId(userId);
				PropertyHolder.setRegistered(true);
				PropertyHolder.setServiceOn(true);
				PropertyHolder.setWithdrawn(false);
				PropertyHolder.setStoreMyData(true);
				

				Intent schedulingIntent = new Intent(
						getResources().getString(R.string.internal_message_id)
								+ Util.MESSAGE_SCHEDULE);
				context.sendBroadcast(schedulingIntent);

				String phoneLanguage = Locale.getDefault().getDisplayLanguage();

				PackageInfo pInfo = null;
				try {
					pInfo = getPackageManager().getPackageInfo(
							getPackageName(), 0);
				} catch (NameNotFoundException e) {
				}
				String version = pInfo.versionName;

				String thisSDK = Integer.toString(Build.VERSION.SDK_INT);


				String user_code = "";
                CharSequence survey_code_chars = survey_code_entry.getText();
                if(survey_code_chars != null && survey_code_chars.length() > 0){
                    user_code = survey_code_chars.toString();
                }

                PropertyHolder.setUserCode(user_code);


                JSONObject response_json = new JSONObject();
                try{
                response_json.put(JSON_KEY_PHONE_LANG, phoneLanguage);
                    response_json.put(JSON_KEY_VERSION, version);
                    response_json.put(JSON_KEY_SDK, thisSDK);
                    response_json.put(JSON_KEY_USER_CODE, user_code);


                } catch(JSONException e){

                }

				String responses = response_json.toString();

				ContentResolver ucr = getContentResolver();

				ucr.insert(Util.getUploadQueueUri(context),
						UploadContentValues.createUpload("REG", responses));

				ucr.insert(
						Util.getUploadQueueUri(context),
						UploadContentValues.createUpload("CON",
								PropertyHolder.getConsentTime()));

				// try to upload them
				Intent i = new Intent(Registration.this, FileUploader.class);
				startService(i);

				// Util.toast(context,
				// "Activity-Space Mapper is now activated");

				// create an intent object and tell it where to go
				Intent intent2ASM = new Intent(Registration.this,
						MapMyData.class);
				// start the intent
				startActivity(intent2ASM);
				finish();
			}
		});

	}

}