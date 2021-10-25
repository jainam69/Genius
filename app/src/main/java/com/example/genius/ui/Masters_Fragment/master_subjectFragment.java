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
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class master_subjectFragment extends Fragment {

    AutoCompleteTextView subject_name;
    RadioButton subject_active, subject_inactive;
    Button save_subject_master, edit_subject_master;
    RecyclerView subject_rv;
    Context context;
    TextView id, id_branch, text,transaction_id;
    List<String> subjectitem = new ArrayList<>();
    List<Integer> subjectid = new ArrayList<>();
    String[] SUBJECTITEM;
    Integer[] SUBJECTID;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    NestedScrollView subject_scroll;
    SubjectMaster_Adapter subjectMaster_adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Subject Master");
        View root = inflater.inflate(R.layout.master_subject_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        subject_name = root.findViewById(R.id.subject_name);
        subject_active = root.findViewById(R.id.subject_active);
        subject_inactive = root.findViewById(R.id.subject_inactive);
        save_subject_master = root.findViewById(R.id.save_subject_master);
        edit_subject_master = root.findViewById(R.id.edit_subject_master);
        subject_rv = root.findViewById(R.id.subject_rv);
        id = root.findViewById(R.id.id);
        id_branch = root.findViewById(R.id.id_branch);
        text = root.findViewById(R.id.text);
        transaction_id = root.findViewById(R.id.transaction_id);
        subject_scroll = root.findViewById(R.id.subject_scroll);

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllSubject();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        save_subject_master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subject_name.getText().toString().equals(""))
                    Toast.makeText(context, "Please Enter Subject Name", Toast.LENGTH_SHORT).show();
                else {
                    if (Function.isNetworkAvailable(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0,Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        SubjectModel model = new SubjectModel(subject_name.getText().toString(),transactionModel,rowStatusModel,branchModel);
                        Call<SubjectModel.SubjectData1> call = apiCalling.SubjectMaintanance(model);
                        call.enqueue(new Callback<SubjectModel.SubjectData1>() {
                            @Override
                            public void onResponse(Call<SubjectModel.SubjectData1> call, Response<SubjectModel.SubjectData1> response) {
                                if (response.isSuccessful()) {
                                    SubjectModel.SubjectData1 data = response.body();
                                    if (data.isCompleted()){
                                        SubjectModel model1 = data.getData();
                                        if (model1.getSubjectID()>0){
                                            Toast.makeText(context, "Subject inserted successfully.", Toast.LENGTH_SHORT).show();
                                            subject_name.setText("");
                                            GetAllSubject();
                                        }else {
                                            Toast.makeText(context, "Subject Already Exists.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                } else {
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<SubjectModel.SubjectData1> call, Throwable t) {
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

        edit_subject_master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subject_name.getText().toString().equals(""))
                    Toast.makeText(context, "Please Enter Subject Name", Toast.LENGTH_SHORT).show();
                else {
                    if (Function.checkNetworkConnection(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Long.parseLong(transaction_id.getText().toString()),Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),0);
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        SubjectModel model = new SubjectModel(Long.parseLong(id.getText().toString()),subject_name.getText().toString(),transactionModel,rowStatusModel,branchModel);
                        Call<SubjectModel.SubjectData1> call = apiCalling.SubjectMaintanance(model);
                        call.enqueue(new Callback<SubjectModel.SubjectData1>() {
                            @Override
                            public void onResponse(Call<SubjectModel.SubjectData1> call, Response<SubjectModel.SubjectData1> response) {
                                if (response.isSuccessful()) {
                                    SubjectModel.SubjectData1 data = response.body();
                                    if (data.isCompleted()){
                                        SubjectModel model1 = data.getData();
                                        if (model1.getSubjectID()>0){
                                            Toast.makeText(context, "Subject updated successfully.", Toast.LENGTH_SHORT).show();
                                            save_subject_master.setVisibility(View.VISIBLE);
                                            edit_subject_master.setVisibility(View.GONE);
                                            subject_name.setText("");
                                            GetAllSubject();
                                        }else {
                                            Toast.makeText(context, "Subject Already Exists.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                } else {
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<SubjectModel.SubjectData1> call, Throwable t) {
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

    public void GetAllSubject() {
        Call<SubjectData> call = apiCalling.GetAllSubject(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SubjectData>() {
            @Override
            public void onResponse(Call<SubjectData> call, Response<SubjectData> response) {
                if (response.isSuccessful()) {
                    SubjectData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            SUBJECTITEM = null;
                            SUBJECTID = null;
                            List<SubjectModel> respose = standardData.getData();
                            if (respose.size() > 0) {
                                List<SubjectModel> list = new ArrayList<>();
                                for (SubjectModel singleResponseModel : respose) {

                                    String std = singleResponseModel.getSubject();
                                    subjectitem.add(std);

                                    int stdid = (int) singleResponseModel.getSubjectID();
                                    subjectid.add(stdid);
                                    if (singleResponseModel.getRowStatus().getRowStatusId()==1){
                                        list.add(singleResponseModel);
                                    }
                                }
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                subject_rv.setLayoutManager(linearLayoutManager);
                                subjectMaster_adapter = new SubjectMaster_Adapter(context, list);
                                subjectMaster_adapter.notifyDataSetChanged();
                                subject_rv.setAdapter(subjectMaster_adapter);
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
                    progressBarHelper.hideProgressDialog();
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
        subject_name.setAdapter(adapter);
        subject_name.setThreshold(1);
    }


    public class SubjectMaster_Adapter extends RecyclerView.Adapter<SubjectMaster_Adapter.ViewHolder> {

        Context context;
        List<SubjectModel> subjectDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        String subname;

        public SubjectMaster_Adapter(Context context, List<SubjectModel> subjectDetails) {
            this.context = context;
            this.subjectDetails = subjectDetails;
        }

        @Override
        public SubjectMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new SubjectMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.subject_master_deatil_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            if (subjectDetails.get(position).getRowStatus().getRowStatusId()==1){
                holder.subject_name.setText(subjectDetails.get(position).getSubject());
                holder.subject_edit.setOnClickListener(new View.OnClickListener() {
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
                        title.setText("Are you sure that you want to Edit Subject?");
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
                                save_subject_master.setVisibility(View.GONE);
                                edit_subject_master.setVisibility(View.VISIBLE);
                                subject_name.setText(subjectDetails.get(position).getSubject());
                                id.setText("" + subjectDetails.get(position).getSubjectID());
                                id_branch.setText("" + subjectDetails.get(position).getBranchInfo().getBranchID());
                                transaction_id.setText(""+subjectDetails.get(position).getTransaction().getTransactionId());
                                subject_scroll.fullScroll(View.FOCUS_UP);
                                subject_scroll.scrollTo(0, 0);
                            }
                        });
                        dialog.show();
                    }
                });
                holder.subject_delete.setOnClickListener(new View.OnClickListener() {
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
                        title.setText("Are you sure that you want to delete this Subject?");
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
                                if (Function.checkNetworkConnection(context)) {
                                    progressBarHelper.showProgressDialog();
                                    Call<CommonModel> call = apiCalling.RemoveSubject(subjectDetails.get(position).getSubjectID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                                    call.enqueue(new Callback<CommonModel>() {
                                        @Override
                                        public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                            if (response.isSuccessful()) {
                                                CommonModel model = response.body();
                                                if (model.isData()) {
                                                    Toast.makeText(context, "Subject deleted successfully.", Toast.LENGTH_SHORT).show();
                                                    subjectDetails.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyDataSetChanged();
                                                }
                                                progressBarHelper.hideProgressDialog();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<CommonModel> call, Throwable t) {
                                            progressBarHelper.hideProgressDialog();
                                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                } else {
                                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                                }
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
            return subjectDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView subject_name, status;
            ImageView subject_edit, subject_delete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                subject_name = itemView.findViewById(R.id.subject_name);
                status = itemView.findViewById(R.id.status);
                subject_edit = itemView.findViewById(R.id.subject_edit);
                subject_delete = itemView.findViewById(R.id.subject_delete);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }
}