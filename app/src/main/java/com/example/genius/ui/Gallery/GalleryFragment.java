package com.example.genius.ui.Gallery;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.GalleryData;
import com.example.genius.Model.GalleryModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.utils.ImageUtility;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class GalleryFragment extends Fragment {

    TextView attachment_gallery, bid, photo, text, transactionid;
    ImageView imageView;
    EditText gallery_description;
    Button save_gallery, edit_gallery;
    RecyclerView gallery_rv;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String Ans, pictureFilePath, attach = "";
    int flag = 0;
    Boolean a, selectfile = false;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    OnBackPressedCallback callback;
    NestedScrollView gallery_scroll;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    byte[] imageVal;
    Bitmap bitmap;
    GalleryMaster_Adapter galleryMaster_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Gallery");
        View root = inflater.inflate(R.layout.fragment_gallery, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        attachment_gallery = root.findViewById(R.id.attachment_gallery);
        gallery_description = root.findViewById(R.id.gallery_description);
        save_gallery = root.findViewById(R.id.save_gallery);
        gallery_rv = root.findViewById(R.id.gallery_rv);
        imageView = root.findViewById(R.id.imageView);
        edit_gallery = root.findViewById(R.id.edit_gallery);
        bid = root.findViewById(R.id.bid);
        photo = root.findViewById(R.id.photo);
        text = root.findViewById(R.id.text);
        transactionid = root.findViewById(R.id.transactionid);
        gallery_scroll = root.findViewById(R.id.gallery_scroll);

        attachment_gallery.setOnClickListener(v -> {
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
                    showAddProfilePicDialog();
                }
            } else {
                showAddProfilePicDialog();
            }
        });

        save_gallery.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (Function.checkNetworkConnection(context)) {
                if (attachment_gallery.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Upload Image.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    GalleryModel gl = new GalleryModel(branchModel, attach, gallery_description.getText().toString(), rowStatusModel, transactionModel);
                    Call<GalleryModel.GallaryData1> call = apiCalling.GalaryImageMaintenance(gl);
                    call.enqueue(new Callback<GalleryModel.GallaryData1>() {
                        @Override
                        public void onResponse(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Response<GalleryModel.GallaryData1> response) {
                            if (response.isSuccessful()) {
                                GalleryModel.GallaryData1 data = response.body();
                                if (data != null && data.isCompleted()) {
                                    GalleryModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, "Image Inserted Successfully...", Toast.LENGTH_SHORT).show();
                                        gallery_description.setText("");
                                        attachment_gallery.setText("");
                                        imageView.setVisibility(View.GONE);
                                        GetGalleryDetails();
                                    } else {
                                        progressBarHelper.hideProgressDialog();
                                        Toast.makeText(context, "Image not Inserted...!", Toast.LENGTH_SHORT).show();
                                    }
                                } else
                                    progressBarHelper.hideProgressDialog();
                            } else
                                progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_gallery.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (Function.checkNetworkConnection(context)) {
                if (attachment_gallery.getText().toString().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Upload Image.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(transactionid.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    GalleryModel gl = new GalleryModel(Long.parseLong(bid.getText().toString()), branchModel, attach, gallery_description.getText().toString(), rowStatusModel, transactionModel);
                    Call<GalleryModel.GallaryData1> call = apiCalling.GalaryImageMaintenance(gl);
                    call.enqueue(new Callback<GalleryModel.GallaryData1>() {
                        @Override
                        public void onResponse(@NotNull Call<GalleryModel.GallaryData1> call, @NotNull Response<GalleryModel.GallaryData1> response) {
                            if (response.isSuccessful()) {
                                GalleryModel.GallaryData1 data = response.body();
                                if (data != null && data.isCompleted()) {
                                    GalleryModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, "Image Updated Successfully...", Toast.LENGTH_SHORT).show();
                                        gallery_description.setText("");
                                        attachment_gallery.setText("");
                                        imageView.setVisibility(View.GONE);
                                        save_gallery.setVisibility(View.VISIBLE);
                                        edit_gallery.setVisibility(View.GONE);
                                        GetGalleryDetails();
                                    } else {
                                        Toast.makeText(context, "Image not Updated...!", Toast.LENGTH_SHORT).show();
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
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetGalleryDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

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


    private void showAddProfilePicDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        LayoutInflater layoutInflater = (LayoutInflater) requireActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.dialog_selection, null);
        LinearLayout camera = dialogView.findViewById(R.id.camera);
        LinearLayout gallery = dialogView.findViewById(R.id.gallery);
        camera.setOnClickListener(v -> {
            dialog.dismiss();
            takePic();
        });
        gallery.setOnClickListener(v -> {
            dialog.dismiss();
            pickImage();
        });
        dialog.setView(dialogView);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showAddProfilePicDialog();
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

    @SuppressLint("QueryPermissionsNeeded")
    private void takePic() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
//        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        File pictureFile;
        try {
            pictureFile = getPictureFile();
        } catch (IOException ex) {

            return;
        }
        Uri photoURI = FileProvider.getUriForFile(context, "com.example.genius.provider", pictureFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == REQUEST_CODE_TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                try {
                    flag = 1;
                    selectfile = true;
                    imageVal = null;
                    imageView.setVisibility(View.VISIBLE);
                   /* Bitmap image = (Bitmap) Objects.requireNonNull(result.getExtras()).get("data");
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(image);
                    imageVal = ImageUtility.using(getActivity()).toBase64FromBitmap(image);
                   */
                    attachment_gallery.setText("Attached");
                    attachment_gallery.setTextColor(context.getResources().getColor(R.color.black));
                    instrumentFileDestination = new File(pictureFilePath);
                    imageVal = ImageUtility.using(context).toBase64(instrumentFileDestination.getPath());
                    attach = Base64.encodeToString(imageVal, Base64.DEFAULT);
                    try {
                        imageView.setImageURI(Uri.fromFile(instrumentFileDestination));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    onCameraImageResultInstrument();
                } catch (Exception ex) {
                    errored();
                }
            } else if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else {
                errored();
            }

        } else if (requestCode == REQUEST_CODE_PICK_GALLERY) {
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
                    if (Path != null) {
                        instrumentFileDestination = new File(Path);
                        InputStream imageStream;
                        imageStream = requireActivity().getContentResolver().openInputStream(image);
                        bitmap = BitmapFactory.decodeStream(imageStream);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageBitmap(bitmap);
                        attachment_gallery.setText("Attached");
                        attachment_gallery.setTextColor(context.getResources().getColor(R.color.black));
                        attach = onGalleryImageResultInstrument(result);
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

    private void onCameraImageResultInstrument() {
        Bitmap rotatedBitmap;
        Bitmap bitmap = null;

        //Uri uri = data.getData();
        Uri uri = FUtils.getUri(instrumentFileDestination);
        /*        String Path = FUtils.getPath(requireContext(), uri);
                if (Path != null) {
                    instrumentFileDestination = new File(Path);
                }*/
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
            }
        }
    }

    private String encodeImage(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "ZOFTINO_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    public void GetGalleryDetails() {
        Call<GalleryData> call = apiCalling.GetAllGalleryImagesBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
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
                                text.setVisibility(View.VISIBLE);
                                gallery_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                gallery_rv.setLayoutManager(new GridLayoutManager(context, 2));
                                galleryMaster_adapter = new GalleryMaster_Adapter(context, list);
                                galleryMaster_adapter.notifyDataSetChanged();
                                gallery_rv.setAdapter(galleryMaster_adapter);
                            }
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(@NotNull Call<GalleryData> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public class GalleryMaster_Adapter extends RecyclerView.Adapter<GalleryMaster_Adapter.ViewHolder> {

        Context context;
        List<GalleryModel> galleryDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;

        public GalleryMaster_Adapter(Context context, List<GalleryModel> galleryDetails) {
            this.context = context;
            this.galleryDetails = galleryDetails;
        }

        @NotNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.gallery_master_deatil_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            holder.description.setText(galleryDetails.get(position).getRemarks());
            attach = galleryDetails.get(position).getFileEncoded();
            /*imageVal = Base64.decode(attach, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(imageVal, 0, imageVal.length);*/
            Glide.with(context).load(galleryDetails.get(position).getFilePath()).into(holder.gallery_image);
            holder.gallery_edit.setOnClickListener(v -> {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Image?");
                image.setImageResource(R.drawable.ic_edit);
                androidx.appcompat.app.AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v13 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v14 -> {
                    dialog.dismiss();
                    save_gallery.setVisibility(View.GONE);
                    edit_gallery.setVisibility(View.VISIBLE);
                    attachment_gallery.setText("Attached");
                    attach = galleryDetails.get(position).getFileEncoded();
                    attachment_gallery.setTextColor(context.getResources().getColor(R.color.black));
                    gallery_description.setText(galleryDetails.get(position).getRemarks());
                    bid.setText("" + galleryDetails.get(position).getUniqueID());
                    transactionid.setText("" + galleryDetails.get(position).getTransaction().getTransactionId());
                    gallery_scroll.fullScroll(View.FOCUS_UP);
                    gallery_scroll.scrollTo(0, 0);
                });
                dialog.show();
            });
            holder.gallery_delete.setOnClickListener(v -> {
                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this Image?");
                androidx.appcompat.app.AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(v1 -> dialog.dismiss());

                btn_delete.setOnClickListener(v12 -> {
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
                                            Toast.makeText(context, "Image Deleted Successfully...", Toast.LENGTH_SHORT).show();
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
        }

        @Override
        public int getItemCount() {
            return galleryDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView description;
            ImageView gallery_image, gallery_edit, gallery_delete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                description = itemView.findViewById(R.id.description);
                gallery_image = itemView.findViewById(R.id.gallery_image);
                gallery_edit = itemView.findViewById(R.id.gallery_edit);
                gallery_delete = itemView.findViewById(R.id.gallery_delete);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }

}