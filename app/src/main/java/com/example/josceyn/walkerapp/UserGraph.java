package com.example.josceyn.walkerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

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
    TextView adminComment;
    Student user;
    ArrayList patientData;
    ArrayList timeStamps;
/*
GraphView graph=(GraphView)findViewById(R.id.graph);
    LineGraphSeries<DataPoint> series=new LineGraphSeries<DataPoint>(new DataPoint[] {
        new DataPoint(0,1),
            new DataPoint(1,5),
    new DataPoint(2,3),
            new DataPoint(3,2),
            new DataPoint(4,6)

    })
*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_graph);

        StudentRepo userRepo=new StudentRepo(this);
        bLogout=(Button) findViewById(R.id.bLogout);
        adminComment=(TextView) findViewById(R.id.adminComment);

        Intent intent=getIntent();
        String username=intent.getStringExtra("username");
        user=userRepo.getStudentByUsername(username);
        ArrayList items=new ArrayList();

        try {
            JSONObject json=new JSONObject(user.comments);
             items=userRepo.getArrayList(json.optJSONArray("comments"));
            graph(items);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adminComment.setText(items.get(items.size()-1).toString());

    }
    public void graph(ArrayList graphData){
        //convert graph data to 2 arrays, 1 for data points, 1 for labels
        convertDataTo2Arrays(graphData);
        //title will be either "today", weekly update, or overall progress
        //find corresponding data for selection
        //graph



        LineChart lineChart = (LineChart) findViewById(R.id.chart);


        ArrayList<Entry> entries = new ArrayList<>();
        entries.add(new Entry(20, 0));
        entries.add(new Entry(15, 1));
        entries.add(new Entry(18, 2));
        entries.add(new Entry(4, 3));
        entries.add(new Entry(3, 4));
        entries.add(new Entry(2, 5));

        LineDataSet dataset = new LineDataSet(entries, "# of Calls");

        ArrayList<String> labels = new ArrayList<String>();
        labels.add("January");
        labels.add("February");
        labels.add("March");
        labels.add("April");
        labels.add("May");
        labels.add("June");

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
    public void convertDataTo2Arrays(ArrayList strData){
        //initialize arrays with capacities
        patientData=new ArrayList(strData.size());
        timeStamps=new ArrayList(strData.size());

        //separate data
        for(int i=0; i<strData.size(); i++){
            String [] temp= strData.get(i).toString().split(",");
            timeStamps.add(temp[0]);
            patientData.add(temp[1]);
        }
    }


}
