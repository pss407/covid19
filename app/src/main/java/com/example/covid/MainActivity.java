package com.example.covid;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class MainActivity extends AppCompatActivity {
    TextView deathView;
    TextView decideView;
    TextView clearView;
    TextView examView;
    TextView decideInterval;
    TextView deathInterval;
    TextView clearInterval;
    TextView examInterval;

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

        if(AppHelper.requestQueue == null)
            AppHelper.requestQueue = Volley.newRequestQueue(getApplicationContext());
    }

    @Override
    protected void onResume() {
        super.onResume();
        sendRequest();
    }

    public void sendRequest() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        Calendar calendar = Calendar.getInstance(); // 오늘날짜
        String today = sdf.format(calendar.getTime());
        calendar.add(Calendar.DATE, -1);  // 오늘 날짜에서 하루를 뺌.
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
        Item today = covidList.response.body.items.item.get(0);
        Item yesterday = covidList.response.body.items.item.get(1);

        println(today.decideCnt, decideView);
        println(today.examCnt, examView);
        println(today.clearCnt, clearView);
        println(today.deathCnt, deathView);

        int dec_inter = Integer.parseInt(today.decideCnt)-Integer.parseInt(yesterday.decideCnt);
        int exam_inter = Integer.parseInt(today.examCnt)-Integer.parseInt(yesterday.examCnt);
        int clear_inter = Integer.parseInt(today.clearCnt)-Integer.parseInt(yesterday.clearCnt);
        int death_inter = Integer.parseInt(today.deathCnt)-Integer.parseInt(yesterday.deathCnt);

        println(dec_inter, decideInterval);
        println(exam_inter, examInterval);
        println(clear_inter, clearInterval);
        println(death_inter, deathInterval);
    }

    public void println(Object data, TextView textView) {
        textView.setText(data.toString());
    }
}