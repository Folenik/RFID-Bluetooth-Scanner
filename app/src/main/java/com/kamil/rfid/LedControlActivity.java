package com.kamil.rfid;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import java.io.IOException;
import java.util.UUID;

public class LedControlActivity extends AppCompatActivity implements View.OnClickListener {

    ProgressDialog pDialog;
    BluetoothAdapter mBluetoothAdapter;
    BluetoothSocket mBluetoothSocket;
    boolean isBluetoothConnected;
    String deviceAddress;
    AppCompatButton buttonOn, buttonOff, buttonDisconnect;
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent newIntent = getIntent();
        deviceAddress = newIntent.getStringExtra(MainActivity.deviceAddress);
        setContentView(R.layout.activity_led);

        init();

    }

    public void init() {
        new ConnectBT().execute();

        buttonOn = (AppCompatButton) findViewById(R.id.led_on);
        buttonOff = (AppCompatButton) findViewById(R.id.led_off);
        buttonDisconnect = (AppCompatButton) findViewById(R.id.bluetooth_disconnect);

        buttonOn.setOnClickListener(this);
        buttonOff.setOnClickListener(this);
        buttonDisconnect.setOnClickListener(this);

        buttonOn.setVisibility(View.INVISIBLE);
        buttonOff.setVisibility(View.INVISIBLE);
        buttonDisconnect.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onClick(View view) {
        if(view == buttonOn) {
            turnOnLed();
        }
        else if(view == buttonOff) {
            turnOffLed();
        }
        else if(view == buttonDisconnect) {
            disconnectDevice();
        }
    }

    private void turnOnLed() {
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.getOutputStream().write("a".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    private void turnOffLed() {
        if (mBluetoothSocket != null) {
            try {
                mBluetoothSocket.getOutputStream().write("b".toString().getBytes());
            } catch (IOException e) {
                msg("Error");
            }
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

    private class ConnectBT extends AsyncTask<Void, Void, Void>
    {
        private boolean ConnectSuccess = true;

        @Override
        protected void onPreExecute() {
            pDialog = ProgressDialog.show(LedControlActivity.this, "Connecting...", "Please wait.");
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
                buttonOn.setVisibility(View.VISIBLE);
                buttonOff.setVisibility(View.VISIBLE);
                buttonDisconnect.setVisibility(View.VISIBLE);
                msg("Connected.");
                isBluetoothConnected = true;
            }
            pDialog.dismiss();
        }
    }
}