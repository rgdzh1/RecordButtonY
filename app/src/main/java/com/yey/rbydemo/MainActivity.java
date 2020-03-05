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
            public void startCb(String current) {
                Toast.makeText(MainActivity.this, "录制开始" + current, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void finishCb(String current) {
                Toast.makeText(MainActivity.this, "录制结束" + current, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void eventCb(String current) {
                Log.e(TAG, "当前录制时间" + current);
            }
            @Override
            public void lessShortTimeRecode(String current) {
                Log.e(TAG, "当前录制时间小于规定最短录制时间回调");
            }
        });
    }
}
