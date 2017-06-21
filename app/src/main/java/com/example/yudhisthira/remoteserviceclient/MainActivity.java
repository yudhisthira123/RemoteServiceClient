package com.example.yudhisthira.remoteserviceclient;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int MSG_REGISTER_CLIENT = 1;
    private static final int MSG_UNREGISTER_CLIENT = 2;
    private static final int MSG_CLIENT = 3;


    class IncomingHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
            Log.d(TAG, "handleMessage - remoteserviceclient");
        }
    }

    class RemoteServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            serviceMessanger = new Messenger(service);

            try {
                Message message = Message.obtain(null, MSG_REGISTER_CLIENT);

                message.replyTo = clientMessanger;
                serviceMessanger.send(message);

                message = Message.obtain(null , MSG_CLIENT);
                serviceMessanger.send(message);
            }
            catch (RemoteException e) {
                Log.d(TAG, "Exception in onServiceConnected");
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    private Messenger serviceMessanger = null;
    private Messenger clientMessanger = new Messenger(new IncomingHandler());

    private Button                  bindButton;
    private Button                  unBindButton;


    Intent serviceIntent;
    ServiceConnection  serviceConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindButton = findViewById(R.id.btnBoundService);
        unBindButton = findViewById(R.id.btnUnBindService);

        bindButton.setOnClickListener(this);
        unBindButton.setOnClickListener(this);

        serviceIntent = new Intent();
        serviceConnection = new RemoteServiceConnection();
    }

    @Override
    public void onClick(View view) {

        int id = view.getId();

        switch (id) {

            case R.id.btnBoundService:
                bindService();
                break;

            case R.id.btnUnBindService:
                unBindService();
                break;

        }

    }

    private void bindService() {

        ComponentName componentName = new ComponentName("com.example.yudhisthira.remoteservice", "com.example.yudhisthira.remoteservice.RemoteService");
        serviceIntent.setComponent(componentName);

        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);

    }

    private void unBindService() {

        Message message = Message.obtain(null, MSG_UNREGISTER_CLIENT);

        message.replyTo = clientMessanger;
        try {
            serviceMessanger.send(message);
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }

        unbindService(serviceConnection);
    }
}
