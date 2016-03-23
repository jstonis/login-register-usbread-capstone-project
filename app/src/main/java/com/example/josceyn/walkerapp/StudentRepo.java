package com.example.josceyn.walkerapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class StudentRepo {
    private DBHelper dbHelper;

    public StudentRepo(Context context) {
        dbHelper = new DBHelper(context);
    }

    public int insert(Student student) {

        //Open connection to write data
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Student.KEY_username, student.username);
        values.put(Student.KEY_password,student.password);
        values.put(Student.KEY_name, student.name);
        values.put(Student.KEY_PT,student.pt);
        values.put(Student.KEY_admin, student.admin);
        values.put(Student.KEY_comments, student.comments);
        values.put(Student.KEY_usbdata, student.usbdata);

        // Inserting Row
        long student_Id = db.insert(Student.TABLE, null, values);
        db.close(); // Closing database connection
        return (int) student_Id;
    }

    public void delete(int student_Id) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        // It's a good practice to use parameter ?, instead of concatenate string
        db.delete(Student.TABLE, Student.KEY_ID + "= ?", new String[]{String.valueOf(student_Id)});
        db.close(); // Closing database connection
    }

    public void update(Student student) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();

        /*values.put(Student.KEY_age, student.age);
        values.put(Student.KEY_email,student.email);*/
        values.put(Student.KEY_name, student.name);
        values.put(Student.KEY_admin, student.admin);
        values.put(Student.KEY_username, student.username);
        values.put(Student.KEY_password, student.password);
        values.put(Student.KEY_PT, student.pt);
        values.put(Student.KEY_usbdata, student.usbdata);
        values.put(Student.KEY_comments, student.comments);

        // It's a good practice to use parameter ?, instead of concatenate string

        db.update(Student.TABLE, values, Student.KEY_username + "= ?", new String[]{String.valueOf(student.username)});
        db.close(); // Closing database connection
    }

    public ArrayList<HashMap<String, String>> getStudentList(String ptName) {
        //Open connection to read only
        SQLiteDatabase db;
        db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Student.KEY_name + "," +
                Student.KEY_username +
                " FROM " + Student.TABLE
                + " WHERE " +
                Student.KEY_PT + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        //Student student = new Student();
        ArrayList<HashMap<String, String>> studentList = new ArrayList<HashMap<String, String>>();

        Cursor cursor = db.rawQuery(selectQuery, new String[]{ptName});
        // looping through all rows and adding to list

        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> student = new HashMap<String, String>();
                student.put("name", cursor.getString(cursor.getColumnIndex(Student.KEY_name)));
                student.put("username", cursor.getString(cursor.getColumnIndex(Student.KEY_username)));
                studentList.add(student);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return studentList;

    }

    public Student getStudentById(int Id){
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Student.KEY_ID + "," +
                Student.KEY_name + "," +
                Student.KEY_username + "," +
                Student.KEY_password + "," +
                Student.KEY_PT + "," +
                Student.KEY_admin + "," +
                " FROM " + Student.TABLE
                + " WHERE " +
                Student.KEY_ID + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;
        Student student = new Student();

        Cursor cursor = db.rawQuery(selectQuery, new String[] { String.valueOf(Id) } );

        if (cursor.moveToFirst()) {
            do {
                student.student_ID =cursor.getInt(cursor.getColumnIndex(Student.KEY_ID));
                student.name =cursor.getString(cursor.getColumnIndex(Student.KEY_name));
                student.username  =cursor.getString(cursor.getColumnIndex(Student.KEY_username));
                student.password =cursor.getString(cursor.getColumnIndex(Student.KEY_password));
                student.pt =cursor.getString(cursor.getColumnIndex(Student.KEY_PT));
                student.admin =cursor.getInt(cursor.getColumnIndex(Student.KEY_admin));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return student;
    }

    public Student getStudentByUsername(String username){
        System.out.println("in student repo: "+ username);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selectQuery =  "SELECT  " +
                Student.KEY_ID + "," +
                Student.KEY_name + "," +
                Student.KEY_username + "," +
                Student.KEY_password + "," +
                Student.KEY_PT + "," +
                Student.KEY_admin + "," +
                Student.KEY_comments + "," +
                Student.KEY_usbdata +
                " FROM " + Student.TABLE
                + " WHERE " +
                Student.KEY_username + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        int iCount =0;
        Student student = new Student();

        Cursor cursor = db.rawQuery(selectQuery, new String[] { username } );

        if (cursor.moveToFirst()) {
            do {
                student.student_ID =cursor.getInt(cursor.getColumnIndex(Student.KEY_ID));
                student.name =cursor.getString(cursor.getColumnIndex(Student.KEY_name));
                student.username  =cursor.getString(cursor.getColumnIndex(Student.KEY_username));
                student.password =cursor.getString(cursor.getColumnIndex(Student.KEY_password));
                student.pt =cursor.getString(cursor.getColumnIndex(Student.KEY_PT));
                student.admin =cursor.getInt(cursor.getColumnIndex(Student.KEY_admin));
                student.usbdata=cursor.getString(cursor.getColumnIndex(Student.KEY_usbdata));
                student.comments=cursor.getString(cursor.getColumnIndex(Student.KEY_comments));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return student;
    }



    public boolean alreadyExists(Student student) throws SQLiteException{
        int count=-1;
        Cursor c=null;
        boolean alreadyExists=true;
        SQLiteDatabase db = dbHelper.getReadableDatabase();


        try {

            String selectQuery = "SELECT  " +
                    Student.KEY_username +
                    " FROM " + Student.TABLE
                    + " WHERE " +
                    Student.KEY_username + "=?";// It's a good practice to use parameter ?, instead of concatenate string


            c = db.rawQuery(selectQuery, new String[]{student.username});

            if(c.moveToFirst()){
                alreadyExists=true;
            }
            else{
                alreadyExists=false;
            }

        }
        catch(SQLiteException sqlException){
            sqlException.printStackTrace();
        }
        finally{

            if(c!=null){
                c.close();
            }
            return alreadyExists;
        }
    }
    public Student getPasswordOfUser(Student student) throws SQLiteException {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String value = "";


        String selectQuery = "SELECT  " +
                Student.KEY_username + "," +
                Student.KEY_name + "," +
                Student.KEY_PT + "," +
                Student.KEY_admin + "," +
                Student.KEY_comments + "," +
                Student.KEY_usbdata + "," +
                Student.KEY_password +
                " FROM " + Student.TABLE
                + " WHERE " +
                Student.KEY_username + "=?";// It's a good practice to use parameter ?, instead of concatenate string

        Cursor cursor;
        try {
            cursor = db.rawQuery(selectQuery, new String[]{String.valueOf(student.username)});
            if (cursor.moveToFirst()) {
                student.username = cursor.getString(cursor.getColumnIndex(Student.KEY_username));
                student.password=cursor.getString(cursor.getColumnIndex(Student.KEY_password));
                student.name=cursor.getString(cursor.getColumnIndex(Student.KEY_name));
                student.admin=cursor.getInt(cursor.getColumnIndex(Student.KEY_admin));
                student.pt=cursor.getString(cursor.getColumnIndex(Student.KEY_admin));
                cursor.close();

            } else {
                cursor.close();

            }

        } catch (SQLiteException exception) {
            exception.printStackTrace();
        }
        return student;
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




}