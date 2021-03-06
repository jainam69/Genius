package com.example.genius.Adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.UserModel;
import com.example.genius.databinding.StaffEntryDeatilListBinding;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Staff_Entry_Fragment.staff_entry_fragment;
import com.google.gson.Gson;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StaffMaster_Adapter extends RecyclerView.Adapter<StaffMaster_Adapter.ViewHolder> {

    Context context;
    List<StaffModel> staffDetails;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    UserModel.PageData userpermission;

    public StaffMaster_Adapter(Context context, List<StaffModel> staffDetails) {
        this.context = context;
        this.staffDetails = staffDetails;
    }

    @Override
    public StaffMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(StaffEntryDeatilListBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull StaffMaster_Adapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        for (UserModel.PageInfoEntity model : userpermission.Data)
        {
            if (model.getPageID() == 4){
                if (!model.Createstatus){
                    holder.binding.staffEdit.setVisibility(View.GONE);
                }
                if (!model.Deletestatus){
                    holder.binding.staffDelete.setVisibility(View.GONE);
                }
                if (!model.Createstatus && !model.Deletestatus){
                    holder.binding.linearActions.setVisibility(View.GONE);
                }
            }
        }
        if (staffDetails.get(position).getRowStatus().getRowStatusId()==1){
            holder.binding.staffName.setText(staffDetails.get(position).getName());
            holder.binding.mobileNo.setText(staffDetails.get(position).getMobileNo());
            holder.binding.email.setText(staffDetails.get(position).getEmailID());
            String grnd = staffDetails.get(position).getGender();
            if (grnd.equals("1")){
                holder.binding.gender.setText("Male");
            }else {
                holder.binding.gender.setText("Female");
            }
            holder.binding.staffEdit.setOnClickListener(new View.OnClickListener() {
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
                    title.setText("Are you sure that you want to Edit User?");
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
                            staff_entry_fragment orderplace = new staff_entry_fragment();
                            Bundle bundle = new Bundle();
                            bundle.putLong("StaffID", staffDetails.get(position).getStaffID());
                            bundle.putString("Name", staffDetails.get(position).getName());
                            bundle.putString("EduQual", staffDetails.get(position).getEducation());
                            if (staffDetails.get(position).getDOB() != null)
                                bundle.putString("DOB", staffDetails.get(position).getDOB().replace("T00:00:00",""));
                            if (staffDetails.get(position).getApptDT() != null)
                                bundle.putString("DOA", staffDetails.get(position).getApptDT().replace("T00:00:00",""));
                            if (staffDetails.get(position).getJoinDT() != null)
                                bundle.putString("DOJ", staffDetails.get(position).getJoinDT().replace("T00:00:00",""));
                            if (staffDetails.get(position).getLeavingDT() != null)
                                bundle.putString("DOl", staffDetails.get(position).getLeavingDT().replace("T00:00:00",""));
                            bundle.putString("Address", staffDetails.get(position).getAddress());
                            bundle.putString("Email", staffDetails.get(position).getEmailID());
                            bundle.putString("Gender", staffDetails.get(position).getGender());
                            bundle.putString("MobileNo", staffDetails.get(position).getMobileNo());
                            bundle.putInt("Status", staffDetails.get(position).getRowStatus().getRowStatusId());
                            bundle.putLong("Branch_ID", staffDetails.get(position).getBranchInfo().getBranchID());
                            bundle.putLong("USER_ID",staffDetails.get(position).getUserID());
                            bundle.putString("USER_PASSWORD",staffDetails.get(position).getUser_Password());
                            bundle.putString("USER_NAME",staffDetails.get(position).getUserNameNew());
                            bundle.putLong("TransactionId",staffDetails.get(position).getTransaction().getTransactionId());
                            orderplace.setArguments(bundle);
                            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                            fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    });
                    dialog.show();
                }
            });
            holder.binding.staffDelete.setOnClickListener(new View.OnClickListener() {
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
                    title.setText("Are you sure that you want to delete this Staff?");
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
                            Call<CommonModel> call = apiCalling.RemoveStaff(staffDetails.get(position).getStaffID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                            call.enqueue(new Callback<CommonModel>() {
                                @Override
                                public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                    if (response.isSuccessful()) {
                                        CommonModel model = response.body();
                                        if (model.isCompleted()) {
                                            if (model.isData()) {
                                                Toast.makeText(context,model.getMessage(), Toast.LENGTH_SHORT).show();
                                                staffDetails.remove(position);
                                                notifyItemRemoved(position);
                                                notifyDataSetChanged();
                                            }else {
                                                Toast.makeText(context,model.getMessage(), Toast.LENGTH_SHORT).show();
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

    }

    @Override
    public int getItemCount() {
        return staffDetails.size();
    }

    public void filterList(List<StaffModel> filteredList) {
        staffDetails = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        StaffEntryDeatilListBinding binding;

        public ViewHolder(@NonNull StaffEntryDeatilListBinding itemView) {
            super(itemView.getRoot());
            binding = itemView;
            userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.PageData.class);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }

}
