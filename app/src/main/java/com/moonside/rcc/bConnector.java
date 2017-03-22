package com.moonside.rcc;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class bConnector extends AppCompatActivity {
    private static final int REQUEST_DISCOVERABLE_BT = 1 ;
    private static final int RESULT_OK = 1 ;
    public static String EXTRA_DEVICE_ADDRESS = "device_address";
    //stuff for layout
    Button BT_disOn;
    ListView pdeviceList ;
    ListView ndeviceList ;
    TextView pTextView;
    TextView nTextView;
    /******************************************************/

    public BluetoothAdapter mBluetoothAdapter;
    ArrayAdapter<String> pdAdapter;
    ArrayAdapter<String> ndAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_b_connector);
        //layout initialize
        BT_disOn = (Button)findViewById(R.id.BT_btDisOn);
        pdeviceList = (ListView) findViewById(R.id.LV_pd);
        ndeviceList = (ListView) findViewById(R.id.LV_nd);
        pTextView = (TextView) findViewById(R.id.TX_pd);
        nTextView = (TextView) findViewById(R.id.TX_nd);

        pdAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        ndAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1);
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(mBluetoothAdapter!=null){
            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    pdAdapter.add(device.getName() + ':' + device.getAddress());
                }
            }
            pdeviceList.setAdapter(pdAdapter);
        }else
            Toast.makeText(getApplicationContext(), "No bluetooth found!",Toast.LENGTH_LONG).show();

        BT_disOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBluetoothAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(),"开始检测",Toast.LENGTH_LONG).show();
            }
        });
        // Register for broadcasts when a device is discovered
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        this.registerReceiver(mReceiver, filter);

        // Register for broadcasts when discovery has finished
        filter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        this.registerReceiver(mReceiver, filter);
        ndeviceList.setAdapter(ndAdapter);
        ndeviceList.setOnItemClickListener(DeviceClickListener);
        pdeviceList.setOnItemClickListener(DeviceClickListener);
    }


    private AdapterView.OnItemClickListener DeviceClickListener
            = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {
            // Cancel discovery because it's costly and we're about to connect
            mBluetoothAdapter.cancelDiscovery();

            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            int index = find_pos(info);
            String device_name = info.substring(0,index-1);
            String device_mac = info.substring(index+1,info.length());
            // Create the result Intent and include the MAC address
            Intent intent = new Intent(bConnector.this,Home.class);
            Bundle b = new Bundle();
            b.putString("nameInfo", device_name);
            b.putString("macInfo", device_mac);
            intent.putExtras(b);
            Toast.makeText(getApplicationContext(),info,Toast.LENGTH_LONG).show();
            // Set result and finish this Activity
            setResult(Activity.RESULT_OK, intent);
            finish();
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        //check for paired devices and put info in mArrayAdapter
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Make sure we're not doing discovery anymore
        if (mBluetoothAdapter != null) {
            mBluetoothAdapter.cancelDiscovery();
        }
        // Unregister broadcast listeners
        this.unregisterReceiver(mReceiver);
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            // When discovery finds a device
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    ndAdapter.add(device.getName() + ':'+ device.getAddress());
                }
                // When discovery is finished, change the Activity title
            }
        }
    };


    public int find_pos(String s){
        byte[] temp = s.getBytes();
        int index = 0;
        for(int i =0;i<s.length();i++){
            if(temp[i] == ':'){
                index = i ;
                break;
            }
        }
        return index;
    }
}
