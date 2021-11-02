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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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

import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BannerData;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.GalleryData;
import com.example.genius.Model.GalleryModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.example.genius.utils.ImageUtility;
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

@SuppressLint("SetTextI18n")
public class Banner_Fragment extends Fragment {

    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    Boolean selectfile = false;
    String BranchID, attach = "";
    SearchableSpinner branch;
    CheckBox ch_admin, ch_teacher, ch_student;
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
    int flag = 0, check_value_admin, check_value_teacher, check_value_student;
    byte[] imageVal;
    Bitmap bitmap;
    File instrumentFileDestination;
    OnBackPressedCallback callback;
    NestedScrollView banner_scroll;
    Long adminid = Long.valueOf(0), teacherid = Long.valueOf(0), studentid = Long.valueOf(0);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Banner Master");
        View root = inflater.inflate(R.layout.fragment_banner, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        branch = root.findViewById(R.id.branch);
        ch_admin = root.findViewById(R.id.ch_admin);
        ch_teacher = root.findViewById(R.id.ch_teacher);
        ch_student = root.findViewById(R.id.ch_student);
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
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        ch_admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_value_admin = 1;
                } else {
                    check_value_admin = 0;
                }
            }
        });
        ch_teacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_value_teacher = 1;
                } else {
                    check_value_teacher = 0;
                }
            }
        });
        ch_student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_value_student = 1;
                } else {
                    check_value_student = 0;
                }
            }
        });

        banner_image.setOnClickListener(new View.OnClickListener() {
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

        save_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (banner_image.getText().toString().equals("")) {
                        Toast.makeText(context, "Please Upload Image.", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBarHelper.showProgressDialog();
                        List<BannerModel.BannerTypeEntity> typeModel = new ArrayList<>();
                        if (check_value_admin == 1) {
                            BannerModel.BannerTypeEntity model1 = new BannerModel.BannerTypeEntity("Admin", 1);
                            typeModel.add(model1);
                        }
                        if (check_value_teacher == 1) {
                            BannerModel.BannerTypeEntity model1 = new BannerModel.BannerTypeEntity("Teacher", 2);
                            typeModel.add(model1);
                        }
                        if (check_value_student == 1) {
                            BannerModel.BannerTypeEntity model1 = new BannerModel.BannerTypeEntity("Student", 3);
                            typeModel.add(model1);
                        }
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                        BannerModel model = new BannerModel(typeModel, branchModel, rowStatusModel, transactionModel, attach);
                        Call<BannerModel.BannerlData1> call = apiCalling.BannerMaintenance(model);
                        call.enqueue(new Callback<BannerModel.BannerlData1>() {
                            @Override
                            public void onResponse(@NotNull Call<BannerModel.BannerlData1> call, @NotNull Response<BannerModel.BannerlData1> response) {
                                if (response.isSuccessful()) {
                                    BannerModel.BannerlData1 data = response.body();
                                    if (data.isCompleted()) {
                                        BannerModel notimodel = data.getData();
                                        if (notimodel != null) {
                                            Toast.makeText(context, "Banner inserted successfully.", Toast.LENGTH_SHORT).show();
                                            banner_image.setText("");
                                            branch.setSelection(0);
                                            ch_admin.setChecked(false);
                                            ch_student.setChecked(false);
                                            ch_teacher.setChecked(false);
                                            GetBannerDetails();
                                        } else {
                                            Toast.makeText(context, "Banner not inserted.", Toast.LENGTH_SHORT).show();
                                        }
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
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_banner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (banner_image.getText().toString().equals("")) {
                        Toast.makeText(context, "Please Upload Image.", Toast.LENGTH_SHORT).show();
                    } else {
                        progressBarHelper.showProgressDialog();
                        List<BannerModel.BannerTypeEntity> typeModel = new ArrayList<>();
                        if (check_value_admin == 1) {
                            if (adminid > 0) {
                                BannerModel.BannerTypeEntity model1 = new BannerModel.BannerTypeEntity(adminid, "Admin", 1);
                                typeModel.add(model1);
                            } else {
                                BannerModel.BannerTypeEntity model1 = new BannerModel.BannerTypeEntity("Admin", 1);
                                typeModel.add(model1);
                            }
                        }
                        if (check_value_teacher == 1) {
                            if (teacherid > 0) {
                                BannerModel.BannerTypeEntity model1 = new BannerModel.BannerTypeEntity(teacherid, "Teacher", 2);
                                typeModel.add(model1);
                            } else {
                                BannerModel.BannerTypeEntity model1 = new BannerModel.BannerTypeEntity("Teacher", 2);
                                typeModel.add(model1);
                            }
                        }
                        if (check_value_student == 1) {
                            if (studentid > 0) {
                                BannerModel.BannerTypeEntity model1 = new BannerModel.BannerTypeEntity(studentid, "Student", 3);
                                typeModel.add(model1);
                            } else {
                                BannerModel.BannerTypeEntity model1 = new BannerModel.BannerTypeEntity("Student", 3);
                                typeModel.add(model1);
                            }
                        }
                        TransactionModel transactionModel = new TransactionModel(Long.parseLong(transactionid.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                        BannerModel model = new BannerModel(Long.parseLong(bannerid.getText().toString()), typeModel, branchModel, rowStatusModel, transactionModel, attach);
                        Call<BannerModel.BannerlData1> call = apiCalling.BannerMaintenance(model);
                        call.enqueue(new Callback<BannerModel.BannerlData1>() {
                            @Override
                            public void onResponse(Call<BannerModel.BannerlData1> call, Response<BannerModel.BannerlData1> response) {
                                if (response.isSuccessful()) {
                                    BannerModel.BannerlData1 data = response.body();
                                    if (data.isCompleted()) {
                                        BannerModel bannerModel = data.getData();
                                        if (bannerModel != null) {
                                            Toast.makeText(context, "Banner updated successfully.", Toast.LENGTH_SHORT).show();
                                            banner_image.setText("");
                                            branch.setSelection(0);
                                            ch_admin.setChecked(false);
                                            ch_student.setChecked(false);
                                            ch_teacher.setChecked(false);
                                            save_banner.setVisibility(View.VISIBLE);
                                            edit_banner.setVisibility(View.GONE);
                                            GetBannerDetails();
                                        } else {
                                            Toast.makeText(context, "Banner not updated.", Toast.LENGTH_SHORT).show();
                                        }
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
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetBannerDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

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
        return root;
    }

    public void GetAllBranch() {
        branchitem.add("Select Branch");
        branchid.add(0);

        Call<BranchModel> call = apiCalling.GetAllBranch();
        call.enqueue(new Callback<BranchModel>() {
            @Override
            public void onResponse(@NotNull Call<BranchModel> call, @NotNull Response<BranchModel> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    BranchModel branchModel = response.body();
                    if (branchModel != null) {
                        if (branchModel.isCompleted()) {
                            List<BranchModel.BranchData> respose = branchModel.getData();
                            for (BranchModel.BranchData singleResponseModel : respose) {

                                String building_name = singleResponseModel.getBranchName();
                                branchitem.add(building_name);

                                int building_id = Integer.parseInt(String.valueOf(singleResponseModel.getBranchID()));
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
            public void onFailure(@NotNull Call<BranchModel> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindbranch() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BRANCHITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        branch.setAdapter(adapter);
        branch.setOnItemSelectedListener(onItemSelectedListener6);

    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BranchID = branchid.get(position).toString();
                    if (branch.getSelectedItem().equals("Select Branch")) {
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
                                List<BannerModel> list = new ArrayList<>();
                                for (BannerModel singlemodel : bannerDataList) {
                                    if (singlemodel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(singlemodel);
                                    }
                                }
                                text.setVisibility(View.VISIBLE);
                                banner_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                banner_rv.setLayoutManager(new GridLayoutManager(context, 2));
                                bannerMaster_adapter = new BannerMaster_Adapter(context, list);
                                bannerMaster_adapter.notifyDataSetChanged();
                                banner_rv.setAdapter(bannerMaster_adapter);
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
        int id;

        public BannerMaster_Adapter(Context context, List<BannerModel> bannerDetails) {
            this.context = context;
            this.bannerDetails = bannerDetails;
        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.banner_master_deatil_list, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            List<BannerModel.BannerTypeEntity> notitypelist = bannerDetails.get(position).getBannerType();
            String a = null;
            for (BannerModel.BannerTypeEntity model : notitypelist) {
                if (a != null) {
                    a = a + "-" + model.getTypeText();
                } else {
                    a = model.getTypeText();
                }
            }
            holder.sub_type.setText("" + a);
            attach = bannerDetails.get(position).getBannerImageText();
            /*imageVal = Base64.decode(attach, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(imageVal, 0, imageVal.length);*/
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
                title.setText("Are you sure that you want to Edit Banner?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    final NestedScrollView scroll = ((Activity) context).findViewById(R.id.banner_scroll);
                    save_banner.setVisibility(View.GONE);
                    edit_banner.setVisibility(View.VISIBLE);
                    transactionid.setText("" + bannerDetails.get(position).getTransaction().getTransactionId());
                    bannerid.setText("" + bannerDetails.get(position).getBannerID());
                    banner_image.setText("Attached");
                    banner_image.setTextColor(context.getResources().getColor(R.color.black));
                    List<BannerModel.BannerTypeEntity> notitypelist1 = bannerDetails.get(position).getBannerType();
                    for (BannerModel.BannerTypeEntity model : notitypelist1) {
                        if (model.getTypeID() == 1) {
                            ch_admin.setChecked(true);
                            adminid = model.getID();
                        }
                        if (model.getTypeID() == 2) {
                            ch_teacher.setChecked(true);
                            teacherid = model.getID();
                        }
                        if (model.getTypeID() == 3) {
                            ch_student.setChecked(true);
                            studentid = model.getID();
                        }
                    }
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
                                            Toast.makeText(context, "Banner deleted successfully.", Toast.LENGTH_SHORT).show();
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

            ImageView banner_image, banner_edit, banner_delete;
            TextView branch_name, sub_type;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                banner_image = itemView.findViewById(R.id.banner_image);
                banner_edit = itemView.findViewById(R.id.banner_edit);
                banner_delete = itemView.findViewById(R.id.banner_delete);
                branch_name = itemView.findViewById(R.id.branch_name);
                sub_type = itemView.findViewById(R.id.sub_type);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
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