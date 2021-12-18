package com.example.genius.Adapter;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genius.Activity.VideoViewActivity;
import com.example.genius.Model.LibraryModel;
import com.example.genius.R;
import com.example.genius.helper.Preferences;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ViewLibrary_Adapter extends RecyclerView.Adapter<ViewLibrary_Adapter.ViewHolder> {

    Context context;
    List<LibraryModel> manageDetails;
    long downloadID;
    String Name;

    public ViewLibrary_Adapter(Context context, List<LibraryModel> manageDetails) {
        this.context = context;
        this.manageDetails = manageDetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.view_library_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (manageDetails.get(position).getLibrary_Type() == 1){
            holder.lib_download.setVisibility(View.GONE);
            holder.play.setVisibility(View.VISIBLE);
            holder.lib_image.setVisibility(View.GONE);
        }
        holder.description.setText(manageDetails.get(position).getDescription());
        Glide.with(context).load(manageDetails.get(position).getThumbnailFilePath()).into(holder.lib_image);
        holder.lib_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to View Document?");
                image.setImageResource(R.drawable.view);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_edit_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        if (manageDetails.get(position).getLibrary_Type() == 1){
                            try {
                                String url = manageDetails.get(position).getVideoLink();
                                if (!manageDetails.get(position).getVideoLink().startsWith("http://") && !manageDetails.get(position).getVideoLink().startsWith("https://")){
                                    url = "http://" + manageDetails.get(position).getVideoLink();
                                }
                                Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(webIntent);
                            } catch (ActivityNotFoundException ex) {
                                ex.printStackTrace();
                            }
                        }else {
                            Intent intent = new Intent(context, VideoViewActivity.class);
                            intent.putExtra("ProIndex", "Images");
                            intent.putExtra("Description", manageDetails.get(position).getDescription());
                            Preferences.getInstance(context).setString(Preferences.KEY_VIDEO_BASE, manageDetails.get(position).getThumbnailFilePath());
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }

                    }
                });
                dialog.show();
            }
        });
        holder.lib_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Download Library Document?");
                image.setImageResource(R.drawable.download);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_edit_yes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        String filetype = manageDetails.get(position).getDocFilePath();
                        Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
                        DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                        Uri uri = Uri.parse(filetype);
                        DownloadManager.Request request = new DownloadManager.Request(uri);
                        Name = manageDetails.get(position).getDocFileName();
                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/AshirvadStudyCircle/" + Name);
                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                        downloadID = dm.enqueue(request);
                        context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return manageDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView lib_image,lib_view,lib_download;
        TextView description;
        LinearLayout play;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            description = itemView.findViewById(R.id.description);
            lib_image = itemView.findViewById(R.id.lib_image);
            lib_view = itemView.findViewById(R.id.lib_view);
            lib_download = itemView.findViewById(R.id.lib_download);
            play = itemView.findViewById(R.id.play);
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
