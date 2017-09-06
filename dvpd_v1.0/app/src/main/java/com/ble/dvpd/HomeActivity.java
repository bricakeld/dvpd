package com.ble.dvpd;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static com.ble.dvpd.BluetoothLeService.mBluetoothAdapter;



public class HomeActivity extends Activity implements
        AdapterView.OnItemSelectedListener {

    TextView Tv_Beacon, Tv_Lock, Tv_Connect;
    Switch Tv_Bulb;
    String[] country = { "Choose Bulb Color", "White", "Red", "Blue", "Pink","Green","Purple","Yellow"  };
    Button Button_Color;
    TextView Spin_Text;
    SharedPreferences.Editor editor;

    private final BroadcastReceiver mReadListener =new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                String value = intent.getStringExtra(CommonConstants.EXTRA_BULB_VALUE);
                if(value.equals("0")) {
                    Tv_Bulb.setChecked(false);
                } else {
                    Tv_Bulb.setChecked(true);
                }
                Toast.makeText(HomeActivity.this, value, Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BluetoothLeService.readCharacteristic();
        Tv_Bulb = (Switch)findViewById(R.id.bulb_switch);
        Tv_Beacon = (TextView)findViewById(R.id.tv_btn_beacon);
        Tv_Lock = (TextView)findViewById(R.id.tv_btn_lock);
        Tv_Connect = (TextView)findViewById(R.id.tv_btn_connect);
        Spin_Text=(TextView)findViewById(R.id.textView1);

        //Getting the instance of Spinner and applying OnItemSelectedListener on it
        Spinner spin = (Spinner) findViewById(R.id.spinner1);
        spin.setOnItemSelectedListener(this);

        //Creating the ArrayAdapter instance having the country list
      //  ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,country);
       // aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_textview_align,country );

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_textview_align);

        //Setting the ArrayAdapter data on the Spinner
        spin.setAdapter(spinnerArrayAdapter);

        Tv_Bulb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b) {
                    BluetoothLeService.writeBulbCharacteristic("1");
                } else {
                    BluetoothLeService.writeBulbCharacteristic("0");
                }
            }
        });

        Tv_Beacon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent beacon = new Intent(Intent.ACTION_VIEW);
                beacon.setData(Uri.parse("https://www.google.com/"));
                startActivity(beacon);
            }
        });

        Tv_Lock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent lock = new Intent(Intent.ACTION_VIEW);
                lock.setData(Uri.parse("https://www.apple.com/in/"));
                startActivity(lock);
            }
        });

        Tv_Connect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences.Editor editor = getSharedPreferences("MyPrf", Context.MODE_PRIVATE).edit();
                editor.clear();
                editor.commit();

                mBluetoothAdapter.disable();

                Intent i = new Intent(HomeActivity.this, MainActivity.class);
                startActivity(i);
                finish();

            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        LocalBroadcastManager.getInstance(this).registerReceiver(mReadListener, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mReadListener);
    }

    //Performing action onItemSelected and onNothing selected
    @Override
    public void onItemSelected(AdapterView<?> arg0, View arg1, int position,long id) {
        String value= String.valueOf(id);

        // Toast.makeText(getApplicationContext(),country[position] ,Toast.LENGTH_LONG).show();
       // Toast.makeText(getApplicationContext(),value ,Toast.LENGTH_LONG).show();
        //"Bulb Color", "Red", "Blue", "Light Green", "Pink","Light Blue"

        if(value=="0"){
           // BluetoothLeService.writeBulbColorCharacteristic("0");
        } else if(value=="1"){
            BluetoothLeService.writeBulbColorCharacteristic("0");
            Tv_Bulb.setBackgroundColor(Color.WHITE);
            Tv_Bulb.setTextColor(Color.BLACK);

        } else if(value=="2"){
            BluetoothLeService.writeBulbColorCharacteristic("1");
            Tv_Bulb.setBackgroundColor(Color.RED);
        } else if(value=="3"){
            BluetoothLeService.writeBulbColorCharacteristic("2");
            Tv_Bulb.setBackgroundColor(Color.BLUE);
        } else if(value=="4"){
            BluetoothLeService.writeBulbColorCharacteristic("3");
            Tv_Bulb.setBackgroundColor(Color.parseColor("#FF4081"));
        } else if(value=="5"){
            BluetoothLeService.writeBulbColorCharacteristic("4");
            Tv_Bulb.setBackgroundColor(Color.GREEN);
        } else if(value=="6"){
            BluetoothLeService.writeBulbColorCharacteristic("5");
            Tv_Bulb.setBackgroundColor(Color.parseColor("#8c3F51B5"));
        } else if(value=="7"){
            BluetoothLeService.writeBulbColorCharacteristic("6");
            Tv_Bulb.setBackgroundColor(Color.YELLOW);
        }
    }


    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
