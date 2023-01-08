package android.redcall.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.redcall.R;
import android.redcall.adapter.SpisokAdapter;
import android.redcall.mvvm.model.Record;
import android.redcall.mvvm.viewmodel.RecordViewModel;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;


import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import com.google.android.exoplayer2.util.Util;

import java.util.ArrayList;
import java.util.List;

public class Spisok extends AppCompatActivity {

    private RecordViewModel recordViewModel;
    private ArrayList<Record> recordArrayList;

    private SimpleExoPlayer simpleExoPlayer;
    private PlayerView playerView;

    private ImageView iconMic;
    private RecyclerView recyclerView;
    private SpisokAdapter adapter;
    private RelativeLayout relativeLayout;

    private boolean isPlay, isStar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spisok);
        setTitle("Записи");

        recordViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(RecordViewModel.class);
        //Получение списка записей в RV
        recordViewModel.getAllRecords().observe(Spisok.this, new Observer<List<Record>>() {
            @Override
            public void onChanged(List<Record> records) {
                recordArrayList = reverse((ArrayList<Record>) records);
                loadRecyclerView();
            }
        });

        Initialization();
        clickMethod();
        Permissions();
    }

    private void Permissions(){
        //Проверка на включение разрешений
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.CAPTURE_AUDIO_OUTPUT) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.FOREGROUND_SERVICE) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_NUMBERS) != PackageManager.PERMISSION_GRANTED){
            String[] permission = {Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.CAPTURE_AUDIO_OUTPUT,
                    Manifest.permission.READ_PHONE_STATE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.FOREGROUND_SERVICE,
                    Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_PHONE_NUMBERS};
            ActivityCompat.requestPermissions(this, permission, 0);
        }
    }

    private void Initialization() {
        iconMic = findViewById(R.id.iconMicImageView);
        relativeLayout = findViewById(R.id.relativeLayout);

        playerView = findViewById(R.id.playerView);
        playerView.setControllerShowTimeoutMs(0);
        playerView.setCameraDistance(30);
        simpleExoPlayer = new SimpleExoPlayer.Builder(this).build();
        playerView.setPlayer(simpleExoPlayer);
    }

    private void clickMethod(){
        //Переход к списку записей
        iconMic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Spisok.this, MainActivity.class));
            }
        });

        //Обработка касая по экрану для выключения плеера
        relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isPlay){
                    simpleExoPlayer.stop();
                    playerView.setVisibility(View.GONE);
                    isPlay = false;
                }
            }
        });
    }

    private void loadRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new SpisokAdapter();
        adapter.setRecordArrayList(recordArrayList, this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(Spisok.this));

        //Удаление record
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override //Удаление
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                new AlertDialog.Builder(Spisok.this).setTitle("Удалить запись?")
                        .setNegativeButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Record record = recordArrayList.get(viewHolder.getAdapterPosition());
                                recordViewModel.readFile(record.getNameRec()).delete();
                                recordViewModel.deleteRecord(record);
                                loadRecyclerView();
                            }
                        })
                        .setPositiveButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                loadRecyclerView();
                            }
                        })
                        .show();
            }
        }).attachToRecyclerView(recyclerView);

        //Клик по записи из списка
        adapter.setOnItemClickListener(new SpisokAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Record record) {
                playAudio(record.getNameRec());
            }

            @Override
            public void onStarClick(Record record) {
                if(isStar){
                    record.setStarImagePath(R.drawable.ic_star_empty);
                    isStar = false;
                    loadRecyclerView();
                }
                else{
                   record.setStarImagePath(R.drawable.ic_star_full);
                   isStar = true;
                   loadRecyclerView();
                }
            }
        });
    }

    //Воспроизведение записи
    private void playAudio(String nameFile) {
        if (isPlay) {
            simpleExoPlayer.stop();
            playerView.setVisibility(View.GONE);
            isPlay = false;
        } else {
            DataSource.Factory data = new DefaultDataSourceFactory(Spisok.this,
                    Util.getUserAgent(Spisok.this, "app"));
            MediaSource audioFile = new ProgressiveMediaSource.Factory(data)
                    .createMediaSource(MediaItem.fromUri(recordViewModel.readFile(nameFile).toString()));
            simpleExoPlayer.prepare(audioFile);
            simpleExoPlayer.setPlayWhenReady(true);
            simpleExoPlayer.increaseDeviceVolume(); //Увеличивает громкость
            playerView.setVisibility(View.VISIBLE);
            isPlay = true;
        }
    }

    private ArrayList<Record> reverse(ArrayList<Record>  arrayList){
        ArrayList<Record>  arrayListRecord = new ArrayList<>();
        for(int i = arrayList.size() - 1; i > -1; i--){
            arrayListRecord.add(arrayList.get(i));
        }
        return arrayListRecord;
    }

    @Override //Создаём меню три точки
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override //Метод вызывается, когда нажата кнопка меню
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                startActivity(new Intent(Spisok.this, SettingsActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}