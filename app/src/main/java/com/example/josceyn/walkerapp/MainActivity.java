package com.example.josceyn.walkerapp;


import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity implements android.view.View.OnClickListener {

    Button btnAdd,btnGetAll,bLogin;
    TextView student_Id, tvRegisterLink;
    EditText etUsername, etPassword;
    Student student;

    @Override
    public void onClick(View view) {
        Dialog dialog = new Dialog(MainActivity.this);
        student=new Student();

        if (view== findViewById(R.id.tvRegisterLink)){

            Intent intent = new Intent(this,StudentDetail.class);
            intent.putExtra("student_Id",0);
            startActivity(intent);

        }
        else
        {

            if (etPassword.getText().toString().equals(getPasswordOfUser())){
                if(!isAdmin(student)) {
                    Toast.makeText(MainActivity.this,
                            "Congrats: Login Successfull", Toast.LENGTH_LONG)
                            .show();
                    dialog.dismiss();
                    Intent main = new Intent(getApplicationContext(), UserDisplay.class);
                    main.putExtra("user_name", student.name);
                    startActivity(main);
                }
                else{
                    displayAdminPage(student);
                }
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
    public void displayAdminPage(Student student){

        Intent intent = new Intent(this,AdminView.class);
        intent.putExtra("username",student.username);
        startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsername=(EditText) findViewById(R.id.etUsername);
        etPassword=(EditText) findViewById(R.id.etPassword);

        bLogin=(Button) findViewById(R.id.bLogin);
        tvRegisterLink=(TextView) findViewById(R.id.tvRegisterLink);

        tvRegisterLink.setOnClickListener(this);
        bLogin.setOnClickListener(this);

        /*btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnGetAll = (Button) findViewById(R.id.btnGetAll);
        btnGetAll.setOnClickListener(this);*/

    }

    public String getPasswordOfUser(){
        StudentRepo studentRepo=new StudentRepo(this);
        student.username=etUsername.getText().toString();
        student.password=etPassword.getText().toString();


        return studentRepo.getPasswordOfUser(student).password;

    }

    public boolean isAdmin(Student student){
        if(student.admin==1){
            return true;
            }
        return false;

    }


}
