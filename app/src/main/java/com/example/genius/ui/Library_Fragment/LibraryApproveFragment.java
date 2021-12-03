package com.example.genius.ui.Library_Fragment;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.LibraryData;
import com.example.genius.Model.LibraryModel;
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

                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<LibraryData> call, Throwable t) {

            }
        });
    }

    public class LibraryApproval_Adapter extends RecyclerView.Adapter<LibraryApproval_Adapter.ViewHolder>
    {
        Context context;
        List<LibraryModel> libraryModels;

        public LibraryApproval_Adapter(Context context, List<LibraryModel> libraryModels) {
            this.context = context;
            this.libraryModels = libraryModels;
        }

        @NonNull
        @Override
        public LibraryApproval_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new LibraryApproval_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.row_library_approval_detail, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull LibraryApproval_Adapter.ViewHolder holder, int position) {
            holder.txt_branchname.setText("Branch Name : All Branch");
            if (libraryModels.get(position).getVideoLink() != null && libraryModels.get(position).getVideoLink() != "")
            {
                holder.txt_videolink.setText("Video Link : " + libraryModels.get(position).getVideoLink());
            }else {
                holder.txt_videolink.setVisibility(View.GONE);
            }
            holder.txt_subjectname.setText("Subject : " + libraryModels.get(position).getSubjectlist(0).getSubject());
            holder.txt_categoryname.setText("Category Name : " + libraryModels.get(position).getCategoryInfo().getCategory());
            holder.txt_description.setText("Description : " + libraryModels.get(position).getDescription());
            holder.txt_approvalstatus.setText("Status : " + libraryModels.get(position).getApproval().getLibrary_Status_text());
        }

        @Override
        public int getItemCount() {
            return libraryModels.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView img_thumbnail,img_download,img_edit;
            TextView txt_branchname,txt_videolink,txt_subjectname,txt_categoryname,txt_description,txt_approvalstatus;

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
    }
}