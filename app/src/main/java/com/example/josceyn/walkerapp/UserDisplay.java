package com.example.josceyn.walkerapp;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;


public class UserDisplay extends Activity implements View.OnClickListener, AnimationListener{

    public final String ACTION_USB_PERMISSION = "com.example.josceyn.walkerapp.USB_PERMISSION";
    TextView name,userName, textView, leftReading, rightReading, errorCheck;
    ImageView leftArrow, rightArrow;
    Button bLogout, bDetails, bDB;
    Student student, patient;
    StudentRepo userRepo;
    PendingIntent mPermissionIntent;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    String strBuilder;
    Animation Blink;
    int animationThreshold=10;
    boolean startRead=false;


   /* public UserDisplay(Student student){
        this.student=student;
    }*/

    // private final String TAG = DeviceListActivity.class.getSimpleName();

    private UsbManager mUsbManager;
    private ListView mListView;
    private TextView mProgressBarTitle;
    private ProgressBar mProgressBar;
    String str=new String();
    String test1=new String();
    String test2=new String();
    String appendStr=new String();
    String appendStr2=new String();

    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {

            String data = null;
            try {
                data = new String(arg0, "UTF-8");
                data=data.replace("\n","");

                str=str+data;
                tvAppend(errorCheck,str);
                if(str.contains("A")){
                    int aIndex=str.indexOf('A');
                    test1=str.substring(aIndex);
                   // System.out.println("finds wA string"test1);
                    if(test1.length()>6){

                        StringBuilder testtest=new StringBuilder();
                        testtest.append(test1.charAt(3));
                        testtest.append(test1.charAt(4));
                        testtest.append(test1.charAt(5));
                       // tvAppend(errorCheck, testtest.toString() + " SIZE IS: " + testtest.length());
                      //  appendStr=Integer.parseInt(testtest.toString());
                        appendStr=testtest.toString();

                    }
                    else if(test1.length()==6){
                      //  tvAppend(errorCheck,test1);
                        StringBuilder testtest=new StringBuilder();
                        testtest.append(test1.charAt(3));
                        testtest.append(test1.charAt(4));
                        testtest.append(test1.charAt(5));
                      //  appendStr=Integer.parseInt(testtest.toString());
                        appendStr=testtest.toString();
                    }

                }
                if(str.contains("B")){
                    int bIndex=str.indexOf('B');
                    test2=str.substring(bIndex);
                    if(test2.length()>6){
                        StringBuilder testtest=new StringBuilder();
                        testtest.append(test2.charAt(3));
                        testtest.append(test2.charAt(4));
                        testtest.append(test2.charAt(5));
                     //   appendStr2=Integer.parseInt(testtest.toString());
                        appendStr2=testtest.toString();


                        try{
                            // int leftWeight=Integer.parseInt(appendStr);
                            // int rightWeight=Integer.parseInt(appendStr2);
                            tvAppend(leftReading, appendStr.toString());
                            tvAppend(rightReading, appendStr2.toString());
                            //  strBuilder = "";


                            animate(appendStr.toString(), appendStr2.toString());
                            addDataToDB(appendStr.toString(), appendStr2.toString());
                        }
                        catch(NumberFormatException e){
                            //  startRead=false;

                            //  return;
                            // errorCheck.setText(strBuilder);
                            //  strBuilder="";
                          /*  byte test[]={Byte.parseByte("1")};
                            serialPort.write(test);*/
                        }




                        str=test2.substring(6);
                    }
                    else if(test2.length()==6){
                        StringBuilder testtest=new StringBuilder();
                        testtest.append(test2.charAt(3));
                        testtest.append(test2.charAt(4));
                        testtest.append(test2.charAt(5));
                      //  appendStr2=Integer.parseInt(testtest.toString());
                        appendStr2=testtest.toString();


                        try{
                            // int leftWeight=Integer.parseInt(appendStr);
                            // int rightWeight=Integer.parseInt(appendStr2);
                            tvAppend(leftReading, appendStr.toString());
                            tvAppend(rightReading, appendStr2.toString());
                            //  strBuilder = "";


                            animate(appendStr.toString(), appendStr2.toString());
                            addDataToDB(appendStr.toString(), appendStr2.toString());
                        }
                        catch(NumberFormatException e){
                            //  startRead=false;

                            //  return;
                            // errorCheck.setText(strBuilder);
                            //  strBuilder="";
                          /*  byte test[]={Byte.parseByte("1")};
                            serialPort.write(test);*/
                        }



                        str="";
                    }
                }

               /* if(data!=null && data.length()>0) {
                    if (data.charAt(0) == 'A') {
                        startRead = true;
                    }
                }
                if(startRead==true) {
                    if (strBuilder.length() < 22) {
                        // tvAppend(leftReading,"");
                        strBuilder += data;
                    } else {

                        //String leftSide = strBuilder.substring(5);
                      //  String newStr = strBuilder.trim();
                        String newStr=strBuilder.substring(0,21);

                        if(strBuilder.length()>21) {

                            String test = strBuilder.substring(21);
                            StringBuilder sb=new StringBuilder(test);
                            for(int i=0; i<sb.length(); i++){
                                if(test.charAt(i)=='\n'){
                                    tvAppend(errorCheck,"YES!!");
                                    sb.deleteCharAt(i);
                                }
                                else if(test.charAt(i)=='\\'){
                                    tvAppend(errorCheck,"UH OH");
                                }

                            }
                            test=sb.toString();

                            // strBuilder=test;
                            String testTrim = test.trim();
                            if(testTrim.contains("\n")){
                                tvAppend(errorCheck,"CONTAINS NL "+testTrim);
                            }
                            else {
                                tvAppend(errorCheck, testTrim);
                            }
                            strBuilder = testTrim;
                        }
                        else{
                            strBuilder="";
                        }
                        String appendStr = newStr.substring(18);
                        String appendStr2 = newStr.substring(7, 10);
*/

                       /* try{
                           // int leftWeight=Integer.parseInt(appendStr);
                           // int rightWeight=Integer.parseInt(appendStr2);
                            tvAppend(leftReading, appendStr.toString());
                            tvAppend(rightReading, appendStr2.toString());
                          //  strBuilder = "";


                            animate(appendStr.toString(), appendStr2.toString());
                            addDataToDB(appendStr.toString(), appendStr2.toString());
                        }
                        catch(NumberFormatException e){
                          //  startRead=false;

                          //  return;
                           // errorCheck.setText(strBuilder);
                         //  strBuilder="";
                          *//*  byte test[]={Byte.parseByte("1")};
                            serialPort.write(test);*//*
                        }
*/


                //    }

             //   }

            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }



        }
    };

    //add data and timestamp to database of user to be able to graph
    public void addDataToDB(String leftRead, String rightRead){
        //convert string data to integer data
        int leftWeight, rightWeight;
        try {
            leftWeight = Integer.parseInt(leftRead);
            rightWeight = Integer.parseInt(rightRead);
        }
        catch(NumberFormatException e){
            return;
        }


        //array list for data
        ArrayList usbData=new ArrayList();
        //used to convert string data from db to arraylist
        JSONObject json = null;
        try {
            //if new data, do this
            if(patient.usbdata==null){
                patient.usbdata="";
                //add to first index of array
                usbData.add(0,userRepo.getCurrentTimeStamp()+","+ leftWeight + "," + rightWeight);

                //used to debug, DELETE LATER
                final int leftTest=leftWeight;
                final int rightTest=rightWeight;

                //instantiate JSONObject
                json=new JSONObject();
                json.put("usbdata", new JSONArray(usbData));

                //update selected user with usbdata
                patient.usbdata = json.toString();
                userRepo.update(patient);

            }
            //if already data for user, append new data
            else {
                json = new JSONObject(patient.usbdata.toString());
                usbData = userRepo.getArrayList(json.optJSONArray("usbdata"));
                usbData.add(usbData.size(), userRepo.getCurrentTimeStamp() + "," +leftWeight + "," + rightWeight);
                json.put("usbdata", new JSONArray(usbData));

                //update patient with usb data-DELETE LATER-debugging purposes
                patient.usbdata = json.toString();
                userRepo.update(patient);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() { //Broadcast Receiver to automatically start and stop the Serial connection.
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ACTION_USB_PERMISSION)) {
                boolean granted = intent.getExtras().getBoolean(UsbManager.EXTRA_PERMISSION_GRANTED);
                if (granted) {
                    connection = usbManager.openDevice(device);
                    serialPort = UsbSerialDevice.createUsbSerialDevice(device, connection);
                    if (serialPort != null) {
                        //if(serialPort.open()){
                        if (serialPort.syncOpen()) { //Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            byte [] syncArray=new byte[13];
                            serialPort.syncRead(syncArray,0);


                           // serialPort.read(mCallback);



                           /* final TextView ftv = leftReading;
                            final TextView ftv1= rightReading;
                            final CharSequence ftext = "connection opened";
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ftv.setText(ftext);
                                    ftv1.setText(ftext);
                                }
                            });*/
                         //   tvAppend(leftReading,"Serial Connection Opened!\n");

                        } else {
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                onUSBStart();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                onUSBStop();

            }
        }

        ;
    };

    //blinking arrow and sound animation
    public void animate(String appendStr, String appendStr2){
        //convert to integers
        int leftWeight, rightWeight;
        try {
            leftWeight = Integer.parseInt(appendStr);
            rightWeight = Integer.parseInt(appendStr2);
        }
        catch(NumberFormatException e){
            return;
        }
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.chord);


        if (Math.abs(leftWeight-rightWeight)>=animationThreshold){
            if(leftWeight>rightWeight) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        leftArrow.setVisibility(View.INVISIBLE);
                        leftArrow.clearAnimation();
                        rightArrow.setVisibility(View.VISIBLE);
                        rightArrow.startAnimation(Blink);
                       // infoText.setText("Lean to the right!");
                        mp.start();

                    }
                });


            }
            else{
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        rightArrow.setVisibility(View.INVISIBLE);
                        rightArrow.clearAnimation();
                        leftArrow.setVisibility(View.VISIBLE);
                        leftArrow.startAnimation(Blink);
                       // infoText.setText("Lean to the left!");
                        mp.start();

                    }
                });
            }
        }
        else{
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    leftArrow.setVisibility(View.INVISIBLE);
                    rightArrow.setVisibility(View.INVISIBLE);
                    leftArrow.clearAnimation();
                    rightArrow.clearAnimation();
                   // infoText.setText("");
                    mp.stop();

                }
            });

        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_display);
        userRepo=new StudentRepo(this);
        userName=(TextView) findViewById(R.id.userName);
        bDetails=(Button) findViewById(R.id.bDetails);
        bLogout=(Button) findViewById(R.id.bLogout);
        leftArrow=(ImageView) findViewById(R.id.leftArrow);
        rightArrow=(ImageView) findViewById(R.id.rightArrow);
        leftReading=(TextView) findViewById(R.id.leftReading);
        rightReading=(TextView) findViewById(R.id.rightReading);
        errorCheck=(TextView) findViewById(R.id.errorCheck);

        leftArrow.setVisibility(View.GONE);
        rightArrow.setVisibility(View.GONE);

        bDetails.setOnClickListener(this);
        bLogout.setOnClickListener(this);
        // load the animation
        Blink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blinking_animation);

        // set animation listener
        Blink.setAnimationListener(this);


        strBuilder="";

        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        Intent intent=getIntent();
        userName.setText(intent.getStringExtra("user_name"));
        patient=userRepo.getStudentByUsername(userName.getText().toString());


        System.out.println("In user display: "+ userName.getText());
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
        onUSBStart();
    }

    public void onUSBStart() {

        HashMap<String, UsbDevice> usbDevices = usbManager.getDeviceList();
        if (!usbDevices.isEmpty()) {
            boolean keep = true;
            for (Map.Entry<String, UsbDevice> entry : usbDevices.entrySet()) {
                device = entry.getValue();
                int deviceVID = device.getVendorId();
                if (deviceVID == 0x2341)//Arduino Vendor ID
                {
                    PendingIntent pi = PendingIntent.getBroadcast(this, 0, new Intent(ACTION_USB_PERMISSION), 0);
                    usbManager.requestPermission(device, pi);
                    keep = false;
                } else {
                    connection = null;
                    device = null;
                }

                if (!keep)
                    break;
            }
        }


    }

    public void onUSBStop() {
        serialPort.close();
     //   tvAppend(leftReading,"\nSerial Connection Closed! \n");
    }

    private void tvAppend(TextView tv, CharSequence text) {
        final TextView ftv = tv;
        final CharSequence ftext = text;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ftv.setText(ftext);
            }
        });
    }


    @Override
    public void onClick(View v) {
        if(v==findViewById(R.id.bDetails)){
            unregisterReceiver(broadcastReceiver);

            Intent main=new Intent(this, UserGraph.class);
            main.putExtra("username", userName.getText());
            startActivity(main);
        }
        else if(v==findViewById(R.id.bLogout)){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }


    }

    @Override
    public void onAnimationStart(Animation animation) {

    }

    @Override
    public void onAnimationEnd(Animation animation) {

    }

    @Override
    public void onAnimationRepeat(Animation animation) {

    }
}
