package com.example.vamoselectrica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class VerifyEmail extends AppCompatActivity {

    TextView textview20,textview21,textview22,textview6;
    EditText editText25;
    Button button1, button2, button4;
    ProgressBar progressBar3;
    FirebaseAuth auth;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    String eMail,userID,TAG="Neo";
    ImageView imageView9;
    Dialog msent, vsuccess;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);

        msent = new Dialog(VerifyEmail.this);
        vsuccess=new Dialog(VerifyEmail.this);
        msent.setContentView(R.layout.verify_emailsent);
        msent.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        //msent.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_rect));
        msent.setCancelable(true);
        vsuccess.setContentView(R.layout.verify_proceed);
        //vsuccess.getWindow().setBackgroundDrawable(getDrawable(R.drawable.dialog_rect));
        vsuccess.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        vsuccess.setCancelable(false);

        auth = FirebaseAuth.getInstance();

        textview20 = findViewById(R.id.textView20);

        editText25=findViewById(R.id.editText25);
        textview22=findViewById(R.id.textView22);
        button1 = findViewById(R.id.button1);
        Button button4= vsuccess.findViewById(R.id.button4);

        progressBar3=findViewById(R.id.progressBar3);


        imageView9=findViewById(R.id.imageView9);
        textview22.setVisibility(View.INVISIBLE);
        textview20.setVisibility(View.INVISIBLE);
        button1.setVisibility(View.INVISIBLE);
        progressBar3.setVisibility(View.INVISIBLE);

       Bundle bundle=getIntent().getExtras();
       if(bundle==null){
           Log.d(TAG,"null bundle");
       }

       eMail = user.getEmail();
       Log.d(TAG,eMail);
       editText25.setText(eMail);
       editText25.setEnabled(false);



        imageView9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userID= auth.getCurrentUser().getUid();
                DocumentReference documentReference = fstore.collection("Users").document(userID);
                documentReference.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d(TAG,"Store Delete success!");
                    }
                });
                auth.getCurrentUser().delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d(TAG,"Auth Delete success!");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

                auth.signOut();
                Toast.makeText(VerifyEmail.this, "Sign in or Register again", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(),SupportScreen.class));
                finish();

            }
        });

        user.reload();
        if (!user.isEmailVerified()){
            textview20.setVisibility(View.VISIBLE);
            button1.setVisibility(View.VISIBLE);

            button1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //send verification email
                    button1.setVisibility(View.INVISIBLE);
                    progressBar3.setVisibility(View.VISIBLE);
                    user.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            msent.show();
                            msent.setCancelable(true);
                            progressBar3.setVisibility(View.INVISIBLE);
                            button1.setVisibility(View.INVISIBLE);
                            textview22.setVisibility(View.VISIBLE);
                        }
                    });
                }
            });
        }

        else {
            vsuccess.show();
        }

            button4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    vsuccess.dismiss();
                    startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                    finish();
                }
            });

        textview22.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.reload();
                if (user.isEmailVerified()) {
                    progressBar3.setVisibility(View.INVISIBLE);
                    vsuccess.show();
                }
                else{
                    Toast.makeText(VerifyEmail.this, "Verification incomplete", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @Override
    protected void onResume() {
        user.reload();
        if (user.isEmailVerified()) {
            progressBar3.setVisibility(View.INVISIBLE);
            textview21.setVisibility(View.INVISIBLE);
            textview22.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
            vsuccess.show();
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        user.reload();
        if (user.isEmailVerified()) {
            textview21.setVisibility(View.INVISIBLE);
            textview22.setVisibility(View.VISIBLE);
            button2.setVisibility(View.VISIBLE);
        }
        super.onStart();
    }
}