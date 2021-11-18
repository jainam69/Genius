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
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
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

public class master_standardFragment extends Fragment {

    AutoCompleteTextView standard;
    RadioButton standard_active, standard_inactive;
    Button save_standard_master, edit_standard_master;
    RecyclerView standard_rv;
    TextView id, id_branch, text, transaction_id;
    Context context;
    List<String> standarditem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>();
    String[] STANDARDITEM;
    Integer[] STANDARDID;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    NestedScrollView standard_scroll;
    StandardMaster_Adapter standardMaster_adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Standard Master");
        View root = inflater.inflate(R.layout.master_standard_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        standard = root.findViewById(R.id.standard);
        standard_active = root.findViewById(R.id.standard_active);
        standard_inactive = root.findViewById(R.id.standard_inactive);
        save_standard_master = root.findViewById(R.id.save_standard_master);
        edit_standard_master = root.findViewById(R.id.edit_standard_master);
        standard_rv = root.findViewById(R.id.standard_rv);
        id = root.findViewById(R.id.id);
        transaction_id = root.findViewById(R.id.transaction_id);
        id_branch = root.findViewById(R.id.id_branch);
        text = root.findViewById(R.id.text);
        standard_scroll = root.findViewById(R.id.standard_scroll);

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllStandard();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

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

        save_standard_master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Function.hideKeyPad(context,standard);
                if (standard.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Enter Standard", Toast.LENGTH_SHORT).show();
                } else {
                    if (Function.isNetworkAvailable(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        StandardModel model = new StandardModel(standard.getText().toString(), transactionModel, rowStatusModel, branchModel);
                        Call<StandardModel.StandardData1> call = apiCalling.StandardMaintenance(model);
                        call.enqueue(new Callback<StandardModel.StandardData1>() {
                            @Override
                            public void onResponse(Call<StandardModel.StandardData1> call, Response<StandardModel.StandardData1> response) {
                                if (response.isSuccessful()) {
                                    StandardModel.StandardData1 data = response.body();
                                    if (data.isCompleted()) {
                                        StandardModel std_model = data.getData();
                                        if (std_model.getStandardID() > 0) {
                                            Toast.makeText(context, "Standard inserted successfully.", Toast.LENGTH_SHORT).show();
                                            standard.setText("");
                                            GetAllStandard();
                                        }else {
                                            Toast.makeText(context, "Standard Already Exists.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                } else {
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<StandardModel.StandardData1> call, Throwable t) {
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

        edit_standard_master.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (standard.getText().toString().equals("")) {
                    Toast.makeText(context, "Please Enter Standard", Toast.LENGTH_SHORT).show();
                }else {
                    if (Function.isNetworkAvailable(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Long.parseLong(transaction_id.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        StandardModel model = new StandardModel(Long.parseLong(id.getText().toString()), standard.getText().toString(), transactionModel, rowStatusModel, branchModel);
                        Call<StandardModel.StandardData1> call = apiCalling.StandardMaintenance(model);
                        call.enqueue(new Callback<StandardModel.StandardData1>() {
                            @Override
                            public void onResponse(Call<StandardModel.StandardData1> call, Response<StandardModel.StandardData1> response) {
                                if (response.isSuccessful()) {
                                    StandardModel.StandardData1 data = response.body();
                                    if (data.isCompleted()) {
                                        StandardModel std_model = data.getData();
                                        if (std_model.getStandardID() > 0) {
                                            Toast.makeText(context, "Standard updated successfully.", Toast.LENGTH_SHORT).show();
                                            edit_standard_master.setVisibility(View.GONE);
                                            save_standard_master.setVisibility(View.VISIBLE);
                                            standard.setText("");
                                            GetAllStandard();
                                        }else {
                                            Toast.makeText(context, "Standard Already Exists.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                } else {
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<StandardModel.StandardData1> call, Throwable t) {
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
        return root;
    }

    public void GetAllStandard() {
        standarditem.clear();
        standardid.clear();
        Call<StandardData> call = apiCalling.GetAllStandard(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StandardData>() {
            @Override
            public void onResponse(Call<StandardData> call, Response<StandardData> response) {
                if (response.isSuccessful()) {
                    StandardData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<StandardModel> respose = standardData.getData();
                            if (respose.size() > 0) {
                                List<StandardModel> list = new ArrayList<>();
                                for (StandardModel singleResponseModel : respose) {

                                    String std = singleResponseModel.getStandard();
                                    standarditem.add(std);

                                    int stdid = (int) singleResponseModel.getStandardID();
                                    standardid.add(stdid);
                                    if (singleResponseModel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(singleResponseModel);
                                    }
                                }
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                standard_rv.setLayoutManager(linearLayoutManager);
                                standardMaster_adapter = new StandardMaster_Adapter(context, list);
                                standardMaster_adapter.notifyDataSetChanged();
                                standard_rv.setAdapter(standardMaster_adapter);
                                STANDARDITEM = new String[standarditem.size()];
                                STANDARDITEM = standarditem.toArray(STANDARDITEM);

                                STANDARDID = new Integer[standardid.size()];
                                STANDARDID = standardid.toArray(STANDARDID);

                                bindstandard();
                            }

                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
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
        standard.setThreshold(1);
    }

    public class StandardMaster_Adapter extends RecyclerView.Adapter<StandardMaster_Adapter.ViewHolder> {

        Context context;
        List<StandardModel> standardDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        String stname;

        public StandardMaster_Adapter(Context context, List<StandardModel> standardDetails) {
            this.context = context;
            this.standardDetails = standardDetails;
        }

        @Override
        public StandardMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new StandardMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.standard_master_deatil_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull StandardMaster_Adapter.ViewHolder holder, int position) {
            if (standardDetails.get(position).getRowStatus().getRowStatusId() == 1) {
                holder.standard.setText(standardDetails.get(position).getStandard());
                holder.standard_edit.setOnClickListener(new View.OnClickListener() {
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
                        title.setText("Are you sure that you want to Edit Standard?");
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
                                save_standard_master.setVisibility(View.GONE);
                                edit_standard_master.setVisibility(View.VISIBLE);
                                standard.setText(standardDetails.get(position).getStandard());
                                id.setText("" + standardDetails.get(position).getStandardID());
                                transaction_id.setText("" + standardDetails.get(position).getTransaction().getTransactionId());
                                id_branch.setText("" + standardDetails.get(position).getBranchInfo().getBranchID());
                                standard_scroll.fullScroll(View.FOCUS_UP);
                                standard_scroll.scrollTo(0, 0);
                            }
                        });
                        dialog.show();
                    }
                });
                holder.standard_delete.setOnClickListener(new View.OnClickListener() {
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
                        title.setText("Are you sure that you want to delete this Standard?");
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
                                Call<CommonModel> call = apiCalling.RemoveStandard(standardDetails.get(position).getStandardID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                                call.enqueue(new Callback<CommonModel>() {
                                    @Override
                                    public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                        if (response.isSuccessful()) {
                                            CommonModel model = response.body();
                                            if (model.isData()) {
                                                Toast.makeText(context, "Standard deleted successfully.", Toast.LENGTH_SHORT).show();
                                                standardDetails.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
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
            return standardDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView standard, status;
            ImageView standard_edit, standard_delete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                standard = itemView.findViewById(R.id.standard);
                status = itemView.findViewById(R.id.status);
                standard_edit = itemView.findViewById(R.id.standard_edit);
                standard_delete = itemView.findViewById(R.id.standard_delete);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }
}