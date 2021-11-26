package com.example.offlineapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private AppWebServices webServices;
    private SharedPreferences loginPref;
    SharedPreferences.Editor loginEditor;

    TextInputEditText userName, password;
    Button loginBtn;

    ArrayList<Integer> userID;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginPref = getSharedPreferences("loginPref", Context.MODE_PRIVATE);
        loginEditor = loginPref.edit();
        webServices = AppWebServices.web.create(AppWebServices.class);

        token = loginPref.getString("token", null);

        checkRunTimePermission();

        if(token!=null){
            Intent intent = new Intent(MainActivity.this, EventActivity.class);
            startActivity(intent);
        }

        userID = new ArrayList<>();

        userName = findViewById(R.id.userName);
        password = findViewById(R.id.password);

        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Call<LoginPojo> call = webServices.loginUser(userName.getText().toString(), password.getText().toString(), "3847a3f81ad2c19a");
                call.enqueue(new Callback<LoginPojo>() {
                    @Override
                    public void onResponse(Call<LoginPojo> call, Response<LoginPojo> response) {
                        if(response.isSuccessful()){
                            if(response.body().getToken()!=null){
                                String token = response.body().getToken();
                                int userIDSize = response.body().getUser().getIdd().getData().size();
                                for(int i=0;i<userIDSize-1;i++){
                                    userID.add(response.body().getUser().getIdd().getData().get(i));
                                }

                                loginEditor.putString("token",token);

                                Gson gson = new Gson();

                                String json = gson.toJson(userID);
                                loginEditor.putString("userID", json);
                                loginEditor.commit();

                                Intent intent = new Intent(MainActivity.this, EventActivity.class);
                                startActivity(intent);

                               // Toast.makeText(MainActivity.this, response.body().getUser().getId().getData()., Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<LoginPojo> call, Throwable t) {

                    }
                });
            }
        });
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
}