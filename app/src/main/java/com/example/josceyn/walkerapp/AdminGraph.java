package com.example.josceyn.walkerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;
import com.dropbox.client2.session.TokenPair;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

public class AdminGraph extends Activity implements View.OnClickListener {

    private DropboxAPI<AndroidAuthSession> dropbox;
    private final static String FILE_DIR = "/DropboxSample/";
    private final static String DROPBOX_NAME = "dropbox_prefs";
    private final static String ACCESS_KEY = "66lfl9cvrzq7gfv";
    private final static String ACCESS_SECRET = "e8f91jvdg0brjm3";
    private boolean isLoggedIn;
    EditText adminComment;
    Button bLogout, bSubmit, bEdit;
    ImageButton bDropbox;
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
        bDropbox=(ImageButton) findViewById(R.id.dropbox);

        bLogout.setOnClickListener(this);
        bSubmit.setOnClickListener(this);
        bEdit.setOnClickListener(this);
        bDropbox.setOnClickListener(this);


        AndroidAuthSession session;
        AppKeyPair pair = new AppKeyPair(ACCESS_KEY, ACCESS_SECRET);

        SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
        String key = prefs.getString(ACCESS_KEY, null);
        String secret = prefs.getString(ACCESS_SECRET, null);


        if (key != null && secret != null) {
            AccessTokenPair token = new AccessTokenPair(key, secret);
            session = new AndroidAuthSession(pair, AccessType.APP_FOLDER, token);
        } else {
            session = new AndroidAuthSession(pair, AccessType.APP_FOLDER);
        }
        dropbox = new DropboxAPI<AndroidAuthSession>(session);
    }

    @Override
    protected void onResume() {
        super.onResume();

        AndroidAuthSession session = dropbox.getSession();
        if (session.authenticationSuccessful()) {
            try {
                session.finishAuthentication();
                TokenPair tokens = session.getAccessTokenPair();
                SharedPreferences prefs = getSharedPreferences(DROPBOX_NAME, 0);
                Editor editor = prefs.edit();
                editor.putString(ACCESS_KEY, tokens.key);
                editor.putString(ACCESS_SECRET, tokens.secret);
                editor.commit();
            } catch (IllegalStateException e) {
                Toast.makeText(this, "Error during Dropbox authentication",
                        Toast.LENGTH_SHORT).show();
            }
        }
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
        else if(v==findViewById(R.id.bEdit))
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
        else if(v==findViewById(R.id.dropbox)){

            UploadFileToDropbox upload = new UploadFileToDropbox(this, dropbox,
                    FILE_DIR);
            upload.execute();
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
