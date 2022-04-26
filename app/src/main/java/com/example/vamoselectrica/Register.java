package com.example.vamoselectrica;

import  androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    public static final String TAG = "TAG";
    EditText editText3, editText4, editText5, editText6, editText7;
    TextView textView12, textView13, textView14, textView15, textView16;
    ImageView Registergreyrect, Signupimg, TncPnP, Contimg;

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String userID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        editText3=findViewById(R.id.editText3);
        editText4=findViewById(R.id.editText4);
        editText5=findViewById(R.id.editText5);
        editText6=findViewById(R.id.editText6);
        editText7=findViewById(R.id.editText7);

        textView12=findViewById(R.id.textView12);
        textView13=findViewById(R.id.textView13);
        textView14=findViewById(R.id.textView14);
        textView15=findViewById(R.id.textView15);
        textView16=findViewById(R.id.textView16);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        Registergreyrect=findViewById(R.id.Registergreyrect);
        Signupimg=findViewById(R.id.Signupimg);
        Contimg=findViewById(R.id.Contimg);
        TncPnP=findViewById(R.id.TnpPnp);

        Contimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Extracting Data from the form
                String fname=editText6.getText().toString();
                String email=editText7.getText().toString();
                String pass=editText4.getText().toString();
                String confpass=editText5.getText().toString();
                String phoneno=editText3.getText().toString();

                if(fname.isEmpty()){
                    editText6.setError("Full Name is Required");
                    return;
                }
                if(email.isEmpty()){
                    editText7.setError("E-mail is Required");
                    return;
                }

                if(pass.isEmpty()){
                    editText4.setError("Password is Required");
                    return;
                }

                if(confpass.isEmpty()){
                    editText5.setError("Re-type the Password");
                    return;
                }

                if(phoneno.isEmpty()){
                    editText3.setError("Full Name is Required");
                    return;
                }
                if(!pass.equals(confpass)){
                    editText5.setError("Passwords do not match");
                }

                //dataisvalidated
                //registeruserusingfirebase

                //Toast.makeText(Register.this, "Data Validated!", Toast.LENGTH_SHORT).show();

                fAuth.createUserWithEmailAndPassword(email,pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {

                        userID=fAuth.getCurrentUser().getUid();
                        DocumentReference documentReference=fstore.collection("Users").document(userID);
                        //Saving data using HashMap
                        Map<String,Object> user = new HashMap<>();
                        user.put("Full Name",fname);
                        user.put("Email",email);
                        user.put("Phone no.",phoneno);

                        documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d(TAG, "User Profile is created for " + userID );
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: "+ e.toString());

                            }
                        });

                        //Send user to next page
                        startActivity(new Intent(getApplicationContext(),MapsActivity.class));
                        finish();
                    }

                    }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


            }
        });
    }
}