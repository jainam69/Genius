package com.example.genius.ui.Permission;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Adapter.UserRole_Adapter;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.RolesModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserData1;
import com.example.genius.Model.UserModel;
import com.example.genius.Model.UserRolesModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PermissionFragment extends Fragment {

    View root;
    EditText no, permission_date;
    SearchableSpinner user_name;
    Button save_permission, edit_permission;
    RecyclerView userpermission_rv;
    //    UserPermissionList_Adapter userPermissionList_adapter;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> usernameitem = new ArrayList<>();
    List<Integer> usernameid = new ArrayList<>();
    String[] USERNAMEITEM;
    Integer[] USERNAMEID;
    int UserName;
    Bundle bundle;
    OnBackPressedCallback callback;
    UserRole_Adapter userRole_adapter;
    List<RolesModel> rolesModels = new ArrayList<>();
    List<RolesModel> rolesModels1 = new ArrayList<>();
    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    UserName = usernameid.get(position);
                    if (user_name.getSelectedItem().equals("Select User Name")) {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                        ((TextView) parent.getChildAt(0)).setTextSize(13);
                        no.setText("" + 0);
                    } else {
                        ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                        ((TextView) parent.getChildAt(0)).setTextSize(14);
                        no.setText("" + usernameid.get(position));
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
    List<RolesModel> modelList;

    public static String yesterday() {
        Calendar cal = Calendar.getInstance();
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        cal.add(Calendar.DATE, 0);
        return dateFormat.format(cal.getTime());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("User Permission");
        root = inflater.inflate(R.layout.fragment_permission, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        no = root.findViewById(R.id.no);
        permission_date = root.findViewById(R.id.permission_date);
        user_name = root.findViewById(R.id.user_name);
        userpermission_rv = root.findViewById(R.id.userpermission_rv);
        save_permission = root.findViewById(R.id.save_permission);
        edit_permission = root.findViewById(R.id.edit_permission);
        bundle = getArguments();
        permission_date.setText(yesterday());
        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            if (bundle != null) {
                if (bundle.containsKey("Emp_Name")) {
                    GetUserName();
                }
            } else {
                GetUserPermissionList();
                GetUserName();
            }
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }
        save_permission.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user_name.getSelectedItem().equals("Select User Name")) {
                    Toast.makeText(context, "Select User Name", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    rolesModels = new ArrayList<>();
                    for (RolesModel rolemodel1 : UserRole_Adapter.permissionlist) {
                        rolemodel1.setUserID(Long.parseLong(String.valueOf(UserName)));
                        rolesModels.add(rolemodel1);
                    }
                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    UserModel model = new UserModel(Long.parseLong(String.valueOf(UserName)), transactionModel, rolesModels);
                    UserRole_Adapter.permissionlist.clear();
                    Call<CommonModel> call = apiCalling.UserRoleManagement(model);
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel commonModel = response.body();
                                if (commonModel.isData()) {
                                    user_name.setSelection(0);
                                    GetUserPermissionList();
                                    no.setText("");
                                    UserRole_Adapter.permissionlist.clear();
                                    Toast.makeText(context, "Permissions Inserted Successfully!!", Toast.LENGTH_SHORT).show();
                                    PermissionListFragment profileFragment = new PermissionListFragment();
                                    FragmentManager fm = getActivity().getSupportFragmentManager();
                                    FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.nav_host_fragment, profileFragment);
                                    ft.addToBackStack(null);
                                    ft.commit();
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(Call<CommonModel> call, Throwable t) {
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                }

            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                PermissionListFragment profileFragment = new PermissionListFragment();
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

    public void GetUserPermissionList() {
        progressBarHelper.showProgressDialog();
        rolesModels1 = new ArrayList<>();
        Call<UserRolesModel> call = apiCalling.GetUserRoleList();
        call.enqueue(new Callback<UserRolesModel>() {
            @Override
            public void onResponse(Call<UserRolesModel> call, Response<UserRolesModel> response) {
                if (response.isSuccessful()) {
                    UserRolesModel rolesModel = response.body();
                    if (rolesModel.isCompleted()) {
                        if (rolesModel.getData().size() > 0) {
                            for (int i = 0; i < rolesModel.getData().size(); i++) {
                                // key
                                String key = (new ArrayList<>(rolesModel.getData().keySet())).get(i);
                                // value
                                Object value = Objects.requireNonNull(rolesModel.getData().get(key));
                                if (bundle != null) {
                                    if (bundle.containsKey("RoleSize")) {
                                        int az = bundle.getInt("RoleSize");
                                        if (az > 0) {
                                            rolesModels1 = rolesModels;
                                        } else {
                                            RolesModel model = new RolesModel();
                                            model.setPermission(key);
                                            model.setRoleName(key);
                                            model.setRoleID((Long.parseLong(String.valueOf(value))));
                                            model.setHasAccess(false);
                                            rolesModels1.add(model);
                                        }
                                    } else {
                                        RolesModel model = new RolesModel();
                                        model.setPermission(key);
                                        model.setRoleName(key);
                                        model.setRoleID((Long.parseLong(String.valueOf(value))));
                                        model.setHasAccess(false);
                                        rolesModels1.add(model);
                                    }
                                } else {
                                    RolesModel model = new RolesModel();
                                    model.setPermission(key);
                                    model.setRoleName(key);
                                    model.setRoleID((Long.parseLong(String.valueOf(value))));
                                    model.setHasAccess(false);
                                    rolesModels1.add(model);
                                }
                            }
                            userpermission_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                            userpermission_rv.setLayoutManager(new GridLayoutManager(context, 2));
                            userRole_adapter = new UserRole_Adapter(context, rolesModels1);
                            userpermission_rv.setAdapter(userRole_adapter);
                            userRole_adapter.notifyDataSetChanged();
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<UserRolesModel> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void GetUserName() {
        usernameitem.add("Select User Name");
        usernameid.add(0);

        Call<UserData1> call = apiCalling.GetAllUsers(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<UserData1>() {
            @Override
            public void onResponse(Call<UserData1> call, Response<UserData1> response) {
                if (response.isSuccessful()) {
                    UserData1 permissionMaster_model = response.body();
                    if (permissionMaster_model != null) {
                        if (permissionMaster_model.isCompleted()) {
                            List<UserModel> respose = permissionMaster_model.getData();
                            for (UserModel singleResponseModel : respose) {
                                String building_name = singleResponseModel.getUsername();
                                usernameitem.add(building_name);

                                int building_id = (int) singleResponseModel.getUserID();
                                usernameid.add(building_id);

                                if (bundle != null) {
                                    if (bundle.containsKey("Emp_Name")) {
                                        String a = bundle.getString("Emp_Name");
                                        if (a.equals(singleResponseModel.getUsername())) {
                                            rolesModels = singleResponseModel.getRoles();
                                            GetUserPermissionList();
                                        }
                                    }
                                }
                            }

                            USERNAMEITEM = new String[usernameitem.size()];
                            USERNAMEITEM = usernameitem.toArray(USERNAMEITEM);

                            USERNAMEID = new Integer[usernameid.size()];
                            USERNAMEID = usernameid.toArray(USERNAMEID);

                            binduser();
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(Call<UserData1> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void binduser() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, USERNAMEITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        user_name.setAdapter(adapter);
        if (bundle != null) {
            if (bundle.containsKey("No")) {
                int a = (int) bundle.getLong("No");
                int pos = usernameid.indexOf(a);
                user_name.setSelection(pos);

            }
        }
        user_name.setOnItemSelectedListener(onItemSelectedListener7);
    }
}