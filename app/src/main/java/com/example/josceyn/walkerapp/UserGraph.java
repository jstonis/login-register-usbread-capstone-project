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
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class UserGraph extends AppCompatActivity {

    Button bLogout;
    TextView adminComment, userName;
    Student user;
    ArrayList<Float> patientDataLeft, patientDataRight;
    ArrayList timeStamps, items, adminComments;
    RadioGroup graphType;
    int graphView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_graph);

        StudentRepo userRepo=new StudentRepo(this);
        bLogout=(Button) findViewById(R.id.bLogout);
        adminComment=(TextView) findViewById(R.id.adminComment);
        userName=(TextView) findViewById(R.id.userName);
        patientDataLeft=new ArrayList();
        patientDataRight=new ArrayList();
        timeStamps=new ArrayList();
        user=new Student();
        graphType = (RadioGroup) findViewById(R.id.radioGroup);
        graphView=graphType.getCheckedRadioButtonId();
        graphType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                graphView=checkedId;
                patientDataLeft.clear();
                patientDataRight.clear();
                timeStamps.clear();
                getGraphData();
                graph();

            }
        });


        Intent intent=getIntent();
       // String username=intent.getStringExtra("username");
        String test=intent.getStringExtra("username");
        System.out.println("TEST:"+ test);
        userName.setText(intent.getStringExtra("username"));
        user=userRepo.getStudentByUsername(test);
        System.out.println("USERS USERNAME= "+user.username);

        adminComments=new ArrayList();

        items=new ArrayList();
        adminComments=new ArrayList();

        try {
            if(user.comments!=null){
                JSONObject json=new JSONObject(user.comments);
                adminComments=userRepo.getArrayList(json.optJSONArray("comments"));
                adminComment.setText(adminComments.get(adminComments.size()-1).toString());
            }


            if(user.usbdata!=null){
                JSONObject json2=new JSONObject(user.usbdata);
                items=userRepo.getArrayList(json2.optJSONArray("usbdata"));
                getGraphData();
                graph();
            }


        System.out.println("LOOK HERE: "+user.username+" "+ user.password+ " " + user.name+ " "+user.usbdata);


        } catch (JSONException e) {
            System.out.println("ERROR : " +e.toString());
            e.printStackTrace();
        }

        if(adminComments.size()!=0) {
            adminComment.setText(adminComments.get(adminComments.size() - 1).toString());
        }


    }
    public void graph(){

       // LineChart lineChart = (LineChart) findViewById(R.id.chart);
        BarChart barChart=(BarChart) findViewById(R.id.chart);

        BarData data;


        ArrayList<BarEntry> entries = new ArrayList<>();
        BarEntry barEntry;
        for(int i=0; i<patientDataLeft.size(); i++){
            barEntry=new BarEntry(patientDataLeft.get(i),i);
            entries.add(barEntry);
        }

        ArrayList<BarEntry> entries2 = new ArrayList<>();
        for(int i=0; i<patientDataRight.size(); i++){
            barEntry=new BarEntry(patientDataRight.get(i),i);
            entries2.add(barEntry);
        }

        BarDataSet barDataSet1=new BarDataSet(entries,"Left Side");
        barDataSet1.setColor(Color.rgb(0, 155, 0));
        BarDataSet barDataSet2=new BarDataSet(entries2, "Right Side");
        barDataSet2.setColor(Color.rgb(0, 0, 155));

        ArrayList<BarDataSet> dataSets=new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);


        ArrayList<String> labels = new ArrayList<String>();
        labels.add(timeStamps.get(0).toString());
        for (int j = 1; j < timeStamps.size() - 1; j++){
            labels.add(j,"");
        }
        labels.add(timeStamps.size()-1, timeStamps.get(timeStamps.size()-1).toString());


        data = new BarData(labels, dataSets);
        barChart.setData(data);
        barChart.setDescription("My Chart");
        barChart.animateXY(2000, 2000);
        barChart.invalidate();


      //  LineDataSet dataset = new LineDataSet(entries, "Unequal Weight Distribution");


       /* LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);

        lineChart.setData(data);
        lineChart.animateY(5000);*/
    }

    public void onClick(View view){
        //go back

        Button button = (Button) view;
        startActivity(new Intent(getApplicationContext(), UserDisplay.class));
    }
    public void getGraphData(){
        //separate data
        System.out.println("items "+items.size());

        for(int i=0; i<items.size(); i++){
            String [] temp= items.get(i).toString().split(",");


            if(graphView==R.id.rButtonToday){
                try{
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    System.out.println("HERE");
                    Date date=format.parse(temp[0]);
                    System.out.println("HERE1");
                    Calendar cal= GregorianCalendar.getInstance();
                    cal.setTime(date);
                    System.out.println("HERE2");

                    //todays date
                    Calendar calendar=Calendar.getInstance();
                    int day=calendar.get(Calendar.DAY_OF_WEEK);
                    System.out.println("GET DAY OF WEEK OF DATA: "+ cal.get(Calendar.DAY_OF_WEEK));
                    System.out.println("TODAYS DATE "+ day);

                    if(cal.get(Calendar.DAY_OF_WEEK)==day){
                        timeStamps.add(temp[0]);
                        patientDataLeft.add(Float.parseFloat(temp[1]));
                        patientDataRight.add(Float.parseFloat(temp[2]));
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            else if(graphView==R.id.rButtonWeekly){
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
            } else if (graphView==R.id.rButtonOverall){
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
