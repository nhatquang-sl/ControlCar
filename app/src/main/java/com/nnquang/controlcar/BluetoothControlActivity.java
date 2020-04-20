package com.nnquang.controlcar;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class BluetoothControlActivity extends AppCompatActivity {

    //SPP UUID. Look for it
    static final UUID myUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    BluetoothSocket btSocket = null;
    int LEFT_FORWARD = 1;
    int LEFT_BACKWARD = 2;
    int RIGHT_FORWARD = 3;
    int RIGHT_BACKWARD = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_control);

        Intent intent = getIntent();
        String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        Toast.makeText(getApplicationContext(), message + " is selected!", Toast.LENGTH_SHORT).show();

        try {
            final BluetoothManager bluetoothManager =
                    (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
            BluetoothDevice dispositivo = bluetoothAdapter.getRemoteDevice(message);//connects to the device's address and checks if it's available
            btSocket = dispositivo.createInsecureRfcommSocketToServiceRecord(myUUID);//create a RFCOMM (SPP) connection
            BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
            btSocket.connect();//start connection
            btSocket.getOutputStream().write("0".toString().getBytes());
        } catch (IOException ex) {
            Toast.makeText(getApplicationContext(), message + " connect failed!", Toast.LENGTH_SHORT).show();
        }

        SetupListener();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Stop the Bluetooth chat services
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private  void SetupListener(){
        final ImageButton leftUpBtn = findViewById(R.id.leftUpBtn);
        final ImageButton leftDownBtn = findViewById(R.id.leftDownBtn);
        final ImageButton rightUpBtn = findViewById(R.id.rightUpBtn);
        final ImageButton rightDownBtn = findViewById(R.id.rightDownBtn);
        leftUpBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SendMessage(LEFT_FORWARD);
                        break;
                    case MotionEvent.ACTION_UP:
                        SendMessage(LEFT_FORWARD);
                        break;
                }
                return true;
            }
        });

        leftDownBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SendMessage(LEFT_BACKWARD);
                        break;
                    case MotionEvent.ACTION_UP:
                        SendMessage(LEFT_BACKWARD);
                        break;
                }
                return true;
            }
        });

        rightUpBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SendMessage(RIGHT_FORWARD);
                        break;
                    case MotionEvent.ACTION_UP:
                        SendMessage(RIGHT_FORWARD);
                        break;
                }
                return true;
            }
        });

        rightDownBtn.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        SendMessage(RIGHT_BACKWARD);
                        break;
                    case MotionEvent.ACTION_UP:
                        SendMessage(RIGHT_BACKWARD);
                        break;
                }
                return true;
            }
        });
    }

    private void SendMessage(int message) {
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write((message+"").getBytes());
            } catch (IOException e) {
            }
        }
    }
}
