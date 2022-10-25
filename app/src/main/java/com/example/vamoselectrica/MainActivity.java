package com.example.vamoselectrica;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.InputEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity implements Serializable {

    Switch switch1;
    TextView textView, textView2, textView3, textView4;
    BluetoothAdapter bluetoothAdapter;
    ListView pairlist;
    ListView discoverlist;
    ProgressBar progressBar;
    ImageView imageView11;

    Button Listenbtn, Unlockbtn;

    public ArrayList<String> arrayList = new ArrayList<>();//Display list
    public ArrayList<BluetoothDevice> btdevices = new ArrayList<>();//store discovered
    ArrayAdapter<String> arrayAdapter1; //Discover list
    public ArrayList<BluetoothDevice> btdevicespaired = new ArrayList<>();//store paired
    ArrayAdapter<String> arrayAdapter; //Pairlist
    public BluetoothSocket mySocket;
    Set<BluetoothDevice> availset = new HashSet<>();
    BluetoothDevice devicetest;


    static final int STATE_LISTENING = 1;
    static final int STATE_CONNECTING = 2;
    static final int STATE_CONNECTED = 3;
    static final int STATE_CONNECTION_FAILED = 4;
    //static final int STATE_MESSAGE_RECEIVED = 5;
    public static final int BOND_BONDED = 12;

    static final int BLUETOOTH_ADVERTISE_PERMISSION = 21;
    static final int BLUETOOTH_CONNECT_PERMISSION = 22;
    static final int BLUETOOTH_SCAN = 23;

    public static final int BLUETOOTH_REQ_CODE = 1;
    public static final int BLUETOOTH_CONNECTION_CODE = 8;
    // HC-05 UUID  "00001101-0000-1000-8000-00805F9B34FB"
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String NAME = "hello";
    public static final String DEVICE_EXTRA = "hello";

    private interface MessageConstants {
        public static final int MESSAGE_READ = 0;
        public static final int MESSAGE_WRITE = 1;
        public static final int MESSAGE_TOAST = 2;
    }





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//Declarations
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        switch1 = findViewById(R.id.switch1);
        pairlist = findViewById(R.id.pairlist);
        discoverlist = findViewById(R.id.discoverlist);
        textView = findViewById(R.id.textView);
        //textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
        textView4 = findViewById(R.id.textView4);
        textView4.setVisibility(View.VISIBLE);
        progressBar = findViewById(R.id.progressBar);
        imageView11 = findViewById(R.id.imageView11);


        IntentFilter filter1 = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mBroadcastReceiver1, filter1);

        IntentFilter filter2 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        registerReceiver(mBroadcastReceiver2, filter2);

        IntentFilter filter3 = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(mBroadcastReceiver3, filter3);


//Discover list
        arrayAdapter1 = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, arrayList);
        discoverlist.setAdapter(arrayAdapter1);


        discoverlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION);
                    }

                }
                bluetoothAdapter.cancelDiscovery();
                devicetest = btdevices.get(i);
                if (devicetest == null) {
                    Toast.makeText(MainActivity.this, "No device passed", Toast.LENGTH_SHORT).show();
                }
                if (devicetest != null) {
                    String nmtest = devicetest.getName();
                    //if(nmtest=="HC-05") {
                    Toast.makeText(MainActivity.this, "You clicked on Device: " + devicetest.getName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                    intent.putExtra(DEVICE_EXTRA, devicetest);
                    startActivity(intent);
                    finish();
                    //}
                    //else
                    //Toast.makeText(MainActivity.this, "Please click on the right device", Toast.LENGTH_SHORT).show();
                        /*if (devicetest.getBondState() != BOND_BONDED) {
                            Log.d(TAG, "bonding start");
                            devicetest.createBond();
                            Log.d(TAG, "bonding finish");
                        } else {
                            Toast.makeText(MainActivity.this, "You clicked on device : " + devicetest.getName() + "Attempting connection", Toast.LENGTH_SHORT).show();
                            ConnectThread connectThread = new ConnectThread(devicetest, MY_UUID);
                            connectThread.start();
                        }*/


                }
            }
        });
        /*pairlist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION);
                    }
                }
                bluetoothAdapter.cancelDiscovery();
                devicetest = btdevicespaired.get(i);

                if (devicetest == null) {
                    Toast.makeText(MainActivity.this, "No device found", Toast.LENGTH_SHORT).show();
                }

                if (devicetest != null) {
                    String nmtest = devicetest.getName();
                    //if(nmtest=="HC-05") {
                        Toast.makeText(MainActivity.this, "You clicked on Device: " + devicetest.getName(), Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                        intent.putExtra(DEVICE_EXTRA, devicetest);
                        startActivity(intent);
                   // }
                  //  else
                       // Toast.makeText(MainActivity.this, "Please click on the right device", Toast.LENGTH_SHORT).show();

                    /*Toast.makeText(MainActivity.this, "You clicked on device : " + devicetest.getName() + "Attempting connection", Toast.LENGTH_SHORT).show();
                      ConnectThread connectThread = new ConnectThread(devicetest, MY_UUID);
                      connectThread.start();
                      mySocket=connectThread.giveSocket();

                }
            }
        });*/

        //Refresh btn
        textView3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                discoverDevices();
            }
        });

//BT availability check
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth is not supported on this device", Toast.LENGTH_SHORT).show();
        }
//Switch initial check
        if (!bluetoothAdapter.isEnabled()) {
            switch1.setChecked(false);
            //textView2.setVisibility(View.INVISIBLE);
            //pairlist.setVisibility(View.INVISIBLE);
            textView.setVisibility(View.INVISIBLE);
            textView3.setVisibility(View.INVISIBLE);
            discoverlist.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.INVISIBLE);

        } else if (bluetoothAdapter.isEnabled()) {
            switch1.setChecked(true);
            //findPairedDevices();
            discoverDevices();
            //textView2.setVisibility(View.VISIBLE);
            //pairlist.setVisibility(View.VISIBLE);
            textView3.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
            discoverlist.setVisibility(View.VISIBLE);
        }

        imageView11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             startActivity(new Intent(MainActivity.this,MapsActivity.class));
            }
        });


        switch1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(
                                MainActivity.this,
                                new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION);
                    }

                }
                if (switch1.isChecked()) {
                    Intent bluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(bluetoothIntent, BLUETOOTH_REQ_CODE);

                } else if (!switch1.isChecked()) {
                    bluetoothAdapter.disable();
                }

            }
        });
    }
    //OnCreate finished

    //Handler
 /*  Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case STATE_LISTENING:
                    if (bluetoothAdapter.isDiscovering())
                        bluetoothAdapter.cancelDiscovery();
                    break;
                case STATE_CONNECTING:
                    discoverlist.setEnabled(false);
                    Toast.makeText(MainActivity.this, "Connecting!!", Toast.LENGTH_SHORT).show();
                    break;
                case STATE_CONNECTED:
                    Toast.makeText(MainActivity.this, "Connection Successful!", Toast.LENGTH_SHORT).show();
                    discoverlist.setEnabled(true);
                    textView2.setVisibility(View.INVISIBLE);
                    pairlist.setVisibility(View.INVISIBLE);
                    textView.setVisibility(View.INVISIBLE);
                    textView3.setVisibility(View.INVISIBLE);
                    discoverlist.setVisibility(View.INVISIBLE);
                    textView4.setVisibility(View.VISIBLE);
                    textView4.setText("You are connected to : " + devicetest.getName().toString());
                    break;
                case STATE_CONNECTION_FAILED:
                    discoverlist.setEnabled(true);
                    Toast.makeText(MainActivity.this, "Connection Failed!", Toast.LENGTH_SHORT).show();
                    break;
            }
            return false;
        }
    });*/


    private final BroadcastReceiver mBroadcastReceiver1 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                switch (state) {
                    case BluetoothAdapter.STATE_OFF:
                        switch1.setChecked(false);
                        //textView2.setVisibility(View.INVISIBLE);
                        //pairlist.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.INVISIBLE);
                        textView3.setVisibility(View.INVISIBLE);
                        discoverlist.setVisibility(View.INVISIBLE);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        switch1.setChecked(true);
                       // textView2.setVisibility(View.VISIBLE);
                        //findPairedDevices();
                        //pairlist.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView3.setVisibility(View.VISIBLE);
                        discoverlist.setVisibility(View.VISIBLE);
                        discoverDevices();
                        break;
                }
            }

        }
    };//switch check by notification bar

    private final BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)) {
                textView3.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
            }
        }
    };//Discovery Started

    private final BroadcastReceiver mBroadcastReceiver3 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)) {
                textView3.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            }
        }
    };//Discovery Finished

    //Device Found receiver
    private final BroadcastReceiver mBroadcastReceiver4 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            /*
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            MainActivity.this,
                            new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION);
                }
                return;
            }*/
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    String dev = device.getName();
                    btdevices.remove(device);
                    btdevices.add(device);
                    arrayList.remove(dev);
                    arrayList.add(dev);
                    arrayAdapter1.notifyDataSetChanged();
                    availset.add(device);//SET

            }
        }
    };


    //Switch - Bt allow deny
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==BLUETOOTH_REQ_CODE) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(MainActivity.this, "Bluetooth is switched ON", Toast.LENGTH_SHORT).show();
                switch1.setChecked(true);
                //findPairedDevices();
                discoverDevices();
            } else {
                if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(MainActivity.this, "Bluetooth operation is cancelled", Toast.LENGTH_SHORT).show();
                    switch1.setChecked(false);
                }
            }
        }
    }
    //Paired Devices definition
    /*private void findPairedDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION);
            }
        }
        int index = 0;
        Set<BluetoothDevice> bluetoothSet = bluetoothAdapter.getBondedDevices();
        String[] str = new String[bluetoothSet.size()];
        btdevicespaired.clear();
        if (bluetoothSet.size() > 0) {
            for (BluetoothDevice device : bluetoothSet) {
                str[index] = device.getName();
                btdevicespaired.remove(device);
                btdevicespaired.add(device);
                index++;
            }
            arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, str);
            pairlist.setAdapter(arrayAdapter);

        }
    }*/

    //Discover devices def
    public void discoverDevices() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        MainActivity.this,
                        new String[]{Manifest.permission.BLUETOOTH_SCAN}, BLUETOOTH_SCAN);
            }
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        } else if (!bluetoothAdapter.isDiscovering()) {
            IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
            registerReceiver(mBroadcastReceiver4, filter);
            arrayList.clear();
            btdevices.clear();
            arrayAdapter1.clear();
            availset.clear();
            bluetoothAdapter.startDiscovery();
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!bluetoothAdapter.isEnabled()) {
            switch1.setChecked(false);
        } else if (bluetoothAdapter.isEnabled()) {
            switch1.setChecked(true);
            //findPairedDevices();
            discoverDevices();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver1);
        unregisterReceiver(mBroadcastReceiver2);
        unregisterReceiver(mBroadcastReceiver3);
        unregisterReceiver(mBroadcastReceiver4);
    }


}

/*private class ConnectedThread extends Thread {
    private final BluetoothSocket mmSocket;
    private final InputStream mmInstream;
    private final OutputStream mmOutstream;
    private byte[] mmBuffer; // mmBuffer store for the stream

    public ConnectedThread(BluetoothSocket socket){
        mmSocket=socket;
        InputStream tmpIn = null;
        OutputStream tmpOut = null;


    try{
        Log.d(TAG, "Trying to get the streams");
        tmpIn=mmSocket.getInputStream();
        tmpOut=mmSocket.getOutputStream();

    } catch (IOException e)
    {
        Log.e(TAG, "Didn't get the streams : " + e);
    }
    mmInstream=tmpIn;
    mmOutstream=tmpOut;
    }

    public void run(){
        while (true) {
            try {
                // Read from the InputStream.
                numBytes = mmInStream.read(mmBuffer);
                // Send the obtained bytes to the UI activity.
                Message readMsg = handler.obtainMessage(
                        MessageConstants.MESSAGE_READ, numBytes, -1,
                        mmBuffer);
                readMsg.sendToTarget();
            } catch (IOException e) {
                Log.d(TAG, "Input stream was disconnected", e);
                break;
            }
        }
    }

    // Call this from the main activity to send data to the remote device.
    public void write(byte[] bytes) {
        try {
            mmOutStream.write(bytes);

            // Share the sent message with the UI activity.
            Message writtenMsg = handler.obtainMessage(
                    MessageConstants.MESSAGE_WRITE, -1, -1, mmBuffer);
            writtenMsg.sendToTarget();
        } catch (IOException e) {
            Log.e(TAG, "Error occurred when sending data", e);

            // Send a failure message back to the activity.
            Message writeErrorMsg =
                    handler.obtainMessage(MessageConstants.MESSAGE_TOAST);
            Bundle bundle = new Bundle();
            bundle.putString("toast",
                    "Couldn't send data to the other device");
            writeErrorMsg.setData(bundle);
            handler.sendMessage(writeErrorMsg);
        }
    }

    // Call this method from the main activity to shut down the connection.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the connect socket", e);
        }
    }

    }
}*/

