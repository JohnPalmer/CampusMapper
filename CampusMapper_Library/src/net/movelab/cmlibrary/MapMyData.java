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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import net.movelab.cmlibrary.R;
import net.movelab.cmlibrary.Fix.Fixes;
import net.movelab.cmlibrary.RangeSeekBar.OnRangeSeekBarChangeListener;
import net.movelab.cmlibrary.RangeSeekBarDonut.OnRangeSeekBarDonutChangeListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Allows user to view their own data in a map (from the database on their phone
 * -- not from the server).
 *
 * @author John R.B. Palmer
 */
public class MapMyData extends FragmentActivity {
    String TAG = "MapMyData";
    private ToggleButton mServiceButton;

    boolean getIntro = false;
    boolean isPro = false;

    boolean loadingData;
    boolean updatingDatabase;
    boolean savingCsv;


    GeoPoint point;
    boolean satToggle;
    String stringLat;
    String stringLng;
    String stringAlt;
    String stringAcc;
    String stringProvider;
    String stringTime;

    boolean shareData;

    Double lastLat = null;
    Double lastLon = null;
    Double lastGeoLat = null;
    Double lastGeoLon = null;

    int startDay;
    int startMonth;
    int startYear;

    int endDay;
    int endMonth;
    int endYear;

    private int lastRecId = 0;

    public static boolean reloadData = false;

    boolean drawConfidenceCircles;
    boolean drawIcons;

    // TODO setting this totrue for testing until I build the on/off button
    boolean selectNewPrivacyZone = true;
    ArrayList<GeoPoint> privacyZones;

    boolean isServiceOn;

    GeoPoint currentCenter;

    int BORDER_COLOR_MAP = 0xee4D2EFF;
    int FILL_COLOR_MAP = 0x554D2EFF;

    int BORDER_COLOR_SAT = 0xeeD9FCFF;
    int FILL_COLOR_SAT = 0xbbD9FCFF;

    int PZ_BORDER_COLOR = 0xee00ff00;
    int PZ_FILL_COLOR = 0x5500ff00;


    ProgressBar progressbar;

    ArrayList<MapPoint> mPoints;
    MapPoint[] mPointsArray;

    TextView progressbarText;

    public static String DATES_BUTTON_MESSAGE = "datesButtonMessage";

    LinearLayout progressNotificationArea;

    LinearLayout receiverNotificationArea;

    private TextView mReceiversOffWarning;

    boolean _mustDraw = true;

    long thumbMin = -1;
    long thumbMax = -1;

    private int minDist = 0;

    Context context = this;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


        // TODO add all of the alerts from countdown activity regarding sensors
        // off, sm off etc.

        PropertyHolder.init(context);
        PowerSensor.init(context);

        if (Util.trafficCop(this))
            finish();

        if (!PropertyHolder.getInitialStartDateSet()) {
            Calendar now = Calendar.getInstance();
            now.setTimeInMillis(System.currentTimeMillis());
            PropertyHolder.setMapStartDate(now);
            PropertyHolder.setInitialStartDateSet(true);
        }

        minDist = Util.getMinDist();
        // Log.e("FixGet", "minDist=" + minDist);

        setContentView(R.layout.map_layout);

        receiverNotificationArea = (LinearLayout) findViewById(R.id.mapReceiverNotificationArea);

        mReceiversOffWarning = (TextView) findViewById(R.id.mapReceiversOffWarning);


        getIntro = PropertyHolder.getIntro();
        isPro = PropertyHolder.getProVersion();

        AppRater.app_launched(this);

        privacyZones = new ArrayList<GeoPoint>();

    }


    @Override
    protected void onResume() {


        Context context = getApplicationContext();

        Log.e("TAPMAP", "selectPZ=" + selectNewPrivacyZone);

        if (Util.trafficCop(this))
            finish();

        if (reloadData) {
            mPoints.clear();
            lastRecId = 0;
        }

        isServiceOn = PropertyHolder.isServiceOn();
        shareData = PropertyHolder.getShareData();

        setNotificationArea();

        // service button
        mServiceButton = (ToggleButton) findViewById(R.id.service_button);
        mServiceButton.setChecked(isServiceOn);
        mServiceButton.setOnClickListener(new ToggleButton.OnClickListener() {
            public void onClick(View view) {
                if (view.getId() != R.id.service_button)
                    return;
                boolean on = ((ToggleButton) view).isChecked();
                String schedule = on ? Util.MESSAGE_SCHEDULE
                        : Util.MESSAGE_UNSCHEDULE;
                // Log.e(TAG, schedule + on);

                Context this_context = view.getContext();
                // now schedule or unschedule
                Intent intent = new Intent(
                        getString(R.string.internal_message_id) + schedule);
                if (this_context != null)
                    this_context.sendBroadcast(intent);
                if (on) {
                    isServiceOn = true;
                    final long ptNow = PropertyHolder.ptStart();

                    ContentResolver ucr = getContentResolver();
                    ucr.insert(
                            Util.getUploadQueueUri(this_context),
                            UploadContentValues.createUpload(DataCodeBook.ON_OFF_PREFIX, Util.makeOnfJsonString(true, ptNow)));
                } else {
                    isServiceOn = false;
                    final long ptNow = PropertyHolder.ptStop();
                    // stop uploader
                    Intent stopUploaderIntent = new Intent(MapMyData.this,
                            FileUploader.class);
                    // Stop service if it is currently running
                    stopService(stopUploaderIntent);
                    ContentResolver ucr = getContentResolver();
                    ucr.insert(
                            Util.getUploadQueueUri(this_context),
                            UploadContentValues.createUpload(DataCodeBook.ON_OFF_PREFIX, Util.makeOnfJsonString(false, ptNow)));

                }

                setNotificationArea();

            }
        });


        super.onResume();

    }

    private void setNotificationArea() {
        if (isServiceOn) {
            final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (!manager
                        .isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    receiverNotificationArea.setVisibility(View.VISIBLE);
                    mReceiversOffWarning.setText(getResources().getString(
                            R.string.noGPSnoNet));
                } else {
                    receiverNotificationArea.setVisibility(View.VISIBLE);

                    mReceiversOffWarning.setText(getResources().getString(
                            R.string.noGPS));
                }

                mReceiversOffWarning
                        .setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(
                                        android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));

                            }
                        });
            } else {
                receiverNotificationArea.setVisibility(View.INVISIBLE);
            }
        } else {
            receiverNotificationArea.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    private void buildFlushGPSAlert() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getString(R.string.renew_gps_alert))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {

                                Util.flushGPSFlag = true;

                                dialog.cancel();
                            }
                        })

                .setNegativeButton(getResources().getString(R.string.cancel),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                dialog.cancel();
                            }
                        });

        final AlertDialog alert = builder.create();
        alert.show();
    }

    private void buildAlertMessageIntro() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getResources().getText(R.string.main_text))
                .setCancelable(true)
                .setNeutralButton(getResources().getString(R.string.ok),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                dialog.dismiss();
                            }
                        });
        final AlertDialog alert = builder.create();
        alert.show();
    }


}