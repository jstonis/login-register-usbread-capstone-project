package com.example.josceyn.walkerapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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


public class UserGraph extends AppCompatActivity implements View.OnClickListener {

    Button bLogout, bNextSet, bPreviousSet, bBack, bSwitchGraph, bComments;
    //TextView adminComment;
    TextView userName;
    Student user;
    ArrayList<Float> patientDataLeft, patientDataRight;
    ArrayList timeStamps, items, adminComments;
    RadioGroup graphType;
    int graphView, dataStart=0, dataEnd=5, leftCounter=0, rightCounter=0, animationThreshold;
    Boolean showBarGraph=true;
    PieChart chart;
    BarChart barChart;
    LineChart lineChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_graph);

        StudentRepo userRepo=new StudentRepo(this);
        bLogout=(Button) findViewById(R.id.bLogout);
       // adminComment=(TextView) findViewById(R.id.adminComment);
        userName=(TextView) findViewById(R.id.userName);
       // bNextSet=(Button) findViewById(R.id.bNextSet);
       // bPreviousSet=(Button) findViewById(R.id.bPreviousSet);
        bBack=(Button) findViewById(R.id.btnBack);
      //  bSwitchGraph=(Button) findViewById(R.id.bSwitchGraph);
        chart=(PieChart) findViewById(R.id.chart);
        bComments=(Button) findViewById(R.id.bComments);
      //  barChart=(BarChart) findViewById(R.id.chart);
       // lineChart=(LineChart) findViewById(R.id.lineChart);
        //set default line chart invisible
//        lineChart.setVisibility(View.INVISIBLE);

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



        //set on click listeners
        bLogout.setOnClickListener(this);
       // bPreviousSet.setOnClickListener(this);
       // bNextSet.setOnClickListener(this);
        //bSwitchGraph.setOnClickListener(this);
        bBack.setOnClickListener(this);
        bComments.setOnClickListener(this);

        patientDataLeft=new ArrayList();
        patientDataRight=new ArrayList();
        timeStamps=new ArrayList();
        user=new Student();
        graphType = (RadioGroup) findViewById(R.id.radioGroup);
        graphView=graphType.getCheckedRadioButtonId();
        graphType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                graphView = checkedId;
                patientDataLeft.clear();
                patientDataRight.clear();
                timeStamps.clear();
                leftCounter=0; rightCounter=0;
                getGraphData();
                graph();

            }
        });

        
        Intent intent=getIntent();
       // String username=intent.getStringExtra("username");
        Bundle extras = intent.getExtras();
       /* String username=extras.getString("patient_username");
        String admin_username = extras.getString("admin_username");*/


       // String test=intent.getStringExtra("username");
      //  animationThreshold=Integer.parseInt(extras.getString("animationThreshold"));
        String test=extras.getString("username");
        userName.setText(extras.getString("username"));
        user=userRepo.getStudentByUsername(test);

       /* if(user.usbdata==null) {



            ArrayList usbData = new ArrayList();
            //used to convert string data from db to arraylist
            JSONObject jsonTest = null;
            try {
                //if new data, do this
                user.usbdata = "";
                //add to first index of array
                usbData.add(0, userRepo.getCurrentTimeStamp() + "," + 30 + "," + 2);


                //instantiate JSONObject
                jsonTest = new JSONObject();
                jsonTest.put("usbdata", new JSONArray(usbData));

                //update selected user with usbdata
                user.usbdata = jsonTest.toString();
                userRepo.update(user);


                //if already data for user, append new data


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }*/
            adminComments=new ArrayList();

        items=new ArrayList();
        adminComments=new ArrayList();

        try {
            if(user.comments!=null){
                JSONObject json=new JSONObject(user.comments);
                adminComments=userRepo.getArrayList(json.optJSONArray("comments"));
              //  adminComment.setText(adminComments.get(adminComments.size()-1).toString());
            }


            if(user.usbdata!=null){

                JSONObject json2=new JSONObject(user.usbdata);
                items=userRepo.getArrayList(json2.optJSONArray("usbdata"));
                leftCounter=0; rightCounter=0;
                getGraphData();
                graph();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(adminComments.size()!=0) {
         //   adminComment.setText(adminComments.get(adminComments.size() - 1).toString());
        }


    }
    public void graph(){
/*
        //bar chart data
        BarData data;
        ArrayList<BarEntry> entries = new ArrayList<>();
        BarEntry barEntry, barEntry2;
        ArrayList<BarEntry> entries2 = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<String>();
        int leftCounter=0, rightCounter=0;

        //Line Graph of sum
        ArrayList<Entry> lineEntries=new ArrayList<>();*/



/*

        if(patientDataLeft.size()<=10) {
            for (int i = 0; i < patientDataLeft.size(); i++) {
                barEntry = new BarEntry(patientDataLeft.get(i), leftCounter);
                entries.add(barEntry);
                labels.add(timeStamps.get(i).toString());
                leftCounter++;

            }


            for (int i = 0; i < patientDataRight.size(); i++) {
                barEntry = new BarEntry(patientDataRight.get(i), rightCounter);
                entries2.add(barEntry);
                labels.add(timeStamps.get(i).toString());
                rightCounter++;
            }
        }
        else if(patientDataLeft.size()>10 && patientDataLeft.size()<20){
            for (int i = 0; i < 10; i++) {
                barEntry = new BarEntry(patientDataLeft.get(i), leftCounter);
                entries.add(barEntry);
                labels.add(timeStamps.get(i).toString());
                leftCounter++;
            }


            for (int i = 0; i < 10; i++) {
                barEntry = new BarEntry(patientDataRight.get(i), rightCounter);
                entries2.add(barEntry);
                labels.add(timeStamps.get(i).toString());
                rightCounter++;
            }
        }
        else{
            int incre=patientDataLeft.size()/8;
            System.out.println("INCREM= "+ incre);


            for (int i = 0; i < patientDataLeft.size(); i+=incre) {

                barEntry = new BarEntry(patientDataLeft.get(i),leftCounter);
                entries.add(barEntry);
                labels.add(timeStamps.get(i).toString());
                leftCounter++;
            }


            for (int i = 0; i < patientDataRight.size(); i+=incre) {
                barEntry = new BarEntry(patientDataRight.get(i), rightCounter);
                entries2.add(barEntry);
                labels.add(timeStamps.get(i).toString());
                rightCounter++;
            }
        }*/

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
        entries.add(new Entry((int)rightPercent, 1));



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


    public void onClick(View view){

        if(view==findViewById(R.id.btnBack)) {

            Intent intent = new Intent(this,UserDisplay.class);
            intent.putExtra("user_name",user.username);
            startActivity(intent);
        }
       /* else if(view==findViewById(R.id.bPreviousSet)){
            for(int i=5; i>0; i--){
                if(dataStart-i>=0){
                    dataStart-=i;
                    break;
                }
            }

            getGraphData();
            graph();
        }*/
       /* else if(view==findViewById(R.id.bNextSet)){
            for(int i=5; i>0; i--){
                if(dataStart+i<items.size()){
                    dataStart+=i;
                    break;
                }
            }

            getGraphData();
            graph();
        }*/
        else if(view==findViewById(R.id.bLogout)){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
        else if(view==findViewById(R.id.bComments)){
            Intent displayComments=new Intent(this, ViewComments.class);
            displayComments.putExtra("user_name",user.username);
            startActivity(displayComments);

        }

       /* else if(view==findViewById(R.id.bSwitchGraph)){

            if(showBarGraph){
                bSwitchGraph.setText("Line Graph");
                barChart.setVisibility(View.VISIBLE);
                lineChart.setVisibility(View.INVISIBLE);
                showBarGraph=false;
            }
            else{
                bSwitchGraph.setText("Bar Graph");
                barChart.setVisibility(View.INVISIBLE);
                lineChart.setVisibility(View.VISIBLE);
                showBarGraph=true;
            }
        }*/
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
    public void convertDataTo2Arrays(ArrayList strData){
        //initialize arrays with capacities
        patientDataLeft=new ArrayList(strData.size());
        patientDataRight=new ArrayList(strData.size());
        timeStamps=new ArrayList(strData.size());

        //separate data
        for(int i=0; i<strData.size(); i++){
            String [] temp= strData.get(i).toString().split(",");
            timeStamps.add(temp[0]);
          //  patientData.add(Float.parseFloat(temp[1]));
        }
    }


}
