package courserra.gorbel01.dailyselfie;

import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
    Comments are somewhat rushed sorry
 */

public class SelfieManagerActivity extends ListActivity {

    private SelfieAdapter sAdapter;

    static final int REQUEST_IMAGE_CAPTURE = 1;

    private static final long INITIAL_ALARM_DELAY = 2 * 60 * 1000L;
    protected static final long JITTER = 5000L;

    private String mCurrentPhotoPath;
    private String mCurrentPhotoName;

    private AlarmManager mAlarmManager;
    private Intent mNotificationReceiverIntent, mLoggerReceiverIntent;
    private PendingIntent mNotificationReceiverPendingIntent, mLoggerReceiverPendingIntent;

    private SelfieDBHelper mDBHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ACTIVITY_CREATE", "Activity onCreate...");
        mDBHelper = new SelfieDBHelper(this);
        sAdapter = new SelfieAdapter(this);
        setupAlarm();
        if (savedInstanceState != null) {
            mCurrentPhotoPath = savedInstanceState.getString("currentPhotoPath");
            mCurrentPhotoName = savedInstanceState.getString("currentPhotoName");
        }

        readInData();

        this.setListAdapter(sAdapter);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        clearDB();
        stopAlarm();
        super.onResume();
    }

    @Override
    protected void onPause() {
        insertSelfieData();
        fireAlarm();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mDBHelper.getWritableDatabase().close();
        super.onDestroy();
    }

    /*
        Setup/initialize fields for the alarm including the manager and intents
     */
    private void setupAlarm() {
        mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        mNotificationReceiverIntent = new Intent(SelfieManagerActivity.this,
                TakeSelfieAlarmNotificationReceiver.class);

        mNotificationReceiverPendingIntent = PendingIntent.getBroadcast(SelfieManagerActivity.this,
                0, mNotificationReceiverIntent, 0);

        mLoggerReceiverIntent = new Intent(SelfieManagerActivity.this, SelfieAlarmLoggerReceiver.class);

        mLoggerReceiverPendingIntent = PendingIntent.getBroadcast(SelfieManagerActivity.this,
                0, mLoggerReceiverIntent, 0);
    }

    /*
        Fire a repeating alarm
     */
    private void fireAlarm() {
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                mNotificationReceiverPendingIntent);

        // Set repeating alarm to fire shortly after previous alarm
        mAlarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + INITIAL_ALARM_DELAY
                        + JITTER,
                AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                mLoggerReceiverPendingIntent);
    }

    /*
        Stop the alarm whenever the activity is resumed (prevent notifications while using app)
     */
    private void stopAlarm() {
        // Cancel all alarms using mNotificationReceiverPendingIntent
        mAlarmManager.cancel(mNotificationReceiverPendingIntent);

        // Cancel all alarms using mLoggerReceiverPendingIntent
        mAlarmManager.cancel(mLoggerReceiverPendingIntent);
    }

    /*
        Read data values from SQL and add them as a BitmapWrapper to the adapter
        (called only in onCreate())
     */
    private void readInData() {
        Cursor c = readSelfieData();
        if (c != null && c.getCount() > 0) {
            c.moveToFirst();
            do {
                String path = c.getString(c.getColumnIndexOrThrow(SelfieDBHelper.PATH));
                String name = c.getString(c.getColumnIndexOrThrow(SelfieDBHelper.NAME));
                BitmapWrapper bmw = new BitmapWrapper(path, name);
                sAdapter.add(bmw);
            } while (c.moveToNext());
            c.close();
        }
    }

    /*
        Write selfie data to the SQL file. Called in onPause()
     */
    private void insertSelfieData() {

        ContentValues values = new ContentValues();

        for (int i = 0; i < sAdapter.getCount(); i++) {
            values.put(SelfieDBHelper.PATH, sAdapter.getItem(i).getFilePath());
            values.put(SelfieDBHelper.NAME, sAdapter.getItem(i).getDisplayName());
            mDBHelper.getWritableDatabase().insert(SelfieDBHelper.TABLE_NAME, null, values);
            values.clear();
        }
    }

    /*
        Obtains a cursor representing the DB structure defined in
         SelfieDBHelper for use in the readInData() method.
     */
    private Cursor readSelfieData() {
        return mDBHelper.getWritableDatabase().query(SelfieDBHelper.TABLE_NAME,
                SelfieDBHelper.columns, null, new String[]{}, null, null, null);
    }

    // Delete all records
    private void clearDB() {
        mDBHelper.getWritableDatabase().delete(SelfieDBHelper.TABLE_NAME, null, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_photo : {
                if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
                    dispatchTakePictureIntent();
                }
                break;
            }
            case (R.id.action_delete) : {
                deleteAllPhotos();
                break;
            }
        }
        //noinspection SimplifiableIfStatement
        return super.onOptionsItemSelected(item);
    }

    /*
        Send an intent that opens the camera and creates a file from the photo taken
        in the directory provided in createImageFIle()
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.UK).format(new Date());
        String imageFileName = "JPEG_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        mCurrentPhotoName = imageFileName;
        return image;
    }

    /*
        Upon receiving the result, make appropriate checks and package data into bitmap wrapper
        add this to the adapter
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                Log.d("INTENT_RECEIVED", "Data Intent Received");
                BitmapWrapper bmw = new BitmapWrapper();
                bmw.setFilePath(mCurrentPhotoPath);
                Log.d("PATH_RESULT", bmw.getFilePath());
                bmw.setDisplayName(mCurrentPhotoName);
                sAdapter.add(bmw);
            }
            else {
                Toast.makeText(getApplicationContext(), "No picture was taken", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*
        Must store these values before taking a photo, otherwise cannot create the bitmap wrapper
        afterwards if display is rotated (class is recreated and thus the fields are reset)
     */
    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("currentPhotoPath", mCurrentPhotoPath);
        savedInstanceState.putString("currentPhotoName", mCurrentPhotoName);
    }

    /*
        Removes all photots (or atleast it should) does the job, but not always in one press
     */
    private void deleteAllPhotos() {
        sAdapter.removeAll();
    }
}
