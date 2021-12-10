package com.example.genius.ui.Library_Fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.example.genius.Model.CategoryData;
import com.example.genius.Model.CategoryModel;
import com.example.genius.Model.LibraryModel;
import com.example.genius.Model.LibrarySingleData;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.SubjectData;
import com.example.genius.Model.SubjectModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.MultiSelectionSpinner;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class library_video_fragment extends Fragment implements MultiSelectionSpinner.OnMultipleItemsSelectedListener {

    RadioGroup rg, rg1;
    RadioButton all, branch_1, rb_general, rb_standard, rb1, rb2;
    TextView master_id, lib_id, uniqid, libraryid, transactionid;
    SearchableSpinner subject;
    MultiSelectionSpinner standard;
    EditText library_description, library_title, library_video_link;
    Button save_library, edit_library;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    LinearLayout linear_spinner;
    String Branch, Type;
    int select, type;
    List<String> standarditem = new ArrayList<>(), subjectitem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>(), subjectid = new ArrayList<>();
    String[] SUBJECTITEM, CATEGORYITEM, STANDARDITEM;
    Integer[] STANDARDID, SUBJECTID, CATEGORYID;
    String StandardName, SubjectName;
    public static final String ERROR_MSG = "error_msg";
    public static final String ERROR = "error";
    Bundle bundle;
    OnBackPressedCallback callback;
    Long StandardId, SubjectId, categoryid;
    SearchableSpinner category;
    List<String> categoryitem = new ArrayList<>();
    List<Integer> categoryId = new ArrayList<>();
    String BranchID;
    String StandardIDs;
    LibraryModel libraryModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Library Video");
        View root = inflater.inflate(R.layout.library_video_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        rg = root.findViewById(R.id.rg);
        rg1 = root.findViewById(R.id.rg1);
        all = root.findViewById(R.id.all);
        branch_1 = root.findViewById(R.id.branch_1);
        rb_general = root.findViewById(R.id.rb_general);
        rb_standard = root.findViewById(R.id.rb_standard);
        standard = root.findViewById(R.id.standard);
        subject = root.findViewById(R.id.subject);
        library_description = root.findViewById(R.id.library_description);
        save_library = root.findViewById(R.id.save_library);
        edit_library = root.findViewById(R.id.edit_library);
        linear_spinner = root.findViewById(R.id.linear_spinner);
        master_id = root.findViewById(R.id.master_id);
        lib_id = root.findViewById(R.id.lib_id);
        uniqid = root.findViewById(R.id.uniqid);
        transactionid = root.findViewById(R.id.transactionid);
        libraryid = root.findViewById(R.id.libraryid);
        branch_1.setText(Preferences.getInstance(context).getString(Preferences.KEY_BRANCH_NAME));
        category = root.findViewById(R.id.category);
        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        library_title = root.findViewById(R.id.library_title);
        library_video_link = root.findViewById(R.id.library_video_link);

        rg.setOnCheckedChangeListener((group, checkedId) -> {
            rb1 = root.findViewById(checkedId);
            Branch = rb1.getText().toString();
        });
        select = rg.getCheckedRadioButtonId();
        rb1 = root.findViewById(select);
        Branch = rb1.getText().toString();

        rg1.setOnCheckedChangeListener((group, checkedId) -> {
            rb2 = root.findViewById(checkedId);
            Type = rb2.getText().toString();
            if (Type.equals("Standard")) {
                linear_spinner.setVisibility(View.VISIBLE);
                type = 2;
            }
            if (Type.equals("General")) {
                linear_spinner.setVisibility(View.GONE);
                type = 1;
            }
        });
        select = rg1.getCheckedRadioButtonId();
        rb2 = root.findViewById(select);
        Type = rb2.getText().toString();

        bundle = getArguments();
        if (bundle != null) {
            save_library.setVisibility(View.GONE);
            edit_library.setVisibility(View.VISIBLE);

            libraryModel = (LibraryModel) bundle.getSerializable("LIBRARY_MST");
            library_title.setText(libraryModel.getLibraryTitle());
            if (libraryModel.getType() == 1)
                rb_general.setChecked(true);
            else
                rb_standard.setChecked(true);
            if (libraryModel.getBranchID() == 0)
                all.setChecked(true);
            else
                branch_1.setChecked(true);
            library_description.setText(libraryModel.getDescription());
            library_video_link.setText(libraryModel.getVideoLink());
        }

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllCategory();
            GetAllStandard();
            GetAllSubject();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        save_library.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (validation()) {
                if (Function.checkNetworkConnection(context)) {
                    Call<LibrarySingleData> call = apiCalling.OldLibraryMaintenance(0, 0, encodeDecode(library_title.getText().toString())
                            , categoryid, StandardIDs, all.isChecked() ? 0 : Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                            , Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), rb_general.isChecked() ? 1 : 2, 1
                            , encodeDecode(library_description.getText().toString()), SubjectId, 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                            , 0, library_video_link.getText().toString(), "none,none", "none,none", "none,none", "none,none"
                            , false, false, MultipartBody.Part.createFormData("attachment", ""
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), ""))
                            , MultipartBody.Part.createFormData("attachment", ""
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                if (response.body().isCompleted()) {
                                    library_videolist_fragment contact = new library_videolist_fragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = (fragmentManager).beginTransaction();
                                    fragmentTransaction.replace(R.id.nav_host_fragment, contact);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }
                                Function.showToast(context, response.body().getMessage());
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<LibrarySingleData> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                } else {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        edit_library.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (validation()) {
                if (Function.checkNetworkConnection(context)) {
                    Call<LibrarySingleData> call = apiCalling.OldLibraryMaintenance(libraryModel.getLibraryID(), 0, encodeDecode(library_title.getText().toString())
                            , categoryid, StandardIDs, all.isChecked() ? 0 : Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID)
                            , Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), rb_general.isChecked() ? 1 : 2, 1
                            , encodeDecode(library_description.getText().toString()), SubjectId, 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME)
                            , 0, library_video_link.getText().toString(), "none,none", "none,none", "none,none", "none,none"
                            , false, false, MultipartBody.Part.createFormData("attachment", ""
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), ""))
                            , MultipartBody.Part.createFormData("attachment", ""
                                    , RequestBody.create(MediaType.parse("multipart/form-data"), "")));
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                if (response.body().isCompleted()) {
                                    library_videolist_fragment contact = new library_videolist_fragment();
                                    FragmentManager fragmentManager = getFragmentManager();
                                    FragmentTransaction fragmentTransaction = (fragmentManager).beginTransaction();
                                    fragmentTransaction.replace(R.id.nav_host_fragment, contact);
                                    fragmentTransaction.addToBackStack(null);
                                    fragmentTransaction.commit();
                                }
                                Function.showToast(context, response.body().getMessage());
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<LibrarySingleData> call, @NotNull Throwable t) {
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            progressBarHelper.hideProgressDialog();
                        }
                    });
                } else {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                }
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                library_videolist_fragment profileFragment = new library_videolist_fragment();
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
        subjectitem.add("Select Subject");
        subjectid.add(0);
        Call<SubjectData> call = apiCalling.GetAllSubject(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<SubjectData>() {
            @Override
            public void onResponse(Call<SubjectData> call, Response<SubjectData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    SubjectData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<SubjectModel> respose = standardData.getData();
                            if (respose.size() > 0) {
                                List<SubjectModel> list = new ArrayList<>();
                                for (SubjectModel singleResponseModel : respose) {

                                    String std = singleResponseModel.getSubject();
                                    subjectitem.add(std);

                                    int stdid = (int) singleResponseModel.getSubjectID();
                                    subjectid.add(stdid);
                                }
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
        subject.setAdapter(adapter);
        if (bundle != null && libraryModel.getList().size() > 0) {
            selectSpinnerValue(subject, libraryModel.getList().get(0).getSubject());
        }
        subject.setOnItemSelectedListener(onItemSelectedListener8);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener8 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectName = subjectitem.get(position);
                    SubjectId = Long.parseLong(subjectid.get(position).toString());
                    if (subject.getSelectedItem().equals("Select Subject")) {
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

    public void GetAllStandard() {
        standarditem.add("Select Standard");
        standardid.add(0);

        Call<StandardData> call = apiCalling.GetAllStandard(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StandardData>() {
            @Override
            public void onResponse(Call<StandardData> call, Response<StandardData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    StandardData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<StandardModel> respose = standardData.getData();
                            for (StandardModel singleResponseModel : respose) {

                                String std = singleResponseModel.getStandard();
                                standarditem.add(std);

                                int stdid = (int) singleResponseModel.getStandardID();
                                standardid.add(stdid);
                            }
                            STANDARDITEM = new String[standarditem.size()];
                            STANDARDITEM = standarditem.toArray(STANDARDITEM);

                            STANDARDID = new Integer[standardid.size()];
                            STANDARDID = standardid.toArray(STANDARDID);

                            bindstandard();
                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
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
        /*ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, STANDARDITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);*/
        standard.setItems(STANDARDITEM);
        standard.setListener(this);
        standard.hasNoneOption(true);
        standard.setSelection(new int[]{0});
        if (bundle != null && libraryModel.getList().size() > 0) {
            List<String> list = new ArrayList<>();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < libraryModel.getList().size(); i++) {
                list.add(libraryModel.getList().get(i).getStandard());
                sb.append(standardid.get(i));
                sb.append(",");
            }
            StandardIDs = sb.toString().substring(0, sb.length() - 1);
            standard.setSelection(list);
        }
        standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener7 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StandardName = standarditem.get(position);
                    StandardId = Long.parseLong(standardid.get(position).toString());
                    if (standard.getSelectedItem().equals("Select Standard")) {
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

    public void GetAllCategory() {
        categoryitem.add("Select Category");
        categoryId.add(0);

        Call<CategoryData> call = apiCalling.GetAllCategory(0);
        call.enqueue(new Callback<CategoryData>() {
            @Override
            public void onResponse(@NotNull Call<CategoryData> call, @NotNull Response<CategoryData> response) {
                if (response.isSuccessful()) {
                    CategoryData branchModel = response.body();
                    if (branchModel != null) {
                        if (branchModel.isCompleted()) {
                            List<CategoryModel> respose = branchModel.getData();
                            for (CategoryModel singleResponseModel : respose) {

                                String building_name = singleResponseModel.getCategory();
                                categoryitem.add(building_name);

                                int building_id = Integer.parseInt(String.valueOf(singleResponseModel.getCategoryID()));
                                categoryId.add(building_id);
                            }
                            CATEGORYITEM = new String[categoryitem.size()];
                            CATEGORYITEM = categoryitem.toArray(CATEGORYITEM);

                            CATEGORYID = new Integer[categoryId.size()];
                            CATEGORYID = categoryId.toArray(CATEGORYID);
                            bindcategory();
                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<CategoryData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindcategory() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, CATEGORYITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(onItemSelectedListener6);
        if (bundle != null) {
            selectSpinnerValue(category, libraryModel.getCategoryInfo().getCategory());
        }
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    categoryid = Long.parseLong(categoryId.get(position).toString());
                    if (category.getSelectedItem().equals("Select Category")) {
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
    public void selectedIndices(List<Integer> indices) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < indices.size(); i++) {
            sb.append(standardid.get(indices.get(i)));
            sb.append(",");
        }
        StandardIDs = sb.toString().substring(0, sb.length() - 1);
    }

    @Override
    public void selectedStrings(List<String> strings) {

    }

    public boolean validation() {
        if (library_title.getText().toString().trim().equals("")) {
            Function.showToast(context, "Please enter library title");
            progressBarHelper.hideProgressDialog();
            return false;
        } else if (category.getSelectedItemId() == 0) {
            Function.showToast(context, "Please select category");
            progressBarHelper.hideProgressDialog();
            return false;
        } else if (library_video_link.getText().toString().equals("")) {
            Function.showToast(context, "Please enter video link");
            progressBarHelper.hideProgressDialog();
            return false;
        } else if (rb_standard.isChecked()) {
            if (StandardIDs.equals("")) {
                Function.showToast(context, "Please select standard");
                progressBarHelper.hideProgressDialog();
                return false;
            } else if (subject.getSelectedItemId() == 0) {
                Function.showToast(context, "Please select subject");
                progressBarHelper.hideProgressDialog();
                return false;
            }
        }
        return true;
    }

    public String encodeDecode(String text) {
        return Base64.encodeToString(text.getBytes(), Base64.DEFAULT).replace("\n", "");
    }

    private void selectSpinnerValue(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equals(myString)) {
                spinner.setSelection(i);
                break;
            }
        }
    }

}