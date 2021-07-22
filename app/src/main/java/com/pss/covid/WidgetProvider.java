package com.pss.covid;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.widget.RemoteViews;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pss.covid.volley.AppHelper;
import com.pss.covid.volley.CovidResponse;
import com.pss.covid.volley.Item;
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

    public void processResponse(Context context, String res, int appWidgetId) {
        XmlToJson xmlToJson = new XmlToJson.Builder(res).build();
        Gson gson = new Gson();
        CovidResponse covidList = gson.fromJson(xmlToJson.toJson().toString(), CovidResponse.class);
        Item today = covidList.getResponse().getBody().getItems().getItem().get(0);
        Item yesterday = covidList.getResponse().getBody().getItems().getItem().get(1);
        DecimalFormat format = new DecimalFormat("###,###");     //천단위 콤마

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget);
        views.setTextViewText(R.id.widget1_1, format.format(today.getDecideCnt()));

        int dec_inter = today.getDecideCnt() - yesterday.getDecideCnt();
        int blue = ContextCompat.getColor(context.getApplicationContext(), R.color.blue);
        int red = ContextCompat.getColor(context.getApplicationContext(), R.color.red);

        if(dec_inter<0) {
            views.setTextColor(R.id.widget1_2, blue);
            views.setTextViewText(R.id.widget1_2, ""+format.format(-dec_inter));
            views.setTextViewCompoundDrawables(R.id.widget1_2, 0, 0, R.drawable.down, 0);
        }

        else {
            views.setTextColor(R.id.widget1_2, red);
            views.setTextViewText(R.id.widget1_2, format.format(dec_inter));
            views.setTextViewCompoundDrawables(R.id.widget1_2, 0, 0, R.drawable.up, 0);
        }

        AppWidgetManager.getInstance(context).updateAppWidget(appWidgetId, views);
    }
}