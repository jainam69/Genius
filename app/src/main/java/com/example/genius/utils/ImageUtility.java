package com.example.genius.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;


public class ImageUtility {
    private final Context context;

    private ImageUtility(Context context) {
        this.context = context;
    }

    public static ImageUtility using(Context context) {
        return new ImageUtility(context);
    }

    public byte[] toBase64(String path) {
        Bitmap bitmap = toBitmap(path);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 10, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }

        return null;
    }


    private Bitmap toBitmap(String path) {
        try {
            return MediaStore.Images.Media.getBitmap(context.getContentResolver(), Uri.fromFile(new File(path)));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public byte[] toBase64FromBitmap(Bitmap bitmap) {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        if (bitmap != null) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
        return null;
    }

    public Bitmap toBitmapFromServer(byte[] path) {
        try {
            return BitmapFactory.decodeByteArray(path, 0, path.length);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
