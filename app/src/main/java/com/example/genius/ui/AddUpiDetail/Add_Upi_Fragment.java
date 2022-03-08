package com.example.genius.ui.AddUpiDetail;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UPIModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Add_Upi_Fragment extends Fragment {

    EditText edt_upiid;
    Button save_upiid,edit_upiid;
    RecyclerView upiid_rv;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    UPIAdapter upiAdapter;
    TextView text;
    UserModel userpermission;
    LinearLayout linear_create_upi;
    long id,transactionid;
    OnBackPressedCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("UPI Details Master");
        View root = inflater.inflate(R.layout.fragment_add__upi_, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        edt_upiid = root.findViewById(R.id.edt_upiid);
        save_upiid = root.findViewById(R.id.save_upiid);
        edit_upiid = root.findViewById(R.id.edit_upiid);
        upiid_rv = root.findViewById(R.id.upiid_rv);
        text = root.findViewById(R.id.text);
        linear_create_upi = root.findViewById(R.id.linear_create_upi);
        userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);

        for (UserModel.UserPermission model : userpermission.getPermission())
        {
            if (model.getPageInfo().getPageID() == 17 && !model.getPackageRightinfo().isCreatestatus()){
                linear_create_upi.setVisibility(View.GONE);
            }
        }

        if (Function.isNetworkAvailable(context)){
            GetAllUPIDetails();
        }else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        save_upiid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (edt_upiid.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please enter UPI ID.", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBarHelper.showProgressDialog();
                        BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        RowStatusModel rowmodel = new RowStatusModel(1);
                        TransactionModel transmodel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                                Preferences.getInstance(context).getString(Preferences.KEY_FINANCIAL_YEAR));
                        UPIModel model = new UPIModel(0,edt_upiid.getText().toString(),branch,rowmodel,transmodel);
                        Call<UPIModel.UPIResponse> call = apiCalling.Save_UPI_ID(model);
                        call.enqueue(new Callback<UPIModel.UPIResponse>() {
                            @Override
                            public void onResponse(Call<UPIModel.UPIResponse> call, Response<UPIModel.UPIResponse> response) {
                                if (response.isSuccessful()){
                                    UPIModel.UPIResponse data = response.body();
                                    if (data.isCompleted()){
                                        Toast.makeText(context, "UPI inserted Successfully.", Toast.LENGTH_SHORT).show();
                                        edt_upiid.setText("");
                                        GetAllUPIDetails();
                                    }else {
                                        Toast.makeText(context, "Please try again!!!", Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<UPIModel.UPIResponse> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_upiid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)){
                    if (edt_upiid.getText().toString().isEmpty()){
                        Toast.makeText(context, "Please enter UPI ID.", Toast.LENGTH_SHORT).show();
                    }else {
                        progressBarHelper.showProgressDialog();
                        BranchModel branch = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        RowStatusModel rowmodel = new RowStatusModel(1);
                        TransactionModel transactionModel = new TransactionModel(transactionid, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0,Preferences.getInstance(context).getString(Preferences.KEY_FINANCIAL_YEAR));
                        UPIModel model = new UPIModel(id,edt_upiid.getText().toString(),branch,rowmodel,transactionModel);
                        Call<UPIModel.UPIResponse> call = apiCalling.Save_UPI_ID(model);
                        call.enqueue(new Callback<UPIModel.UPIResponse>() {
                            @Override
                            public void onResponse(Call<UPIModel.UPIResponse> call, Response<UPIModel.UPIResponse> response) {
                                if (response.isSuccessful()){
                                    UPIModel.UPIResponse data = response.body();
                                    if (data.isCompleted()){
                                        Toast.makeText(context, "UPI updated Successfully.", Toast.LENGTH_SHORT).show();
                                        edt_upiid.setText("");
                                        GetAllUPIDetails();
                                    }else {
                                        Toast.makeText(context, "Please try again!!!", Toast.LENGTH_SHORT).show();
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<UPIModel.UPIResponse> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }else {
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
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

    public void GetAllUPIDetails()
    {
        progressBarHelper.showProgressDialog();
        Call<UPIModel.UPIData> call = apiCalling.Get_All_UPI_Details(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<UPIModel.UPIData>() {
            @Override
            public void onResponse(Call<UPIModel.UPIData> call, Response<UPIModel.UPIData> response) {
                if (response.isSuccessful()){
                    UPIModel.UPIData data = response.body();
                    if (data.isCompleted()){
                        List<UPIModel> list = data.getData();
                        if (list.size() > 0){
                            text.setVisibility(View.VISIBLE);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                            upiid_rv.setLayoutManager(linearLayoutManager);
                            upiAdapter = new UPIAdapter(context, list);
                            upiAdapter.notifyDataSetChanged();
                            upiid_rv.setAdapter(upiAdapter);
                        }else {
                            text.setVisibility(View.GONE);
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<UPIModel.UPIData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public class UPIAdapter extends RecyclerView.Adapter<UPIAdapter.ViewHolder> {

        Context context;
        List<UPIModel> upiModelList;

        public UPIAdapter(Context context, List<UPIModel> upiModelList) {
            this.context = context;
            this.upiModelList = upiModelList;
        }

        @NonNull
        @Override
        public UPIAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new UPIAdapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_upi_details, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull UPIAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
            holder.upi_id.setText(upiModelList.get(position).UPICode);
            holder.upi_edit.setOnClickListener(new View.OnClickListener() {
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
                    title.setText("Are you sure that you want to Edit UPI Detail?");
                    image.setImageResource(R.drawable.ic_edit);
                    AlertDialog dialog = builder.create();

                    btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                    btn_edit_yes.setOnClickListener(v12 -> {
                        dialog.dismiss();
                        final NestedScrollView scroll = ((Activity) context).findViewById(R.id.upi_scroll);
                        save_upiid.setVisibility(View.GONE);
                        edit_upiid.setVisibility(View.VISIBLE);
                        id = upiModelList.get(position).UPIId;
                        edt_upiid.setText(upiModelList.get(position).UPICode);
                        transactionid = upiModelList.get(position).TransactionData.getTransactionId();
                        scroll.scrollTo(0, 0);
                        scroll.fullScroll(View.FOCUS_UP);
                    });
                    dialog.show();
                }
            });
            holder.upi_delete.setOnClickListener(new View.OnClickListener() {
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
                    title.setText("Are you sure that you want to delete this UPI Detail?");
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
                            Call<CommonModel> call = apiCalling.RemoveUPI(upiModelList.get(position).UPIId, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                            call.enqueue(new Callback<CommonModel>() {
                                @Override
                                public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                                    if (response.isSuccessful()) {
                                        CommonModel model = response.body();
                                        if (model.isCompleted()) {
                                            if (model.isData()) {
                                                Toast.makeText(context, "UPI ID deleted successfully.", Toast.LENGTH_SHORT).show();
                                                upiModelList.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
                                            }
                                        }
                                        progressBarHelper.hideProgressDialog();
                                    }
                                }

                                @Override
                                public void onFailure(@NotNull Call<CommonModel> call, @NotNull Throwable t) {
                                    progressBarHelper.hideProgressDialog();
                                    Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
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
            return upiModelList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView upi_id;
            ImageView upi_edit,upi_delete;
            LinearLayout linear_actions;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                linear_actions = itemView.findViewById(R.id.linear_actions);
                upi_id = itemView.findViewById(R.id.upi_id);
                upi_edit = itemView.findViewById(R.id.upi_edit);
                upi_delete = itemView.findViewById(R.id.upi_delete);
            }
        }
    }
}