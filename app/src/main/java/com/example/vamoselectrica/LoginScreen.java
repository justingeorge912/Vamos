package com.example.vamoselectrica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {
    EditText editText1, editText2;
    TextView textView9,textView10, textView11;
    ImageView loginimg, Logingreyrect, TncPnP, Contimg;
    FirebaseAuth firebaseAuth;

    static final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        firebaseAuth=FirebaseAuth.getInstance();

        editText1=findViewById(R.id.editText1);
        editText2=findViewById(R.id.editText2);

        textView9=findViewById(R.id.textView9);
        textView10=findViewById(R.id.textView10);
        textView11=findViewById(R.id.textView11);

        loginimg=findViewById(R.id.loginimg);
        Logingreyrect=findViewById(R.id.Logingreyrect);
        //TncPnP=findViewById(R.id.TncPnP);
        Contimg=findViewById(R.id.Contimg);



        Contimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //extract and validate
                if (editText1.getText().toString().isEmpty()) {
                    editText1.setError("Email is Required");
                    return;
                }

                if (editText2.getText().toString().isEmpty()) {
                    editText1.setError("Password is Required");
                    return;
                }
                //data is valid
                //LoginUser

                firebaseAuth.signInWithEmailAndPassword(editText1.getText().toString(),editText2.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Login Is successful

                        {
                            //Request Permission
                            //Check permission
                            if (ContextCompat.checkSelfPermission(LoginScreen.this, Manifest.permission.CAMERA) +
                                    ContextCompat.checkSelfPermission(LoginScreen.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                                // Permission not available
                                if (ActivityCompat.shouldShowRequestPermissionRationale(LoginScreen.this, Manifest.permission.CAMERA) ||
                                        ActivityCompat.shouldShowRequestPermissionRationale(LoginScreen.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                                    //Create alert dialog if denied earlier
                                    AlertDialog.Builder builder = new AlertDialog.Builder(
                                            LoginScreen.this
                                    );
                                    builder.setTitle("Please Grant Permissions");
                                    builder.setMessage("To sign in, Camera and location permissions are required");
                                    builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            ActivityCompat.requestPermissions(
                                                    LoginScreen.this,
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
                                //Asking for pemission first time
                                else {
                                    ActivityCompat.requestPermissions(
                                            LoginScreen.this,
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
                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                finish();
                            }
                        }



                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            startActivity(new Intent(getApplicationContext(),MapsActivity.class));
            finish();
        }
    }
}