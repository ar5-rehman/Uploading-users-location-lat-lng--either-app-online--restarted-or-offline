package com.example.offlineapp;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

public class LocationService extends Service {
    public static final String BROADCAST_ACTION = "Hello World";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public Location previousBestLocation = null;

    private BroadcastReceiver MyReceiver = null;
    public MyLocationListener listener;

    Intent intent;
    int counter = 0;

    public static List<Double> latList;
    public static List<Double> lngList;

    @Override
    public void onCreate() {
        super.onCreate();
        latList = new ArrayList<>();
        lngList = new ArrayList<>();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private String createNotificationChannel(NotificationManager notificationManager) {
        String channelId = "myapp.mobile.offlineapp";
        String channelName = "My Foreground Service";

        NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
        // omitted the LED color
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_SECRET);
        notificationManager.createNotificationChannel(channel);

        return channelId;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            String channelId = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O ? createNotificationChannel(notificationManager) : "Apps Protected";
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId).setSmallIcon(R.drawable.ic_launcher_background).setContentTitle("Apps Protected").setContentText("Apps Protection is on. If you want to turn off protection go to app and remove it.").setStyle(new NotificationCompat.BigTextStyle().bigText("Apps Protection is on. If you want to turn off protection go to app and remove it.")).setPriority(NotificationCompat.PRIORITY_HIGH);
            Notification notification = notificationBuilder.setOngoing(true).setSmallIcon(R.drawable.ic_launcher_background).setContentInfo("Apps Protected. Apps Protection is on. If you want to turn off protection go to app and remove it.").setCategory(NotificationCompat.CATEGORY_SERVICE).build();
            startForeground(100, notification);
        }

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        listener = new MyLocationListener();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }

        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 4000, 0, (LocationListener) listener);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 4000, 0, listener);


        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

   /* public class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            double lat = location.getLatitude();
            double lng = location.getLongitude();

            latList.add(lat);
            lngList.add(lng);

            MyReceiver = new MyReceiver();
            registerReceiver(MyReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
        }*/


   /* public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }*/


    public class MyLocationListener implements LocationListener {

        public void onLocationChanged(final Location loc) {

            double lat = loc.getLatitude();
            double lng = loc.getLongitude();

            latList.add(lat);
            lngList.add(lng);

            MyReceiver = new MyReceiver();
            registerReceiver(MyReceiver,
                    new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        public void onProviderDisabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT).show();
        }


        public void onProviderEnabled(String provider) {
            Toast.makeText(getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

    }
}