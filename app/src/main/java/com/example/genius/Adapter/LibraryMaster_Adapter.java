package com.example.genius.Adapter;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.LibraryModel;
import com.example.genius.R;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;

import java.util.List;

public class LibraryMaster_Adapter extends RecyclerView.Adapter<LibraryMaster_Adapter.ViewHolder> {

    Context context;
    List<LibraryModel> libraryDetails;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    int id,bid;
    long downloadID;
    String Name;
    byte[] imageVal;

    public LibraryMaster_Adapter(Context context, List<LibraryModel> libraryDetails) {
        this.context = context;
        this.libraryDetails = libraryDetails;
    }

    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_master_deatil_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        /*holder.doc_desc.setText(libraryDetails.get(position).getDescription());
        imageVal = Base64.decode(libraryDetails.get(position).getLibraryData().getThumbImageContentText(),Base64.DEFAULT);
        Bitmap decodedByte = BitmapFactory.decodeByteArray(imageVal, 0, imageVal.length);
        Glide.with(context).load(decodedByte).into(holder.image);

        holder.library_edit.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to Edit Library?");
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
                        library_fragment orderplace = new library_fragment();
                        Bundle bundle = new Bundle();
                        bundle.putLong("BranchId",libraryDetails.get(position).getBranchID());
                        bundle.putLong("LibraryID",libraryDetails.get(position).getLibraryID());
                        bundle.putLong("UniqueID",libraryDetails.get(position).getLibraryData().getUniqueID());
                        bundle.putString("ThumbImageName",libraryDetails.get(position).getThumbImageName());
                        bundle.putString("ThumbDocName",libraryDetails.get(position).getThumbDocName());
                        bundle.putInt("Type",libraryDetails.get(position).getType());
                        bundle.putLong("StandardID",libraryDetails.get(position).getStandardID());
                        bundle.putLong("SubjectID",libraryDetails.get(position).getSubjectID());
                        bundle.putString("Description",libraryDetails.get(position).getDescription());
                        bundle.putString("ThumbImageContentText",libraryDetails.get(position).getLibraryData().getThumbImageContentText());
                        bundle.putString("ThumbImageExt",libraryDetails.get(position).getLibraryData().getThumbImageExt());
                        bundle.putString("DocContentExt",libraryDetails.get(position).getLibraryData().getDocContentExt());
                        bundle.putString("DocContentText",libraryDetails.get(position).getLibraryData().getDocContentText());
                        bundle.putLong("TransactionId",libraryDetails.get(position).getTransaction().getTransactionId());
                        orderplace.setArguments(bundle);
                        FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
                        FragmentTransaction fragmentTransaction = ((FragmentManager) fragmentManager).beginTransaction();
                        fragmentTransaction.replace(R.id.nav_host_fragment, orderplace);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                });
                dialog.show();
            }
        });
        holder.library_delete.setOnClickListener(new View.OnClickListener() {
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
                title.setText("Are you sure that you want to delete this Library?");
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
                        dialog.dismiss();
                        progressBarHelper.showProgressDialog();
                        Call<CommonModel> call = apiCalling.RemoveLibrary(libraryDetails.get(position).getLibraryID(), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                        call.enqueue(new Callback<CommonModel>() {
                            @Override
                            public void onResponse(Call<CommonModel> call, Response<CommonModel> response) {
                                if (response.isSuccessful()) {
                                    CommonModel model = response.body();
                                    if (model.isCompleted()) {
                                        if (model.isData()) {
                                            libraryDetails.remove(position);
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
                    }
                });
                dialog.show();
            }
        });
        holder.library_download.setOnClickListener(new View.OnClickListener() {
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

                    }
                });
                dialog.show();
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return libraryDetails.size();
    }

    private final BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context1, Intent intent) {
            //Fetching the download id received with the broadcast
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            //Checking if the received broadcast is for our enqueued download by matching download id
            if (downloadID == id) {
//                Toast.makeText(context, "Download " + Name + " Completed And Stored In AshirvadStudyCircle Folder...", Toast.LENGTH_LONG).show();
            }
        }
    };
    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView doc_desc;
        ImageView image,library_edit,library_delete,library_download;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            doc_desc = itemView.findViewById(R.id.doc_desc);
            image = itemView.findViewById(R.id.image);
            library_edit = itemView.findViewById(R.id.library_edit);
            library_delete = itemView.findViewById(R.id.library_delete);
            library_download = itemView.findViewById(R.id.library_download);
            progressBarHelper = new ProgressBarHelper(context, false);
            apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);
        }
    }
}
