package com.example.genius.ui.Notification;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.NotificationData;
import com.example.genius.Model.NotificationModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Masters_Fragment.MasterSelectorFragment;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationFragment extends Fragment {

    SearchableSpinner branch;
    CheckBox ch_admin, ch_teacher, ch_student;
    Long adminid = Long.valueOf(0), teacherid = Long.valueOf(0), studentid = Long.valueOf(0);
    TextView text, id, image, transaction_id, notification_id;
    RecyclerView notification_rv;
    Button save_notification, edit_notification;
    Context context;
    ProgressBarHelper progressBarHelper;
    EditText notification_message;
    ApiCalling apiCalling;
    List<String> branchitem = new ArrayList<>();
    List<Integer> branchid = new ArrayList<>();
    String[] BRANCHITEM;
    Integer[] BRANCHID;
    int BranchId = 0, flag = 0, check_value_admin, check_value_teacher, check_value_student;
    OnBackPressedCallback callback;
    NestedScrollView notification_scroll;
    String BranchName, BranchID;
    Notification_Adapter notification_adapter;

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    BranchName = branchitem.get(position);
                    BranchID = String.valueOf(branchid.get(position));
                    if (branch.getSelectedItem().equals("Select Branch")) {
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
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

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
                } else {
                    check_value_teacher = 0;
                }
            }
        });
        ch_student.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    check_value_student = 1;
                } else {
                    check_value_student = 0;
                }
            }
        });

        save_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.isNetworkAvailable(context)) {
                    if (notification_message.getText().toString().equals("")) {
                        Toast.makeText(context, "Please Enter Description.", Toast.LENGTH_SHORT).show();
                    } else {
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
                        NotificationModel model = new NotificationModel(typeModel, branchModel, rowStatusModel, transactionModel, notification_message.getText().toString());
                        Call<NotificationModel.NotificationData1> call = apiCalling.NotificationMaintanance(model);

                        call.enqueue(new Callback<NotificationModel.NotificationData1>() {
                            @Override
                            public void onResponse(@NotNull Call<NotificationModel.NotificationData1> call, @NotNull Response<NotificationModel.NotificationData1> response) {
                                if (response.isSuccessful()) {
                                    NotificationModel.NotificationData1 data = response.body();
                                    if (data.isCompleted()) {
                                        NotificationModel notimodel = data.getData();
                                        if (notimodel != null) {
                                            Toast.makeText(context, "Notification inserted successfully.", Toast.LENGTH_SHORT).show();
                                            notification_message.setText("");
                                            branch.setSelection(0);
                                            ch_admin.setChecked(false);
                                            ch_student.setChecked(false);
                                            ch_teacher.setChecked(false);
                                            GetAllNotification();
                                        } else {
                                            Toast.makeText(context, "Notification not Inserted.", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<NotificationModel.NotificationData1> call, @NotNull Throwable t) {
                                progressBarHelper.hideProgressDialog();
                            }
                        });
                    }
                } else {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_notification.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Function.checkNetworkConnection(context)) {
                    if (notification_message.getText().toString().equals("")) {
                        Toast.makeText(context, "Please Enter Description.", Toast.LENGTH_SHORT).show();
                    } else {
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
                        NotificationModel model = new NotificationModel(Long.parseLong(notification_id.getText().toString()), typeModel, branchModel, rowStatusModel, transactionModel, notification_message.getText().toString());
                        Call<NotificationModel.NotificationData1> call = apiCalling.NotificationMaintanance(model);

                        call.enqueue(new Callback<NotificationModel.NotificationData1>() {
                            @Override
                            public void onResponse(Call<NotificationModel.NotificationData1> call, Response<NotificationModel.NotificationData1> response) {
                                if (response.isSuccessful()) {
                                    NotificationModel.NotificationData1 data = response.body();
                                    if (data.isCompleted()) {
                                        NotificationModel notimodel = data.getData();
                                        if (notimodel != null) {
                                            Toast.makeText(context, "Notification updated successfully.", Toast.LENGTH_SHORT).show();
                                            notification_message.setText("");
                                            branch.setSelection(0);
                                            ch_admin.setChecked(false);
                                            ch_student.setChecked(false);
                                            ch_teacher.setChecked(false);
                                            GetAllNotification();
                                        } else {
                                            Toast.makeText(context, "Notification not Updated.", Toast.LENGTH_SHORT).show();
                                        }
                                        save_notification.setVisibility(View.VISIBLE);
                                        edit_notification.setVisibility(View.GONE);
                                    }
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(Call<NotificationModel.NotificationData1> call, Throwable t) {
                                progressBarHelper.hideProgressDialog();
                            }
                        });
                    }
                } else {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetAllNotification();
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
        return root;
    }

    public void GetAllNotification() {
        progressBarHelper.showProgressDialog();
        Call<NotificationData> call = apiCalling.GetAllNotificationBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
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
                                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                    notification_rv.setLayoutManager(linearLayoutManager);
                                    notification_adapter = new Notification_Adapter(context, respose);
                                    notification_adapter.notifyDataSetChanged();
                                    notification_rv.setAdapter(notification_adapter);
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

    public void GetAllBranch() {
        branchitem.add("Select Branch");
        branchid.add(0);

        Call<BranchModel> call = apiCalling.GetAllBranch();
        call.enqueue(new Callback<BranchModel>() {
            @Override
            public void onResponse(Call<BranchModel> call, Response<BranchModel> response) {
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
            public void onFailure(Call<BranchModel> call, Throwable t) {
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

    public class Notification_Adapter extends RecyclerView.Adapter<Notification_Adapter.ViewHolder> {

        Context context;
        List<NotificationModel> notificationDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;

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
        public void onBindViewHolder(@NonNull Notification_Adapter.ViewHolder holder, int position) {
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

            TextView noti_desc, sub_type;
            ImageView noti_delete, noti_edit;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                noti_desc = itemView.findViewById(R.id.noti_desc);
                sub_type = itemView.findViewById(R.id.sub_type);
                noti_delete = itemView.findViewById(R.id.noti_delete);
                noti_edit = itemView.findViewById(R.id.noti_edit);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }
}