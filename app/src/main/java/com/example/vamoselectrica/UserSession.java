package com.example.vamoselectrica;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class UserSession extends AppCompatActivity {

    TextView textView;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_session);

        textView=findViewById(R.id.textView);
        button=findViewById(R.id.button);

        Bundle bundle=getIntent().getExtras();
        if (bundle==null){
            Toast.makeText(this, "bundle null", Toast.LENGTH_SHORT).show();
        }
        String msg = bundle.getString("bikeno");
        if(msg==null)
        {
            Toast.makeText(this, "msg null", Toast.LENGTH_SHORT).show();
        }
        textView.setText(msg);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
        });

    }
}