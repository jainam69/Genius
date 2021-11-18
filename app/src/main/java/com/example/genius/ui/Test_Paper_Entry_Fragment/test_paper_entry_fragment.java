package com.example.genius.ui.Test_Paper_Entry_Fragment;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;


import com.example.genius.API.ApiCalling;
import com.example.genius.Model.*;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class test_paper_entry_fragment extends Fragment {

    SearchableSpinner standard, batch_time, subject, branch;
    EditText test_date, start_time, end_time, total_marks, remarks, link;
    TextView upload_test_paper, id, paper, paper_name;
    RadioGroup rg_status, rg_type;
    RadioButton upload_doc, upload_link, active, inactive, rb1, rb2;
    Button save_test_paper, edit_test_paper;
    String type, status;
    RelativeLayout linear_doc, linear_link;
    Context context;
    int select;
    private int year;
    private int month;
    private int day;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(), branchitem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(), branchid = new ArrayList<>();
    String[] STANDARDITEM, SUBJECTITEM, BRANCHITEM, BATCHITEM;
    Integer[] STANDARDID, SUBJECTID, BRANCHID, BATCHID;
    String StandardName, SubjectName, BatchTime, BranchName, format, Ans, upload_paper, upload_paper_name, BatchId, SubjectId, BranchID, papername, paper_ext;
    int BranchId;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    int flag = 0;
    Boolean a;
    Bundle bundle;
    OnBackPressedCallback callback;
    Long StandardId;
    String FileName = "none", Extension = "jpg";

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.test_paper_entry_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        standard = root.findViewById(R.id.standard);
        batch_time = root.findViewById(R.id.batch_time);
        subject = root.findViewById(R.id.subject);
        branch = root.findViewById(R.id.branch);
        test_date = root.findViewById(R.id.test_date);
        start_time = root.findViewById(R.id.start_time);
        end_time = root.findViewById(R.id.end_time);
        total_marks = root.findViewById(R.id.total_marks);
        remarks = root.findViewById(R.id.remarks);
        link = root.findViewById(R.id.link);
        upload_test_paper = root.findViewById(R.id.upload_test_paper);
        rg_status = root.findViewById(R.id.rg_status);
        rg_type = root.findViewById(R.id.rg_type);
        upload_doc = root.findViewById(R.id.upload_doc);
        upload_link = root.findViewById(R.id.upload_link);
        active = root.findViewById(R.id.active);
        inactive = root.findViewById(R.id.inactive);
        save_test_paper = root.findViewById(R.id.save_test_paper);
        edit_test_paper = root.findViewById(R.id.edit_test_paper);
        linear_doc = root.findViewById(R.id.linear_doc);
        linear_link = root.findViewById(R.id.linear_link);
        id = root.findViewById(R.id.id);
        paper = root.findViewById(R.id.paper);
        paper_name = root.findViewById(R.id.paper_name);

        bundle = getArguments();
        if (bundle != null) {
            save_test_paper.setVisibility(View.GONE);
            edit_test_paper.setVisibility(View.VISIBLE);
            if (bundle.containsKey("ID")) {
                id.setText("" + bundle.getInt("ID"));
            }
            if (bundle.containsKey("TestDate")) {
                test_date.setText(bundle.getString("TestDate"));
            }
            if (bundle.containsKey("TestMarks")) {
                total_marks.setText(bundle.getString("TestMarks"));
            }
            if (bundle.containsKey("StartTime")) {
                start_time.setText(bundle.getString("StartTime"));
            }
            if (bundle.containsKey("EndTime")) {
                end_time.setText(bundle.getString("EndTime"));
            }
            if (bundle.containsKey("Type")) {
                String tp = bundle.getString("Type");
                if (tp.equals("UploadDocument")) {
                    upload_doc.setChecked(true);
                    upload_link.setChecked(false);
                    linear_doc.setVisibility(View.VISIBLE);
                    linear_link.setVisibility(View.GONE);
                }
                if (tp.equals("UploadLink")) {
                    upload_doc.setChecked(false);
                    upload_link.setChecked(true);
                    linear_doc.setVisibility(View.GONE);
                    linear_link.setVisibility(View.VISIBLE);
                }
            }
            if (bundle.containsKey("Status")) {
                String st = bundle.getString("Status");
                if (st.equals("Active")) {
                    active.setChecked(true);
                    inactive.setChecked(false);
                }
                if (st.equals("Inactive")) {
                    active.setChecked(false);
                    inactive.setChecked(true);
                }
            }
            if (bundle.containsKey("Paper")) {
                String p = bundle.getString("Paper");
                if (!(p.equals(" "))) {
                    upload_test_paper.setText("Attached");
                    upload_test_paper.setTextColor(context.getResources().getColor(R.color.black));
                }
                paper.setText(p);
            }
            if (bundle.containsKey("paperlink")) {
                link.setText(bundle.getString("paperlink"));
            }
            if (bundle.containsKey("PaperName")) {
                upload_paper_name = bundle.getString("PaperName");
                paper_name.setText(upload_paper_name);
            }
        }

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllBranch();
            GetAllStandard();
            GetAllSubject();
            selectbatch_time();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        rg_type.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb1 = root.findViewById(checkedId);
                type = rb1.getText().toString();
                if (type.equals("UploadDocument")) {
                    linear_doc.setVisibility(View.VISIBLE);
                    linear_link.setVisibility(View.GONE);
                }
                if (type.equals("UploadLink")) {
                    linear_link.setVisibility(View.VISIBLE);
                    linear_doc.setVisibility(View.GONE);
                }
            }
        });
        select = rg_type.getCheckedRadioButtonId();
        rb1 = root.findViewById(select);
        type = rb1.getText().toString();

        rg_status.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                rb2 = root.findViewById(checkedId);
                status = rb2.getText().toString();
            }
        });
        select = rg_status.getCheckedRadioButtonId();
        rb2 = root.findViewById(select);
        status = rb2.getText().toString();

        upload_test_paper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionForAll();
            }
        });

        test_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();

                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year2, int monthOfYear, int dayOfMonth) {
                                year = year2;
                                month = monthOfYear;
                                day = dayOfMonth;
                                test_date.setText(String.valueOf(new StringBuilder().append(pad(day)).append("/").append(pad(month + 1)).append("/").append(year)));
                            }
                        }, year, month, day);
                picker.show();
            }
        });

        start_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                if (hourOfDay == 0) {
                                    hourOfDay += 12;
                                    format = "AM";
                                } else if (hourOfDay == 12) {
                                    format = "PM";
                                } else if (hourOfDay > 12) {
                                    hourOfDay -= 12;
                                    format = "PM";
                                } else {
                                    format = "AM";
                                }
                                start_time.setText(hourOfDay + ":" + minute + " " + format);
                            }
                        }, hour, minute, false);
                timePickerDialog.show();
            }
        });

        end_time.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute1) -> {
                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";
                        } else if (hourOfDay == 12) {
                            format = "PM";
                        } else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        } else {
                            format = "AM";
                        }
                        end_time.setText(hourOfDay + ":" + minute1 + " " + format);
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        save_test_paper.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (branch.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Branch.", Toast.LENGTH_SHORT).show();
                else if (standard.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                else if (batch_time.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                else if (subject.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                else if (test_date.getText().toString().equals(""))
                    Toast.makeText(context, "Please Select Date.", Toast.LENGTH_SHORT).show();
                else if (total_marks.getText().toString().equals(""))
                    Toast.makeText(context, "Please Enter Total Marks.", Toast.LENGTH_SHORT).show();
                else if (type.equals("UploadDocument") && upload_test_paper.getText().toString().equals(""))
                    Toast.makeText(context, "Please Upload Document.", Toast.LENGTH_SHORT).show();
                else if (type.equals("UploadLink") && link.getText().toString().equals(""))
                    Toast.makeText(context, "Please Enter Link.", Toast.LENGTH_SHORT).show();
                else {

                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_test_paper.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (branch.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Branch.", Toast.LENGTH_SHORT).show();
                else if (standard.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                else if (batch_time.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                else if (subject.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                else if (test_date.getText().toString().equals(""))
                    Toast.makeText(context, "Please Select Date.", Toast.LENGTH_SHORT).show();
                else if (total_marks.getText().toString().equals(""))
                    Toast.makeText(context, "Please Enter Total Marks.", Toast.LENGTH_SHORT).show();
                else if (type.equals("UploadDocument") && upload_test_paper.getText().toString().equals(""))
                    Toast.makeText(context, "Please Upload Document.", Toast.LENGTH_SHORT).show();
                else if (type.equals("UploadLink") && link.getText().toString().equals(""))
                    Toast.makeText(context, "Please Enter Link.", Toast.LENGTH_SHORT).show();
                else {

                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });


        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                Test_Paper_Listfragment profileFragment = new Test_Paper_Listfragment();
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
            public void onResponse(Call<BranchModel> call, Response<BranchModel> response) {
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
            public void onFailure(Call<BranchModel> call, Throwable t) {
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
                        } catch (Exception e) {
                        }
                    } else {
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception e) {
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }

            };

    private void requestPermissionForAll() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

            if (!hasPermission) {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                        }, 1);
            } else {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select File"), 4);
            }
        } else {
            Intent intent = new Intent();
            intent.setType("*/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select File"), 4);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result) {
        super.onActivityResult(requestCode, resultCode, result);
        if (requestCode == 4) {
            if (resultCode == RESULT_CANCELED) {
                userCancelled();
            } else if (resultCode == RESULT_OK) {
                Uri image = result.getData();
                try {
                    flag = 1;
                    upload_paper = "";
                    InputStream imageStream;
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    upload_test_paper.setText("Attached");
                    upload_test_paper.setTextColor(context.getResources().getColor(R.color.black));
                    String name = instrumentFileDestination.getName();
                    paper_ext = name.substring(name.lastIndexOf("."));
                    papername = instrumentFileDestination.getName();
                    upload_paper = encodeFileToBase64Binary(instrumentFileDestination);
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

        String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
        return encoded;
    }

    public void userCancelled() {

    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
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