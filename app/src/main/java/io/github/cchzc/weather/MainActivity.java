package io.github.cchzc.weather;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import java.security.Security;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    static final int GET_DATA_FAILED = -1;
    static final int GET_DATA_STARTED = 1;
    static final int GET_DATA_COMPLETE = 2;
    private Handler mHandler;
    private ListView lv_forecast;
    private int mode = 3;
    private List<ForecastData> data3hr = new ArrayList<>();
    private List<ForecastData> data7d = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //enable TLS 1.2 for android 4.4
        if(Build.VERSION.SDK_INT <20)
            Security.insertProviderAt(new org.conscrypt.OpenSSLProvider(),1);

        //get townId
        SharedPreferences preference = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);
        String townId = preference.getString(getString(R.string.preferences_town), "6600200");

        Button btn_threeDay = findViewById(R.id.btn_threeDay);
        Button btn_week = findViewById(R.id.btn_week);
        btn_threeDay.setOnClickListener(view -> {
            mode = 3;
            btn_threeDay.setEnabled(false);
            btn_week.setEnabled(true);
            resetDataList();
        });

        btn_week.setOnClickListener(view -> {
            mode = 7;
            btn_threeDay.setEnabled(true);
            btn_week.setEnabled(false);
            resetDataList();
        });
        TextView emptyView = findViewById(R.id.empty_view);
        lv_forecast = findViewById(R.id.lv_forecast);
        lv_forecast.setEmptyView(emptyView);

        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case GET_DATA_FAILED:
                        Log.e("mHandler", "GET_DATA_FAILED");
                        break;
                    case GET_DATA_STARTED:
                        Log.v("mHandler", "GET_DATA_STARTED");
                        break;
                    case GET_DATA_COMPLETE:{
                        Log.v("mHandler", "GET_DATA_COMPLETE");
                        resetDataList();
                        break;
                    }
                    default:
                        super.handleMessage(msg);
                }
            }
        };
        new Thread(new DownloadTask(townId)).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = null;
        if(item.getItemId() == R.id.action_settings){
             intent = new Intent(this, SettingsActivity.class);
        } else if(item.getItemId() == R.id.action_help){
            intent = new Intent(this, HelpActivity.class);
        }
        startActivity(intent);
        return true;
    }

    private void resetDataList() {
        ForecastAdapter adapter;
        if(mode == 3) {
            adapter = new ForecastAdapter(MainActivity.this, data3hr);
        }
        else {
            adapter = new ForecastAdapter(MainActivity.this, data7d);
        }
        lv_forecast.setAdapter(adapter);
    }

    private class DownloadTask implements Runnable{

        private final String townId;

        DownloadTask(String townId){
            this.townId = townId;
        }

        @Override
        public void run() {
            try {
                mHandler.sendEmptyMessage(GET_DATA_STARTED);

                data3hr = WeatherBot.get3HrForecastData(townId);
                data7d = WeatherBot.get7DayForecastData(townId);

                mHandler.sendEmptyMessage(GET_DATA_COMPLETE);
            } catch (Exception e) {
                mHandler.sendEmptyMessage(GET_DATA_FAILED);
                e.printStackTrace();
            }
        }
    }
}