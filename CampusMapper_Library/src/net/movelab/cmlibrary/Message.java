
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

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Message extends Activity {

    String message_body;
    int notification_code = Util.MESSAGE_A_NOTIFICATION;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.message);

        Intent incomingIntent = getIntent();
        if (incomingIntent != null && incomingIntent.hasExtra("message_body")) {

            message_body = incomingIntent.getStringExtra("message_body");
            notification_code = incomingIntent.getIntExtra("notification_code", Util.MESSAGE_A_NOTIFICATION);



            TextView t = (TextView) findViewById(R.id.messageText);
            t.setText(message_body);
            t.setTextColor(getResources().getColor(R.color.light_yellow));

        }

        Button b = (Button) findViewById(R.id.messageButtonOK);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendBroadcast(new Intent(notification_code==Util.MESSAGE_A_NOTIFICATION?Util.MESSAGE_CANCEL_A_NOTIFICATION:Util.MESSAGE_CANCEL_B_NOTIFICATION));
                startActivity(new Intent(Message.this, MapMyData.class));
                finish();
            }
        });
    }

    @Override
    protected void onResume() {

        super.onResume();

    }


}