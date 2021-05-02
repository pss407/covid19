package com.example.covid;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import fr.arnaudguyon.xmltojsonlib.XmlToJson;

public class WidgetProvider extends AppWidgetProvider {

    public void updateAppWidget(Context context, int appWidgetId) {
        sendRequest(context, appWidgetId);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for(int appWidgetId : appWidgetIds)
            updateAppWidget(context, appWidgetId);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName myWidget = new ComponentName(context.getPackageName(), WidgetProvider.class.getName());
        int[] widgetIds = appWidgetManager.getAppWidgetIds(myWidget);
        String action = intent.getAction(); //업데이트 액션이 들어오면
        if(action.equals(AppWidgetManager.ACTION_APPWIDGET_UPDATE))
        {
            this.onUpdate(context, AppWidgetManager.getInstance(context), widgetIds);
        }
    }

    public void sendRequest(Context context, int appWidgetId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance(); // 오늘날짜
        String today = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, -2);  // 오늘 날짜에서 하루를 뺌.
        String yesterday = sdf.format(calendar.getTime());
        String url = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?ServiceKey=2iybDyV%2FLv6DuHv4r0r8nM%2FqLiPheezoPVCGS9vHYtnUB%2BFU4jAWK6MRC05HQSo1ac%2FfKCBl6hV%2FZ6%2FU7ypIjA%3D%3D&pageNo=1&numOfRows=10&startCreateDt="+yesterday+"&endCreateDt="+today;

        if(AppHelper.requestQueue == null)
            AppHelper.requestQueue = Volley.newRequestQueue(context);

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(context, response, appWidgetId);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(context.getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                return params;
            }
        };

        request.setShouldCache(false);
        AppHelper.requestQueue.add(request);
    }

    public void processResponse(Context context, String response, int appWidgetId) {
        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
        Gson gson = new Gson();
        CovidResponse covidList = gson.fromJson(xmlToJson.toJson().toString(), CovidResponse.class);
        Item today = covidList.response.body.items.item.get(0);
        Item yesterday = covidList.response.body.items.item.get(1);

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.widget1_1, today.decideCnt);
        /*
        println(today.examCnt, examView);
        println(today.clearCnt, clearView);
        println(today.deathCnt, deathView);
        println("기준일 : "+today.stateDt, dateView);*/

        int dec_inter = Integer.parseInt(today.decideCnt)-Integer.parseInt(yesterday.decideCnt);
        int exam_inter = Integer.parseInt(today.examCnt)-Integer.parseInt(yesterday.examCnt);
        int clear_inter = Integer.parseInt(today.clearCnt)-Integer.parseInt(yesterday.clearCnt);
        int death_inter = Integer.parseInt(today.deathCnt)-Integer.parseInt(yesterday.deathCnt);

        int blue = ContextCompat.getColor(context.getApplicationContext(), R.color.blue);
        int red = ContextCompat.getColor(context.getApplicationContext(), R.color.red);

        if(dec_inter<0) {
            views.setTextColor(R.id.widget1_2, blue);
            views.setTextViewText(R.id.widget1_2, ""+(-dec_inter));
        }

        else {
            views.setTextColor(R.id.widget1_2, red);
            views.setTextViewText(R.id.widget1_2, Integer.toString(dec_inter));
        }

        /*if(exam_inter<0) {
            examInterval.setTextColor(blue);
            println(-exam_inter, examInterval);
            examInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down, 0);
        }

        else {
            examInterval.setTextColor(red);
            println(exam_inter, examInterval);
            examInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up, 0);
        }

        if(clear_inter<0) {
            clearInterval.setTextColor(blue);
            println(-clear_inter, clearInterval);
            clearInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down, 0);
        }

        else {
            clearInterval.setTextColor(red);
            println(clear_inter, clearInterval);
            clearInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up, 0);
        }

        if(death_inter==0) {
            deathInterval.setTextColor(red);
            println(death_inter, deathInterval);
        }

        else if(death_inter>0) {
            deathInterval.setTextColor(red);
            println(death_inter, deathInterval);
            deathInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up, 0);
        }*/

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
    }
}
