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
import android.text.InputType;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginScreen extends AppCompatActivity {

    EditText editText1, editText2,editText25;
    TextView textView9,textView10, textView11;
    ImageView loginimg, Logingreyrect, TncPnP, Contimg;
    ProgressBar progressBar2;
    FirebaseAuth firebaseAuth;
    String peremail;

    static final int REQUEST_CODE = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        editText1=findViewById(R.id.editText1);
        editText2=findViewById(R.id.editText2);

        textView9=findViewById(R.id.textView9);
        textView10=findViewById(R.id.textView10);
        textView11=findViewById(R.id.textView11);
        loginimg=findViewById(R.id.loginimg);
        //Logingreyrect=findViewById(R.id.Logingreyrect);
        //TncPnP=findViewById(R.id.TncPnP);
        Contimg=findViewById(R.id.Contimg);
        progressBar2=findViewById(R.id.progressBar2);

        firebaseAuth=FirebaseAuth.getInstance();

        //Login continue:
        Contimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //extract and validate
                if (editText1.getText().toString().isEmpty()) {
                    editText1.setError("Email is Required");
                    return;
                }
                if (editText2.getText().toString().isEmpty()) {
                    editText2.setError("Password is Required");
                    return;
                }
                //data is valid
                //LoginUser

                String email=editText1.getText().toString().trim();
                    editText1.setEnabled(false);
                    editText2.setEnabled(false);
                progressBar2.setVisibility(View.VISIBLE);
                Contimg.setVisibility(View.INVISIBLE);

                //trim to remove spaces
                firebaseAuth.signInWithEmailAndPassword(editText1.getText().toString().trim(), editText2.getText().toString().trim()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        //Login Is successful
                            //Send back data to support screen
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);

                            if(firebaseAuth.getCurrentUser().isEmailVerified()){
                                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                                finish();
                            }
                            else if(!firebaseAuth.getCurrentUser().isEmailVerified()) {
                                //Verify Email
                                Intent i = new Intent(getApplicationContext(), VerifyEmail.class);
                                peremail = firebaseAuth.getCurrentUser().getEmail();
                                i.putExtra("Mail",peremail);
                                startActivity(i);
                                finish();
                            }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginScreen.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar2.setVisibility(View.GONE);
                            editText1.setEnabled(true);
                            editText2.setEnabled(true) ;
                        Contimg.setVisibility(View.VISIBLE);
                    }
                });
            }
        });


        //Forgot Password
        textView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ForgotPass.class));
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