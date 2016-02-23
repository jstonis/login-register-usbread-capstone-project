package com.example.josceyn.walkerapp;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

public class AdminView extends ListActivity implements android.view.View.OnClickListener{

    Student student;
    TextView adminName, student_Id;
    Button bLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_view);
        Intent intent = getIntent();
        StudentRepo studentHelper=new StudentRepo(this);
        bLogout=(Button) findViewById(R.id.bLogout);

        bLogout.setOnClickListener(this);

        //get user
        adminName=(TextView) findViewById(R.id.adminName);
        String username=intent.getStringExtra("username");
        student=studentHelper.getStudentByUsername(username);
        adminName.setText(student.name);



        //get list view
        //get list of users that list admin as their PT
         final ArrayList<HashMap<String, String>> studentList =  studentHelper.getStudentList(student.name);
        if(studentList.size()!=0) {
            final ListView lv = getListView();
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                    String username=studentList.get(position).get("username").toString();

                    Intent objIndent = new Intent(getApplicationContext(),AdminGraph.class);
                    objIndent.putExtra("username", username);
                    startActivity(objIndent);
                }
            });
            // ListAdapter adapter = new SimpleAdapter( AdminView.this,studentList, R.layout.view_student_entry, new String[] { "id","name"}, new int[] {R.id.student_Id, R.id.student_name});
            ListAdapter adapter = new SimpleAdapter(AdminView.this,studentList, R.layout.view_student_entry, new String[] { "id","name"}, new int[] {R.id.student_Id, R.id.student_name});
            setListAdapter(adapter);
        }else{
            Toast.makeText(this, "No student!", Toast.LENGTH_SHORT).show();
        }




    }


    @Override
    public void onClick(View v) {
        if(v==findViewById(R.id.bLogout)){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }

    }
}
