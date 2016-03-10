package com.example.josceyn.walkerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AdminGraph extends Activity implements View.OnClickListener {

    EditText adminComment;
    Button bLogout, bSubmit, bEdit;
    Student selectedUser;
    StudentRepo repo;
    boolean newComment=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_graph);

        //get selected user
        repo=new StudentRepo(this);
        Intent intent=getIntent();
        String username=intent.getStringExtra("username");
        selectedUser=repo.getStudentByUsername(username);



        adminComment=(EditText) findViewById(R.id.adminComment);
        bLogout=(Button) findViewById(R.id.bLogout);
        bSubmit=(Button) findViewById(R.id.bSubmit);
        bEdit=(Button) findViewById(R.id.bEdit);

        bLogout.setOnClickListener(this);
        bSubmit.setOnClickListener(this);
        bEdit.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        //go back

        Button button=(Button) v;
        startActivity(new Intent(getApplicationContext(),AdminView.class));

        int position;
        //if logout, bring back to login page
        if(v==findViewById(R.id.bLogout)){
            Intent intent=new Intent(this,MainActivity.class);
            startActivity(intent);
        }
        else if(v==findViewById(R.id.bSubmit)){

            ArrayList comments=new ArrayList();

                if (adminComment.getText().toString() != null) {

                    JSONObject json = null;
                    try {
                       if(selectedUser.comments==null){
                           selectedUser.comments="";
                       }
                        else {
                           json = new JSONObject(selectedUser.comments.toString());
                           comments = getArrayList(json.optJSONArray("comments"));
                       }


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    JSONObject json2=new JSONObject();
                    if (newComment) {
                        position = 0;
                        comments.add(position, getCurrentTimeStamp() + ":" + adminComment.getText());

                        try {
                            json2.put("comments",new JSONArray(comments));
                            //update selected user with comments
                            selectedUser.comments = json2.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        position = comments.size() - 1;
                        comments.remove(position);
                        comments.add(position, adminComment.getText());

                        try {
                            json2.put("comments",new JSONArray(comments));
                            //update selected user with comments
                            selectedUser.comments = json2.toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    //update database with new comments + timestamp
                    repo.update(selectedUser);
                    adminComment.setText("");
                    confirmationDialog();
                }
    }
        else
        {
            newComment=false;
            String comm=selectedUser.comments;
            if(selectedUser.comments==null){
                noPreviousCommentsDialog();
                return;
            }

            JSONObject json= null;
            try {
                json = new JSONObject(selectedUser.comments.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            ArrayList comments=getArrayList(json.optJSONArray("comments"));

            adminComment.setText(comments.get(comments.size()-1).toString());

        }

    }
    public void noPreviousCommentsDialog(){
        //confirmation dialog
        Toast.makeText(AdminGraph.this,
                "Comment submitted!",
                Toast.LENGTH_LONG).show();

        adminComment.setText("");

    }

    public static ArrayList getArrayList(JSONArray jsonArray){
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
    public void confirmationDialog(){
        //confirmation dialog
        Toast.makeText(AdminGraph.this,
                "Comment submitted!",
                Toast.LENGTH_LONG).show();

        adminComment.setText("");
    }
}
