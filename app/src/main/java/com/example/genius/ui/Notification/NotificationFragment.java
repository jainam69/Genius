package com.example.genius.ui.Notification;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchClassModel;
import com.example.genius.Model.BranchClassSingleModel;
import com.example.genius.Model.BranchCourseModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.NotificationData;
import com.example.genius.Model.NotificationModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.example.genius.ui.MultiSelectionSpinner;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

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

public class NotificationFragment extends Fragment implements MultiSelectionSpinner.OnMultipleItemsSelectedListener{

    SearchableSpinner branch,course_name;
    LinearLayout linear_create_notification,linear_spinner;
    CheckBox ch_admin, ch_teacher, ch_student;
    Long adminid = Long.valueOf(0), teacherid = Long.valueOf(0), studentid = Long.valueOf(0);
    TextView text, id, image, transaction_id, notification_id;
    RecyclerView notification_rv;
    Button save_notification, edit_notification;
    Context context;
    ProgressBarHelper progressBarHelper;
    EditText notification_message,notification_date;
    ApiCalling apiCalling;
    int check_value_admin, check_value_teacher, check_value_student;
    Long courseID = 0L,StandardId;
    OnBackPressedCallback callback;
    NestedScrollView notification_scroll;
    String BranchID,noti_date,StandardIDs="";
    Notification_Adapter notification_adapter;
    UserModel userpermission;
    private int year;
    private int month;
    private int day;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");
    List<String> standarditem = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(),courseid = new ArrayList<>();
    String[] STANDARDITEM,COURSEITEM;
    Integer[] STANDARDID,COURSEID;
    MultiSelectionSpinner standard;
    List<NotificationModel.NotificationStandardEntity> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Notification");
        View root = inflater.inflate(R.layout.fragment_notification, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        branch = root.findViewById(R.id.branch);
        ch_admin = root.findViewById(R.id.ch_admin);
        ch_teacher = root.findViewById(R.id.ch_teacher);
        ch_student = root.findViewById(R.id.ch_student);
        notification_message = root.findViewById(R.id.notification_message);
        save_notification = root.findViewById(R.id.save_notification);
        edit_notification = root.findViewById(R.id.edit_notification);
        notification_rv = root.findViewById(R.id.notification_rv);
        text = root.findViewById(R.id.text);
        id = root.findViewById(R.id.id);
        image = root.findViewById(R.id.image);
        notification_id = root.findViewById(R.id.notification_id);
        transaction_id = root.findViewById(R.id.transaction_id);
        notification_scroll = root.findViewById(R.id.notification_scroll);
        notification_date = root.findViewById(R.id.notification_date);
        linear_spinner = root.findViewById(R.id.linear_spinner);
        course_name = root.findViewById(R.id.course_name);
        standard = root.findViewById(R.id.standard);
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        linear_create_notification = root.findViewById(R.id.linear_create_notification);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 10 && !model.getPackageRightinfo().isCreatestatus()){
                linear_create_notification.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            GetAllCourse();
            GetAllNotification();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();

        Calendar cal2 = Calendar.getInstance();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        cal2.add(Calendar.DATE, 0);
        noti_date = dateFormat1.format(cal2.getTime());

        notification_date.setText(yesterday());

        notification_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar c = Calendar.getInstance();
                year = c.get(Calendar.YEAR);
                month = c.get(Calendar.MONTH);
                day = c.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog picker = new DatePickerDialog(getActivity(),
                        (view, year2, monthOfYear, dayOfMonth) -> {
                            year = year2;
                            month = monthOfYear;
                            day = dayOfMonth;
                            noti_date = year + "-" + pad(month + 1) + "-" + pad(day);
                            notification_date.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                        }, year, month, day);
                picker.show();
            }
        });

        ch_admin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_value_admin = 1;
                } else {
                    check_value_admin = 0;
                }
            }
        });

        ch_teacher.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_value_teacher = 1;
                    linear_spinner.setVisibility(View.VISIBLE);
                } else {
                    check_value_teacher = 0;
                    if (!ch_student.isChecked()){
                        linear_spinner.setVisibility(View.GONE);
                    }
                }
            }
        });

        ch_student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_value_student = 1;
                    linear_spinner.setVisibility(View.VISIBLE);
                } else {
                    check_value_student = 0;
                    if (!ch_teacher.isChecked()){
                        linear_spinner.setVisibility(View.GONE);
                    }
                }
            }
        });

        save_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (notification_message.getText().toString().equals("")) {
                        Toast.makeText(context, "Please Enter Description.", Toast.LENGTH_SHORT).show();
                    }else if (notification_date.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please enter Notification Date.", Toast.LENGTH_SHORT).show();
                    }else if ((ch_teacher.isChecked() || ch_student.isChecked()) && course_name.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please Select Course.", Toast.LENGTH_SHORT).show();
                    } else if ((ch_teacher.isChecked() || ch_student.isChecked()) && StandardIDs.equals("")){
                        Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                    }else if (ch_admin.isChecked() || ch_teacher.isChecked() || ch_student.isChecked())
                    {
                        progressBarHelper.showProgressDialog();
                        List<NotificationModel.NotificationTypeModel> typeModel = new ArrayList<>();
                        if (check_value_admin == 1) {
                            NotificationModel.NotificationTypeModel model1 = new NotificationModel.NotificationTypeModel("Admin", 1);
                            typeModel.add(model1);
                        }
                        if (check_value_teacher == 1) {
                            NotificationModel.NotificationTypeModel model1 = new NotificationModel.NotificationTypeModel("Teacher", 2);
                            typeModel.add(model1);
                        }
                        if (check_value_student == 1) {
                            NotificationModel.NotificationTypeModel model1 = new NotificationModel.NotificationTypeModel("Student", 3);
                            typeModel.add(model1);
                        }
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                        BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                        NotificationModel model = new NotificationModel(typeModel, branchModel, rowStatusModel, transactionModel, notification_message.getText().toString(),noti_date,StandardIDs,course);
                        Call<NotificationModel.NotificationData1> call = apiCalling.NotificationMaintanance(model);
                        call.enqueue(new Callback<NotificationModel.NotificationData1>() {
                            @Override
                            public void onResponse(@NotNull Call<NotificationModel.NotificationData1> call, @NotNull Response<NotificationModel.NotificationData1> response) {
                                if (response.isSuccessful()) {
                                    NotificationModel.NotificationData1 data = response.body();
                                    if (data.isCompleted()) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        notification_message.setText("");
                                        notification_date.setText(yesterday());
                                        ch_admin.setChecked(false);
                                        ch_student.setChecked(false);
                                        ch_teacher.setChecked(false);
                                        GetAllNotification();
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<NotificationModel.NotificationData1> call, @NotNull Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(context, "Please Select SubType.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (notification_message.getText().toString().equals("")) {
                        Toast.makeText(context, "Please Enter Description.", Toast.LENGTH_SHORT).show();
                    }else if (notification_date.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please enter Notification Date.", Toast.LENGTH_SHORT).show();
                    }else if ((ch_teacher.isChecked() || ch_student.isChecked()) && course_name.getSelectedItemId() == 0){
                        Toast.makeText(context, "Please Select Course.", Toast.LENGTH_SHORT).show();
                    }else if ((ch_teacher.isChecked() || ch_student.isChecked()) && StandardIDs.equals("")){
                        Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                    }else if (ch_admin.isChecked() || ch_teacher.isChecked() || ch_student.isChecked())
                    {
                        progressBarHelper.showProgressDialog();
                        List<NotificationModel.NotificationTypeModel> typeModel = new ArrayList<>();
                        if (check_value_admin == 1) {
                            if (adminid > 0) {
                                NotificationModel.NotificationTypeModel model1 = new NotificationModel.NotificationTypeModel(adminid, "Admin", 1);
                                typeModel.add(model1);
                            } else {
                                NotificationModel.NotificationTypeModel model1 = new NotificationModel.NotificationTypeModel("Admin", 1);
                                typeModel.add(model1);
                            }
                        }
                        if (check_value_teacher == 1) {
                            if (teacherid > 0) {
                                NotificationModel.NotificationTypeModel model1 = new NotificationModel.NotificationTypeModel(teacherid, "Teacher", 2);
                                typeModel.add(model1);
                            } else {
                                NotificationModel.NotificationTypeModel model1 = new NotificationModel.NotificationTypeModel("Teacher", 2);
                                typeModel.add(model1);
                            }
                        }
                        if (check_value_student == 1) {
                            if (studentid > 0) {
                                NotificationModel.NotificationTypeModel model1 = new NotificationModel.NotificationTypeModel(studentid, "Student", 3);
                                typeModel.add(model1);
                            } else {
                                NotificationModel.NotificationTypeModel model1 = new NotificationModel.NotificationTypeModel("Student", 3);
                                typeModel.add(model1);
                            }
                        }
                        TransactionModel transactionModel = new TransactionModel(Long.parseLong(transaction_id.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                        BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                        NotificationModel model = new NotificationModel(Long.parseLong(notification_id.getText().toString()), typeModel, branchModel, rowStatusModel, transactionModel, notification_message.getText().toString(),noti_date,StandardIDs,course);
                        Call<NotificationModel.NotificationData1> call = apiCalling.NotificationMaintanance(model);
                        call.enqueue(new Callback<NotificationModel.NotificationData1>() {
                            @Override
                            public void onResponse(Call<NotificationModel.NotificationData1> call, Response<NotificationModel.NotificationData1> response) {
                                if (response.isSuccessful()) {
                                    NotificationModel.NotificationData1 data = response.body();
                                    if (data.isCompleted()) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        notification_message.setText("");
                                        notification_date.setText(yesterday());
                                        ch_admin.setChecked(false);
                                        ch_student.setChecked(false);
                                        ch_teacher.setChecked(false);
                                        GetAllNotification();
                                        save_notification.setVisibility(View.VISIBLE);
                                        edit_notification.setVisibility(View.GONE);
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<NotificationModel.NotificationData1> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }else {
                        Toast.makeText(context, "Please Select SubType.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                MasterSelectorFragment profileFragment = new MasterSelectorFragment();
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
        //long id = Long.parseLong(notification_id.getText().toString());
        if (list.size() > 0){
            List<String> lst = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < list.size(); i++){
                lst.add(list.get(i).standard);
                sb.append(list.get(i).std_id);
                sb.append(",");
            }
            StandardIDs = sb.toString().substring(0, sb.length() - 1);
            standard.setSelection(lst);
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
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
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

    public void GetAllNotification() {
        Call<NotificationData> call = apiCalling.GetAllMobileNotificationBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<NotificationData>() {
            @Override
            public void onResponse(Call<NotificationData> call, Response<NotificationData> response) {
                if (response.isSuccessful()) {
                    NotificationData data = response.body();
                    if (data != null) {
                        if (data.isCompleted()) {
                            List<NotificationModel> respose = data.getData();
                            if (respose != null) {
                                if (respose.size() > 0) {
                                    text.setVisibility(View.VISIBLE);
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                    notification_rv.setLayoutManager(linearLayoutManager);
                                    notification_adapter = new Notification_Adapter(context, respose);
                                    notification_adapter.notifyDataSetChanged();
                                    notification_rv.setAdapter(notification_adapter);
                                }else {
                                    text.setVisibility(View.GONE);
                                }
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<NotificationData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class Notification_Adapter extends RecyclerView.Adapter<Notification_Adapter.ViewHolder> {

        Context context;
        List<NotificationModel> notificationDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        UserModel userpermission;
        String stdname;

        public Notification_Adapter(Context context, List<NotificationModel> notificationDetails) {
            this.context = context;
            this.notificationDetails = notificationDetails;
        }

        @NonNull
        @Override
        public Notification_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Notification_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout_noti, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Notification_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            for (UserModel.UserPermission model : userpermission.getPermission())
            {
                if (model.getPageInfo().getPageID() == 10){
                    if (!model.getPackageRightinfo().isCreatestatus()){
                        holder.noti_edit.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isDeletestatus()){
                        holder.noti_delete.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isCreatestatus() && !model.getPackageRightinfo().isDeletestatus()){
                        holder.linear_actions.setVisibility(View.GONE);
                    }
                }
            }
            String date = notificationDetails.get(position).getNotification_Date();
            try {
                Date d = actualdate.parse(date);
                holder.ndate.setText(displaydate.format(d));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            holder.noti_desc.setText("" + notificationDetails.get(position).getNotificationMessage());
            List<NotificationModel.NotificationTypeModel> notitypelist = notificationDetails.get(position).getNotificationType();
            String a = null;
            for (NotificationModel.NotificationTypeModel model : notitypelist) {
                if (a != null) {
                    a = a + "-" + model.getTypeText();
                } else {
                    a = model.getTypeText();
                }
            }
            holder.sub_type.setText("" + a);
            stdname = "";
            if (notificationDetails.get(position).getList().size() > 0){
                holder.linear_course.setVisibility(View.VISIBLE);
                holder.linear_standard.setVisibility(View.VISIBLE);
                holder.course.setText(notificationDetails.get(position).getList().get(0).BranchCourse.getCourse().getCourseName());
                for (int i = 0; i < notificationDetails.get(position).getList().size(); i++){
                    stdname += notificationDetails.get(position).getList().get(i).standard;
                    stdname += "\n";
                }
                holder.standard.setText(stdname);
            }else {
                holder.linear_course.setVisibility(View.GONE);
                holder.linear_standard.setVisibility(View.GONE);
            }
            holder.noti_edit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                    View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                    builder.setView(dialogView);
                    builder.setCancelable(true);
                    Button btn_cancel = dialogView.findViewById(R.id.btn_edit_no);
                    Button btn_delete = dialogView.findViewById(R.id.btn_edit_yes);
                    TextView title = dialogView.findViewById(R.id.title);
                    ImageView image = dialogView.findViewById(R.id.image);
                    image.setImageResource(R.drawable.noti);
                    title.setText("Are you sure that you want to edit this Notification?");
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
                            save_notification.setVisibility(View.GONE);
                            edit_notification.setVisibility(View.VISIBLE);
                            transaction_id.setText("" + notificationDetails.get(position).getTransaction().getTransactionId());
                            id.setText("" + notificationDetails.get(position).getNotificationID());
                            notification_id.setText("" + notificationDetails.get(position).getNotificationID());
                            notification_message.setText(" " + notificationDetails.get(position).getNotificationMessage());
                            try {
                                Date d = actualdate.parse(notificationDetails.get(position).getNotification_Date());
                                notification_date.setText("" + displaydate.format(d));
                                noti_date = actualdate.format(d);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            List<NotificationModel.NotificationTypeModel> notitypelist = notificationDetails.get(position).getNotificationType();
                            for (NotificationModel.NotificationTypeModel model : notitypelist) {
                                if (model.getTypeID() == 1) {
                                    ch_admin.setChecked(true);
                                    adminid = model.getID();
                                }
                                if (model.getTypeID() == 2) {
                                    ch_teacher.setChecked(true);
                                    teacherid = model.getID();
                                }
                                if (model.getTypeID() == 3) {
                                    ch_student.setChecked(true);
                                    studentid = model.getID();
                                }
                            }
                            if (notificationDetails.get(position).getList().size() > 0){
                                list = notificationDetails.get(position).getList();
                                selectSpinnerValue(course_name,notificationDetails.get(position).getList().get(0).BranchCourse.getCourse().getCourseName());
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.show();

                }
            });

            holder.noti_delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                    View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                    builder.setView(dialogView);
                    builder.setCancelable(true);
                    Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                    Button btn_delete = dialogView.findViewById(R.id.btn_delete);
                    TextView title = dialogView.findViewById(R.id.title);
                    ImageView image = dialogView.findViewById(R.id.image);
                    image.setImageResource(R.drawable.delete);
                    title.setText("Are you sure that you want to delete this Notification?");
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
                            progressBarHelper.showProgressDialog();
                            Call<CommonModel> call = apiCalling.RemoveNotification(notificationDetails.get(position).getNotificationID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                            call.enqueue(new Callback<CommonModel>() {
                                @Override
                                public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                    if (response.isSuccessful()) {
                                        CommonModel model = response.body();
                                        if (model.isCompleted()) {
                                            if (model.isData()) {
                                                Toast.makeText(context, "Notification deleted successfully.", Toast.LENGTH_SHORT).show();
                                                notificationDetails.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
                                            }
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }

                                @Override
                                public void onFailure(Call<CommonModel> call, Throwable t) {
                                    progressBarHelper.hideProgressDialog();
                                }
                            });
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        }

        @Override
        public int getItemCount() {
            return notificationDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView noti_desc, sub_type,ndate,course,standard;
            ImageView noti_delete, noti_edit;
            LinearLayout linear_actions,linear_course,linear_standard;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                noti_desc = itemView.findViewById(R.id.noti_desc);
                sub_type = itemView.findViewById(R.id.sub_type);
                noti_delete = itemView.findViewById(R.id.noti_delete);
                noti_edit = itemView.findViewById(R.id.noti_edit);
                linear_actions = itemView.findViewById(R.id.linear_actions);
                ndate = itemView.findViewById(R.id.ndate);
                linear_course = itemView.findViewById(R.id.linear_course);
                linear_standard = itemView.findViewById(R.id.linear_standard);
                course = itemView.findViewById(R.id.course);
                standard = itemView.findViewById(R.id.standard);
                userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

    public static String yesterday() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cal.add(Calendar.DATE, 0);
        return dateFormat.format(cal.getTime());
    }
}