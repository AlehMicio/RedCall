package android.redcall.adapter;

import android.content.Context;
import android.redcall.R;
import android.redcall.mvvm.model.Record;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SpisokAdapter extends RecyclerView.Adapter<SpisokAdapter.SpisokViewHolder> {

    private OnItemClickListener onItemClickListener;
    private ArrayList<Record> recordArrayList = new ArrayList<>();
    private Context context;

    class SpisokViewHolder extends RecyclerView.ViewHolder {

        public TextView nameTextView, timeTextView;
        public ImageView imageView;

        public SpisokViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            timeTextView = itemView.findViewById(R.id.timeTextView);
            imageView = itemView.findViewById(R.id.imageView);

            //Клик по CardView
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = getAdapterPosition();
                    if (onItemClickListener != null && pos != RecyclerView.NO_POSITION){
                        onItemClickListener.onItemClick(recordArrayList.get(pos));
                    }
                }
            });
        }
    }

    @NonNull
    @Override
    public SpisokViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.spisok_item, parent, false);
        return new SpisokViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SpisokViewHolder holder, int position) {
        Record record = recordArrayList.get(position);
        holder.nameTextView.setText(record.getNameRec());
        holder.timeTextView.setText(record.getTimeRec());
        holder.imageView.setImageResource(record.getStarImagePath());

        //Клик по звёздочке
        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = holder.getAdapterPosition();
                if (onItemClickListener != null && pos != RecyclerView.NO_POSITION){
                    onItemClickListener.onStarClick(recordArrayList.get(pos));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordArrayList.size();
    }

    //Сеттер для обноления RV
    public void setRecordArrayList(ArrayList<Record> recordArrayList, Context context){
        this.recordArrayList = recordArrayList;
        this.context = context;
        notifyDataSetChanged();
    }

    //Для управления клик-событиями
    public interface OnItemClickListener{
        void onItemClick(Record record);
        void onStarClick(Record record);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
