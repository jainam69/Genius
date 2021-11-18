package com.example.genius.ui.Live_Video;

import android.annotation.SuppressLint;
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
import com.example.genius.Model.LinkData;
import com.example.genius.Model.LinkModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.StandardData;
import com.example.genius.Model.StandardModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Home_Fragment.home_fragment;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class LiveVideoFragment extends Fragment {

    SearchableSpinner standard;
    EditText video_description, live_url, video_title;
    Button save_live_video, edit_live_video;
    RecyclerView live_rv;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    List<String> standarditem = new ArrayList<>();
    List<Integer> standardid = new ArrayList<>();
    String[] STANDARDITEM;
    Integer[] STANDARDID;
    String StandardName;
    TextView id, text, transaction_id, unique_id;
    OnBackPressedCallback callback;
    NestedScrollView live_scroll;
    LiveVideo_Adapter liveVideo_adapter;
    Long StandardId;

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(((AppCompatActivity) requireActivity()).getSupportActionBar()).setTitle("Live Video");
        View root = inflater.inflate(R.layout.fragment_live_video, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        standard = root.findViewById(R.id.standard);
        video_description = root.findViewById(R.id.video_description);
        save_live_video = root.findViewById(R.id.save_live_video);
        live_url = root.findViewById(R.id.live_url);
        edit_live_video = root.findViewById(R.id.edit_live_video);
        live_rv = root.findViewById(R.id.live_rv);
        id = root.findViewById(R.id.id);
        text = root.findViewById(R.id.text);
        video_title = root.findViewById(R.id.video_title);
        live_scroll = root.findViewById(R.id.live_scroll);
        transaction_id = root.findViewById(R.id.transaction_id);
        unique_id = root.findViewById(R.id.unique_id);

        save_live_video.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (Function.checkNetworkConnection(context)) {
                if (standard.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (live_url.getText().toString().equalsIgnoreCase("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Enter Live Video URL", Toast.LENGTH_SHORT).show();
                } else if (video_description.getText().toString().equalsIgnoreCase("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Enter Live URL Description", Toast.LENGTH_SHORT).show();
                } else {
                    if (Function.checkNetworkConnection(context)) {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), "");
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                        LinkModel model = new LinkModel(branchModel, StandardId, video_description.getText().toString(), live_url.getText().toString(), rowStatusModel, transactionModel, video_title.getText().toString());
                        Call<LinkModel.LinkData1> call = apiCalling.LiveVideoMaintenance(model);
                        call.enqueue(new Callback<LinkModel.LinkData1>() {
                            @Override
                            public void onResponse(@NotNull Call<LinkModel.LinkData1> call, @NotNull Response<LinkModel.LinkData1> response) {
                                if (response.isSuccessful()) {
                                    LinkModel.LinkData1 data1 = response.body();
                                    if (data1 != null) {
                                        if (data1.isCompleted()) {
                                            LinkModel model1 = data1.getData();
                                            if (model1 != null) {
                                                if (model1.getUniqueID() > 0) {
                                                    Toast.makeText(context, "Live Video URL inserted successfully.", Toast.LENGTH_SHORT).show();
                                                    video_title.setText("");
                                                    video_description.setText("");
                                                    live_url.setText("");
                                                    standard.setSelection(0);
                                                    GetAllLiveVideo();
                                                } else {
                                                    Toast.makeText(context, "Live Video URL Not Inserted.", Toast.LENGTH_SHORT).show();
                                                }
                                            }
                                        }
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }

                            @Override
                            public void onFailure(@NotNull Call<LinkModel.LinkData1> call, @NotNull Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_live_video.setOnClickListener(v -> {
            progressBarHelper.showProgressDialog();
            if (Function.checkNetworkConnection(context)) {
                if (standard.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Select Standard.", Toast.LENGTH_SHORT).show();
                } else if (live_url.getText().toString().equalsIgnoreCase("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Enter Live Video URL", Toast.LENGTH_SHORT).show();
                } else if (video_description.getText().toString().equalsIgnoreCase("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please Enter Live URL Description", Toast.LENGTH_SHORT).show();
                } else {
                    progressBarHelper.showProgressDialog();
                    TransactionModel transactionModel = new TransactionModel(Long.parseLong(transaction_id.getText().toString()), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0);
                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                    BranchModel branchModel = new BranchModel(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
                    LinkModel model = new LinkModel(Long.parseLong(unique_id.getText().toString()), branchModel, StandardId, video_description.getText().toString(), live_url.getText().toString(), rowStatusModel, transactionModel, video_title.getText().toString());
                    Call<LinkModel.LinkData1> call = apiCalling.LiveVideoMaintenance(model);
                    call.enqueue(new Callback<LinkModel.LinkData1>() {
                        @Override
                        public void onResponse(@NotNull Call<LinkModel.LinkData1> call, @NotNull Response<LinkModel.LinkData1> response) {
                            if (response.isSuccessful()) {
                                LinkModel.LinkData1 data1 = response.body();
                                if (data1 != null) {
                                    if (data1.isCompleted()) {
                                        LinkModel model1 = data1.getData();
                                        if (model1 != null) {
                                            save_live_video.setVisibility(View.VISIBLE);
                                            edit_live_video.setVisibility(View.GONE);
                                            Toast.makeText(context, "Live Video URL updated successfully...!!!", Toast.LENGTH_SHORT).show();
                                            GetAllLiveVideo();
                                            video_title.setText("");
                                            video_description.setText("");
                                            live_url.setText("");
                                            standard.setSelection(0);
                                        }
                                    }
                                }
                            }
                            progressBarHelper.hideProgressDialog();
                        }

                        @Override
                        public void onFailure(@NotNull Call<LinkModel.LinkData1> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, "" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        if (Function.checkNetworkConnection(context)) {
            progressBarHelper.showProgressDialog();
            GetAllLiveVideo();
            GetAllStandard();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

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

    public void GetAllLiveVideo() {
        progressBarHelper.showProgressDialog();
        Call<LinkData> call = apiCalling.GetLiveVideoLinksBranch(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<LinkData>() {
            @Override
            public void onResponse(@NotNull Call<LinkData> call, @NotNull Response<LinkData> response) {
                if (response.isSuccessful()) {
                    LinkData data = response.body();
                    if (data != null && data.isCompleted()) {
                        List<LinkModel> models = data.getData();
                        if (models != null) {
                            if (models.size() > 0) {
                                List<LinkModel> list = new ArrayList<>();
                                for (LinkModel linkmodel : models) {
                                    if (linkmodel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(linkmodel);
                                    }
                                }
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
                                live_rv.setLayoutManager(linearLayoutManager);
                                liveVideo_adapter = new LiveVideo_Adapter(context, list);
                                liveVideo_adapter.notifyDataSetChanged();
                                live_rv.setAdapter(liveVideo_adapter);
                            }
                        }
                    }
                }
                progressBarHelper.hideProgressDialog();
            }

            @Override
            public void onFailure(@NotNull Call<LinkData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public void GetAllStandard() {
        standarditem.add("Select Standard");
        standardid.add(0);

        Call<StandardData> call = apiCalling.GetAllStandard(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<StandardData>() {
            @Override
            public void onResponse(@NotNull Call<StandardData> call, @NotNull Response<StandardData> response) {
                if (response.isSuccessful()) {
                    progressBarHelper.hideProgressDialog();
                    StandardData standardData = response.body();
                    if (standardData != null) {
                        if (standardData.isCompleted()) {
                            List<StandardModel> respose = standardData.getData();
                            if (respose != null) {
                                if (respose.size() > 0) {
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
                                }
                            }

                        } else {
                            progressBarHelper.hideProgressDialog();
                        }
                    }
                }
            }

            @Override
            public void onFailure(@NotNull Call<StandardData> call, @NotNull Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void bindstandard() {

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, STANDARDITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        standard.setAdapter(adapter);
        standard.setOnItemSelectedListener(onItemSelectedListener7);
    }

    public class LiveVideo_Adapter extends RecyclerView.Adapter<LiveVideo_Adapter.ViewHolder> {
        Context context;
        List<LinkModel> linkdetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        String stname;

        public LiveVideo_Adapter(Context context, List<LinkModel> linkdetails) {
            this.context = context;
            this.linkdetails = linkdetails;
        }

        @NotNull
        @Override
        public LiveVideo_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LiveVideo_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.livevideo_master_deatil_list, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull LiveVideo_Adapter.ViewHolder holder, int position) {
            if (linkdetails.get(position).getRowStatus().getRowStatusId() == 1) {
                holder.video_name.setText("" + linkdetails.get(position).getTitle());
                holder.description.setText("" + linkdetails.get(position).getLinkDesc());
                holder.video_url.setText("" + linkdetails.get(position).getLinkURL());
                holder.live_edit.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                    View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                    builder.setView(dialogView);
                    builder.setCancelable(true);
                    Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                    Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                    ImageView image = dialogView.findViewById(R.id.image);
                    TextView title = dialogView.findViewById(R.id.title);
                    title.setText("Are you sure that you want to Edit Live Video?");
                    image.setImageResource(R.drawable.ic_edit);
                    AlertDialog dialog = builder.create();

                    btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());
                    btn_edit_yes.setOnClickListener(v12 -> {
                        dialog.dismiss();
                        save_live_video.setVisibility(View.GONE);
                        edit_live_video.setVisibility(View.VISIBLE);
                        unique_id.setText("" + linkdetails.get(position).getUniqueID());
                        transaction_id.setText("" + linkdetails.get(position).getTransaction().getTransactionId());
                        video_description.setText("" + linkdetails.get(position).getLinkDesc());
                        video_title.setText("" + linkdetails.get(position).getTitle());
                        live_url.setText("" + linkdetails.get(position).getLinkURL());
                        int a = standardid.indexOf(Integer.parseInt(String.valueOf(linkdetails.get(position).getStandardID())));
                        standard.setSelection(a);
                    });
                    dialog.show();

                });
                holder.live_delete.setOnClickListener(v -> {
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                    View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                    builder.setView(dialogView);
                    builder.setCancelable(true);
                    Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                    Button btn_delete = dialogView.findViewById(R.id.btn_delete);
                    TextView title = dialogView.findViewById(R.id.title);
                    ImageView image = dialogView.findViewById(R.id.image);
                    image.setImageResource(R.drawable.delete);
                    title.setText("Are you sure that you want to delete this Live Video?");
                    AlertDialog dialog = builder.create();

                    btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

                    btn_delete.setOnClickListener(v14 -> {
                        progressBarHelper.showProgressDialog();
                        Call<CommonModel> call = apiCalling.RemoveLink(linkdetails.get(position).getUniqueID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        call.enqueue(new Callback<CommonModel>() {
                            @Override
                            public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                                if (response.isSuccessful()) {
                                    CommonModel model = response.body();
                                    if (model != null && model.isData()) {
                                        Toast.makeText(context, "Live Video Deleted Successfully.", Toast.LENGTH_SHORT).show();
                                        linkdetails.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
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

        }

        @Override
        public int getItemCount() {
            return linkdetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            TextView video_name, description, video_url;
            ImageView live_edit, live_delete;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                video_name = itemView.findViewById(R.id.video_name);
                description = itemView.findViewById(R.id.description);
                video_url = itemView.findViewById(R.id.video_url);
                live_edit = itemView.findViewById(R.id.live_edit);
                live_delete = itemView.findViewById(R.id.live_delete);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
            }
        }
    }

}