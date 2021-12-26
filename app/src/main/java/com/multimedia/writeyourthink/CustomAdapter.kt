package com.multimedia.writeyourthink;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.CustomViewHolder> {

    private ArrayList<Diary> arrayList;
    private Context context;


    public CustomAdapter(ArrayList<Diary> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_layout, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        Diary diary = arrayList.get(position);
        holder.setItem(diary);
    }

    @Override
    public int getItemCount() {
        // 삼항 연산자
        return (arrayList != null ? arrayList.size() : 0);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView textView;
        TextView textView2;
        TextView tv_date;
        TextView tv_location;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iconImageView = itemView.findViewById(R.id.iconImageView);
            this.textView = itemView.findViewById(R.id.textView);
            this.textView2  = itemView.findViewById(R.id.textView2);
            this.tv_date = itemView.findViewById(R.id.tv_date);
            this.tv_location = itemView.findViewById(R.id.tv_location);
        }

        public void setItem(Diary diary){
            Glide.with(itemView).load(diary.getProfile()).into(iconImageView);
            textView.setText(diary.getWhere());
            textView2.setText(diary.getContents());
            tv_date.setText(diary.getDate());
            tv_location.setText(diary.getLocation());
        }
    }
}
