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

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.maps.GeoPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Allows user to view their own data in a map (from the database on their phone
 * -- not from the server).
 *
 * @author John R.B. Palmer
 */
public class MapMyData extends FragmentActivity implements OnMapReadyCallback {

    PolylineOptions routeLineOptionsBlack;
    Polyline polyline;

    private int count = 0;
    private long startMillis = 0;

    String TAG = "MapMyData";
    private ToggleButton mServiceButton;

    boolean getIntro = false;
    boolean isPro = false;

    boolean loadingData;
    boolean updatingDatabase;
    boolean savingCsv;

    boolean popup_a_already_done = false;
    boolean popup_b_already_done = false;

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

    List<LatLng> mPoints;
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

    private GoogleMap mMap;

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);


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

        routeLineOptionsBlack = new PolylineOptions()
                .width(25)
                .color(Color.BLACK)
                .geodesic(true);
        mPoints = new ArrayList<LatLng>();

        receiverNotificationArea = (LinearLayout) findViewById(R.id.mapReceiverNotificationArea);
        progressNotificationArea = (LinearLayout) findViewById(R.id.mapProgressNotificationArea);
        mReceiversOffWarning = (TextView) findViewById(R.id.mapReceiversOffWarning);
        progressbar = (ProgressBar) findViewById(R.id.mapProgressbar);

        getIntro = PropertyHolder.getIntro();
        isPro = PropertyHolder.getProVersion();

        privacyZones = new ArrayList<GeoPoint>();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }


    @Override
    public void onMapReady(GoogleMap map) {

        polyline = mMap.addPolyline(routeLineOptionsBlack);
        if (mPoints.size() > 0) {
            polyline.setPoints(mPoints);
        }

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


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventaction = event.getAction();
        if (eventaction == MotionEvent.ACTION_UP) {
            long time = System.currentTimeMillis();
            if (startMillis == 0 || (time - startMillis > 3000)) {
                startMillis = time;
                count = 1;
            } else {
                count++;
            }
            if (count == 4) {
                buildExpertMessage();
            }
            return true;
        }
        return false;
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



    private void buildExpertMessage() {
        final boolean expert = PropertyHolder.getExpertMode();
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(expert ? getResources().getString(R.string.exit_expert) : getResources().getString(R.string.enter_expert)).setTitle(getResources().getString(R.string.expert_title))
                .setCancelable(true)
                .setPositiveButton(getResources().getString(R.string.yes),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                PropertyHolder.setExpertMode(!expert);
                                // if expert was false at first, now we are in expert mode, so make sure alarm on if service on
                                if (!expert) {
                                    sendBroadcast(new Intent(getResources().getString(R.string.internal_message_id) + Util.MESSAGE_START_MESSAGE_C_TIMER));
                                    Util.toast(context, getResources().getString(R.string.expert_title));
                                } else {
                                    sendBroadcast(new Intent(getResources().getString(R.string.internal_message_id) + Util.MESSAGE_CANCEL_C_NOTIFICATION));
                                }
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(getResources().getString(R.string.no),
                        new DialogInterface.OnClickListener() {
                            public void onClick(final DialogInterface dialog,
                                                final int id) {
                                dialog.dismiss();
                            }
                        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public class DataGrabberTask extends AsyncTask<Context, Integer, Boolean> {
        int myProgress;
        int nFixes;
        ArrayList<MapPoint> results = new ArrayList<MapPoint>();
        GeoPoint center;

        @Override
        protected void onPreExecute() {
            myProgress = 0;
            progressNotificationArea.setVisibility(View.VISIBLE);
        }

        protected Boolean doInBackground(Context... context) {
            results.clear();
            final String selectionString;
            selectionString = Fix.Fixes.KEY_ROWID + " > " + lastRecId;
            ContentResolver cr = getContentResolver();
            Cursor c = cr.query(Util.getFixesUri(context[0]),
                    Fix.Fixes.KEYS_LATLONACCTIMES, selectionString, null, null);
            if (!c.moveToFirst()) {
                c.close();
                return false;
            }
            int latCol = c.getColumnIndexOrThrow("latitude");
            int lonCol = c.getColumnIndexOrThrow("longitude");
            int accCol = c.getColumnIndexOrThrow("accuracy");
            int idCol = c.getColumnIndexOrThrow("_id");
            int timeCol = c.getColumnIndexOrThrow(Fix.Fixes.KEY_TIMELONG);
            int sdtimeCol = c
                    .getColumnIndexOrThrow(Fix.Fixes.KEY_STATION_DEPARTURE_TIMELONG);
            nFixes = c.getCount();
// float lastAcc = 0;
            int currentRecord = 0;
            while (!c.isAfterLast()) {
                myProgress = (int) (((currentRecord + 1) / (float) nFixes) * 100);
                publishProgress(myProgress);
// Escape early if cancel() is called
                if (isCancelled())
                    break;
                lastRecId = c.getInt(idCol);
// First grabbing double values of lat lon and time
                Double lat = c.getDouble(latCol);
                Double lon = c.getDouble(lonCol);
                float acc = c.getFloat(accCol);
                long entryTime = c.getLong(timeCol);
                long exitTime = c.getLong(sdtimeCol);
                Double geoLat = lat * 1E6;
                Double geoLon = lon * 1E6;
                results.add(new MapPoint(geoLat.intValue(), geoLon.intValue(),
                        acc, entryTime, exitTime, MapPoint.ICON_NORMAL));
                c.moveToNext();
                currentRecord++;
            }
            c.close();
            return true;
        }

        protected void onProgressUpdate(Integer... progress) {
            progressbar.setProgress(progress[0]);
        }

        protected void onPostExecute(Boolean result) {
            if (result) {
                if (results != null && results.size() > 0) {
                    for (MapPoint p : results) {
                        mPoints.add(new LatLng(p.lat, p.lon));
                    }
                    final int newlastFixIndex = mPoints.size() - 1;
                    if (polyline != null) {
                        polyline.setPoints(mPoints);
                    }
                }
            } else
                progressNotificationArea.setVisibility(View.INVISIBLE);
            loadingData = false;
        }
    }


}