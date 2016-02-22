package com.example.josceyn.walkerapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class StudentDetail extends ActionBarActivity implements android.view.View.OnClickListener{

   /* Button btnSave ,  btnDelete;
    Button btnClose;
    EditText editTextName;
    EditText editTextEmail;
    EditText editTextAge;*/
   public static String EXTRA_MESSAGE="";
    private int _Student_Id=0;

    Button bRegister, btnClose;
    EditText etUsername, etPassword, etName, etPhysicalTherapist;
    CheckBox etAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_detail);

        /*btnSave = (Button) findViewById(R.id.btnSave);
        btnDelete = (Button) findViewById(R.id.btnDelete);
        btnClose = (Button) findViewById(R.id.btnClose);
        */

        btnClose=(Button) findViewById(R.id.btnClose);
        bRegister=(Button) findViewById(R.id.bRegister);


        etUsername=(EditText) findViewById(R.id.etUsername);
        etName=(EditText) findViewById(R.id.etName);
        etPassword=(EditText) findViewById(R.id.etPassword);
        etPhysicalTherapist=(EditText) findViewById(R.id.etPhysicalTherapist);
        etAdmin=(CheckBox) findViewById(R.id.etAdmin);

       /* editTextName = (EditText) findViewById(R.id.editTextName);
        editTextEmail = (EditText) findViewById(R.id.editTextEmail);
        editTextAge = (EditText) findViewById(R.id.editTextAge);*/

        btnClose.setOnClickListener(this);
        bRegister.setOnClickListener(this);

       /* btnSave.setOnClickListener(this);
        btnDelete.setOnClickListener(this);
        btnClose.setOnClickListener(this);
*/

        _Student_Id =0;
        Intent intent = getIntent();
        _Student_Id =intent.getIntExtra("student_Id", 0);
        StudentRepo repo = new StudentRepo(this);
        Student student = new Student();
//        student = repo.getStudentById(_Student_Id);

        /*editTextAge.setText(String.valueOf(student.age));
        editTextName.setText(student.name);
        editTextEmail.setText(student.email);*/

        etUsername.setText(student.username);
        etPassword.setText(student.password);
        etName.setText(student.name);
        etPhysicalTherapist.setText(student.pt);
        if(student.admin==0){etAdmin.setChecked(false);}else{etAdmin.setChecked(true);}

    }



    public void onClick(View view) {
        if (view == findViewById(R.id.bRegister)){
            StudentRepo repo = new StudentRepo(this);
            Student student = new Student();
           /* student.age= Integer.parseInt(editTextAge.getText().toString());
            student.email=editTextEmail.getText().toString();
            student.name=editTextName.getText().toString();*/

            student.name=etName.getText().toString();
            student.username=etUsername.getText().toString();
            student.password=etPassword.getText().toString();
            student.pt=etPhysicalTherapist.getText().toString();
           // if(student.admin==0){etAdmin.setChecked(false);}else{etAdmin.setChecked(true);}

            if(etAdmin.isChecked()==true){
                student.admin=1;
            }
            else
            {
                student.admin=0;
            }
            student.student_ID=_Student_Id;

//            if (_Student_Id==0){
                Dialog dialog=new Dialog(StudentDetail.this);

                if(!repo.alreadyExists(student)){

                    _Student_Id = repo.insert(student);
                    Toast.makeText(StudentDetail.this,
                            "Registration Complete! Login to Continue: ", Toast.LENGTH_LONG)
                            .show();
                    dialog.dismiss();
                    Intent main = new Intent(StudentDetail.this, MainActivity.class);
                    EXTRA_MESSAGE=student.name;
                    startActivity(main);
                }
                else
                {
                    AlertDialog.Builder dialogBuilder=new AlertDialog.Builder(StudentDetail.this);
                    dialogBuilder.setMessage("Username already exists! Create a new username and try again");
                    dialogBuilder.setPositiveButton("Ok", null);
                    dialogBuilder.show();
                }

          /*      _Student_Id = repo.insert(student);

                Toast.makeText(this,"New Student Insert",Toast.LENGTH_SHORT).show();
            }*//*else{

                repo.update(student);
                Toast.makeText(this,"Student Record updated",Toast.LENGTH_SHORT).show();
            }*/
        }/*else if (view== findViewById(R.id.btnDelete)){
            StudentRepo repo = new StudentRepo(this);
            repo.delete(_Student_Id);
            Toast.makeText(this, "Student Record Deleted", Toast.LENGTH_SHORT);
            finish();
        }*/else if (view== findViewById(R.id.btnClose)){
            finish();
        }


    }

}