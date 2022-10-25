package com.example.vamoselectrica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

public class SupportScreen extends AppCompatActivity {

    TextView textView1, textView2, textView3, textView4, textView5, textView7, textView8;
    ImageView SSgreyrect, ellipse, liveforgreen, EScooter, Accountimg, LoginBTN, imageView6;
    FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    String peremail;


    int Register_activity_req_code = 0;
    int Login_activity_req_code = 12;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_screen);


        textView1 = findViewById(R.id.textView20);
        textView2 = findViewById(R.id.textView21);
        textView5 = findViewById(R.id.textView5);
        SSgreyrect = findViewById(R.id.SSgreyrect);
        ellipse = findViewById(R.id.ellipse);
        liveforgreen = findViewById(R.id.liveforgreen);
        EScooter = findViewById(R.id.EScooter);
        Accountimg = findViewById(R.id.Accountimg);
        LoginBTN = findViewById(R.id.LoginBTN);
        imageView6 = findViewById(R.id.imageView6);

        //link to send feedback
        imageView6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://www.vamoselectrica.com/"));
                startActivity(intent);
            }
        });

        //Login btn
        LoginBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginScreen.class);
                startActivityForResult(intent,Login_activity_req_code);
            }
        });
        //Sign up btn
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), Register.class);
                startActivityForResult(intent,Register_activity_req_code);
            }
        });
        //Customer support
        textView5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_DIAL,Uri.parse("tel: 6265310590"));
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check that it is the SecondActivity with an OK result
        if (requestCode == Register_activity_req_code || requestCode == Login_activity_req_code) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        { if(FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }
        else if(!FirebaseAuth.getInstance().getCurrentUser().isEmailVerified()){
            Intent i = new Intent(getApplicationContext(), VerifyEmail.class);
            peremail = firebaseAuth.getCurrentUser().getEmail();
            i.putExtra("Mail",peremail);
            startActivity(i);
            finish();
        }
        }
    }
}