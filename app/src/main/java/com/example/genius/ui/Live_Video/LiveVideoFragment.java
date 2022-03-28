package com.example.genius.ui.Live_Video;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import com.example.genius.Model.LinkData;
import com.example.genius.Model.LinkModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.gson.Gson;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class LiveVideoFragment extends Fragment {

    SearchableSpinner standard,course_name;
    EditText video_description, live_url, video_title;
    Button save_live_video, edit_live_video;
    RecyclerView live_rv;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(),courseid = new ArrayList<>();
    String[] STANDARDITEM,COURSEITEM;
    Integer[] STANDARDID,COURSEID;
    TextView id, text, transaction_id, unique_id;
    OnBackPressedCallback callback;
    NestedScrollView live_scroll;
    LiveVideo_Adapter liveVideo_adapter;
    Long StandardId,courseID;
    String stdname = "";
    LinearLayout linear_create_live;
    UserModel userpermission;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Online Class");
        View root = inflater.inflate(R.layout.fragment_live_video, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        standard = root.findViewById(R.id.standard);
        video_description = root.findViewById(R.id.video_description);
        save_live_video = root.findViewById(R.id.save_live_video);
        live_url = root.findViewById(R.id.live_url);
        edit_live_video = root.findViewById(R.id.edit_live_video);
        live_rv = root.findViewById(R.id.live_rv);
        id = root.findViewById(R.id.id);
        text = root.findViewById(R.id.text);
        video_title = root.findViewById(R.id.video_title);
        live_scroll = root.findViewById(R.id.live_scroll);
        transaction_id = root.findViewById(R.id.transaction_id);
        unique_id = root.findViewById(R.id.unique_id);
        course_name = root.findViewById(R.id.course_name);
        linear_create_live = root.findViewById(R.id.linear_create_live);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 79 && !model.getPackageRightinfo().isCreatestatus()){
                linear_create_live.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllLiveVideo();
            GetAllCourse();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();

        save_live_video.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (course_name.getSelectedItemId() == 0){
                    Toast.makeText(context,"Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (live_url.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please Enter Live Video URL.", Toast.LENGTH_SHORT).show();
                } else if (video_description.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please Enter Live URL Description.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), "");
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData branchclass = new BranchClassSingleModel.BranchClassData(StandardId);
                    LinkModel model = new LinkModel(branchModel, course,branchclass, video_description.getText().toString(), live_url.getText().toString(), rowStatusModel, transactionModel, video_title.getText().toString());
                    Call<LinkModel.LinkData1> call = apiCalling.LiveVideoMaintenance(model);
                    call.enqueue(new Callback<LinkModel.LinkData1>() {
                        @Override
                        public void onResponse(@NotNull Call<LinkModel.LinkData1> call, @NotNull Response<LinkModel.LinkData1> response) {
                            if (response.isSuccessful()) {
                                LinkModel.LinkData1 data1 = response.body();
                                if (data1.isCompleted()){
                                    Toast.makeText(context, data1.getMessage(), Toast.LENGTH_SHORT).show();
                                    video_title.setText("");
                                    video_description.setText("");
                                    live_url.setText("");
                                    standard.setSelection(0);
                                    course_name.setSelection(0);
                                    GetAllLiveVideo();
                                }else {
                                    Toast.makeText(context, data1.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<LinkModel.LinkData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_live_video.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (course_name.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (standard.getSelectedItemId() == 0) {
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (live_url.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please Enter Live Video URL.", Toast.LENGTH_SHORT).show();
                } else if (video_description.getText().toString().equalsIgnoreCase("")) {
                    Toast.makeText(context, "Please Enter Live URL Description.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(transaction_id.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                    BranchClassSingleModel.BranchClassData branchclass = new BranchClassSingleModel.BranchClassData(StandardId);
                    LinkModel model = new LinkModel(Long.parseLong(unique_id.getText().toString()), branchModel, course,branchclass, video_description.getText().toString(), live_url.getText().toString(), rowStatusModel, transactionModel, video_title.getText().toString());
                    Call<LinkModel.LinkData1> call = apiCalling.LiveVideoMaintenance(model);
                    call.enqueue(new Callback<LinkModel.LinkData1>() {
                        @Override
                        public void onResponse(@NotNull Call<LinkModel.LinkData1> call, @NotNull Response<LinkModel.LinkData1> response) {
                            if (response.isSuccessful()) {
                                LinkModel.LinkData1 data1 = response.body();
                                if (data1.isCompleted()){
                                    Toast.makeText(context, data1.getMessage(), Toast.LENGTH_SHORT).show();
                                    save_live_video.setVisibility(View.VISIBLE);
                                    edit_live_video.setVisibility(View.GONE);
                                    GetAllLiveVideo();
                                    video_title.setText("");
                                    video_description.setText("");
                                    live_url.setText("");
                                    standard.setSelection(0);
                                    course_name.setSelection(0);
                                    stdname = "";
                                }else {
                                    Toast.makeText(context, data1.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<LinkModel.LinkData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

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

    public void GetAllLiveVideo() {
        progressBarHelper.showProgressDialog();
        Call<LinkData> call = apiCalling.GetLiveVideoLinksBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<LinkData>() {
            @Override
            public void onResponse(@NotNull Call<LinkData> call, @NotNull Response<LinkData> response) {
                if (response.isSuccessful()) {
                    LinkData data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<LinkModel> models = data.getData();
                        if (models != null) {
                            if (models.size() > 0) {
                                text.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                live_rv.setLayoutManager(linearLayoutManager);
                                liveVideo_adapter = new LiveVideo_Adapter(context, models);
                                liveVideo_adapter.notifyDataSetChanged();
                                live_rv.setAdapter(liveVideo_adapter);
                            }else {
                                text.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(@NotNull Call<LinkData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void GetAllCourse(){
        courseitem.clear();
        courseid.clear();
        courseitem.add("Select Course");
        courseid.add(0);

        Call <BranchCourseModel> call = apiCalling.GetAllCourseDDL(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
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
            public void onResponse(@NotNull Call<BranchClassModel> call, @NotNull Response<BranchClassModel> response) {
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
        if (stdname != ""){
            selectSpinnerValue(standard,stdname);
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
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };

    public class LiveVideo_Adapter extends RecyclerView.Adapter<LiveVideo_Adapter.ViewHolder> {

        Context context;
        List<LinkModel> linkdetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        UserModel userpermission;

        public LiveVideo_Adapter(Context context, List<LinkModel> linkdetails) {
            this.context = context;
            this.linkdetails = linkdetails;
        }

        @NotNull
        @Override
        public LiveVideo_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LiveVideo_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.livevideo_master_deatil_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull LiveVideo_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            for (UserModel.UserPermission model : userpermission.getPermission()){
                if (model.getPageInfo().getPageID() == 79){
                    if (!model.getPackageRightinfo().isCreatestatus()){
                        holder.live_edit.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isDeletestatus()){
                        holder.live_delete.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isCreatestatus() && !model.getPackageRightinfo().isDeletestatus()){
                        holder.linear_actions.setVisibility(View.GONE);
                    }
                }
            }
            holder.video_name.setText("" + linkdetails.get(position).getTitle());
            holder.description.setText("" + linkdetails.get(position).getLinkDesc());
            holder.video_url.setText("" + linkdetails.get(position).getLinkURL());
            holder.course.setText(linkdetails.get(position).getBranchCourse().getCourse().getCourseName());
            holder.standard.setText(linkdetails.get(position).getBranchClass().getClassModel().getClassName());

            holder.live_edit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Online Class?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());
                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    save_live_video.setVisibility(View.GONE);
                    edit_live_video.setVisibility(View.VISIBLE);
                    unique_id.setText("" + linkdetails.get(position).getUniqueID());
                    transaction_id.setText("" + linkdetails.get(position).getTransaction().getTransactionId());
                    video_description.setText("" + linkdetails.get(position).getLinkDesc());
                    video_title.setText("" + linkdetails.get(position).getTitle());
                    live_url.setText("" + linkdetails.get(position).getLinkURL());
                    stdname = linkdetails.get(position).getBranchClass().getClassModel().getClassName();
                    int a = courseid.indexOf(Integer.parseInt(String.valueOf(linkdetails.get(position).getBranchCourse().getCourse_dtl_id())));
                    course_name.setSelection(a);
                });
                dialog.show();

            });
            holder.live_delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this Online Class?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

                btn_delete.setOnClickListener(v14 -> {
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.RemoveLink(linkdetails.get(position).getUniqueID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel model = response.body();
                                if (model != null && model.isData()) {
                                    Toast.makeText(context, "Live Video Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                    linkdetails.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();
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
                });
                dialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return linkdetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView video_name, description, video_url,standard,course;
            ImageView live_edit, live_delete;
            LinearLayout linear_actions;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                video_name = itemView.findViewById(R.id.video_name);
                description = itemView.findViewById(R.id.description);
                video_url = itemView.findViewById(R.id.video_url);
                live_edit = itemView.findViewById(R.id.live_edit);
                live_delete = itemView.findViewById(R.id.live_delete);
                standard = itemView.findViewById(R.id.standard);
                linear_actions = itemView.findViewById(R.id.linear_actions);
                course = itemView.findViewById(R.id.course);
                userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
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
        standard.setAdapter(adapter);
        standard.setOnItemSelectedListener(onItemSelectedListener7);
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