package com.kamil.rfid;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

public class RFIDActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog pDialog;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mBluetoothSocket;
    boolean isBluetoothConnected;
    String deviceAddress;
    AppCompatButton buttonDisconnect;
    AppCompatTextView rfidTEST;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    volatile boolean stopWorker;
    Thread workerThread;
    byte[] readBuffer;
    int readBufferPosition;
    InputStream mmInputStream;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newIntent = getIntent();
        deviceAddress = newIntent.getStringExtra(MainActivity.deviceAddress);
        setContentView(R.layout.activity_rfid);

        init();

    }

    public void init() {
        new ConnectBT().execute();

        buttonDisconnect = (AppCompatButton) findViewById(R.id.bluetooth_disconnect);
        rfidTEST = (AppCompatTextView) findViewById(R.id.rfid_textview);

        buttonDisconnect.setOnClickListener(this);

        buttonDisconnect.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonDisconnect) {
            disconnectDevice();
        }
    }

    private void disconnectDevice() {
        if(mBluetoothSocket !=null) {
            try {
                mBluetoothSocket.close();
            } catch (IOException e) {
                msg("Error");
            }
        }
        finish();
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void listenForData() {
        final Handler handler = new Handler();
        final byte delimiter = 10; //This is the ASCII code for a newline character

        stopWorker = false;
        readBufferPosition = 0;
        readBuffer = new byte[1024];
        workerThread = new Thread(new Runnable()
        {
            public void run()
            {
                while(!Thread.currentThread().isInterrupted() && !stopWorker)
                {
                    try
                    {
                        int bytesAvailable = mmInputStream.available();
                        if(bytesAvailable > 0)
                        {
                            byte[] packetBytes = new byte[bytesAvailable];
                            mmInputStream.read(packetBytes);
                            for(int i=0;i<bytesAvailable;i++)
                            {
                                byte b = packetBytes[i];
                                if(b == delimiter)
                                {
                                    byte[] encodedBytes = new byte[readBufferPosition];
                                    System.arraycopy(readBuffer, 0, encodedBytes, 0, encodedBytes.length);
                                    final String data = new String(encodedBytes, "US-ASCII");
                                    readBufferPosition = 0;

                                    handler.post(new Runnable()
                                    {
                                        public void run()
                                        {
                                            rfidTEST.setText(data);
                                        }
                                    });
                                }
                                else
                                {
                                    readBuffer[readBufferPosition++] = b;
                                }
                            }
                        }
                    }
                    catch (IOException ex)
                    {
                        stopWorker = true;
                    }
                }
            }
        });
        workerThread.start();
    }

    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            pDialog = ProgressDialog.show(RFIDActivity.this, "Connecting...", "Please wait.");
        }

        @Override
        protected Void doInBackground(Void... devices) {
            try {
                if (mBluetoothSocket == null || !isBluetoothConnected) {
                    mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice myDevice = mBluetoothAdapter.getRemoteDevice(deviceAddress);
                    mBluetoothSocket = myDevice.createInsecureRfcommSocketToServiceRecord(myUUID);
                    BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
                    mBluetoothSocket.connect();
                    mmInputStream = mBluetoothSocket.getInputStream();
                }
            } catch (IOException e) {
                ConnectSuccess = false;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (!ConnectSuccess) {
                msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
                finish();
            } else {
                buttonDisconnect.setVisibility(View.VISIBLE);
                msg("Connected.");
                isBluetoothConnected = true;
                listenForData();
            }
            pDialog.dismiss();
        }
    }

}