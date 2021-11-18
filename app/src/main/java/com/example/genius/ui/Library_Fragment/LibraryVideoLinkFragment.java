package com.example.genius.ui.Library_Fragment;

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
import android.widget.Spinner;
import android.widget.TextView;
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

import com.example.genius.API.ApiCalling;
import com.example.genius.Model.BranchModel;
import com.example.genius.Model.CategoryData;
import com.example.genius.Model.CategoryModel;
import com.example.genius.Model.CommonModel;
import com.example.genius.Model.LibraryData;
import com.example.genius.Model.LibraryModel;
import com.example.genius.Model.LibrarySingleData;
import com.example.genius.Model.RowStatusModel;
import com.example.genius.Model.TransactionModel;
import com.example.genius.helper.Preferences;
import com.example.genius.R;
import com.example.genius.helper.Function;
import com.example.genius.helper.MyApplication;
import com.example.genius.helper.ProgressBarHelper;
import com.toptoche.searchablespinnerlibrary.SearchableSpinner;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LibraryVideoLinkFragment extends Fragment {

    Context context;
    ProgressBarHelper progressBarHelper;
    ApiCalling apiCalling;
    OnBackPressedCallback callback;
    SearchableSpinner category;
    EditText title, description;
    String BranchID;
    Button save_image, edit_image;
    List<String> branchitem = new ArrayList<>();
    List<Integer> branchid = new ArrayList<>();
    String[] BRANCHITEM;
    Integer[] BRANCHID;
    long categoryid;
    long TransactionId;
    long LibraryID, LibraryDetailID;
    BannerMaster_Adapter bannerMaster_adapter;
    RecyclerView library_image_rv;
    EditText link;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Library Video Entry");
        View root = inflater.inflate(R.layout.library_video_fragment, container, false);
        context = getActivity();
        progressBarHelper = new ProgressBarHelper(context, false);
        apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

        category = root.findViewById(R.id.category);
        title = root.findViewById(R.id.title);
        description = root.findViewById(R.id.description);
        save_image = root.findViewById(R.id.save_image);
        edit_image = root.findViewById(R.id.edit_image);
        library_image_rv = root.findViewById(R.id.library_image_rv);
        link = root.findViewById(R.id.link);

        BranchID = String.valueOf(Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));

        if (Function.isNetworkAvailable(context)) {
            progressBarHelper.showProgressDialog();
            GetBannerDetails();
            GetAllStandard();
        } else {
            Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
        }

        save_image.setOnClickListener(view -> {
            progressBarHelper.showProgressDialog();
            if (Function.isNetworkAvailable(context)) {
                if (title.getText().toString().trim().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (category.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please select category", Toast.LENGTH_SHORT).show();
                } else if (link.getText().toString().trim().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter link", Toast.LENGTH_SHORT).show();
                } else {
                    LibraryModel libraryModel = new LibraryModel(0, 0, 1, title.getText().toString()
                            , null, link.getText().toString(), "", "", description.getText().toString()
                            , new RowStatusModel(1)
                            , new TransactionModel(0, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID))
                            , new BranchModel(Long.parseLong(BranchID)), new CategoryModel(categoryid));
                    Call<LibrarySingleData> call = apiCalling.LibraryLinkMaintenance(libraryModel);
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                LibrarySingleData data = response.body();
                                if (data.isCompleted()) {
                                    LibraryModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        link.setText("");
                                        title.setText("");
                                        description.setText("");
                                        category.setSelection(0);
                                        GetBannerDetails();
                                    }
                                }
                                Function.showToast(context, "Library Video Link Created Successfully!");
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<LibrarySingleData> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        edit_image.setOnClickListener(view -> {
            progressBarHelper.showProgressDialog();
            if (Function.isNetworkAvailable(context)) {
                if (title.getText().toString().trim().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter title", Toast.LENGTH_SHORT).show();
                } else if (category.getSelectedItemId() == 0) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please select category", Toast.LENGTH_SHORT).show();
                } else if (link.getText().toString().trim().equals("")) {
                    progressBarHelper.hideProgressDialog();
                    Toast.makeText(context, "Please enter link", Toast.LENGTH_SHORT).show();
                } else {
                    LibraryModel libraryModel = new LibraryModel(0, 0, 1, title.getText().toString()
                            , null, link.getText().toString(), "", "", description.getText().toString()
                            , new RowStatusModel(1)
                            , new TransactionModel(TransactionId, Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID), Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME), Preferences.getInstance(context).getLong(Preferences.KEY_USER_ID))
                            , new BranchModel(Long.parseLong(BranchID)), new CategoryModel(categoryid));
                    Call<LibrarySingleData> call = apiCalling.LibraryLinkMaintenance(libraryModel);
                    call.enqueue(new Callback<LibrarySingleData>() {
                        @Override
                        public void onResponse(@NotNull Call<LibrarySingleData> call, @NotNull Response<LibrarySingleData> response) {
                            if (response.isSuccessful()) {
                                LibrarySingleData data = response.body();
                                if (data.isCompleted()) {
                                    LibraryModel notimodel = data.getData();
                                    if (notimodel != null) {
                                        link.setText("");
                                        title.setText("");
                                        description.setText("");
                                        category.setSelection(0);
                                        GetBannerDetails();
                                    }
                                }
                                Function.showToast(context, "Library Video Link Updated Successfully!");
                                progressBarHelper.hideProgressDialog();
                            }
                        }

                        @Override
                        public void onFailure(@NotNull Call<LibrarySingleData> call, @NotNull Throwable t) {
                            progressBarHelper.hideProgressDialog();
                            Toast.makeText(context, t.toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                progressBarHelper.hideProgressDialog();
                Toast.makeText(context, "Please check your internet connectivity...", Toast.LENGTH_SHORT).show();
            }
        });

        callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                LibrarySelectorFragment profileFragment = new LibrarySelectorFragment();
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

    public void GetAllStandard() {
        branchitem.add("Select Category");
        branchid.add(0);

        Call<CategoryData> call = apiCalling.GetAllCategory(Long.parseLong(BranchID));
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
                                branchitem.add(building_name);

                                int building_id = Integer.parseInt(String.valueOf(singleResponseModel.getCategoryID()));
                                branchid.add(building_id);
                            }
                            BRANCHITEM = new String[branchitem.size()];
                            BRANCHITEM = branchitem.toArray(BRANCHITEM);

                            BRANCHID = new Integer[branchid.size()];
                            BRANCHID = branchid.toArray(BRANCHID);

                            bindbranch();
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

    public void bindbranch() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_dropdown_item, BRANCHITEM);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        category.setAdapter(adapter);
        category.setOnItemSelectedListener(onItemSelectedListener6);
    }

    AdapterView.OnItemSelectedListener onItemSelectedListener6 =
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    categoryid = Long.parseLong(branchid.get(position).toString());
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

    public void GetBannerDetails() {
        Call<LibraryData> call = apiCalling.GetAllLibrary(1, Preferences.getInstance(context).getLong(Preferences.KEY_BRANCH_ID));
        call.enqueue(new Callback<LibraryData>() {
            @Override
            public void onResponse(@NotNull Call<LibraryData> call, @NotNull Response<LibraryData> response) {
                if (response.isSuccessful()) {
                    LibraryData bannerData = response.body();
                    if (bannerData.isCompleted()) {
                        List<LibraryModel> bannerDataList = bannerData.getData();
                        if (bannerDataList != null) {
                            if (bannerDataList.size() > 0) {
                                List<LibraryModel> list = new ArrayList<>();
                                for (LibraryModel singlemodel : bannerDataList) {
                                    if (singlemodel.getRowStatus().getRowStatusId() == 1) {
                                        list.add(singlemodel);
                                    }
                                }
                                library_image_rv.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
                                library_image_rv.setLayoutManager(new GridLayoutManager(context, 1));
                                bannerMaster_adapter = new BannerMaster_Adapter(context, list);
                                bannerMaster_adapter.notifyDataSetChanged();
                                library_image_rv.setAdapter(bannerMaster_adapter);
                            }
                        }
                    }
                    progressBarHelper.hideProgressDialog();
                }
            }

            @Override
            public void onFailure(@NotNull Call<LibraryData> call, @NotNull Throwable t) {
                Toast.makeText(getActivity(), t.toString(), Toast.LENGTH_LONG).show();
                progressBarHelper.hideProgressDialog();
            }
        });
    }

    public class BannerMaster_Adapter extends RecyclerView.Adapter<BannerMaster_Adapter.ViewHolder> {

        Context context;
        List<LibraryModel> bannerDetails;
        ProgressBarHelper progressBarHelper;
        ApiCalling apiCalling;
        int id;

        public BannerMaster_Adapter(Context context, List<LibraryModel> bannerDetails) {
            this.context = context;
            this.bannerDetails = bannerDetails;
        }

        @NotNull
        @Override
        public BannerMaster_Adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BannerMaster_Adapter.ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.library_video_detail_list, parent, false));
        }


        @Override
        public void onBindViewHolder(@NonNull BannerMaster_Adapter.ViewHolder holder, int position) {
            /*attach = bannerDetails.get(position).getFilePath();
            imageVal = Base64.decode(attach, Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(imageVal, 0, imageVal.length);*/
            holder.category.setText(bannerDetails.get(position).getCategoryInfo().getCategory());
            holder.title.setText(bannerDetails.get(position).getTitle());
            holder.description.setText(bannerDetails.get(position).getDescription());
            holder.link.setText(bannerDetails.get(position).getLink());
            holder.banner_edit.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_edit_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_edit_no = dialogView.findViewById(R.id.btn_edit_no);
                Button btn_edit_yes = dialogView.findViewById(R.id.btn_edit_yes);
                ImageView image1 = dialogView.findViewById(R.id.image);
                TextView title1 = dialogView.findViewById(R.id.title);
                title1.setText("Are you sure that you want to edit video link?");
                image1.setImageResource(R.drawable.ic_edit);
                AlertDialog dialog = builder.create();

                btn_edit_no.setOnClickListener(v1 -> dialog.dismiss());

                btn_edit_yes.setOnClickListener(v12 -> {
                    dialog.dismiss();
                    final NestedScrollView scroll = ((Activity) context).findViewById(R.id.library_image_scroll);
                    save_image.setVisibility(View.GONE);
                    edit_image.setVisibility(View.VISIBLE);
                    TransactionId = bannerDetails.get(position).getTransaction().getTransactionId();
                    LibraryID = bannerDetails.get(position).getLibraryID();
                    LibraryDetailID = bannerDetails.get(position).getLibraryDetailID();
                    categoryid = bannerDetails.get(position).getCategoryInfo().getCategoryID();
                    selectSpinnerValue(category, bannerDetails.get(position).getCategoryInfo().getCategory());
                    title.setText(bannerDetails.get(position).getTitle());
                    description.setText(bannerDetails.get(position).getDescription());
                    link.setText(bannerDetails.get(position).getLink());
                    scroll.scrollTo(0, 0);
                    scroll.fullScroll(View.FOCUS_UP);
                });
                dialog.show();
            });
            holder.banner_delete.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.DialogStyle);
                View dialogView = ((Activity) context).getLayoutInflater().inflate(R.layout.dialog_delete_staff, null);
                builder.setView(dialogView);
                builder.setCancelable(true);
                Button btn_cancel = dialogView.findViewById(R.id.btn_cancel);
                Button btn_delete = dialogView.findViewById(R.id.btn_delete);
                TextView title = dialogView.findViewById(R.id.title);
                ImageView image = dialogView.findViewById(R.id.image);
                image.setImageResource(R.drawable.delete);
                title.setText("Are you sure that you want to delete this video link?");
                AlertDialog dialog = builder.create();

                btn_cancel.setOnClickListener(v13 -> dialog.dismiss());

                btn_delete.setOnClickListener(v14 -> {
                    dialog.dismiss();
                    progressBarHelper.showProgressDialog();
                    Call<CommonModel> call = apiCalling.RemoveNewLibrary(bannerDetails.get(position).getLibraryID()
                            , Preferences.getInstance(context).getString(Preferences.KEY_USER_NAME));
                    call.enqueue(new Callback<CommonModel>() {
                        @Override
                        public void onResponse(@NotNull Call<CommonModel> call, @NotNull Response<CommonModel> response) {
                            if (response.isSuccessful()) {
                                CommonModel model = response.body();
                                if (model.isCompleted()) {
                                    if (model.isData()) {
                                        Toast.makeText(context, "Video link deleted successfully.", Toast.LENGTH_SHORT).show();
                                        bannerDetails.remove(position);
                                        notifyItemRemoved(position);
                                        notifyDataSetChanged();
                                    }
                                }
                                progressBarHelper.hideProgressDialog();
                            }
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
            return bannerDetails.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView banner_edit, banner_delete;
            TextView title, category, description, link;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);

                link = itemView.findViewById(R.id.link);
                banner_edit = itemView.findViewById(R.id.banner_edit);
                banner_delete = itemView.findViewById(R.id.banner_delete);
                progressBarHelper = new ProgressBarHelper(context, false);
                apiCalling = MyApplication.getRetrofit().create(ApiCalling.class);

                title = itemView.findViewById(R.id.title);
                category = itemView.findViewById(R.id.category);
                description = itemView.findViewById(R.id.description);
            }
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

}
