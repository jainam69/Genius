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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.FeeStructureData;
import com.example.genius.Model.FeeStructureModel;
import com.example.genius.Model.FeeStructureSingleData;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.example.genius.utils.ImageUtility;
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

    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    Boolean selectfile = false;
    String BranchID, attach = "";
    SearchableSpinner branch, standard;
    TextView banner_image, text, id, image, transactionid, bannerid;
    RecyclerView banner_rv;
    BannerMaster_Adapter bannerMaster_adapter;
    Button save_banner, edit_banner;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> branchitem = new ArrayList<>();
    List<Integer> branchid = new ArrayList<>();
    String[] BRANCHITEM;
    Integer[] BRANCHID;
    int flag = 0;
    byte[] imageVal;
    Bitmap bitmap;
    File instrumentFileDestination;
    OnBackPressedCallback callback;
    NestedScrollView banner_scroll;
    Long adminid = Long.valueOf(0), teacherid = Long.valueOf(0), studentid = Long.valueOf(0);
    EditText remarks;
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    ImageView imageView;
    long TransactionId, FeesId, FeesDetailId;
    String Description = "none", Extension,FinalFileName,RandomFileName,OriginFilename;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Fee Structure");
        View root = inflater.inflate(R.layout.fragment_fee_structure, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        branch = root.findViewById(R.id.branch);
        standard = root.findViewById(R.id.standard);
        banner_image = root.findViewById(R.id.banner_image);
        save_banner = root.findViewById(R.id.save_banner);
        edit_banner = root.findViewById(R.id.edit_banner);
        banner_rv = root.findViewById(R.id.banner_rv);
        text = root.findViewById(R.id.text);
        id = root.findViewById(R.id.id);
        image = root.findViewById(R.id.image);
        transactionid = root.findViewById(R.id.transactionid);
        bannerid = root.findViewById(R.id.bannerid);
        banner_scroll = root.findViewById(R.id.banner_scroll);
        remarks = root.findViewById(R.id.remarks);
        imageView = root.findViewById(R.id.imageView);

        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        banner_image.setOnClickListener(v -> {
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

        save_banner.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (Function.isNetworkAvailable(context)) {
                if (standard.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please select standard", Toast.LENGTH_SHORT).show();
                } else if (instrumentFileDestination == null) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please upload image", Toast.LENGTH_SHORT).show();
                } else {
                    if (!remarks.getText().toString().isEmpty()){
                        Description = remarks.getText().toString();
                    }
                    Call<FeeStructureSingleData> call = apiCalling.FeesMaintenance(0, 0, studentid
                            , Long.parseLong(BranchID), Description, actualdate.format(Calendar.getInstance().getTime())
                            , Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID)
                            , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                            0, "0", "0", true,  MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                    call.enqueue(new Callback<FeeStructureSingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<FeeStructureSingleData> call, @NotNull Response<FeeStructureSingleData> response) {
                            if (response.isSuccessful()) {
                                FeeStructureSingleData data = response.body();
                                if (data.isCompleted()) {
                                    FeeStructureModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        standard.setSelection(0);
                                        remarks.setText("");
                                        banner_image.setText("");
                                        branch.setSelection(0);
                                        GetBannerDetails();
                                    }
                                }
                                Function.showToast(context, data.getMessage());
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
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_banner.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (Function.isNetworkAvailable(context)) {
                if (standard.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please select standard", Toast.LENGTH_SHORT).show();
                } else if (banner_image.getText().toString().isEmpty()) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please upload image", Toast.LENGTH_SHORT).show();
                }else {
                    if (!remarks.getText().toString().isEmpty()){
                        Description = remarks.getText().toString();
                    }
                    Call<FeeStructureSingleData> call;
                    if (instrumentFileDestination != null) {
                        call = apiCalling.FeesMaintenance(FeesId, FeesDetailId, studentid
                                , Long.parseLong(BranchID), Description, actualdate.format(Calendar.getInstance().getTime())
                                , Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID)
                                , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                                , TransactionId,"0", "0", true, MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                        , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                    } else {
                        FinalFileName = OriginFilename + "," + RandomFileName;
                        call = apiCalling.FeesMaintenance(FeesId, FeesDetailId, studentid
                                , Long.parseLong(BranchID), Description, actualdate.format(Calendar.getInstance().getTime())
                                , Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID)
                                , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                                , TransactionId, FinalFileName, Extension, false, MultipartBody.Part.createFormData("attachment", ""
                                        , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                    }
                    call.enqueue(new Callback<FeeStructureSingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<FeeStructureSingleData> call, @NotNull Response<FeeStructureSingleData> response) {
                            if (response.isSuccessful()) {
                                FeeStructureSingleData data = response.body();
                                if (data.isCompleted()) {
                                    FeeStructureModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        standard.setSelection(0);
                                        remarks.setText("");
                                        banner_image.setText("");
                                        branch.setSelection(0);
                                        imageView.setVisibility(View.GONE);
                                        GetBannerDetails();
                                    }
                                }
                                Function.showToast(context, data.getMessage());
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
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetBannerDetails();
            GetAllStandard();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

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
        return root;
    }


    public void GetAllStandard() {
        branchitem.add("Select Standard");
        branchid.add(0);

        Call<StandardData> call = apiCalling.GetAllStandard(Long.parseLong(BranchID));
        call.enqueue(new Callback<StandardData>() {
            @Override
            public void onResponse(@NotNull Call<StandardData> call, @NotNull Response<StandardData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    StandardData branchModel = response.body();
                    if (branchModel != null) {
                        if (branchModel.isCompleted()) {
                            List<StandardModel> respose = branchModel.getData();
                            for (StandardModel singleResponseModel : respose) {

                                String building_name = singleResponseModel.getStandard();
                                branchitem.add(building_name);

                                int building_id = Integer.parseInt(String.valueOf(singleResponseModel.getStandardID()));
                                branchid.add(building_id);
                            }
                            BRANCHITEM = new String[branchitem.size()];
                            BRANCHITEM = branchitem.toArray(BRANCHITEM);

                            BRANCHID = new Integer[branchid.size()];
                            BRANCHID = branchid.toArray(BRANCHID);

                            bindbranch();
                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<StandardData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindbranch() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BRANCHITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        standard.setAdapter(adapter);
        standard.setOnItemSelectedListener(onItemSelectedListener6);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    studentid = Long.valueOf(branchid.get(position).toString());
                    if (standard.getSelectedItem().equals("Select Standard")) {
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
                    selectfile = true;
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    InputStream imageStream;
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                    banner_image.setText("Attached");
                    banner_image.setTextColor(context.getResources().getColor(R.color.black));
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
                                text.setVisibility(View.VISIBLE);
                                banner_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                banner_rv.setLayoutManager(new GridLayoutManager(context, 1));
                                bannerMaster_adapter = new BannerMaster_Adapter(context, bannerDataList);
                                bannerMaster_adapter.notifyDataSetChanged();
                                banner_rv.setAdapter(bannerMaster_adapter);
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

    public class BannerMaster_Adapter extends RecyclerView.Adapter<BannerMaster_Adapter.ViewHolder> {

        Context context;
        List<FeeStructureModel> bannerDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;

        public BannerMaster_Adapter(Context context, List<FeeStructureModel> bannerDetails) {
            this.context = context;
            this.bannerDetails = bannerDetails;
        }

        @NotNull
        @Override
        public BannerMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BannerMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.feestructure_detail_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull BannerMaster_Adapter.ViewHolder holder, int position) {
            holder.remark.setText(bannerDetails.get(position).getRemark());
            holder.standard.setText(bannerDetails.get(position).getStandardInfo().getStandard());
            Glide.with(context).load(bannerDetails.get(position).getFilePath()).into(holder.banner_image);
            holder.banner_edit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to edit fee structure?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    final NestedScrollView scroll = ((Activity) context).findViewById(R.id.banner_scroll);
                    save_banner.setVisibility(View.GONE);
                    edit_banner.setVisibility(View.VISIBLE);
                    banner_image.setText("Attached");
                    banner_image.setTextColor(context.getResources().getColor(R.color.black));
                    if (bannerDetails.get(position).getFilePath().contains(".") && bannerDetails.get(position).getFilePath().contains("/")) {
                        Extension = bannerDetails.get(position).getFilePath().substring(bannerDetails.get(position).getFilePath().lastIndexOf(".") + 1);
                        String FileNameWithExtension = bannerDetails.get(position).getFilePath().substring(bannerDetails.get(position).getFilePath().lastIndexOf("/") + 1);
                        String[] FileNameArray = FileNameWithExtension.split("\\.");
                        RandomFileName = FileNameArray[0];
                    }
                    TransactionId = bannerDetails.get(position).getTransaction().getTransactionId();
                    OriginFilename = bannerDetails.get(position).getFileName();
                    FeesId = bannerDetails.get(position).getFeesID();
                    FeesDetailId = bannerDetails.get(position).getFeesDetailID();
                    studentid = bannerDetails.get(position).getStandardInfo().getStandardID();
                    selectSpinnerValue(standard, bannerDetails.get(position).getStandardInfo().getStandard());
                    remarks.setText(bannerDetails.get(position).getRemark());
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(context).load(bannerDetails.get(position).getFilePath()).into(imageView);
                    scroll.scrollTo(0, 0);
                    scroll.fullScroll(View.FOCUS_UP);
                });
                dialog.show();
            });
            holder.banner_delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
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
                                        Toast.makeText(context, "Fee Structure deleted successfully.", Toast.LENGTH_SHORT).show();
                                        bannerDetails.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
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

            ImageView banner_image, banner_edit, banner_delete;
            TextView standard, remark;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                banner_image = itemView.findViewById(R.id.banner_image);
                banner_edit = itemView.findViewById(R.id.banner_edit);
                banner_delete = itemView.findViewById(R.id.banner_delete);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

                standard = itemView.findViewById(R.id.standard);
                remark = itemView.findViewById(R.id.remark);
            }
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

}