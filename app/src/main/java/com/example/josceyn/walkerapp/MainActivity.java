package com.example.josceyn.walkerapp;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements android.view.View.OnClickListener {

    Button btnAdd,btnGetAll,bLogin, tvRegisterLink;
    TextView student_Id;
    EditText etUsername, etPassword;



    @Override
    public void onClick(View view) {
        Dialog dialog = new Dialog(MainActivity.this);

        if (view== findViewById(R.id.tvRegisterLink)){

            Intent intent = new Intent(this,StudentDetail.class);
            intent.putExtra("student_Id",0);
            startActivity(intent);

        }
        else
        {

            if (etPassword.getText().toString().equals(getPasswordOfUser())){
                Toast.makeText(MainActivity.this,
                        "Congrats: Login Successfull", Toast.LENGTH_LONG)
                        .show();
                dialog.dismiss();
                Intent main = new Intent(MainActivity.this, UserDisplay.class);
                startActivity(main);
            } else {
                Toast.makeText(MainActivity.this,
                        "User Name or Password does not match",
                        Toast.LENGTH_LONG).show();
            }
        }

        /*else {

            StudentRepo repo = new StudentRepo(this);

            ArrayList<HashMap<String, String>> studentList =  repo.getStudentList();
            if(studentList.size()!=0) {
                ListView lv = getListView();
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
                        student_Id = (TextView) view.findViewById(R.id.student_Id);
                        String studentId = student_Id.getText().toString();
                        Intent objIndent = new Intent(getApplicationContext(),StudentDetail.class);
                        objIndent.putExtra("student_Id", Integer.parseInt( studentId));
                        startActivity(objIndent);
                    }
                });
                ListAdapter adapter = new SimpleAdapter( MainActivity.this,studentList, R.layout.view_student_entry, new String[] { "id","name"}, new int[] {R.id.student_Id, R.id.student_name});
                setListAdapter(adapter);
            }else{
                Toast.makeText(this,"No student!",Toast.LENGTH_SHORT).show();
            }

        }*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername=(EditText) findViewById(R.id.etUsername);
        etPassword=(EditText) findViewById(R.id.etPassword);

        bLogin=(Button) findViewById(R.id.bLogin);
        tvRegisterLink=(Button) findViewById(R.id.tvRegisterLink);

        tvRegisterLink.setOnClickListener(this);
        bLogin.setOnClickListener(this);

        /*btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnGetAll = (Button) findViewById(R.id.btnGetAll);
        btnGetAll.setOnClickListener(this);*/

    }

    public String getPasswordOfUser(){
        StudentRepo studentRepo=new StudentRepo(this);
        Student student=new Student();
        student.username=etUsername.getText().toString();
        student.password=etPassword.getText().toString();
        Log.d("username: ", student.username);
        Log.d("password: ", student.password);
        return studentRepo.getPasswordOfUser(student);

    }


}
