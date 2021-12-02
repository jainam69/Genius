package com.example.genius.ui.Library_Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.genius.API.ApiCalling;
import com.example.genius.Model.CategoryData;
import com.example.genius.Model.CategoryModel;
import com.example.genius.Model.LibraryModel;
import com.example.genius.Model.LibrarySingleData;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.utils.ImageUtility;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
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
public class library_fragment extends Fragment {

    RadioGroup rg, rg1;
    RadioButton all, branch_1, rb_general, rb_standard, rb1, rb2;
    TextView attach_thumbnail, attach_document, master_id, lib_id, uniqid, libraryid, transactionid;
    SearchableSpinner standard, subject;
    EditText library_description, library_title;
    Button save_library, edit_library;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    LinearLayout linear_spinner;
    String Branch, Type, thunm_name, doc_name;
    int select, type;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>();
    String[] STANDARDITEM, SUBJECTITEM, CATEGORYITEM;
    Integer[] STANDARDID, SUBJECTID, CATEGORYID;
    String StandardName, SubjectName, attach = "", attach_doc = "", thumb_ext, doc_ext;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_PICK_GALLERY1 = 0x11;
    byte[] imageVal;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination, instrumentFileDestination1;
    Bundle bundle;
    OnBackPressedCallback callback;
    Long StandardId, SubjectId, categoryid;
    LibraryModel libraryModel;
    SearchableSpinner category;
    List<String> categoryitem = new ArrayList<>();
    List<Integer> categoryId = new ArrayList<>();
    String BranchID;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.library_fragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        rg = root.findViewById(R.id.rg);
        rg1 = root.findViewById(R.id.rg1);
        all = root.findViewById(R.id.all);
        branch_1 = root.findViewById(R.id.branch_1);
        rb_general = root.findViewById(R.id.rb_general);
        rb_standard = root.findViewById(R.id.rb_standard);
        attach_document = root.findViewById(R.id.attach_document);
        attach_thumbnail = root.findViewById(R.id.attach_thumbnail);
        standard = root.findViewById(R.id.standard);
        subject = root.findViewById(R.id.subject);
        library_description = root.findViewById(R.id.library_description);
        save_library = root.findViewById(R.id.save_library);
        edit_library = root.findViewById(R.id.edit_library);
        linear_spinner = root.findViewById(R.id.linear_spinner);
        master_id = root.findViewById(R.id.master_id);
        lib_id = root.findViewById(R.id.lib_id);
        uniqid = root.findViewById(R.id.uniqid);
        transactionid = root.findViewById(R.id.transactionid);
        libraryid = root.findViewById(R.id.libraryid);
        branch_1.setText(Preferences.getInstance(context).getString(Preferences.KEY_BRANCH_NAME));
        category = root.findViewById(R.id.category);
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        library_title = root.findViewById(R.id.library_title);

        bundle = getArguments();
        if (bundle != null) {
            save_library.setVisibility(View.GONE);
            edit_library.setVisibility(View.VISIBLE);

            if (!(bundle.getString("DocContentText") == null)) {
                attach_document.setText("Attached");
                attach_document.setTextColor(getResources().getColor(R.color.black));
                attach_doc = bundle.getString("DocContentText");
            }
            if (!(bundle.getString("ThumbImageContentText") == null)) {
                attach_thumbnail.setText("Attached");
                attach_thumbnail.setTextColor(getResources().getColor(R.color.black));
                attach = bundle.getString("ThumbImageContentText");
            }
            if (bundle.containsKey("StandardID") && bundle.containsKey("SubjectID")) {
                long std = bundle.getLong("StandardID");
                long sub = bundle.getLong("SubjectID");
                if (std == 0 && sub == 0) {
                    type = 1;
                    rb_general.setChecked(true);
                    rb_standard.setChecked(false);
                    linear_spinner.setVisibility(View.GONE);
                } else {
                    type = 2;
                    rb_general.setChecked(false);
                    rb_standard.setChecked(true);
                    linear_spinner.setVisibility(View.VISIBLE);
                }
            }
            if (bundle.containsKey("Description")) {
                library_description.setText(bundle.getString("Description"));
            }
            if (bundle.containsKey("LibraryID")) {
                libraryid.setText("" + bundle.getLong("LibraryID"));
            }
            if (bundle.containsKey("UniqueID")) {
                uniqid.setText("" + bundle.getLong("UniqueID"));
            }
            if (bundle.containsKey("TransactionId")) {
                transactionid.setText("" + bundle.getLong("TransactionId"));
            }
            if (bundle.containsKey("ThumbImageExt")) {
                thumb_ext = bundle.getString("ThumbImageExt");
            }
            if (bundle.containsKey("DocContentExt")) {
                doc_ext = bundle.getString("DocContentExt");
            }
            if (bundle.containsKey("ThumbImageName")) {
                thunm_name = bundle.getString("ThumbImageName");
            }
            if (bundle.containsKey("ThumbDocName")) {
                doc_name = bundle.getString("ThumbDocName");
            }
            if (bundle.containsKey("BranchId")) {
                long br = bundle.getLong("BranchId");
                if (br == 0) {
                    all.setChecked(true);
                    branch_1.setChecked(false);
                } else {
                    all.setChecked(false);
                    branch_1.setChecked(true);
                }
            }
        }

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            rb1 = root.findViewById(checkedId);
            Branch = rb1.getText().toString();
        });
        select = rg.getCheckedRadioButtonId();
        rb1 = root.findViewById(select);
        Branch = rb1.getText().toString();

        rg1.setOnCheckedChangeListener((group, checkedId) -> {
            rb2 = root.findViewById(checkedId);
            Type = rb2.getText().toString();
            if (Type.equals("Standard")) {
                linear_spinner.setVisibility(View.VISIBLE);
                type = 2;
            }
            if (Type.equals("General")) {
                linear_spinner.setVisibility(View.GONE);
                type = 1;
            }
        });
        select = rg1.getCheckedRadioButtonId();
        rb2 = root.findViewById(select);
        Type = rb2.getText().toString();

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllCategory();
            GetAllStandard();
            GetAllSubject();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        attach_thumbnail.setOnClickListener(v -> {
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

        attach_document.setOnClickListener(v -> {
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
                    pickImage1();
                }
            } else {
                pickImage1();
            }
        });

        save_library.setOnClickListener((View.OnClickListener) v -> {
            if (Function.checkNetworkConnection(context)) {
                progressBarHelper.showProgressDialog();
                Call<LibrarySingleData> call = apiCalling.OldLibraryMaintenance(0, 0, library_title.getText().toString()
                        , categoryid, StandardId, all.isChecked() ? 0 : Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                        , rb_general.isChecked() ? 1 : 2, 2
                        , library_description.getText().toString(), SubjectId, 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                        , 0, "", "", "", ""
                        , true, true, MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination))
                        , MultipartBody.Part.createFormData("", instrumentFileDestination1.getName()
                                , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination1)));
                /*call.enqueue(new Callback<LibrarySingleData>() {
                    @Override
                    public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                        if (response.isSuccessful()) {
                            if (response.body().isCompleted()) {
                                library_Listfragment contact = new library_Listfragment();
                                FragmentManager fragmentManager = getFragmentManager();
                                FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                                fragmentTransaction.replace(R.id.nav_host_fragment, contact);
                                fragmentTransaction.addToBackStack(null);
                                fragmentTransaction.commit();
                            }
                            Function.showToast(context, response.body().getMessage());
                        }
                        progressBarHelper.hideProgressDialog();
                    }

                    @Override
                    public void onFailure(@NotNull Call<LibrarySingleData> call, @NotNull Throwable t) {
                        Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        progressBarHelper.hideProgressDialog();
                    }
                });*/
                progressBarHelper.hideProgressDialog();
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        /*edit_library.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.checkNetworkConnection(context)) {
                    progressBarHelper.showProgressDialog();
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(transactionid.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    LibraryModel.LibraryDataEntity libraryDataEntity = new LibraryModel.LibraryDataEntity(Long.parseLong(uniqid.getText().toString()), Long.parseLong(libraryid.getText().toString()), attach, thumb_ext, thunm_name, attach_doc, doc_name, doc_ext);
                    if (Branch.equals("All") && Type.equals("Standard")) {
                        libraryModel = new LibraryModel(Long.parseLong(libraryid.getText().toString()), 0, thunm_name, doc_name,
                                2, StandardId, Long.parseLong(SubjectId), library_description.getText().toString(), rowStatusModel, transactionModel, libraryDataEntity);
                    } else if (Branch.equals("All") && Type.equals("General")) {
                        libraryModel = new LibraryModel(Long.parseLong(libraryid.getText().toString()), 0, thunm_name, doc_name,
                                1, 0, 0, library_description.getText().toString(), rowStatusModel, transactionModel, libraryDataEntity);
                    } else if (Branch.equals(Preferences.getInstance(context).getString(Preferences.KEY_BRANCH_NAME)) && Type.equals("Standard")) {
                        libraryModel = new LibraryModel(Long.parseLong(libraryid.getText().toString()), Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), thunm_name, doc_name,
                                2, StandardId, Long.parseLong(SubjectId), library_description.getText().toString(), rowStatusModel, transactionModel, libraryDataEntity);
                    } else if (Branch.equals(Preferences.getInstance(context).getString(Preferences.KEY_BRANCH_NAME)) && Type.equals("General")) {
                        libraryModel = new LibraryModel(Long.parseLong(libraryid.getText().toString()), Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), thunm_name, doc_name,
                                1, 0, 0, library_description.getText().toString(), rowStatusModel, transactionModel, libraryDataEntity);
                    }
                    Call<LibraryModel.LibraryData1> call = apiCalling.OldLibraryMaintenance(libraryModel);
                    call.enqueue(new Callback<LibraryModel.LibraryData1>() {
                        @Override
                        public void onResponse(Call<LibraryModel.LibraryData1> call, Response<LibraryModel.LibraryData1> response) {
                            if (response.isSuccessful()) {
                                LibraryModel.LibraryData1 data = response.body();
                                if (data.isCompleted()) {
                                    LibraryModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, "Books Updated Successfully...", Toast.LENGTH_SHORT).show();
                                        library_Listfragment contact = new library_Listfragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                                        fragmentTransaction.replace(R.id.nav_host_fragment, contact);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    } else {
                                        Toast.makeText(context, "Books not Updated...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(Call<LibraryModel.LibraryData1> call, Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });*/

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                library_Listfragment profileFragment = new library_Listfragment();
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

    public void GetAllSubject() {
        subjectitem.add("Select Subject");
        subjectid.add(0);
        Call<SubjectData> call = apiCalling.GetAllSubject(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SubjectData>() {
            @Override
            public void onResponse(Call<SubjectData> call, Response<SubjectData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    SubjectData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<SubjectModel> respose = standardData.getData();
                            if (respose.size() > 0) {
                                List<SubjectModel> list = new ArrayList<>();
                                for (SubjectModel singleResponseModel : respose) {

                                    String std = singleResponseModel.getSubject();
                                    subjectitem.add(std);

                                    int stdid = (int) singleResponseModel.getSubjectID();
                                    subjectid.add(stdid);
                                }
                                SUBJECTITEM = new String[subjectitem.size()];
                                SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

                                SUBJECTID = new Integer[subjectid.size()];
                                SUBJECTID = subjectid.toArray(SUBJECTID);

                                bindsubject();
                            }

                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<SubjectData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindsubject() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(adapter);
        if (bundle != null) {
            int bc = subjectid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("SubjectID"))));
            subject.setSelection(bc);
        }
        subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener8 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectName = subjectitem.get(position);
                    SubjectId = Long.parseLong(subjectid.get(position).toString());
                    if (subject.getSelectedItem().equals("Select Subject")) {
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

    public void GetAllStandard() {
        standarditem.add("Select Standard");
        standardid.add(0);

        Call<StandardData> call = apiCalling.GetAllStandard(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StandardData>() {
            @Override
            public void onResponse(Call<StandardData> call, Response<StandardData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    StandardData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<StandardModel> respose = standardData.getData();
                            for (StandardModel singleResponseModel : respose) {

                                String std = singleResponseModel.getStandard();
                                standarditem.add(std);

                                int stdid = (int) singleResponseModel.getStandardID();
                                standardid.add(stdid);
                            }
                            STANDARDITEM = new String[standarditem.size()];
                            STANDARDITEM = standarditem.toArray(STANDARDITEM);

                            STANDARDID = new Integer[standardid.size()];
                            STANDARDID = standardid.toArray(STANDARDID);

                            bindstandard();
                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<StandardData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindstandard() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STANDARDITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        standard.setAdapter(adapter);
        if (bundle != null) {
            int b = standardid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("StandardID"))));
            standard.setSelection(b);
        }
        standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StandardName = standarditem.get(position);
                    StandardId = Long.parseLong(standardid.get(position).toString());
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
                pickImage1();
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
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    attach_thumbnail.setText("Attached");
                    attach_thumbnail.setTextColor(context.getResources().getColor(R.color.black));
                    String name = instrumentFileDestination.getName();
                    thumb_ext = name.substring(name.lastIndexOf("."));
                    thunm_name = instrumentFileDestination.getName();
                    attach = onGalleryImageResultInstrument(result);
                } catch (Exception e) {
                    errored();
                }
            } else {
                errored();
            }
        } else if (requestCode == REQUEST_CODE_PICK_GALLERY1) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else if (resultCode == RESULT_OK) {
                Uri image = result.getData();
                try {
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination1 = new File(Path);
                    attach_document.setText("Attached");
                    attach_document.setTextColor(context.getResources().getColor(R.color.black));
                    String nm = instrumentFileDestination1.getName();
                    doc_ext = nm.substring(nm.lastIndexOf("."));
                    doc_name = instrumentFileDestination1.getName();
                    attach_doc = encodeFileToBase64Binary(instrumentFileDestination1);
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

    private void pickImage1() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, REQUEST_CODE_PICK_GALLERY1);
        } catch (ActivityNotFoundException ignored) {
        }
    }

    private String encodeFileToBase64Binary(File yourFile) {
        int size = (int) yourFile.length();
        byte[] bytes = new byte[size];
        String encode = "";
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(yourFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        encode = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encode;
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
                encodedImage = Base64.encodeToString(imageVal, android.util.Base64.DEFAULT);
            }
        }
        return encodedImage;
    }

    public void userCancelled() {

    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
    }

    public void GetAllCategory() {
        categoryitem.add("Select Category");
        categoryId.add(0);

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
                                categoryitem.add(building_name);

                                int building_id = Integer.parseInt(String.valueOf(singleResponseModel.getCategoryID()));
                                categoryId.add(building_id);
                            }
                            CATEGORYITEM = new String[categoryitem.size()];
                            CATEGORYITEM = categoryitem.toArray(CATEGORYITEM);

                            CATEGORYID = new Integer[categoryId.size()];
                            CATEGORYID = categoryId.toArray(CATEGORYID);
                            bindcategory();
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

    public void bindcategory() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, CATEGORYITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(onItemSelectedListener6);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    categoryid = Long.parseLong(categoryId.get(position).toString());
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

}