package com.eksempel.beer;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


/**
 * Implementation of App Widget functionality.
 */
public class BeerWidget extends AppWidgetProvider {

    public static String ACTION_WIDGET_ADDBEER = "ActionReceiverAdd";
    static SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
    static ArrayList<Date> allbeers = new ArrayList<Date>();
    static AppWidgetManager manager;
    static int widgedId;

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; i++) {
            updateAppWidget(context, appWidgetManager, appWidgetIds[i]);
        }
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), com.eksempel.beer.R.layout.beer_widget);
        Intent active = new Intent(context, BeerWidget.class);
        active.setAction(ACTION_WIDGET_ADDBEER);
        PendingIntent actionPendingIntent = PendingIntent.getBroadcast(context, 0, active, 0);
        remoteViews.setOnClickPendingIntent(com.eksempel.beer.R.id.imageButton3, actionPendingIntent);
        appWidgetManager.updateAppWidget(appWidgetIds, remoteViews);
        manager = appWidgetManager;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION_WIDGET_ADDBEER)) {
            RemoteViews views = new RemoteViews(context.getPackageName(), com.eksempel.beer.R.layout.beer_widget);
            Date now = new Date();
            String string = formatter.format(now)+",";
            try {
                FileOutputStream fos = context.openFileOutput("text.txt", Context.MODE_APPEND);
                fos.write(string.getBytes());
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
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
            int today = 0;
            for(Date beer : allbeers) {
                formatter.format(beer);
                if (beer.getDay() == now.getDay() && beer.getMonth() == now.getMonth() && beer.getYear() == now.getYear()) {
                    today++;
                }
            }
            views.setTextViewText(com.eksempel.beer.R.id.appwidget_text, today+"");

            manager.updateAppWidget(widgedId, views);
        } else {
            super.onReceive(context, intent);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), com.eksempel.beer.R.layout.beer_widget);
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
        Date now = new Date();
        int today = 0;
        for(Date beer : allbeers) {
            formatter.format(beer);
            if (beer.getDay() == now.getDay() && beer.getMonth() == now.getMonth() && beer.getYear() == now.getYear()) {
                today++;
            }
        }
        views.setTextViewText(com.eksempel.beer.R.id.appwidget_text, today+"");
        // Instruct the widget manager to update the widget
        widgedId = appWidgetId;
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
}


