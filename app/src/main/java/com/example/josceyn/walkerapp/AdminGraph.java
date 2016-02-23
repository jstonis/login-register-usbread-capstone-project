package com.example.josceyn.walkerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminGraph extends Activity implements View.OnClickListener {

    TextView adminComment;
    Button bLogout, bSubmit;
    Student selectedUser;

    StudentRepo repo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_graph);

        //get selected user
        repo=new StudentRepo(this);
        Intent intent=getIntent();
        String username=intent.getStringExtra("username");
        selectedUser=repo.getStudentByUsername(username);



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
        else if(v==findViewById(R.id.bSubmit)){
            if(adminComment.getText()!=""){
                JSONObject json= null;
                try {
                    json = new JSONObject(selectedUser.comments.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList comments=getArrayList(json.optJSONArray(selectedUser.comments));
                comments.add(comments.size(),adminComment.getText());
                //update selected user with comments
                selectedUser.comments=comments.toString();
                //update database with new comments + timestamp
                repo.update(selectedUser);

                //confirmation dialog
                Toast.makeText(AdminGraph.this,
                        "Comment submitted!",
                        Toast.LENGTH_LONG).show();

                adminComment.setText("");



            }
        }
        else
        {
            if(adminComment.getText()!=""){
                JSONObject json= null;
                try {
                    json = new JSONObject(selectedUser.comments.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                ArrayList comments=getArrayList(json.optJSONArray(selectedUser.comments));
                comments.add(comments.size(),adminComment.getText());
                //update selected user with comments
                selectedUser.comments=comments.toString();
                //update database with new comments + timestamp
                repo.update(selectedUser);

                //confirmation dialog
                Toast.makeText(AdminGraph.this,
                        "Comment edited!",
                        Toast.LENGTH_LONG).show();

                adminComment.setText("");



            }
        }

    }

    public ArrayList getArrayList(JSONArray jsonArray){
        ArrayList<String> list = new ArrayList<String>();
        if (jsonArray != null) {
            int len = jsonArray.length();
            for (int i=0;i<len;i++){
                try {
                    list.add(jsonArray.get(i).toString());
                }
                catch(JSONException e){
                    e.printStackTrace();
                    return null;
                }
            }

        }
        return list;

    }
    //get timestamp
    public static String getCurrentTimeStamp(){
        try{
            SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentTimeStamp=dateFormat.format(new Date()); //find today's date
            return currentTimeStamp;
        }
        catch(Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
