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

public class UserGraph extends AppCompatActivity {

    Button bLogout;
    TextView adminComment;
    Student user;
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
             items=AdminGraph.getArrayList(json.optJSONArray("comments"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        adminComment.setText(items.get(items.size()-1).toString());

    }

    public void onClick(View view){
        //go back

        Button button = (Button) view;
        startActivity(new Intent(getApplicationContext(), UserDisplay.class));
    }

}
