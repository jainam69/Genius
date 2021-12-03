package com.example.genius.ui.Library_Fragment;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.Preferences;
import com.example.genius.helper.ProgressBarHelper;
import com.example.genius.ui.Banner.Banner_Fragment;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryApproveFragment extends Fragment {

    Context context;
    RecyclerView approval_rv;
    TextView txt_nodata;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    LibraryApproval_Adapter libraryApproval_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Manage Library");
        View root = inflater.inflate(R.layout.fragment_library_approve, container, false);

        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        approval_rv = root.findViewById(R.id.approval_rv);
        txt_nodata = root.findViewById(R.id.txt_nodata);

        if (Function.isNetworkAvailable(context))
        {
            GetAllLibraryApprovalList();
        }else{
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }
        return root;
    }

    public void GetAllLibraryApprovalList()
    {
        progressBarHelper.showProgressDialog();
        Call<LibraryData> call = apiCalling.Getalllibraryapprovallist(0);
        call.enqueue(new Callback<LibraryData>() {
            @Override
            public void onResponse(Call<LibraryData> call, Response<LibraryData> response) {
                if (response.isSuccessful()){
                    LibraryData data = response.body();
                    if (data.isCompleted()){
                        List<LibraryModel> list = data.getData();
                        if (list != null && list.size() > 0)
                        {
                            approval_rv.setVisibility(View.VISIBLE);
                            txt_nodata.setVisibility(View.GONE);
                            approval_rv.setLayoutManager(new LinearLayoutManager(context));
                            libraryApproval_adapter = new LibraryApproval_Adapter(context, list);
                            libraryApproval_adapter.notifyDataSetChanged();
                            approval_rv.setAdapter(libraryApproval_adapter);
                        }else {
                            approval_rv.setVisibility(View.GONE);
                            txt_nodata.setVisibility(View.VISIBLE);
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

    public static class LibraryApproval_Adapter extends RecyclerView.Adapter<LibraryApproval_Adapter.ViewHolder>
    {
        Context context;
        List<LibraryModel> libraryModels;
        long downloadID;
        String Name,sts;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        int select;

        public LibraryApproval_Adapter(Context context, List<LibraryModel> libraryModels) {
            this.context = context;
            this.libraryModels = libraryModels;
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }

        @NonNull
        @Override
        public LibraryApproval_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_library_approval_detail, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull LibraryApproval_Adapter.ViewHolder holder, int position) {
            if (libraryModels.get(position).getThumbnailFilePath() != null && libraryModels.get(position).getThumbnailFilePath() != "")
            {
                Glide.with(context).load(libraryModels.get(position).getThumbnailFilePath()).into(holder.img_thumbnail);
            }else
            {
                holder.img_thumbnail.setVisibility(View.GONE);
                holder.img_download.setVisibility(View.GONE);
            }
            holder.txt_branchname.setText("Branch Name :   All Branch");
            if (libraryModels.get(position).getVideoLink() != null && libraryModels.get(position).getVideoLink() != "")
            {
                holder.txt_videolink.setText("Video Link :   " + libraryModels.get(position).getVideoLink());
            }else {
                holder.txt_videolink.setVisibility(View.GONE);
            }
            holder.txt_subjectname.setText("Subject :   " + libraryModels.get(position).getSubjectlist().get(0).getSubject());
            holder.txt_categoryname.setText("Category Name :   " + libraryModels.get(position).getCategoryInfo().getCategory());
            holder.txt_description.setText("Description :   " + libraryModels.get(position).getDescription());
            holder.txt_approvalstatus.setText("Status :   " + libraryModels.get(position).getApproval().getLibrary_Status_text());
            holder.img_edit.setOnClickListener(new View.OnClickListener() {
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
                    title.setText("Are you sure that you want to Approve Library?");
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
                            AlertDialog.Builder builder = new AlertDialog.Builder(context,R.style.DialogStyle);
                            View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_manage_library, null);
                            builder.setView(dialogView);
                            builder.setCancelable(true);
                            Button edit = dialogView.findViewById(R.id.edit_manage_lib);
                            RadioGroup rg = dialogView.findViewById(R.id.rg);
                            RadioButton app = dialogView.findViewById(R.id.approved);
                            RadioButton rej = dialogView.findViewById(R.id.reject);

                            AlertDialog dialog = builder.create();
                            String st = libraryModels.get(position).getApproval().getLibrary_Status_text();
                            if (st.equals("Approve"))
                            {
                                app.setChecked(true);
                                rej.setChecked(false);
                            }
                            if (st.equals("Reject"))
                            {
                                app.setChecked(false);
                                rej.setChecked(true);
                            }
                            rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(RadioGroup group, int checkedId) {
                                    holder.rb1 = dialogView.findViewById(checkedId);
                                    sts = holder.rb1.getText().toString();
                                }
                            });
                            select = rg.getCheckedRadioButtonId();
                            holder.rb1 = dialogView.findViewById(select);
                            sts = holder.rb1.getText().toString();

                            dialog.show();
                            edit.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    progressBarHelper.showProgressDialog();
                                    TransactionModel transactionModel = new TransactionModel(Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), 0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                                    RowStatusModel rowStatusModel = new RowStatusModel(1);
                                    LibraryModel libraryModel = new LibraryModel(libraryModels.get(position).getLibraryID());
                                    LibraryModel.ApprovalModel model = new LibraryModel.ApprovalModel(libraryModels.get(position).getApproval().getApproval_id(),libraryModel,
                                            Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID),transactionModel,rowStatusModel,sts);
                                    Call<LibraryModel.ApprovalData> call = apiCalling.Library_Approval_Maintenanace(model);
                                    call.enqueue(new Callback<LibraryModel.ApprovalData>() {
                                        @Override
                                        public void onResponse(Call<LibraryModel.ApprovalData> call, Response<LibraryModel.ApprovalData> response) {
                                            if (response.isSuccessful()) {
                                                progressBarHelper.hideProgressDialog();
                                                LibraryModel.ApprovalData data = response.body();
                                                if (data.isCompleted()) {
                                                    Toast.makeText(context, "Library Approved Successfully.", Toast.LENGTH_SHORT).show();
                                                    holder.txt_approvalstatus.setText(sts);
                                                    libraryModels.get(position).setApproval(new LibraryModel.ApprovalModel(sts));
                                                    dialog.dismiss();
                                                }
                                            }else {
                                                Toast.makeText(context, response.message(), Toast.LENGTH_SHORT).show();
                                                progressBarHelper.hideProgressDialog();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<LibraryModel.ApprovalData> call, Throwable t) {
                                            progressBarHelper.hideProgressDialog();
                                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            });
                        }
                    });
                    dialog.show();
                }
            });
            holder.img_download.setOnClickListener(new View.OnClickListener() {
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
                    title.setText("Are you sure that you want to Download Document?");
                    image.setImageResource(R.drawable.download);
                    AlertDialog dialog = builder.create();

                    btn_edit_no.setOnClickListener(v18 -> dialog.dismiss());

                    btn_edit_yes.setOnClickListener(v17 -> {
                        dialog.dismiss();
                        String filetype = libraryModels.get(position).getDocFilePath();
                        String filetyp = filetype.substring(filetype.lastIndexOf("."));
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
                }
            });
        }

        @Override
        public int getItemCount() {
            return libraryModels.size();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {

            ImageView img_thumbnail,img_download,img_edit;
            TextView txt_branchname,txt_videolink,txt_subjectname,txt_categoryname,txt_description,txt_approvalstatus;
            RadioButton rb1;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                img_thumbnail = itemView.findViewById(R.id.img_thumbnail);
                img_edit = itemView.findViewById(R.id.img_edit);
                img_download = itemView.findViewById(R.id.img_download);
                txt_branchname = itemView.findViewById(R.id.txt_branchname);
                txt_videolink = itemView.findViewById(R.id.txt_videolink);
                txt_subjectname = itemView.findViewById(R.id.txt_subjectname);
                txt_categoryname = itemView.findViewById(R.id.txt_categoryname);
                txt_description = itemView.findViewById(R.id.txt_description);
                txt_approvalstatus = itemView.findViewById(R.id.txt_approvalstatus);
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