package com.apandroidcenter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.apandroidcenter.location.SingleShotLocationProvider;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*APIService.getUrls(getApplicationContext(), new JsonCallBack<CFAResponse>() {
            @Override
            public void onResponse(CFAResponse response) {
                Log.i("Android_Center", String.valueOf(response));
            }

            @Override
            public void onError(String msg, Exception e) {

            }
        });*/


        /*SingleShotLocationProvider.requestSingleUpdate(this,
                new SingleShotLocationProvider.LocationCallback() {
                    @Override
                    public void onNewLocationAvailable(SingleShotLocationProvider.GPSCoordinates location) {
                        Log.d("Location", "my location is " + location.latitude + ", " + location.longitude);
                    }
                });*/
    }
}