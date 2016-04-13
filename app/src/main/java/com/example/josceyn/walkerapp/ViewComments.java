package com.example.josceyn.walkerapp;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ViewComments extends Activity implements View.OnClickListener{
    Student user;
    StudentRepo userHelper;
    Button bBack, bLogout;
    TextView username;
    ListView commentList;
    ArrayList adminComments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);

        //get user
        Intent intent = getIntent();
        userHelper = new StudentRepo(this);
        String patientUsername = intent.getStringExtra("user_name");
        user = userHelper.getStudentByUsername(patientUsername);

        //initialize buttons and set listeners
        username = (TextView) findViewById(R.id.username);
        bBack = (Button) findViewById(R.id.bBack);
        bLogout = (Button) findViewById(R.id.bLogout);
        bBack.setOnClickListener(this);
        bLogout.setOnClickListener(this);

        //set username textview
        username.setText(patientUsername);

        //set listview to user's comments
        //get list view
        //get list of users that list admin as their PT
        commentList=(ListView) findViewById(R.id.commentList);
        try {
            if (user.comments != null) {
                JSONObject json = new JSONObject(user.comments);
                adminComments = userHelper.getArrayList(json.optJSONArray("comments"));
                //  adminComment.setText(adminComments.get(adminComments.size()-1).toString());
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                        android.R.layout.simple_list_item_1, adminComments);

                commentList.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
        @Override
    public void onClick(View v) {
        if(v==findViewById(R.id.bBack)){
            Intent userGraph=new Intent(this,UserDisplay.class);
            Bundle bundles=new Bundle();
            bundles.putString("user_name", user.username);
            System.out.println(user.username);
            userGraph.putExtras(bundles);
            startActivity(userGraph);
        }
        else if(v==findViewById(R.id.bLogout)){
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
        }
    }
}
