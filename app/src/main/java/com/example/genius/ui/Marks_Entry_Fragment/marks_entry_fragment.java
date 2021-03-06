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
import com.example.genius.databinding.MarksEntryFragmentBinding;
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
import java.util.Objects;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class marks_entry_fragment extends Fragment {

    MarksEntryFragmentBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>(), batchitem = new ArrayList<>(), batchid = new ArrayList<>(), dateitem = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>(), dateid = new ArrayList<>(),courseid = new ArrayList<>();
    String[] STANDARDITEM, SUBJECTITEM, BATCHITEM, DATEITEM,COURSEITEM;
    Integer[] STANDARDID, SUBJECTID, TESTID,COURSEID;
    String SubjectName, BatchTime, SubjectId, BatchId, TestDate,pictureFilePath, Achieve_Marks = "", StudentID = "", Subject_Date, Marks_Date;
    Long StandardId, TestID,courseID;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    File instrumentFileDestination;
    int flag = 0;
    OnBackPressedCallback callback;
    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final int REQUEST_CODE_TAKE_PICTURE = 0x2;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    byte[] imageVal;
    Bitmap bitmap;
    boolean marksentered;
    MarksEnterAdapter marksEnterAdapter;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Marks Master Entry");
        binding = MarksEntryFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllCourse();
            SelectTestDate();
            SelectSubject();
            selectbatch_time();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();

        binding.uploadImage.setOnClickListener(new View.OnClickListener() {
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

        binding.btnsearchStudent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (binding.courseName.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                    }else if (binding.standard.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                    else if (binding.batchTime.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                    else if (binding.testDate.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Test Date.", Toast.LENGTH_SHORT).show();
                    else if (binding.subject.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Subject.", Toast.LENGTH_SHORT).show();
                    else {
                        if (marksentered) {
                            Toast.makeText(context, "Marks Already inserted for this Test !", Toast.LENGTH_LONG).show();
                        } else {
                            progressBarHelper.showProgressDialog();
                            Call<StudentModel.StudentDataList> call = apiCalling.Get_Student_Details(StandardId, Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Long.parseLong(BatchId));
                            call.enqueue(new Callback<StudentModel.StudentDataList>() {
                                @Override
                                public void onResponse(Call<StudentModel.StudentDataList> call, Response<StudentModel.StudentDataList> response) {
                                    if (response.isSuccessful()) {
                                        StudentModel.StudentDataList data = response.body();
                                        if (data.isCompleted()) {
                                            List<StudentModel> list = data.getData();
                                            if (list != null && list.size() > 0) {
                                                binding.marksRv.setVisibility(View.VISIBLE);
                                                binding.txtNodata.setVisibility(View.GONE);
                                                binding.saveTestMarks.setVisibility(View.VISIBLE);
                                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                                binding.marksRv.setLayoutManager(linearLayoutManager);
                                                marksEnterAdapter = new MarksEnterAdapter(context, list);
                                                marksEnterAdapter.notifyDataSetChanged();
                                                binding.marksRv.setAdapter(marksEnterAdapter);
                                            } else {
                                                binding.marksRv.setVisibility(View.GONE);
                                                binding.txtNodata.setVisibility(View.VISIBLE);
                                                binding.saveTestMarks.setVisibility(View.GONE);
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

        binding.saveTestMarks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (binding.courseName.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                    }else if (binding.standard.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                    else if (binding.batchTime.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Batch Time.", Toast.LENGTH_SHORT).show();
                    else if (binding.testDate.getSelectedItemId() == 0)
                        Toast.makeText(context, "Please Select Test Date.", Toast.LENGTH_SHORT).show();
                    else {
                        progressBarHelper.showProgressDialog();
                        if (MarksEnterAdapter.studentModels.size() > 0) {
                            for (int i = 0; i < MarksEnterAdapter.studentModels.size(); i++) {
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
                        } catch (Exception e) {

                        }
                        Call<MarksModel.MarksData1> call;
                        if (binding.uploadImage.getText().toString().isEmpty()) {
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                            MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", requestBody);
                            call = apiCalling.MarksMaintenance(0, Marks_Date, TestID, Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                                    , StudentID, Achieve_Marks, Integer.parseInt(BatchId),courseID,StandardId,Long.parseLong(SubjectId), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0,
                                    "0", "0", false, uploadfile);
                        } else {
                            RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                            MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                            call = apiCalling.MarksMaintenance(0, Marks_Date, TestID, Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                                    , StudentID, Achieve_Marks, Integer.parseInt(BatchId), courseID,StandardId,Long.parseLong(SubjectId), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0,
                                    "0", "0", true, uploadfile);
                        }
                        call.enqueue(new Callback<MarksModel.MarksData1>() {
                            @Override
                            public void onResponse(@NonNull Call<MarksModel.MarksData1> call, @NonNull Response<MarksModel.MarksData1> response) {
                                if (response.isSuccessful()) {
                                    MarksModel.MarksData1 data = response.body();
                                    if (data.isCompleted()) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        marks_entry_Listfragment profileFragment = new marks_entry_Listfragment();
                                        FragmentManager fm = requireActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.nav_host_fragment, profileFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    } else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(@NonNull Call<MarksModel.MarksData1> call, @NonNull Throwable t) {
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
        return binding.getRoot();
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
                    imageVal = null;
                    binding.uploadImage.setText("Attached");
                    binding.uploadImage.setTextColor(context.getResources().getColor(R.color.black));
                    instrumentFileDestination = new File(pictureFilePath);
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
                    Uri uri = result.getData();
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Path);
                    InputStream imageStream;
                    imageStream = requireActivity().getContentResolver().openInputStream(image);
                    bitmap = BitmapFactory.decodeStream(imageStream);
                    binding.uploadImage.setText("Attached");
                    binding.uploadImage.setTextColor(context.getResources().getColor(R.color.black));
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
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File image = File.createTempFile(pictureFile, ".jpg", storageDir);
        pictureFilePath = image.getAbsolutePath();
        return image;
    }

    @SuppressLint("QueryPermissionsNeeded")
    private void takePic() {
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_FINISH_ON_COMPLETION, true);
        File pictureFile = null;
        try {
            pictureFile = getPictureFile();
        } catch (IOException ex) {

            return;
        }
        if (pictureFile != null) {
            Uri photoURI = FileProvider.getUriForFile(context, "com.ashirvad.genius.provider", pictureFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
            startActivityForResult(intent, REQUEST_CODE_TAKE_PICTURE);
        }
    }

    private void onCameraImageResultInstrument() {
        Bitmap rotatedBitmap;
        Bitmap bitmap = null;
        Uri uri = FUtils.getUri(instrumentFileDestination);
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

    public void GetAllSubject() {
        progressBarHelper.showProgressDialog();
        subjectitem.clear();
        subjectid.clear();
        dateid.clear();
        subjectitem.add("Select Subject");
        subjectid.add(0);
        dateid.add(0);
        try {
            Date d = displaydate.parse(TestDate);
            Subject_Date = actualdate.format(d);
        } catch (Exception e) {

        }
        Call<SubjectData> call = apiCalling.GetAllSubjectByTestDate(Subject_Date,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
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
                                for (SubjectModel singleResponseModel : respose) {

                                    String std = singleResponseModel.getSubject();
                                    subjectitem.add(std);

                                    int stdid = (int) singleResponseModel.getSubjectID();
                                    subjectid.add(stdid);

                                    int testid = (int) singleResponseModel.getTestID();
                                    dateid.add(testid);
                                }
                                SUBJECTITEM = new String[subjectitem.size()];
                                SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

                                SUBJECTID = new Integer[subjectid.size()];
                                SUBJECTID = subjectid.toArray(SUBJECTID);

                                TESTID = new Integer[dateid.size()];
                                TESTID = dateid.toArray(TESTID);

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
        binding.subject.setAdapter(adapter);
        binding.subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener8 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectName = subjectitem.get(position);
                    SubjectId = subjectid.get(position).toString();
                    TestID = Long.parseLong(dateid.get(position).toString());
                    if (binding.subject.getSelectedItem().equals("Select Subject")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (binding.subject.getSelectedItemId() != 0) {
                        GetTestDetails();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetTestDetails() {
        progressBarHelper.showProgressDialog();
        Call<TestScheduleModel.TestScheduleData1> call = apiCalling.Get_Test_Details(TestID, Long.parseLong(SubjectId));
        call.enqueue(new Callback<TestScheduleModel.TestScheduleData1>() {
            @Override
            public void onResponse(Call<TestScheduleModel.TestScheduleData1> call, Response<TestScheduleModel.TestScheduleData1> response) {
                if (response.isSuccessful()) {
                    TestScheduleModel.TestScheduleData1 data = response.body();
                    if (data != null && data.isCompleted()) {
                        TestScheduleModel model = data.getData();
                        binding.totalMarks.setText("" + model.getMarks());
                        binding.remarks.setText(model.getRemarks());
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
        binding.courseName.setAdapter(adapter);
        binding.courseName.setOnItemSelectedListener(selectcourse);
    }

    AdapterView.OnItemSelectedListener selectcourse =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    courseID = Long.parseLong(courseid.get(position).toString());
                    if (binding.courseName.getSelectedItem().equals("Select Course")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (binding.courseName.getSelectedItemId() != 0){
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STANDARDITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.standard.setAdapter(adapter);
        binding.standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StandardId = Long.parseLong(standardid.get(position).toString());
                    if (binding.standard.getSelectedItem().equals("Select Standard")) {
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
        batchid.clear();
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
        binding.batchTime.setAdapter(adapter);
        binding.batchTime.setOnItemSelectedListener(onItemSelectedListener77);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener77 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BatchTime = batchitem.get(position);
                    BatchId = batchid.get(position);
                    if (binding.batchTime.getSelectedItem().equals("Select Batch Time")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    }
                    if (binding.batchTime.getSelectedItemId() != 0) {
                        GetTestDates();
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetTestDates() {
        progressBarHelper.showProgressDialog();
        dateitem.clear();
        dateitem.add("Test Date");
        Call<MarksModel.MarksData> call = apiCalling.Get_Test_Marks(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), courseID,StandardId, Integer.parseInt(BatchId));
        call.enqueue(new Callback<MarksModel.MarksData>() {
            @Override
            public void onResponse(Call<MarksModel.MarksData> call, Response<MarksModel.MarksData> response) {
                if (response.isSuccessful()) {
                    MarksModel.MarksData data = response.body();
                    if (data.isCompleted() && data != null) {
                        List<MarksModel> model = data.getData();
                        for (MarksModel marksModel : model) {
                            String testdate = marksModel.getTestDate();
                            try {
                                Date d = actualdate.parse(testdate);
                                String date = displaydate.format(d);
                                dateitem.add(date);
                            } catch (Exception e) {

                            }
                        }

                        DATEITEM = new String[dateitem.size()];
                        DATEITEM = dateitem.toArray(DATEITEM);

                        bindDate();
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

    public void bindDate() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, DATEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.testDate.setAdapter(adapter);
        binding.testDate.setOnItemSelectedListener(onItemSelectedListenerdate);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListenerdate =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    TestDate = dateitem.get(position);
                    if (binding.testDate.getSelectedItem().equals("Test Date")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                    if (binding.testDate.getSelectedItemId() != 0) {
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
            }
        }
    }

    public void SelectTestDate() {
        dateitem.clear();
        dateitem.add("Test Date");

        DATEITEM = new String[dateitem.size()];
        DATEITEM = dateitem.toArray(DATEITEM);

        bindTestDate();
    }

    public void bindTestDate() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, DATEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.testDate.setAdapter(adapter);
        binding.testDate.setOnItemSelectedListener(onItemSelectedListener80);
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

    public void SelectSubject() {
        subjectitem.clear();
        subjectitem.add("Select Subject");

        SUBJECTITEM = new String[subjectitem.size()];
        SUBJECTITEM = subjectitem.toArray(SUBJECTITEM);

        bindselectsubject();
    }

    public void bindselectsubject() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SUBJECTITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.subject.setAdapter(adapter);
        binding.subject.setOnItemSelectedListener(onItemSelectedListener90);
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
        binding.standard.setAdapter(adapter);
        binding.standard.setOnItemSelectedListener(onItemSelectedListener7);
    }
}