package com.example.genius.ui.Marks_Entry_Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
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
import android.widget.LinearLayout;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.MarksEnterAdapter;
import com.example.genius.Model.*;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Staff_Entry_Fragment.staff_entry_listfragment;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

public class marks_entry_fragment extends Fragment {

    SearchableSpinner standard, batch_time, subject, branch, test_date;
    EditText remarks, total_marks;
    TextView upload_image,txt_nodata;
    RecyclerView marks_rv;
    Button save_test_marks, edit_test_marks, btnsearch_student;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(), branchitem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>(), dateitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(), branchid = new ArrayList<>(), dateid = new ArrayList<>();
    String[] STANDARDITEM, SUBJECTITEM, BRANCHITEM, BATCHITEM, DATEITEM;
    Integer[] STANDARDID, SUBJECTID, BRANCHID, BATCHID, DATEID;
    String StandardName, SubjectName, BatchTime, BranchName,BranchID,SubjectId,BatchId,TestDate;
    Long StandardId,TestID;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    int flag = 0;
    LinearLayout linear_line;
    OnBackPressedCallback callback;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    byte[] imageVal;
    Bitmap bitmap;
    String pictureFilePath,Achieve_Marks = "",StudentID = "",Subject_Date,Marks_Date;
    boolean marksentered;
    MarksEnterAdapter marksEnterAdapter;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Marks Entry");
        View root = inflater.inflate(R.layout.marks_entry_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        standard = root.findViewById(R.id.standard);
        batch_time = root.findViewById(R.id.batch_time);
        subject = root.findViewById(R.id.subject);
        test_date = root.findViewById(R.id.test_date);
        total_marks = root.findViewById(R.id.total_marks);
        remarks = root.findViewById(R.id.remarks);
        upload_image = root.findViewById(R.id.upload_image);
        save_test_marks = root.findViewById(R.id.save_test_marks);
        edit_test_marks = root.findViewById(R.id.edit_test_marks);
        branch = root.findViewById(R.id.branch);
        btnsearch_student = root.findViewById(R.id.btnsearch_student);
        marks_rv = root.findViewById(R.id.marks_rv);
        linear_line = root.findViewById(R.id.linear_line);
        txt_nodata = root.findViewById(R.id.txt_nodata);

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllStandard();
            SelectTestDate();
            SelectSubject();
            selectbatch_time();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        upload_image.setOnClickListener(new View.OnClickListener() {
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
                        showAddProfilePicDialog();
                    }
                } else {
                    showAddProfilePicDialog();
                }
            }
        });

        btnsearch_student.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.checkNetworkConnection(context)) {
                    if (standard.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                    else if (batch_time.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                    else if (test_date.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Test Date.", Toast.LENGTH_SHORT).show();
                    else if (subject.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                    else {
                        if (marksentered){
                            Toast.makeText(context, "Marks Already inserted for this Test !", Toast.LENGTH_LONG).show();
                        }else {
                            progressBarHelper.showProgressDialog();
                            Call<StudentModel.StudentDataList> call = apiCalling.Get_Student_Details(StandardId,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),Long.parseLong(BatchId));
                            call.enqueue(new Callback<StudentModel.StudentDataList>() {
                                @Override
                                public void onResponse(Call<StudentModel.StudentDataList> call, Response<StudentModel.StudentDataList> response) {
                                    if (response.isSuccessful()){
                                        StudentModel.StudentDataList data = response.body();
                                        if (data.isCompleted()){
                                            List<StudentModel> list = data.getData();
                                            if (list != null && list.size() > 0){
                                                marks_rv.setVisibility(View.VISIBLE);
                                                txt_nodata.setVisibility(View.GONE);
                                                save_test_marks.setVisibility(View.VISIBLE);
                                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                                marks_rv.setLayoutManager(linearLayoutManager);
                                                marksEnterAdapter = new MarksEnterAdapter(context, list);
                                                marksEnterAdapter.notifyDataSetChanged();
                                                marks_rv.setAdapter(marksEnterAdapter);
                                            }else {
                                                marks_rv.setVisibility(View.GONE);
                                                txt_nodata.setVisibility(View.VISIBLE);
                                                save_test_marks.setVisibility(View.GONE);
                                            }
                                        }
                                        progressBarHelper.hideProgressDialog();
                                    }
                                }

                                @Override
                                public void onFailure(Call<StudentModel.StudentDataList> call, Throwable t) {
                                    progressBarHelper.hideProgressDialog();
                                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        save_test_marks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.checkNetworkConnection(context)) {
                    if (standard.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                    else if (batch_time.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                    else if (test_date.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Test Date.", Toast.LENGTH_SHORT).show();
                    else if (upload_image.getText().toString().isEmpty())
                        Toast.makeText(context, "Please Upload Solution Image.", Toast.LENGTH_SHORT).show();
                    else {
                        progressBarHelper.showProgressDialog();
                        if (MarksEnterAdapter.studentModels.size() > 0){
                            for (int i = 0; i < MarksEnterAdapter.studentModels.size(); i++){
                                String id = String.valueOf(MarksEnterAdapter.studentModels.get(i).getStudentID());
                                String mks = MarksEnterAdapter.studentModels.get(i).getAchieveMarks();
                                StudentID = StudentID + "," + id;
                                Achieve_Marks = Achieve_Marks + "," + mks;
                            }
                        }
                        StudentID = StudentID.substring(1);
                        Achieve_Marks = Achieve_Marks.substring(1);
                        try {
                            Date d = displaydate.parse(TestDate);
                            Marks_Date = actualdate.format(d);
                        }catch (Exception e){

                        }
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        Call<MarksModel.MarksData> call = apiCalling.MarksMaintenance(0,Marks_Date,TestID,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                                , StudentID,Achieve_Marks,Integer.parseInt(BatchId),Long.parseLong(SubjectId),Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0,
                                "0","0",true,uploadfile);
                        call.enqueue(new Callback<MarksModel.MarksData>() {
                            @Override
                            public void onResponse(Call<MarksModel.MarksData> call, Response<MarksModel.MarksData> response) {
                                if (response.isSuccessful()){
                                    MarksModel.MarksData data = response.body();
                                    if (data.isCompleted()){
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        marks_entry_Listfragment profileFragment = new marks_entry_Listfragment();
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.nav_host_fragment, profileFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<MarksModel.MarksData> call, Throwable t) {
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
                marks_entry_Listfragment profileFragment = new marks_entry_Listfragment();
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

    private void showAddProfilePicDialog()
    {
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
                    imageVal = null;
                   /* Bitmap image = (Bitmap) Objects.requireNonNull(result.getExtras()).get("data");
                    imageView.setVisibility(View.VISIBLE);
                    imageView.setImageBitmap(image);
                    imageVal = ImageUtility.using(getActivity()).toBase64FromBitmap(image);
                   */
                    upload_image.setText("Attached");
                    upload_image.setTextColor(context.getResources().getColor(R.color.black));
                    instrumentFileDestination = new File(pictureFilePath);
                    try{
//                        imageView.setImageURI(Uri.fromFile(instrumentFileDestination));
                    }catch (Exception e){

                    }
                    Toast.makeText(context, ""+instrumentFileDestination, Toast.LENGTH_SHORT).show();
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
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    InputStream imageStream;
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    upload_image.setText("Attached");
                    upload_image.setTextColor(context.getResources().getColor(R.color.black));
                    onGalleryImageResultInstrument(result);
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

    private File getPictureFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        String pictureFile = "ZOFTINO_" + timeStamp;
        File storageDir =  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File image = File.createTempFile(pictureFile,  ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void takePic() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION,true);
//        startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        File pictureFile = null;
        try {
            pictureFile = getPictureFile();
        } catch (IOException ex) {

            return;
        }
        if (pictureFile != null) {
            Uri photoURI = FileProvider.getUriForFile(context,"com.ashirvad.genius.provider",pictureFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        }
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

    public void GetAllBranch()
    {
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
                    }
                    else{
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };


    public void GetAllSubject() {
        progressBarHelper.showProgressDialog();
        subjectitem.clear();
        subjectid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);
        try {
            Date d = displaydate.parse(TestDate);
            Subject_Date = actualdate.format(d);
        }catch (Exception e){

        }
        Call<SubjectData> call = apiCalling.GetAllSubjectByTestDate(Subject_Date);
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
                    }
                    else{
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (subject.getSelectedItemId() != 0){
                        GetTestDetails();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetTestDetails()
    {
        progressBarHelper.showProgressDialog();
        Call<TestScheduleModel.TestScheduleData1> call = apiCalling.Get_Test_Details(TestID,Long.parseLong(SubjectId));
        call.enqueue(new Callback<TestScheduleModel.TestScheduleData1>() {
            @Override
            public void onResponse(Call<TestScheduleModel.TestScheduleData1> call, Response<TestScheduleModel.TestScheduleData1> response) {
                if (response.isSuccessful()){
                    TestScheduleModel.TestScheduleData1 data = response.body();
                    if (data.isCompleted()){
                        TestScheduleModel model = data.getData();
                        total_marks.setText(""+model.getMarks());
                        remarks.setText(model.getRemarks());
                        marksentered = model.isMarksentered();
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<TestScheduleModel.TestScheduleData1> call, Throwable t) {
                progressBarHelper.showProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

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

    public void selectbatch_time()
    {
        batchitem.clear();
        batchid.clear();
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
                    }
                    else{
                        try {
                            ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                            ((TextView) parent.getChildAt(0)).setTextSize(14);
                        } catch (Exception e) {
                        }
                    }
                    if (batch_time.getSelectedItemId() != 0){
                        GetTestDates();
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetTestDates()
    {
        progressBarHelper.showProgressDialog();
        dateitem.clear();
        dateid.clear();
        dateitem.add("Test Date");
        dateid.add(0);
        Call<MarksModel.MarksData> call = apiCalling.Get_Test_Marks(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),StandardId,Integer.parseInt(BatchId));
        call.enqueue(new Callback<MarksModel.MarksData>() {
            @Override
            public void onResponse(Call<MarksModel.MarksData> call, Response<MarksModel.MarksData> response) {
                if (response.isSuccessful()){
                    MarksModel.MarksData data = response.body();
                    if (data.isCompleted() && data != null){
                        List<MarksModel> model = data.getData();
                        for (MarksModel marksModel : model) {

                            String testdate = marksModel.getTestDate();
                            try {
                                Date d = actualdate.parse(testdate);
                                String date = displaydate.format(d);
                                dateitem.add(date);
                            }catch (Exception e){

                            }

                            int id = (int) marksModel.getTestID();
                            dateid.add(id);
                        }

                        DATEITEM = new String[dateitem.size()];
                        DATEITEM = dateitem.toArray(DATEITEM);

                        DATEID = new Integer[dateid.size()];
                        DATEID = dateid.toArray(DATEID);

                        bindDate();
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<MarksModel.MarksData> call, Throwable t) {

            }
        });
    }

    public void bindDate() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, DATEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        test_date.setAdapter(adapter);
        test_date.setOnItemSelectedListener(onItemSelectedListenerdate);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListenerdate =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TestDate = dateitem.get(position);
                    TestID = Long.parseLong(dateid.get(position).toString());
                    if (test_date.getSelectedItem().equals("Test Date")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (test_date.getSelectedItemId() != 0){
                        GetAllSubject();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void userCancelled() {

    }

    public void errored() {
        Intent intent = new Intent();
        intent.putExtra(ERROR, true);
        intent.putExtra(ERROR_MSG, "Error while opening the image file. Please try again.");
    }

    private void onGalleryImageResultInstrument(Intent data) {
        Bitmap rotatedBitmap;
        Bitmap bitmap = null;

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
//                imageVal = ImageUtility.using(context).toBase64(instrumentFileDestination.getPath());
            }
        }
    }

    private String encodeImage(byte[] b) {
        return Base64.encodeToString(b, Base64.DEFAULT);
    }

    public void SelectTestDate()
    {
        dateitem.clear();
        dateitem.add("Test Date");

        DATEITEM = new String[dateitem.size()];
        DATEITEM = dateitem.toArray(DATEITEM);

        bindTestDate();
    }

    public void bindTestDate() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, DATEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        test_date.setAdapter(adapter);
        test_date.setOnItemSelectedListener(onItemSelectedListener80);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener80 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    ((TextView) parent.getChildAt(0)).setTextSize(13);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void SelectSubject()
    {
        subjectitem.clear();
        subjectitem.add("Select Subject");

        SUBJECTITEM = new String[subjectitem.size()];
        SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

        bindselectsubject();
    }

    public void bindselectsubject() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        subject.setAdapter(adapter);
        subject.setOnItemSelectedListener(onItemSelectedListener90);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener90 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                    ((TextView) parent.getChildAt(0)).setTextSize(13);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
}