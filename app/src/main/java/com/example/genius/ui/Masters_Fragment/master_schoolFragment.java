package com.example.genius.ui.Masters_Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.SchoolData;
import com.example.genius.Model.SchoolModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.MasterSchoolFragmentBinding;
import com.example.genius.databinding.SchoolMasterDeatilListBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class master_schoolFragment extends Fragment {

    MasterSchoolFragmentBinding binding;
    List<String> schoolitem = new ArrayList<>();
    List<Integer> schoolid = new ArrayList<>();
    String[] SCHOOLITEM;
    Integer[] SCHOOLID;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    SchoolMaster_Adapter schoolMaster_adapter;
    UserModel userpermission;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("School Master");
        binding = MasterSchoolFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 6 && !model.getPackageRightinfo().isCreatestatus()){
                binding.linearCreateSchool.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllSchool();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        binding.saveSchoolMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.schoolName.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Enter School Name", Toast.LENGTH_SHORT).show();
                } else {
                    if (Function.isNetworkAvailable(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        SchoolModel model = new SchoolModel(binding.schoolName.getText().toString(), transactionModel, rowStatusModel, branchModel);
                        Call<SchoolModel.SchoolData1> call = apiCalling.SchoolMaintanance(model);
                        call.enqueue(new Callback<SchoolModel.SchoolData1>() {
                            @Override
                            public void onResponse(Call<SchoolModel.SchoolData1> call, Response<SchoolModel.SchoolData1> response) {
                                if (response.isSuccessful()) {
                                    SchoolModel.SchoolData1 data = response.body();
                                    if (data.isCompleted()) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        binding.schoolName.setText("");
                                        GetAllSchool();
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<SchoolModel.SchoolData1> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressBarHelper.hideProgressDialog();
                        Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        binding.editSchoolMaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.schoolName.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Enter School Name", Toast.LENGTH_SHORT).show();
                } else {
                    if (Function.isNetworkAvailable(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Long.parseLong(binding.transactionId.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        SchoolModel model = new SchoolModel(Long.parseLong(binding.id.getText().toString()), binding.schoolName.getText().toString(), transactionModel, rowStatusModel, branchModel);
                        Call<SchoolModel.SchoolData1> call = apiCalling.SchoolMaintanance(model);
                        call.enqueue(new Callback<SchoolModel.SchoolData1>() {
                            @Override
                            public void onResponse(Call<SchoolModel.SchoolData1> call, Response<SchoolModel.SchoolData1> response) {
                                if (response.isSuccessful()) {
                                    SchoolModel.SchoolData1 data = response.body();
                                    if (data.isCompleted()) {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                        binding.saveSchoolMaster.setVisibility(View.VISIBLE);
                                        binding.editSchoolMaster.setVisibility(View.GONE);
                                        binding.schoolName.setText("");
                                        GetAllSchool();
                                    }else {
                                        Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<SchoolModel.SchoolData1> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        progressBarHelper.hideProgressDialog();
                        Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                    }
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
        return binding.getRoot();
    }

    public void GetAllSchool() {

        schoolitem.clear();
        schoolid.clear();
        Call<SchoolData> call = apiCalling.GetAllSchool(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SchoolData>() {
            @Override
            public void onResponse(Call<SchoolData> call, Response<SchoolData> response) {
                if (response.isSuccessful()) {
                    SchoolData schoolData = response.body();
                    if (schoolData != null) {
                        if (schoolData.isCompleted()) {
                            SCHOOLID = null;
                            SCHOOLITEM = null;
                            List<SchoolModel> respose = schoolData.getData();
                            if (respose.size() > 0) {
                                List<SchoolModel> list = new ArrayList<>();
                                for (SchoolModel singleResponseModel : respose) {

                                    String school_name = singleResponseModel.getSchoolName();
                                    schoolitem.add(school_name);

                                    int school_id = (int) singleResponseModel.getSchoolID();
                                    schoolid.add(school_id);
                                    if (singleResponseModel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(singleResponseModel);
                                    }
                                }
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                binding.schoolRv.setLayoutManager(linearLayoutManager);
                                schoolMaster_adapter = new SchoolMaster_Adapter(context, list);
                                schoolMaster_adapter.notifyDataSetChanged();
                                binding.schoolRv.setAdapter(schoolMaster_adapter);
                                SCHOOLITEM = new String[schoolitem.size()];
                                SCHOOLITEM = schoolitem.toArray(SCHOOLITEM);

                                SCHOOLID = new Integer[schoolid.size()];
                                SCHOOLID = schoolid.toArray(SCHOOLID);

                                bindschool();
                            }

                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<SchoolData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindschool() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, SCHOOLITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.schoolName.setAdapter(adapter);
        binding.schoolName.setThreshold(1);
    }

    public class SchoolMaster_Adapter extends RecyclerView.Adapter<SchoolMaster_Adapter.ViewHolder> {

        Context context;
        List<SchoolModel> schoolDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        UserModel userpermission;

        public SchoolMaster_Adapter(Context context, List<SchoolModel> schoolDetails) {
            this.context = context;
            this.schoolDetails = schoolDetails;
        }

        @Override
        public SchoolMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(SchoolMasterDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull SchoolMaster_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            for (UserModel.UserPermission model : userpermission.getPermission())
            {
                if (model.getPageInfo().getPageID() == 6){
                    if (!model.getPackageRightinfo().isCreatestatus()){
                        holder.binding.schoolEdit.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isDeletestatus()){
                        holder.binding.schoolDelete.setVisibility(View.GONE);
                    }
                    if (!model.getPackageRightinfo().isCreatestatus() && !model.getPackageRightinfo().isDeletestatus()){
                        holder.binding.linearActions.setVisibility(View.GONE);
                    }
                }
            }
            if (schoolDetails.get(position).getRowStatus().getRowStatusId() == 1) {
                holder.binding.schoolName.setText(schoolDetails.get(position).getSchoolName());
                holder.binding.schoolEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                        builder.setView(dialogView);
                        builder.setCancelable(true);
                        TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                        TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                        ImageView image = dialogView.findViewById(R.id.image);
                        TextView title = dialogView.findViewById(R.id.title);
                        title.setText("Are you sure that you want to Edit School Name?");
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
                                binding.saveSchoolMaster.setVisibility(View.GONE);
                                binding.editSchoolMaster.setVisibility(View.VISIBLE);
                                binding.schoolName.setText(schoolDetails.get(position).getSchoolName());
                                binding.id.setText("" + schoolDetails.get(position).getSchoolID());
                                binding.idBranch.setText("" + schoolDetails.get(position).getBranchInfo().getBranchID());
                                binding.transactionId.setText("" + schoolDetails.get(position).getTransaction().getTransactionId());
                                binding.schoolScroll.fullScroll(View.FOCUS_UP);
                                binding.schoolScroll.scrollTo(0, 0);
                            }
                        });
                        dialog.show();
                    }
                });
                holder.binding.schoolDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                        View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                        builder.setView(dialogView);
                        builder.setCancelable(true);
                        TextView btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                        TextView btn_delete = dialogView.findViewById(R.id.btn_delete);
                        TextView title = dialogView.findViewById(R.id.title);
                        ImageView image = dialogView.findViewById(R.id.image);
                        image.setImageResource(R.drawable.delete);
                        title.setText("Are you sure that you want to delete this School Name?");
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
                                Call<CommonModel> call = apiCalling.RemoveSchool(schoolDetails.get(position).getSchoolID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                                call.enqueue(new Callback<CommonModel>() {
                                    @Override
                                    public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                        if (response.isSuccessful()) {
                                            progressBarHelper.hideProgressDialog();
                                            CommonModel model = response.body();
                                            if (model.isCompleted()) {
                                                if (model.isData()) {
                                                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                                    schoolDetails.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyDataSetChanged();
                                                }else {
                                                    Toast.makeText(context, model.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        } else {
                                            progressBarHelper.hideProgressDialog();
                                        }
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
            return schoolDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            SchoolMasterDeatilListBinding binding;

            public ViewHolder(@NonNull SchoolMasterDeatilListBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
                userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }
}