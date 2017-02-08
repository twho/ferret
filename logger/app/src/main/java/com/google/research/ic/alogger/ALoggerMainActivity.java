/*******************************************************************************
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.google.research.ic.alogger;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class ALoggerMainActivity extends ActionBarActivity {

    int counter = 0;

    String demoModeString = null;
    String lastEventString = null;
    String lastServerContact = null;

    int taskCounter = 1;
    boolean taskIsActive = false;

    private TextView accountStatus;
    public static EditText edTurk, edTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alogger_main);
        accountStatus = (TextView) findViewById(R.id.textSignInStatusView);
        edTurk = (EditText) findViewById(R.id.ed_turk_id);
        edTask = (EditText) findViewById(R.id.ed_task_id);

        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 0);
    }

    public static String getTurkId(){
        return edTurk.getText().toString();
    }

    public static String getCurrentTaskId(){
        return edTask.getText().toString();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_alogger_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onClickButtons(View view) {
        DebugLogger.log("Sending Marker message");
        TextView status = (TextView) findViewById(R.id.textStatusView);
        Button startTaskButton = (Button) findViewById(R.id.startTaskButton);
        Button endTaskButton = (Button) findViewById(R.id.endTaskButton);
        Button startSessionButton = (Button) findViewById(R.id.startSessionButton);
        Button startAppButton = (Button) findViewById(R.id.startApp);
        if (view.equals(startTaskButton)) {
            status.setText("Clicked start task");
            if (!taskIsActive) {
                taskIsActive = true;
                updateButtons();
            }
        } else if (view.equals(endTaskButton)) {
            status.setText("Clicked end task");
            if (taskIsActive) {
                taskCounter++;
                taskIsActive = false;
                updateButtons();
            }
        } else if (view.equals(startSessionButton)) {
            status.setText("Started new session");

        } else if (view.equals(startAppButton)) {
            if ("".equalsIgnoreCase(edTask.getText().toString()) && "".equalsIgnoreCase(edTurk.getText().toString())) {
                AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Please make sure turk id and task id are not empty.");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                alertDialog.show();
                return;
            }

            edTask.setEnabled(false);
            edTurk.setEnabled(false);

            Intent intent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN,
                    Intent.CATEGORY_APP_GALLERY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Min SDK 15
            startActivity(intent);
        }
    }

    public void updateButtons() {
        Button startTaskButton = (Button) findViewById(R.id.startTaskButton);
        Button endTaskButton = (Button) findViewById(R.id.endTaskButton);

        if (taskIsActive) {
            startTaskButton.setEnabled(false);
            endTaskButton.setEnabled(true);
        } else {
            startTaskButton.setEnabled(true);
            endTaskButton.setEnabled(false);
        }
        startTaskButton.setText("Start task " + taskCounter);
        endTaskButton.setText("End Task " + taskCounter);

    }

    public void onOpenAccessibilitySettings(View view) {
        DebugLogger.log("Opening Accessibility Settings");
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 0);
    }

    public void updateDemoMode(String s) {

    }

    public void udpateLastEvent(String s) {

    }

    public void updateLastSeverContact(String s) {

    }

    public void updateStatusString() {

    }

}
