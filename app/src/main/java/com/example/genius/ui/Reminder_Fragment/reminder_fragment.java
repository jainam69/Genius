package com.example.genius.ui.Reminder_Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BannerData;
import com.example.genius.Model.BannerModel;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.ReminderData;
import com.example.genius.Model.ReminderModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Banner.Banner_Fragment;
import com.example.genius.ui.Home_Fragment.home_fragment;

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

@SuppressLint({"SimpleDateFormat", "SetTextI18n"})
public class reminder_fragment extends Fragment {

    View root;
    String format, toDate;
    Context context;
    TextView text, id, transactionid, reminderid;
    EditText reminder_time, date_reminder, edt_reminderDescription;
    RecyclerView reminder_rv;
    Button save_reminder, edit_reminder;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    private int year;
    private int month;
    private int day;
    OnBackPressedCallback callback;
    NestedScrollView reminder_scroll;
    int hour1, minute1;
    String date;
    Reminder_Adapter reminder_adapter;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Reminder");
        root = inflater.inflate(R.layout.reminder_fragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        reminder_time = root.findViewById(R.id.reminder_time);
        date_reminder = root.findViewById(R.id.date_reminder);
        text = root.findViewById(R.id.text);
        edt_reminderDescription = root.findViewById(R.id.edt_reminderDescription);
        save_reminder = root.findViewById(R.id.save_reminder);
        edit_reminder = root.findViewById(R.id.edit_reminder);
        reminder_rv = root.findViewById(R.id.reminder_rv);
        id = root.findViewById(R.id.id);
        reminderid = root.findViewById(R.id.reminderid);
        transactionid = root.findViewById(R.id.transactionid);
        reminder_scroll = root.findViewById(R.id.reminder_scroll);

        date_reminder.setText(yesterday());
        Calendar cal2 = Calendar.getInstance();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        cal2.add(Calendar.DATE, 0);
        date = dateFormat1.format(cal2.getTime());
        save_reminder.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (edt_reminderDescription.getText().toString().equals(""))
                    Toast.makeText(context, "Please Enter Description", Toast.LENGTH_SHORT).show();
                else if (reminder_time.getText().toString().equals(""))
                    Toast.makeText(context, "Please Select Time", Toast.LENGTH_SHORT).show();
                else if (date_reminder.getText().toString().equals(""))
                    Toast.makeText(context, "Please Select Date", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    ReminderModel model = new ReminderModel(branchModel, Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), date, reminder_time.getText().toString(), edt_reminderDescription.getText().toString(), transactionModel, rowStatusModel);
                    Call<ReminderModel.ReminderData1> call = apiCalling.ReminderMaintenance(model);
                    call.enqueue(new Callback<ReminderModel.ReminderData1>() {
                        @Override
                        public void onResponse(@NotNull Call<ReminderModel.ReminderData1> call, @NotNull Response<ReminderModel.ReminderData1> response) {
                            if (response.isSuccessful()) {
                                ReminderModel.ReminderData1 data = response.body();
                                if (data != null) {
                                    ReminderModel reminderModel = data.getData();
                                    if (reminderModel.getReminderID() > 0) {
                                        edt_reminderDescription.setText("");
                                        reminder_time.setText("");
                                        date_reminder.setText(yesterday());
                                        GetReminderDetails();
                                        Toast.makeText(context, "Reminder Added Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<ReminderModel.ReminderData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_reminder.setOnClickListener(v -> {
            if (Function.checkNetworkConnection(context)) {
                if (edt_reminderDescription.getText().toString().equals(""))
                    Toast.makeText(context, "Please Enter Description", Toast.LENGTH_SHORT).show();
                else if (reminder_time.getText().toString().equals(""))
                    Toast.makeText(context, "Please Select Time", Toast.LENGTH_SHORT).show();
                else if (date_reminder.getText().toString().equals(""))
                    Toast.makeText(context, "Please Select Date", Toast.LENGTH_SHORT).show();
                else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(transactionid.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    ReminderModel model = new ReminderModel(Long.parseLong(reminderid.getText().toString()), branchModel, Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), date, reminder_time.getText().toString(), edt_reminderDescription.getText().toString(), transactionModel, rowStatusModel);
                    Call<ReminderModel.ReminderData1> call = apiCalling.ReminderMaintenance(model);
                    call.enqueue(new Callback<ReminderModel.ReminderData1>() {
                        @Override
                        public void onResponse(@NotNull Call<ReminderModel.ReminderData1> call, @NotNull Response<ReminderModel.ReminderData1> response) {
                            if (response.isSuccessful()) {
                                ReminderModel.ReminderData1 data = response.body();
                                if (data != null) {
                                    ReminderModel reminderModel = data.getData();
                                    if (reminderModel.getReminderID() > 0) {
                                        edt_reminderDescription.setText("");
                                        reminder_time.setText("");
                                        date_reminder.setText(yesterday());
                                        GetReminderDetails();
                                        Toast.makeText(context, "Reminder Edited Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<ReminderModel.ReminderData1> call, @NotNull Throwable t) {

                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetReminderDetails();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        reminder_time.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog timePickerDialog = new TimePickerDialog(context,
                    (view, hourOfDay, minute2) -> {
                        if (hourOfDay == 0) {
                            hourOfDay += 12;
                            format = "AM";
                        } else if (hourOfDay == 12) {
                            format = "PM";
                        } else if (hourOfDay > 12) {
                            hourOfDay -= 12;
                            format = "PM";
                        } else {
                            format = "AM";
                        }
                        hour1 = hourOfDay;
                        minute1 = minute2;
                        reminder_time.setText(hourOfDay + ":" + minute2 + " " + format);
                    }, hour, minute, false);
            timePickerDialog.show();
        });

        date_reminder.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(context,
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        date_reminder.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                        date = pad(year) + "-" + pad(month + 1) + "-" + day;
                    }, year, month, day);
            picker.show();
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

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

    public static String yesterday() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cal.add(Calendar.DATE, 0);
        return dateFormat.format(cal.getTime());
    }

    public void GetReminderDetails() {
        Call<ReminderData> call = apiCalling.GetAllReminderByBranchUser(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID));
        call.enqueue(new Callback<ReminderData>() {
            @Override
            public void onResponse(@NotNull Call<ReminderData> call, @NotNull Response<ReminderData> response) {
                if (response.isSuccessful()) {
                    ReminderData reminderData = response.body();
                    if (reminderData.isCompleted()) {
                        List<ReminderModel> reminderModelList = reminderData.getData();
                        if (reminderModelList != null) {
                            if (reminderModelList.size() > 0) {
                                reminder_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
                                reminder_adapter = new Reminder_Adapter(context, reminderModelList);
                                reminder_adapter.notifyDataSetChanged();
                                reminder_rv.setAdapter(reminder_adapter);
                            }
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(@NotNull Call<ReminderData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public class Reminder_Adapter extends RecyclerView.Adapter<Reminder_Adapter.ViewHolder> {

        Context context;
        List<ReminderModel> reminderModels;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        int id;

        public Reminder_Adapter(Context context, List<ReminderModel> reminderModels) {
            this.context = context;
            this.reminderModels = reminderModels;
        }

        @NotNull
        @Override
        public Reminder_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new Reminder_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_master_deatil_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull Reminder_Adapter.ViewHolder holder, int position) {
            if (reminderModels.get(position).getReminderDate() != null) {
                String a = reminderModels.get(position).getReminderDate().replace("T00:00:00", "");
                try {
                    Date d = actualdate.parse(a);
                    holder.task_date.setText("" + displaydate.format(d));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            holder.task_time.setText("" + reminderModels.get(position).getReminderTime());
            holder.desc.setText("" + reminderModels.get(position).getReminderDesc());
            holder.reminder_edit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Edit Reminder?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    save_reminder.setVisibility(View.GONE);
                    edit_reminder.setVisibility(View.VISIBLE);
                    transactionid.setText("" + reminderModels.get(position).getTransaction().getTransactionId());
                    reminderid.setText("" + reminderModels.get(position).getReminderID());
                    if (reminderModels.get(position).getReminderDate() != null) {
                        String a = reminderModels.get(position).getReminderDate().replace("T00:00:00", "");
                        try {
                            Date d = actualdate.parse(a);
                            date_reminder.setText("" + displaydate.format(d));
                            date = actualdate.format(d).toString();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                    reminder_time.setText("" + reminderModels.get(position).getReminderTime());
                    edt_reminderDescription.setText("" + reminderModels.get(position).getReminderDesc());
                    reminder_scroll.scrollTo(0, 0);
                    reminder_scroll.fullScroll(View.FOCUS_UP);
                });
                dialog.show();
            });
            holder.reminder_delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this Reminder?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(v14 -> dialog.dismiss());

                btn_delete.setOnClickListener(v13 -> {
                    dialog.dismiss();
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.RemoveReminder(reminderModels.get(position).getReminderID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel model = response.body();
                                if (model.isCompleted()) {
                                    if (model.isData()) {
                                        reminderModels.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
                                    }
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
            return reminderModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView reminder_edit, reminder_delete;
            TextView task_date, task_time, desc;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                reminder_edit = itemView.findViewById(R.id.reminder_edit);
                reminder_delete = itemView.findViewById(R.id.reminder_delete);
                task_date = itemView.findViewById(R.id.task_date);
                task_time = itemView.findViewById(R.id.task_time);
                desc = itemView.findViewById(R.id.desc);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }

    }

}