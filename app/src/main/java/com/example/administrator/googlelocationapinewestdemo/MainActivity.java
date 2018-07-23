package com.example.administrator.googlelocationapinewestdemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 1000;
    private String[] requestPermissionStrings = {Manifest.permission.ACCESS_FINE_LOCATION};
    private TextView longitudeTextView, latitudeTextView;
    private Button startUpdatesButton, stopUpdatesButton;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeView();

        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this, requestPermissionStrings, REQUEST_CODE);
        } else {

            /** 初始化請求與回調 */
            buildLocationRequest();
            buildLocationCallback();

            startUpdatesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /** 確認如果沒有取得相關權限, 會先跳出並請求權限 */
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, requestPermissionStrings, REQUEST_CODE);
                        return;
                    }

                    /** 取得當前客戶端, 並請求取得位置更新的數據 */
                    fusedLocationProviderClient =
                            LocationServices.getFusedLocationProviderClient(MainActivity.this);
                    fusedLocationProviderClient.requestLocationUpdates(
                            locationRequest,
                            locationCallback,
                            Looper.myLooper()                                                        // 获取当前线程绑定的Looper, 如果没有返回null
                    );
                    startUpdatesButton.setEnabled(false);
                    stopUpdatesButton.setEnabled(true);
                }
            });

            stopUpdatesButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    /** 確認如果沒有取得相關權限, 會先跳出並請求權限 */
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, requestPermissionStrings, REQUEST_CODE);
                        return;
                    }

                    /** 結束對於位置更新的請求 */
                    fusedLocationProviderClient.removeLocationUpdates(locationCallback);
                    stopUpdatesButton.setEnabled(false);
                    startUpdatesButton.setEnabled(true);
                }
            });
        }
    }

    /**
     * 初始化View
     */
    private void initializeView() {
        longitudeTextView = findViewById(R.id.longitudeTextView);
        latitudeTextView = findViewById(R.id.latitudeTextView);
        startUpdatesButton = findViewById(R.id.startUpdatesButton);
        stopUpdatesButton = findViewById(R.id.stopUpdatesButton);
    }

    /**
     * 創建請求並設置時間間隔
     */
    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(5000);                                                          // Desired interval for active location updates, it is inexact and you may not receive upates at all if no location servers are available
        locationRequest.setFastestInterval(3000);                                                   // Interval is exact and app will never receive updates faster than this value
        locationRequest.setSmallestDisplacement(10);
    }

    /**
     * 創建一個可以獲得位置更新的回調
     */
    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                for (Location location : locationResult.getLocations()) {
                    longitudeTextView.setText("Longitude: " + location.getLongitude());
                    latitudeTextView.setText("Latitude: " + location.getLatitude());
                }
            }
        };
    }

    /**
     * 監聽是否成功取得權限, 以及想做些什麼的對應行為
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_CODE:
                if (grantResults.length > 0) {
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, "PERMISSION_GRANTED", Toast.LENGTH_SHORT).show();
                    } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(this, "PERMISSION_DENIED", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}
