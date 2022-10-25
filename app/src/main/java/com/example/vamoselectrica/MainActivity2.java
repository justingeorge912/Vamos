package com.example.vamoselectrica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public class MainActivity2 extends AppCompatActivity {

    static final int BLUETOOTH_CONNECT_PERMISSION = 22;
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    public static final String DEVICE_EXTRA = "hello";
    private static final String TAG = "FadeMoveAct2";
    private LocationRequest locationRequest;
    public BluetoothSocket mySocket;
    String State = "STATE",t1,t2, userID;
    String[] location_det = new String[2];
    ConnectionService connectionService;
    BluetoothDevice mBTDevice;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    ImageView imageView12,imageView8;
    Button startRidebtn, endRidebtn, startRidebtn2,button2,button5;
    TextView textView2,textView25,textView27;
    FirebaseFirestore fstore;
    FirebaseAuth fauth;
    FusedLocationProviderClient fusedLocationProviderClient;

    Date currentTime1;
    Date currentTime2;

    Boolean writestat;

    Switch switch2;
    ProgressBar progressBar5,progressBar4;
    Map<String, Object> user = new HashMap<>();

    @SuppressLint("MissingPermission")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        IntentFilter filter5 = new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver5, filter5);

        /*IntentFilter filter6 = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver6, filter6);*/

        LocalBroadcastManager.getInstance(this).registerReceiver(threadReceiver, new IntentFilter("connectionstat"));

        switch2 = findViewById(R.id.switch2);
        textView2 = findViewById(R.id.textView2);
        textView25 = findViewById(R.id.textView25);
        textView27 = findViewById(R.id.textView27);
        textView27.setVisibility(View.INVISIBLE);

        progressBar4=findViewById(R.id.progressBar4);
        progressBar5=findViewById(R.id.progressBar5);
        imageView12=findViewById(R.id.imageView12);
        imageView12.setVisibility(View.INVISIBLE);
        imageView8=findViewById(R.id.imageView8);
        imageView8.setVisibility(View.INVISIBLE);


        startRidebtn=findViewById(R.id.startRideBtn);
        startRidebtn2=findViewById(R.id.startRideBtn2);
        endRidebtn=findViewById(R.id.endRideBtn);
        button5=findViewById(R.id.button5);
        startRidebtn.setVisibility(View.INVISIBLE);
        startRidebtn2.setVisibility(View.INVISIBLE);
        endRidebtn.setVisibility(View.INVISIBLE);
        textView2.setVisibility(View.INVISIBLE);
        imageView8.setVisibility(View.INVISIBLE);
        button2=findViewById(R.id.button2);
        switch2.setVisibility(View.INVISIBLE);
        button2.setVisibility(View.INVISIBLE);


        fauth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();



        ConnectionService connectionService = new ConnectionService(getApplicationContext());

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


        Intent intent = getIntent();
        mBTDevice = intent.getParcelableExtra(DEVICE_EXTRA);
        if (mBTDevice != null) {
            //Toast.makeText(this, "device received", Toast.LENGTH_SHORT).show();
        }
       // if (mBTDevice.getBondState() == BluetoothDevice.BOND_NONE)
            //mBTDevice.createBond();
        connectionService.startClient(mBTDevice,MY_UUID);


        switch2 = findViewById(R.id.switch2);
        switch2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (switch2.isChecked()) {
                    connectionService.twrite(0);
                    startRidebtn.setVisibility(View.VISIBLE);
                    endRidebtn.setVisibility(View.VISIBLE);
                }
                if (!switch2.isChecked()){
                    connectionService.twrite(1);
                    startRidebtn.setVisibility(View.INVISIBLE);
                    endRidebtn.setVisibility(View.INVISIBLE);
                }
            }
        });

        imageView12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.disable();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBluetoothAdapter.disable();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        });


        startRidebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentTime1 = Calendar.getInstance().getTime();
                 t1 = currentTime1.toString();
                Log.d(TAG, "time : "+ t1);
                Toast.makeText(MainActivity2.this, "DATE"+t1, Toast.LENGTH_SHORT).show();
                startRidebtn.setVisibility(View.INVISIBLE);
                startRidebtn2.setVisibility(View.VISIBLE);
                startRidebtn2.setEnabled(false);
                getStartLocation();
            }
        });

        endRidebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar4.setVisibility(View.VISIBLE);
                currentTime2 = Calendar.getInstance().getTime();
                t2 = currentTime2.toString();
                Log.d(TAG, "time : "+ t2);
                Toast.makeText(MainActivity2.this, "##DATE##"+t2, Toast.LENGTH_SHORT).show();
                Log.d(TAG,"hello "+t1+ " " +t2);
                getEndingLocation();
                Log.d(TAG,"Location Det "+ location_det[0]+" " + location_det[1]);
                startRidebtn.setVisibility(View.INVISIBLE);
                endRidebtn.setVisibility(View.INVISIBLE);
                progressBar4.setVisibility(View.VISIBLE);
                userID = fauth.getCurrentUser().getUid();



                DocumentReference documentReference1 = fstore.collection("Users").document(userID);
                String nm = fauth.getCurrentUser().getEmail();
                Log.d(TAG,"name :"+nm);


                String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                DocumentReference documentReference2 = fstore.collection("Rides").document(currentDate).collection("users").document(nm);

                user.put("End TimeStamp:",t2);
                user.put("Start TimeStamp:",t1);
                //user.put("End Lat","1235");
                //user.put("End Long","1256");
                String hel = (String) user.get("End Lat");
                String hel1 = (String) user.get("End Long");






                documentReference2.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"Store success");

                        Log.d(TAG,"Check "+hel+ " ******* " + hel1);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG,"Error : "+e);
                    }

                });

               // startActivity(new Intent(MainActivity2.this,FinishTrip.class));
               // finish();
            }
        });

        Log.d(TAG,"hello "+ t1+t2);

        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch2.setVisibility(View.VISIBLE);
                imageView12.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);
                startRidebtn.setVisibility(View.VISIBLE);
                endRidebtn.setVisibility(View.VISIBLE);
                progressBar5.setVisibility(View.INVISIBLE);
                textView25.setVisibility(View.INVISIBLE);
                textView27.setVisibility(View.INVISIBLE);
                imageView8.setVisibility(View.INVISIBLE);
                button2.setVisibility(View.INVISIBLE);
                button5.setVisibility(View.INVISIBLE);
            }
        });
    }


    private final BroadcastReceiver threadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String stat = intent.getStringExtra("Connstat");

            if(stat=="0"){
                //Toast.makeText(context, "Connection Failed! Try disconnecting all other bluetooth devices, and try again", Toast.LENGTH_SHORT).show();
                button2.setVisibility(View.VISIBLE);
                textView27.setVisibility(View.VISIBLE);
                imageView8.setVisibility(View.VISIBLE);
                progressBar5.setVisibility(View.INVISIBLE);
                textView25.setVisibility(View.INVISIBLE);
               // Intent i = new Intent(getApplicationContext(),MainActivity.class);
                //startActivity(i); //(disabled to check date time buttons)
            }
            if(stat=="1"){
                Toast.makeText(context, "Connection Successful", Toast.LENGTH_SHORT).show();
                switch2.setVisibility(View.VISIBLE);
                imageView12.setVisibility(View.VISIBLE);
                textView2.setVisibility(View.VISIBLE);

            }
            if(stat=="4"){
                Toast.makeText(context, "Transmission Error,try reconnecting again!", Toast.LENGTH_SHORT).show();
                mBluetoothAdapter.disable();
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
            }
        }

    };

    //Pairing reciever
    private final BroadcastReceiver mBroadcastReceiver5 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(MainActivity2.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity2.this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION);
                }
            }
            String action = intent.getAction();
            if (action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)) {
                 BluetoothDevice mDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                //3 Cases:
                //case 1: Bond created

                if (mDevice.getBondState() == BluetoothDevice.BOND_BONDED) {
                    //Toast.makeText(context, "Pairing Successful! Attempting Connection", Toast.LENGTH_SHORT).show();
                    //connectionService.startClient(mBTDevice,MY_UUID);
                }
                    //case 2: Creating Bond
                    if (mDevice.getBondState() == BluetoothDevice.BOND_BONDING) {
                        //Toast.makeText(context, "Pairing started", Toast.LENGTH_SHORT).show();
                    }
                    //case 3: Breaking bond
                    if (mDevice.getBondState() == BluetoothDevice.BOND_NONE) {
                        //Toast.makeText(context, "Device unPaired successfully", Toast.LENGTH_SHORT).show();
                        mDevice.createBond();
                    }

            }
        }
    };

    @Override
    protected void onDestroy() {
        unregisterReceiver(mBroadcastReceiver5);
        //unregisterReceiver(mBroadcastReceiver6);
        super.onDestroy();
    }

    private void turnOnGPS(){

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        builder.setAlwaysShow(true);

        Task<LocationSettingsResponse> result = LocationServices.getSettingsClient(getApplicationContext())
                .checkLocationSettings(builder.build());

        result.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {

                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                   // Toast.makeText(getApplicationContext(), "GPS is already turned on", Toast.LENGTH_SHORT).show();

                } catch (ApiException e) {

                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                            try {
                                ResolvableApiException resolvableApiException = (ResolvableApiException)e;
                                resolvableApiException.startResolutionForResult(MainActivity2.this,2);
                            } catch (IntentSender.SendIntentException ex) {
                                ex.printStackTrace();
                            }
                            break;

                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            //Device does not have location
                            break;
                    }
                }
            }
        });
    }

    private boolean isGPSEnabled(){
        LocationManager locationManager=null;
        boolean isEnabled = false;

        if(locationManager==null){
            locationManager=(LocationManager)getSystemService(Context.LOCATION_SERVICE);
        }
        isEnabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        return isEnabled;
    }

    @SuppressLint("MissingPermission")
    private void getStartLocation(){
        if(isGPSEnabled()) {
            String[] locdet = new String[2];
            fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {

                    Location location = task.getResult();
                    if (location != null) {
                        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                        try {

                            List<Address> addressList = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                            locdet[0] = String.valueOf(addressList.get(0).getLatitude());
                            locdet[1] = String.valueOf(addressList.get(0).getLongitude());
                            Log.d(TAG, "StartLat(func) : " + locdet[0] + " Long(func) : " + locdet[1]);
                            user.put("Start Lat", locdet[0]);
                            user.put("Start Long", locdet[1]);


                        } catch (IOException e) {
                            Log.d(TAG, "error : " + e);
                        }

                    } else {
                        Log.d(TAG, "null location received");
                        Toast.makeText(MainActivity2.this, "Location error! Please enable location in settings", Toast.LENGTH_SHORT).show();
                    }
                }

            });
        }
        else{
            Toast.makeText(MainActivity2.this, "Location error! Please enable location in app settings", Toast.LENGTH_SHORT).show();
            turnOnGPS();
        }

    }

    @SuppressLint("MissingPermission")
    private String[] getEndingLocation(){
        String[] locdet2= new String[2];
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task2) {

                Location location2=task2.getResult();
                if(location2!=null) {
                    Geocoder geocoder2 = new Geocoder(getApplicationContext(), Locale.getDefault());
                    try {
                        List<Address> addressList2=geocoder2.getFromLocation(location2.getLatitude(), location2.getLongitude(), 1);
                        locdet2[0]= String.valueOf(addressList2.get(0).getLatitude());
                        locdet2[1]= String.valueOf(addressList2.get(0).getLongitude());
                        Log.d(TAG,"End LAT : "+ locdet2[0]+" Long : " +locdet2[1] + "Put function done");
                        user.put("End Lat", locdet2[0]);
                        user.put("End Long", locdet2[1]);


                    }catch (IOException e){
                        Log.d(TAG,"error : "+e);
                    }

                }
                else{
                    Log.d(TAG,"null location received");
                    Toast.makeText(MainActivity2.this, "Location error! Please enable location in settings", Toast.LENGTH_SHORT).show();
                }
            }
        });
        Log.d(TAG,"Before Rturn End LAT : "+ locdet2[0]+" Long : " +locdet2[1] + "Put function done");
        return locdet2;

    }

    public void locstring(String[] loc) {
        Log.d(TAG,"*Received* " + "End LAT : "+ loc[0]+" Long : " +loc[1]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            user.putIfAbsent("End Lat", loc[0]);
            user.putIfAbsent("End Long", loc[1]);
            String hel = (String) user.get("End Lat");
            String hel1 = (String) user.get("End Long");
            Log.d(TAG, "(func)" + hel + " ******* " + hel1);
        }
        else {
            user.put("End Lat", loc[0]);
            user.put("End Long", loc[1]);
            String hel = (String) user.get("End Lat");
            String hel1 = (String) user.get("End Long");
            Log.d(TAG, "(func)" + hel + " ******* " + hel1);
        }
    }
}

