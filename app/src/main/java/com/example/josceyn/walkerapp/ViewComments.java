package com.example.josceyn.walkerapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;

public class ViewComments extends AppCompatActivity implements View.OnClickListener{
    Student user;
    StudentRepo userHelper;
    Button bBack, bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_comments);

        //get user
        Intent intent=new Intent();
        userHelper=new StudentRepo(this);
        String username=intent.getStringExtra("user_name");
        user=userHelper.getStudentByUsername(username);
        //initialize buttons and set listeners
        bBack=(Button) findViewById(R.id.bBack);
        bLogout=(Button) findViewById(R.id.bLogout);
        bBack.setOnClickListener(this);
        bLogout.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        if(v==findViewById(R.id.bBack)){

        }
        else if(v==findViewById(R.id.bLogout)){

        }
    }
}
