package com.example.vamoselectrica;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

class ConnectionService {

    private static final String TAG = "BluetoothConnectionServ";
    private static final String appName = "Myapp";
    private final BluetoothAdapter mbluetoothAdapter;

    Context mContext;
    Intent connectionstatintent = new Intent("connectionstat");
    String status="Connstat";
    private ConnectThread connectThread;
    private ConnectedThread connectedThread;
    private ProgressDialog mProgressDialog;

    public ConnectionService(Context context){
        mContext=context;
        mbluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
    }

    public class ConnectThread extends Thread {
        private BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;
        private final UUID mmuuid;
        BluetoothAdapter bluetoothAdapter;
        static final int BLUETOOTH_CONNECT_PERMISSION = 22;

    /*Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            return false;
        }
    });*/

        static final int STATE_LISTENING = 1;
        static final int STATE_CONNECTING = 2;
        static final int STATE_CONNECTED = 3;
        static final int STATE_CONNECTION_FAILED = 4;

        public ConnectThread(BluetoothDevice device, UUID uuid) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            this.mmDevice = device;
            mmuuid = uuid;
        }

        public void run() {
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION);
            }
        }*/
            BluetoothSocket tmp = null;
            try {
                Log.d(TAG, "trying Socket's created");
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                if(mmDevice!=null){
                    tmp = mmDevice.createInsecureRfcommSocketToServiceRecord(mmuuid);
                    Log.d(TAG, "Socket's created");
                }
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            catch(Exception e){
                e.printStackTrace();
            }

            mmSocket = tmp;

            // Cancel discovery because it otherwise slows down the connection.

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                // Message message2 = Message.obtain();
                // message2.what = STATE_CONNECTING;
                // handler.sendMessage(message2);
                mmSocket.connect();

                // Message message = Message.obtain();
                // message.what = STATE_CONNECTED;
                // handler.sendMessage(message);
                connected(mmSocket,mmDevice);
                Log.d(TAG, "Connection done");
                connectionstatintent.putExtra(status,"1");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(connectionstatintent);

            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                // Message message = Message.obtain();
                // message.what = STATE_CONNECTION_FAILED;
                // handler.sendMessage(message);
                Log.e(TAG, "Failed connection!" + connectException.getMessage(), connectException);
                connectionstatintent.putExtra(status,"0");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(connectionstatintent);
                try {
                    try {
                        try {
                            Log.d(TAG, "Fallback trial started : ");
                            mmSocket = (BluetoothSocket) mmDevice.getClass().getMethod("createRfcommSocket", new Class[]{int.class}).invoke(mmDevice, 1);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    } catch (NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                    /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.BLUETOOTH_CONNECT}, BLUETOOTH_CONNECT_PERMISSION);
                        }
                    }*/
                    if (mmSocket != null) {
                        mmSocket.connect();
                    }
                    Log.e(TAG, "Socket close started");
                    mmSocket.close();
                    Log.e(TAG, "Socket close done");
                } catch (IOException closeException) {
                    Log.d(TAG, "Could not close the client socket " + closeException.getMessage());
                }

            }


            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.

        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                Log.e(TAG, "Cancel function start");
                mmSocket.close();
                Log.e(TAG, "Cancel function finished");
            } catch (IOException e) {
                Log.d(TAG, "Could not close the client socket", e);
            }
        }
    }
    /*public synchronized void start(){
        if(connectThread!=null){
            connectThread.cancel();
            connectThread=null;
        }
    }*/

    public void startClient(BluetoothDevice device, UUID uuid){
        Log.d(TAG,"startClient : Started");
        //mProgressDialog = ProgressDialog.show(mContext,"Connecting Bluetooth","Please Wait!",true);
        connectThread = new ConnectThread(device,uuid);
        connectThread.start();
    }

    public class ConnectedThread extends Thread{ private final BluetoothSocket mmSocket;
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
            int numBytes;
            while (true) {
                try {
                    // Read from the InputStream.
                    numBytes = mmInstream.read(mmBuffer);
                    // Send the obtained bytes to the UI activity.
                   String incomingmsg = new String(mmBuffer,0,numBytes);
                   Log.d(TAG,"incoming message : " + incomingmsg);
                } catch (IOException e) {
                    Log.d(TAG, "Input stream was disconnected", e);
                    break;
                }
            }
        }

        // Call this from the main activity to send data to the remote device.
        public void write(int i) {
            if (i == 0) {
                try {
                    mmOutstream.write("1".toString().getBytes());
                    connectionstatintent.putExtra(status,"3");

                } catch (IOException e) {
                    Log.e(TAG, "Error in writing : ", e);
                    connectionstatintent.putExtra(status,"4");
                }
            }
            if(i==1) {
                try
                {
                    mmOutstream.write("0".toString().getBytes());
                    connectionstatintent.putExtra(status,"3");
                }
                catch (IOException e)
                {
                    Log.e(TAG, "Error in writing : ", e);
                    connectionstatintent.putExtra(status,"4");
                }
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
    private void connected(BluetoothSocket mmSocket, BluetoothDevice device) {

            Log.d(TAG, "Connected method starting");
            connectedThread = new ConnectedThread(mmSocket);
            connectedThread.write(1);

    }

        public void twrite (int i){
            ConnectedThread r;
            Log.d(TAG, "Write called");
            connectedThread.write(i);
        }

}


