package com.example.genius.ui.FeeStructure;

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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
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

import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.FeeStructureData;
import com.example.genius.Model.FeeStructureModel;
import com.example.genius.Model.FeeStructureSingleData;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.FeestructureDetailListBinding;
import com.example.genius.databinding.FragmentFeeStructureBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
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
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
public class FeeStructureFragment extends Fragment {

    FragmentFeeStructureBinding binding;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    BannerMaster_Adapter bannerMaster_adapter;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> branchitem = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> branchid = new ArrayList<>(),courseid = new ArrayList<>();
    String[] BRANCHITEM,COURSEITEM;
    Integer[] BRANCHID,COURSEID;
    int flag = 0;
    byte[] imageVal;
    Bitmap bitmap;
    File instrumentFileDestination;
    OnBackPressedCallback callback;
    Long courseID = Long.valueOf(0), studentid = Long.valueOf(0);
    long TransactionId, FeesId, FeesDetailId;
    String OriginFilename,stdname = "",FilePath;
    UserModel userpermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Fee Structure Master");
        binding = FragmentFeeStructureBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission()){
            if (model.getPageInfo().getPageID() == 15 && !model.getPackageRightinfo().isCreatestatus()){
                binding.linearCreateFee.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetBannerDetails();
            GetAllCourse();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();

        binding.bannerImage.setOnClickListener(v -> {
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
        });

        binding.saveBanner.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.courseName.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (binding.standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please select standard.", Toast.LENGTH_SHORT).show();
                } else if (instrumentFileDestination == null) {
                    Toast.makeText(context, "Please upload image.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData classmodel = new BranchClassSingleModel.BranchClassData(studentid);
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, "");
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    FeeStructureModel model = new FeeStructureModel(0,0,OriginFilename,branch,
                            transactionModel,rowStatusModel,FilePath,binding.remarks.getText().toString(),course,classmodel);
                    String data = new Gson().toJson(model);
                    Call<FeeStructureSingleData> call = apiCalling.FeesMaintenance(data,true,  MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                    call.enqueue(new Callback<FeeStructureSingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<FeeStructureSingleData> call, @NotNull Response<FeeStructureSingleData> response) {
                            if (response.isSuccessful()) {
                                FeeStructureSingleData data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.courseName.setSelection(0);
                                    binding.standard.setSelection(0);
                                    binding.remarks.setText("");
                                    binding.bannerImage.setText("");
                                    binding.imageView.setVisibility(View.GONE);
                                    GetBannerDetails();
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<FeeStructureSingleData> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        binding.editBanner.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.courseName.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (binding.standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please select standard.", Toast.LENGTH_SHORT).show();
                } else if (binding.bannerImage.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Please upload image.", Toast.LENGTH_SHORT).show();
                }else {
                    progressBarHelper.showProgressDialog();
                    Call<FeeStructureSingleData> call;
                    BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData classmodel = new BranchClassSingleModel.BranchClassData(studentid);
                    TransactionModel transactionModel = new TransactionModel(TransactionId, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    FeeStructureModel model = new FeeStructureModel(FeesId,FeesDetailId,OriginFilename,branch,
                            transactionModel,rowStatusModel,FilePath,binding.remarks.getText().toString(),course,classmodel);
                    String data = new Gson().toJson(model);
                    if (instrumentFileDestination != null) {
                        call = apiCalling.FeesMaintenance(data,true, MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                        , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                    } else {
                        call = apiCalling.FeesMaintenance(data,false, MultipartBody.Part.createFormData("attachment", ""
                                        , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                    }
                    call.enqueue(new Callback<FeeStructureSingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<FeeStructureSingleData> call, @NotNull Response<FeeStructureSingleData> response) {
                            if (response.isSuccessful()) {
                                FeeStructureSingleData data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    binding.saveBanner.setVisibility(View.VISIBLE);
                                    binding.editBanner.setVisibility(View.GONE);
                                    stdname = "";
                                    binding.courseName.setSelection(0);
                                    binding.standard.setSelection(0);
                                    binding.remarks.setText("");
                                    binding.bannerImage.setText("");
                                    binding.imageView.setVisibility(View.GONE);
                                    GetBannerDetails();
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<FeeStructureSingleData> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
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
                home_fragment profileFragment = new home_fragment();
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

    public void GetAllCourse()
    {
        progressBarHelper.showProgressDialog();
        courseitem.clear();
        courseid.clear();
        courseitem.add("Select Course");
        courseid.add(0);

        Call<BranchCourseModel> call = apiCalling.GetAllCourseDDL(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<BranchCourseModel>() {
            @Override
            public void onResponse(Call<BranchCourseModel> call, Response<BranchCourseModel> response) {
                if (response.isSuccessful()){
                    BranchCourseModel data = response.body();
                    if (data.isCompleted()){
                        List<BranchCourseModel.BranchCourceData> list = data.getData();
                        if (list != null && list.size() > 0){
                            for (BranchCourseModel.BranchCourceData model : list) {
                                String coursename = model.getCourse().getCourseName();
                                courseitem.add(coursename);
                                int id = Integer.parseInt(String.valueOf(model.getCourse_dtl_id()));
                                courseid.add(id);
                            }
                            COURSEITEM = new String[courseitem.size()];
                            COURSEITEM = courseitem.toArray(COURSEITEM);

                            COURSEID = new Integer[courseid.size()];
                            COURSEID = courseid.toArray(COURSEID);

                            bindcourse();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<BranchCourseModel> call, Throwable t) {

            }
        });
    }

    public void bindcourse() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, COURSEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.courseName.setAdapter(adapter);
        binding.courseName.setOnItemSelectedListener(selectcourse);
    }

    AdapterView.OnItemSelectedListener selectcourse =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    courseID = Long.valueOf(courseid.get(position).toString());
                    if (binding.courseName.getSelectedItem().equals("Select Course")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (binding.courseName.getSelectedItemId() != 0){
                        GetAllStandard(courseID);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetAllStandard(long coursedetailid) {
        progressBarHelper.showProgressDialog();
        branchitem.clear();
        branchid.clear();
        branchitem.add("Select Standard");
        branchid.add(0);

        Call<BranchClassModel> call = apiCalling.Get_Class_Spinner(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),coursedetailid);
        call.enqueue(new Callback<BranchClassModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchClassModel> call, @NotNull Response<BranchClassModel> response) {
                if (response.isSuccessful()) {
                    BranchClassModel data = response.body();
                    if (data.getCompleted()) {
                        List<BranchClassSingleModel.BranchClassData> list = data.getData();
                        for (BranchClassSingleModel.BranchClassData model : list) {
                            String stdname = model.getClassModel().getClassName();
                            branchitem.add(stdname);
                            int id = Integer.parseInt(String.valueOf(model.getClass_dtl_id()));
                            branchid.add(id);
                        }
                        BRANCHITEM = new String[branchitem.size()];
                        BRANCHITEM = branchitem.toArray(BRANCHITEM);

                        BRANCHID = new Integer[branchid.size()];
                        BRANCHID = branchid.toArray(BRANCHID);

                        bindbranch();
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<BranchClassModel> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindbranch() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BRANCHITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.standard.setAdapter(adapter);
        if (stdname != "") {
            selectSpinnerValue(binding.standard,stdname);
        }
        binding.standard.setOnItemSelectedListener(onItemSelectedListener6);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    studentid = Long.valueOf(branchid.get(position).toString());
                    if (binding.standard.getSelectedItem().equals("Select Standard")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

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
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    InputStream imageStream;
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    binding.imageView.setVisibility(View.VISIBLE);
                    binding.imageView.setImageBitmap(bitmap);
                    binding.bannerImage.setText("Attached");
                    binding.bannerImage.setTextColor(context.getResources().getColor(R.color.black));
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
        Call<FeeStructureData> call = apiCalling.GetFeesByBranchID(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<FeeStructureData>() {
            @Override
            public void onResponse(@NotNull Call<FeeStructureData> call, @NotNull Response<FeeStructureData> response) {
                if (response.isSuccessful()) {
                    FeeStructureData bannerData = response.body();
                    List<FeeStructureModel> bannerDataList;
                    if (bannerData != null) {
                        bannerDataList = bannerData.getData();
                        if (bannerDataList != null) {
                            if (bannerDataList.size() > 0) {
                                binding.text.setVisibility(View.VISIBLE);
                                binding.bannerRv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                binding.bannerRv.setLayoutManager(new GridLayoutManager(context, 1));
                                bannerMaster_adapter = new BannerMaster_Adapter(context, bannerDataList);
                                bannerMaster_adapter.notifyDataSetChanged();
                                binding.bannerRv.setAdapter(bannerMaster_adapter);
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<FeeStructureData> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void selectStandard() {
        branchitem.clear();
        branchid.clear();
        branchitem.add("Select Standard");
        branchid.add(0);

        BRANCHITEM = new String[branchitem.size()];
        BRANCHITEM = branchitem.toArray(BRANCHITEM);

        bindstd();
    }

    public void bindstd() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BRANCHITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.standard.setAdapter(adapter);
        binding.standard.setOnItemSelectedListener(onItemSelectedListener6);
    }

    public class BannerMaster_Adapter extends RecyclerView.Adapter<BannerMaster_Adapter.ViewHolder> {

        Context context;
        List<FeeStructureModel> bannerDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        UserModel userpermission;

        public BannerMaster_Adapter(Context context, List<FeeStructureModel> bannerDetails) {
            this.context = context;
            this.bannerDetails = bannerDetails;
        }

        @NotNull
        @Override
        public BannerMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(FeestructureDetailListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull BannerMaster_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            for (UserModel.UserPermission model : userpermission.getPermission()){
                if (model.getPageInfo().getPageID() == 15){
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
            holder.binding.remark.setText(bannerDetails.get(position).getRemark());
            holder.binding.course.setText(bannerDetails.get(position).getBranchCourse().getCourse().getCourseName());
            holder.binding.standard.setText(bannerDetails.get(position).getBranchClass().getClassModel().getClassName());
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
                title.setText("Are you sure that you want to edit fee structure?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    final NestedScrollView scroll = ((Activity) context).findViewById(R.id.banner_scroll);
                    binding.saveBanner.setVisibility(View.GONE);
                    binding.editBanner.setVisibility(View.VISIBLE);
                    binding.bannerImage.setText("Attached");
                    binding.bannerImage.setTextColor(context.getResources().getColor(R.color.black));
                    TransactionId = bannerDetails.get(position).getTransaction().getTransactionId();
                    OriginFilename = bannerDetails.get(position).getFileName();
                    FilePath = bannerDetails.get(position).getFilePath().replace("https://mastermind.org.in","");
                    FeesId = bannerDetails.get(position).getFeesID();
                    FeesDetailId = bannerDetails.get(position).getFeesDetailID();
                    studentid = bannerDetails.get(position).getBranchClass().getClass_dtl_id();
                    courseID = bannerDetails.get(position).getBranchCourse().getCourse_dtl_id();
                    stdname= bannerDetails.get(position).getBranchClass().getClassModel().getClassName();
                    selectSpinnerValue(binding.courseName, bannerDetails.get(position).getBranchCourse().getCourse().getCourseName());
                    binding.remarks.setText(bannerDetails.get(position).getRemark());
                    binding.imageView.setVisibility(View.VISIBLE);
                    Glide.with(context).load(bannerDetails.get(position).getFilePath()).into(binding.imageView);
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
                title.setText("Are you sure that you want to delete this Fee Structure?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

                btn_delete.setOnClickListener(v14 -> {
                    dialog.dismiss();
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.RemoveFees(bannerDetails.get(position).getFeesID()
                            , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
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
                });
                dialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return bannerDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            FeestructureDetailListBinding binding;

            public ViewHolder(@NonNull FeestructureDetailListBinding itemView) {
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

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }
}