package com.ble.dvpd;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Jitendra Kumar (@ji2kumar) and Sunita (@0ddblade).
 * Application is designed to work with Damn Vulnerable Vulerable Peripheral Device (DVPD).
 * DVPD is a nodejs based application which uses Bluetooth Low Energy and vulnerable to
 * different kind of security issues.
 */

public class MainActivity extends AppCompatActivity {

    //here variables
    TextView Tv_Username, Tv_Password, Tv_Btn_Login;
    EditText Et_Username, Et_Password;
    Button Btn_Login;
    String Val_username, Val_password;

    SharedPreferences.Editor editor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tv_Username = (TextView)findViewById(R.id.tv_login_name);
        Tv_Password = (TextView)findViewById(R.id.tv_login_password);
     //   Tv_Btn_Login = (TextView)findViewById(R.id.tv_btn_login);
        Et_Username = (EditText)findViewById(R.id.et_login_name);
        Et_Password = (EditText)findViewById(R.id.et_login_password);
        Btn_Login = (Button)findViewById(R.id.btn_login);

        //SharedPref
        SharedPreferences prefsPrivate = getApplicationContext().getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        editor = prefsPrivate.edit();

        Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Val_username = Et_Username.getText().toString();
                Val_password = Et_Password.getText().toString();
                Log.d("Value1 : ", Val_username);
                Log.d("Value2 : ", Val_password);
                if (Val_username.length() == 0){
                    Toast.makeText(MainActivity.this, "dude! username is not entered", Toast.LENGTH_SHORT).show();
                }
                else if (Val_password.length() == 0){
                    Toast.makeText(MainActivity.this, "dude! Seriouslly, enter password", Toast.LENGTH_SHORT).show();
                }
                else if (Val_password.length() <= 4){
                    Toast.makeText(MainActivity.this, "dude! enter more than 4 characters", Toast.LENGTH_SHORT).show();
                }
                else if (Val_username.equals("admin") && Val_password.equals("12345")){
                    SharedPreferences.Editor editor = getSharedPreferences("MyPrf", Context.MODE_PRIVATE).edit();
                    editor.putString("value_username", Val_username);
                    editor.putString("value_userpass", Val_password);
                    // editor.putInt("idName", 12);
                    editor.commit();
                    Intent i = new Intent(MainActivity.this, ScanActivity.class);
                    startActivity(i);
                    onBackPressed();

                }
                else {
                    Toast.makeText(MainActivity.this, "dude! wrong username and password", Toast.LENGTH_SHORT).show();
                }

            }
        });
     /*   Tv_Btn_Login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent a = new Intent(Intent.ACTION_VIEW);
                a.setData(Uri.parse("http://www.google.com/"));
                startActivity(a);
            }
        });*/




    }

    @Override
    public void onBackPressed() {
        // Simply Do noting!
        super.onBackPressed();
    }


}
