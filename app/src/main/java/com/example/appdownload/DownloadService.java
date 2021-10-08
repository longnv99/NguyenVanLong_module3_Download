 package com.example.appdownload;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static com.example.appdownload.NotificationDownloadChanel.CHANEL_ID;

public
class DownloadService extends Service {
    @Nullable
    @Override
    public
    IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public
    int onStartCommand(Intent intent, int flags, int startId) {
        //receiver --> send data to notification
        String dataURL = intent.getStringExtra("key_url");
        new Thread(new Runnable() {
            @Override
            public
            void run() {
                Intent intentlocal = new Intent();
                intentlocal.setAction("download");
                String filename = download2(dataURL);
                intentlocal.putExtra("completed", filename);
                Log.d("fn", filename);
                stopSelf();
                sendBroadcast(intentlocal);
            }
        }).start();
        return START_NOT_STICKY;
    }

    private
    String download2(String link) {
        String fileName = null;
        int progress = 0;

        try {
            URL url = new URL(link);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            int code = connection.getResponseCode();
            if(code == HttpURLConnection.HTTP_OK){
                int length = connection.getContentLength();

                String disposition = connection.getHeaderField("Content-Disposition");
                if(disposition != null){
                    int index = disposition.indexOf("filename=");
                    if(index > 0){
                        fileName = disposition.substring(index + 10);
                    }
                }
                else {
                    int index = link.lastIndexOf("/");
                    if(index > 0){
                        fileName = link.substring(index + 1);
                    }
                }
                if (fileName == null){
                    fileName = "NoName";
                }

                InputStream is = connection.getInputStream();
                File output = new File(getExternalFilesDir(null), fileName);
                FileOutputStream fos = new FileOutputStream(output);
                 byte[] buffer = new byte[1024];
                 int size;
                 int downloadCount = 0;
                 while ((size = is.read(buffer, 0, 1024)) > 0){
                    fos.write(buffer, 0, size);
                    downloadCount += size;

                    progress = (int)(downloadCount * 100.0 / length);
                    Log.d("prr", progress+"%");
                    notificationDownloading(progress, fileName);
                 }
                 Log.d("scf", "Successfull");
                 fos.close();
                 is.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Log.d("era", e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("era", e.getMessage());
        }
        Log.d("progress", progress +"/" +fileName);
        return fileName;
    }

    private void notificationDownloading(int progress, String filename){
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, CHANEL_ID)
                .setContentTitle(filename)
                .setSmallIcon(R.drawable.ic_baseline_download_24)
                .setContentText("downloading...")
                .setContentIntent(pendingIntent) // open activity when click notification
                .setOnlyAlertOnce(true)
                .setOngoing(true)
                .setProgress(100, progress, false);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(1, notification.build());

        startForeground(1, notification.build());
    }

}