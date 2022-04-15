package com.example.genius.Adapter;

import android.annotation.SuppressLint;
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
import com.example.genius.Activity.ViewDocumentActivity;
import com.example.genius.Model.LibraryModel;
import com.example.genius.R;
import com.example.genius.databinding.ViewLibraryMasterDeatilListBinding;
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
        return new ViewHolder(ViewLibraryMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        if (manageDetails.get(position).getLibrary_Type() == 1) {
            holder.binding.libDownload.setVisibility(View.GONE);
            if (isYoutubeUrl(manageDetails.get(position).getVideoLink())) {
                holder.binding.libImage.setVisibility(View.VISIBLE);
                holder.binding.ivPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.youtube_icon, context.getTheme()));
                Glide.with(context).load("http://img.youtube.com/vi/" + extractYoutubeId(manageDetails.get(position).getVideoLink()) + "/0.jpg").into(holder.binding.libImage);
            } else {
                holder.binding.ivPlay.setImageDrawable(context.getResources().getDrawable(R.drawable.play, context.getTheme()));
            }
        } else {
            holder.binding.libDownload.setVisibility(View.VISIBLE);
            holder.binding.libImage.setVisibility(View.VISIBLE);
            holder.binding.ivPlay.setVisibility(View.GONE);
            Glide.with(context).load(manageDetails.get(position).getThumbnailFilePath()).into(holder.binding.libImage);
        }
        holder.binding.description.setText(manageDetails.get(position).getDescription());
        holder.binding.libView.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
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
                    if (manageDetails.get(position).getLibrary_Type() == 1) {
                        try {
                            String url = manageDetails.get(position).getVideoLink();
                            if (!manageDetails.get(position).getVideoLink().startsWith("http://") && !manageDetails.get(position).getVideoLink().startsWith("https://")) {
                                url = "http://" + manageDetails.get(position).getVideoLink();
                            }
                            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            webIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(webIntent);
                        } catch (ActivityNotFoundException ex) {
                            ex.printStackTrace();
                        }
                    } else {
                        Intent intent = new Intent(context, ViewDocumentActivity.class);
                        intent.putExtra("ProIndex",manageDetails.get(position).getDescription());
                        intent.putExtra("PaperPath",manageDetails.get(position).getDocFilePath());
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                    }

                }
            });
            dialog.show();
        });
        holder.binding.libDownload.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
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
        });
    }

    @Override
    public int getItemCount() {
        return manageDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ViewLibraryMasterDeatilListBinding binding;

        public ViewHolder(@NonNull ViewLibraryMasterDeatilListBinding itemView) {
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

    public String extractYoutubeId(String ytUrl) {
        String vId = null;
        Pattern regex = Pattern.compile("http(?:s)?:\\/\\/(?:m.)?(?:www\\.)?youtu(?:\\.be\\/|be\\.com\\/(?:watch\\?(?:feature=youtu.be\\&)?v=|v\\/|embed\\/|user\\/(?:[\\w#]+\\/)+))([^&#?\\n]+)");
        Matcher regexMatcher = regex.matcher(ytUrl);
        if (regexMatcher.find()) {
            vId = regexMatcher.group(1);
        }
        return vId;
    }

    public static boolean isYoutubeUrl(String youTubeURl) {
        boolean success;
        String pattern = "^(http(s)?:\\/\\/)?((w){3}.)?youtu(be|.be)?(\\.com)?\\/.+";
        success = !youTubeURl.isEmpty() && youTubeURl.matches(pattern);
        return success;
    }
}
