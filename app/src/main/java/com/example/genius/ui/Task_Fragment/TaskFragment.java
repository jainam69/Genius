package com.example.genius.ui.Task_Fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.ToDoByIdData;
import com.example.genius.Model.TodoData;
import com.example.genius.Model.TodoModel;
import com.example.genius.Model.UserData1;
import com.example.genius.Model.UserModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.FUtils;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.File;
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
public class TaskFragment extends Fragment {

    public static final int REQUEST_CODE_PICK_GALLERY = 0x1;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 0x3;
    File instrumentFileDestination;
    OnBackPressedCallback callback;
    View root;
    Context context;
    EditText date_task, edt_taskDescription;
    TextView attachment, text, id, photo, no_content, todoid, transactionid;
    SearchableSpinner branch, user;
    Button save_task, edit_task;
    RecyclerView task_rv;
    SearchableSpinner status;
    byte[] imageVal;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> branchitem = new ArrayList<>(), useritem = new ArrayList<>();
    List<Integer> branchid = new ArrayList<>(), userid = new ArrayList<>();
    String[] BRANCHITEM, USERITEM;
    Integer[] BRANCHID, USERID;
    String BranchName, BranchID, UserName, UserId;
    int flag = 0;
    List<String> statusitem = new ArrayList<>();
    String[] STATUSITEM;
    String StatusName = "Pending";
    NestedScrollView scroll;
    String attach, date, filename ,Extension;
    int userid1 = 0;
    private int year;
    private int month;
    private int day;
    TodoMaster_Adapter todoMaster_adapter;

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    public static String yesterday() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cal.add(Calendar.DATE, 0);
        return dateFormat.format(cal.getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Task");
        root = inflater.inflate(R.layout.fragment_task, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        date_task = root.findViewById(R.id.date_task);
        attachment = root.findViewById(R.id.attachment);
        branch = root.findViewById(R.id.branch);
        user = root.findViewById(R.id.user);
        edt_taskDescription = root.findViewById(R.id.edt_taskDescription);
        task_rv = root.findViewById(R.id.task_rv);
        save_task = root.findViewById(R.id.save_task);
        edit_task = root.findViewById(R.id.edit_task);
        text = root.findViewById(R.id.text);
        id = root.findViewById(R.id.id);
        photo = root.findViewById(R.id.photo);
        todoid = root.findViewById(R.id.todoid);
        transactionid = root.findViewById(R.id.transactionid);
        status = root.findViewById(R.id.status);
        scroll = root.findViewById(R.id.scroll);
        no_content = root.findViewById(R.id.no_content);
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        if (Function.checkNetworkConnection(context)) {
            selectstatus();
            GetAllTask();
            GetAllUser(Long.parseLong(BranchID));
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        save_task.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (date_task.getText().toString().isEmpty())
                    Toast.makeText(context, "Please Select Task Date.", Toast.LENGTH_SHORT).show();
                else if(user.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select User.", Toast.LENGTH_SHORT).show();
                else if(attachment.getText().toString().equals(""))
                    Toast.makeText(context, "Please Upload Task Document.", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                    MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                    Call<TodoModel.TodoData1> call = apiCalling.ToDoMaintenance(0,date,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),Long.parseLong(UserId),
                            edt_taskDescription.getText().toString(),Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0
                            , "0", "0",true,uploadfile);
                    call.enqueue(new Callback<TodoModel.TodoData1>() {
                        @Override
                        public void onResponse(@NotNull Call<TodoModel.TodoData1> call, @NotNull Response<TodoModel.TodoData1> response) {
                            if (response.isSuccessful()) {
                                TodoModel.TodoData1 data1 = response.body();
                                if (data1.isCompleted()) {
                                    TodoModel model = data1.getData();
                                    if (model != null) {
                                        Toast.makeText(context,data1.getMessage(), Toast.LENGTH_SHORT).show();
                                        edt_taskDescription.setText("");
                                        date_task.setText("");
                                        attachment.setText("");
                                        filename = "";
                                        attach = "";
                                        branch.setSelection(0);
                                        user.setSelection(0);
                                        GetAllTask();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<TodoModel.TodoData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_task.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (date_task.getText().toString().isEmpty())
                    Toast.makeText(context, "Please Select Task Date.", Toast.LENGTH_SHORT).show();
                else if(user.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select User.", Toast.LENGTH_SHORT).show();
                else if(attachment.getText().toString().equals(""))
                    Toast.makeText(context, "Please Upload Task Document.", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    Call<TodoModel.TodoData1> call;
                    if (instrumentFileDestination != null) {
                        RequestBody requestBody = RequestBody.create(MediaType.parse("multipart/form-data"), instrumentFileDestination);
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("", instrumentFileDestination.getName(), requestBody);
                        call = apiCalling.ToDoMaintenance(Long.parseLong(todoid.getText().toString()),date,Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),Long.parseLong(UserId),
                                edt_taskDescription.getText().toString(),Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Long.parseLong(transactionid.getText().toString())
                                , "0", "0",true,uploadfile);
                    }else {
                        RequestBody attachmentEmpty = RequestBody.create(MediaType.parse("multipart/form-data"), "");
                        MultipartBody.Part uploadfile = MultipartBody.Part.createFormData("attachment", "", attachmentEmpty);
                        call = apiCalling.ToDoMaintenance(Long.parseLong(todoid.getText().toString()),date, Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),Long.parseLong(UserId),
                                edt_taskDescription.getText().toString(),Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Long.parseLong(transactionid.getText().toString())
                                , filename, Extension, false, uploadfile);
                    }
                    call.enqueue(new Callback<TodoModel.TodoData1>() {
                        @Override
                        public void onResponse(@NotNull Call<TodoModel.TodoData1> call, @NotNull Response<TodoModel.TodoData1> response) {
                            if (response.isSuccessful()) {
                                TodoModel.TodoData1 data1 = response.body();
                                if (data1.isCompleted()) {
                                    TodoModel model = data1.getData();
                                    if (model != null) {
                                        Toast.makeText(context,data1.getMessage(), Toast.LENGTH_SHORT).show();
                                        edt_taskDescription.setText("");
                                        date_task.setText("");
                                        attachment.setText("");
                                        filename = "";
                                        attach = "";
                                        userid1 = 0;
                                        branch.setSelection(0);
                                        user.setSelection(0);
                                        GetAllTask();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<TodoModel.TodoData1> call, @NotNull Throwable t) {

                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        Calendar cal2 = Calendar.getInstance();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        cal2.add(Calendar.DATE, 0);
        date = dateFormat1.format(cal2.getTime());
        date_task.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog picker = new DatePickerDialog(context,
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        date_task.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                        date = year + "-" + pad(month + 1) + "-" + pad(day);
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
                    pickImage();
                }
            } else {
                pickImage();
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                TaskListFragment profileFragment = new TaskListFragment();
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

    public void selectstatus() {
        statusitem.clear();
        statusitem.add("Select Status");
        statusitem.add("Pending");
        statusitem.add("Reject");
        statusitem.add("Approved");

        STATUSITEM = new String[statusitem.size()];
        STATUSITEM = statusitem.toArray(STATUSITEM);

        bindstatus();
    }

    public void bindstatus() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STATUSITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        status.setAdapter(adapter);
        status.setSelection(1);
        status.setOnItemSelectedListener(onItemSelectedListener61);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener61 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StatusName = statusitem.get(position);
                    if (status.getSelectedItem().equals("Select Status")) {
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
                        GetAllUser(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                        GetAllUser(Long.parseLong(BranchID));
                    }
                }
                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public void GetAllUser(long branch) {
        progressBarHelper.showProgressDialog();
        useritem.clear();
        userid.clear();
        useritem.add("Select User");
        userid.add(0);
        Call<UserData1> call = apiCalling.GetAllUsersddl(branch);
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
                                        useritem.add(building_name);

                                        int building_id = Integer.parseInt(String.valueOf(singleResponseModel.getUserID()));
                                        userid.add(building_id);
                                    }
                                    USERITEM = new String[useritem.size()];
                                    USERITEM = useritem.toArray(USERITEM);

                                    USERID = new Integer[userid.size()];
                                    USERID = userid.toArray(USERID);
                                    binduser();
                                }
                            }

                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(@NotNull Call<UserData1> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void binduser() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, USERITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user.setAdapter(adapter);
        user.setOnItemSelectedListener(UserItemListener);
    }

    AdapterView.OnItemSelectedListener UserItemListener =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    UserName = useritem.get(position);
                    UserId = userid.get(position).toString();
                    if (user.getSelectedItem().equals("Select User")) {
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
                    String Path = FUtils.getPath(requireContext(), uri);
                    instrumentFileDestination = new File(Objects.requireNonNull(Path));
                    attachment.setText("Attached");
                    attachment.setTextColor(context.getResources().getColor(R.color.black));
                    filename = instrumentFileDestination.getName();
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

    public void GetAllTask() {
        Call<TodoData> call = apiCalling.GetAllToDoWithoutContentByBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<TodoData>() {
            @Override
            public void onResponse(@NotNull Call<TodoData> call, @NotNull Response<TodoData> response) {
                if (response.isSuccessful()) {
                    TodoData data = response.body();
                    if (data.isCompleted()) {
                        List<TodoModel> modelList = data.getData();
                        if (modelList != null) {
                            if (modelList.size() > 0) {
                                task_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                todoMaster_adapter = new TodoMaster_Adapter(context, modelList);
                                todoMaster_adapter.notifyDataSetChanged();
                                task_rv.setAdapter(todoMaster_adapter);
                            }
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<TodoData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public class TodoMaster_Adapter extends RecyclerView.Adapter<TodoMaster_Adapter.ViewHolder> {

        Context context;
        List<TodoModel> todoModels;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
        DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

        public TodoMaster_Adapter(Context context, List<TodoModel> todoModels) {
            this.context = context;
            this.todoModels = todoModels;
        }

        @NotNull
        @Override
        public TodoMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new TodoMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.task_master_deatil_list, parent, false));
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull TodoMaster_Adapter.ViewHolder holder, int position) {

            if (todoModels.get(position).getToDoDate() != null) {
                String a = todoModels.get(position).getToDoDate().replace("T00:00:00", "");
                try {
                    Date d = actualdate.parse(a);
                    holder.task_date.setText("" + displaydate.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            holder.staff_name.setText("" + todoModels.get(position).getUserInfo().getUsername());
            holder.task_description.setText(todoModels.get(position).getToDoDescription());
            holder.task_edit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Task?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    progressBarHelper.showProgressDialog();
                    Call<ToDoByIdData> call = apiCalling.GetToDoByHWID(todoModels.get(position).getToDoID());
                    call.enqueue(new Callback<ToDoByIdData>() {
                        @Override
                        public void onResponse(@NotNull Call<ToDoByIdData> call, @NotNull Response<ToDoByIdData> response) {
                            if (response.isSuccessful()) {
                                progressBarHelper.hideProgressDialog();
                                ToDoByIdData paperData = response.body();
                                if (paperData.Completed) {
                                    TodoModel paperModelList = paperData.Data;
                                    if (paperModelList != null) {
                                        save_task.setVisibility(View.GONE);
                                        edit_task.setVisibility(View.VISIBLE);
                                        int az = Integer.parseInt(String.valueOf(todoModels.get(position).getUserInfo().getUserID()));
                                        if (az > 0) {
                                            userid1 = userid.indexOf(az);
                                            user.setSelection(userid1);
                                        }
                                        if (todoModels.get(position).getFilePath().contains(".") && todoModels.get(position).getFilePath().contains("/")) {
                                            Extension = todoModels.get(position).getFilePath().substring(todoModels.get(position).getFilePath().lastIndexOf(".") + 1);
                                            String FileNameWithExtension = todoModels.get(position).getFilePath().substring(todoModels.get(position).getFilePath().lastIndexOf("/") + 1);
                                            String[] FileNameArray = FileNameWithExtension.split("\\.");
                                            filename = todoModels.get(position).getToDoFileName();
                                        }
                                        transactionid.setText("" + todoModels.get(position).getTransaction().getTransactionId());
                                        todoid.setText("" + todoModels.get(position).getToDoID());
                                        edt_taskDescription.setText("" + todoModels.get(position).getToDoDescription());
                                        attachment.setText("Attached");
                                        attachment.setTextColor(context.getResources().getColor(R.color.black));
                                        String a = todoModels.get(position).getToDoDate().replace("T00:00:00", "");
                                        try {
                                            Date d = actualdate.parse(a);
                                            date = actualdate.format(d);
                                            date_task.setText("" + displaydate.format(d));
                                        } catch (ParseException e) {
                                            e.printStackTrace();
                                        }
                                        scroll.scrollTo(0, 0);
                                        scroll.fullScroll(View.FOCUS_UP);
                                    }
                                }
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<ToDoByIdData> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });

                });
                dialog.show();
            });
            holder.task_delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this Task?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                btn_delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        progressBarHelper.showProgressDialog();
                        Call<CommonModel> call = apiCalling.RemoveTodo(todoModels.get(position).getToDoID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        call.enqueue(new Callback<CommonModel>() {
                            @Override
                            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                                if (response.isSuccessful()) {
                                    CommonModel model = response.body();
                                    if (model.isCompleted()) {
                                        if (model.isData()) {
                                            todoModels.remove(position);
                                            notifyItemRemoved(position);
                                            notifyDataSetChanged();
                                        }
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }

                            @Override
                            public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                                progressBarHelper.hideProgressDialog();
                            }
                        });
                        dialog.dismiss();
                    }
                });
                dialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return todoModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView task_edit, task_delete;
            TextView task_date, staff_name, task_description;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                task_date = itemView.findViewById(R.id.task_date);
                staff_name = itemView.findViewById(R.id.staff_name);
                task_edit = itemView.findViewById(R.id.task_edit);
                task_delete = itemView.findViewById(R.id.task_delete);
                task_description = itemView.findViewById(R.id.task_description);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }

}