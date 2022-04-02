package com.example.vamoselectrica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class SupportScreen extends AppCompatActivity {

    TextView textView1, textView2, textView3, textView4, textView5, textView6, textView7, textView8;
    ImageView SSgreyrect, ellipse, liveforgreen, EScooter, Accountimg, LoginBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_screen);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        textView1 = findViewById(R.id.textView1);
        textView2 = findViewById(R.id.textView2);
        textView5 = findViewById(R.id.textView5);
        textView6 = findViewById(R.id.textView6);


        SSgreyrect = findViewById(R.id.SSgreyrect);
        ellipse = findViewById(R.id.ellipse);
        liveforgreen = findViewById(R.id.liveforgreen);
        EScooter = findViewById(R.id.EScooter);
        Accountimg = findViewById(R.id.Accountimg);
        LoginBTN = findViewById(R.id.LoginBTN);


        LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivity(intent);

            }
        });
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivity(intent);

            }
        });
    }
}