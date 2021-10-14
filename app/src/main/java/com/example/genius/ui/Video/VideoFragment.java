package com.example.genius.ui.Video;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
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


import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.GalleryData;
import com.example.genius.Model.GalleryModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.VideoViewActivity;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Gallery.GalleryFragment;
import com.example.genius.ui.Gallery.GallerySelectorFragment;

import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class VideoFragment extends Fragment {

    TextView attachment_video, bid, video, text, transaction_id, unique_id;
    ImageView imageView;
    EditText video_description;
    Button save_video, edit_video;
    RecyclerView video_rv;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String Ans;
    int flag = 0;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    Boolean a, b;
    OnBackPressedCallback callback;
    NestedScrollView video_scroll;
    String videoData;
    ByteArrayOutputStream byteBuffer;
    byte[] imageVal;
    VideoMaster_Adapter videoMaster_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Video");
        View root = inflater.inflate(R.layout.fragment_video, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        attachment_video = root.findViewById(R.id.attachment_video);
        imageView = root.findViewById(R.id.imageView);
        video_description = root.findViewById(R.id.video_description);
        save_video = root.findViewById(R.id.save_video);
        edit_video = root.findViewById(R.id.edit_video);
        video_rv = root.findViewById(R.id.video_rv);
        bid = root.findViewById(R.id.bid);
        video = root.findViewById(R.id.video);
        video_scroll = root.findViewById(R.id.video_scroll);
//        text = root.findViewById(R.id.text);
        transaction_id = root.findViewById(R.id.transaction_id);
        unique_id = root.findViewById(R.id.unique_id);

        attachment_video.setOnClickListener(v -> requestPermissionForVideo());

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetVideoDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        save_video.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (attachment_video.getText().toString().equals(""))
                    Toast.makeText(context, "Please Attach Video.", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    GalleryModel gl = new GalleryModel(branchModel, videoData, video_description.getText().toString(), rowStatusModel, transactionModel);
                    Call<GalleryModel.GallaryData1> call = apiCalling.GalaryVideoMaintenance(gl);
                    call.enqueue(new Callback<GalleryModel.GallaryData1>() {
                        @Override
                        public void onResponse(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Response<GalleryModel.GallaryData1> response) {
                            if (response.isSuccessful()) {
                                GalleryModel.GallaryData1 data = response.body();
                                if (data != null && data.isCompleted()) {
                                    GalleryModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, "Video Inserted Successfully...", Toast.LENGTH_SHORT).show();
                                        video_description.setText("");
                                        attachment_video.setText("");
                                        imageView.setVisibility(View.GONE);
                                        GetVideoDetails();
                                    } else {
                                        Toast.makeText(context, "Video not Inserted...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_video.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (attachment_video.getText().toString().equals(""))
                    Toast.makeText(context, "Please Attach Video.", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(transaction_id.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    GalleryModel gl = new GalleryModel(Long.parseLong(unique_id.getText().toString()), branchModel, videoData, video_description.getText().toString(), rowStatusModel, transactionModel);
                    Call<GalleryModel.GallaryData1> call = apiCalling.GalaryVideoMaintenance(gl);
                    call.enqueue(new Callback<GalleryModel.GallaryData1>() {
                        @Override
                        public void onResponse(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Response<GalleryModel.GallaryData1> response) {
                            if (response.isSuccessful()) {
                                GalleryModel.GallaryData1 data = response.body();
                                if (data != null && data.isCompleted()) {
                                    GalleryModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        save_video.setVisibility(View.VISIBLE);
                                        edit_video.setVisibility(View.GONE);
                                        Toast.makeText(context, "Video Updated Successfully...", Toast.LENGTH_SHORT).show();
                                        video_description.setText("");
                                        attachment_video.setText("");
                                        imageView.setVisibility(View.GONE);
                                        GetVideoDetails();
                                    } else {
                                        Toast.makeText(context, "Video not Updated...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
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
        return root;
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
            /*Intent intent = new Intent();
            intent.setType("video/*");*/
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            //intent.setAction(Intent.ACTION_GET_CONTENT);
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
                Uri image = result.getData();
                try {
                    flag = 1;
                    InputStream imageStream;
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    if (Path != null) {
                        instrumentFileDestination = new File(Path);
                        imageStream = requireActivity().getContentResolver().openInputStream(image);
                        attachment_video.setText("Attached");
                        attachment_video.setTextColor(context.getResources().getColor(R.color.black));

                        //Conversion Code
                        Uri selectedVideoUri = result.getData();
                        String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.SIZE, MediaStore.Video.Media.DURATION};
                        @SuppressLint("Recycle") Cursor cursor = context.getContentResolver().query(selectedVideoUri, projection, null, null, null);

                        cursor.moveToFirst();
                        InputStream inputStream = null;
                        // Converting the video in to the bytes
                        try {
                            inputStream = context.getContentResolver().openInputStream(selectedVideoUri);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        int bufferSize = 1024;
                        byte[] buffer = new byte[bufferSize];
                        byteBuffer = new ByteArrayOutputStream();
                        int len = 0;
                        try {
                            if (inputStream != null) {
                                while ((len = inputStream.read(buffer)) != -1) {
                                    byteBuffer.write(buffer, 0, len);
                                }
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        System.out.println("converted!");

                        //Converting bytes into base64
                        videoData = Base64.encodeToString(byteBuffer.toByteArray(), Base64.DEFAULT);
                        Log.d("VideoData**>  ", videoData);
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
                                List<GalleryModel> list = new ArrayList<>();
                                for (GalleryModel singlemodel : galleryModelList) {
                                    if (singlemodel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(singlemodel);
                                    }
                                }
                                video_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                video_rv.setLayoutManager(new GridLayoutManager(context, 2));
                                videoMaster_adapter = new VideoMaster_Adapter(context, list);
                                videoMaster_adapter.notifyDataSetChanged();
                                video_rv.setAdapter(videoMaster_adapter);
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

        public VideoMaster_Adapter(Context context, List<GalleryModel> galleryDetails) {
            this.context = context;
            this.galleryDetails = galleryDetails;
        }

        @NotNull
        @Override
        public VideoMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new VideoMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.video_master_deatil_list, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull VideoMaster_Adapter.ViewHolder holder, int position) {
            holder.description.setText(galleryDetails.get(position).getRemarks());
            holder.video_image.setOnClickListener(v -> {
                Intent intent = new Intent(context, VideoViewActivity.class);
                intent.putExtra("ProIndex", "Video");
                intent.putExtra("Description", galleryDetails.get(position).getRemarks());
                Preferences.getInstance(context).setString(Preferences.KEY_VIDEO_BASE, galleryDetails.get(position).getFileEncoded());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            });
            holder.video_edit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Video?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    save_video.setVisibility(View.GONE);
                    edit_video.setVisibility(View.VISIBLE);
                    attachment_video.setText("Attached");
                    attachment_video.setTextColor(context.getResources().getColor(R.color.black));
                    video_description.setText(galleryDetails.get(position).getRemarks());
                    unique_id.setText("" + galleryDetails.get(position).getUniqueID());
                    transaction_id.setText("" + galleryDetails.get(position).getTransaction().getTransactionId());
                    video_scroll.fullScroll(View.FOCUS_UP);
                    video_scroll.scrollTo(0, 0);
                });
                dialog.show();
            });
            holder.video_delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
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
                                    if (model != null && model.isCompleted()) {
                                        if (model.isData()) {
                                            Toast.makeText(context, "Video Deleted Successfully...", Toast.LENGTH_SHORT).show();
                                            galleryDetails.remove(position);
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
                        dialog.dismiss();
                    } else {
                        Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            });
            holder.video_view.setOnClickListener(v -> {
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

                btn_edit_no.setOnClickListener(v15 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v16 -> {
                    dialog.dismiss();
                    Intent intent = new Intent(context, VideoViewActivity.class);
                    intent.putExtra("ProIndex", "Video");
                    intent.putExtra("Description", galleryDetails.get(position).getRemarks());
                    Preferences.getInstance(context).setString(Preferences.KEY_VIDEO_BASE, galleryDetails.get(position).getFileEncoded());
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

            TextView description;
            ImageView video_view, video_edit, video_delete, video_image;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                description = itemView.findViewById(R.id.description);
                video_view = itemView.findViewById(R.id.video_view);
                video_delete = itemView.findViewById(R.id.video_delete);
                video_edit = itemView.findViewById(R.id.video_edit);
                video_image = itemView.findViewById(R.id.video_image);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }
}