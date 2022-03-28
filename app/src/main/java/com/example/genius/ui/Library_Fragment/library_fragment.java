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
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchSubjectModel;
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
import com.example.genius.helper.FileUtils;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.MultiSelectionSpinner;
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
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

@SuppressLint("SetTextI18n")
public class library_fragment extends Fragment implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    RadioGroup rg, rg1;
    RadioButton all, branch_1, rb_general, rb_standard, rb1, rb2;
    TextView attach_thumbnail, attach_document, master_id, lib_id, uniqid, libraryid, transactionid;
    SearchableSpinner subject,course_name;
    MultiSelectionSpinner standard;
    EditText library_description, library_title;
    Button save_library, edit_library;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    LinearLayout linear_spinner;
    String Branch, Type, thunm_name, doc_name,attach = "", attach_doc = "", thumb_ext, doc_ext,BranchID,StandardIDs = "none";
    int select, type;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(),courseitem = new ArrayList<>(),categoryitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(),courseid = new ArrayList<>(),categoryId = new ArrayList<>();
    String[] SUBJECTITEM, CATEGORYITEM, STANDARDITEM,COURSEITEM;
    Integer[] STANDARDID, SUBJECTID, CATEGORYID,COURSEID;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_PICK_GALLERY1 = 0x11;
    byte[] imageVal;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination, instrumentFileDestination1;
    Bundle bundle;
    OnBackPressedCallback callback;
    Long StandardId = 0L, SubjectId = 0L, categoryid = 0L,courseID = 0L;
    SearchableSpinner category;
    LibraryModel libraryModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Library Image/Document");
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
        course_name = root.findViewById(R.id.course_name);

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

        bundle = getArguments();
        if (bundle != null) {
            save_library.setVisibility(View.GONE);
            edit_library.setVisibility(View.VISIBLE);

            libraryModel = (LibraryModel) bundle.getSerializable("LIBRARY_MST");
            library_title.setText(libraryModel.getLibraryTitle());
            if (libraryModel.getType() == 1)
                rb_general.setChecked(true);
            else
                rb_standard.setChecked(true);
            if (libraryModel.getBranchID() == 0)
                all.setChecked(true);
            else
                branch_1.setChecked(true);
            library_description.setText(libraryModel.getDescription());
            attach_thumbnail.setText("Attached");
            attach_document.setText("Attached");
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllCategory();
            GetAllCourse();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();
        selectSubject();

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

        save_library.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (validation()) {
                if (Function.isNetworkAvailable(context)) {
                    Call<LibrarySingleData> call = apiCalling.OldLibraryMaintenance(0, 0, encodeDecode(library_title.getText().toString())
                            , categoryid, courseID,StandardIDs, all.isChecked() ? 0 : Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                            , Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), rb_general.isChecked() ? 1 : 2, 2
                            , encodeDecode(library_description.getText().toString()), SubjectId, 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                            , 0, "none", "none,none", "none", "none,none", "none"
                            , true, true, MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination))
                            , MultipartBody.Part.createFormData("", instrumentFileDestination1.getName()
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination1)));
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().isCompleted()) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    library_Listfragment contact = new library_Listfragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                                    fragmentTransaction.replace(R.id.nav_host_fragment, contact);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }else {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<LibrarySingleData> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_library.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (editValidation()) {
                if (Function.isNetworkAvailable(context)) {
                    boolean isThumbnail = false, isDocument = false;
                    MultipartBody.Part multipartThumbnailBody = MultipartBody.Part.createFormData("attachment", ""
                            , RequestBody.create(MediaType.parse("multipart/form-data"), ""));
                    MultipartBody.Part multipartDocBody = MultipartBody.Part.createFormData("attachment", ""
                            , RequestBody.create(MediaType.parse("multipart/form-data"), ""));
                    String ThumbnailFileName = "none,none", ThumbnailFileExtension = "none", DocumentFileName = "none,none", DocumentFileExtension = "none";
                    if (libraryModel.getThumbnailFilePath().contains(".") && libraryModel.getThumbnailFilePath().contains("/")) {
                        ThumbnailFileExtension = libraryModel.getThumbnailFilePath().substring(libraryModel.getThumbnailFilePath().lastIndexOf(".") + 1);
                        String FileNameWithExtension = libraryModel.getThumbnailFilePath().substring(libraryModel.getThumbnailFilePath().lastIndexOf("/") + 1);
                        String[] FileNameArray = FileNameWithExtension.split("\\.");
                        ThumbnailFileName = libraryModel.getThumbnailFileName() + "," + FileNameArray[0];
                    }
                    if (libraryModel.getDocFilePath().contains(".") && libraryModel.getDocFilePath().contains("/")) {
                        DocumentFileExtension = libraryModel.getDocFilePath().substring(libraryModel.getDocFilePath().lastIndexOf(".") + 1);
                        String FileNameWithExtension = libraryModel.getDocFilePath().substring(libraryModel.getDocFilePath().lastIndexOf("/") + 1);
                        String[] FileNameArray = FileNameWithExtension.split("\\.");
                        DocumentFileName = libraryModel.getDocFileName() + "," + FileNameArray[0];
                    }
                    if (instrumentFileDestination != null) {
                        isThumbnail = true;
                        multipartThumbnailBody = MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination));
                    }
                    if (instrumentFileDestination1 != null) {
                        isDocument = true;
                        multipartDocBody = MultipartBody.Part.createFormData("", instrumentFileDestination1.getName()
                                , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination1));
                    }
                    Call<LibrarySingleData> call = apiCalling.OldLibraryMaintenance(libraryModel.getLibraryID(), 0, encodeDecode(library_title.getText().toString().trim())
                            , categoryid,courseID, StandardIDs, all.isChecked() ? 0 : Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                            , Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), rb_general.isChecked() ? 1 : 2, 2
                            , !library_description.getText().toString().trim().equals("") ? encodeDecode(library_description.getText().toString().trim()) : "-", SubjectId, 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                            , 0, "none", ThumbnailFileName, ThumbnailFileExtension, DocumentFileName, DocumentFileExtension
                            , isThumbnail, isDocument, multipartThumbnailBody
                            , multipartDocBody);
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                if (response.body() != null && response.body().isCompleted()) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    library_Listfragment contact = new library_Listfragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.nav_host_fragment, contact);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }else {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<LibrarySingleData> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                library_Listfragment profileFragment = new library_Listfragment();
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

    public void GetAllCourse()
    {
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
                                String course = model.getCourse().getCourseName();
                                courseitem.add(course);
                                int id = (int) model.getCourse_dtl_id();
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
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindcourse() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, COURSEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course_name.setAdapter(adapter);
        if (bundle != null && libraryModel.getList().size() > 0) {
            selectSpinnerValue(course_name, libraryModel.getList().get(0).getBranchCourse().getCourse().getCourseName());
        }
        course_name.setOnItemSelectedListener(selectcourse);
    }

    AdapterView.OnItemSelectedListener selectcourse =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    courseID = Long.parseLong(courseid.get(position).toString());
                    if (course_name.getSelectedItem().equals("Select Course")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (course_name.getSelectedItemId() != 0){
                        GetAllStandard(courseID);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetAllStandard(long coursedetailid) {
        progressBarHelper.showProgressDialog();
        standarditem.clear();
        standardid.clear();
        standarditem.add("Select Standard");
        standardid.add(0);

        Call<BranchClassModel> call = apiCalling.Get_Class_Spinner(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),coursedetailid);
        call.enqueue(new Callback<BranchClassModel>() {
            @Override
            public void onResponse(Call<BranchClassModel> call, Response<BranchClassModel> response) {
                if (response.isSuccessful()) {
                    BranchClassModel data = response.body();
                    if (data.getCompleted()) {
                        List<BranchClassSingleModel.BranchClassData> list = data.getData();
                        if (list != null && list.size() > 0){
                            for (BranchClassSingleModel.BranchClassData model : list) {
                                String std = model.getClassModel().getClassName();
                                standarditem.add(std);
                                int stdid = (int) model.getClass_dtl_id();
                                standardid.add(stdid);
                            }
                            STANDARDITEM = new String[standarditem.size()];
                            STANDARDITEM = standarditem.toArray(STANDARDITEM);

                            STANDARDID = new Integer[standardid.size()];
                            STANDARDID = standardid.toArray(STANDARDID);

                            bindstandard();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<BranchClassModel> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindstandard() {
        standard.setItems(STANDARDITEM);
        standard.setListener(this);
        standard.hasNoneOption(true);
        standard.setSelection(new int[]{0});
        if (bundle != null && libraryModel.getList().size() > 0) {
            List<String> list = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < libraryModel.getList().size(); i++) {
                list.add(libraryModel.getList().get(i).getStandard());
                sb.append(libraryModel.getList().get(i).getStd_id());
                sb.append(",");
            }
            StandardIDs = sb.toString().substring(0, sb.length() - 1);
            standard.setSelection(list);
            GetAllSubject(courseID,StandardIDs);
        }
        standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
    public void selectedIndices(List<Integer> indices) {
        StringBuilder sb = new StringBuilder();
        if (indices.size() > 0) {
            for (int i = 0; i < indices.size(); i++) {
                sb.append(standardid.get(indices.get(i)));
                sb.append(",");
            }
            StandardIDs = sb.toString().substring(0, sb.length() - 1);
        } else {
            StandardIDs = "";
            standard.setSelection(new int[]{0});
        }
        standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    @Override
    public void selectedStrings(List<String> strings) {
        if (!StandardIDs.isEmpty()){
            GetAllSubject(courseID,StandardIDs);
        }
    }

    public void GetAllSubject(long coursedetailid,String classdetailid) {
        progressBarHelper.showProgressDialog();
        subjectitem.clear();
        subjectid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);

        Call<BranchSubjectModel> call = apiCalling.Get_Library_Subject_DDL(coursedetailid,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),classdetailid);
        call.enqueue(new Callback<BranchSubjectModel>() {
            @Override
            public void onResponse(Call<BranchSubjectModel> call, Response<BranchSubjectModel> response) {
                if (response.isSuccessful()){
                    BranchSubjectModel data = response.body();
                    if (data.isCompleted()){
                        List<BranchSubjectModel.BranchSubjectData> list = data.getData();
                        if (list != null && list.size() > 0){
                            for (BranchSubjectModel.BranchSubjectData model : list){
                               String name = model.getSubject().getSubjectName();
                               subjectitem.add(name);
                               int id = (int) model.getSubject_dtl_id();
                               subjectid.add(id);
                            }
                            SUBJECTITEM = new String[subjectitem.size()];
                            SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

                            SUBJECTID = new Integer[subjectid.size()];
                            SUBJECTID = subjectid.toArray(SUBJECTID);

                            bindsubject();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<BranchSubjectModel> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindsubject() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(adapter);
        if (bundle != null && libraryModel.getList().size() > 0) {
            selectSpinnerValue(subject, libraryModel.getList().get(0).getSubject());
        }
        subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener8 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
                try {
                    Uri uri = result.getData();
                    String Path = FileUtils.getReadablePathFromUri(requireActivity(),uri);
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

        Call<CategoryData> call = apiCalling.GetAllCategory(0);
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
        if (bundle != null) {
            selectSpinnerValue(category, libraryModel.getCategoryInfo().getCategory());
        }
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

    public boolean validation() {
        if (library_title.getText().toString().trim().equals("")) {
            Function.showToast(context, "Please enter library title");
            progressBarHelper.hideProgressDialog();
            return false;
        } else if (category.getSelectedItemId() == 0) {
            Function.showToast(context, "Please select category");
            progressBarHelper.hideProgressDialog();
            return false;
        } else if (instrumentFileDestination == null) {
            Function.showToast(context, "Please select thumbnail");
            progressBarHelper.hideProgressDialog();
            return false;
        } else if (instrumentFileDestination1 == null) {
            Function.showToast(context, "Please select document");
            progressBarHelper.hideProgressDialog();
            return false;
        } else if (rb_standard.isChecked()) {
            if (course_name.getSelectedItemId() == 0){
                Function.showToast(context,"Please select Course.");
                progressBarHelper.hideProgressDialog();
                return false;
            }else if (StandardIDs.equals("")) {
                Function.showToast(context, "Please select standard");
                progressBarHelper.hideProgressDialog();
                return false;
            } else if (subject.getSelectedItemId() == 0) {
                Function.showToast(context, "Please select subject");
                progressBarHelper.hideProgressDialog();
                return false;
            }
        }
        return true;
    }

    public boolean editValidation() {
        if (library_title.getText().toString().trim().equals("")) {
            Function.showToast(context, "Please enter library title");
            progressBarHelper.hideProgressDialog();
            return false;
        } else if (category.getSelectedItemId() == 0) {
            Function.showToast(context, "Please select category");
            progressBarHelper.hideProgressDialog();
            return false;
        } else if (rb_standard.isChecked()) {
            if (course_name.getSelectedItemId() == 0){
                Function.showToast(context,"Please select Course.");
                progressBarHelper.hideProgressDialog();
                return false;
            }else if (StandardIDs.equals("")) {
                Function.showToast(context, "Please select standard");
                progressBarHelper.hideProgressDialog();
                return false;
            } else if (subject.getSelectedItemId() == 0) {
                Function.showToast(context, "Please select subject");
                progressBarHelper.hideProgressDialog();
                return false;
            }
        }
        return true;
    }

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public String encodeDecode(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replace("\n", "");
    }

    public void selectStandard() {
        standarditem.clear();
        standardid.clear();
        standarditem.add("Select Standard");
        standardid.add(0);

        STANDARDITEM = new String[standarditem.size()];
        STANDARDITEM = standarditem.toArray(STANDARDITEM);

        bindstd();
    }

    public void bindstd() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STANDARDITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        standard.setItems(STANDARDITEM);
        standard.setListener(this);
        standard.hasNoneOption(true);
        standard.setSelection(new int[]{0});
        standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    public void selectSubject() {
        subjectitem.clear();
        subjectid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);

        SUBJECTITEM = new String[subjectitem.size()];
        SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

        bindsub();
    }

    public void bindsub() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(adapter);
        subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

}