package com.example.josceyn.walkerapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Point;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.felhr.usbserial.UsbSerialDevice;
import com.felhr.usbserial.UsbSerialInterface;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;


public class UserDisplay extends Activity implements View.OnClickListener, AnimationListener {

    public final String ACTION_USB_PERMISSION = "com.example.josceyn.walkerapp.USB_PERMISSION";
    TextView name, userName, textView, leftReading, rightReading, errorCheck;
    ImageView leftArrow, rightArrow;
    Button bLogout, bDetails, bDB, bSettings;
    Student student, patient;
    StudentRepo userRepo;
    PendingIntent mPermissionIntent;
    UsbManager usbManager;
    UsbDevice device;
    UsbSerialDevice serialPort;
    UsbDeviceConnection connection;
    String strBuilder;
    Animation Blink;
    int animationThreshold;
    boolean startRead = false;
    byte[] syncArray;
    boolean startbytefound=false, showVals=false;
    Switch showValues;



   /* public UserDisplay(Student student){
        this.student=student;
    }*/

    // private final String TAG = DeviceListActivity.class.getSimpleName();

    private UsbManager mUsbManager;
    private ListView mListView;
    private TextView mProgressBarTitle;
    private ProgressBar mProgressBar;
    String str = new String();
    String test1 = new String();
    String test2 = new String();
    String appendStr = new String();
    String appendStr2 = new String();
    byte [] data=new byte[0];
    byte iNBbyte=13;

    byte[] buffer=new byte[1024];
    int bufferSize;
    boolean isAck=false;

    public boolean isStartByte(byte firstChar){
        System.out.println("THE FIRST BYTE IS: "+ firstChar);
        if(firstChar==65){
            System.out.println("YAY!!!");
            return true;
        }
        else{
            return false;
        }
    }
    public void clearBytes(){

        Arrays.fill(buffer, (byte) 0);
        bufferSize=0;

    }
    public void appendBytes(byte[] buf){

      System.arraycopy(buf,0,buffer,bufferSize,buf.length);
        bufferSize+=buf.length;

    }
    public void checkData(){

        System.out.println("CHECK DATA!");
        String testStr="";
        try {
            testStr=new String(buffer,"UTF-8");
            String rightSide=testStr.substring(3,6);
            String leftSide=String.valueOf(testStr.charAt(10))+String.valueOf(testStr.charAt(11))+String.valueOf(testStr.charAt(12));


          if(showVals) {
              //show readings
              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      leftReading.setVisibility(View.VISIBLE);
                      rightReading.setVisibility(View.VISIBLE);
                  }
              });


              tvAppend(leftReading, leftSide);
              tvAppend(rightReading, rightSide);
          }
            else{
              //hide readings

              runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      leftReading.setVisibility(View.INVISIBLE);
                      rightReading.setVisibility(View.INVISIBLE);
                  }
              });
          }
            animate(leftSide,rightSide);
            addDataToDB(leftSide,rightSide);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return;
        }

    }



    UsbSerialInterface.UsbReadCallback mCallback = new UsbSerialInterface.UsbReadCallback() { //Defining a Callback which triggers whenever data is read.
        @Override
        public void onReceivedData(byte[] arg0) {

               // data = new String(arg0, "UTF-8");


                //try this for now
            System.out.println("ON RECEIVED DATA!");

                if(arg0!= null){
                    if(arg0.length > 0){
                        System.out.println("HERE HERE");
                        if (isStartByte(arg0[0])&& !startbytefound) { //look if its a new frame
                            startbytefound = true;
                            clearBytes(); // clears my buffer
                        }
                        appendBytes(arg0);

                        if(bufferSize >= iNBbyte && startbytefound){

                            bufferSize = iNBbyte;
                            byte[] buf = new byte[iNBbyte];
                            System.arraycopy(buffer, 0, buf, 0, bufferSize);
                           // clearBytes();
                           // appendBytes(buf); // append to my buffer

                            checkData(); //process the data
                            startbytefound = false;
                           // isAck = false;
                        }
                    }
                }















               /* data = data.replace("\n", "");

                str = str + data;
                tvAppend(errorCheck, str);
                if (str.contains("A")) {
                    int aIndex = str.indexOf('A');
                    test1 = str.substring(aIndex);
                    // System.out.println("finds wA string"test1);
                    if (test1.length() > 6) {

                        StringBuilder testtest = new StringBuilder();
                        testtest.append(test1.charAt(3));
                        testtest.append(test1.charAt(4));
                        testtest.append(test1.charAt(5));
                        // tvAppend(errorCheck, testtest.toString() + " SIZE IS: " + testtest.length());
                        //  appendStr=Integer.parseInt(testtest.toString());
                        appendStr = testtest.toString();

                    } else if (test1.length() == 6) {
                        //  tvAppend(errorCheck,test1);
                        StringBuilder testtest = new StringBuilder();
                        testtest.append(test1.charAt(3));
                        testtest.append(test1.charAt(4));
                        testtest.append(test1.charAt(5));
                        //  appendStr=Integer.parseInt(testtest.toString());
                        appendStr = testtest.toString();
                    }

                }
                if (str.contains("B")) {
                    int bIndex = str.indexOf('B');
                    test2 = str.substring(bIndex);
                    if (test2.length() > 6) {
                        StringBuilder testtest = new StringBuilder();
                        testtest.append(test2.charAt(3));
                        testtest.append(test2.charAt(4));
                        testtest.append(test2.charAt(5));
                        //   appendStr2=Integer.parseInt(testtest.toString());
                        appendStr2 = testtest.toString();


                        try {
                            // int leftWeight=Integer.parseInt(appendStr);
                            // int rightWeight=Integer.parseInt(appendStr2);
                            tvAppend(leftReading, appendStr.toString());
                            tvAppend(rightReading, appendStr2.toString());
                            //  strBuilder = "";


                            animate(appendStr.toString(), appendStr2.toString());
                            addDataToDB(appendStr.toString(), appendStr2.toString());
                        } catch (NumberFormatException e) {
                            //  startRead=false;

                            //  return;
                            // errorCheck.setText(strBuilder);
                            //  strBuilder="";
                          *//*  byte test[]={Byte.parseByte("1")};
                            serialPort.write(test);*//*
                        }


                        str = test2.substring(6);
                    } else if (test2.length() == 6) {
                        StringBuilder testtest = new StringBuilder();
                        testtest.append(test2.charAt(3));
                        testtest.append(test2.charAt(4));
                        testtest.append(test2.charAt(5));
                        //  appendStr2=Integer.parseInt(testtest.toString());
                        appendStr2 = testtest.toString();


                        try {
                            // int leftWeight=Integer.parseInt(appendStr);
                            // int rightWeight=Integer.parseInt(appendStr2);
                            tvAppend(leftReading, appendStr.toString());
                            tvAppend(rightReading, appendStr2.toString());
                            //  strBuilder = "";


                            animate(appendStr.toString(), appendStr2.toString());
                            addDataToDB(appendStr.toString(), appendStr2.toString());
                        } catch (NumberFormatException e) {
                            //  startRead=false;

                            //  return;
                            // errorCheck.setText(strBuilder);
                            //  strBuilder="";
                          *//*  byte test[]={Byte.parseByte("1")};
                            serialPort.write(test);*//*
                        }


                        str = "";
                    }
                }*/

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




        }
    };
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    //add data and timestamp to database of user to be able to graph
    public void addDataToDB(String leftRead, String rightRead) {
        System.out.println("IN ADD TO DB, LEFT READ: "+leftRead + "RIGHT READ: "+ rightRead);
        //convert string data to integer data
        int leftWeight, rightWeight;
        try {
            leftWeight = Integer.parseInt(leftRead);
            rightWeight = Integer.parseInt(rightRead);
        } catch (NumberFormatException e) {
            System.out.println("CATCH IN ADD TO DB");
            return;
        }


        //array list for data
        ArrayList usbData = new ArrayList();
        //used to convert string data from db to arraylist
        JSONObject json = null;
        try {
            //if new data, do this
            if (patient.usbdata == null) {
                patient.usbdata = "";
                //add to first index of array
                usbData.add(0, userRepo.getCurrentTimeStamp() + "," + leftWeight + "," + rightWeight);


                //instantiate JSONObject
                json = new JSONObject();
                json.put("usbdata", new JSONArray(usbData));

                //update selected user with usbdata
                patient.usbdata = json.toString();
                userRepo.update(patient);

            }
            //if already data for user, append new data
            else {
                json = new JSONObject(patient.usbdata.toString());
                usbData = userRepo.getArrayList(json.optJSONArray("usbdata"));
                usbData.add(usbData.size(), userRepo.getCurrentTimeStamp() + "," + leftWeight + "," + rightWeight);
                json.put("usbdata", new JSONArray(usbData));

                //update patient with usb data-DELETE LATER-debugging purposes
                patient.usbdata = json.toString();
                userRepo.update(patient);
            }
        } catch (JSONException e) {
            System.out.println("TROUBLE SAVING DATA!");
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
                        if(serialPort.open()){
                       // if (serialPort.syncOpen()) { //Set Serial Connection Parameters.
                            serialPort.setBaudRate(9600);
                            serialPort.setDataBits(UsbSerialInterface.DATA_BITS_8);
                            serialPort.setStopBits(UsbSerialInterface.STOP_BITS_1);
                            serialPort.setParity(UsbSerialInterface.PARITY_NONE);
                            serialPort.setFlowControl(UsbSerialInterface.FLOW_CONTROL_OFF);
                            System.out.println("EXPECTING DATA...");

/*
                                serialPort.syncRead(syncArray, 0);


                                byte rightRead = syncArray[3];
                                final String value = new String(syncArray, 13);
                                String hello = new String(syncArray, 0, 13);
                                final String hel = new String(syncArray, StandardCharsets.UTF_8);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                   errorCheck.setText(value);

                                }
                            });


                                tvAppend(leftReading, value.substring(3, 6));
                                tvAppend(rightReading, value.substring(10));*/


                             serialPort.read(mCallback);



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
                            System.out.println("PORT NOT OPEN");
                            Log.d("SERIAL", "PORT NOT OPEN");
                        }
                    } else {
                        System.out.println("PORT IS NULL");
                        Log.d("SERIAL", "PORT IS NULL");
                    }
                } else {
                    System.out.println("PERM NOT GRANTED");
                    Log.d("SERIAL", "PERM NOT GRANTED");
                }
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_ATTACHED)) {
                System.out.println("USB DEVICE ATTACHED");
                onUSBStart();
            } else if (intent.getAction().equals(UsbManager.ACTION_USB_DEVICE_DETACHED)) {
                System.out.println("USB DEVICE DETACHED");
                onUSBStop();

            }
        }

        ;
    };

    //blinking arrow and sound animation
    public void animate(String appendStr, String appendStr2) {
        //convert to integers
        int leftWeight, rightWeight;
        try {
            leftWeight = Integer.parseInt(appendStr);
            rightWeight = Integer.parseInt(appendStr2);
        } catch (NumberFormatException e) {
            System.out.println("CATCH INTEGER ERROR!");
            return;
        }
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.chord);


        if (Math.abs(leftWeight - rightWeight) >= animationThreshold) {
            if (leftWeight > rightWeight) {
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


            } else {
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
        } else {
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

        animationThreshold=10;
        syncArray = new byte[33];
        userRepo = new StudentRepo(this);
        userName = (TextView) findViewById(R.id.userName);
        bDetails = (Button) findViewById(R.id.bDetails);
        bLogout = (Button) findViewById(R.id.bLogout);
        leftArrow = (ImageView) findViewById(R.id.leftArrow);
        rightArrow = (ImageView) findViewById(R.id.rightArrow);
        leftReading = (TextView) findViewById(R.id.leftReading);
        rightReading = (TextView) findViewById(R.id.rightReading);
        errorCheck = (TextView) findViewById(R.id.errorCheck);
        showValues=(Switch) findViewById(R.id.showValues);
        bSettings=(Button) findViewById(R.id.bSettings);

        //debug
        System.out.println("Animation tresh is: "+ animationThreshold);

        leftArrow.setVisibility(View.GONE);
        rightArrow.setVisibility(View.GONE);

        bDetails.setOnClickListener(this);
        bLogout.setOnClickListener(this);
        bSettings.setOnClickListener(this);
        showValues.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    showVals=true;
                }
                else{
                    showVals=false;
                }
            }
        });
        // load the animation
        Blink = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.blinking_animation);

        // set animation listener
        Blink.setAnimationListener(this);


        strBuilder = "";

        usbManager = (UsbManager) getSystemService(this.USB_SERVICE);
        Intent intent = getIntent();
        userName.setText(intent.getStringExtra("user_name"));
        patient = userRepo.getStudentByUsername(userName.getText().toString());
        patient.animationThreshold=10;


        System.out.println("In user display: " + userName.getText());
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
        filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
        registerReceiver(broadcastReceiver, filter);
        onUSBStart();
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
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
                    System.out.println("CONNECTION IS NULL");
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
        if (v == findViewById(R.id.bDetails)) {
            unregisterReceiver(broadcastReceiver);

            Intent main = new Intent(this, UserGraph.class);
            System.out.println("GOING TO GRAPH, USER IS: "+ userName.getText());
            if(patient.usbdata==null){
                System.out.println("USER DATA IS NULL BEFORE GRAPHING");
            }


            //on usb stop
            if(connection!=null) {
                onUSBStop();
            }
            Bundle extras = new Bundle();
            extras.putString("username",userName.getText().toString());
          //  extras.putString("animationThreshold", String.valueOf(animationThreshold));
            main.putExtras(extras);

           // main.putExtra("username", userName.getText());
            startActivity(main);
        } else if (v == findViewById(R.id.bLogout)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
        else if(v==findViewById(R.id.bSettings)){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Unequal Weight Threshold");

// Set up the input
            final EditText input = new EditText(this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
            input.setInputType(InputType.TYPE_CLASS_TEXT);
            builder.setView(input);

// Set up the buttons
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    animationThreshold = Integer.parseInt(input.getText().toString());
                    patient.animationThreshold=animationThreshold;
                    dialog.cancel();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
            System.out.println("animation threshold is: "+ animationThreshold);
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

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "UserDisplay Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.josceyn.walkerapp/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "UserDisplay Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.example.josceyn.walkerapp/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
