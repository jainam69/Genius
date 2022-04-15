package com.example.genius.Adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.Activity.ViewDocumentActivity;
import com.example.genius.BuildConfig;
import com.example.genius.Model.CircularModel;
import com.example.genius.R;
import com.example.genius.databinding.RowCircularBinding;

import java.io.File;
import java.util.List;

public class CircularAdapter extends RecyclerView.Adapter<CircularAdapter.ViewHolder> {

    Context context;
    List<CircularModel.CircularData> data;
    long downloadID;
    String Name;

    public CircularAdapter(Context context, List<CircularModel.CircularData> data) {
        this.context = context;
        this.data = data;
    }

    @NonNull
    @Override
    public CircularAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(RowCircularBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull CircularAdapter.ViewHolder holder, int position) {
        holder.binding.title.setText(data.get(position).CircularTitle);
        holder.binding.paperView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to View Circular?");
            image.setImageResource(R.drawable.view);
            AlertDialog dialog = builder.create();
            btn_edit_no.setOnClickListener(v18 -> dialog.dismiss());
            btn_edit_yes.setOnClickListener(v17 -> {
                dialog.dismiss();
                if (!data.get(position).FileName.equals("") && data.get(position).FileName != null) {
                    Intent intent = new Intent(context, ViewDocumentActivity.class);
                    intent.putExtra("ProIndex", data.get(position).CircularTitle);
                    intent.putExtra("PaperPath", data.get(position).FilePath);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });
            dialog.show();
        });
        holder.binding.download.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Download Circular?");
            image.setImageResource(R.drawable.download);
            AlertDialog dialog = builder.create();
            btn_edit_no.setOnClickListener(v18 -> dialog.dismiss());
            btn_edit_yes.setOnClickListener(v17 -> {
                dialog.dismiss();
                String filetype = data.get(position).FilePath;
                Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(filetype);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                Name = data.get(position).FileName;
                request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/AshirvadStudyCircle/" + Name);
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                downloadID = dm.enqueue(request);
                context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            });
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        RowCircularBinding binding;

        public ViewHolder(@NonNull RowCircularBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
        }
    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context1, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Toast.makeText(context, "Download " + Name + " Completed And Stored In AshirvadStudyCircle Folder...", Toast.LENGTH_LONG).show();
            }
        }
    };
}
