package com.example.genius.ui.Faculty_Fragment;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.Preference;
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
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchSubjectModel;
import com.example.genius.Model.FacultyModel;
import com.example.genius.Model.UserData1;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.example.genius.ui.Student_Registration_Fragment.student_registration_Listfragment;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class faculty_fragment extends Fragment {

    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    Bundle bundle;
    SearchableSpinner sp_faculty,sp_course,sp_class,sp_subject;
    TextView attachment;
    EditText faculty_description;
    Button save_faculty,edit_faculty;
    List<String> facultyitem = new ArrayList<>(), courseitem = new ArrayList<>(), classitem = new ArrayList<>(), subjectitem = new ArrayList<>();
    String[] FACULTYITEM,COURSEITEM,CLASSITEM,SUBJECTITEM;
    List<Integer> facultyid = new ArrayList<>(), courseid = new ArrayList<>(), classid = new ArrayList<>(), subjectid = new ArrayList<>();
    Integer[] FACULTYID,COURSEID,CLASSID,SUBJECTID;
    long faculty_id, course_id, class_id, subject_id;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    File instrumentFileDestination;
    ImageView view_image;
    Bitmap bitmap;
    FacultyModel facultyModel;
    String FinalFileName, OriginFileName, RandomFileName, Extension;
    long uniq_id = 0, transaction_id;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Faculty Master");
        View root = inflater.inflate(R.layout.fragment_faculty_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        sp_faculty = root.findViewById(R.id.sp_faculty);
        sp_course = root.findViewById(R.id.sp_course);
        sp_class = root.findViewById(R.id.sp_class);
        sp_subject = root.findViewById(R.id.sp_subject);
        attachment = root.findViewById(R.id.attachment);
        faculty_description = root.findViewById(R.id.faculty_description);
        save_faculty = root.findViewById(R.id.save_faculty);
        edit_faculty = root.findViewById(R.id.edit_faculty);
        view_image = root.findViewById(R.id.view_image);

        bundle = getArguments();
        if (bundle != null)
        {
            save_faculty.setVisibility(View.GONE);
            edit_faculty.setVisibility(View.VISIBLE);
            facultyModel = (FacultyModel) bundle.getSerializable("FACULTY_LIST");
            attachment.setText("Attached");
            view_image.setVisibility(View.VISIBLE);
            Glide.with(context).load(facultyModel.getFilePath()).into(view_image);
            faculty_description.setText(facultyModel.getDescripation());
            if (facultyModel.getFilePath().contains(".") && facultyModel.getFilePath().contains("/")) {
                Extension = facultyModel.getFilePath().substring(facultyModel.getFilePath().lastIndexOf(".") + 1);
                String FileNameWithExtension = facultyModel.getFilePath().substring(facultyModel.getFilePath().lastIndexOf("/") + 1);
                String[] FileNameArray = FileNameWithExtension.split("\\.");
                RandomFileName = FileNameArray[0];
            }
            OriginFileName = facultyModel.getFacultyContentFileName();
            uniq_id = facultyModel.getFacultyID();
            transaction_id = facultyModel.getTransaction().getTransactionId();
        }

        if (Function.isNetworkAvailable(context)){
            progressBarHelper.showProgressDialog();
            GetFacultyList();
            GetCourseList();
        }else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectClass();
        selectSubject();

        attachment.setOnClickListener(new View.OnClickListener() {
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

        save_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (sp_faculty.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select faculty.", Toast.LENGTH_SHORT).show();
                    }else if (sp_course.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select course.", Toast.LENGTH_SHORT).show();
                    }else if (sp_class.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select class name.", Toast.LENGTH_SHORT).show();
                    }else if (sp_subject.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select subject.", Toast.LENGTH_SHORT).show();
                    }else if (attachment.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please upload faculty image.", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBarHelper.showProgressDialog();
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        Call<FacultyModel.FacultyModelData> call = apiCalling.Faculty_Maintanance(uniq_id,faculty_id,subject_id,course_id,class_id,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),
                                faculty_description.getText().toString().isEmpty() ? "none" : encodeDecode(faculty_description.getText().toString()),Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0,
                                "0","0",true,uploadfile);
                        call.enqueue(new Callback<FacultyModel.FacultyModelData>() {
                            @Override
                            public void onResponse(Call<FacultyModel.FacultyModelData> call, Response<FacultyModel.FacultyModelData> response) {
                                if (response.isSuccessful()){
                                    FacultyModel.FacultyModelData data = response.body();
                                    if (data.isCompleted()){
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        faculty_listfragment profileFragment = new faculty_listfragment();
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.nav_host_fragment, profileFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }

                            @Override
                            public void onFailure(Call<FacultyModel.FacultyModelData> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_faculty.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (sp_faculty.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select faculty.", Toast.LENGTH_SHORT).show();
                    }else if (sp_course.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select course.", Toast.LENGTH_SHORT).show();
                    }else if (sp_class.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select class name.", Toast.LENGTH_SHORT).show();
                    }else if (sp_subject.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select subject.", Toast.LENGTH_SHORT).show();
                    }else if (attachment.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please upload faculty image.", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBarHelper.showProgressDialog();
                        Call<FacultyModel.FacultyModelData> call;
                        FinalFileName = OriginFileName + "," + RandomFileName;
                        if (instrumentFileDestination != null){
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                            MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                            call = apiCalling.Faculty_Maintanance(uniq_id,faculty_id,subject_id,course_id,class_id,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),
                                    faculty_description.getText().toString().isEmpty() ? "none" : encodeDecode(faculty_description.getText().toString()),Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),transaction_id,
                                    "0","0",true,uploadfile);
                        }else {
                            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                            MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
                            call = apiCalling.Faculty_Maintanance(uniq_id,faculty_id,subject_id,course_id,class_id,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),
                                    faculty_description.getText().toString().isEmpty() ? "none" : encodeDecode(faculty_description.getText().toString()),Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),transaction_id,
                                    FinalFileName,Extension,false,uploadfile);
                        }
                        call.enqueue(new Callback<FacultyModel.FacultyModelData>() {
                            @Override
                            public void onResponse(Call<FacultyModel.FacultyModelData> call, Response<FacultyModel.FacultyModelData> response) {
                                if (response.isSuccessful()){
                                    FacultyModel.FacultyModelData data = response.body();
                                    if (data.isCompleted()){
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        faculty_listfragment profileFragment = new faculty_listfragment();
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.nav_host_fragment, profileFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }

                            @Override
                            public void onFailure(Call<FacultyModel.FacultyModelData> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                faculty_listfragment profileFragment = new faculty_listfragment();
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
                Uri image = result.getData();
                try {
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    InputStream imageStream;
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    view_image.setVisibility(View.VISIBLE);
                    view_image.setImageBitmap(bitmap);
                    attachment.setText("Attached");
                    attachment.setTextColor(context.getResources().getColor(R.color.black));
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

    public void GetFacultyList()
    {
        facultyitem.clear();
        facultyid.clear();
        facultyitem.add("Select Faculty");
        facultyid.add(0);
        Call<UserData1> call = apiCalling.GetAllUsersddl(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<UserData1>() {
            @Override
            public void onResponse(@NotNull Call<UserData1> call, @NotNull Response<UserData1> response) {
                if (response.isSuccessful()) {
                    UserData1 userData1 = response.body();
                    if (userData1 != null) {
                        if (userData1.isCompleted()) {
                            List<UserModel> userModelList = userData1.getData();
                            if (userModelList != null) {
                                if (userModelList.size() > 0) {
                                    for (UserModel singleResponseModel : userModelList) {
                                        String building_name = singleResponseModel.getUsername();
                                        facultyitem.add(building_name);

                                        int building_id = Integer.parseInt(String.valueOf(singleResponseModel.getUserID()));
                                        facultyid.add(building_id);
                                    }
                                    FACULTYITEM = new String[facultyitem.size()];
                                    FACULTYITEM = facultyitem.toArray(FACULTYITEM);

                                    FACULTYID = new Integer[facultyid.size()];
                                    FACULTYID = facultyid.toArray(FACULTYID);

                                    binduser();
                                }
                            }

                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<UserData1> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void binduser() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, FACULTYITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_faculty.setAdapter(adapter);
        if (bundle != null){
            selectSpinnerValue(sp_faculty,facultyModel.getStaff().getName());
        }
        sp_faculty.setOnItemSelectedListener(UserItemListener);
    }

    AdapterView.OnItemSelectedListener UserItemListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    faculty_id = facultyid.get(position);
                    if (sp_faculty.getSelectedItem().equals("Select Faculty")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetCourseList()
    {
        courseitem.clear();
        courseid.clear();
        courseitem.add("Select Course");
        courseid.add(0);
        Call<BranchCourseModel> call = apiCalling.Get_All_Branch_Course(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<BranchCourseModel>() {
            @Override
            public void onResponse(Call<BranchCourseModel> call, Response<BranchCourseModel> response) {
                if (response.isSuccessful()){
                    BranchCourseModel data = response.body();
                    if (data.isCompleted()){
                        List<BranchCourseModel.BranchCourceData> list = data.getData();
                        if (list != null && list.size() > 0){
                            for (BranchCourseModel.BranchCourceData model : list){
                                String name = model.getCourse().getCourseName();
                                courseitem.add(name);

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
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<BranchCourseModel> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void bindcourse()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, COURSEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_course.setAdapter(adapter);
        if (bundle != null){
            selectSpinnerValue(sp_course,facultyModel.getBranchCourse().getCourse().getCourseName());
        }
        sp_course.setOnItemSelectedListener(CourseItemListener);
    }

    AdapterView.OnItemSelectedListener CourseItemListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    course_id = courseid.get(position);
                    if (sp_course.getSelectedItem().equals("Select Course")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    }
                    if (sp_course.getSelectedItemId() != 0){
                        GetClassList();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetClassList()
    {
        progressBarHelper.showProgressDialog();
        classitem.clear();
        classid.clear();
        classitem.add("Select Class Name");
        classid.add(0);
        Call<BranchClassModel> call = apiCalling.Get_Class_Spinner(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),course_id);
        call.enqueue(new Callback<BranchClassModel>() {
            @Override
            public void onResponse(Call<BranchClassModel> call, Response<BranchClassModel> response) {
                if (response.isSuccessful()){
                    BranchClassModel data = response.body();
                    if (data.getCompleted()){
                        List<BranchClassSingleModel.BranchClassData> list = data.getData();
                        if (list != null && list.size() > 0){
                            for (BranchClassSingleModel.BranchClassData model : list){
                                String name = model.getClassModel().getClassName();
                                classitem.add(name);

                                int id = Integer.parseInt(String.valueOf(model.getClass_dtl_id()));
                                classid.add(id);
                            }
                            CLASSITEM = new String[classitem.size()];
                            CLASSITEM = classitem.toArray(CLASSITEM);

                            CLASSID = new Integer[classid.size()];
                            CLASSID = classid.toArray(CLASSID);

                            bindclass();
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<BranchClassModel> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void bindclass()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, CLASSITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_class.setAdapter(adapter);
        if (bundle != null){
            selectSpinnerValue(sp_class,facultyModel.getBranchClass().getClassModel().getClassName());
        }
        sp_class.setOnItemSelectedListener(ClassItemListener);
    }

    AdapterView.OnItemSelectedListener ClassItemListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    class_id = classid.get(position);
                    if (sp_class.getSelectedItem().equals("Select Class Name")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    }
                    if (sp_class.getSelectedItemId() != 0){
                        GetSubjectList();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetSubjectList()
    {
        progressBarHelper.showProgressDialog();
        subjectitem.clear();
        subjectid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);
        Call<BranchSubjectModel> call = apiCalling.Get_All_Subject(class_id,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),course_id);
        call.enqueue(new Callback<BranchSubjectModel>() {
            @Override
            public void onResponse(Call<BranchSubjectModel> call, Response<BranchSubjectModel> response) {
                if (response.isSuccessful()){
                    BranchSubjectModel data = response.body();
                    if (data.isCompleted()){
                        List<BranchSubjectModel.BranchSubjectData> list = data.getData();
                        if (list != null && list.size() > 0){
                            for (BranchSubjectModel.BranchSubjectData model : list){
                                for (int i = 0; i < model.getBranchSubjectData().size(); i++){
                                    String name = model.getBranchSubjectData().get(i).getSubject().getSubjectName();
                                    subjectitem.add(name);

                                    int id = Integer.parseInt(String.valueOf(model.getBranchSubjectData().get(i).getSubject_dtl_id()));
                                    subjectid.add(id);
                                }
                            }
                            SUBJECTITEM = new String[subjectitem.size()];
                            SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

                            SUBJECTID = new Integer[subjectid.size()];
                            SUBJECTID = subjectid.toArray(SUBJECTID);

                            bindsubject();
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<BranchSubjectModel> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void bindsubject()
    {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp_subject.setAdapter(adapter);
        if (bundle != null){
            selectSpinnerValue(sp_subject,facultyModel.getBranchSubject().getSubject().getSubjectName());
        }
        sp_subject.setOnItemSelectedListener(SubjectItemListener);
    }

    AdapterView.OnItemSelectedListener SubjectItemListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    subject_id = subjectid.get(position);
                    if (sp_subject.getSelectedItem().equals("Select Subject")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

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

    public void selectClass()
    {
        classitem.clear();
        classid.clear();
        classitem.add("Select Class Name");
        classid.add(0);

        CLASSITEM = new String[classitem.size()];
        CLASSITEM = classitem.toArray(CLASSITEM);

        bindclass();
    }

    public void selectSubject()
    {
        subjectitem.clear();
        subjectid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);

        SUBJECTITEM = new String[subjectitem.size()];
        SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

        bindsubject();
    }
}