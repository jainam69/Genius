package com.example.genius.ui.Banner;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
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
import com.example.genius.Model.BannerData;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.BannerMasterDeatilListBinding;
import com.example.genius.databinding.FragmentBannerBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.example.genius.utils.ImageUtility;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@SuppressLint("SetTextI18n")
public class Banner_Fragment extends Fragment {

    FragmentBannerBinding binding;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    Boolean selectfile = false;
    String BranchID, attach = "",OriginalFileName,FilePath;
    BannerMaster_Adapter bannerMaster_adapter;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    int flag = 0;
    byte[] imageVal;
    Bitmap bitmap;
    Boolean isAdmin = false,isTeacher = false,isStudent = false;
    File instrumentFileDestination;
    OnBackPressedCallback callback;
    UserModel userpermission;
    List<BannerModel.BannerTypeEntity> listentity = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Banner Master");
        binding = FragmentBannerBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
             if (model.getPageInfo().getPageID() == 73 && !model.getPackageRightinfo().isCreatestatus()){
                binding.linearCreateBanner.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetBannerDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        binding.chAdmin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isAdmin = true;
                    listentity.add(new BannerModel.BannerTypeEntity(0,1));
                }else {
                    isAdmin = false;
                    listentity.removeIf(x->x.getTypeID() == 2);
                }
            }
        });
        binding.chTeacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isTeacher = true;
                    listentity.add(new BannerModel.BannerTypeEntity(0,2));
                }else {
                    isTeacher = false;
                    listentity.removeIf(x->x.getTypeID() == 2);
                }
            }
        });
        binding.chStudent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isStudent = true;
                    listentity.add(new BannerModel.BannerTypeEntity(0,3));
                }else {
                    isStudent = false;
                    listentity.removeIf(x->x.getTypeID() == 3);
                }
            }
        });

        binding.bannerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
                            != PackageManager.PERMISSION_GRANTED
                            && ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                                        , Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                    } else {
                        pickImage();
                    }
                } else {
                    pickImage();
                }
            }
        });

        binding.saveBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (binding.bannerImage.getText().toString().equals("")) {
                        Toast.makeText(context, "Please Upload Banner Image.", Toast.LENGTH_SHORT).show();
                    }else if (binding.chAdmin.isChecked() || binding.chStudent.isChecked() || binding.chTeacher.isChecked())
                    {
                        progressBarHelper.showProgressDialog();
                        BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, "");
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BannerModel model = new BannerModel(0,listentity,branch,transactionModel,rowStatusModel,
                                FilePath,OriginalFileName);
                        String data = new Gson().toJson(model);
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        Call<BannerModel.BannerlData1> call = apiCalling.BannerMaintenance(data,true,uploadfile);
                        call.enqueue(new Callback<BannerModel.BannerlData1>() {
                            @Override
                            public void onResponse(@NotNull Call<BannerModel.BannerlData1> call, @NotNull Response<BannerModel.BannerlData1> response) {
                                if (response.isSuccessful()) {
                                    BannerModel.BannerlData1 data = response.body();
                                    if (data.isCompleted()) {
                                        Toast.makeText(context,data.getMessage(), Toast.LENGTH_SHORT).show();
                                        binding.bannerImage.setText("");
                                        binding.chAdmin.setChecked(false);
                                        binding.chStudent.setChecked(false);
                                        binding.chTeacher.setChecked(false);
                                        GetBannerDetails();
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<BannerModel.BannerlData1> call, @NotNull Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(context, "Please Select SubType.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

       binding.editBanner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (binding.bannerImage.getText().toString().equals("")) {
                        Toast.makeText(context, "Please Upload Banner Image.", Toast.LENGTH_SHORT).show();
                    } else if (binding.chAdmin.isChecked() || binding.chTeacher.isChecked() || binding.chStudent.isChecked())
                    {
                        progressBarHelper.showProgressDialog();
                        Call<BannerModel.BannerlData1> call;
                        BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        TransactionModel transactionModel = new TransactionModel(Long.parseLong(binding.transactionid.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BannerModel model = new BannerModel(Long.parseLong(binding.bannerid.getText().toString()),listentity,branch,transactionModel,rowStatusModel,
                                FilePath,OriginalFileName);
                        String data = new Gson().toJson(model);
                        if (instrumentFileDestination != null) {
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                            MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                            call = apiCalling.BannerMaintenance(data,true, uploadfile);
                        }else {
                            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                            MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
                            call = apiCalling.BannerMaintenance(data,false, uploadfile);
                        }
                        call.enqueue(new Callback<BannerModel.BannerlData1>() {
                            @Override
                            public void onResponse(Call<BannerModel.BannerlData1> call, Response<BannerModel.BannerlData1> response) {
                                if (response.isSuccessful()) {
                                    BannerModel.BannerlData1 data = response.body();
                                    if (data.isCompleted()) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        binding.bannerImage.setText("");
                                        binding.chAdmin.setChecked(false);
                                        binding.chStudent.setChecked(false);
                                        binding.chTeacher.setChecked(false);
                                        binding.saveBanner.setVisibility(View.VISIBLE);
                                        binding.editBanner.setVisibility(View.GONE);
                                        GetBannerDetails();
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<BannerModel.BannerlData1> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(context, "Please Select SubType.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MasterSelectorFragment profileFragment = new MasterSelectorFragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        };
        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);
        return binding.getRoot();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_CODE_PICK_GALLERY) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else if (resultCode == RESULT_OK) {
                Uri image = result.getData();
                try {
                    flag = 1;
                    imageVal = null;
                    selectfile = true;
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    InputStream imageStream;
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    binding.bannerImage.setText("Attached");
                    binding.bannerImage.setTextColor(context.getResources().getColor(R.color.black));
                    attach = onGalleryImageResultInstrument(result);
                } catch (Exception e) {
                    errored();
                }
            } else {
                errored();
            }
        }
    }

    private void pickImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_PICK);
            intent.setType("image/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    public void userCancelled() {

    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
    }

    private String onGalleryImageResultInstrument(Intent data) {
        Bitmap rotatedBitmap;
        Bitmap bitmap = null;
        String encodedImage = "";
        Uri uri = data.getData();
        String Path = FUtils.getPath(requireContext(), uri);
        if (Path != null) {
            instrumentFileDestination = new File(Path);
        }
        try {
            bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ExifInterface ei = null;
        try {
            ei = new ExifInterface(String.valueOf(instrumentFileDestination));
        } catch (IOException e) {
            e.printStackTrace();
        }
        int orientation = 0;
        if (ei != null) {
            orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
        }
        int rotationDegree;
        if (orientation >= 0 && orientation <= 1) {
            rotationDegree = 0;
        } else if (orientation >= 2 && orientation <= 4) {
            rotationDegree = 180;
        } else if (orientation >= 8) {
            rotationDegree = 270;
        } else {
            rotationDegree = 90;
        }
        if (bitmap != null) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegree);
            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
            rotatedBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix, true);
            if (instrumentFileDestination != null) {
                if (instrumentFileDestination.length() >= 999999) {
                    int compressRatio = 90;
                    while (instrumentFileDestination.length() > 999999) {
                        OutputStream fOut = null;
                        try {
                            fOut = new FileOutputStream(instrumentFileDestination);
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, compressRatio, fOut);
                        compressRatio = compressRatio - 20;
                        try {
                            if (fOut != null) {
                                fOut.close();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                imageVal = ImageUtility.using(context).toBase64(instrumentFileDestination.getPath());
                encodedImage = Base64.encodeToString(imageVal, Base64.DEFAULT);
            }
        }
        return encodedImage;
    }

    public void GetBannerDetails() {
        Call<BannerData> call = apiCalling.GetAllBannerBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<BannerData>() {
            @Override
            public void onResponse(@NotNull Call<BannerData> call, @NotNull Response<BannerData> response) {
                if (response.isSuccessful()) {
                    BannerData bannerData = response.body();
                    if (bannerData.isCompleted()) {
                        List<BannerModel> bannerDataList = bannerData.getData();
                        if (bannerDataList != null) {
                            if (bannerDataList.size() > 0) {
                                binding.text.setVisibility(View.VISIBLE);
                                binding.bannerRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                binding.bannerRv.setLayoutManager(new GridLayoutManager(context, 2));
                                bannerMaster_adapter = new BannerMaster_Adapter(context, bannerDataList);
                                bannerMaster_adapter.notifyDataSetChanged();
                                binding.bannerRv.setAdapter(bannerMaster_adapter);
                            }else {
                                binding.text.setVisibility(View.GONE);
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BannerData> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public class BannerMaster_Adapter extends RecyclerView.Adapter<BannerMaster_Adapter.ViewHolder> {

        Context context;
        List<BannerModel> bannerDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        UserModel userpermission;

        public BannerMaster_Adapter(Context context, List<BannerModel> bannerDetails) {
            this.context = context;
            this.bannerDetails = bannerDetails;
        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(BannerMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            for (UserModel.UserPermission model : userpermission.getPermission())
            {
                if (model.getPageInfo().getPageID() == 73){
                    if (!model.getPackageRightinfo().isCreatestatus()){
                        holder.binding.bannerEdit.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isDeletestatus()){
                        holder.binding.bannerDelete.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isCreatestatus() && !model.getPackageRightinfo().isDeletestatus()){
                        holder.binding.linearActions.setVisibility(View.GONE);
                    }
                }
            }
            List<BannerModel.BannerTypeEntity> notitypelist = bannerDetails.get(position).getBannerType();
            String a = null;
            for (BannerModel.BannerTypeEntity model : notitypelist) {
                if (a != null) {
                    a = a + "-" + model.getTypeText();
                } else {
                    a = model.getTypeText();
                }
            }
            holder.binding.subType.setText("" + a);
            Glide.with(context).load(bannerDetails.get(position).getFilePath()).into(holder.binding.bannerImage);
            holder.binding.bannerEdit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Banner?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    final NestedScrollView scroll = ((Activity) context).findViewById(R.id.banner_scroll);
                    binding.saveBanner.setVisibility(View.GONE);
                    binding.editBanner.setVisibility(View.VISIBLE);
                    binding.transactionid.setText("" + bannerDetails.get(position).getTransaction().getTransactionId());
                    binding.bannerid.setText("" + bannerDetails.get(position).getBannerID());
                    binding.bannerImage.setText("Attached");
                    binding.bannerImage.setTextColor(context.getResources().getColor(R.color.black));
                    OriginalFileName = bannerDetails.get(position).getFileName();
                    FilePath = bannerDetails.get(position).getFilePath().replace("https://mastermind.org.in","");
                    List<BannerModel.BannerTypeEntity> notitypelist1 = bannerDetails.get(position).getBannerType();
                    for (BannerModel.BannerTypeEntity model : notitypelist1) {
                        if (model.getTypeID() == 1) {
                            binding.chAdmin.setChecked(true);
                            isAdmin = true;
                        }
                        if (model.getTypeID() == 2) {
                            binding.chTeacher.setChecked(true);
                            isTeacher = true;
                        }
                        if (model.getTypeID() == 3) {
                            binding.chStudent.setChecked(true);
                            isStudent = true;
                        }
                    }
                    listentity = notitypelist1;
                    scroll.scrollTo(0, 0);
                    scroll.fullScroll(View.FOCUS_UP);
                });
                dialog.show();
            });
            holder.binding.bannerDelete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this Banner?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        progressBarHelper.showProgressDialog();
                        Call<CommonModel> call = apiCalling.RemoveBanner(bannerDetails.get(position).getBannerID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        call.enqueue(new Callback<CommonModel>() {
                            @Override
                            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                                if (response.isSuccessful()) {
                                    CommonModel model = response.body();
                                    if (model.isCompleted()) {
                                        if (model.isData()) {
                                            Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                            bannerDetails.remove(position);
                                            notifyItemRemoved(position);
                                            notifyDataSetChanged();
                                        }else {
                                            Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                                progressBarHelper.hideProgressDialog();
                            }
                        });
                        dialog.dismiss();
                    }
                });
                dialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return bannerDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            BannerMasterDeatilListBinding binding;

            public ViewHolder(@NonNull BannerMasterDeatilListBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
                userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }

}