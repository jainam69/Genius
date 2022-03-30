package com.example.genius.ui.Youtube_Video;

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

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class YoutubeVideoFragment extends Fragment {

    SearchableSpinner standard,course_name;
    EditText youtube_title, youtube_link;
    Button save_youtube, edit_youtube;
    RecyclerView youtube_rv;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>(),courseitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(),courseid = new ArrayList<>();
    String[] STANDARDITEM,COURSEITEM;
    Integer[] STANDARDID,COURSEID;
    TextView id, text, transaction_id, unique_id;
    OnBackPressedCallback callback;
    NestedScrollView you_scroll;
    Long StandardId,courseID;
    String stdname = "";
    YoutubeVideo_Adapter youtubeVideo_adapter;
    UserModel userpermission;
    LinearLayout linear_create_youtube;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("YouTube Video");
        View root = inflater.inflate(R.layout.fragment_youtube_video, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        standard = root.findViewById(R.id.standard);
        youtube_title = root.findViewById(R.id.youtube_title);
        youtube_link = root.findViewById(R.id.youtube_link);
        save_youtube = root.findViewById(R.id.save_youtube);
        edit_youtube = root.findViewById(R.id.edit_youtube);
        youtube_rv = root.findViewById(R.id.youtube_rv);
        transaction_id = root.findViewById(R.id.transaction_id);
        unique_id = root.findViewById(R.id.unique_id);
        id = root.findViewById(R.id.id);
        text = root.findViewById(R.id.text);
        you_scroll = root.findViewById(R.id.you_scroll);
        course_name = root.findViewById(R.id.course_name);
        linear_create_youtube = root.findViewById(R.id.linear_create_youtube);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission()){
            if (model.getPageInfo().getPageID() == 86 && !model.getPackageRightinfo().isCreatestatus()){
                linear_create_youtube.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllCourse();
            GetAllYoutubeVideos();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        selectStandard();

        save_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (course_name.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (standard.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                else if (youtube_title.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(context, "Please Enter Title.", Toast.LENGTH_SHORT).show();
                else if (youtube_link.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(context, "Please Enter Url.", Toast.LENGTH_SHORT).show();
                else {
                    if (Function.isNetworkAvailable(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                        BranchClassSingleModel.BranchClassData branchclass = new BranchClassSingleModel.BranchClassData(StandardId);
                        LinkModel model = new LinkModel(branchModel, course,branchclass, youtube_link.getText().toString(), rowStatusModel, transactionModel, youtube_title.getText().toString());
                        Call<LinkModel.LinkData1> call = apiCalling.YoutubeVideoMaintenance(model);
                        call.enqueue(new Callback<LinkModel.LinkData1>() {
                            @Override
                            public void onResponse(Call<LinkModel.LinkData1> call, Response<LinkModel.LinkData1> response) {
                                if (response.isSuccessful()) {
                                    LinkModel.LinkData1 data1 = response.body();
                                    if (data1.isCompleted()) {
                                        Toast.makeText(context, data1.getMessage(), Toast.LENGTH_SHORT).show();
                                        GetAllYoutubeVideos();
                                        youtube_link.setText("");
                                        youtube_title.setText("");
                                        standard.setSelection(0);
                                        course_name.setSelection(0);
                                    }else {
                                        Toast.makeText(context, data1.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }

                            @Override
                            public void onFailure(Call<LinkModel.LinkData1> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, "" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        edit_youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (course_name.getSelectedItemId() == 0){
                    Toast.makeText(context, "Please select Course.", Toast.LENGTH_SHORT).show();
                }else if (standard.getSelectedItemId() == 0)
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                else if (youtube_title.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(context, "Please Enter Title.", Toast.LENGTH_SHORT).show();
                else if (youtube_link.getText().toString().equalsIgnoreCase(""))
                    Toast.makeText(context, "Please Enter Url.", Toast.LENGTH_SHORT).show();
                else {
                    if (Function.isNetworkAvailable(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Long.parseLong(transaction_id.getText().toString()),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        BranchCourseModel.BranchCourceData course = new BranchCourseModel.BranchCourceData(courseID);
                        BranchClassSingleModel.BranchClassData branchclass = new BranchClassSingleModel.BranchClassData(StandardId);
                        LinkModel model = new LinkModel(Long.parseLong(unique_id.getText().toString()),branchModel, course,branchclass, youtube_link.getText().toString(), rowStatusModel, transactionModel, youtube_title.getText().toString());
                        Call<LinkModel.LinkData1> call = apiCalling.YoutubeVideoMaintenance(model);
                        call.enqueue(new Callback<LinkModel.LinkData1>() {
                            @Override
                            public void onResponse(Call<LinkModel.LinkData1> call, Response<LinkModel.LinkData1> response) {
                                if (response.isSuccessful()) {
                                    LinkModel.LinkData1 data1 = response.body();
                                    if (data1.isCompleted()) {
                                        Toast.makeText(context, data1.getMessage(), Toast.LENGTH_SHORT).show();
                                        save_youtube.setVisibility(View.VISIBLE);
                                        edit_youtube.setVisibility(View.GONE);
                                        GetAllYoutubeVideos();
                                        youtube_link.setText("");
                                        youtube_title.setText("");
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
                            public void onFailure(Call<LinkModel.LinkData1> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, "" + t.getMessage().toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                home_fragment profileFragment = new home_fragment();
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

    public void GetAllYoutubeVideos() {
        progressBarHelper.showProgressDialog();
        Call<LinkData> call = apiCalling.GetYoutubeLinksBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<LinkData>() {
            @Override
            public void onResponse(Call<LinkData> call, Response<LinkData> response) {
                if (response.isSuccessful()) {
                    LinkData data = response.body();
                    if (data.isCompleted()) {
                        List<LinkModel> models = data.getData();
                        if (models != null) {
                            if (models.size() > 0) {
                                text.setVisibility(View.VISIBLE);
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                youtube_rv.setLayoutManager(linearLayoutManager);
                                youtubeVideo_adapter = new YoutubeVideo_Adapter(context, models);
                                youtubeVideo_adapter.notifyDataSetChanged();
                                youtube_rv.setAdapter(youtubeVideo_adapter);
                            }else {
                                text.setVisibility(View.GONE);
                            }
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<LinkData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
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

    public class YoutubeVideo_Adapter extends RecyclerView.Adapter<YoutubeVideo_Adapter.ViewHolder> {

        Context context;
        List<LinkModel> linkdetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        UserModel userpermission;

        public YoutubeVideo_Adapter(Context context, List<LinkModel> linkdetails) {
            this.context = context;
            this.linkdetails = linkdetails;
        }

        @Override
        public YoutubeVideo_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new YoutubeVideo_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.youtube_master_deatil_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull YoutubeVideo_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            for (UserModel.UserPermission model : userpermission.getPermission()){
                if (model.getPageInfo().getPageID() == 86){
                    if (!model.getPackageRightinfo().isCreatestatus()){
                        holder.youtube_edit.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isDeletestatus()){
                        holder.youtube_delete.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isCreatestatus() && !model.getPackageRightinfo().isDeletestatus()){
                        holder.linear_actions.setVisibility(View.GONE);
                    }
                }
            }
            if (linkdetails.get(position).getRowStatus().getRowStatusId() == 1) {
                try {
                    int qw = standardid.indexOf(Integer.parseInt(String.valueOf(linkdetails.get(position).getStandardID())));
                    String az = standarditem.get(qw);
                    holder.standardname.setText("" + az);
                } catch (Exception ex) {

                }
                holder.course.setText(linkdetails.get(position).getBranchCourse().getCourse().getCourseName());
                holder.standardname.setText("" + linkdetails.get(position).getBranchClass().getClassModel().getClassName());
                holder.title.setText("" + linkdetails.get(position).getTitle());
                holder.youtube_url.setText("" + linkdetails.get(position).getLinkURL());
                holder.youtube_edit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                        builder.setView(dialogView);
                        builder.setCancelable(true);
                        Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                        Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                        ImageView image = dialogView.findViewById(R.id.image);
                        TextView title = dialogView.findViewById(R.id.title);
                        title.setText("Are you sure that you want to Edit Youtube Video?");
                        image.setImageResource(R.drawable.ic_edit);
                        AlertDialog dialog = builder.create();

                        btn_edit_no.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        btn_edit_yes.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                                save_youtube.setVisibility(View.GONE);
                                edit_youtube.setVisibility(View.VISIBLE);
                                unique_id.setText("" + linkdetails.get(position).getUniqueID());
                                transaction_id.setText("" + linkdetails.get(position).getTransaction().getTransactionId());
                                youtube_title.setText("" + linkdetails.get(position).getTitle());
                                youtube_link.setText("" + linkdetails.get(position).getLinkURL());
                                int a = courseid.indexOf(Integer.parseInt(String.valueOf(linkdetails.get(position).getBranchCourse().getCourse_dtl_id())));
                                course_name.setSelection(a);
                                StandardId = linkdetails.get(position).getBranchClass().getClass_dtl_id();
                                stdname = linkdetails.get(position).getBranchClass().getClassModel().getClassName();
                            }
                        });
                        dialog.show();

                    }
                });
                holder.youtube_delete.setOnClickListener(new View.OnClickListener() {
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
                        title.setText("Are you sure that you want to delete this Youtube Video?");
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
                                Call<CommonModel> call = apiCalling.RemoveLink(linkdetails.get(position).getUniqueID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                                call.enqueue(new Callback<CommonModel>() {
                                    @Override
                                    public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                        if (response.isSuccessful()) {
                                            CommonModel model = response.body();
                                            if (model.isData()) {
                                                Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                                linkdetails.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
                                            }else {
                                                Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
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
        }

        @Override
        public int getItemCount() {
            return linkdetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView standardname, title, youtube_url,course;
            ImageView youtube_delete, youtube_edit;
            LinearLayout linear_actions;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                standardname = itemView.findViewById(R.id.standardname);
                title = itemView.findViewById(R.id.title);
                youtube_url = itemView.findViewById(R.id.youtube_url);
                youtube_delete = itemView.findViewById(R.id.youtube_delete);
                youtube_edit = itemView.findViewById(R.id.youtube_edit);
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