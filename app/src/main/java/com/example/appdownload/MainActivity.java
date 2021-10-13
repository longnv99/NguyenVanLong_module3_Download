package com.example.appdownload;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.File;

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
        //txtURL.setText("https://c1-ex-swe.nixcdn.com/Singer_Audio5/SuThatSauMotLoiHua-ChiDan-3316709.mp3?st=Y3tpeInoQqQywhx2vxRk7g&e=1625581497&download=true");
        txtURL.setText("https://images.pexels.com/photos/65894/peacock-pen-alluring-yet-lure-65894.jpeg?cs=srgb&dl=pexels-pixabay-65894.jpg&fm=jpg");
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
//        Intent intent = new Intent();
//        intent.setAction(Intent.ACTION_VIEW);
//        File file = new File();
//        intent.setDataAndType(Uri.fromFile(file), null);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANEL_ID)
                .setContentTitle(filename)
                .setContentText("Downloading Completed")
                .setSmallIcon(R.drawable.ic_baseline_download_done_24);
//                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification.build());
        unregisterReceiver(broadcastReceiver);
    }

}