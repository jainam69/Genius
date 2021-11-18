package com.example.genius.ui.Masters_Fragment;

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
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class master_schoolFragment extends Fragment {

    AutoCompleteTextView school_name;
    RadioButton school_active, school_inactive;
    Button save_school_master, edit_school_master;
    RecyclerView school_rv;
    TextView id, id_branch, text, transaction_id;
    List<String> schoolitem = new ArrayList<>();
    List<Integer> schoolid = new ArrayList<>();
    String[] SCHOOLITEM;
    Integer[] SCHOOLID;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    NestedScrollView school_scroll;
    SchoolMaster_Adapter schoolMaster_adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("School Master");
        View root = inflater.inflate(R.layout.master_school_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        school_name = root.findViewById(R.id.school_name);
        school_active = root.findViewById(R.id.school_active);
        school_inactive = root.findViewById(R.id.school_inactive);
        save_school_master = root.findViewById(R.id.save_school_master);
        edit_school_master = root.findViewById(R.id.edit_school_master);
        school_rv = root.findViewById(R.id.school_rv);
        id = root.findViewById(R.id.id);
        id_branch = root.findViewById(R.id.id_branch);
        text = root.findViewById(R.id.text);
        school_scroll = root.findViewById(R.id.school_scroll);
        transaction_id = root.findViewById(R.id.transaction_id);

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllSchool();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        save_school_master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (school_name.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Enter School Name", Toast.LENGTH_SHORT).show();
                } else {
                    if (Function.isNetworkAvailable(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        SchoolModel model = new SchoolModel(school_name.getText().toString(), transactionModel, rowStatusModel, branchModel);
                        Call<SchoolModel.SchoolData1> call = apiCalling.SchoolMaintanance(model);
                        call.enqueue(new Callback<SchoolModel.SchoolData1>() {
                            @Override
                            public void onResponse(Call<SchoolModel.SchoolData1> call, Response<SchoolModel.SchoolData1> response) {
                                if (response.isSuccessful()) {
                                    SchoolModel.SchoolData1 data = response.body();
                                    if (data.isCompleted()) {
                                        SchoolModel model1 = data.getData();
                                        if (model1.getSchoolID() > 0) {
                                            Toast.makeText(context, "School inserted successfully.", Toast.LENGTH_SHORT).show();
                                            school_name.setText("");
                                            GetAllSchool();
                                        }else {
                                            Toast.makeText(context, "School Already Exists.", Toast.LENGTH_SHORT).show();
                                        }
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

        edit_school_master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (school_name.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Enter School Name", Toast.LENGTH_SHORT).show();
                } else {
                    if (Function.isNetworkAvailable(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Long.parseLong(transaction_id.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        SchoolModel model = new SchoolModel(Long.parseLong(id.getText().toString()), school_name.getText().toString(), transactionModel, rowStatusModel, branchModel);
                        Call<SchoolModel.SchoolData1> call = apiCalling.SchoolMaintanance(model);
                        call.enqueue(new Callback<SchoolModel.SchoolData1>() {
                            @Override
                            public void onResponse(Call<SchoolModel.SchoolData1> call, Response<SchoolModel.SchoolData1> response) {
                                if (response.isSuccessful()) {
                                    SchoolModel.SchoolData1 data = response.body();
                                    if (data.isCompleted()) {
                                        SchoolModel model1 = data.getData();
                                        if (model1.getSchoolID() > 0) {
                                            Toast.makeText(context, "School updated successfully.", Toast.LENGTH_SHORT).show();
                                            save_school_master.setVisibility(View.VISIBLE);
                                            edit_school_master.setVisibility(View.GONE);
                                            school_name.setText("");
                                            GetAllSchool();
                                        }else {
                                            Toast.makeText(context, "School Already Exists.", Toast.LENGTH_SHORT).show();
                                        }
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
        return root;
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
                                school_rv.setLayoutManager(linearLayoutManager);
                                schoolMaster_adapter = new SchoolMaster_Adapter(context, list);
                                schoolMaster_adapter.notifyDataSetChanged();
                                school_rv.setAdapter(schoolMaster_adapter);
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
        school_name.setAdapter(adapter);
        school_name.setThreshold(1);

    }

    public class SchoolMaster_Adapter extends RecyclerView.Adapter<SchoolMaster_Adapter.ViewHolder> {

        Context context;
        List<SchoolModel> schoolDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        String sname;

        public SchoolMaster_Adapter(Context context, List<SchoolModel> schoolDetails) {
            this.context = context;
            this.schoolDetails = schoolDetails;
        }

        @Override
        public SchoolMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SchoolMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.school_master_deatil_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull SchoolMaster_Adapter.ViewHolder holder, int position) {
            if (schoolDetails.get(position).getRowStatus().getRowStatusId() == 1) {
                holder.school_name.setText(schoolDetails.get(position).getSchoolName());
                holder.school_edit.setOnClickListener(new View.OnClickListener() {
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
                                save_school_master.setVisibility(View.GONE);
                                edit_school_master.setVisibility(View.VISIBLE);
                                school_name.setText(schoolDetails.get(position).getSchoolName());
                                id.setText("" + schoolDetails.get(position).getSchoolID());
                                id_branch.setText("" + schoolDetails.get(position).getBranchInfo().getBranchID());
                                transaction_id.setText("" + schoolDetails.get(position).getTransaction().getTransactionId());
                                school_scroll.fullScroll(View.FOCUS_UP);
                                school_scroll.scrollTo(0, 0);
                            }
                        });
                        dialog.show();
                    }
                });
                holder.school_delete.setOnClickListener(new View.OnClickListener() {
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
                                                    Toast.makeText(context, "School deleted successfully.", Toast.LENGTH_SHORT).show();
                                                    schoolDetails.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyDataSetChanged();
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

            TextView school_name, status;
            ImageView school_edit, school_delete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                school_name = itemView.findViewById(R.id.school_name);
                status = itemView.findViewById(R.id.status);
                school_edit = itemView.findViewById(R.id.school_edit);
                school_delete = itemView.findViewById(R.id.school_delete);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }
}