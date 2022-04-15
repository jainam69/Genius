package com.example.genius.ui.Video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.GalleryData;
import com.example.genius.Model.GalleryModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.FragmentVideoBinding;
import com.example.genius.databinding.VideoMasterDeatilListBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.Activity.VideoViewActivity;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Gallery.GallerySelectorFragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class VideoFragment extends Fragment {

    FragmentVideoBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    int flag = 0;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    OnBackPressedCallback callback;
    String OriginFileName,FilePath;
    VideoMaster_Adapter videoMaster_adapter;
    UserModel userpermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Video Master");
        binding = FragmentVideoBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission()){
            if (model.getPageInfo().getPageID() == 85 && !model.getPackageRightinfo().isCreatestatus()){
                binding.linearCreateVideo.setVisibility(View.GONE);
            }
        }

        binding.attachmentVideo.setOnClickListener(v -> requestPermissionForVideo());

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetVideoDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        binding.saveVideo.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.attachmentVideo.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Attach Video.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, "");
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    GalleryModel model = new GalleryModel(0,branch,binding.videoDescription.getText().toString(),
                            rowStatusModel,transactionModel,2,FilePath,OriginFileName);
                    String data = new Gson().toJson(model);
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                    MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                    Call<GalleryModel.GallaryData1> call = apiCalling.GalleryImageMaintenance(data,true,uploadfile);
                    call.enqueue(new Callback<GalleryModel.GallaryData1>() {
                        @Override
                        public void onResponse(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Response<GalleryModel.GallaryData1> response) {
                            if (response.isSuccessful()) {
                                GalleryModel.GallaryData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.videoDescription.setText("");
                                    binding.attachmentVideo.setText("");
                                    binding.imageView.setVisibility(View.GONE);
                                    GetVideoDetails();
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editVideo.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.attachmentVideo.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Attach Video.", Toast.LENGTH_SHORT).show();
                }else {
                    progressBarHelper.showProgressDialog();
                    Call<GalleryModel.GallaryData1> call;
                    BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(binding.transactionId.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    GalleryModel model = new GalleryModel(Long.parseLong(binding.uniqueId.getText().toString()),branch,binding.videoDescription.getText().toString(),
                            rowStatusModel,transactionModel,2,FilePath,OriginFileName);
                    String data = new Gson().toJson(model);
                    if (instrumentFileDestination != null) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        call = apiCalling.GalleryImageMaintenance(data,true, uploadfile);
                    }else {
                        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
                        call = apiCalling.GalleryImageMaintenance(data, false, uploadfile);
                    }
                    call.enqueue(new Callback<GalleryModel.GallaryData1>() {
                        @Override
                        public void onResponse(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Response<GalleryModel.GallaryData1> response) {
                            if (response.isSuccessful()) {
                                GalleryModel.GallaryData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context,data.getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.saveVideo.setVisibility(View.VISIBLE);
                                    binding.editVideo.setVisibility(View.GONE);
                                    binding.videoDescription.setText("");
                                    binding.attachmentVideo.setText("");
                                    binding.imageView.setVisibility(View.GONE);
                                    GetVideoDetails();
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }else {
                Toast.makeText(context, "Please check your internet connectivity.", Toast.LENGTH_SHORT).show();
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                GallerySelectorFragment profileFragment = new GallerySelectorFragment();
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);
        return binding.getRoot();
    }


    private void requestPermissionForVideo() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
            if (!hasPermission) {
                ActivityCompat.requestPermissions(requireActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, 1);
            } else {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Video"), 4);
            }
        } else {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            getActivity().startActivityForResult(intent, 4);
        }
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == 4) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else if (resultCode == RESULT_OK) {
                try {
                    flag = 1;
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    if (Path != null) {
                        instrumentFileDestination = new File(Path);
                        binding.attachmentVideo.setText("Attached");
                        binding.attachmentVideo.setTextColor(context.getResources().getColor(R.color.black));
                    }
                } catch (Exception e) {
                    errored();
                }
            } else {
                errored();
            }
        }
    }

    public void userCancelled() {

    }

    public void GetVideoDetails() {
        Call<GalleryData> call = apiCalling.GetAllGalleryVideoBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<GalleryData>() {
            @Override
            public void onResponse(@NotNull Call<GalleryData> call, @NotNull Response<GalleryData> response) {
                if (response.isSuccessful()) {
                    GalleryData galleryMaster_model = response.body();
                    if (galleryMaster_model != null && galleryMaster_model.isCompleted()) {
                        List<GalleryModel> galleryModelList = galleryMaster_model.getData();
                        if (galleryModelList != null) {
                            if (galleryModelList.size() > 0) {
                                binding.text.setVisibility(View.VISIBLE);
                                binding.videoRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                binding.videoRv.setLayoutManager(new GridLayoutManager(context, 2));
                                videoMaster_adapter = new VideoMaster_Adapter(context, galleryModelList);
                                videoMaster_adapter.notifyDataSetChanged();
                                binding.videoRv.setAdapter(videoMaster_adapter);
                            }else {
                                binding.text.setVisibility(View.GONE);
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<GalleryData> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
    }

    public class VideoMaster_Adapter extends RecyclerView.Adapter<VideoMaster_Adapter.ViewHolder> {

        Context context;
        List<GalleryModel> galleryDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        UserModel userpermission;

        public VideoMaster_Adapter(Context context, List<GalleryModel> galleryDetails) {
            this.context = context;
            this.galleryDetails = galleryDetails;
        }

        @NotNull
        @Override
        public VideoMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(VideoMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull VideoMaster_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            for (UserModel.UserPermission model : userpermission.getPermission()){
                if (model.getPageInfo().getPageID() == 85){
                    if (!model.getPackageRightinfo().isCreatestatus()){
                        holder.binding.videoEdit.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isDeletestatus()){
                        holder.binding.videoDelete.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isCreatestatus() && !model.getPackageRightinfo().isDeletestatus()){
                        holder.binding.videoEdit.setVisibility(View.GONE);
                        holder.binding.videoDelete.setVisibility(View.GONE);
                    }
                }
            }
            holder.binding.description.setText(galleryDetails.get(position).getRemarks());
            holder.binding.videoEdit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Video?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    binding.saveVideo.setVisibility(View.GONE);
                    binding.editVideo.setVisibility(View.VISIBLE);
                    binding.attachmentVideo.setText("Attached");
                    binding.attachmentVideo.setTextColor(context.getResources().getColor(R.color.black));
                    binding.videoDescription.setText(galleryDetails.get(position).getRemarks());
                    OriginFileName = galleryDetails.get(position).getFileName();
                    FilePath = galleryDetails.get(position).getFilePath().replace("https://mastermind.org.in","");
                    binding.uniqueId.setText("" + galleryDetails.get(position).getUniqueID());
                    binding.transactionId.setText("" + galleryDetails.get(position).getTransaction().getTransactionId());
                    binding.videoScroll.fullScroll(View.FOCUS_UP);
                    binding.videoScroll.scrollTo(0, 0);
                });
                dialog.show();
            });
            holder.binding.videoDelete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this Video?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

                btn_delete.setOnClickListener(v14 -> {
                    dialog.dismiss();
                    if (Function.checkNetworkConnection(context)) {
                        progressBarHelper.showProgressDialog();
                        Call<CommonModel> call = apiCalling.RemoveGallery(galleryDetails.get(position).getUniqueID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        call.enqueue(new Callback<CommonModel>() {
                            @Override
                            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                                if (response.isSuccessful()) {
                                    CommonModel model = response.body();
                                    if (model.isCompleted()) {
                                        if (model.isData()) {
                                            Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                            galleryDetails.remove(position);
                                            notifyItemRemoved(position);
                                            notifyDataSetChanged();
                                        }else {
                                            Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
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
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            });
            holder.binding.videoView.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Play Video?");
                image.setImageResource(R.drawable.view);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v15 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v16 -> {
                    dialog.dismiss();
                    Intent intent = new Intent(context, VideoViewActivity.class);
                    intent.putExtra("ProIndex", "Video");
                    intent.putExtra("Description", galleryDetails.get(position).getRemarks());
                    Preferences.getInstance(context).setString(Preferences.KEY_VIDEO_BASE, galleryDetails.get(position).getFilePath());
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                });
                dialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return galleryDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            VideoMasterDeatilListBinding binding;

            public ViewHolder(@NonNull VideoMasterDeatilListBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
                userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
            }
        }
    }

    public String encodeDecode(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replace("\n", "");
    }
}