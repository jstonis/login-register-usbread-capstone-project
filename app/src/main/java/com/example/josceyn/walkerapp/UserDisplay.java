package com.example.josceyn.walkerapp;


import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.hoho.android.usbserial.driver.UsbSerialDriver;
import com.hoho.android.usbserial.driver.UsbSerialPort;
import com.hoho.android.usbserial.driver.UsbSerialProber;

import java.io.IOException;
import java.util.List;

public class UserDisplay extends Activity{

    TextView name,userName;
    Student student;
    PendingIntent mPermissionIntent;
    private static final String ACTION_USB_PERMISSION="com.example.josceyn.walkerapp.USB_PERMISSION";
    UsbDevice device;
    UsbManager manager;

   /* public UserDisplay(Student student){
        this.student=student;
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);
        userName=(TextView) findViewById(R.id.userName);

        Intent intent=getIntent();
        userName.setText(intent.getStringExtra("user_name"));


        // Find all available drivers from attached devices.
        UsbManager manager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<UsbSerialDriver> availableDrivers = UsbSerialProber.getDefaultProber().findAllDrivers(manager);
        if (availableDrivers.isEmpty()) {
            return;
        }

// Open a connection to the first available driver.
        UsbSerialDriver driver = availableDrivers.get(0);
        UsbDeviceConnection connection = manager.openDevice(driver.getDevice());
        if (connection == null) {
            // You probably need to call UsbManager.requestPermission(driver.getDevice(), ..)
            return;
        }

// Read some data! Most have just one port (port 0).
       // UsbSerialPort port = driver.getPort(0);
        List myPortList = driver.getPorts();
        UsbSerialPort port = (UsbSerialPort)myPortList.get(0);

        try {
        port.open(connection);

            port.setParameters(9600,8,1,0);
            byte buffer[] = new byte[16];
            int numBytesRead = port.read(buffer, 1000);
            Log.d("", "Read " + numBytesRead + " bytes.");
            port.close();
        } catch (IOException e) {
            // Deal with error.
        } finally {

        }



       // checkDeviceInfo();


       /* Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
           /* }
        });*/
    }
   /* public void checkDeviceInfo(){
        manager=(UsbManager)getSystemService(Context.USB_SERVICE);


        mPermissionIntent=PendingIntent.getBroadcast(this,0,new Intent(ACTION_USB_PERMISSION),0);
        IntentFilter filter=new IntentFilter(ACTION_USB_PERMISSION);
        registerReceiver(mUsbReceiver,filter);

    }
    private final BroadcastReceiver mUsbReceiver=new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(ACTION_USB_PERMISSION.equals(action)){
                synchronized (this){
                    UsbDevice device=(UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if(intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED,false)){
                        if(device!=null){
                            //call method to set up device
                        }
                    }
                    else{
                        Log.d("Tag", "permission denied for device " + device);
                    }
                }
            }
        }
    };*/

}
