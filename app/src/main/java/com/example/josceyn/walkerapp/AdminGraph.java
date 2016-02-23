package com.example.josceyn.walkerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class AdminGraph extends Activity implements View.OnClickListener {

    TextView adminComment;
    Button bLogout, bSubmit;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_graph);

        //get selected user
        StudentRepo repo=new StudentRepo(this);
        Intent intent=getIntent();
        String username=intent.getStringExtra("username");
        Student selectedUser=repo.getStudentByUsername(username);



        adminComment=(TextView) findViewById(R.id.adminComment);
        bLogout=(Button) findViewById(R.id.bLogout);
        bSubmit=(Button) findViewById(R.id.bSubmit);


        bLogout.setOnClickListener(this);
        bSubmit.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        //if logout, bring back to login page
        if(v==findViewById(R.id.bLogout)){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        else{

        }

    }
}
