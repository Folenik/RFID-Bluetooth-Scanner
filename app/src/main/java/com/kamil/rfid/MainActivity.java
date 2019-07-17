package com.kamil.rfid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.kamil.rfid.Const.DEVICE_ADDRESS;
import static com.kamil.rfid.Const.REQUEST_CODE_BLUETOOTH;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    AppCompatButton bluetoothPair;
    BluetoothAdapter mBluetoothAdapter;
    Set<BluetoothDevice> pairedDevices;
    ArrayAdapter listAdapter;
    ListView deviceListView;
    TextView deviceTextView;
    public static String deviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    public void init() {
        bluetoothPair = (AppCompatButton) findViewById(R.id.bluetooth_pair);
        deviceListView = (ListView) findViewById(R.id.paired_devices_listView);
        deviceTextView = (TextView) findViewById(R.id.paired_devices_text);

        bluetoothPair.setOnClickListener(this);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //TESTS
        Intent i = new Intent(MainActivity.this, WiFiActivity.class);
        startActivity(i);
        /////////
    }

    public void pairDevice() {
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        ArrayList deviceList = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                deviceList.add(bt.getName() + " " + bt.getAddress());
            }
        } else {
            Toast.makeText(getApplicationContext(), "No devices found.", Toast.LENGTH_SHORT).show();
        }

        listAdapter = new ArrayAdapter(this,R.layout.list_item, deviceList);
        deviceListView.setAdapter(listAdapter);
        deviceListView.setOnItemClickListener(mListClickListener);
    }

    private AdapterView.OnItemClickListener mListClickListener = new AdapterView.OnItemClickListener()
    {
        public void onItemClick (AdapterView av, View v, int arg2, long arg3)
        {
            String info = ((TextView) v).getText().toString();
            String address = info.substring(info.length() - 17);


            //LedControlActivity can be used just for BT connection
            Intent i = new Intent(MainActivity.this, RFIDActivity.class);
            i.putExtra(deviceAddress, address);
            startActivity(i);
        }
    };

    @Override
    public void onClick(View view) {
        if (bluetoothPair.equals(view)) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBluetoothIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBluetoothIntent, REQUEST_CODE_BLUETOOTH);
                pairDevice();
            }
            else {
                pairDevice();
            }
        }
        else {
            Toast.makeText(getApplicationContext(), "Something went wrong.", Toast.LENGTH_SHORT).show();
        }
    }
}
