package com.example.genius.ui.Library_Fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.genius.API.ApiCalling;
import com.example.genius.Model.LibraryData;
import com.example.genius.Model.LibraryModel;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.Model.UserModel;
import com.example.genius.R;
import com.example.genius.databinding.FragmentLibraryApproveBinding;
import com.example.genius.databinding.RowLibraryApprovalDetailBinding;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Banner.Banner_Fragment;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

@SuppressLint("SetTextI18n")
public class LibraryApproveFragment extends Fragment {

    FragmentLibraryApproveBinding binding;
    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    LibraryApproval_Adapter libraryApproval_adapter;
    OnBackPressedCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Manage Library");
        binding = FragmentLibraryApproveBinding.inflate(getLayoutInflater());
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        if (Function.isNetworkAvailable(context)) {
            GetAllLibraryApprovalList();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                LibrarySelectorFragment profileFragment = new LibrarySelectorFragment();
                FragmentManager fm = requireActivity().getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                ft.replace(R.id.nav_host_fragment, profileFragment);
                ft.addToBackStack(null);
                ft.commit();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(requireActivity(), callback);
        return binding.getRoot();
    }

    public void GetAllLibraryApprovalList() {
        progressBarHelper.showProgressDialog();
        Call<LibraryData> call = apiCalling.Getalllibraryapprovallist(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<LibraryData>() {
            @Override
            public void onResponse(Call<LibraryData> call, Response<LibraryData> response) {
                if (response.isSuccessful()) {
                    LibraryData data = response.body();
                    if (data.isCompleted()) {
                        List<LibraryModel> list = data.getData();
                        if (list != null && list.size() > 0) {
                            binding.approvalRv.setVisibility(View.VISIBLE);
                            binding.txtNodata.setVisibility(View.GONE);
                            binding.approvalRv.setLayoutManager(new LinearLayoutManager(context));
                            libraryApproval_adapter = new LibraryApproval_Adapter(context, list);
                            libraryApproval_adapter.notifyDataSetChanged();
                            binding.approvalRv.setAdapter(libraryApproval_adapter);
                        } else {
                            binding.approvalRv.setVisibility(View.GONE);
                            binding.txtNodata.setVisibility(View.VISIBLE);
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(Call<LibraryData> call, Throwable t) {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static class LibraryApproval_Adapter extends RecyclerView.Adapter<LibraryApproval_Adapter.ViewHolder> {

        Context context;
        List<LibraryModel> libraryModels;
        long downloadID;
        String Name, sts;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        int select;
        UserModel userpermission;

        public LibraryApproval_Adapter(Context context, List<LibraryModel> libraryModels) {
            this.context = context;
            this.libraryModels = libraryModels;
        }

        @NonNull
        @Override
        public LibraryApproval_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(RowLibraryApprovalDetailBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false));
        }

        @SuppressLint("RecyclerView")
        @Override
        public void onBindViewHolder(@NonNull LibraryApproval_Adapter.ViewHolder holder,int position) {
            for (UserModel.UserPermission model : userpermission.getPermission()){
                if (model.getPageInfo().getPageID() == 80){
                    if (!model.getPackageRightinfo().isCreatestatus()){
                        holder.binding.imgEdit.setVisibility(View.GONE);
                    }
                }
            }
            if (libraryModels.get(position).getLibrary_Type() == 2) {
                Glide.with(context).load(libraryModels.get(position).getThumbnailFilePath()).into(holder.binding.imgThumbnail);
                holder.binding.imgThumbnail.setVisibility(View.VISIBLE);
                holder.binding.imgDownload.setVisibility(View.VISIBLE);
            } else {
                holder.binding.imgThumbnail.setVisibility(View.GONE);
                holder.binding.imgDownload.setVisibility(View.GONE);
            }

            holder.binding.txtBranchname.setText("All Branch");
            if (libraryModels.get(position).getVideoLink() != null && !libraryModels.get(position).getVideoLink().equals("")) {
                holder.binding.txtVideolink.setText(libraryModels.get(position).getVideoLink());
                holder.binding.linearVideolink.setVisibility(View.VISIBLE);
            } else {
                holder.binding.linearVideolink.setVisibility(View.GONE);
            }
            if (!libraryModels.get(position).getSubject().getSubject().equals("")) {
                holder.binding.linearSubject.setVisibility(View.VISIBLE);
                holder.binding.txtSubjectname.setText(libraryModels.get(position).getSubject().getSubject());
            } else {
                ViewGroup.LayoutParams params = holder.binding.linearSubject.getLayoutParams();
                params.height = 0;
                params.width = 0;
                holder.binding.linearSubject.setLayoutParams(params);
            }
            holder.binding.txtCategoryname.setText(libraryModels.get(position).getCategoryInfo().getCategory());
            holder.binding.txtDescription.setText(libraryModels.get(position).getDescription());
            holder.binding.txtApprovalstatus.setText(libraryModels.get(position).getApproval().getLibrary_Status_text());
            holder.binding.imgEdit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to change Library Status?");
                image.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(context, R.style.DialogStyle);
                    View dialogView1 = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_manage_library, null);
                    builder1.setView(dialogView1);
                    builder1.setCancelable(true);
                    Button edit = dialogView1.findViewById(R.id.edit_manage_lib);
                    RadioGroup rg = dialogView1.findViewById(R.id.rg);
                    RadioButton app = dialogView1.findViewById(R.id.approved);
                    RadioButton rej = dialogView1.findViewById(R.id.reject);

                    AlertDialog dialog1 = builder1.create();
                    String st = libraryModels.get(position).getApproval().getLibrary_Status_text();
                    if (st.equals("Approve")) {
                        app.setChecked(true);
                        rej.setChecked(false);
                    }
                    if (st.equals("Reject")) {
                        app.setChecked(false);
                        rej.setChecked(true);
                    }
                    rg.setOnCheckedChangeListener((group, checkedId) -> {
                        holder.rb1 = dialogView1.findViewById(checkedId);
                        sts = holder.rb1.getText().toString();
                    });
                    select = rg.getCheckedRadioButtonId();
                    holder.rb1 = dialogView1.findViewById(select);
                    sts = holder.rb1.getText().toString();

                    dialog1.show();
                    edit.setOnClickListener(v121 -> {
                        progressBarHelper.showProgressDialog();
                        TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        RowStatusModel rowStatusModel = new RowStatusModel(1);
                        LibraryModel libraryModel = new LibraryModel(libraryModels.get(position).getLibraryID());
                        LibraryModel.ApprovalModel model = new LibraryModel.ApprovalModel(libraryModels.get(position).getApproval().getApproval_id(), libraryModel,
                                Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID), transactionModel, rowStatusModel, sts);
                        Call<LibraryModel.ApprovalData> call = apiCalling.Library_Approval_Maintenanace(model);
                        call.enqueue(new Callback<LibraryModel.ApprovalData>() {
                            @Override
                            public void onResponse(@NotNull Call<LibraryModel.ApprovalData> call, @NotNull Response<LibraryModel.ApprovalData> response) {
                                if (response.isSuccessful()) {
                                    progressBarHelper.hideProgressDialog();
                                    LibraryModel.ApprovalData data = response.body();
                                    if (data.isCompleted()) {
                                        Toast.makeText(context, "Library Approved Successfully.", Toast.LENGTH_SHORT).show();
                                        holder.binding.txtApprovalstatus.setText(sts);
                                        libraryModels.get(position).setApproval(new LibraryModel.ApprovalModel(sts));
                                        dialog1.dismiss();
                                    }
                                } else {
                                    Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                    progressBarHelper.hideProgressDialog();
                                }
                            }

                            @Override
                            public void onFailure(@NotNull Call<LibraryModel.ApprovalData> call, @NotNull Throwable t) {
                                progressBarHelper.hideProgressDialog();
                                Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                });
                dialog.show();
            });
            holder.binding.imgDownload.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                TextView btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                TextView btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image = dialogView.findViewById(R.id.image);
                TextView title = dialogView.findViewById(R.id.title);
                title.setText("Are you sure that you want to Download Document?");
                image.setImageResource(R.drawable.download);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v18 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v17 -> {
                    dialog.dismiss();
                    String filetype = libraryModels.get(position).getDocFilePath();
                    Toast.makeText(context, "Download Started..", Toast.LENGTH_SHORT).show();
                    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    Uri uri = Uri.parse(filetype);
                    DownloadManager.Request request = new DownloadManager.Request(uri);
                    Name = libraryModels.get(position).getDocFileName();
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "/AshirvadStudyCircle/" + Name);
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                    downloadID = dm.enqueue(request);
                    context.registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                });
                dialog.show();
            });
        }

        @Override
        public int getItemCount() {
            return libraryModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            RowLibraryApprovalDetailBinding binding;
            RadioButton rb1;

            public ViewHolder(@NonNull RowLibraryApprovalDetailBinding itemView) {
                super(itemView.getRoot());
                binding = itemView;
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
                userpermission = new Gson().fromJson(Preferences.getInstance(context).getString(Preferences.KEY_PERMISSION_LIST), UserModel.class);
            }
        }

        private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context1, Intent intent) {
                long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
                if (downloadID == id) {
                    Toast.makeText(context, "Download " + Name + " Completed And Stored In AshirvadStudyCircle Folder...", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

}