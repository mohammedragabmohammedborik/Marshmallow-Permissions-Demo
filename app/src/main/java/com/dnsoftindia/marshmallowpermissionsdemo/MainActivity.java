package com.dnsoftindia.marshmallowpermissionsdemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private static final int REQUEST_ACCESS_FINE_LOCATION = 1;
    private Button btnLocation;
    private TextView tvLocation;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;

    private void findViews() {
        btnLocation = (Button)findViewById( R.id.btnLocation );
        tvLocation = (TextView)findViewById( R.id.tvLocation );

        btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGoogleApiClient != null) {
                    if (mGoogleApiClient.isConnected()) {
                        getLocation();
                    } else {
                        Snackbar.make(tvLocation, "Google Play Services not ready!", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    Snackbar.make(tvLocation, "Google Play Services not available!", Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void getLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_ACCESS_FINE_LOCATION);

            return;
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLastLocation != null) {
            String msg = "Latitude: " + String.valueOf(mLastLocation.getLatitude()) + "\n"
                    + "Longitude: " + String.valueOf(mLastLocation.getLongitude());
            tvLocation.setText(msg);
        }else{
            Snackbar.make(tvLocation, "Location unavailable!", Snackbar.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // instantiate Google API client
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        findViews();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        getLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Snackbar.make(tvLocation, "Google Play Services not ready!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Snackbar.make(tvLocation, "Google Play Services connection failed!", Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_ACCESS_FINE_LOCATION: {
                // grantResults array will be empty if the user denies permission...
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Snackbar.make(tvLocation, "Permission granted!", Snackbar.LENGTH_LONG).show();
                    getLocation();

                } else {
                    Snackbar.make(tvLocation, "Permission denied!", Snackbar.LENGTH_LONG).show();
                    // you can also build a case with the user if the permission is important for the app.
                    AlertDialog.Builder builder = new AlertDialog.Builder(
                            MainActivity.this);
                    builder.setTitle("Marshmallow Permissions");
                    builder.setMessage("Location is important for the app to function " +
                            "and give you better results! Please grant location permission.");
                    builder.setPositiveButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    getLocation();
                                }
                            });
                    builder.show();
                }
                return;
            }

            // other permissions can be checked in Case statements
        }
    }
}
