package com.example.appdownload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import static com.example.appdownload.NotificationDownloadChanel.CHANEL_ID;

public
class MainActivity extends AppCompatActivity {

    EditText txtURL;
    Button btn;

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public
        void onReceive(Context context, Intent intent) {
            String fn = intent.getStringExtra("completed");
            Log.d("fn", fn + " 2");
            notificationComplete(fn);
        }
    };

    @Override
    protected
    void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtURL = findViewById(R.id.url);
        btn = findViewById(R.id.btn);

        if(ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
        //txtURL.setText("https://dlcdn.apache.org/netbeans/netbeans/12.3/Apache-NetBeans-12.3-bin-windows-x64.exe");
        txtURL.setText("https://c1-ex-swe.nixcdn.com/Singer_Audio5/SuThatSauMotLoiHua-ChiDan-3316709.mp3?st=Y3tpeInoQqQywhx2vxRk7g&e=1625581497&download=true");
        //txtURL.setText("https://pixabay.com/get/g01cf7d8dc8862f6f22cf7a047b201d12d54a8a95b18fbf3a25b5237f15e06f39c3a6aa3daeb6943fce434af9726fce776f45a9fa89e6ce693603833ef6af7254d6775f5764f63526d6decfc546646d27_1280.jpg?attachment=");
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public
            void onClick(View v) {
                clickDownload();
                Log.d("Click", "scf");
            }
        });

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("download");
        registerReceiver(broadcastReceiver, intentFilter);
    }


    private
    void clickDownload() {
        Intent intentService = new Intent(this, DownloadService.class);
        intentService.putExtra("key_url", txtURL.getText().toString().trim());
        startService(intentService);
    }

    @Override
    protected
    void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(broadcastReceiver);
    }

    private
    void notificationComplete(String filename) {
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANEL_ID)
                .setContentTitle(filename)
                .setContentText("Downloading Completed")
                .setSmallIcon(R.drawable.ic_baseline_download_done_24);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification.build());
    }

}