package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
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
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.LibraryModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.LibraryMasterDeatilListBinding;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.BranchClass.BranchClassFragment;
import com.example.genius.ui.Library_Fragment.library_fragment;
import com.example.genius.ui.Library_Fragment.library_video_fragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryMaster_Adapter extends RecyclerView.Adapter<LibraryMaster_Adapter.ViewHolder> {

    Context context;
    List<LibraryModel> libraryDetails;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String standard,Course;
    long downloadID;
    String Name;
    UserModel.PageData userpermission;

    public LibraryMaster_Adapter(Context context, List<LibraryModel> libraryDetails) {
        this.context = context;
        this.libraryDetails = libraryDetails;
    }

    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LibraryMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.binding.docCategory.setText(libraryDetails.get(position).getCategoryInfo().getCategory());
        holder.binding.docDesc.setText(libraryDetails.get(position).getDescription());
        if (libraryDetails.get(position).getLibrary_Type() == 1) {
            for (UserModel.PageInfoEntity model : userpermission.Data){
                if (model.getPageID() == 30){
                    if (!model.Createstatus){
                        holder.binding.libraryEdit.setVisibility(View.GONE);
                    }
                    if (!model.Deletestatus){
                        holder.binding.libraryDelete.setVisibility(View.GONE);
                    }
                }
            }
            holder.binding.libraryVideoLink.setVisibility(View.VISIBLE);
            holder.binding.image.setVisibility(View.GONE);
            holder.binding.libraryDownload.setVisibility(View.GONE);
            holder.binding.libraryVideoLink.setText(libraryDetails.get(position).getVideoLink());
        } else {
            for (UserModel.PageInfoEntity model : userpermission.Data)
            {
                if (model.getPageID() == 78){
                    if (!model.Createstatus){
                        holder.binding.libraryEdit.setVisibility(View.GONE);
                    }
                    if (!model.Deletestatus){
                        holder.binding.libraryDelete.setVisibility(View.GONE);
                    }
                }
            }
            ViewGroup.LayoutParams params = holder.binding.linearVideo.getLayoutParams();
            params.height = 0;
            params.width = 0;
            holder.binding.linearVideo.setLayoutParams(params);
            holder.binding.image.setVisibility(View.VISIBLE);
            holder.binding.libraryDownload.setVisibility(View.VISIBLE);
            Glide.with(context).load(libraryDetails.get(position).getThumbnailFilePath()).into(holder.binding.image);
        }
        standard = "";
        Course = "";
        if (libraryDetails.get(position).getList().size() == 0) {
            ViewGroup.LayoutParams params = holder.binding.linearStandard.getLayoutParams();
            params.height = 0;
            params.width = 0;
            holder.binding.linearStandard.setLayoutParams(params);
            holder.binding.linearCourse.setLayoutParams(params);
        } else {
            holder.binding.linearStandard.setVisibility(View.VISIBLE);
            holder.binding.linearCourse.setVisibility(View.VISIBLE);
            for (int i = 0; i < libraryDetails.get(position).getList().size(); i++) {
                standard += libraryDetails.get(position).getList().get(i).getStandard();
                standard += "\n";
            }
            holder.binding.standard.setText(standard);
            holder.binding.course.setText(libraryDetails.get(position).getList().get(0).getBranchCourse().getCourse().getCourseName());
        }
        holder.binding.libraryEdit.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Edit Library?");
            image.setImageResource(R.drawable.ic_edit);
            AlertDialog dialog = builder.create();
            btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());
            btn_edit_yes.setOnClickListener(v12 -> {
                if (libraryDetails.get(position).getLibrary_Type() == 1) {
                    library_video_fragment orderplace = new library_video_fragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("LIBRARY_MST", libraryDetails.get(position));
                    orderplace.setArguments(bundle);
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = (fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                } else {
                    library_fragment orderplace = new library_fragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("LIBRARY_MST", libraryDetails.get(position));
                    orderplace.setArguments(bundle);
                    FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = (fragmentManager).beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                dialog.dismiss();
            });
            dialog.show();
        });
        holder.binding.libraryDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
            TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
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
                            if (model != null && model.isCompleted()) {
                                if (model.isData()) {
                                    Toast.makeText(context, "Library Removed Successfully.", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        progressBarHelper.hideProgressDialog();
                    }
                });
            });
            dialog.show();
        });
        holder.binding.libraryDownload.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
            builder.setView(dialogView);
            builder.setCancelable(true);
            TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
            TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
            ImageView image = dialogView.findViewById(R.id.image);
            TextView title = dialogView.findViewById(R.id.title);
            title.setText("Are you sure that you want to Download Document?");
            image.setImageResource(R.drawable.download);
            AlertDialog dialog = builder.create();
            btn_edit_no.setOnClickListener(v15 -> dialog.dismiss());
            btn_edit_yes.setOnClickListener(v16 -> {
                dialog.dismiss();
                String filetype = libraryDetails.get(position).getDocFilePath();
                Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
                DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(filetype);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                Name = libraryDetails.get(position).getDocFileName();
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
        return libraryDetails.size();
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

    public class ViewHolder extends RecyclerView.ViewHolder {

        LibraryMasterDeatilListBinding binding;

        public ViewHolder(@NonNull LibraryMasterDeatilListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            standard = binding.standard.getText().toString();
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }


}
