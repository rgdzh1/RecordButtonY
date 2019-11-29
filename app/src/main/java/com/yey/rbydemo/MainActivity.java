package com.yey.rbydemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.yey.rby.RBYCallback;
import com.yey.rby.RButtonY;

public class MainActivity extends AppCompatActivity {
    private final static String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((RButtonY) findViewById(R.id.rby)).setiRBYClick(new RBYCallback() {
            @Override
            public void finishCb(String current) {
                Toast.makeText(MainActivity.this, "录制结束" + current, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void eventCb(String current) {
//                Toast.makeText(MainActivity.this, "回调时间" + current, Toast.LENGTH_SHORT).show();
                Log.e(TAG, "回调时间" + current);
            }

            @Override
            public void startCb(String current) {
                Toast.makeText(MainActivity.this, "录制开始" + current, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
