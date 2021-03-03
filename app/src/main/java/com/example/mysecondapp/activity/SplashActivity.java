package com.example.mysecondapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.example.mysecondapp.R;

public class SplashActivity extends Activity {

        private final int SPLASH_DISPLAY_LENGTH = 4000;

        @Override
        public void onCreate(Bundle icicle) {
                super.onCreate(icicle);
                setContentView(R.layout.activity_splash);
                new Handler().postDelayed(new Runnable(){
                    @Override
                    public void run() {
                            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                    }
                }, SPLASH_DISPLAY_LENGTH);
        }
}
