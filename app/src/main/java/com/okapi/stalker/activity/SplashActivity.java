package com.okapi.stalker.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;

import com.google.firebase.messaging.FirebaseMessaging;
import com.okapi.stalker.R;
import com.okapi.stalker.data.MainDataBaseHandler;
import com.okapi.stalker.fcm.DeleteTokenService;
import com.okapi.stalker.service.CourseNotificationService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class SplashActivity extends Activity {
    private final static String DELIMITER = "\\#";
    private final static String OBJ_DELIMITER = "\\$";
    private final static String ARRAY_DELIMITER = "\\^";
    private final static String DB_VERSION_TAG = "db_version";
    private boolean needToUpdate = true;
    private SharedPreferences sharedPreferences;
    private ProgressDialog pDialog;
    private Integer lastDBVersion;
    private String android_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        android_id = Settings.Secure.getString(getContentResolver(),
                Settings.Secure.ANDROID_ID);
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        new LoadDatabase().execute();
        //startService(new Intent(this, DeleteTokenService.class));
    }
    /**
     * Background Async Task to Load all product by making HTTP Request
     * */
    class LoadDatabase extends AsyncTask<String, String, String> {
        boolean shouldItCheck;
        /**
         * Before starting background thread Show Progress Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(SplashActivity.this);
            pDialog.setMessage(getString(R.string.checking_for_update));
            pDialog.setMax(100);
            pDialog.setProgress(0);
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting database from url
         * */
        protected String doInBackground(String... args) {

            Integer db_version = sharedPreferences.getInt(DB_VERSION_TAG, 1);
            final String url = "http://185.118.140.5/android/db_version.php?device_id="+android_id;
            StringBuilder stringBuilder1 = new StringBuilder();
            URL db_Version = null;
            try {
                db_Version = new URL(url);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            BufferedReader in;
            try {
                in = new BufferedReader(
                        new InputStreamReader(
                                db_Version.openStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null)
                    stringBuilder1.append(inputLine);

                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try{
                lastDBVersion = Integer.parseInt(stringBuilder1.toString());
            }catch (Exception e){
                lastDBVersion = db_version;
            }
            // Eger veritabani guncel degilse
            if(lastDBVersion == db_version){
                needToUpdate = false;
            }
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putLong("last_update_check", System.currentTimeMillis());
            if(needToUpdate){
                publishProgress();
                String url2 = "http://185.118.140.5/android/all_database.php?device_id="+android_id;;
                StringBuilder stringBuilder = new StringBuilder();
                URL allDb = null;
                try {
                    allDb = new URL(url2);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    in = new BufferedReader(
                            new InputStreamReader(
                                    allDb.openStream()));
                    String inputLine;

                    while ((inputLine = in.readLine()) != null)
                        stringBuilder.append(inputLine);

                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                publishProgress();
                MainDataBaseHandler db = new MainDataBaseHandler(getBaseContext());
                db.thatseEnoughBitch(stringBuilder.toString());
                sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                editor.putInt(DB_VERSION_TAG, lastDBVersion != null ? lastDBVersion : -1);
                startService(new Intent(getBaseContext(), CourseNotificationService.class));
            }
            editor.commit();
            return null;
        }
        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
            pDialog.setMessage(getString(R.string.recreating_db));
            pDialog.setProgress(pDialog.getProgress()+20);
        }
        private boolean shouldItCheck(){
            Integer checkCycle = Integer.parseInt(sharedPreferences.getString("list_auto_update_check_cycle", "1"));
            Long lastUpdateCheck = sharedPreferences.getLong("last_update_check", 0);
            return System.currentTimeMillis() - lastUpdateCheck > checkCycle * 86400000;
        }

        /**
         * After completing background task Dismiss the progress dialog
         * **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
            Intent intent;
            if (sharedPreferences.contains("id")) {
                MainDataBaseHandler db = new MainDataBaseHandler(getBaseContext());
                String id = sharedPreferences.getString("id", "");
                if(db.getStudent(id).getName() != null){
                    intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.putExtra("key", sharedPreferences.getString("id", ""));
                }else{
                    intent = new Intent(getBaseContext(), LoginActivity.class);
                }
            }else{
                intent = new Intent(getBaseContext(), LoginActivity.class);
            }

            startActivity(intent);
            finish();

        }

    }
}
