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
import com.example.genius.databinding.StaffEntryFragmentFragmentBinding;
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

    StaffEntryFragmentFragmentBinding binding;
    RadioButton rb1;
    private int year;
    private int month;
    private int day;
    String gender, RoleName, BranchID,ddate, apdate, jodate, ledate;
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
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("User Master Entry");
        binding = StaffEntryFragmentFragmentBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        Calendar cal2 = Calendar.getInstance();
        DateFormat dateFormat1 = new SimpleDateFormat("yyyy-MM-dd");
        cal2.add(Calendar.DATE, 0);
        apdate = dateFormat1.format(cal2.getTime());
        jodate = dateFormat1.format(cal2.getTime());
        ledate = dateFormat1.format(cal2.getTime());

        binding.dateOfAppo.setText(yesterday());
        binding.dateOfJoin.setText(yesterday());
        binding.dateOfLeaving.setText(yesterday());

        GetStaffRole();

        bundle = getArguments();
        if (bundle != null) {
            binding.saveStaff.setVisibility(View.GONE);
            binding.editStaff.setVisibility(View.VISIBLE);
            if (bundle.containsKey("StaffID")) {
                binding.idReg.setText("" + bundle.getLong("StaffID"));
            }
            if (bundle.containsKey("USER_ID")){
                userid = bundle.getLong("USER_ID");
            }
            if (bundle.containsKey("TransactionId")) {
                binding.transactionId.setText("" + bundle.getLong("TransactionId"));
            }
            if (bundle.containsKey("Branch_ID")) {
                binding.idBranch.setText("" + bundle.getLong("Branch_ID"));
            }
            if (bundle.containsKey("Name")) {
                binding.fullname.setText(bundle.getString("Name"));
            }
            if (bundle.containsKey("EduQual")) {
                binding.educationQua.setText(bundle.getString("EduQual"));
            }
            if (bundle.containsKey("USER_NAME")){
                binding.userName.setText(bundle.getString("USER_NAME"));
            }
            if (bundle.containsKey("DOB")) {
                try {
                    Date d = actualdate.parse(bundle.getString("DOB"));
                    binding.dateOfBirth.setText("" + displaydate.format(d));
                    ddate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("DOA")) {
                try {
                    Date d = actualdate.parse(bundle.getString("DOA"));
                    binding.dateOfAppo.setText("" + displaydate.format(d));
                    apdate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

            }
            if (bundle.containsKey("DOJ")) {
                try {
                    Date d = actualdate.parse(bundle.getString("DOJ"));
                    binding.dateOfJoin.setText("" + displaydate.format(d));
                    jodate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("DOl")) {
                try {
                    Date d = actualdate.parse(bundle.getString("DOl"));
                    binding.dateOfLeaving.setText("" + displaydate.format(d));
                    ledate = actualdate.format(d);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
            if (bundle.containsKey("Address")) {
                binding.address.setText(bundle.getString("Address"));
            }
            if (bundle.containsKey("Email")) {
                binding.email.setText(bundle.getString("Email"));
            }
            if (bundle.containsKey("Gender")) {
                String gndr = bundle.getString("Gender");
                if (gndr.equals("1")) {
                    binding.male.setChecked(true);
                    binding.female.setChecked(false);
                } else {
                    binding.male.setChecked(false);
                    binding.female.setChecked(true);
                }
            }
            if (bundle.containsKey("MobileNo")) {
                binding.mobileNo.setText(bundle.getString("MobileNo"));
            }
            if (bundle.containsKey("Password")) {
                binding.password.setText(bundle.getString("Password"));
            }
            if (bundle.containsKey("USER_PASSWORD")){
                binding.userPassword.setText(bundle.getString("USER_PASSWORD"));
            }
        }

        binding.hidePassword.setOnClickListener(v -> {
            if (binding.userPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
                binding.hidePassword.setImageResource(R.drawable.eye_on);
                binding.userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                binding.userPassword.setSelection(binding.userPassword.length());
            } else {
                binding.hidePassword.setImageResource(R.drawable.eye_off);
                binding.userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                binding.userPassword.setSelection(binding.userPassword.length());
            }
        });

        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        binding.genderRg.setOnCheckedChangeListener((group, checkedId) -> {
            rb1 = binding.getRoot().findViewById(checkedId);
            gender = rb1.getText().toString();
        });
        select = binding.genderRg.getCheckedRadioButtonId();
        rb1 = binding.getRoot().findViewById(select);
        gender = rb1.getText().toString();

        binding.dateOfBirth.setOnClickListener(v -> {
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
                        binding.dateOfBirth.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });

        binding.dateOfAppo.setOnClickListener(v -> {
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
                        binding.dateOfAppo.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });

        binding.dateOfJoin.setOnClickListener(v -> {
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
                        binding.dateOfJoin.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });

        binding.dateOfLeaving.setOnClickListener(v -> {
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
                        binding.dateOfLeaving.setText(pad(day) + "/" + pad(month + 1) + "/" + year);
                    }, year, month, day);
            picker.show();
        });

        binding.saveStaff.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.fullname.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter your fullname.", Toast.LENGTH_SHORT).show();
                } else if (binding.address.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                } else if (binding.mobileNo.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter mobile number.", Toast.LENGTH_SHORT).show();
                } else if (binding.email.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Email Id.", Toast.LENGTH_SHORT).show();
                } else if (binding.mobileNo.getText().toString().length() < 10){
                    Toast.makeText(context, "Please enter valid mobile number.", Toast.LENGTH_SHORT).show();
                } else if (binding.userName.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter User Name.", Toast.LENGTH_SHORT).show();
                } else if (binding.userPassword.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter Password.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                    StaffModel model = new StaffModel(binding.fullname.getText().toString()
                            , binding.educationQua.getText().toString(), ddate, gender, binding.address.getText().toString(), apdate, jodate
                            , ledate, binding.email.getText().toString(), binding.mobileNo.getText().toString(), transactionModel, rowStatusModel, branchModel, "Staff",
                            binding.userPassword.getText().toString(),binding.userName.getText().toString());
                    Call<StaffModel.StaffData1> call = apiCalling.StaffMaintanance(model);
                    call.enqueue(new Callback<StaffModel.StaffData1>() {
                        @Override
                        public void onResponse(@NotNull Call<StaffModel.StaffData1> call, @NotNull Response<StaffModel.StaffData1> response) {
                            if (response.isSuccessful()) {
                                StaffModel.StaffData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    staff_entry_listfragment profileFragment = new staff_entry_listfragment();
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.nav_host_fragment, profileFragment);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
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

        binding.editStaff.setOnClickListener(v -> {
            if (Function.isNetworkAvailable(context)) {
                if (binding.fullname.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter your fullname.", Toast.LENGTH_SHORT).show();
                } else if (binding.address.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter address.", Toast.LENGTH_SHORT).show();
                } else if (binding.mobileNo.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter mobile number.", Toast.LENGTH_SHORT).show();
                } else if (binding.email.getText().toString().equals("")) {
                    Toast.makeText(context, "Please enter Email Id.", Toast.LENGTH_SHORT).show();
                }else if (binding.mobileNo.getText().toString().length() < 10){
                    Toast.makeText(context, "Please enter valid mobile number.", Toast.LENGTH_SHORT).show();
                } else if (binding.userName.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter User Name.", Toast.LENGTH_SHORT).show();
                } else if (binding.userPassword.getText().toString().isEmpty()){
                    Toast.makeText(context, "Please enter Password.", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(binding.transactionId.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Long.parseLong(BranchID));
                    StaffModel model = new StaffModel(Long.parseLong(binding.idReg.getText().toString())
                            , binding.fullname.getText().toString(), binding.educationQua.getText().toString(), ddate, gender, binding.address.getText().toString()
                            , apdate, jodate, ledate, binding.email.getText().toString(), binding.mobileNo.getText().toString(), transactionModel, rowStatusModel, branchModel, userid,
                            binding.userPassword.getText().toString(),binding.userName.getText().toString());
                    Call<StaffModel.StaffData1> call = apiCalling.StaffMaintanance(model);
                    call.enqueue(new Callback<StaffModel.StaffData1>() {
                        @Override
                        public void onResponse(@NotNull Call<StaffModel.StaffData1> call, @NotNull Response<StaffModel.StaffData1> response) {
                            if (response.isSuccessful()) {
                                StaffModel.StaffData1 data = response.body();
                                if (data.isCompleted()) {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
                                    staff_entry_listfragment profileFragment = new staff_entry_listfragment();
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.nav_host_fragment, profileFragment);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }else {
                                    Toast.makeText(context, data.getMessage(), Toast.LENGTH_SHORT).show();
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
        return binding.getRoot();
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
        binding.role.setAdapter(adapter);
        binding.role.setSelection(1);
        binding.role.setOnItemSelectedListener(onItemSelectedListener7);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    RoleName = roleitem.get(position);
                    if (binding.role.getSelectedItem().equals("Select Role")) {
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

    public static String yesterday() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cal.add(Calendar.DATE, 0);
        return dateFormat.format(cal.getTime());
    }

}