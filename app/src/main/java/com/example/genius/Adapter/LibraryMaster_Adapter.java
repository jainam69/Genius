package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.LibraryModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Library_Fragment.library_fragment;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryMaster_Adapter extends RecyclerView.Adapter<LibraryMaster_Adapter.ViewHolder> {

    Context context;
    List<LibraryModel> libraryDetails;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String standard;
    int id, bid;
    long downloadID;
    String Name;
    byte[] imageVal;

    public LibraryMaster_Adapter(Context context, List<LibraryModel> libraryDetails) {
        this.context = context;
        this.libraryDetails = libraryDetails;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_master_deatil_list, parent, false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.doc_desc.setText(libraryDetails.get(position).getDescription());
        List<LibraryModel.LibraryStandardModel> libraryStandardModels = libraryDetails.get(position).getList();
        standard = "";
        for (int i = 0; i < libraryStandardModels.size(); i++) {
            standard += libraryStandardModels.get(i).getStandard();
            standard += "\n";
        }
        holder.standard.setText(standard);
        Glide.with(context).load(libraryDetails.get(position).getThumbnailFilePath()).into(holder.image);
        holder.library_edit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Edit Library?");
            image.setImageResource(R.drawable.ic_edit);
            AlertDialog dialog = builder.create();
            btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());
            btn_edit_yes.setOnClickListener(v12 -> {
                dialog.dismiss();
            });
            dialog.show();
        });
        holder.library_delete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            Button btn_delete = dialogView.findViewById(R.id.btn_delete);
            TextView title = dialogView.findViewById(R.id.title);
            ImageView image = dialogView.findViewById(R.id.image);
            image.setImageResource(R.drawable.delete);
            title.setText("Are you sure that you want to delete this Library?");
            AlertDialog dialog = builder.create();
            btn_cancel.setOnClickListener(v13 -> dialog.dismiss());
            btn_delete.setOnClickListener(v14 -> {
                dialog.dismiss();
                progressBarHelper.showProgressDialog();
                Call<CommonModel> call = apiCalling.RemoveLibrary(libraryDetails.get(position).getLibraryID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                call.enqueue(new Callback<CommonModel>() {
                    @Override
                    public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                        if (response.isSuccessful()) {
                            CommonModel model = response.body();
                            if (model.isCompleted()) {
                                if (model.isData()) {
                                    libraryDetails.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
                                }
                            }
                        }
                        progressBarHelper.hideProgressDialog();
                    }

                    @Override
                    public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                        progressBarHelper.hideProgressDialog();
                    }
                });
            });
            dialog.show();
        });
        holder.library_download.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Download Document?");
            image.setImageResource(R.drawable.download);
            AlertDialog dialog = builder.create();
            btn_edit_no.setOnClickListener(v15 -> dialog.dismiss());
            btn_edit_yes.setOnClickListener(v16 -> dialog.dismiss());
            dialog.show();
        });
    }

    @Override
    public int getItemCount() {
        return libraryDetails.size();
    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context1, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
//                Toast.makeText(context, "Download " + Name + " Completed And Stored In AshirvadStudyCircle Folder...", Toast.LENGTH_LONG).show();
            }
        }
    };

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView doc_desc, standard;
        ImageView image, library_edit, library_delete, library_download;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_desc = itemView.findViewById(R.id.doc_desc);
            standard = itemView.findViewById(R.id.standard);
            image = itemView.findViewById(R.id.image);
            library_edit = itemView.findViewById(R.id.library_edit);
            library_delete = itemView.findViewById(R.id.library_delete);
            library_download = itemView.findViewById(R.id.library_download);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }
}
