package com.example.vamoselectrica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.vamoselectrica.databinding.ActivityMapsBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    ImageView whitebg, letsgoelec, Scanqrimg;
    Button logoutbtn;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    static final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        logoutbtn=findViewById(R.id.logoutbtn);
        whitebg=findViewById(R.id.whitebg);
        letsgoelec=findViewById(R.id.letsgoelec);
        Scanqrimg=findViewById(R.id.Scanqrimg);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Scanqrimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                {
                    //Request Permission
                    //Check permission
                    if (ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.CAMERA) +
                            ContextCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                        // Permission not available
                        if (ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.CAMERA) ||
                                ActivityCompat.shouldShowRequestPermissionRationale(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                            //Create alert dialog if denied earlier
                            AlertDialog.Builder builder = new AlertDialog.Builder(
                                    MapsActivity.this
                            );
                            builder.setTitle("Please Grant Permissions");
                            builder.setMessage("To sign in, Camera and location permissions are required");
                            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ActivityCompat.requestPermissions(
                                            MapsActivity.this,
                                            new String[]{
                                                    Manifest.permission.CAMERA,
                                                    Manifest.permission.ACCESS_FINE_LOCATION
                                            },
                                            REQUEST_CODE
                                    );
                                }
                            });
                            builder.setNegativeButton("Cancel", null);
                            AlertDialog alertDialog = builder.create();
                            alertDialog.show();
                        }
                        //Asking for permission first time
                        else {
                            ActivityCompat.requestPermissions(
                                    MapsActivity.this,
                                    new String[]{
                                            Manifest.permission.CAMERA,
                                            Manifest.permission.ACCESS_FINE_LOCATION
                                    },
                                    REQUEST_CODE
                            );
                        }
                    }
                    //If permission granted
                    else
                    {
                        startActivity(new Intent(getApplicationContext(), MainActivity2.class));
                        finish();
                    }
                }
            }
        });

        //Signout:
        logoutbtn.setOnClickListener(new View.OnClickListener() {@Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(),SupportScreen.class));
                finish();
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                // Add a marker in Sydney and move the camera
                LatLng pt1 = new LatLng(23.250319624328363, 77.52649294439303);
                LatLng pt2 = new LatLng(23.250745308738832, 77.52344104221238);

                mMap.addMarker(new MarkerOptions().position(pt1).title("Stand 1"));
                mMap.addMarker(new MarkerOptions().position(pt2).title("Stand 2"));

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(pt1,15f));
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(pt1));
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        int flag=1;
        Intent intent = new Intent(MapsActivity.this, Scanner.class);
        Log.d("perreq","onreq called");
        if(requestCode==REQUEST_CODE) {
            Log.d("perreq","onreq runn");
            for (int i = 0; i < grantResults.length; ++i) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Log.d("perreq", "onreq success" + grantResults.length + ":" + grantResults[i]);
                }
                else
                    flag=0;
            }
            if(flag==1)
                startActivity(intent);
            else
                Toast.makeText(this, "Please allow the the required permissions in App settings", Toast.LENGTH_SHORT).show();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


}
