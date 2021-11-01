package com.example.genius.ui.Library_Fragment;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.example.genius.Model.CategoryData;
import com.example.genius.Model.CategoryModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.FeeStructureData;
import com.example.genius.Model.FeeStructureModel;
import com.example.genius.Model.FeeStructureSingleData;
import com.example.genius.Model.LibraryData;
import com.example.genius.Model.LibraryModel;
import com.example.genius.Model.LibrarySingleData;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.FeeStructure.FeeStructureFragment;
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
public class LibraryImageFragment extends Fragment {

    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    LibraryModel libraryModel;
    SearchableSpinner category;
    EditText title, description;
    TextView image;
    byte[] imageVal;
    Bitmap bitmap;
    File instrumentFileDestination;
    Boolean selectfile = false;
    String BranchID, attach = "";
    int flag = 0;
    Button save_image, edit_image;
    List<String> branchitem = new ArrayList<>();
    List<Integer> branchid = new ArrayList<>();
    String[] BRANCHITEM;
    Integer[] BRANCHID;
    long categoryid;
    long TransactionId, FeesId, FeesDetailId;
    String FileName, FilePath, Extension;
    long LibraryID, LibraryDetailID;
    BannerMaster_Adapter bannerMaster_adapter;
    RecyclerView library_image_rv;
    ImageView imageView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Library Image Entry");
        View root = inflater.inflate(R.layout.library_image_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        category = root.findViewById(R.id.category);
        title = root.findViewById(R.id.title);
        description = root.findViewById(R.id.description);
        image = root.findViewById(R.id.image);
        save_image = root.findViewById(R.id.save_image);
        edit_image = root.findViewById(R.id.edit_image);
        library_image_rv = root.findViewById(R.id.library_image_rv);
        imageView = root.findViewById(R.id.imageView);

        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetBannerDetails();
            GetAllStandard();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        image.setOnClickListener(v -> {
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

        save_image.setOnClickListener(view -> {
            progressBarHelper.showProgressDialog();
            if (Function.isNetworkAvailable(context)) {
                if (title.getText().toString().trim().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (category.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please select category", Toast.LENGTH_SHORT).show();
                } else if (instrumentFileDestination == null) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please upload image", Toast.LENGTH_SHORT).show();
                } else {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                    MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                    Call<LibrarySingleData> call = apiCalling.LibraryMaintenance(0, 0, title.getText().toString()
                            , "0", "0", "0", description.getText().toString()
                            , Long.parseLong(BranchID), categoryid
                            , (int) Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID)
                            , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                            0, true, 2, uploadfile);
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                LibrarySingleData data = response.body();
                                if (data.isCompleted()) {
                                    LibraryModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        image.setText("");
                                        title.setText("");
                                        description.setText("");
                                        category.setSelection(0);
                                        GetBannerDetails();
                                    }
                                }
                                Function.showToast(context, data.getMessage());
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<LibrarySingleData> call, @NotNull Throwable t) {
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
        edit_image.setOnClickListener(view -> {
            progressBarHelper.showProgressDialog();
            if (Function.isNetworkAvailable(context)) {
                if (title.getText().toString().trim().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (category.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please select category", Toast.LENGTH_SHORT).show();
                } else {
                    Call<LibrarySingleData> call;
                    if (instrumentFileDestination != null) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        call = apiCalling.LibraryMaintenance(LibraryID, LibraryDetailID, title.getText().toString()
                                , "0", FileName, Extension, description.getText().toString()
                                , Long.parseLong(BranchID), categoryid
                                , (int) Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID)
                                , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                                TransactionId, true, 2, uploadfile);
                    } else {
                        call = apiCalling.LibraryMaintenance(LibraryID, LibraryDetailID, title.getText().toString()
                                , "0", FileName, Extension, description.getText().toString()
                                , Long.parseLong(BranchID), categoryid
                                , (int) Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID)
                                , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                                TransactionId, false, 2, null);
                    }
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                LibrarySingleData data = response.body();
                                if (data.isCompleted()) {
                                    LibraryModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        image.setText("");
                                        title.setText("");
                                        description.setText("");
                                        category.setSelection(0);
                                        imageView.setVisibility(View.GONE);
                                        imageView.setImageDrawable(null);
                                        GetBannerDetails();
                                    }
                                }
                                Function.showToast(context, data.getMessage());
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<LibrarySingleData> call, @NotNull Throwable t) {
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
        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                LibrarySelectorFragment profileFragment = new LibrarySelectorFragment();
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
                Uri uriimage = result.getData();
                try {
                    flag = 1;
                    imageVal = null;
                    selectfile = true;
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    InputStream imageStream;
                    imageStream = requireActivity().getContentResolver().openInputStream(uriimage);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    image.setText("Attached");
                    image.setTextColor(context.getResources().getColor(R.color.black));
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

    public void GetAllStandard() {
        branchitem.add("Select Category");
        branchid.add(0);

        Call<CategoryData> call = apiCalling.GetAllCategory(Long.parseLong(BranchID));
        call.enqueue(new Callback<CategoryData>() {
            @Override
            public void onResponse(@NotNull Call<CategoryData> call, @NotNull Response<CategoryData> response) {
                if (response.isSuccessful()) {
                    CategoryData branchModel = response.body();
                    if (branchModel != null) {
                        if (branchModel.isCompleted()) {
                            List<CategoryModel> respose = branchModel.getData();
                            for (CategoryModel singleResponseModel : respose) {

                                String building_name = singleResponseModel.getCategory();
                                branchitem.add(building_name);

                                int building_id = Integer.parseInt(String.valueOf(singleResponseModel.getCategoryID()));
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
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<CategoryData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindbranch() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BRANCHITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(onItemSelectedListener6);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    categoryid = Long.parseLong(branchid.get(position).toString());
                    if (category.getSelectedItem().equals("Select Category")) {
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

    public void GetBannerDetails() {
        Call<LibraryData> call = apiCalling.GetAllLibrary(2, Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<LibraryData>() {
            @Override
            public void onResponse(@NotNull Call<LibraryData> call, @NotNull Response<LibraryData> response) {
                if (response.isSuccessful()) {
                    LibraryData bannerData = response.body();
                    if (bannerData.isCompleted()) {
                        List<LibraryModel> bannerDataList = bannerData.getData();
                        if (bannerDataList != null) {
                            if (bannerDataList.size() > 0) {
                                List<LibraryModel> list = new ArrayList<>();
                                for (LibraryModel singlemodel : bannerDataList) {
                                    if (singlemodel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(singlemodel);
                                    }
                                }
                                library_image_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                library_image_rv.setLayoutManager(new GridLayoutManager(context, 1));
                                bannerMaster_adapter = new BannerMaster_Adapter(context, list);
                                bannerMaster_adapter.notifyDataSetChanged();
                                library_image_rv.setAdapter(bannerMaster_adapter);
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<LibraryData> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public class BannerMaster_Adapter extends RecyclerView.Adapter<BannerMaster_Adapter.ViewHolder> {

        Context context;
        List<LibraryModel> bannerDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        int id;

        public BannerMaster_Adapter(Context context, List<LibraryModel> bannerDetails) {
            this.context = context;
            this.bannerDetails = bannerDetails;
        }

        @NotNull
        @Override
        public BannerMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BannerMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_image_detail_list, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull BannerMaster_Adapter.ViewHolder holder, int position) {
            /*attach = bannerDetails.get(position).getFilePath();
            imageVal = Base64.decode(attach, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(imageVal, 0, imageVal.length);*/
            holder.category.setText(bannerDetails.get(position).getCategoryInfo().getCategory());
            holder.title.setText(bannerDetails.get(position).getTitle());
            holder.description.setText(bannerDetails.get(position).getDescription());
            Glide.with(context).load("http://192.168.91.181/" + bannerDetails.get(position).getFilePath()).into(holder.banner_image);
            holder.banner_edit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image1 = dialogView.findViewById(R.id.image);
                TextView title1 = dialogView.findViewById(R.id.title);
                title1.setText("Are you sure that you want to edit image?");
                image1.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    final NestedScrollView scroll = ((Activity) context).findViewById(R.id.library_image_scroll);
                    save_image.setVisibility(View.GONE);
                    edit_image.setVisibility(View.VISIBLE);
                    image.setText("Attached");
                    image.setTextColor(context.getResources().getColor(R.color.black));
                    if (bannerDetails.get(position).getFilePath().contains(".") && bannerDetails.get(position).getFilePath().contains("/")) {
                        Extension = bannerDetails.get(position).getFilePath().substring(bannerDetails.get(position).getFilePath().lastIndexOf(".") + 1);
                        String FileNameWithExtension = bannerDetails.get(position).getFilePath().substring(bannerDetails.get(position).getFilePath().lastIndexOf("/") + 1);
                        String[] FileNameArray = FileNameWithExtension.split("\\.");
                        FileName = FileNameArray[0];
                    }
                    TransactionId = bannerDetails.get(position).getTransaction().getTransactionId();
                    FilePath = bannerDetails.get(position).getFilePath();
                    LibraryID = bannerDetails.get(position).getLibraryID();
                    LibraryDetailID = bannerDetails.get(position).getLibraryDetailID();
                    categoryid = bannerDetails.get(position).getCategoryInfo().getCategoryID();
                    selectSpinnerValue(category, bannerDetails.get(position).getCategoryInfo().getCategory());
                    title.setText(bannerDetails.get(position).getTitle());
                    description.setText(bannerDetails.get(position).getDescription());
                    imageView.setVisibility(View.VISIBLE);
                    Glide.with(context).load("http://192.168.91.181/" + bannerDetails.get(position).getFilePath()).into(imageView);
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
                title.setText("Are you sure that you want to delete this image?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

                btn_delete.setOnClickListener(v14 -> {
                    dialog.dismiss();
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.RemoveNewLibrary(bannerDetails.get(position).getLibraryID()
                            , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel model = response.body();
                                if (model.isCompleted()) {
                                    if (model.isData()) {
                                        Toast.makeText(context, "Image deleted successfully.", Toast.LENGTH_SHORT).show();
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
            TextView title, category, description;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                banner_image = itemView.findViewById(R.id.banner_image);
                banner_edit = itemView.findViewById(R.id.banner_edit);
                banner_delete = itemView.findViewById(R.id.banner_delete);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

                title = itemView.findViewById(R.id.title);
                category = itemView.findViewById(R.id.category);
                description = itemView.findViewById(R.id.description);
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
