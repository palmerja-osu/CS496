package com.androidui.georgew.sqlitegps;

import android.*;
import android.Manifest;
import android.app.Dialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.provider.BaseColumns;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tagmanager.TagManagerApiImpl;

import static android.R.id.input;

public class MainActivity extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    SQLiteDB sqLiteDB;
    Button getWord;
    Cursor sqlCursor;
    SimpleCursorAdapter sqlCursorAdaptor;
    SQLiteDatabase mySQLDB;
    private static final String TAG = "MainActivity";
    private GoogleApiClient googleApiClient;
    private Location myLocation;
    private LocationListener locationListener;
    private LocationRequest locationRequest;
    private TextView latitude;
    double default_longitude = -123.2;
    double default_latitude = 44.5;
    private TextView longitude;
    private static final int LOCATION_PERMISSION_RESULT = 17;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        //set up textviews
        latitude = (TextView) findViewById(R.id.lat_coordinate);
        longitude = (TextView) findViewById(R.id.long_coordinate);
        //set location request settings
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(5000);
        //set location listener
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if (location != null) {
                    longitude.setText(String.valueOf(location.getLongitude()));
                    latitude.setText(String.valueOf(location.getLatitude()));
                } else {
                    longitude.setText("No Location Avaliable");
                }
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                CharSequence text = "Location Changed";
                Toast toast = Toast.makeText(context, text, duration);
                toast.show();
            }
        };
        //activate instance of sqlite db
        sqLiteDB = new SQLiteDB(this);
        //make writeable
        mySQLDB = sqLiteDB.getWritableDatabase();

        //set up button for user inputs/update sqlite db
        getWord = (Button) findViewById(R.id.getWord);
        getWord.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(mySQLDB != null){
                    //add current values to db if db exists
                    ContentValues vals = new ContentValues();
                    EditText curInput = (EditText)findViewById(R.id.input_word);
                    String userInput;
                    //check for input in input field
                    if(curInput.getText().toString() != null){
                        userInput = curInput.getText().toString();
                    } else {
                        userInput = "None";
                    }
                    //fill out db row
                    vals.put(DBContract.myTable.COLUMN_NAME_WORD, userInput);
                    vals.put(DBContract.myTable.COLUMN_NAME_LONGITUDE, longitude.getText().toString());
                    vals.put(DBContract.myTable.COLUMN_NAME_LATITUDE, latitude.getText().toString());
                    mySQLDB.insert(DBContract.myTable.TABLE_NAME, null, vals);
                    makeToast(userInput);
                } else {
                    Log.d(TAG, "Unable to access DB for writing");
                }

                //repopulate table
                populateTable();
            }
        });
        //populate table on load
        populateTable();
    }

    private void makeToast(String toastText){
        //notify user
        Context context = getApplicationContext();
        int duration = Toast.LENGTH_SHORT;
        CharSequence text =  toastText + " Added";
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    //load data from the database
    private void populateTable(){
        if (mySQLDB != null){
            try {
                //close cursor adaptor if it exists and is open
                if(sqlCursorAdaptor != null && sqlCursorAdaptor.getCursor() != null){
                    if(!sqlCursorAdaptor.getCursor().isClosed()){
                        sqlCursorAdaptor.getCursor().close();
                    }
                }
                //create fields to add to table
                String[] columns = new String[]{DBContract.myTable._ID,
                        DBContract.myTable.COLUMN_NAME_WORD,
                        DBContract.myTable.COLUMN_NAME_LONGITUDE,
                        DBContract.myTable.COLUMN_NAME_LATITUDE};
                //set up query
                sqlCursor = mySQLDB.query(DBContract.myTable.TABLE_NAME,
                        columns, null, null, null, null, null);
                //create list
                ListView coordinate_list = (ListView) findViewById(R.id.coordinate_list);
                //set up fields for input into list view adaptor
                String [] fields = new String[]{DBContract.myTable.COLUMN_NAME_LATITUDE, DBContract.myTable.COLUMN_NAME_LONGITUDE, DBContract.myTable.COLUMN_NAME_WORD};
                sqlCursorAdaptor = new SimpleCursorAdapter(this,
                        R.layout.coordinate_list,
                        sqlCursor,
                        fields,
                        new int[]{R.id.word_list, R.id.longitude_list, R.id.latitude_list},
                        0);
                //set coordinate list to sql cursor
                coordinate_list.setAdapter(sqlCursorAdaptor);
            } catch (Exception e) {
                Log.d(TAG, "Unable to load data from database");
            }
        }
    }
    //connect to API
    @Override
    protected void onStart() {
        googleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    //check for permissions, if granted then update location
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSION_RESULT);
            longitude.setText("-123.2");
            latitude.setText("44.5");
            //myLocation.setLongitude(default_longitude);
            //myLocation.setLatitude(default_latitude);
            return;
        }
        updateLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    //return error if connection is failed
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Dialog errorDialog = GoogleApiAvailability.getInstance().getErrorDialog(this, connectionResult.getErrorCode(), 0);
        errorDialog.show();
        return;
    }

    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_RESULT) {
            if (grantResults.length > 0) {
                updateLocation();
            }
        }
    }

    private void updateLocation() {
        //if permission is not granted return
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            longitude.setText("-123.2");
            latitude.setText("44.5");
            return;
        }
        //get last location from google api
        //myLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        //if location is valid set text fields of ui to longitude and latitude
        if (myLocation != null) {
            longitude.setText(String.valueOf(myLocation.getLongitude()));
            latitude.setText(String.valueOf(myLocation.getLatitude()));
        } else {
            //use location services to find location
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);
        }
    }
}


class SQLiteDB extends SQLiteOpenHelper{

    public SQLiteDB(Context context){
        super(context, DBContract.myTable.DB_NAME, null, DBContract.myTable.DB_VERSION);
    }

    //create table
    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(DBContract.myTable.SQL_CREATE_TABLE);
    }
    //drop old table and update with new version
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL(DBContract.myTable.SQL_DROP_TABLE);
        onCreate(db);
    }

}

//class to make inserts, queries easier to manage
final class DBContract{
    private DBContract(){};

    public final class myTable implements BaseColumns{
        public static final String DB_NAME = "Coordinates";
        public static final String TABLE_NAME = "Coordinate_Table";
        public static final String COLUMN_NAME_LONGITUDE = "Longitude";
        public static final String COLUMN_NAME_LATITUDE = "Latitude";
        public static final String COLUMN_NAME_WORD = "Word";
        public static final int DB_VERSION = 11;

        public static final String SQL_CREATE_TABLE = "CREATE TABLE " +
                myTable.TABLE_NAME + "(" + myTable._ID + " INTEGER PRIMARY KEY NOT NULL," +
                myTable.COLUMN_NAME_WORD + " VARCHAR(255)," +
                myTable.COLUMN_NAME_LONGITUDE + " VARCHAR(255)," +
                myTable.COLUMN_NAME_LATITUDE + " VARCHAR(255))";

        public static final String SQL_DROP_TABLE = "DROP TABLE IF EXISTS " + myTable.TABLE_NAME;
    }
}