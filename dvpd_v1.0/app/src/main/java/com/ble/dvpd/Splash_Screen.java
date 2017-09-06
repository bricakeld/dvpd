package com.ble.dvpd;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by intel on 9/3/2017.
 */

public class Splash_Screen extends Activity {
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        Window window = getWindow();
        window.setFormat(PixelFormat.RGBA_8888);
    }
    private final int SPLASH_DISPLAY_LENGTH = 6000;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        StartAnimations();

        Thread timer = new Thread() {
            public void run() {
                try {
                    sleep(SPLASH_DISPLAY_LENGTH);

                    String name_value,pass_value;
                    SharedPreferences prefs =getSharedPreferences("MyPrf", Context.MODE_PRIVATE);
                    name_value=prefs.getString("value_username", null);
                    pass_value=prefs.getString("value_userpass", null);


                    if(name_value!=null && pass_value!=null){

                        Intent i = new Intent(Splash_Screen.this, ScanActivity.class);
                        startActivity(i);

                    }
                    else
                    {
                        Intent i = new Intent(Splash_Screen.this, MainActivity.class);
                        startActivity(i);

                    }

                    finish();

                }

                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        timer.start();

    }
    private void StartAnimations() {
        Animation anim = AnimationUtils.loadAnimation(this, R.anim.alpha);
        anim.reset();
        LinearLayout l=(LinearLayout) findViewById(R.id.lin_lay);
        l.clearAnimation();
        l.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.bounce);
        anim.reset();
        ImageView iv = (ImageView) findViewById(R.id.logo);
        iv.clearAnimation();
        iv.startAnimation(anim);

        anim = AnimationUtils.loadAnimation(this, R.anim.translate);
        anim.reset();
        TextView iv2 = (TextView) findViewById(R.id.tv_project_name);
        iv2.clearAnimation();
        iv2.startAnimation(anim);

    }

}