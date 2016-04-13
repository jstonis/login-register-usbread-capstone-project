package com.example.josceyn.walkerapp;

/**
 * Created by Josceyn on 3/17/2016.
 */
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Toast;
import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.exception.DropboxException;

import org.json.JSONException;
import org.json.JSONObject;

public class UploadFileToDropbox extends AsyncTask<Void, Void, Boolean> implements View.OnClickListener {

    private DropboxAPI<?> dropbox;
    private String path;
    private Context context;
    private Student patient;
    private StudentRepo userHelper;
    private String timeStamp;


    public UploadFileToDropbox(Context context, DropboxAPI<?> dropbox,
                               String path, Student patient) {
        System.out.println("IN DROPBOX CLASS");
        this.context = context.getApplicationContext();
        this.dropbox = dropbox;
        this.path = path;
        this.patient=patient;

        userHelper=new StudentRepo();
        timeStamp=userHelper.getCurrentTimeStamp();
    }

    @Override
    protected Boolean doInBackground(Void... params) {
        final File tempDir = context.getCacheDir();
        File tempFile1; //comments file
        File tempFile2; //data file
        FileWriter fr1;
        FileWriter fr2;
        try {
            //comments file
            tempFile1 = File.createTempFile("file", ".csv", tempDir);
            fr1 = new FileWriter(tempFile1);
            if(patient.comments!=null) {
                writeFile(fr1, patient.comments, "comments");
            }
            fr1.close();

            FileInputStream fileInputStream = new FileInputStream(tempFile1);
            dropbox.putFile(path + timeStamp +"- Comments", fileInputStream,
                    tempFile1.length(), null, null);
            tempFile1.delete();


            //data file
            tempFile2 = File.createTempFile("file", ".csv", tempDir);
            fr2 = new FileWriter(tempFile2);
            if(patient.usbdata!=null) {
                writeFile(fr2, patient.usbdata, "usbdata");
            }
            fr2.close();

            FileInputStream fileInputStream2 = new FileInputStream(tempFile2);
            dropbox.putFile(path + timeStamp +"- Data", fileInputStream2,
                    tempFile2.length(), null, null);
            tempFile2.delete();




            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DropboxException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            Toast.makeText(context, "Files Uploaded Succesfully!",
                    Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, "Failed to upload file", Toast.LENGTH_LONG)
                    .show();
        }
    }

    @Override
    public void onClick(View v) {

    }

    //Take string of patient data/comments, and write file
    public void writeFile(FileWriter fr, String patientInfo, String field){
        ArrayList items=new ArrayList();
        String [] temp;
        JSONObject json2= null;
        try {
            json2 = new JSONObject(patientInfo);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        items=userHelper.getArrayList(json2.optJSONArray(field));


        for(int i=0; i<items.size(); i++){
            temp= items.get(i).toString().split(",");
            try {
                if(temp.length==2) {
                    fr.write(temp[0].toString() + "," + temp[1].toString() + "\n");
                }
                else if(temp.length==3){
                    fr.write(temp[0].toString() + "," + temp[1].toString() + ","+ temp[2].toString()+ "\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


        }

    }
}