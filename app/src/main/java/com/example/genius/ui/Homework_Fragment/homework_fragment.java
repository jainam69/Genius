package com.example.genius.ui.Homework_Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.HomeworkByIdData;
import com.example.genius.Model.HomeworkModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
import com.example.genius.helper.FileUtils;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class homework_fragment extends Fragment {
    SearchableSpinner standard, batch_time, subject, branch;
    EditText remarks;
    TextView homework_date;
    TextView attachment_homework, id, transactionid;
    ImageView imageView;
    Button save_homework, edit_homework;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(), branchitem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(), branchid = new ArrayList<>();
    String[] STANDARDITEM, SUBJECTITEM, BRANCHITEM, BATCHITEM;
    Integer[] STANDARDID, SUBJECTID, BRANCHID;
    private int year;
    private int month;
    private int day;
    String indate, attach = "", filename = "";
    String StandardName, SubjectName, BatchTime, BranchName, BranchID, SubjectId, BatchId;
    Bundle bundle;
    OnBackPressedCallback callback;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    int flag = 0;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    Long StandardId;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    String Description = "none", Extension,FinalFileName,OriginFileName,RandomFileName;

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Homework Entry");
        View root = inflater.inflate(R.layout.fragment_homework_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        standard = root.findViewById(R.id.standard);
        batch_time = root.findViewById(R.id.batch_time);
        subject = root.findViewById(R.id.subject);
        branch = root.findViewById(R.id.branch);
        homework_date = root.findViewById(R.id.homework_date);
        remarks = root.findViewById(R.id.remarks);
        attachment_homework = root.findViewById(R.id.attachment_homework);
        save_homework = root.findViewById(R.id.save_homework);
        edit_homework = root.findViewById(R.id.edit_homework);
        imageView = root.findViewById(R.id.imageView);
        id = root.findViewById(R.id.id);
        transactionid = root.findViewById(R.id.transactionid);
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        bundle = getArguments();
        if (bundle != null) {
            save_homework.setVisibility(View.GONE);
            edit_homework.setVisibility(View.VISIBLE);
            if (bundle.containsKey("HomeworkDate")) {
                try {
                    String date = bundle.getString("HomeworkDate").replace("T00:00:00", "");
                    Date d = actualdate.parse(date);
                    if (d != null) {
                        homework_date.setText("" + displaydate.format(d));
                    }
                    indate = date;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (!(bundle.getString("Remarks") == null)) {
                remarks.setText(bundle.getString("Remarks"));
            }
            if (!(bundle.getString("FilePath") == null)) {
                attachment_homework.setText("Attached");
                attachment_homework.setTextColor(context.getResources().getColor(R.color.black));
                if (bundle.getString("FilePath").contains(".") && bundle.getString("FilePath").contains("/")) {
                    Extension = bundle.getString("FilePath").substring(bundle.getString("FilePath").lastIndexOf(".") + 1);
                    String FileNameWithExtension = bundle.getString("FilePath").substring(bundle.getString("FilePath").lastIndexOf("/") + 1);
                    String[] FileNameArray = FileNameWithExtension.split("\\.");
                    RandomFileName = FileNameArray[0];
                }
            }
            if (bundle.containsKey("FileName")) {
                OriginFileName = bundle.getString("FileName");
            }
            if (bundle.containsKey("TransactionId")) {
                transactionid.setText("" + bundle.getLong("TransactionId"));
            }
        }

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllStandard();
            GetAllSubject();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        attachment_homework.setOnClickListener(v -> {
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

        save_homework.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (homework_date.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Select Homework Date.", Toast.LENGTH_SHORT).show();
                } else if (branch.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Branch.", Toast.LENGTH_SHORT).show();
                } else if (standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (instrumentFileDestination == null) {
                    Toast.makeText(context, "Please Upload Homework.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    if (!remarks.getText().toString().isEmpty()){
                        Description = remarks.getText().toString();
                    }
                    Call<HomeworkModel.HomeworkData1> call = apiCalling.HomeworkMaintenance(0
                            , indate, Long.parseLong(BranchID)
                            , StandardId, Long.parseLong(SubjectId), Integer.parseInt(BatchId), Description, Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID)
                            , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                            , 0, "0", "0", true, MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                    call.enqueue(new Callback<HomeworkModel.HomeworkData1>() {
                        @Override
                        public void onResponse(@NotNull Call<HomeworkModel.HomeworkData1> call, @NotNull Response<HomeworkModel.HomeworkData1> response) {
                            if (response.isSuccessful()) {
                                HomeworkModel.HomeworkData1 data = response.body();
                                if (data.isCompleted()) {
                                    HomeworkModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context,data.getMessage(), Toast.LENGTH_SHORT).show();
                                        homework_Listfragment orderplace = new homework_Listfragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    } else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }else
                                {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<HomeworkModel.HomeworkData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_homework.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (homework_date.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Select Homework Date.", Toast.LENGTH_SHORT).show();
                } else if (branch.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Branch.", Toast.LENGTH_SHORT).show();
                } else if (standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (attachment_homework.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Upload Homework.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    Call<HomeworkModel.HomeworkData1> call;
                    if (!remarks.getText().toString().isEmpty()){
                        Description = remarks.getText().toString();
                    }
                    if (instrumentFileDestination != null) {
                        call = apiCalling.HomeworkMaintenance(bundle.getLong("HomeworkID")
                                , indate, Long.parseLong(BranchID)
                                , StandardId, Long.parseLong(SubjectId), Integer.parseInt(BatchId)
                                , Description, Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID)
                                , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                                , Long.parseLong(transactionid.getText().toString()), "0", "0", true, MultipartBody.Part.createFormData("", instrumentFileDestination.getName()
                                        , RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination)));
                    } else {
                        FinalFileName = OriginFileName + "," + RandomFileName;
                        call = apiCalling.HomeworkMaintenance(bundle.getLong("HomeworkID")
                                , indate, Long.parseLong(BranchID)
                                , StandardId, Long.parseLong(SubjectId), Integer.parseInt(BatchId)
                                ,Description, Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID)
                                , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                                , Long.parseLong(transactionid.getText().toString()), FinalFileName, Extension, false, MultipartBody.Part.createFormData("attachment", ""
                                        , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                    }
                    call.enqueue(new Callback<HomeworkModel.HomeworkData1>() {
                        @Override
                        public void onResponse(@NotNull Call<HomeworkModel.HomeworkData1> call, @NotNull Response<HomeworkModel.HomeworkData1> response) {
                            if (response.isSuccessful()) {
                                HomeworkModel.HomeworkData1 data = response.body();
                                if (data.isCompleted()) {
                                    HomeworkModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        homework_Listfragment orderplace = new homework_Listfragment();
                                        FragmentManager fragmentManager = getFragmentManager();
                                        FragmentTransaction fragmentTransaction = Objects.requireNonNull(fragmentManager).beginTransaction();
                                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();
                                    } else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }else
                                {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<HomeworkModel.HomeworkData1> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });


        selectbatch_time();

        homework_date.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        homework_date.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                        String as = homework_date.getText().toString();
                        Date dt;
                        try {
                            dt = displaydate.parse(as);
                            indate = actualdate.format(Objects.requireNonNull(dt));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }, year, month, day);
            picker.show();
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                homework_Listfragment profileFragment = new homework_Listfragment();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage();
            }
        }
    }

    @SuppressLint("SetTextI18n")
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
                    InputStream imageStream;
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireActivity(), uri);
                    instrumentFileDestination = new File(Objects.requireNonNull(Path));
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    attachment_homework.setText("Attached");
                    attachment_homework.setTextColor(context.getResources().getColor(R.color.black));
                    filename = instrumentFileDestination.getName();
                    attach = encodeFileToBase64Binary(instrumentFileDestination);
                } catch (Exception e) {
                    errored();
                }
            } else {
                errored();
            }
        }
    }

    private String encodeFileToBase64Binary(File yourFile) {
        int size = (int) yourFile.length();
        byte[] bytes = new byte[size];
        String encoded = "";
        try {
            BufferedInputStream buf = new BufferedInputStream(new FileInputStream(yourFile));
            buf.read(bytes, 0, bytes.length);
            buf.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        encoded = Base64.encodeToString(bytes, Base64.DEFAULT);
        return encoded;
    }

    private void pickImage() {
        try {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
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

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
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
        if (bundle != null) {
            int a = branchid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("BranchID"))));
            branch.setSelection(a);
        }
        branch.setOnItemSelectedListener(onItemSelectedListener6);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BranchName = branchitem.get(position);
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


    public void GetAllSubject() {
        subjectitem.add("Select Subject");
        subjectid.add(0);
        Call<SubjectData> call = apiCalling.GetAllSubject(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SubjectData>() {
            @Override
            public void onResponse(@NotNull Call<SubjectData> call, @NotNull Response<SubjectData> response) {
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
            public void onFailure(@NotNull Call<SubjectData> call, @NotNull Throwable t) {
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
            int b = subjectid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("SubjectID"))));
            subject.setSelection(b);
        }
        subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener8 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectName = subjectitem.get(position);
                    SubjectId = subjectid.get(position).toString();
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
        if (bundle != null) {
            int c = standardid.indexOf(Integer.parseInt(String.valueOf(bundle.getLong("StandardID"))));
            standard.setSelection(c);
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
        if (bundle != null) {
            if (bundle.containsKey("BatchTimeText")) {
                selectSpinnerValue(batch_time, bundle.getString("BatchTimeText"));
            }
        }
        batch_time.setOnItemSelectedListener(onItemSelectedListener77);
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
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

}