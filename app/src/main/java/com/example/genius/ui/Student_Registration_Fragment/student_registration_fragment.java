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
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BatchModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.SchoolData;
import com.example.genius.Model.SchoolModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.StudentByIdData;
import com.example.genius.Model.StudentModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Preferences;
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
    Boolean a, selectfile = false;
    String FilePath_URL;
    TextView attachment, uno;
    ImageView imageView;
    SearchableSpinner standard, school_name, school_time, batch_time;
    EditText gr_no, addmission_date, first_name, middle_name, last_name, birth_date, address, percentage, contact_no, class_name, father_occu, mother_occu, parent_name, login_id, password;
    RadioButton pass, fail, active, inactive, rb1, rb2;
    RadioGroup result_rg, status_rg;
    Button save_student_regi, edit_student_regi;
    Context context;
    byte[] imageVal;
    Bitmap bitmap;
    File instrumentFileDestination, photofile;
    List<String> standarditem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>();
    String[] STANDARDITEM;
    Integer[] STANDARDID;
    List<String> schoolnitem = new ArrayList<>();
    List<Integer> schoolnid = new ArrayList<>();
    String[] SCHOOLNITEM;
    Integer[] SCHOOLNID;
    List<String> schooltitem = new ArrayList<>(), schooltid = new ArrayList<>();
    String[] SCHOOLTITEM, SCHOOLTID;
    List<String> batchitem = new ArrayList<>(), batchid = new ArrayList<>();
    String[] BATCHITEM, BATCHID;
    String indate, StandardName, SchoolName, Result, Status, SchoolTime, BatchTime, photo, BatchId, SchooltimeId, bdate;
    int select, flag = 0;
    long SchoolId, StandardId, TransactionID, StudentID, ParentID;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    Bundle bundle;
    OnBackPressedCallback callback;
    String pictureFilePath, attach = "";
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
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
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
    private int year;
    private int month;
    private int day;
    int result1, status1;

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

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
            if (bundle.containsKey("Percentage")) {
                percentage.setText("" + bundle.getInt("Percentage"));
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
            if (bundle.containsKey("ParentID")) {
                ParentID = bundle.getLong("ParentID");
            }
            if (bundle.containsKey("TransactionID")) {
                TransactionID = bundle.getLong("TransactionID");
            }
            if (bundle.containsKey("StudentID")) {
                StudentID = bundle.getLong("StudentID");
                progressBarHelper.showProgressDialog();
                Call<StudentByIdData> call = apiCalling.GetStudentByID(StudentID);
                call.enqueue(new Callback<StudentByIdData>() {
                    @Override
                    public void onResponse(@NotNull Call<StudentByIdData> call, @NotNull Response<StudentByIdData> response) {
                        if (response.isSuccessful()) {
                            StudentByIdData data = response.body();
                            if (data != null && data.Completed) {
                                StudentModel studentModel = data.Data;
                                if (studentModel.getStudentID() > 0) {
                                    attachment.setText("Attached");
                                    imageView.setVisibility(View.VISIBLE);
                                    attach = studentModel.getStudImage();
                                    attachment.setTextColor(context.getResources().getColor(R.color.black));
                                    imageVal = Base64.decode(attach, Base64.DEFAULT);
                                    Bitmap decodedByte = BitmapFactory.decodeByteArray(imageVal, 0, imageVal.length);
                                    imageView.setImageBitmap(decodedByte);
                                }
                            }
                        }
                        progressBarHelper.hideProgressDialog();
                    }

                    @Override
                    public void onFailure(@NotNull Call<StudentByIdData> call, @NotNull Throwable t) {
                        progressBarHelper.hideProgressDialog();
                    }
                });
            }
        } else {
            addmission_date.setText("" + displaydate.format(Calendar.getInstance().getTime()));
            indate = actualdate.format(Calendar.getInstance().getTime());
        }

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllSchool();
            GetAllStandard();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }
        selectschool_time();
        selectbatch_time();
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
                        indate = pad(month + 1) + "/" + pad(day) + "/" + year;
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
                        bdate = pad(month + 1) + "/" + pad(day) + "/" + year;
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
                if (addmission_date.getText().toString().equals(""))
                    Toast.makeText(context, "Please select Admission Date.", Toast.LENGTH_SHORT).show();
                else if (first_name.getText().toString().equals(""))
                    Toast.makeText(context, "Please enter First Name.", Toast.LENGTH_SHORT).show();
                else if (middle_name.getText().toString().equals(""))
                    Toast.makeText(context, "Please enter Middle Name.", Toast.LENGTH_SHORT).show();
                else if (last_name.getText().toString().equals(""))
                    Toast.makeText(context, "Please enter Last Name.", Toast.LENGTH_SHORT).show();
                else if (address.getText().toString().equals(""))
                    Toast.makeText(context, "Please enter Address.", Toast.LENGTH_SHORT).show();
                else if (standard.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please select Standard.", Toast.LENGTH_SHORT).show();
                else if (batch_time.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please select Batch Time.", Toast.LENGTH_SHORT).show();
                else if (parent_name.getText().toString().equals(""))
                    Toast.makeText(context, "Please enter Parent Name.", Toast.LENGTH_SHORT).show();
                else if (login_id.getText().toString().equals(""))
                    Toast.makeText(context, "Please enter Contact No(Login Id).", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
//                    insertwithoutAttachment();
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
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(status1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    StudentModel.StudentMaintModel maintModel = new StudentModel.StudentMaintModel(parent_name.getText().toString(), father_occu.getText().toString(), mother_occu.getText().toString(), login_id.getText().toString());
                    BatchModel batchModel = new BatchModel(BatchTime);
                    SchoolModel schoolModel = new SchoolModel(SchoolId);
                    StandardModel standardModel = new StandardModel(StandardId);
                    StudentModel model = new StudentModel("", first_name.getText().toString(), middle_name.getText().toString(), last_name.getText().toString(), bdate, indate, address.getText().toString(), Integer.parseInt(SchooltimeId), result1,
                            percentage.getText().toString(), class_name.getText().toString(), contact_no.getText().toString(), attach, standardModel, schoolModel, branchModel, transactionModel, rowStatusModel, batchModel, maintModel);


                    Call<StudentModel.StudentData1> call = apiCalling.StudentMaintanance(model);
                    call.enqueue(new Callback<StudentModel.StudentData1>() {
                        @Override
                        public void onResponse(@NotNull Call<StudentModel.StudentData1> call, @NotNull Response<StudentModel.StudentData1> response) {

                            if (response.isSuccessful()) {
                                StudentModel.StudentData1 data = response.body();
                                if (data.isCompleted()) {
                                    StudentModel studentModel = data.getData();
                                    if (studentModel.getStudentID() > 0) {
                                        student_registration_Listfragment profileFragment = new student_registration_Listfragment();
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.nav_host_fragment, profileFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<StudentModel.StudentData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
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
                    if (addmission_date.getText().toString().equals(""))
                        Toast.makeText(context, "Please select Admission Date.", Toast.LENGTH_SHORT).show();
                    else if (first_name.getText().toString().equals(""))
                        Toast.makeText(context, "Please enter First Name.", Toast.LENGTH_SHORT).show();
                    else if (middle_name.getText().toString().equals(""))
                        Toast.makeText(context, "Please enter Middle Name.", Toast.LENGTH_SHORT).show();
                    else if (last_name.getText().toString().equals(""))
                        Toast.makeText(context, "Please enter Last Name.", Toast.LENGTH_SHORT).show();
                    else if (address.getText().toString().equals(""))
                        Toast.makeText(context, "Please enter Address.", Toast.LENGTH_SHORT).show();
                    else if (standard.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please select Standard.", Toast.LENGTH_SHORT).show();
                    else if (batch_time.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please select Batch Time.", Toast.LENGTH_SHORT).show();
                    else if (parent_name.getText().toString().equals(""))
                        Toast.makeText(context, "Please enter Parent Name.", Toast.LENGTH_SHORT).show();
                    else if (login_id.getText().toString().equals(""))
                        Toast.makeText(context, "Please enter Contact No(Login Id).", Toast.LENGTH_SHORT).show();
                    else {
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
                        progressBarHelper.showProgressDialog();
//                    insertwithoutAttachment();
                        TransactionModel transactionModel = new TransactionModel(TransactionID, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                        RowStatusModel rowStatusModel = new RowStatusModel(status1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        StudentModel.StudentMaintModel maintModel = new StudentModel.StudentMaintModel(StudentID, parent_name.getText().toString(), father_occu.getText().toString(), mother_occu.getText().toString(), login_id.getText().toString(), ParentID);
                        BatchModel batchModel = new BatchModel(BatchTime);
                        SchoolModel schoolModel = new SchoolModel(SchoolId);
                        StandardModel standardModel = new StandardModel(StandardId);
                        StudentModel model = new StudentModel(StudentID, "", first_name.getText().toString(), middle_name.getText().toString(), last_name.getText().toString(), bdate, indate, address.getText().toString(), Integer.parseInt(SchooltimeId), result1,
                                percentage.getText().toString(), class_name.getText().toString(), contact_no.getText().toString(), attach, standardModel, schoolModel, branchModel, transactionModel, rowStatusModel, batchModel, maintModel);
                        Call<StudentModel.StudentData1> call = apiCalling.StudentMaintanance(model);
                        call.enqueue(new Callback<StudentModel.StudentData1>() {
                            @Override
                            public void onResponse(@NotNull Call<StudentModel.StudentData1> call, @NotNull Response<StudentModel.StudentData1> response) {

                                if (response.isSuccessful()) {
                                    StudentModel.StudentData1 data = response.body();
                                    if (data.isCompleted()) {
                                        StudentModel studentModel = data.getData();
                                        if (studentModel.getStudentID() > 0) {
                                            student_registration_Listfragment profileFragment = new student_registration_Listfragment();
                                            FragmentManager fm = getActivity().getSupportFragmentManager();
                                            FragmentTransaction ft = fm.beginTransaction();
                                            ft.replace(R.id.nav_host_fragment, profileFragment);
                                            ft.addToBackStack(null);
                                            ft.commit();
                                        }
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }

                            @Override
                            public void onFailure(@NotNull Call<StudentModel.StudentData1> call, @NotNull Throwable t) {
                                progressBarHelper.hideProgressDialog();
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
        };

        getActivity().getOnBackPressedDispatcher().addCallback(getActivity(), callback);

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
                   /* Bitmap image = (Bitmap) Objects.requireNonNull(result.getExtras()).get("data");
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(image);
                    imageVal = ImageUtility.using(getActivity()).toBase64FromBitmap(image);
                   */
                    attachment.setText("Attached");
                    attachment.setTextColor(context.getResources().getColor(R.color.black));
                    instrumentFileDestination = new File(pictureFilePath);
                    imageVal = ImageUtility.using(context).toBase64(instrumentFileDestination.getPath());
                    attach = Base64.encodeToString(imageVal, android.util.Base64.DEFAULT);
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

    public void GetAllSchool() {
        schoolnitem.add("Select School");
        schoolnid.add(0);

        Call<SchoolData> call = apiCalling.GetAllSchool(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SchoolData>() {
            @Override
            public void onResponse(@NotNull Call<SchoolData> call, @NotNull Response<SchoolData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
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
                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
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

    public void GetAllStandard() {
        standarditem.add("Select Standard");
        standardid.add(0);

        Call<StandardData> call = apiCalling.GetAllStandard(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StandardData>() {
            @Override
            public void onResponse(@NotNull Call<StandardData> call, @NotNull Response<StandardData> response) {
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
            public void onFailure(@NotNull Call<StandardData> call, @NotNull Throwable t) {
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
            int a = batchid.indexOf(String.valueOf(bundle.getInt("BatchTime")));
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
}