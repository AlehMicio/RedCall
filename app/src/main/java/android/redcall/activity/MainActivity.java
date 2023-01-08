package android.redcall.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.redcall.R;
import android.redcall.mvvm.model.Record;
import android.redcall.mvvm.viewmodel.RecordViewModel;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private MediaRecorder mediaRecord;
    private RecordViewModel recordViewModel;

    private ImageView startRecord, stopRecord, iconSpisok;
    private TextView timeTextView;
    private Handler handler;

    private boolean isRecordMic = false;
    private int second = 0; //Секунды записи (для секундомера)
    private String currentDate, currentTime;

    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialization();

        //Переход к Списку
        iconSpisok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            startActivity(new Intent(MainActivity.this, Spisok.class));
            }
        });
    }

    private void Initialization(){
        startRecord = findViewById(R.id.startImageView);
        stopRecord = findViewById(R.id.stopImageView);
        timeTextView = findViewById(R.id.timeTextView);
        iconSpisok = findViewById(R.id.iconSpisokImageView);
        handler = new Handler();

        recordViewModel = new ViewModelProvider
                .AndroidViewModelFactory(getApplication())
                .create(RecordViewModel.class);
    }

    //Кнопка запуска записи
    public void recordButton(View view) throws IOException {
        //Проверка на включение разрешений
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
            || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            String[] permission = {Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE};
            ActivityCompat.requestPermissions(this, permission, 0);
        }
        else {
            //Если разрешения даны, то запускается процесс записи:
            recordingMic();
            runTime();
        }
    }

    //Процесс записи
    private void recordingMic() {
        try {
            if (isRecordMic) {
                mediaRecord.stop();
                mediaRecord.release();
                startRecord.animate().alpha(1);
                stopRecord.animate().alpha(0);
                isRecordMic = false;
                Toast.makeText(MainActivity.this, "Запись остановлена", Toast.LENGTH_SHORT).show();
                recordViewModel.addToSpisok("Rec");
                currentDate = null;
                currentTime = null;
            } else {
                mediaRecord = new MediaRecorder();
                mediaRecord.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecord.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecord.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                mediaRecord.setOutputFile(recordViewModel.writeFile("Rec"));
                mediaRecord.prepare();
                mediaRecord.start();
                startRecord.animate().alpha(0);
                stopRecord.animate().alpha(1);
                isRecordMic = true;
                Toast.makeText(MainActivity.this, "Идёт запись", Toast.LENGTH_SHORT).show();
            }
        }
        catch (IOException e){
           Toast.makeText(this, "Ошибка", Toast.LENGTH_SHORT).show();
        }
    }

    //Обновление времени записи
    private void runTime(){
        handler.post(new Runnable() {
            @Override
            public void run() {
                int min = second/60;
                int sec = second - min*60;

                if (isRecordMic){
                    second++;
                }
                else second = 0;

                timeTextView.setText(String.format(Locale.getDefault(), "%02d:%02d", min, sec));
                handler.removeCallbacksAndMessages(null);
                handler.postDelayed(this, 1000);
            }
        });
    }
}