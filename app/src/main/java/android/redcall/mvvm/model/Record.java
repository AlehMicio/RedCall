package android.redcall.mvvm.model;


import android.widget.ImageView;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(tableName = "record_table")
public class Record {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "record_id")
    private int id;

    @ColumnInfo(name = "record_name")
    private String nameRec;

    @ColumnInfo(name = "record_time")
    private String timeRec;

    @ColumnInfo(name = "record_star")
    private int starImagePath;

    @Ignore
    public Record() {
    }

    public Record(int id, String nameRec, String timeRec, int starImagePath) {
        this.id = id;
        this.nameRec = nameRec;
        this.timeRec = timeRec;
        this.starImagePath = starImagePath;
    }

    @Ignore
    public Record(String nameRec, String timeRec, int starImagePath) {
        this.nameRec = nameRec;
        this.timeRec = timeRec;
        this.starImagePath = starImagePath;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNameRec() {
        return nameRec;
    }

    public void setNameRec(String nameRec) {
        this.nameRec = nameRec;
    }

    public String getTimeRec() {
        return timeRec;
    }

    public void setTimeRec(String timeRec) {
        this.timeRec = timeRec;
    }

    public int getStarImagePath() {
        return starImagePath;
    }

    public void setStarImagePath(int starImagePath) {
        this.starImagePath = starImagePath;
    }
}
