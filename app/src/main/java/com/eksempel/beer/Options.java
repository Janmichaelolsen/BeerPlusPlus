package com.eksempel.beer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class Options extends Activity {

    ArrayList<Date> allbeers = new ArrayList<Date>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.eksempel.beer.R.layout.activity_options);
        allbeers = (ArrayList<Date>) getIntent().getExtras().getSerializable("beers");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(com.eksempel.beer.R.menu.menu_options, menu);
        return true;
    }

    public void deleteAllBeers(View v){
        try {
            String s = "";
            FileOutputStream fos = openFileOutput("text.txt", Context.MODE_PRIVATE);
            fos.write(s.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        Toast.makeText(getApplicationContext(), "Deleted all beers", Toast.LENGTH_SHORT).show();
    }

    public void deleteTodaysBeers(View v){
        Date now = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
        for(int i=allbeers.size()-1; i>=0; i--){
            if(allbeers.get(i).getDate() == now.getDate() && allbeers.get(i).getMonth() == now.getMonth() && allbeers.get(i).getYear() == now.getYear()){
                allbeers.remove(i);
            }
        }
        try {
            String s = "";
            FileOutputStream fos = openFileOutput("text.txt", Context.MODE_PRIVATE);
            fos.write(s.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }
        try {
            FileOutputStream fos = openFileOutput("text.txt", Context.MODE_APPEND);
            for(Date d : allbeers){
                String string = formatter.format(d)+",";
                fos.write(string.getBytes());
            }
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            Intent returnIntent = new Intent();
            setResult(RESULT_OK,returnIntent);
            Toast.makeText(getApplicationContext(), "Deleted todays beers", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == com.eksempel.beer.R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
