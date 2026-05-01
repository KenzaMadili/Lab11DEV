package com.example.localisationsmartphone;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private double latitude;
    private double longitude;
    private double altitude;
    private float accuracy;

    private RequestQueue requestQueue;
    private TextView tvInfo;

    private String insertUrl = "http://192.168.1.128/localisation/createPosition.php";
    private String getUrl = "http://192.168.1.128/localisation/getPositions.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvInfo = findViewById(R.id.tvInfo);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        Button btnGetPositions = findViewById(R.id.btnGetPositions);
        btnGetPositions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPositions();
            }
        });

        LocationManager locationManager =
                (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    }, 1);
            return;
        }

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                10000,
                10,
                new LocationListener() {

                    @Override
                    public void onLocationChanged(Location location) {

                        latitude = location.getLatitude();
                        longitude = location.getLongitude();
                        altitude = location.getAltitude();
                        accuracy = location.getAccuracy();

                        String msg = "Latitude : " + latitude +
                                "\nLongitude : " + longitude +
                                "\nAltitude : " + altitude +
                                "\nPrécision : " + accuracy + " m";

                        tvInfo.setText(msg);

                        Toast.makeText(getApplicationContext(),
                                msg, Toast.LENGTH_LONG).show();

                        addPosition(latitude, longitude);
                    }

                    @Override
                    public void onStatusChanged(String provider, int status, Bundle extras) {
                        String state = "";

                        if (status == LocationProvider.OUT_OF_SERVICE)
                            state = "OUT_OF_SERVICE";
                        else if (status == LocationProvider.TEMPORARILY_UNAVAILABLE)
                            state = "TEMPORARILY_UNAVAILABLE";
                        else if (status == LocationProvider.AVAILABLE)
                            state = "AVAILABLE";

                        Toast.makeText(getApplicationContext(),
                                "Statut : " + state,
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        Toast.makeText(getApplicationContext(),
                                "GPS activé",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        Toast.makeText(getApplicationContext(),
                                "GPS désactivé",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void addPosition(final double lat, final double lon) {

        StringRequest request = new StringRequest(
                Request.Method.POST,
                insertUrl,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(getApplicationContext(),
                                response,
                                Toast.LENGTH_SHORT).show();
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "Erreur serveur",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        ) {

            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                params.put("latitude", String.valueOf(lat));
                params.put("longitude", String.valueOf(lon));
                params.put("date_position", sdf.format(new Date()));

                String androidId = Settings.Secure.getString(
                        getContentResolver(),
                        Settings.Secure.ANDROID_ID
                );

                params.put("imei", androidId);

                return params;
            }
        };

        requestQueue.add(request);
    }

    private void getPositions() {
        StringRequest request = new StringRequest(
                Request.Method.GET,
                getUrl,

                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            StringBuilder sb = new StringBuilder();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject obj = jsonArray.getJSONObject(i);
                                sb.append("📍 Position ").append(i + 1).append("\n");
                                sb.append("Lat : ").append(obj.getString("latitude")).append("\n");
                                sb.append("Lon : ").append(obj.getString("longitude")).append("\n");
                                sb.append("Date : ").append(obj.getString("date_position")).append("\n");
                                sb.append("─────────────────\n");
                            }

                            tvInfo.setText(sb.toString());

                        } catch (JSONException e) {
                            tvInfo.setText("Erreur JSON : " + e.getMessage());
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(),
                                "Erreur serveur",
                                Toast.LENGTH_SHORT).show();
                    }
                }
        );

        requestQueue.add(request);
    }
}