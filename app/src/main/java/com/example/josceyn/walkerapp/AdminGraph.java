package com.example.josceyn.walkerapp;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.app.ActionBar;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class AdminGraph extends Activity implements View.OnClickListener {

    private DropboxAPI<AndroidAuthSession> dropbox;
    private static String FILE_DIR = "/SmartWalkerData/";
    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "66lfl9cvrzq7gfv";
    private final static String ACCESS_SECRET = "e8f91jvdg0brjm3";
    private boolean isLoggedIn;
    EditText adminComment;
    Button bLogout, bSubmit, bEdit;
    ImageButton bDropbox;
    Student selectedUser;
    StudentRepo repo;
    boolean newComment=true;
    ArrayList<Float> patientDataLeft, patientDataRight;
    ArrayList timeStamps, items, adminComments;
    TextView usernameTextView;
    //BarChart barChart;
    //LineChart lineChart;
    PieChart chart;
    int graphView, dataStart=0, dataEnd=5, leftCounter=0, rightCounter=0, animationThreshold;
    RadioGroup graphType;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_graph);

        //get selected user
        repo=new StudentRepo(this);
        Intent intent=getIntent();
        Bundle extras = intent.getExtras();
        String username=extras.getString("patient_username");
        String admin_username = extras.getString("admin_username");

        selectedUser=repo.getStudentByUsername(username);
        animationThreshold=selectedUser.animationThreshold;

        //initialize username textview
        usernameTextView=(TextView) findViewById(R.id.userName);
        usernameTextView.setText(username);


        //initialize pie graph for users data
        chart=(PieChart)findViewById(R.id.chart);
        //set percent values
        chart.setUsePercentValues(true);

        // enable hole and configure
        chart.setDrawHoleEnabled(true);
        chart.setHoleColorTransparent(true);
        chart.setHoleRadius(7);
        chart.setTransparentCircleRadius(30);

        // enable rotation of the chart by touch
        chart.setRotationAngle(0);
        chart.setRotationEnabled(true);


      /*  bNextSet=(Button) findViewById(R.id.bNextSet);
        bPreviousSet=(Button) findViewById(R.id.bPreviousSet);
        bSwitchGraph=(Button) findViewById(R.id.bSwitchGraph);*/
       /* barChart=(BarChart) findViewById(R.id.chart);
        lineChart=(LineChart) findViewById(R.id.lineChart);*/
        //set default line chart invisible
      //  lineChart.setVisibility(View.INVISIBLE);



        patientDataLeft=new ArrayList();
        patientDataRight=new ArrayList();
        timeStamps=new ArrayList();

        //initialize graph view
        graphType = (RadioGroup) findViewById(R.id.radioGroup);
        graphView=graphType.getCheckedRadioButtonId();
        graphType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                graphView = checkedId;
                patientDataLeft.clear();
                patientDataRight.clear();
                timeStamps.clear();
                getGraphData();
                graph();

            }
        });


        adminComment=(EditText) findViewById(R.id.adminComment);
        bLogout=(Button) findViewById(R.id.bLogout);
        bSubmit=(Button) findViewById(R.id.bSubmit);
        bEdit=(Button) findViewById(R.id.bEdit);
        bDropbox=(ImageButton) findViewById(R.id.dropbox);

        bLogout.setOnClickListener(this);
        bSubmit.setOnClickListener(this);
        bEdit.setOnClickListener(this);
        bDropbox.setOnClickListener(this);
       /* bPreviousSet.setOnClickListener(this);
        bNextSet.setOnClickListener(this);
        bSwitchGraph.setOnClickListener(this);
*/
        loggedIn(false);



        //graph patient's data
        if(selectedUser.usbdata!=null){
            JSONObject json2= null;
            try {
                json2 = new JSONObject(selectedUser.usbdata);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            items=repo.getArrayList(json2.optJSONArray("usbdata"));
            getGraphData();
            graph();
        }


        AndroidAuthSession session;
        AppKeyPair pair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);


        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(pair, AccessType.APP_FOLDER, token);
        } else {
            session = new AndroidAuthSession(pair, AccessType.APP_FOLDER);
        }
        dropbox = new DropboxAPI<AndroidAuthSession>(session);
    }
    public void getGraphData(){
        //separate data


        for(int i=0; i<items.size(); i++){
            String [] temp= items.get(i).toString().split(",");


            if(graphView==R.id.rButtonToday){
                //check month, day, year
                Calendar c=Calendar.getInstance();


                if(Integer.parseInt(temp[0].substring(5,7))==c.get(Calendar.MONTH)+1 && Integer.parseInt(temp[0].substring(8,10))==c.get(Calendar.DAY_OF_MONTH) && Integer.parseInt(temp[0].substring(0,4))==c.get(Calendar.YEAR)){
                    timeStamps.add(temp[0]);
                    patientDataRight.add(Float.parseFloat(temp[1]));
                    patientDataLeft.add(Float.parseFloat(temp[2]));
                }

            }
           /* else if(graphView==R.id.rButtonWeekly){
                //Date of data
                SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date=null;
                try {
                    date= sdf.parse(temp[0].toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //current date before-7 days
                Calendar currentDateBefore7Days=Calendar.getInstance();
                currentDateBefore7Days.add(Calendar.DAY_OF_MONTH,-7);

                if(date.after(currentDateBefore7Days.getTime())){
                    timeStamps.add(temp[0]);
                    patientDataLeft.add(Float.parseFloat(temp[1]));
                    patientDataRight.add(Float.parseFloat(temp[2]));
                }
            }*/ else if (graphView==R.id.rButtonOverall){
                timeStamps.add(temp[0]);
                patientDataLeft.add(Float.parseFloat(temp[1]));
                patientDataRight.add(Float.parseFloat(temp[2]));
            }
            temp=null;
        }
    }
    public void graph(){
        int x=0;
        for(int i=dataStart; i<patientDataLeft.size(); i++){
           /* if(x==5){
                break;
            }*/
            if(Math.abs(patientDataLeft.get(i)-patientDataRight.get(i))>animationThreshold){
                if(patientDataRight.get(i)>patientDataLeft.get(i)){
                    rightCounter++;
                }
                else{
                    leftCounter++;
                }
            }

            /*barEntry=new BarEntry(patientDataLeft.get(i),x);
            entries.add(barEntry);
            barEntry2=new BarEntry(patientDataRight.get(i),x);
            entries2.add(barEntry2);*/

            //get time
        /*    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            Date date= null;
            try {
                date = format.parse(timeStamps.get(i).toString());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println("HERE1");
            Calendar cal= GregorianCalendar.getInstance();
            cal.setTime(date);
            System.out.println("HERE2");

            //todays date
            Calendar calendar=Calendar.getInstance();
            int hour=calendar.get(Calendar.HOUR);
            int minute=calendar.get(Calendar.MINUTE);

            String timeString=hour+":"+minute;*/
         /*   String timeString=timeStamps.get(i).toString().substring(11,13)+":"+timeStamps.get(i).toString().substring(14,16)+":"+timeStamps.get(i).toString().substring(17,19);
            labels.add(timeString);


            ///add line graph points
            float totalOfSides=(patientDataLeft.get(i) + patientDataRight.get(i));
            lineEntries.add(new Entry(totalOfSides,x));
            x++;*/
        }

        double leftPercent=((double)leftCounter/((double)leftCounter+(double)rightCounter))*100.00;
        double rightPercent=((double)rightCounter/((double)leftCounter+(double)rightCounter))*100.00;

        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry((int)leftPercent, 0));
        entries.add(new Entry((int) rightPercent, 1));



        PieDataSet dataset = new PieDataSet(entries, "Percentage of Leaning on Each Side");


// creating labels
        ArrayList<String> labels = new ArrayList<String>();
        labels.add("Left Side");
        labels.add("Right Side");


        PieData data = new PieData(labels, dataset); // initialize Piedata
        data.setValueTextSize(15f);
        chart.setData(data); //set data into chart


        chart.setDescription("Weight threshold met on each side");  // set the description
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); // set the color

        chart.animateY(5000);


      /*  //initialize line data set
        LineDataSet dataSetLine=new LineDataSet(lineEntries,"Total Pounds");
        dataSetLine.setColors(ColorTemplate.COLORFUL_COLORS);

        BarDataSet barDataSet1=new BarDataSet(entries,"Left Side");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
       // barDataSet1.setBarSpacePercent(5f);
        BarDataSet barDataSet2=new BarDataSet(entries2, "Right Side");
        barDataSet2.setColor(Color.rgb(0, 0, 155));


        //set the line chart data
        LineData dataLine=new LineData(labels,dataSetLine);
        lineChart.setData(dataLine);



        ArrayList<BarDataSet> dataSets=new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);


        //NEED TO FIX
        *//*labels.add(timeStamps.get(0).toString());
        for (int j = 1; j < timeStamps.size() - 1; j++){
            labels.add(j,"");
        }
        labels.add(timeStamps.size() - 1, timeStamps.get(timeStamps.size() - 1).toString());*//*


        data = new BarData(labels, dataSets);
        barChart.setData(data);*/

        //set description based on graph view
        if(graphView==R.id.rButtonToday) {
            chart.setDescription("Today");
        }
        else{
            chart.setDescription("Overall");
        }


       /* //animate graph results
        barChart.animateXY(2000, 2000);
        barChart.invalidate();
*/

        //  LineDataSet dataset = new LineDataSet(entries, "Unequal Weight Distribution");


       /* LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);

        lineChart.setData(data);
        lineChart.animateY(5000);*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = dropbox.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                Editor editor = prefs.edit();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onClick(View v) {


        int position;
        //if logout, bring back to login page
        if (v == findViewById(R.id.bLogout)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }


        //if submitting comment
        else if (v == findViewById(R.id.bSubmit)) {

            ArrayList comments = new ArrayList();
            if (adminComment.getText().toString() != null) {

                JSONObject json = null;
                try {
                    if (selectedUser.comments == null) {
                        selectedUser.comments = "";
                    } else {
                        json = new JSONObject(selectedUser.comments.toString());
                        comments = repo.getArrayList(json.optJSONArray("comments"));
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JSONObject json2 = new JSONObject();
                if (newComment) {
                    System.out.println("NEW COMMENT");
                    position = 0;
                    comments.add(position, repo.getCurrentTimeStamp() + "," + adminComment.getText());

                    try {
                        json2.put("comments", new JSONArray(comments));
                        //update selected user with comments
                        selectedUser.comments = json2.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    position = 0;
                    comments.remove(position);
                    System.out.println("TESTING...GET PREV COMMENT: "+ comments.get(0));

                    comments.add(position, repo.getCurrentTimeStamp()+","+adminComment.getText().toString());
                    System.out.println("Comment is: " + adminComment.getText());

                    try {
                        json2.put("comments", new JSONArray(comments));
                        //update selected user with comments
                        selectedUser.comments = json2.toString();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
                //update database with new comments + timestamp
                repo.update(selectedUser);
                adminComment.setText("");
                confirmationDialog();
            }
        } else if (v == findViewById(R.id.bEdit)) {
            newComment = false;
            String comm = selectedUser.comments;
            if (selectedUser.comments == null) {
                noPreviousCommentsDialog();
                return;
            }

            JSONObject json = null;
            try {
                json = new JSONObject(selectedUser.comments.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList comments = repo.getArrayList(json.optJSONArray("comments"));

            String [] temp=comments.get(0).toString().split(",");
            if(temp.length>=2){
            adminComment.setText(temp[1]);}

        } else if (v == findViewById(R.id.dropbox)) {
            System.out.println("IN DROPBOX CLICK!   ");

            if (isLoggedIn) {
                dropbox.getSession().unlink();
                loggedIn(false);
                System.out.println("IN LGGED IN");
            } else {
                dropbox.getSession().startAuthentication(AdminGraph.this);
            }

            FILE_DIR=FILE_DIR+selectedUser.name+"/";
            UploadFileToDropbox upload = new UploadFileToDropbox(this, dropbox,
                    FILE_DIR, selectedUser);
            upload.execute();
        } /*else if (v == findViewById(R.id.bPreviousSet)) {
            for (int i = 5; i > 0; i--) {
                if (dataStart - i >= 0) {
                    dataStart -= i;
                    break;
                }
            }

            getGraphData();
            graph();
        } else if (v == findViewById(R.id.bNextSet)) {
            for (int i = 5; i > 0; i--) {
                if (dataStart + i < items.size()) {
                    dataStart += i;
                    break;
                }
            }*/
            if(selectedUser.usbdata!=null) {
                getGraphData();
                graph();
            }


    }
    public void loggedIn(boolean isLogged) {
        isLoggedIn = isLogged;
    }
    public void noPreviousCommentsDialog(){
        //confirmation dialog
        Toast.makeText(AdminGraph.this,
                "Comment submitted!",
                Toast.LENGTH_LONG).show();

        adminComment.setText("");

    }
    public void confirmationDialog(){
        //confirmation dialog
        Toast.makeText(AdminGraph.this,
                "Comment submitted!",
                Toast.LENGTH_LONG).show();

        adminComment.setText("");
    }

}
