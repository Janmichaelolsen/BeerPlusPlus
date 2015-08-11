package com.eksempel.beer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.helper.StaticLabelsFormatter;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends FragmentActivity {

    CollectionPagerAdapter mCollectionPagerAdapter;
    ViewPager mViewPager;
    public static SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
    public static ArrayList<Date> allbeers = new ArrayList<Date>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(com.eksempel.beer.R.layout.main);
        mCollectionPagerAdapter = new CollectionPagerAdapter(
                getSupportFragmentManager());

        // Set up the ViewPager, attaching the adapter.
        mViewPager = (ViewPager) findViewById(com.eksempel.beer.R.id.pager);
        mViewPager.setAdapter(mCollectionPagerAdapter);
        final ViewPager layout = (ViewPager)findViewById(com.eksempel.beer.R.id.pager);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
            layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            getFromFile();
            findFrontpageInfo();
            updateInfo();
            }
        });
    }

    public void goToSettings(View v){
        Intent i = new Intent(this, Options.class);
        i.putExtra("beers", (Serializable) allbeers);
        startActivityForResult(i, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        getFromFile();
        findFrontpageInfo();
        updateInfo();
    }//onActivityResult

    public void getFromFile(){
        Context context = getApplicationContext();
        FileInputStream fis = null;
        try {
            fis = context.openFileInput("text.txt");
            InputStreamReader isr = new InputStreamReader(fis);
            // READ STRING OF UNKNOWN LENGTH
            StringBuilder sb = new StringBuilder();
            char[] inputBuffer = new char[2048];
            int l;
            // FILL BUFFER WITH DATA
            while ((l = isr.read(inputBuffer)) != -1) {
                sb.append(inputBuffer, 0, l);
            }
            // CONVERT BYTES TO STRING
            String readString = sb.toString();
            fis.close();
            String[] parts = readString.split(",");
            ArrayList<Date> dates = new ArrayList<Date>();
            if (parts[0].trim().equals("")) {
                allbeers = dates;
            }else {
                for(int i=0; i<parts.length; i++){
                    Date date = formatter.parse(parts[i]);
                    dates.add(date);
                }
                allbeers = dates;
            }
        }
        catch (Exception e) {

        } finally {
            if (fis != null) {
                fis = null;
            }
        }
    }

    public void findFrontpageInfo(){
        Date now = new Date();
        int today = 0;
        int month = 0;
        for(Date beer : allbeers){
            formatter.format(beer);
            if(beer.getDay()==now.getDay() && beer.getMonth()==now.getMonth() && beer.getYear()==now.getYear()){
                today++;
            }
            if(beer.getMonth()==now.getMonth() && beer.getYear()==now.getYear()){
                month++;
            }
        }
        TextView tv = (TextView) findViewById(com.eksempel.beer.R.id.textView);
        tv.setText(today+"");
        TextView tv2 = (TextView) findViewById(com.eksempel.beer.R.id.textView5);
        tv2.setText(month+"");
    }

    public void addBeer(View v){
        Date now = new Date();
        String string = formatter.format(now)+",";
        try {
            FileOutputStream fos = openFileOutput("text.txt", Context.MODE_APPEND);
            fos.write(string.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            getFromFile();
            findFrontpageInfo();
            updateInfo();
        }
    }

    public void createGraph(double[] values){
        GraphView graph = (GraphView) findViewById(com.eksempel.beer.R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, values[0]),
                new DataPoint(1, values[1]),
                new DataPoint(2, values[2]),
                new DataPoint(3, values[3]),
                new DataPoint(4, values[4]),
                new DataPoint(5, values[5]),
                new DataPoint(6, values[6])
        });
        StaticLabelsFormatter staticLabelsFormatter = new StaticLabelsFormatter(graph);
        staticLabelsFormatter.setHorizontalLabels(new String[] {"Mo", "Tu", "We", "Th", "Fr", "Sa", "Su"});
        graph.getGridLabelRenderer().setLabelFormatter(staticLabelsFormatter);
        Viewport v = graph.getViewport();
        series.setColor(Color.parseColor("#1ADC6C"));
        series.setSpacing(3);
        v.setMinY(0);
        graph.removeAllSeries();
        graph.addSeries(series);
    }

    public void updateInfo(){
        findTotal();
        findThisYear();
        findRecordPerDay();
        getBeerPrWeek();
        //findMoneySpent();
        findWeekDays();
    }
    public void findTotal(){
        TextView tv = (TextView) findViewById(com.eksempel.beer.R.id.textView7);
        tv.setText(allbeers.size()+"");
    }
    public void findThisYear(){
        Date now = new Date();
        int res = 0;
        for(Date beer : allbeers){
            if(beer.getYear() == now.getYear()){
                res++;
            }
        }
        TextView tv = (TextView) findViewById(com.eksempel.beer.R.id.textView9);
        tv.setText(res+"");
    }
    public void findRecordPerDay(){
        if(allbeers.size()>0){
            int max = 0;
            String maxdate = "";
            int currentval = 1;
            Date currentdate = allbeers.get(0);
            if(allbeers.size()==1){
                if(currentval>max){
                    max = currentval;
                    maxdate = formatter.format(currentdate);
                    currentdate = allbeers.get(0);
                }
            }else {
                for(int i=1; i<allbeers.size(); i++){
                    if(allbeers.get(i).compareTo(allbeers.get(i-1))==0){
                        currentval++;
                    }else{
                        if(currentval>max){
                            max = currentval;
                            maxdate = formatter.format(currentdate);
                            currentval = 1;
                            currentdate = allbeers.get(i);
                        }
                    }
                    if(i==allbeers.size()-1 && currentval>max){
                        max = currentval;
                        maxdate = formatter.format(currentdate);
                    }
                }
            }
            TextView tv = (TextView) findViewById(com.eksempel.beer.R.id.textView11);
            tv.setText(max+"");
            TextView tv2 = (TextView) findViewById(com.eksempel.beer.R.id.textView10);
            tv2.setText("Record"+" ("+maxdate+")");
        }else{
            TextView tv = (TextView) findViewById(com.eksempel.beer.R.id.textView11);
            tv.setText("0");
            TextView tv2 = (TextView) findViewById(com.eksempel.beer.R.id.textView10);
            tv2.setText("Record");
        }
    }

    public void getBeerPrWeek(){
        if(allbeers.size()>0){
            int weeks = getWeeksBetween(allbeers.get(0), allbeers.get(allbeers.size()-1));
            if(weeks == 0) weeks = 1;
            TextView tv = (TextView) findViewById(com.eksempel.beer.R.id.textView13);
            tv.setText(allbeers.size()/weeks+"");
        }else{
            TextView tv = (TextView) findViewById(com.eksempel.beer.R.id.textView13);
            tv.setText("0");
        }
    }
    public static int getWeeksBetween (Date a, Date b) {

        if (b.before(a)) {
            return -getWeeksBetween(b, a);
        }
        a = resetTime(a);
        b = resetTime(b);

        Calendar cal = new GregorianCalendar();
        cal.setTime(a);
        int weeks = 0;
        while (cal.getTime().before(b)) {
            // add another week
            cal.add(Calendar.WEEK_OF_YEAR, 1);
            weeks++;
        }
        return weeks;
    }
    public static Date resetTime (Date d) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(d);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public void findWeekDays() {
        double[] weekdayvalues = new double[7];
        for (Date beer : allbeers) {
            switch (beer.getDay()) {
                case 0:
                    weekdayvalues[6]++;
                    break;
                case 1:
                    weekdayvalues[0]++;
                    break;
                case 2:
                    weekdayvalues[1]++;
                    break;
                case 3:
                    weekdayvalues[2]++;
                    break;
                case 4:
                    weekdayvalues[3]++;
                    break;
                case 5:
                    weekdayvalues[4]++;
                    break;
                case 6:
                    weekdayvalues[5]++;
                    break;
            }
        }
        createGraph(weekdayvalues);
    }

        /**
         * A {@link android.support.v4.app.FragmentStatePagerAdapter} that returns a
         * fragment representing an object in the collection.
         */
    public class CollectionPagerAdapter extends FragmentStatePagerAdapter {

        final int NUM_ITEMS = 2; // number of tabs

        public CollectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new TabFragment();
            Bundle args = new Bundle();
            args.putInt(TabFragment.ARG_OBJECT, i);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return NUM_ITEMS;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            String tabLabel = null;
            switch (position) {
                case 0:
                    tabLabel = getString(com.eksempel.beer.R.string.label1);
                    break;
                case 1:
                    tabLabel = getString(com.eksempel.beer.R.string.label2);
                    break;
                case 2:
                    tabLabel = getString(com.eksempel.beer.R.string.label3);
                    break;
            }
            return tabLabel;
        }
    }

    /**
     * A dummy fragment representing a section of the app, but that simply
     * displays dummy text.
     */
    public static class TabFragment extends Fragment {

        public static final String ARG_OBJECT = "object";

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            Bundle args = getArguments();
            int position = args.getInt(ARG_OBJECT);

            int tabLayout = 0;
            switch (position) {
                case 0:
                    tabLayout = com.eksempel.beer.R.layout.tab1;
                    break;
                case 1:
                    tabLayout = com.eksempel.beer.R.layout.tab2;
                    break;
                case 2:
                    tabLayout = com.eksempel.beer.R.layout.tab3;
                    break;
            }
            View rootView = inflater.inflate(tabLayout, container, false);
            return rootView;
        }
    }
}