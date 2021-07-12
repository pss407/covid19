package com.example.covid;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.icu.text.DecimalFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.Volley;
import com.example.covid.retrofit.DTO;
import com.example.covid.retrofit.RetrofitClient;
import com.example.covid.retrofit.ServiceAPI;
import com.example.covid.volley.AppHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity {
    TextView deathView;
    TextView decideView;
    TextView clearView;
    TextView examView;
    TextView dateView;
    TextView decideInterval;
    TextView deathInterval;
    TextView clearInterval;
    TextView examInterval;
    ServiceAPI service;
    String serviceKey = "2iybDyV/Lv6DuHv4r0r8nM/qLiPheezoPVCGS9vHYtnUB+FU4jAWK6MRC05HQSo1ac/fKCBl6hV/Z6/U7ypIjA==";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deathView = (TextView) findViewById(R.id.death);
        decideView = (TextView) findViewById(R.id.decide);
        clearView = (TextView) findViewById(R.id.clear);
        examView = (TextView) findViewById(R.id.exam);
        decideInterval = (TextView) findViewById(R.id.decide_interval);
        examInterval = (TextView) findViewById(R.id.exam_interval);
        clearInterval = (TextView) findViewById(R.id.clear_interval);
        deathInterval = (TextView) findViewById(R.id.death_interval);
        dateView = (TextView) findViewById(R.id.date);
        service = RetrofitClient.getClient().create(ServiceAPI.class);

        if(AppHelper.requestQueue == null)
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());

        Button refreshBtn=(Button)findViewById(R.id.renew_btn);
        refreshBtn.setBackgroundResource(R.drawable.ic_refresh);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("te", "refresh");
                sendRequest();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendRequest();

        Intent widgetIntent = new Intent(this, WidgetProvider.class);
        widgetIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        this.sendBroadcast(widgetIntent);
    }

    /*--------retrofit 이용한 통신 부분----------*/
    public void sendRequest() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance(); // 오늘날짜
        String today = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, -2);  // 오늘 날짜에서 하루를 뺌.
        String yesterday = sdf.format(calendar.getTime());

        HashMap<String, String> query = new HashMap<>();
        query.put("serviceKey", serviceKey);
        query.put("pageNo", "1");
        query.put("numOfRows", "10");
        query.put("startCreateDt", yesterday);
        query.put("endCreateDt", today);

        service.response(query).enqueue(new Callback<DTO>() {
            @Override
            public void onResponse(Call<DTO> call, retrofit2.Response<DTO> response) {
                DTO res = response.body();
                DTO.Item today = res.body.items.get(0);
                DTO.Item yesterday = res.body.items.get(1);
                DecimalFormat format = new DecimalFormat("###,###");     //천단위 콤마

                println(format.format(today.decideCnt), decideView);
                println(format.format(today.examCnt), examView);
                println(format.format(today.clearCnt), clearView);
                println(format.format(today.deathCnt), deathView);
                println("기준일 : " + today.stateDt, dateView);

                int dec_inter = today.decideCnt - yesterday.decideCnt;
                int exam_inter = today.examCnt - yesterday.examCnt;
                int clear_inter = today.clearCnt - yesterday.clearCnt;
                int death_inter = today.deathCnt - yesterday.deathCnt;

                int blue = ContextCompat.getColor(getApplicationContext(), R.color.blue);
                int red = ContextCompat.getColor(getApplicationContext(), R.color.red);

                if (dec_inter < 0) {
                    decideInterval.setTextColor(blue);
                    println(format.format(-dec_inter), decideInterval);
                    decideInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down, 0);
                } else {
                    decideInterval.setTextColor(red);
                    println(format.format(dec_inter), decideInterval);
                    decideInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up, 0);
                }

                if (exam_inter < 0) {
                    examInterval.setTextColor(blue);
                    println(format.format(-exam_inter), examInterval);
                    examInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down, 0);
                } else {
                    examInterval.setTextColor(red);
                    println(format.format(exam_inter), examInterval);
                    examInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up, 0);
                }

                if (clear_inter < 0) {
                    clearInterval.setTextColor(blue);
                    println(format.format(-clear_inter), clearInterval);
                    clearInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down, 0);
                } else {
                    clearInterval.setTextColor(red);
                    println(format.format(clear_inter), clearInterval);
                    clearInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up, 0);
                }

                if (death_inter == 0) {
                    deathInterval.setTextColor(red);
                } else if (death_inter > 0) {
                    deathInterval.setTextColor(red);
                    deathInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up, 0);
                }
                println(format.format(death_inter), deathInterval);
            }

            @Override
            public void onFailure(Call<DTO> call, Throwable t) {
                Toast.makeText(MainActivity.this, "에러 발생", Toast.LENGTH_SHORT).show();
                Log.e("에러 발생", t.getMessage());
                t.printStackTrace();
            }
        });
    }

    /*--------volley 이용한 통신 부분----------*/
    /*public void sendRequest() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance(); // 오늘날짜
        String today = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, -2);  // 오늘 날짜에서 하루를 뺌.
        String yesterday = sdf.format(calendar.getTime());
        String url = "http://openapi.data.go.kr/openapi/service/rest/Covid19/getCovid19InfStateJson?ServiceKey=2iybDyV%2FLv6DuHv4r0r8nM%2FqLiPheezoPVCGS9vHYtnUB%2BFU4jAWK6MRC05HQSo1ac%2FfKCBl6hV%2FZ6%2FU7ypIjA%3D%3D&pageNo=1&numOfRows=10&startCreateDt="+yesterday+"&endCreateDt="+today;

        StringRequest request = new StringRequest(
                Request.Method.GET,
                url,
                new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        processResponse(response);
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT);
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

    public void processResponse(String response) {
        XmlToJson xmlToJson = new XmlToJson.Builder(response).build();
        Gson gson = new Gson();
        CovidResponse covidList = gson.fromJson(xmlToJson.toJson().toString(), CovidResponse.class);
        Item today = covidList.getResponse().getBody().getItems().getItem().get(0);
        Item yesterday = covidList.getResponse().getBody().getItems().getItem().get(1);

        println(today.getDecideCnt(),decideView);
        println(today.getExamCnt(), examView);
        println(today.getClearCnt(), clearView);
        println(today.getDeathCnt(), deathView);
        println("기준일 : "+today.getStateDt(), dateView);

        int dec_inter = Integer.parseInt(today.getDecideCnt())-Integer.parseInt(yesterday.getDecideCnt());
        int exam_inter = Integer.parseInt(today.getExamCnt())-Integer.parseInt(yesterday.getExamCnt());
        int clear_inter = Integer.parseInt(today.getClearCnt())-Integer.parseInt(yesterday.getClearCnt());
        int death_inter = Integer.parseInt(today.getDeathCnt())-Integer.parseInt(yesterday.getDeathCnt());

        int blue = ContextCompat.getColor(getApplicationContext(), R.color.blue);
        int red = ContextCompat.getColor(getApplicationContext(), R.color.red);

        if(dec_inter<0) {
            decideInterval.setTextColor(blue);
            println(-dec_inter, decideInterval);
            decideInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.down, 0);
        }

        else {
            decideInterval.setTextColor(red);
            println(dec_inter, decideInterval);
            decideInterval.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.up, 0);
        }

        if(exam_inter<0) {
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
        }
    }*/

    public void println(Object data, TextView textView) {
        textView.setText(data.toString());
    }
}