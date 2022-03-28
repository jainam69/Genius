package com.example.genius.ui.Library_Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
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
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.MultiSelectionSpinner;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class library_video_fragment extends Fragment implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    RadioGroup rg, rg1;
    RadioButton all, branch_1, rb_general, rb_standard, rb1, rb2;
    TextView master_id, lib_id, uniqid, libraryid, transactionid;
    SearchableSpinner subject,course_name,category;
    MultiSelectionSpinner standard;
    EditText library_description, library_title, library_video_link;
    Button save_library, edit_library;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    LinearLayout linear_spinner;
    String Branch, Type,BranchID,StandardIDs = "none";
    int select, type;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(),courseitem = new ArrayList<>(),categoryitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(),courseid = new ArrayList<>(),categoryId = new ArrayList<>();
    String[] SUBJECTITEM, CATEGORYITEM, STANDARDITEM,COURSEITEM;
    Integer[] STANDARDID, SUBJECTID, CATEGORYID,COURSEID;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    Bundle bundle;
    OnBackPressedCallback callback;
    Long StandardId = 0L, SubjectId = 0L, categoryid = 0L,courseID = 0L;
    LibraryModel libraryModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Library Video");
        View root = inflater.inflate(R.layout.library_video_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        rg = root.findViewById(R.id.rg);
        rg1 = root.findViewById(R.id.rg1);
        all = root.findViewById(R.id.all);
        branch_1 = root.findViewById(R.id.branch_1);
        rb_general = root.findViewById(R.id.rb_general);
        rb_standard = root.findViewById(R.id.rb_standard);
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
        library_video_link = root.findViewById(R.id.library_video_link);
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
            library_video_link.setText(libraryModel.getVideoLink());
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

        save_library.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (validation()) {
                if (Function.isNetworkAvailable(context)) {
                    Call<LibrarySingleData> call = apiCalling.OldLibraryMaintenance(0, 0, encodeDecode(library_title.getText().toString())
                            , categoryid, courseID,StandardIDs, all.isChecked() ? 0 : Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                            , Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), rb_general.isChecked() ? 1 : 2, 1
                            , encodeDecode(library_description.getText().toString()), SubjectId, 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                            , 0, encodeDecode(library_video_link.getText().toString()), "none,none", "none", "none,none", "none"
                            , false, false, MultipartBody.Part.createFormData("attachment", ""
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), ""))
                            , MultipartBody.Part.createFormData("attachment", ""
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                if (response.body().isCompleted()) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    library_videolist_fragment contact = new library_videolist_fragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = (fragmentManager).beginTransaction();
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
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_library.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (validation()) {
                if (Function.isNetworkAvailable(context)) {
                    Call<LibrarySingleData> call = apiCalling.OldLibraryMaintenance(libraryModel.getLibraryID(), 0, encodeDecode(library_title.getText().toString())
                            , categoryid, courseID,StandardIDs, all.isChecked() ? 0 : Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                            , Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), rb_general.isChecked() ? 1 : 2, 1
                            , encodeDecode(library_description.getText().toString()), SubjectId, 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                            , 0, encodeDecode(library_video_link.getText().toString()), "none,none", "none", "none,none", "none"
                            , false, false, MultipartBody.Part.createFormData("attachment", ""
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), ""))
                            , MultipartBody.Part.createFormData("attachment", ""
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                if (response.body().isCompleted()) {
                                    Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                                    library_videolist_fragment contact = new library_videolist_fragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = (fragmentManager).beginTransaction();
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
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                library_videolist_fragment profileFragment = new library_videolist_fragment();
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
        } else if (library_video_link.getText().toString().equals("")) {
            Function.showToast(context, "Please enter video link");
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