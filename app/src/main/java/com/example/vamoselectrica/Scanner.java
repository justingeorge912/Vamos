package com.example.vamoselectrica;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.WindowManager;
import android.widget.Toast;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;

public class Scanner extends AppCompatActivity {

    CodeScanner codeScanner;
    CodeScannerView scannView;
    String res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);


        scannView=findViewById(R.id.scanner);

        codeScanner = new CodeScanner(this, scannView);

        codeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        res= result.getText().toString();
                        Intent i = new Intent(getApplicationContext(),UserSession.class);
                        i.putExtra("bikeno",res);


                        Vibrator v=(Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        v.vibrate(400);

                        startActivity(i);
                    }
                });
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        codeScanner.startPreview();
    }
}