package com.example.roadsafety20;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.provider.Settings;
import android.telephony.SmsManager;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.ktx.Firebase;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    FirebaseAuth auth;
    Button button;
    TextView textView;
    String userName;
    private EditText startLocationEditText;
    private EditText endLocationEditText;
    private String emergencyPhoneNumber;
//    private EditText emergencyPhoneNumberEditText;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private CountDownTimer timer;
    private LocationManager locationManager;
    private static final int REQUEST_LOCATION = 1;
    private boolean isAccidentDetected = false;
    private TextView accelerometerTextView, gyroscopeTextView;

    @Override
    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;


        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            double acceleration = Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);


            if (acceleration > 20.0) {

                isAccidentDetected = true;
            }
        } else if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {

            double rotation = Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);


            if (rotation > 10.0) {
                isAccidentDetected = true;
            }
        }

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                double acceleration = Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
                double maxacc = 0.0;
                if(acceleration>maxacc){
                    maxacc = acceleration;
                }
                accelerometerTextView.setText(String.format("Accelerometer:\n %.2f", maxacc));
                break;
            case Sensor.TYPE_GYROSCOPE:
                double rotation = Math.sqrt(values[0] * values[0] + values[1] * values[1] + values[2] * values[2]);
                double maxrot = 0.0;
                if(rotation>maxrot){
                    maxrot = rotation;
                }
                gyroscopeTextView.setText(String.format("Gyroscope:\n %.2f", maxrot));
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        auth = FirebaseAuth.getInstance();
        button = findViewById(R.id.logout);
        textView = findViewById(R.id.user_details);
        String userID = auth.getCurrentUser().getUid();
        DatabaseReference reference;
        reference  = FirebaseDatabase.getInstance().getReference("Users");

        DatabaseReference user = reference.child("users").child(userID);
        DatabaseReference username = user.child("userName");

        if (user == null){
            Intent intent = new Intent(getApplicationContext(), Login.class);
            startActivity(intent);
            finish();
        }
        else {
            textView.setGravity(Gravity.CENTER);
            username.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    userName = snapshot.getValue(String.class);
                    textView.setText("Welcome! " + userName);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), Login.class);
                startActivity(intent);
                finish();
            }
        });

        startLocationEditText = findViewById(R.id.start_location);
        endLocationEditText = findViewById(R.id.end_location);
        Button startNavigationButton = findViewById(R.id.start_navigation);
//        emergencyPhoneNumberEditText = findViewById(R.id.emergency_phone_number);
        Button saveEmergencyPhoneNumberButton = findViewById(R.id.save_emergency_phone_number);
        accelerometerTextView = findViewById(R.id.accelerometerTextView);
        gyroscopeTextView = findViewById(R.id.gyroscopeTextView);

        startNavigationButton.setOnClickListener(v -> {
            String startLocation = startLocationEditText.getText().toString();
            String endLocation = endLocationEditText.getText().toString();

            if (!startLocation.isEmpty() && !endLocation.isEmpty() ) {
                String url = "https://www.google.com/maps/dir/?api=1&origin=" + startLocation + "&destination=" + endLocation + "&travelmode=driving";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);
            } else if(startLocation.isEmpty() && !endLocation.isEmpty()){
                String url = "https://www.google.com/maps/dir/?api=1&origin=" + "&destination=" + endLocation + "&travelmode=driving";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setPackage("com.google.android.apps.maps");
                startActivity(intent);

            }else{
                Toast.makeText(MainActivity.this, "Please enter start and end locations", Toast.LENGTH_SHORT).show();
            }
        });

        saveEmergencyPhoneNumberButton.setOnClickListener(v -> {
            DatabaseReference phoneref = user.child("mobileNumber");
            phoneref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    emergencyPhoneNumber = snapshot.getValue(String.class);
                }
                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
//            emergencyPhoneNumberEditText = findViewById(R.id.emergency_phone_number);
//            emergencyPhoneNumber = emergencyPhoneNumberEditText.getText().toString();
                sensorManager = ( SensorManager ) getSystemService(SENSOR_SERVICE);
                accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);


                sensorManager.registerListener(MainActivity.this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
                sensorManager.registerListener(MainActivity.this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);


                locationManager = ( LocationManager ) getSystemService(Context.LOCATION_SERVICE);

            if (isAccidentDetected) {
                timer = new CountDownTimer(10000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                        builder.setMessage("Are you alright? Press 'Yes' to cancel.")
                                .setCancelable(false)
                                .setPositiveButton("Yes", (dialog, which) -> {
                                    dialog.dismiss();
                                    timer.cancel();
                                    stopService(new Intent(MainActivity.this, MainActivity.class));
                                    isAccidentDetected = false;

                                });

                        AlertDialog alert = builder.create();
                        alert.show();
                    }

                    @Override
                    public void onFinish() {
                        stopService(new Intent(MainActivity.this, MainActivity.class));
                        if(isAccidentDetected){sendAlertMessage();
                        }}
                };
                timer.start();
            }
        });
    }

    private void sendAlertMessage() {
        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                String message = "Help! I have been in an accident.My current Location is https://www.google.com/maps/search/?api=1&query=" + latitude + "," + longitude;
                sendSMS(message);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            @Override
            public void onProviderEnabled(String provider) {
            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION);
        } else {
            locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null);
        }
    }
    private void sendSMS(String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(emergencyPhoneNumber, null, message, null, null);
            Toast.makeText(getApplicationContext(), "Alert message sent!", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Failed to send alert message!", Toast.LENGTH_LONG).show();
            ex.printStackTrace();
        }
    }
}