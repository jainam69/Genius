package com.example.genius.helper;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import com.example.genius.R;

public class PicModeSelectDialogFragment extends DialogFragment {

    private final String[] picMode = {Constants.PicModes.CAMERA, Constants.PicModes.GALLERY};

    private IPicModeSelectListener iPicModeSelectListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogView = layoutInflater.inflate(R.layout.dialog_selection, null);
        ImageView camera = dialogView.findViewById(R.id.camera);
        ImageView gallery = dialogView.findViewById(R.id.gallery);
        builder.setItems(picMode, (dialog, which) -> {
            camera.setImageResource(R.drawable.camera);
            gallery.setImageResource(R.drawable.gallery);
            if (iPicModeSelectListener != null)
                iPicModeSelectListener.onPicModeSelected(picMode[which]);
        });
//        builder.setView(dialogView);
//        builder.show();
        return builder.create();

//        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

//        builder.setItems(picMode, (dialog, which) -> {
//            if (iPicModeSelectListener != null)
//                iPicModeSelectListener.onPicModeSelected(picMode[which]);
//        });
//        return builder.create();
    }

    public void setiPicModeSelectListener(IPicModeSelectListener iPicModeSelectListener) {
        this.iPicModeSelectListener = iPicModeSelectListener;
    }

    public interface IPicModeSelectListener {
        void onPicModeSelected(String mode);
    }
}
