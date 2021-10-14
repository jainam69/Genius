package com.example.genius.helper;

import android.app.ProgressDialog;
import android.content.Context;

import com.example.genius.R;


public class ProgressBarHelper implements ProgressListener {
    private final ProgressDialog dialog;

    public ProgressBarHelper(Context context, boolean isCancelable) {

        dialog = new ProgressDialog(context, R.style.DialogTheme);
        dialog.setCancelable(isCancelable);
        dialog.setIndeterminate(true);
        dialog.setCanceledOnTouchOutside(isCancelable);
        dialog.setMessage("Please wait...");
    }

    @Override
    public void showProgressDialog() {
        if (dialog != null) {
            dialog.show();
        }
    }

    @Override
    public void hideProgressDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }
}
