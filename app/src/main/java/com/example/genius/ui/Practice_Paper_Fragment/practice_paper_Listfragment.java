package com.example.genius.ui.Practice_Paper_Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.*;
import com.example.genius.Model.SubjectData;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
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
public class practice_paper_Listfragment extends Fragment {

    SearchableSpinner standard, subject, branch, batch_time;
    TextView attach_paper, text, id, photo, paper_id, uniq_id;
    Button save_practice_paper, edit_practice_paper;
    ImageView imageView;
    Context context;
    RecyclerView practice_paper_rv;
    PracticePaperMaster_Adapter practicePaperMaster_adapter;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(), branchitem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(), branchid = new ArrayList<>();
    String[] STANDARDITEM, SUBJECTITEM, BRANCHITEM, BATCHITEM;
    Integer[] STANDARDID, SUBJECTID, BRANCHID;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    String StandardName, SubjectName, BatchTime, BranchName, BranchID, BatchId, SubjectId;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    int flag = 0;
    Long StandardId;
    OnBackPressedCallback callback;
    NestedScrollView paper_scroll;
    String attach = "", path = "",Description = "none", Extension,FinalFileName,OriginFileName,RandomFileName;
    EditText remarks;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Practice Paper Entry");
        View root = inflater.inflate(R.layout.practice_paper__listfragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        standard = root.findViewById(R.id.standard);
        subject = root.findViewById(R.id.subject);
        branch = root.findViewById(R.id.branch);
        batch_time = root.findViewById(R.id.batch_time);
        attach_paper = root.findViewById(R.id.attach_paper);
        save_practice_paper = root.findViewById(R.id.save_practice_paper);
        edit_practice_paper = root.findViewById(R.id.edit_practice_paper);
        practice_paper_rv = root.findViewById(R.id.practice_paper_rv);
        text = root.findViewById(R.id.text);
        imageView = root.findViewById(R.id.imageView);
        id = root.findViewById(R.id.id);
        photo = root.findViewById(R.id.photo);
        paper_scroll = root.findViewById(R.id.paper_scroll);
        paper_id = root.findViewById(R.id.paper_id);
        uniq_id = root.findViewById(R.id.uniq_id);
        remarks = root.findViewById(R.id.remarks);
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllStandard();
            GetAllSubject();
            selectbatch_time();
            GetPracticePaperDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        save_practice_paper.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (batch_time.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (attach_paper.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Upload Practice Paper.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    if (!remarks.getText().toString().isEmpty()){
                        Description = encodeDecode(remarks.getText().toString());
                    }
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                    MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                    Call<PaperModel.PaperData1> call = apiCalling.PaperMaintenance(0,0,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),StandardId,
                            Long.parseLong(SubjectId),Integer.parseInt(BatchId),Description,Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0,
                            "0","0",true,uploadfile);
                    call.enqueue(new Callback<PaperModel.PaperData1>() {
                        @Override
                        public void onResponse(@NotNull Call<PaperModel.PaperData1> call, @NotNull Response<PaperModel.PaperData1> response) {
                            if (response.isSuccessful()) {
                                PaperModel.PaperData1 data = response.body();
                                if (data != null && data.isCompleted()) {
                                    PaperModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        GetPracticePaperDetails();
                                        subject.setSelection(0);
                                        branch.setSelection(0);
                                        standard.setSelection(0);
                                        batch_time.setSelection(0);
                                        attach_paper.setText("");
                                        remarks.setText("");
                                    } else {
                                        Toast.makeText(context, "Practice Paper not Inserted...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<PaperModel.PaperData1> call, @NotNull Throwable t) {
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

        edit_practice_paper.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (subject.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                } else if (batch_time.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                } else if (attach_paper.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Upload Practice Paper.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    if (!remarks.getText().toString().isEmpty()){
                        Description = encodeDecode(remarks.getText().toString());
                    }
                    Call<PaperModel.PaperData1> call;
                    if (instrumentFileDestination != null) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        call = apiCalling.PaperMaintenance(Long.parseLong(paper_id.getText().toString()),Long.parseLong(uniq_id.getText().toString()),Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),StandardId,
                                Long.parseLong(SubjectId),Integer.parseInt(BatchId),Description,Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0,
                                "0","0",true,uploadfile);
                    }else {
                        FinalFileName = OriginFileName + "," + RandomFileName;
                        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
                        call = apiCalling.PaperMaintenance(Long.parseLong(paper_id.getText().toString()),Long.parseLong(uniq_id.getText().toString()),Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),StandardId,
                                Long.parseLong(SubjectId),Integer.parseInt(BatchId),Description,Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0,
                                FinalFileName,Extension,false,uploadfile);
                    }
                    call.enqueue(new Callback<PaperModel.PaperData1>() {
                        @Override
                        public void onResponse(@NotNull Call<PaperModel.PaperData1> call, @NotNull Response<PaperModel.PaperData1> response) {
                            if (response.isSuccessful()) {
                                PaperModel.PaperData1 data = response.body();
                                if (data != null && data.isCompleted()) {
                                    PaperModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        GetPracticePaperDetails();
                                        subject.setSelection(0);
                                        branch.setSelection(0);
                                        standard.setSelection(0);
                                        batch_time.setSelection(0);
                                        attach_paper.setText("");
                                        remarks.setText("");
                                        save_practice_paper.setVisibility(View.VISIBLE);
                                        edit_practice_paper.setVisibility(View.GONE);
                                    } else {
                                        Toast.makeText(context, "Practice Paper not Updated...!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<PaperModel.PaperData1> call, @NotNull Throwable t) {
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

        attach_paper.setOnClickListener(v -> requestPermissionForAll());


        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                home_fragment profileFragment = new home_fragment();
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

    private void requestPermissionForAll() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean hasPermission = (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);

            if (!hasPermission) {
                ActivityCompat.requestPermissions(requireActivity(),
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
                    InputStream imageStream;
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    if (Path != null) {
                        instrumentFileDestination = new File(Path);
                    }
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    attach_paper.setText("Attached");
                    attach_paper.setTextColor(context.getResources().getColor(R.color.black));
                    path = instrumentFileDestination.getName();
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

    public void userCancelled() {

    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
    }

    public void GetAllBranch() {
        branchitem.clear();
        branchid.clear();
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
        subjectitem.clear();
        subjectid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);
        Call<SubjectData> call = apiCalling.GetAllSubject(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SubjectData>() {
            @Override
            public void onResponse(@NotNull Call<SubjectData> call, @NotNull Response<SubjectData> response) {
                if (response.isSuccessful()) {
                    //progressBarHelper.hideProgressDialog();
                    SubjectData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<SubjectModel> respose = standardData.getData();
                            if (respose.size() > 0) {
                                List<SubjectModel> list = new ArrayList<>();
                                for (SubjectModel singleResponseModel : respose) {

                                    String std = singleResponseModel.getSubject();
                                    subjectitem.add(std);

                                    int stdid = Integer.parseInt(String.valueOf(singleResponseModel.getSubjectID()));
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
        standarditem.clear();
        standardid.clear();
        standarditem.add("Select Standard");
        standardid.add(0);

        Call<StandardData> call = apiCalling.GetAllStandard(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StandardData>() {
            @Override
            public void onResponse(@NotNull Call<StandardData> call, @NotNull Response<StandardData> response) {
                if (response.isSuccessful()) {
                    //progressBarHelper.hideProgressDialog();
                    StandardData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<StandardModel> respose = standardData.getData();
                            for (StandardModel singleResponseModel : respose) {

                                String std = singleResponseModel.getStandard();
                                standarditem.add(std);

                                int stdid = Integer.parseInt(String.valueOf(singleResponseModel.getStandardID()));
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
        batchid.clear();
        batchitem.clear();
        batchitem.add("Select Batch Time");
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
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener77 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BatchTime = batchitem.get(position);
                    BatchId = batchid.get(position);
                    if (batch_time.getSelectedItem().equals("Select Batch Time")) {
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

    public void GetPracticePaperDetails() {
        Call<PaperData> call = apiCalling.GetAllPaperWithoutContent(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<PaperData>() {
            @Override
            public void onResponse(@NotNull Call<PaperData> call, @NotNull Response<PaperData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    PaperData paperData = response.body();
                    if (paperData.isCompleted()) {
                        List<PaperModel> paperModelList = paperData.getData();
                        if (paperModelList != null) {
                            if (paperModelList.size() > 0) {
                                List<PaperModel> list = new ArrayList<>();
                                for (PaperModel singlemodel : paperModelList) {
                                    if (singlemodel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(singlemodel);
                                    }
                                }
                                text.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                practice_paper_rv.setLayoutManager(linearLayoutManager);
                                practicePaperMaster_adapter = new PracticePaperMaster_Adapter(context, list);
                                practicePaperMaster_adapter.notifyDataSetChanged();
                                practice_paper_rv.setAdapter(practicePaperMaster_adapter);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<PaperData> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public class PracticePaperMaster_Adapter extends RecyclerView.Adapter<PracticePaperMaster_Adapter.ViewHolder> {

        Context context;
        List<PaperModel> paperModels;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        byte[] imageVal;
        long downloadID;
        String Name;

        public PracticePaperMaster_Adapter(Context context, List<PaperModel> paperModels) {
            this.context = context;
            this.paperModels = paperModels;
        }

        @NonNull
        @Override
        public PracticePaperMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new PracticePaperMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.practicepaper_master_deatil_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull PracticePaperMaster_Adapter.ViewHolder holder, int position) {
            holder.standard.setText(paperModels.get(position).getStandard().getStandard());
            holder.subject.setText(paperModels.get(position).getSubject().getSubject());
            int a = paperModels.get(position).getBatchTypeID();
            if (a == 1) {
                holder.batch_time.setText("Morning");
            } else if (a == 2) {
                holder.batch_time.setText("AfterNoon");
            } else {
                holder.batch_time.setText("Evening");
            }

            holder.paper_edit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Practice Paper?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v13 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v14 -> {
                    dialog.dismiss();
                    progressBarHelper.showProgressDialog();
                    Call<PaperByIdData> call = apiCalling.GetPaperByPaperID(paperModels.get(position).getPaperID());
                    call.enqueue(new Callback<PaperByIdData>() {
                        @Override
                        public void onResponse(@NotNull Call<PaperByIdData> call, @NotNull Response<PaperByIdData> response) {
                            if (response.isSuccessful()) {
                                progressBarHelper.hideProgressDialog();
                                PaperByIdData paperData = response.body();
                                if (paperData.Completed) {
                                    PaperModel paperModelList = paperData.Data;
                                    if (paperModelList != null) {
                                        save_practice_paper.setVisibility(View.GONE);
                                        edit_practice_paper.setVisibility(View.VISIBLE);
                                        int cb = Integer.parseInt(String.valueOf(paperModels.get(position).getBranch().getBranchID()));
                                        int aa = branchid.indexOf(cb);
                                        branch.setSelection(aa);
                                        int b = Integer.parseInt(String.valueOf(paperModels.get(position).getStandard().getStandardID()));
                                        int bb = standardid.indexOf(b);
                                        standard.setSelection(bb);
                                        int c = Integer.parseInt(String.valueOf(paperModels.get(position).getSubject().getSubjectID()));
                                        int cc = subjectid.indexOf(c);
                                        subject.setSelection(cc);
                                        int d = batchid.indexOf(String.valueOf(paperModels.get(position).getBatchTypeID()));
                                        batch_time.setSelection(d);
                                        attach = paperModelList.getPaperData().getPaperContentText();
                                        path = paperModels.get(position).getPaperData().getPaperPath();
                                        attach_paper.setText("Attached");
                                        if (paperModels.get(position).getPaperData().getFilePath().contains(".") && paperModels.get(position).getPaperData().getFilePath().contains("/")) {
                                            Extension = paperModels.get(position).getPaperData().getFilePath().substring(paperModels.get(position).getPaperData().getFilePath().lastIndexOf(".") + 1);
                                            String FileNameWithExtension = paperModels.get(position).getPaperData().getFilePath().substring(paperModels.get(position).getPaperData().getFilePath().lastIndexOf("/") + 1);
                                            String[] FileNameArray = FileNameWithExtension.split("\\.");
                                            RandomFileName = FileNameArray[0];
                                        }
                                        attach_paper.setTextColor(context.getResources().getColor(R.color.black));
                                        uniq_id.setText("" + paperModels.get(position).getPaperData().getUniqueID());
                                        paper_id.setText("" + paperModels.get(position).getPaperID());
                                        id.setText("" + paperModels.get(position).getTransaction().getTransactionId());
                                        remarks.setText(paperModels.get(position).getRemarks());
                                        OriginFileName = paperModels.get(position).getPaperData().getPaperPath();
                                        paper_scroll.scrollTo(0, 0);
                                        paper_scroll.fullScroll(View.FOCUS_UP);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<PaperByIdData> call, @NotNull Throwable t) {
                            Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                });
                dialog.show();
            });

            holder.paper_delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this Practice Paper?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(v15 -> dialog.dismiss());

                btn_delete.setOnClickListener(v16 -> {
                    dialog.dismiss();
                    if (Function.checkNetworkConnection(context)) {
                        progressBarHelper.showProgressDialog();
                        Call<CommonModel> call = apiCalling.RemovePaper(paperModels.get(position).getPaperID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        call.enqueue(new Callback<CommonModel>() {
                            @Override
                            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                                if (response.isSuccessful()) {
                                    CommonModel model = response.body();
                                    if (model != null && model.isCompleted()) {
                                        if (model.isData()) {
                                            Toast.makeText(context, "Practice Paper deleted Successfully.", Toast.LENGTH_SHORT).show();
                                            paperModels.remove(position);
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
                    } else {
                        Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();
            });

            holder.paper_download.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Download Document?");
                image.setImageResource(R.drawable.download);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    String filetype = paperModels.get(position).getPaperData().getFilePath();
                    String filetyp = filetype.substring(filetype.lastIndexOf("."));
                    Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
                    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(filetype);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    if (paperModels.get(position).getSubject().getSubject() != null) {
                        Name = "Practice_Paper" + "_" + paperModels.get(position).getSubject().getSubject() + filetyp;
                    } else {
                        Name = "Practice_Paper" + filetyp;
                    }
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/AshirvadStudyCircle/" + Name);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

                    downloadID = dm.enqueue(request);
                    context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                });
                dialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return paperModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView standard, subject, batch_time;
            ImageView paper_edit, paper_delete, paper_download;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                standard = itemView.findViewById(R.id.standard);
                subject = itemView.findViewById(R.id.subject);
                batch_time = itemView.findViewById(R.id.batch_time);
                paper_edit = itemView.findViewById(R.id.paper_edit);
                paper_delete = itemView.findViewById(R.id.paper_delete);
                paper_download = itemView.findViewById(R.id.paper_download);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }

        private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context1, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID == id) {
                    Toast.makeText(context, "Download " + Name + " Completed And Stored In AshirvadStudyCircle Folder...", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    public String encodeDecode(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replace("\n", "");
    }
}