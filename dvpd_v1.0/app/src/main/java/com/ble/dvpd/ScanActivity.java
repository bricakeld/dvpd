package com.ble.dvpd;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static android.bluetooth.BluetoothAdapter.STATE_CONNECTED;
import static android.bluetooth.BluetoothAdapter.STATE_CONNECTING;
import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.media.session.PlaybackState.STATE_NONE;

/**
 * Created by g2 on 7/27/2017.
 */

public class ScanActivity extends Activity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_DISCOVERABLE_BT = 1;
    private static final String TAG = ScanActivity.class.getSimpleName();
    TextView Tv_Btn_Scan, Tv_Btn_Main;
    private BluetoothAdapter bluetoothAdapter;
    private ListView DeviceListView;
    private ArrayAdapter<String> DeviceArrayAdapter,pairedDevicesAdapter;
    //Connecting to Gatt Server
    private BluetoothGatt DeviceBluetoothGatt;
    private BluetoothDevice BtDeviceMain;
    String BtDeviceAddressValue;
    // String bluetoothGatts;
    //***********************
    Handler bluetoothIn;
    private StringBuilder recDataString = new StringBuilder();
    TextView txtString;
    TextView txtStringLength;
    private static final UUID BTMODULEUUID = UUID.fromString("00000000-0000-2300-0000-000000616132");
    //00000000-0000-2300-0000-000000616132 //00002a00-0000-1000-8000-00805f9b34fb
    private static String address;
    private BluetoothSocket btSocket = null;
     private ScanActivity.ConnectedThread mConnectedThread;

    private Handler mHandler; // Our main handler that will receive callback notifications
   // private ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    private BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    SharedPreferences.Editor editor;

    //11aug
    LinearLayout Layout_Btn_Scan;
    // #defines for identifying shared types between calling functions
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    private Set<BluetoothDevice> mPairedDevices;
    TextView mBluetoothStatus;
    static final UUID myUUID = UUID.fromString("00002a00-0000-1000-8000-00805f9b34fb");

    ThreadConnected myThreadConnected;
    ThreadConnectBTdevice myThreadConnectBTdevice;

    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent != null && !CommonUtils.isNullOrEmptyString(intent.getAction())) {
                if(intent.getAction().equals(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED)) {
                    Intent intent1 = new Intent(ScanActivity.this, HomeActivity.class);
                    startActivity(intent1);
                    finish();
                }
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_ble_devices);
        //SharedPref
        SharedPreferences prefsPrivate = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = prefsPrivate.edit();

        Tv_Btn_Scan = (TextView)findViewById(R.id.tv_btn_scan);
        Tv_Btn_Main = (TextView)findViewById(R.id.tv_btn_main);
        Layout_Btn_Scan=(LinearLayout)findViewById(R.id.layout_btn_scan);
        mBluetoothStatus=(TextView)findViewById(R.id.tv_status);

        DeviceListView = (ListView)findViewById(R.id.Lv_scan_device);
        DeviceArrayAdapter = new ArrayAdapter<String>(ScanActivity.this, android.R.layout.simple_list_item_1);
        DeviceListView.setAdapter(DeviceArrayAdapter);
        DeviceListView.setOnItemClickListener(mDeviceClickListener);


//        IntentFilter intentFilter=new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
      // registerReceiver(DeviceBroadcastReceiver,intentFilter);

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0)
        { for (BluetoothDevice device : pairedDevices) { BtDeviceMain = device; } }

        // Ask for location permission if not already allowed
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);


        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                Log.d("value10", "Handler");
                Log.d("value11", String.valueOf(msg.what));
                Log.d("value12", String.valueOf(MESSAGE_READ));
                String MsgValue= String.valueOf(msg.what);
                Log.d("value13", MsgValue);
                if(msg.what == MESSAGE_READ){

                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                        Log.d("value14", (String) msg.obj);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                   // Toast.makeText(ScanActivity.this,readMessage,Toast.LENGTH_LONG).show();
                   // Log.d("readMessage",readMessage);
                }

                if(msg.what == CONNECTING_STATUS){
                    Log.d("value15", String.valueOf(msg.arg1));
                    Log.d("value16", MsgValue);
                    if(msg.arg1 == -1)
                        mBluetoothStatus.setText("Connected to Device: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Connection Failed");
                }
            }
        };
        //31st July 2017
        // Use this check to determine whether BLE is supported on the device. Then
// you can selectively disable BLE-related features.
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            Toast.makeText(this, " Bluetooth LE is not supported!", Toast.LENGTH_SHORT).show();
            finish();
        }
        else {
            if (bluetoothAdapter == null){
                Toast.makeText(this, "dude! your device does not support.", Toast.LENGTH_SHORT).show();
            }
            else if (!bluetoothAdapter.isEnabled()){
                Toast.makeText(this, "dude! Enabling bluetooth", Toast.LENGTH_SHORT).show();
                Intent en = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(en, REQUEST_ENABLE_BT);
                mBluetoothStatus.setText("Bluetooth enabled");
                Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();
            }
            else if (bluetoothAdapter.isEnabled()){
                Toast.makeText(this, "dude! Bluetooth is Enabled", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "dude! Unknown error", Toast.LENGTH_SHORT).show();
            }

            if (!bluetoothAdapter.isDiscovering()){
                Toast.makeText(this, "dude! Making your device discoverable ", Toast.LENGTH_LONG).show();
                Intent dis = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
                startActivityForResult(dis, REQUEST_DISCOVERABLE_BT);
            }
            else if (bluetoothAdapter.isDiscovering()){
                Toast.makeText(this, "dude! your device is discoverable", Toast.LENGTH_SHORT).show();
            }
            else {
                Toast.makeText(this, "dude! unknown error", Toast.LENGTH_SHORT).show();
            }
        }

        Tv_Btn_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindDevices(v);

            }
        });

        Layout_Btn_Scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FindDevices(v);
            }
        });
// Main page button
        Tv_Btn_Main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ScanActivity.this, "its working", Toast.LENGTH_SHORT).show();
               // MainActivity_value="1";
                /*Intent i = new Intent(ScanActivity.this, MainActivity.class);
                startActivity(i);*/
                LayoutInflater lrm = LayoutInflater.from(ScanActivity.this);
                View popupView = lrm.inflate(R.layout.scan_ble_popup, null);

                AlertDialog.Builder DialogBuilder = new AlertDialog.Builder(
                        ScanActivity.this);
                DialogBuilder.setView(popupView);
                DialogBuilder
                        .setCancelable(false);

                final AlertDialog Popup = DialogBuilder.create();
                Popup.show();

                final LinearLayout OK = (LinearLayout)popupView.findViewById(R.id.ContinueLayout);
                OK.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Popup.dismiss();
                                /*Intent intent=new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse("https://play.google.com/store/apps?hl=en"));
                                startActivity(intent);*/
                    }
                });

                final LinearLayout Cancel = (LinearLayout)popupView.findViewById(R.id.CancelLayout);
                Cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Popup.dismiss();
                    }
                });
            }
        });

// List Items from device list view and setting action for each device
        DeviceListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
               // Toast.makeText(ScanActivity.this, "BLE Device Position: " + position+" and BLE device ID : " + id, Toast.LENGTH_LONG).show();

                BluetoothLeService.connect(BtDeviceMain.getAddress(), BtDeviceMain.getName(), ScanActivity.this);


                bluetoothAdapter.cancelDiscovery();
            }
        });

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
//        this.registerReceiver(DeviceBroadcastReceiver, filter);
    }


    //Called in ThreadConnectBTdevice once connect successed
    //to start ThreadConnected
    private void startThreadConnected(BluetoothSocket socket){

        myThreadConnected = new ThreadConnected(socket);
        myThreadConnected.start();
    }

    private class ThreadConnected extends Thread {
        private final BluetoothSocket connectedBluetoothSocket;
        private final InputStream connectedInputStream;
        private final OutputStream connectedOutputStream;

        public ThreadConnected(BluetoothSocket socket) {
            connectedBluetoothSocket = socket;
            InputStream in = null;
            OutputStream out = null;

            try {
                in = socket.getInputStream();
                out = socket.getOutputStream();

                Log.d("INout",in+"\n"+out);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            connectedInputStream = in;
            connectedOutputStream = out;
        }

        @Override
        public void run() {
            byte[] buffer = new byte[1024];
            int bytes;

            while (true) {
                try {
                    bytes = connectedInputStream.read(buffer);
                    String strReceived = new String(buffer, 0, bytes);
                    final String msgReceived = String.valueOf(bytes) +
                            " bytes received:\n"
                            + strReceived;

                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            mBluetoothStatus.setText(msgReceived);
                        }});

                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                    final String msgConnectionLost = "Connection lost:\n"
                            + e.getMessage();
                    runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            //mBluetoothStatus.setText(msgConnectionLost);
                        }});
                }
            }
        }

    }

    final BroadcastReceiver DeviceBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String activity = intent.getAction();
            if (BluetoothDevice.ACTION_FOUND.equals(activity)){
                BluetoothDevice BtDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                DeviceArrayAdapter.add(BtDevice.getName()+"\n"+BtDevice.getAddress());
                DeviceArrayAdapter.notifyDataSetChanged();
              //  Toast.makeText(context, "Name: " + BtDevice.getName() + " and Address: " + BtDevice.getAddress() + "  BtDevice value: " + BtDevice, Toast.LENGTH_LONG).show();
                BtDeviceMain=BtDevice;
                BtDeviceAddressValue=BtDevice.getAddress();
                Log.d("deviceAddress", String.valueOf(BtDeviceAddressValue));
                // BluetoothDevice device = BluetoothAdapter.getRemoteDevice(String.valueOf(BtDevice));
                //************
               /* Toast.makeText(ScanActivity.this,
                        "Name: " + BtDevice.getName() + "\n"
                                + "Address: " + BtDevice.getAddress() + "\n"
                                + "BondState: " + BtDevice.getBondState() + "\n"
                                + "BluetoothClass: " + BtDevice.getBluetoothClass() + "\n"
                                + "Class: " + BtDevice.getClass(),
                        Toast.LENGTH_LONG).show();*/

                mBluetoothStatus.setText("start ThreadConnectBTdevice");
             //   Toast.makeText(ScanActivity.this,"start ThreadConnectBTdevice",Toast.LENGTH_LONG);
                myThreadConnectBTdevice = new ThreadConnectBTdevice(BtDevice);
                myThreadConnectBTdevice.start();

                if (BluetoothDevice.ACTION_FOUND.equals(activity)) {
            //Device found
                    Toast.makeText(ScanActivity.this,"Device Found....",Toast.LENGTH_LONG);
                }
                else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(activity)) {
            //Device is now connected
                    Toast.makeText(ScanActivity.this,"Device is now connected....",Toast.LENGTH_LONG);
                    Log.d( "ConnectedDevice","ConnectedDevice");
                }
                else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(activity)) {
           //Done searching
                    Toast.makeText(ScanActivity.this,"Done searching....",Toast.LENGTH_LONG);

                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED.equals(activity)) {
           //Device is about to disconnect
                    Toast.makeText(ScanActivity.this,"Device is about to disconnect...",Toast.LENGTH_LONG);
                }
                else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(activity)) {
          //Device has disconnected
                    Toast.makeText(ScanActivity.this,"Device has disconnected",Toast.LENGTH_LONG);
                }

            }

        }
    };


    public void FindDevices(View v){
        // Check if the device is already discovering
        if(bluetoothAdapter.isDiscovering()){
            bluetoothAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"Discovery stopped",Toast.LENGTH_SHORT).show();
        }
        else {
            if (bluetoothAdapter.isEnabled()) {
                DeviceArrayAdapter.clear(); // clear items
                bluetoothAdapter.startDiscovery();
                Toast.makeText(ScanActivity.this, "Discovery started", Toast.LENGTH_SHORT).show();
                registerReceiver(DeviceBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));

            } else {
                Toast.makeText(ScanActivity.this, "Bluetooth not on", Toast.LENGTH_SHORT).show();
            }

        }
    }


    public void FindDevices2(){
        DeviceArrayAdapter.clear();
        bluetoothAdapter.startDiscovery();
        registerReceiver(DeviceBroadcastReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
    }
    // 3rd August 2017

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!bluetoothAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth not on", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);
            Log.d("value1",info);
            Log.d("value2",address);
            Log.d("value3",name);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
                    Log.d("value4", String.valueOf(device));
                   // device.createBond();
                    try {
                        mBTSocket = createBluetoothSocket(device);
                        Log.d("value5", String.valueOf(mBTSocket));
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(ScanActivity.this, "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                        Log.d("value6","connected");
                        Log.d("value7", String.valueOf(CONNECTING_STATUS));
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            Log.d("value8", String.valueOf(CONNECTING_STATUS));
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1).sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(ScanActivity.this, "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();

                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name).sendToTarget();
                    }
                }
            }.start();
        }
    };

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    private class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()
            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.available();
                    if(bytes != 0) {
                        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                        bytes = mmInStream.available(); // how many bytes are ready to be read?
                        bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                        mHandler.obtainMessage(MESSAGE_READ, bytes, -1, buffer)
                                .sendToTarget(); // Send the obtained bytes to the UI activity
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    break;
                }
            }
        }

    }

    private class ThreadConnectBTdevice extends Thread {

        private BluetoothSocket bluetoothSocket = null;
        private final BluetoothDevice bluetoothDevice;


        private ThreadConnectBTdevice(BluetoothDevice device) {
            bluetoothDevice = device;
            String Address=device.getAddress().toString();
            String Name=device.getName().toString();
            int State=device.getBondState();
            Log.d("bluetoothDevice", String.valueOf(bluetoothDevice));
            Log.d("FinalValues",Name+"and"+Address+"and"+State);


            try {
                bluetoothSocket = device.createRfcommSocketToServiceRecord(myUUID);
              //  mBluetoothStatus.setText("bluetoothSocket: \n" + bluetoothSocket);
             //   Log.d("mBluetoothStatus", String.valueOf(bluetoothSocket));
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            boolean success = false;
            try {

                bluetoothSocket.connect();
                success = true;
            } catch (IOException e) {

                e.printStackTrace();

                final String eMessage = e.getMessage();
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                      //  mBluetoothStatus.setText("something wrong bluetoothSocket.connect(): \n" + eMessage);
                    }
                });

                try {
                    bluetoothSocket.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }
            }

            if(success){
                //connect successful
                final String msgconnected = "connect successful:\n" + "BluetoothSocket: " + bluetoothSocket + "\n"
                        + "BluetoothDevice: " + bluetoothDevice;

                runOnUiThread(new Runnable(){

                    @Override
                    public void run() {
                        mBluetoothStatus.setText(msgconnected);
                        DeviceListView.setVisibility(View.GONE);
                        Log.d("Home","TBConnect");
                    }});

                startThreadConnected(bluetoothSocket);
            }else{
                //fail

            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReceiver);
    }
}
