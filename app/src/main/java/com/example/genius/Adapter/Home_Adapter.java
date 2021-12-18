package com.example.genius.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.R;

import java.util.List;

public class Home_Adapter extends RecyclerView.Adapter<Home_Adapter.ViewHolder> {

    Context context;
    List<String> titlename;
    List<Integer> imagename;

    public Home_Adapter(Context context, List<String> titlename, List<Integer> imagename) {
        this.context = context;
        this.titlename = titlename;
        this.imagename = imagename;
    }

    @NonNull
    @Override
    public Home_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new Home_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.home_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull Home_Adapter.ViewHolder holder, int position) {
        holder.title.setText(titlename.get(position));
        holder.image.setImageResource(imagename.get(position));
    }

    @Override
    public int getItemCount() {
        return titlename.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView image;
        TextView title;
        CardView linear_masters;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            image = itemView.findViewById(R.id.image);
            title = itemView.findViewById(R.id.title);
            linear_masters = itemView.findViewById(R.id.linear_masters);
        }
    }
}
