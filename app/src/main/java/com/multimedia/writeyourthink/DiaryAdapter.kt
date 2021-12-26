package com.multimedia.writeyourthink;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> implements OnItemClickListener {

    ArrayList<Diary> items = new ArrayList<Diary>();
    OnItemClickListener listener;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View itemview = inflater.inflate(R.layout.recycle_layout, viewGroup, false);
        return new ViewHolder(itemview, this);
    }


    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int position) {
       Diary diary = items.get(position);
       viewHolder.setItem(diary);
       viewHolder.itemView.setLongClickable(true);


    }

    @Override
    public int getItemCount() { return items.size();}
    public void addItem(Diary diary) { items.add(diary); }
    public void removeItem(){
        items.clear();
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }



    @Override
    public void onItemClick(ViewHolder holder, View view, int position) {
        if (listener != null){
            listener.onItemClick(holder, view, position);
        }
    }



    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImageView;
         TextView textView;
        TextView textView2;
        TextView date;
        TextView location;
        public ViewHolder(@NonNull final View itemView, final OnItemClickListener listener) {
            super(itemView);

            this.iconImageView = itemView.findViewById(R.id.iconImageView);
            this.textView = itemView.findViewById(R.id.textView);
            this.textView2 = itemView.findViewById(R.id.textView2);
            this.date = itemView.findViewById(R.id.tv_date);
            this.location = itemView.findViewById(R.id.tv_location);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();

                    if (listener != null){
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });

        }

        public void setItem(Diary diary){
            Glide.with(itemView).load(diary.getProfile()).into(iconImageView);
            textView.setText(diary.getWhere());
            textView2.setText(diary.getContents());
            date.setText(diary.getDate());
            location.setText(diary.getLocation());
        }
    }


}
