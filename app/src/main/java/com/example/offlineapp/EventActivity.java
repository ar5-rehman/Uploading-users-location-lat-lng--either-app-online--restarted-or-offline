package com.example.offlineapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.Observer;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import android.Manifest;
import android.app.ActivityManager;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.navigation.NavigationView;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventActivity extends AppCompatActivity {

    private NotificationCompat.Builder mbuilder;
    private NotificationManager mnotice;
    private EventActivity activity;
    private SharedPreferences loginPref;
    private AppWebServices webServices;

    private PeriodicWorkRequest mPeriodicWorkRequest;

    private SharedPreferences latlngPref;
    SharedPreferences.Editor latlngEditor;

    ArrayList<Integer> userID;

    TextView latView, lngView;
    DrawerLayout drawer;
    Menu menu;
    String lat, lng;

    private FusedLocationProviderClient fusedLocationClient;

    Button send;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow (). addFlags (WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON // Keep the screen on
                | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED // The screen can also be displayed when the screen is locked
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON); // Light the screen when starting the Activity

        setContentView(R.layout.activity_event);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            Intent intent = new Intent();
            String packageName = getPackageName();
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);

            if (!pm.isIgnoringBatteryOptimizations(packageName)) {
                intent.setAction(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                startActivity(intent);
            }

        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.artist_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        webServices = AppWebServices.web.create(AppWebServices.class);

        loginPref = getSharedPreferences("loginPref", Context.MODE_PRIVATE);
        latlngPref = getSharedPreferences("latlngPref", Context.MODE_PRIVATE);
        latlngEditor = latlngPref.edit();

        token = loginPref.getString("token", null);

        latView = findViewById(R.id.lat);
        lngView = findViewById(R.id.lng);

        //checkRunTimePermission();

        activity = this;
        //for handling notification
        mbuilder = new NotificationCompat.Builder(this);
        mnotice = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if(item.getItemId()==R.id.nav_logout)
                {

                }

                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        getLocationService();

    }

    public void getLocationService(){
        if (!this.isLocationEnabled(this)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Looks like location is not enabled!");  // GPS not found
            builder.setMessage("Do you want to enable location service?"); // Want to enable?
            builder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialogInterface, int i) {

                    Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    activity.startActivity(intent);
                }
            });

            //if no - bring user to selecting Static Location Activity
            builder.setNegativeButton("no", new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(activity, "Please enable Location-based service / GPS", Toast.LENGTH_LONG).show();

                }

            });
            builder.create().show();
        }
    }

    public void getLocationLatLng(){
        if (isLocationEnabled(this)) {
            if (ActivityCompat.checkSelfPermission(EventActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(EventActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                return;
            }

            LocationRequest mLocationRequest = LocationRequest.create();
            mLocationRequest.setInterval(60000);
            mLocationRequest.setFastestInterval(5000);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationCallback mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    if (locationResult == null) {
                        return;
                    }
                    for (Location location : locationResult.getLocations()) {
                        if (location != null) {
                            if (location != null) {

                                lat = String.valueOf(location.getLatitude());
                                lng = String.valueOf(location.getLongitude());

                                latView.setText("Lat "+lat);
                                lngView.setText("Lng "+lng);

                                latlngEditor.putString("lat", lat);
                                latlngEditor.putString("lng", lng);
                                latlngEditor.commit();

                            }else{
                                Toast.makeText(activity, "null", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }
            };

            apiCall(lat, lng);

           // final OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(WorkerClass.class).build();


            LocationServices.getFusedLocationProviderClient(EventActivity.this).requestLocationUpdates(mLocationRequest, mLocationCallback, null);

           // WorkManager.getInstance().enqueue(workRequest);

            /*if(isMyServiceRunning(LocationService.class)){

            }else {
                Intent serviceIntent = new Intent(EventActivity.this, LocationService.class);
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                    startForegroundService(serviceIntent);
                } else {
                    startService(serviceIntent);
                }
            }*/

            if(isWorkScheduled("periodicWorkRequest")){

            }else {

                mPeriodicWorkRequest = new PeriodicWorkRequest.Builder(WorkerClass.class,
                        1, TimeUnit.MINUTES)

                        .addTag("periodicWorkRequest")
                        .build();

                WorkManager.getInstance().enqueueUniquePeriodicWork("PERIODIC", ExistingPeriodicWorkPolicy.KEEP,mPeriodicWorkRequest);
            }

        }else{

        }
    }



    private boolean isWorkScheduled(String tag) {
        WorkManager instance = WorkManager.getInstance();
        ListenableFuture<List<WorkInfo>> statuses = instance.getWorkInfosByTag(tag);
        try {
            boolean running = false;
            List<WorkInfo> workInfoList = statuses.get();
            for (WorkInfo workInfo : workInfoList) {
                WorkInfo.State state = workInfo.getState();
                running = state == WorkInfo.State.RUNNING | state == WorkInfo.State.ENQUEUED;
            }
            return running;
        } catch (ExecutionException e) {
            e.printStackTrace();
            return false;
        } catch (InterruptedException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void getUserID(){
        Gson gson = new Gson();
        String json = loginPref.getString("userID", null);

        Type type = new TypeToken<ArrayList<Integer>>() {}.getType();

        userID = gson.fromJson(json, type);

        if (userID != null) {
            int userIDSize = userID.size();
            Toast.makeText(EventActivity.this, ""+userIDSize, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean isLocationEnabled(Context mContext) {
        LocationManager locationManager = (LocationManager)
                mContext.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }


    public void checkRunTimePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED||
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {


            } else {
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        10);
            }
        } else {
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 10) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale((this) , Manifest.permission.ACCESS_FINE_LOCATION)) {
                    // If User Checked 'Don't Show Again' checkbox for runtime permission, then navigate user to Settings
                    AlertDialog.Builder dialog = new AlertDialog.Builder(this);
                    dialog.setTitle("Permission Required");
                    dialog.setCancelable(false);
                    dialog.setMessage("You have to Allow permission to access user location");
                    dialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package",
                                    getBaseContext().getPackageName(), null));
                            //i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivityForResult(i, 1001);
                        }
                    });
                    AlertDialog alertDialog = dialog.create();
                    alertDialog.show();
                }
                //code for deny
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getLocationLatLng();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        getLocationLatLng();
        finishAffinity();
    }

    public void apiCall(String lat, String lng){

            Call<EventPojo> call = webServices.event("770f2070-f62e-11ea-bb42-cf321c2b2880", "gps", lng, lat, "770f2070-f62e-11ea-bb42-cf321c2b2880", "Bearer "+ token);
            call.enqueue(new Callback<EventPojo>() {
                @Override
                public void onResponse(Call<EventPojo> call, Response<EventPojo> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getEvent() != null) {
                         //   Toast.makeText(EventActivity.this, "DONE", Toast.LENGTH_SHORT).show();
                            Log.v("key","DONEE");
                        }
                    }
                }

                @Override
                public void onFailure(Call<EventPojo> call, Throwable t) {

                }
            });

    }
}