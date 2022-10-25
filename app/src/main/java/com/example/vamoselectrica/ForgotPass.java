package com.example.vamoselectrica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPass extends AppCompatActivity {

    EditText editText1;
    Button ForgotBtn;
    ImageView imageView7;
    FirebaseAuth auth;
    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_pass);

        editText1 = findViewById(R.id.editText1);
        imageView7=findViewById(R.id.imageView7);

        ForgotBtn = findViewById(R.id.ForgotBtn);
        auth = FirebaseAuth.getInstance();

        ForgotBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email=editText1.getText().toString();
                if (email.isEmpty()) {
                    editText1.setError("Email is Required");
                }
                else {
                    auth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()){
                                Toast.makeText(ForgotPass.this, "Check your mail", Toast.LENGTH_SHORT).show();

                                startActivity(new Intent(ForgotPass.this , LoginScreen.class));
                                finish();
                            }
                            else{
                                Toast.makeText(ForgotPass.this, "Error : "+task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                                
                        }
                    });
                }
            }
        });

        imageView7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), LoginScreen.class));
            }
        });
    }
}