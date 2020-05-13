package com.example.boundserviceexample;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Intent serviceIntent;
    public static final String TAG = MainActivity.class.getSimpleName();
    private RandomNumberGeneratorService mService;
    private boolean isServiceBound;
    private ServiceConnection mServiceConnection;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.textView);
        serviceIntent = new Intent(getApplicationContext(), RandomNumberGeneratorService.class);

        findViewById(R.id.buttonThreadStarter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: ThreadID "+Thread.currentThread().getId());
                startService(serviceIntent);
            }
        });

        findViewById(R.id.buttonStopthread).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService(serviceIntent);
            }
        });

        findViewById(R.id.buttonBindService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceConnection == null) {
                    mServiceConnection = new ServiceConnection() {
                        @Override
                        public void onServiceConnected(ComponentName name, IBinder service) {
                            isServiceBound = true;
                            RandomNumberGeneratorService.MyServiceBinder serviceBinder = (RandomNumberGeneratorService.MyServiceBinder) service;
                            mService = serviceBinder.getService();
                        }

                        @Override
                        public void onServiceDisconnected(ComponentName name) {
                            isServiceBound = false;
                        }
                    };
                }
                bindService(serviceIntent, mServiceConnection, getApplicationContext().BIND_AUTO_CREATE);
            }
        });

        findViewById(R.id.buttonUnBindService).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mServiceConnection != null) {
                    unbindService(mServiceConnection);
                    isServiceBound = false;
                }
            }
        });

        findViewById(R.id.buttonGetRandomNumber).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setRandomNumber();
            }
        });
    }


    public void setRandomNumber() {
        if (isServiceBound) {
            textView.setText("" + mService.getmRandomNumber());
        } else {
            textView.setText("Service not bound");
        }
    }

    @Override
    protected void onDestroy() {
        unbindService(mServiceConnection);
        super.onDestroy();
    }
}
