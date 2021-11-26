package com.example.offlineapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReceiver extends BroadcastReceiver {

    private FusedLocationProviderClient fusedLocationClient;
    private AppWebServices webServices;
    private SharedPreferences loginPref;
    private SharedPreferences latlngPref;

    String token;

    String lat, lng;

    CountDownTimer timer;
    boolean onceHit = false, calledOnce = true;
    int latListSize = 0, lngListSize = 0;
    long mili = 30000;

    @Override
    public void onReceive(Context context, Intent intent) {

        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean status = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        //Toast.makeText(context, ""+isConnected, Toast.LENGTH_SHORT).show();

       // String status = NetworkUtil.getConnectivityStatusString(context);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        loginPref = context.getSharedPreferences("loginPref", Context.MODE_PRIVATE);
       // latlngPref = context.getSharedPreferences("latlngPref", Context.MODE_PRIVATE);
        token = loginPref.getString("token", null);

        /*lat = latlngPref.getString("lat" ,"");
        lng = latlngPref.getString("lng" ,"");*/

        latListSize = LocationService.latList.size();
        lngListSize = LocationService.lngList.size();

        webServices = AppWebServices.web.create(AppWebServices.class);

       // Toast.makeText(context, status, Toast.LENGTH_LONG).show();
       // Log.v("key",status);


        hitOfflineAPI(status, context);

       /* if(calledOnce){
            calledOnce = false;
            timer = new CountDownTimer(mili, 30000) {
                public void onTick(long millisUntilFinished) {
                    if(onceHit) {
                        onceHit = false;
                        hitOfflineAPI(finalStatus, context);
                    }
                    //  Toast.makeText(MainActivity.this, ""+millisUntilFinished/1000, Toast.LENGTH_SHORT).show();
                }

                public void onFinish() {
                    timer.start();
                    onceHit = true;
                }
            }.start();
        }
*/
    }

    /*public void hitAPI(String status, Context context){
        if(status.equals("true")){

            Call<EventPojo> call = webServices.event("770f2070-f62e-11ea-bb42-cf321c2b2880", "gps", lng, lat, "770f2070-f62e-11ea-bb42-cf321c2b2880", "Bearer "+ token);
            call.enqueue(new Callback<EventPojo>() {
                @Override
                public void onResponse(Call<EventPojo> call, Response<EventPojo> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getEvent() != null) {
                            Toast.makeText(context, "DONE", Toast.LENGTH_SHORT).show();
                            Log.v("key","DONEE");
                        }
                    }
                }

                @Override
                public void onFailure(Call<EventPojo> call, Throwable t) {

                }
            });

        }else if(status.equals("false")){

        }
    }*/

    public void hitOfflineAPI(boolean status, Context context){
        if(status){

            for (int i = 0; i<LocationService.latList.size(); i++){

                String lat = LocationService.latList.get(i).toString();
                String lng = LocationService.lngList.get(i).toString();

                Call<EventPojo> call = webServices.event("770f2070-f62e-11ea-bb42-cf321c2b2880", "gps", lng, lat, "770f2070-f62e-11ea-bb42-cf321c2b2880", "Bearer "+ token);
                call.enqueue(new Callback<EventPojo>() {
                    @Override
                    public void onResponse(Call<EventPojo> call, Response<EventPojo> response) {
                        if (response.isSuccessful()) {
                            if (response.body().getEvent() != null) {
                               Toast.makeText(context, "DONE", Toast.LENGTH_SHORT).show();
                                Log.v("key","DONEE");
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EventPojo> call, Throwable t) {

                    }
                });


            }

            LocationService.lngList = new ArrayList<>();
            LocationService.latList = new ArrayList<>();

        }else if(!status){

        }
    }

}