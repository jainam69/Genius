package com.example.genius.ui.Staff_Entry_Fragment;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StaffModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

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

@SuppressLint({"SetTextI18n", "SimpleDateFormat"})
public class staff_entry_fragment extends Fragment {

    EditText date_of_birth, date_of_appo, date_of_join, date_of_leaving, fullname, education_qua, address, email, mobile_no, password,user_password;
    Button save_staff, edit_staff;
    RadioGroup gender_rg, status_rg;
    RadioButton active, inactive, male, female, rb1, rb2;
    SearchableSpinner role, branch;
    TextView id_reg, id_branch, transaction_id;
    ImageView hide_password;
    private int year;
    private int month;
    private int day;
    String gender, status,RoleName, BranchID;
    String ddate, apdate, jodate, ledate;
    int select;
    Context context;
    List<String> roleitem = new ArrayList<>();
    String[] ROLEITEM;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    Bundle bundle;
    long userid;
    DateFormat displaydate = new SimpleDateFormat("dd/MM/yyyy");
    DateFormat actualdate = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("User Entry");
        View root = inflater.inflate(R.layout.staff_entry_fragment_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        date_of_birth = root.findViewById(R.id.date_of_birth);
        date_of_appo = root.findViewById(R.id.date_of_appo);
        date_of_join = root.findViewById(R.id.date_of_join);
        date_of_leaving = root.findViewById(R.id.date_of_leaving);
        role = root.findViewById(R.id.role);
        branch = root.findViewById(R.id.branch);
        fullname = root.findViewById(R.id.fullname);
        education_qua = root.findViewById(R.id.education_qua);
        address = root.findViewById(R.id.address);
        email = root.findViewById(R.id.email);
        mobile_no = root.findViewById(R.id.mobile_no);
        password = root.findViewById(R.id.password);
        save_staff = root.findViewById(R.id.save_staff);
        edit_staff = root.findViewById(R.id.edit_staff);
        active = root.findViewById(R.id.active);
        inactive = root.findViewById(R.id.inactive);
        male = root.findViewById(R.id.male);
        female = root.findViewById(R.id.female);
        gender_rg = root.findViewById(R.id.gender_rg);
        status_rg = root.findViewById(R.id.status_rg);
        id_reg = root.findViewById(R.id.id_reg);
        id_branch = root.findViewById(R.id.id_branch);
        transaction_id = root.findViewById(R.id.transaction_id);
        user_password = root.findViewById(R.id.user_password);
        hide_password = root.findViewById(R.id.hide_password);
        GetStaffRole();

        bundle = getArguments();
        if (bundle != null) {
            save_staff.setVisibility(View.GONE);
            edit_staff.setVisibility(View.VISIBLE);
            if (bundle.containsKey("StaffID")) {
                id_reg.setText("" + bundle.getLong("StaffID"));
            }
            if (bundle.containsKey("USER_ID")){
                userid = bundle.getLong("USER_ID");
            }
            if (bundle.containsKey("TransactionId")) {
                transaction_id.setText("" + bundle.getLong("TransactionId"));
            }
            if (bundle.containsKey("Branch_ID")) {
                id_branch.setText("" + bundle.getLong("Branch_ID"));
            }
            if (bundle.containsKey("Name")) {
                fullname.setText(bundle.getString("Name"));
            }
            if (bundle.containsKey("EduQual")) {
                education_qua.setText(bundle.getString("EduQual"));
            }
            if (bundle.containsKey("DOB")) {
                try {
                    Date d = actualdate.parse(bundle.getString("DOB"));
                    date_of_birth.setText("" + displaydate.format(d));
                    ddate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("DOA")) {
                try {
                    Date d = actualdate.parse(bundle.getString("DOA"));
                    date_of_appo.setText("" + displaydate.format(d));
                    apdate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            if (bundle.containsKey("DOJ")) {
                try {
                    Date d = actualdate.parse(bundle.getString("DOJ"));
                    date_of_join.setText("" + displaydate.format(d));
                    jodate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("DOl")) {
                try {
                    Date d = actualdate.parse(bundle.getString("DOl"));
                    date_of_leaving.setText("" + displaydate.format(d));
                    ledate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("Address")) {
                address.setText(bundle.getString("Address"));
            }
            if (bundle.containsKey("Email")) {
                email.setText(bundle.getString("Email"));
            }
            if (bundle.containsKey("Gender")) {
                String gndr = bundle.getString("Gender");
                if (gndr.equals("1")) {
                    male.setChecked(true);
                    female.setChecked(false);
                } else {
                    male.setChecked(false);
                    female.setChecked(true);
                }
            }
            if (bundle.containsKey("MobileNo")) {
                mobile_no.setText(bundle.getString("MobileNo"));
            }
            if (bundle.containsKey("Password")) {
                password.setText(bundle.getString("Password"));
            }
            if (bundle.containsKey("USER_PASSWORD")){
                user_password.setText(bundle.getString("USER_PASSWORD"));
            }
            if (bundle.containsKey("Status")) {
                int st = bundle.getInt("Status");
                if (st == 1) {
                    active.setChecked(true);
                    inactive.setChecked(false);
                }
                if (st == 2) {
                    active.setChecked(false);
                    inactive.setChecked(true);
                }
            }
        }

        hide_password.setOnClickListener(v -> {
            if (user_password.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                hide_password.setImageResource(R.drawable.eye_on);
                user_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                user_password.setSelection(user_password.length());
            } else {
                hide_password.setImageResource(R.drawable.eye_off);
                user_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                user_password.setSelection(user_password.length());
            }
        });

        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        gender_rg.setOnCheckedChangeListener((group, checkedId) -> {
            rb1 = root.findViewById(checkedId);
            gender = rb1.getText().toString();
        });
        select = gender_rg.getCheckedRadioButtonId();
        rb1 = root.findViewById(select);
        gender = rb1.getText().toString();

        status_rg.setOnCheckedChangeListener((group, checkedId) -> {
            rb2 = root.findViewById(checkedId);
            status = rb2.getText().toString();
        });
        select = status_rg.getCheckedRadioButtonId();
        rb2 = root.findViewById(select);
        status = rb2.getText().toString();

        date_of_birth.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        ddate = pad(month + 1) + "/" + pad(day) + "/" + year;
                        date_of_birth.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });
        date_of_appo.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        apdate = pad(month + 1) + "/" + pad(day) + "/" + year;
                        date_of_appo.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });
        date_of_join.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        jodate = pad(month + 1) + "/" + pad(day) + "/" + year;
                        date_of_join.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });
        date_of_leaving.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();

            year = c.get(Calendar.YEAR);
            month = c.get(Calendar.MONTH);
            day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog picker = new DatePickerDialog(getActivity(),
                    (view, year2, monthOfYear, dayOfMonth) -> {
                        year = year2;
                        month = monthOfYear;
                        day = dayOfMonth;
                        ledate = pad(month + 1) + "/" + pad(day) + "/" + year;
                        date_of_leaving.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });

        save_staff.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (fullname.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter your fullname.", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                } else if (mobile_no.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter mobile number(login id).", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Email Id.", Toast.LENGTH_SHORT).show();
                } else if (mobile_no.getText().toString().length() < 10){
                    Toast.makeText(context, "Please enter valid mobile number.", Toast.LENGTH_SHORT).show();
                }else if (user_password.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter Password.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME),
                            Preferences.getInstance(context).getString(Preferences.KEY_FINANCIAL_YEAR));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                    StaffModel model = new StaffModel(fullname.getText().toString()
                            , education_qua.getText().toString(), ddate, gender, address.getText().toString(), apdate, jodate
                            , ledate, email.getText().toString(), mobile_no.getText().toString(), transactionModel, rowStatusModel, branchModel, "Staff",
                            user_password.getText().toString());
                    Call<StaffModel.StaffData1> call = apiCalling.StaffMaintanance(model);
                    call.enqueue(new Callback<StaffModel.StaffData1>() {
                        @Override
                        public void onResponse(@NotNull Call<StaffModel.StaffData1> call, @NotNull Response<StaffModel.StaffData1> response) {
                            if (response.isSuccessful()) {
                                StaffModel.StaffData1 data = response.body();
                                if (data.isCompleted()) {
                                    StaffModel staffModel = data.getData();
                                    if (staffModel.getStaffID() > 0) {
                                        Toast.makeText(context, "User inserted successfully.", Toast.LENGTH_SHORT).show();
                                        staff_entry_listfragment profileFragment = new staff_entry_listfragment();
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.nav_host_fragment, profileFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    } else {
                                        Toast.makeText(context, "User Already Exists.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<StaffModel.StaffData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_staff.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (fullname.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter your fullname.", Toast.LENGTH_SHORT).show();
                } else if (address.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                } else if (mobile_no.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter mobile number(login id).", Toast.LENGTH_SHORT).show();
                } else if (email.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Email Id.", Toast.LENGTH_SHORT).show();
                }else if (mobile_no.getText().toString().length() < 10){
                    Toast.makeText(context, "Please enter valid mobile number.", Toast.LENGTH_SHORT).show();
                }else if (user_password.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter Password.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(transaction_id.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0,
                            Preferences.getInstance(context).getString(Preferences.KEY_FINANCIAL_YEAR));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                    StaffModel model = new StaffModel(Long.parseLong(id_reg.getText().toString())
                            , fullname.getText().toString(), education_qua.getText().toString(), ddate, gender, address.getText().toString()
                            , apdate, jodate, ledate, email.getText().toString(), mobile_no.getText().toString(), transactionModel, rowStatusModel, branchModel, userid,
                            user_password.getText().toString());
                    Call<StaffModel.StaffData1> call = apiCalling.StaffMaintanance(model);
                    call.enqueue(new Callback<StaffModel.StaffData1>() {
                        @Override
                        public void onResponse(@NotNull Call<StaffModel.StaffData1> call, @NotNull Response<StaffModel.StaffData1> response) {
                            if (response.isSuccessful()) {
                                StaffModel.StaffData1 data = response.body();
                                if (data.isCompleted()) {
                                    StaffModel staffModel = data.getData();
                                    if (staffModel.getStaffID() > 0) {
                                        Toast.makeText(context, "User Updated Successfully.", Toast.LENGTH_SHORT).show();
                                        staff_entry_listfragment profileFragment = new staff_entry_listfragment();
                                        FragmentManager fm = getActivity().getSupportFragmentManager();
                                        FragmentTransaction ft = fm.beginTransaction();
                                        ft.replace(R.id.nav_host_fragment, profileFragment);
                                        ft.addToBackStack(null);
                                        ft.commit();
                                    } else {
                                        Toast.makeText(context, "User Already Exists.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<StaffModel.StaffData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
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
                staff_entry_listfragment profileFragment = new staff_entry_listfragment();
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

    public void GetStaffRole() {

        roleitem.clear();
        roleitem.add("Select Role");
        roleitem.add("Staff");

        ROLEITEM = new String[roleitem.size()];
        ROLEITEM = roleitem.toArray(ROLEITEM);

        bindrole();
    }

    public void bindrole() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, ROLEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        role.setAdapter(adapter);
        role.setSelection(1);
        role.setOnItemSelectedListener(onItemSelectedListener7);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    RoleName = roleitem.get(position);
                    if (role.getSelectedItem().equals("Select Role")) {
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

    private static String pad(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + c;
    }

}