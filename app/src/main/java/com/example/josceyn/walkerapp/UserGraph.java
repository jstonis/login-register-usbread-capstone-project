package com.example.josceyn.walkerapp;

import android.content.Intent;
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
    ArrayList<Float> patientData;
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
        patientData=new ArrayList();
        timeStamps=new ArrayList();
        user=new Student();


        graphType = (RadioGroup) findViewById(R.id.radioGroup);
        graphType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                graphView=checkedId;
                patientData.clear();
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
                items=userRepo.getArrayList(json2.optJSONArray("usbData"));
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

        LineChart lineChart = (LineChart) findViewById(R.id.chart);


        ArrayList<Entry> entries = new ArrayList<>();

        for(int i=0; i<patientData.size(); i++){
            entries.add(new Entry(patientData.get(i),i));
        }

        LineDataSet dataset = new LineDataSet(entries, "Unequal Weight Distribution");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add(timeStamps.get(0).toString());
        for (int j = 1; j < timeStamps.size() - 1; j++){
            labels.add(j,"");
        }
        labels.add(timeStamps.size()-1, timeStamps.get(timeStamps.size()-1).toString());


        LineData data = new LineData(labels, dataset);
        dataset.setColors(ColorTemplate.COLORFUL_COLORS); //
        dataset.setDrawCubic(true);
        dataset.setDrawFilled(true);

        lineChart.setData(data);
        lineChart.animateY(5000);
    }

    public void onClick(View view){
        //go back

        Button button = (Button) view;
        startActivity(new Intent(getApplicationContext(), UserDisplay.class));
    }
    public void getGraphData(){
        //separate data
        for(int i=0; i<items.size(); i++){
            String [] temp= items.get(i).toString().split(",");

            if(graphView==R.id.rButtonToday){
                try{
                    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date=format.parse(temp[0]);
                    Calendar cal= GregorianCalendar.getInstance();
                    cal.setTime(date);

                    //todays date
                    Calendar calendar=Calendar.getInstance();
                    int day=calendar.get(Calendar.DAY_OF_WEEK);

                    if(cal.get(Calendar.DAY_OF_WEEK)==day){
                        timeStamps.add(temp[0]);
                        patientData.add(Float.parseFloat(temp[1]));
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
                    patientData.add(Float.parseFloat(temp[1]));
                }
            } else if (graphView==R.id.rButtonOverall){
                timeStamps.add(temp[0]);
                patientData.add(Float.parseFloat(temp[1]));
            }
            temp=null;
        }
    }
    public void convertDataTo2Arrays(ArrayList strData){
        //initialize arrays with capacities
        patientData=new ArrayList(strData.size());
        timeStamps=new ArrayList(strData.size());

        //separate data
        for(int i=0; i<strData.size(); i++){
            String [] temp= strData.get(i).toString().split(",");
            timeStamps.add(temp[0]);
            patientData.add(Float.parseFloat(temp[1]));
        }
    }


}
