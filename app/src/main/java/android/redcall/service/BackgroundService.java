package android.redcall.service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.IBinder;

import android.redcall.mvvm.viewmodel.RecordViewModel;
import android.util.Log;


import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;


import java.io.IOException;


public class BackgroundService extends Service {

    private MediaRecorder mediaRecord;
    private RecordViewModel recordViewModel;
    private Handler handler;
    private String nameNum;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        recordViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getApplication())
                .create(RecordViewModel.class);
        handler = new Handler();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        nameNum = intent.getStringExtra("name");
        mediaRecord = new MediaRecorder();
        mediaRecord.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        mediaRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecord.setOutputFile(recordViewModel.writeFile(nameNum));
        try {
            mediaRecord.prepare();
            mediaRecord.start();
        } catch (IOException e) {
            Log.d("error11", "Ошибка");
        }
        recordViewModel.runTime(handler);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mediaRecord.stop();
        mediaRecord.release();
        recordViewModel.addToSpisok(nameNum);
    }

}
