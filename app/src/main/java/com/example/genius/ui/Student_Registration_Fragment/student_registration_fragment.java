package com.example.genius.ui.Student_Registration_Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
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
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
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
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.SchoolData;
import com.example.genius.Model.SchoolModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.StudentByIdData;
import com.example.genius.Model.StudentModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

@SuppressLint({"SetTextI18n", "SimpleDateFormat"})
public class student_registration_fragment extends Fragment {

    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    Boolean selectfile = false;
    TextView attachment, uno;
    ImageView imageView;
    SearchableSpinner standard, school_name, school_time, batch_time,course_name;
    EditText gr_no, addmission_date, first_name, middle_name, last_name, address, percentage, contact_no, class_name, father_occu, mother_occu, parent_name, login_id, password, student_password, parent_password;
    TextView birth_date;
    RadioButton pass, fail, active, inactive, rb1, rb2;
    RadioGroup result_rg, status_rg;
    Button save_student_regi, edit_student_regi;
    ImageView hide_password;
    Context context;
    byte[] imageVal;
    Bitmap bitmap;
    File instrumentFileDestination;
    List<String> standarditem = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(),courseid = new ArrayList<>();
    String[] STANDARDITEM,COURSEITEM;
    Integer[] STANDARDID,COURSEID;
    List<String> schoolnitem = new ArrayList<>();
    List<Integer> schoolnid = new ArrayList<>();
    String[] SCHOOLNITEM;
    Integer[] SCHOOLNID;
    List<String> schooltitem = new ArrayList<>(), schooltid = new ArrayList<>();
    String[] SCHOOLTITEM;
    List<String> batchitem = new ArrayList<>(), batchid = new ArrayList<>();
    String[] BATCHITEM;
    String indate = "01-01-0001", StandardName, SchoolName, Result, Status, SchoolTime, BatchTime, BatchId, SchooltimeId = "-1", bdate = "01-01-0001", grade = "none", classname = "none", student_contact_no = "none", father_occupation = "none", mother_occupation = "none", Name,Course_Standard,
            FileName = "none", Extension = "none", Address, attach, FinalFileName = "none", RandomFileName, OriginalFileName, Result_Status;
    int select, flag = 0;
    long SchoolId = -1, StandardId, TransactionID, StudentID, ParentID,courseID;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    Bundle bundle;
    OnBackPressedCallback callback;
    String pictureFilePath;
    private int year;
    private int month;
    private int day;
    int result1 = -1, status1;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Student Registration");
        View root = inflater.inflate(R.layout.student_registration_fragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        attachment = root.findViewById(R.id.attachment);
        imageView = root.findViewById(R.id.imageView);
        standard = root.findViewById(R.id.standard);
        school_name = root.findViewById(R.id.school_name);
        school_time = root.findViewById(R.id.school_time);
        batch_time = root.findViewById(R.id.batch_time);
        gr_no = root.findViewById(R.id.gr_no);
        addmission_date = root.findViewById(R.id.addmission_date);
        first_name = root.findViewById(R.id.first_name);
        middle_name = root.findViewById(R.id.middle_name);
        last_name = root.findViewById(R.id.last_name);
        birth_date = root.findViewById(R.id.birth_date);
        address = root.findViewById(R.id.address);
        percentage = root.findViewById(R.id.percentage);
        contact_no = root.findViewById(R.id.contact_no);
        class_name = root.findViewById(R.id.class_name);
        father_occu = root.findViewById(R.id.father_occu);
        mother_occu = root.findViewById(R.id.mother_occu);
        parent_name = root.findViewById(R.id.parent_name);
        login_id = root.findViewById(R.id.login_id);
        password = root.findViewById(R.id.password);
        pass = root.findViewById(R.id.pass);
        fail = root.findViewById(R.id.fail);
        active = root.findViewById(R.id.active);
        inactive = root.findViewById(R.id.inactive);
        save_student_regi = root.findViewById(R.id.save_student_regi);
        edit_student_regi = root.findViewById(R.id.edit_student_regi);
        result_rg = root.findViewById(R.id.result_rg);
        status_rg = root.findViewById(R.id.status_rg);
        uno = root.findViewById(R.id.uno);
        student_password = root.findViewById(R.id.student_password);
        parent_password = root.findViewById(R.id.parent_password);
        course_name = root.findViewById(R.id.course_name);
        hide_password = root.findViewById(R.id.hide_password);

        bundle = getArguments();
        if (bundle != null) {
            save_student_regi.setVisibility(View.GONE);
            edit_student_regi.setVisibility(View.VISIBLE);
            if (bundle.containsKey("Address")) {
                address.setText(bundle.getString("Address"));
            }
            if (bundle.containsKey("BirthDate")) {
                try {
                    Date d = actualdate.parse(bundle.getString("BirthDate"));
                    if (d != null) {
                        birth_date.setText("" + displaydate.format(d));
                    }
                    bdate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("ClassName")) {
                class_name.setText(bundle.getString("ClassName"));
            }
            if (bundle.containsKey("ContactNo")) {
                contact_no.setText(bundle.getString("ContactNo"));
            }
            if (bundle.containsKey("FatherOccupation")) {
                father_occu.setText(bundle.getString("FatherOccupation"));
            }
            if (bundle.containsKey("FirstName")) {
                first_name.setText(bundle.getString("FirstName"));
            }
            if (bundle.containsKey("LastName")) {
                last_name.setText(bundle.getString("LastName"));
            }
            if (bundle.containsKey("MiddleName")) {
                middle_name.setText(bundle.getString("MiddleName"));
            }
            if (bundle.containsKey("MotherOccupation")) {
                mother_occu.setText(bundle.getString("MotherOccupation"));
            }
            if (bundle.containsKey("ParentContactNo")) {
                login_id.setText(bundle.getString("ParentContactNo"));
            }
            if (bundle.containsKey("ParentName")) {
                parent_name.setText(bundle.getString("ParentName"));
            }
            if (bundle.containsKey("Grade")) {
                percentage.setText(bundle.getString("Grade"));
            }
            if (bundle.containsKey("STUDENT_PASSWORD")) {
                student_password.setText("" + bundle.getString("STUDENT_PASSWORD"));
            }
            if (bundle.containsKey("PARENT_PASSWORD")) {
                parent_password.setText("" + bundle.getString("PARENT_PASSWORD"));
            }
            if (bundle.containsKey("FileName")) {
                String file = bundle.getString("FileName");
                if (file != null) {
                    String Path = bundle.getString("FilePath");
                    imageView.setVisibility(View.VISIBLE);
                    attachment.setText("attached");
                    Glide.with(context).load(Path).into(imageView);
                    if (Path.contains(".") && Path.contains("/")) {
                        Extension = Path.substring(Path.lastIndexOf(".") + 1);
                        String FileNameWithExtension = Path.substring(Path.lastIndexOf("/") + 1);
                        String[] FileNameArray = FileNameWithExtension.split("\\.");
                        RandomFileName = FileNameArray[0];
                    }
                    FinalFileName = file + "," + RandomFileName;
                } else {
                    imageView.setVisibility(View.GONE);
                    attachment.setText("");
                    FinalFileName = "none";
                }
            }
            if (bundle.containsKey("AdmissionDate")) {
                try {
                    Date d = actualdate.parse(bundle.getString("AdmissionDate"));
                    addmission_date.setText("" + displaydate.format(d));
                    indate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("Status")) {
                int st = bundle.getInt("Status");
                if (st == 1) {
                    active.setChecked(true);
                    inactive.setChecked(false);
                }
                if (st == 2) {
                    active.setChecked(false);
                    inactive.setChecked(true);
                }
            }
            if (bundle.containsKey("LastYearResult")) {
                int st = bundle.getInt("LastYearResult");
                if (st == 1) {
                    pass.setChecked(true);
                    fail.setChecked(false);
                }
                if (st == 2) {
                    pass.setChecked(false);
                    fail.setChecked(true);
                }
            }
            if (bundle.containsKey("ParentID")) {
                ParentID = bundle.getLong("ParentID");
            }
            if (bundle.containsKey("StudentID")) {
                StudentID = bundle.getLong("StudentID");
            }
            if (bundle.containsKey("TransactionID")) {
                TransactionID = bundle.getLong("TransactionID");
            }
        }

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllCourse();
            GetAllSchool();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectschool_time();
        selectbatch_time();
        selectStandard();

        hide_password.setOnClickListener(v -> {
            if (student_password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                hide_password.setImageResource(R.drawable.eye_on);
                student_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                student_password.setSelection(student_password.length());
            } else {
                hide_password.setImageResource(R.drawable.eye_off);
                student_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                student_password.setSelection(student_password.length());
            }
        });

        result_rg.setOnCheckedChangeListener((group, checkedId) -> {
            rb1 = root.findViewById(checkedId);
            Result = rb1.getText().toString();
        });
        select = result_rg.getCheckedRadioButtonId();
        rb1 = root.findViewById(select);
        Result = rb1.getText().toString();

        status_rg.setOnCheckedChangeListener((group, checkedId) -> {
            rb2 = root.findViewById(checkedId);
            Status = rb2.getText().toString();
        });
        select = status_rg.getCheckedRadioButtonId();
        rb2 = root.findViewById(select);
        Status = rb2.getText().toString();

        addmission_date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        indate = year + "-" + pad(month + 1) + "-" + pad(day);
                        addmission_date.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });

        birth_date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        bdate = year + "-" + pad(month + 1) + "-" + pad(day);
                        birth_date.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });

        attachment.setOnClickListener(v -> {
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

        save_student_regi.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (first_name.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter First Name.", Toast.LENGTH_SHORT).show();
                } else if (middle_name.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Middle Name.", Toast.LENGTH_SHORT).show();
                } else if (last_name.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Last Name.", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Address.", Toast.LENGTH_SHORT).show();
                }else if (course_name.getSelectedItemId() ==0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                } else if (standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please select Standard.", Toast.LENGTH_SHORT).show();
                } else if (batch_time.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (parent_name.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Parent Name.", Toast.LENGTH_SHORT).show();
                } else if (login_id.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Contact No(Login Id).", Toast.LENGTH_SHORT).show();
                } else if (student_password.getText().toString().isEmpty())
                    Toast.makeText(context, "Please enter Password.", Toast.LENGTH_SHORT).show();
                else if (login_id.getText().toString().length() < 10) {
                    Toast.makeText(context, "Please enter valid Contact No(Login Id).", Toast.LENGTH_SHORT).show();
                } else if (contact_no.getText().toString().length() < 10) {
                    Toast.makeText(context, "Please enter valid student contact number.", Toast.LENGTH_SHORT).show();
                } else {
                    Call<StudentModel.StudentData1> call;
                    progressBarHelper.showProgressDialog();
                    if (Result.equalsIgnoreCase("Pass")) {
                        result1 = 1;
                    } else {
                        result1 = 2;
                    }
                    if (Status.equalsIgnoreCase("Active")) {
                        status1 = 1;
                    } else {
                        status1 = 2;
                    }
                    if (!percentage.getText().toString().equals("")) {
                        grade = percentage.getText().toString();
                    }
                    if (!class_name.getText().toString().equals("")) {
                        classname = encodeDecode(class_name.getText().toString().trim());
                    }
                    if (!contact_no.getText().toString().equals("")) {
                        student_contact_no = contact_no.getText().toString();
                    }
                    if (!father_occu.getText().toString().equals("")) {
                        father_occupation = encodeDecode(father_occu.getText().toString());
                    }
                    if (!mother_occu.getText().toString().equals("")) {
                        mother_occupation = encodeDecode(mother_occu.getText().toString());
                    }
                    Address = encodeDecode(address.getText().toString().trim());
                    Name = first_name.getText().toString().replaceAll("\\s", "") + "," + middle_name.getText().toString().replaceAll("\\s", "") + "," + last_name.getText().toString().replaceAll("\\s", "");
                    Course_Standard = courseID + "," + StandardId;
                    Result_Status = result1 + "," + status1;
                    if (instrumentFileDestination != null) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        call = apiCalling.StudentMaintenance(0, 0, "1", Name, bdate, Address,
                                Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Course_Standard, SchoolId, Integer.parseInt(SchooltimeId),
                                Integer.parseInt(BatchId), Result_Status, grade, classname, student_contact_no, indate, encodeDecode(parent_name.getText().toString()), father_occupation, mother_occupation, login_id.getText().toString(),
                                Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, student_password.getText().toString(), student_password.getText().toString(), "0", "0", true, uploadfile);
                    } else {
                        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
                        call = apiCalling.StudentMaintenance(0, 0, "1", Name, bdate, Address,
                                Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Course_Standard, SchoolId, Integer.parseInt(SchooltimeId),
                                Integer.parseInt(BatchId), Result_Status, grade, classname, student_contact_no, indate, encodeDecode(parent_name.getText().toString()), father_occupation, mother_occupation, login_id.getText().toString(),
                                Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, student_password.getText().toString(), student_password.getText().toString(), FileName, Extension, false, uploadfile);
                    }
                    call.enqueue(new Callback<StudentModel.StudentData1>() {
                        @Override
                        public void onResponse(@NotNull Call<StudentModel.StudentData1> call, @NotNull Response<StudentModel.StudentData1> response) {
                            if (response.isSuccessful()) {
                                StudentModel.StudentData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    student_registration_Listfragment profileFragment = new student_registration_Listfragment();
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.nav_host_fragment, profileFragment);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<StudentModel.StudentData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_student_regi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.checkNetworkConnection(context)) {
                    if (first_name.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter First Name.", Toast.LENGTH_SHORT).show();
                    } else if (middle_name.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter Middle Name.", Toast.LENGTH_SHORT).show();
                    } else if (last_name.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter Last Name.", Toast.LENGTH_SHORT).show();
                    } else if (address.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter Address.", Toast.LENGTH_SHORT).show();
                    }else if (course_name.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                    } else if (standard.getSelectedItemId() == 0) {
                        Toast.makeText(context, "Please select Standard.", Toast.LENGTH_SHORT).show();
                    } else if (batch_time.getSelectedItemId() == 0) {
                        Toast.makeText(context, "Please select Batch Time.", Toast.LENGTH_SHORT).show();
                    } else if (parent_name.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter Parent Name.", Toast.LENGTH_SHORT).show();
                    } else if (login_id.getText().toString().equals("")) {
                        Toast.makeText(context, "Please enter Contact No(Login Id).", Toast.LENGTH_SHORT).show();
                    } else if (student_password.getText().toString().isEmpty())
                        Toast.makeText(context, "Please enter Password.", Toast.LENGTH_SHORT).show();
                    else if (login_id.getText().toString().length() < 10) {
                        Toast.makeText(context, "Please enter valid Contact No(Login Id).", Toast.LENGTH_SHORT).show();
                    } else if (contact_no.getText().toString().length() < 10) {
                        Toast.makeText(context, "Please enter valid student contact number.", Toast.LENGTH_SHORT).show();
                    } else {
                        Call<StudentModel.StudentData1> call;
                        progressBarHelper.showProgressDialog();
                        if (Result.equalsIgnoreCase("Pass")) {
                            result1 = 1;
                        } else {
                            result1 = 2;
                        }
                        if (Status.equalsIgnoreCase("Active")) {
                            status1 = 1;
                        } else {
                            status1 = 2;
                        }
                        if (!percentage.getText().toString().equals("")) {
                            grade = percentage.getText().toString();
                        }
                        if (!class_name.getText().toString().equals("")) {
                            classname = encodeDecode(class_name.getText().toString().trim());
                        }
                        if (!contact_no.getText().toString().equals("")) {
                            student_contact_no = contact_no.getText().toString();
                        }
                        if (!father_occu.getText().toString().equals("")) {
                            father_occupation = encodeDecode(father_occu.getText().toString());
                        }
                        if (!mother_occu.getText().toString().equals("")) {
                            mother_occupation = encodeDecode(mother_occu.getText().toString());
                        }
                        Address = encodeDecode(address.getText().toString().trim());
                        Name = first_name.getText().toString().replaceAll("\\s", "") + "," + middle_name.getText().toString().replaceAll("\\s", "") + "," + last_name.getText().toString().replaceAll("\\s", "");
                        Course_Standard = courseID + "," + StandardId;
                        Result_Status = result1 + "," + status1;
                        if (instrumentFileDestination != null) {
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                            MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                            call = apiCalling.StudentMaintenance(StudentID, ParentID, "1", Name, bdate, Address,
                                    Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Course_Standard, SchoolId, Integer.parseInt(SchooltimeId),
                                    Integer.parseInt(BatchId), Result_Status, grade, classname, student_contact_no, indate, encodeDecode(parent_name.getText().toString()), father_occupation, mother_occupation, login_id.getText().toString(),
                                    Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), TransactionID, student_password.getText().toString(), student_password.getText().toString(), "0", "0", true, uploadfile);
                        } else {
                            RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                            MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
                            call = apiCalling.StudentMaintenance(StudentID, ParentID, "1", Name, bdate, Address,
                                    Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Course_Standard, SchoolId, Integer.parseInt(SchooltimeId),
                                    Integer.parseInt(BatchId), Result_Status, grade, classname, student_contact_no, indate, encodeDecode(parent_name.getText().toString()), father_occupation, mother_occupation, login_id.getText().toString(),
                                    Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), TransactionID, student_password.getText().toString(), student_password.getText().toString(), FinalFileName, Extension, false, uploadfile);
                        }
                        call.enqueue(new Callback<StudentModel.StudentData1>() {
                            @Override
                            public void onResponse(@NotNull Call<StudentModel.StudentData1> call, @NotNull Response<StudentModel.StudentData1> response) {
                                if (response.isSuccessful()) {
                                    StudentModel.StudentData1 data = response.body();
                                    if (data.isCompleted()) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        student_registration_Listfragment profileFragment = new student_registration_Listfragment();
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.nav_host_fragment, profileFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<StudentModel.StudentData1> call, @NotNull Throwable t) {
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

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                student_registration_Listfragment profileFragment = new student_registration_Listfragment();
                FragmentManager fm = getActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        }

        ;

        getActivity().

                getOnBackPressedDispatcher().

                addCallback(getActivity(), callback);

        return root;
    }

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    private void showAddProfilePicDialog() {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.dialog_selection, null);
        LinearLayout camera = dialogView.findViewById(R.id.camera);
        LinearLayout gallery = dialogView.findViewById(R.id.gallery);
        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                takePic();
            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                pickImage();
            }
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
                    attachment.setText("Attached");
                    attachment.setTextColor(context.getResources().getColor(R.color.black));
                    instrumentFileDestination = new File(pictureFilePath);
                    try {
                        imageView.setImageURI(Uri.fromFile(instrumentFileDestination));
                    } catch (Exception ignored) {

                    }
                    Toast.makeText(context, "" + instrumentFileDestination, Toast.LENGTH_SHORT).show();
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
                    instrumentFileDestination = new File(Path);
                    InputStream imageStream;
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(bitmap);
                    attachment.setText("Attached");
                    attachment.setTextColor(context.getResources().getColor(R.color.black));
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

    @SuppressLint("QueryPermissionsNeeded")
    private void takePic() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
//        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        File pictureFile = null;
        try {
            pictureFile = getPictureFile();
        } catch (IOException ex) {

            return;
        }
        if (pictureFile != null) {
            Uri photoURI = FileProvider.getUriForFile(context, "com.example.genius.provider", pictureFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
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
                encodedImage = Base64.encodeToString(imageVal, android.util.Base64.DEFAULT);
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
               }
           }

           @Override
           public void onFailure(Call<BranchCourseModel> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
               Toast.makeText(context,t.toString(), Toast.LENGTH_SHORT).show();
           }
       });
    }

    public void bindcourse() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, COURSEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        course_name.setAdapter(adapter);
        if (bundle != null) {
            int b = courseid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("Course"))));
            course_name.setSelection(b);
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
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
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
            public void onResponse(@NotNull Call<BranchClassModel> call, @NotNull Response<BranchClassModel> response) {
                if (response.isSuccessful()) {
                    BranchClassModel data = response.body();
                    if (data.getCompleted()) {
                        List<BranchClassSingleModel.BranchClassData> list = data.getData();
                        if (list != null && list.size() > 0)
                        {
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
            public void onFailure(@NotNull Call<BranchClassModel> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindstandard() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STANDARDITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        standard.setAdapter(adapter);
        standard.setOnItemSelectedListener(onItemSelectedListener7);
        if (bundle != null) {
            int b = standardid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("Standard"))));
            standard.setSelection(b);
        }
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
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetAllSchool() {
        schoolnitem.clear();
        schoolnid.clear();
        schoolnitem.add("Select School");
        schoolnid.add(0);

        Call<SchoolData> call = apiCalling.GetAllSchool(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SchoolData>() {
            @Override
            public void onResponse(@NotNull Call<SchoolData> call, @NotNull Response<SchoolData> response) {
                if (response.isSuccessful()) {
                    SchoolData schoolData = response.body();
                    if (schoolData != null) {
                        if (schoolData.isCompleted()) {
                            List<SchoolModel> respose = schoolData.getData();
                            for (SchoolModel singleResponseModel : respose) {

                                String school_name = singleResponseModel.getSchoolName();
                                schoolnitem.add(school_name);

                                int school_id = (int) singleResponseModel.getSchoolID();
                                schoolnid.add(school_id);
                            }
                            SCHOOLNITEM = new String[schoolnitem.size()];
                            SCHOOLNITEM = schoolnitem.toArray(SCHOOLNITEM);

                            SCHOOLNID = new Integer[schoolnid.size()];
                            SCHOOLNID = schoolnid.toArray(SCHOOLNID);

                            bindschool();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<SchoolData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindschool() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SCHOOLNITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        school_name.setAdapter(adapter);
        school_name.setOnItemSelectedListener(onItemSelectedListener6);
        if (bundle != null) {
            int b = schoolnid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("SchoolName"))));
            school_name.setSelection(b);
        }
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SchoolName = schoolnitem.get(position);
                    SchoolId = Long.parseLong(schoolnid.get(position).toString());
                    if (school_name.getSelectedItem().equals("Select School")) {
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


    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "ZOFTINO_" + timeStamp;
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    public void selectschool_time() {
        schooltitem.clear();

        schooltitem.add("School Time");
        schooltid.add("0");
        schooltitem.add("Morning");
        schooltid.add("1");
        schooltitem.add("AfterNoon");
        schooltid.add("2");
        SCHOOLTITEM = new String[schooltitem.size()];
        SCHOOLTITEM = schooltitem.toArray(SCHOOLTITEM);

        bindschool_time();
    }

    public void bindschool_time() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SCHOOLTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        school_time.setAdapter(adapter);
        school_time.setOnItemSelectedListener(onItemSelectedListener61);
        if (bundle != null) {
            int b = schooltid.indexOf(String.valueOf(bundle.getInt("SchoolTime")));
            school_time.setSelection(b);
        }
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener61 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SchoolTime = schooltitem.get(position);
                    SchooltimeId = schooltid.get(position);
                    if (school_time.getSelectedItem().equals("School Time")) {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                            ((TextView) parent.getChildAt(0)).setTextSize(13);
                        } catch (Exception ignored) {
                        }
                    } else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception ignored) {
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    public void selectbatch_time() {
        batchitem.clear();
        batchitem.add("Batch Time");
        batchid.add("0");
        batchitem.add("Morning");
        batchid.add("1");
        batchitem.add("AfterNoon");
        batchid.add("2");
        batchitem.add("Evening");
        batchid.add("3");
        BATCHITEM = new String[batchitem.size()];
        BATCHITEM = batchitem.toArray(BATCHITEM);

        bindbatch_time();
    }

    public void bindbatch_time() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BATCHITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batch_time.setAdapter(adapter);
        batch_time.setOnItemSelectedListener(onItemSelectedListener77);
        if (bundle != null) {
            int a = batchid.indexOf(String.valueOf(bundle.getString("BatchTime")));
            batch_time.setSelection(a);
        }
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener77 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BatchTime = batchitem.get(position);
                    BatchId = batchid.get(position);
                    if (batch_time.getSelectedItem().equals("Batch Time")) {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                            ((TextView) parent.getChildAt(0)).setTextSize(13);
                        } catch (Exception ignored) {
                        }
                    } else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception ignored) {
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

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
        standard.setAdapter(adapter);
        standard.setOnItemSelectedListener(selectstandard);
    }

    AdapterView.OnItemSelectedListener selectstandard =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StandardId = standardid.get(position);
                    if (standard.getSelectedItem().equals("Select Standard")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
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

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }
}