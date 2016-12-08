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

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ALoggerMainActivity extends ActionBarActivity {

    int counter = 0;

    String demoModeString = null;
    String lastEventString = null;
    String lastServerContact = null;

    int taskCounter = 1;
    boolean taskIsActive = false;

    private TextView accountStatus;

    //Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private static final String TAG = "EmailPassword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alogger_main);
        accountStatus = (TextView) findViewById(R.id.textSignInStatusView);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
            }
        };
        Intent intent = new Intent(android.provider.Settings.ACTION_ACCESSIBILITY_SETTINGS);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onResume() {
        super.onResume();
        signIn("tsungwei50521@hotmail.com", "801020");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        signOut();
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
//            String urlString="http://twho-test-server.sandcats.io:6080/signup/PA6Bune28MmpduTka";
//            Intent intent=new Intent(Intent.ACTION_VIEW, Uri.parse(urlString));
//            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.setPackage("com.android.chrome");
//            try {
//                this.startActivity(intent);
//            } catch (ActivityNotFoundException ex) {
//                // Chrome browser presumably not installed so allow user to choose instead
//                intent.setPackage(null);
//                this.startActivity(intent);
//            }
            Intent intent=Intent.makeMainSelectorActivity(Intent.ACTION_MAIN,
                    Intent.CATEGORY_APP_GALLERY);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//Min SDK 15
            startActivity(intent);
//            try {
//                this.startActivity(intent);
//            } catch (ActivityNotFoundException noSuchActivity) {
//                Log.e(TAG, noSuchActivity.getMessage());
//            }
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

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

//        showProgressDialog();

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        if (!task.isSuccessful()) {
                            accountStatus.setText("Sign in failed.");
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                        } else {
                            accountStatus.setText("Sign in successful!");
                        }
//                        hideProgressDialog();
                    }
                });
    }

    private void signOut() {
        mAuth.signOut();
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
