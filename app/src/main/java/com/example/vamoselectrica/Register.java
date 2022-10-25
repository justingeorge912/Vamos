package com.example.vamoselectrica;

import  androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {

    public static final String TAG = "TAG";

    EditText editText3, editText4, editText5, editText6, editText7;
    TextView textView12, textView13, textView14, textView15, textView16;
    ImageView Registergreyrect, Signupimg, TncPnP, Contimg;
    String userID;
    ProgressBar progressBar1;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        Registergreyrect=findViewById(R.id.Registergreyrect);
        Signupimg=findViewById(R.id.Signupimg);
        Contimg=findViewById(R.id.Contimg);
        TncPnP=findViewById(R.id.TnpPnp);
        progressBar1=findViewById(R.id.progressBar1);
        progressBar1.setVisibility(View.INVISIBLE);
        Contimg.setVisibility(View.VISIBLE);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        //Capitalize first letter:
        editText6.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);
        editText7.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_SENTENCES);

        Contimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Extracting Data from the form
                String fname = editText6.getText().toString().trim();
                String email = editText7.getText().toString().trim();
                String pass = editText4.getText().toString().trim();
                String confpass = editText5.getText().toString().trim();
                String phoneno = editText3.getText().toString().trim();

                if (fname.isEmpty()) {
                    editText6.setError("Full Name is Required");
                    return;
                }
                if (email.isEmpty()) {
                    editText7.setError("E-mail is Required");
                    return;
                }
                if (pass.isEmpty()) {
                    editText4.setError("Password is Required");
                    return;
                }
                if (confpass.isEmpty()) {
                    editText5.setError("Re-type the Password");
                    return;
                }
                if (phoneno.isEmpty()) {
                    editText3.setError("Full Name is Required");
                    return;
                }
                if (phoneno.toString().length() != 10) {
                    editText3.setError("Phone no. should be of 10 digits");
                    return;
                }
                if (!pass.equals(confpass)) {
                    editText5.setError("Passwords do not match");
                    return;
                }
                //data is valida
                //register user using firebase
                //Progressbar
                if (pass.equals(confpass)) {
                    if (phoneno.toString().length() == 10){
                        editText3.setEnabled(false);
                    editText4.setEnabled(false);
                    editText5.setEnabled(false);
                    editText6.setEnabled(false);
                    editText7.setEnabled(false);
                    progressBar1.setVisibility(View.VISIBLE);
                    Contimg.setVisibility(View.INVISIBLE);

                    fAuth.createUserWithEmailAndPassword(email, pass).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            userID = fAuth.getCurrentUser().getUid();
                            DocumentReference documentReference = fstore.collection("Users").document(userID);
                            //Saving data using HashMap
                            Map<String, Object> user = new HashMap<>();
                            user.put("Full Name", fname);
                            user.put("Email", email);
                            user.put("Phone no.", phoneno);

                            documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "User Profile is created for " + userID);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e.toString());

                                    editText3.setEnabled(true);
                                    editText4.setEnabled(true);
                                    editText5.setEnabled(true);
                                    editText6.setEnabled(true);
                                    editText7.setEnabled(true);
                                    progressBar1.setVisibility(View.GONE);
                                    Contimg.setVisibility(View.VISIBLE);
                                }
                            });

                            //Send data back to support screen
                            Intent intent = new Intent();
                            setResult(RESULT_OK, intent);

                            //Send user to next page
                            Intent i=new Intent(getApplicationContext(), VerifyEmail.class);
                            i.putExtra("Mail",email);
                            startActivity(i);
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            editText3.setEnabled(true);
                            editText4.setEnabled(true);
                            editText5.setEnabled(true);
                            editText6.setEnabled(true);
                            editText7.setEnabled(true);
                            progressBar1.setVisibility(View.GONE);
                            Contimg.setVisibility(View.VISIBLE);
                        }
                    });
                }
            }
            }
        });
    }
}