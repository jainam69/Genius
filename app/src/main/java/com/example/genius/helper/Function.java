package com.example.genius.helper;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.StrictMode;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Function {
    public static DialogOptionsSelectedListener dialogOptionsSelectedListener;
    public static OkDialogDismissListener okDialogDismissListener;

    public static boolean isValidEmailAddress(String email) {
        return Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}").matcher(email).matches();
    }

    public static boolean isValidMobile(String phone) {
        return Pattern.compile("[0-9]{10}").matcher(phone).matches();
    }

    public static boolean isValidOTP(String otp) {
        return Pattern.compile("[0-9]{6}").matcher(otp).matches();
    }

    public static boolean validateLetters(String txt) {

        String regx = "^(?=.*[0-9])(?=.*[a-zA-Z]).{6,16}$";
        Pattern pattern = Pattern.compile(regx, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(txt);
        return matcher.find();

    }

    public static void fireIntent(Context context, Class cls) {
        Intent i = new Intent(context, cls);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        return manufacturer + " " + model;
    }

    public static void fireIntent(Activity context) {
        context.finish();
    }

    public static void fireIntentForResult(Activity context, Class<?> cls, int requestCode) {

        Intent intent = new Intent(context, cls);
        context.startActivityForResult(intent, requestCode);
    }

    public static void fireIntentForResult(Activity context, Intent intent, int requestCode) {

        context.startActivityForResult(intent, requestCode);
    }

    public static void fireIntentWithClearFlag(Activity context, Class cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

    }

    public static void FunctionsfireIntentWithClearFlagWithWithPendingTransition(Activity context, Class cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }

    public static void fireIntentWithClearFlagWithWithPendingTransition(Activity context, Intent intent) {

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }

    public static void fireIntentWithClearFlagWithWithPendingTransition(Activity context, Class cls) {
        Intent intent = new Intent(context, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
        context.finish();
    }

    public static void fireIntentWithData(Context context, Intent intent) {
        context.startActivity(intent);
        Activity activity = (Activity) context;
    }

    public static String toStingEditText(EditText editText) {
        return editText.getText().toString().trim();
    }

    public static boolean isEmptyEditText(EditText editText) {
        return TextUtils.isEmpty(editText.getText().toString().trim());
    }

    public static Typeface getFontType(Context _context, int typeValue) {

        Typeface typeface;

        if (typeValue == 1) {
            typeface = Typeface.createFromAsset(_context.getAssets(), "fonts/regular.ttf");

        } else if (typeValue == 2) {
            typeface = Typeface.createFromAsset(_context.getAssets(), "fonts/medium.ttf");

        } else if (typeValue == 3) {
            typeface = Typeface.createFromAsset(_context.getAssets(), "fonts/semibold.ttf");

        } else if (typeValue == 4) {
            typeface = Typeface.createFromAsset(_context.getAssets(), "fonts/bold.ttf");

        } else {
            typeface = Typeface.createFromAsset(_context.getAssets(), "fonts/regular.ttf");
        }
        return typeface;
    }

    public static void openPlayStore(Context context) {
        String appPackageName = context.getPackageName(); // getPackageName() from Context or Activities object

        try {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
        } catch (android.content.ActivityNotFoundException anfe) {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
        }
    }

    public static void showToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static void showToastLong(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    public static boolean checkString(String input) {
        return input != null && input.trim().length() > 0;
    }

    public static boolean isInternetAvailable() {
        try {
            final InetAddress address = InetAddress.getByName("https://4peak.azurewebsites.net/");
            return !address.equals("");
        } catch (UnknownHostException e) {
            // Log error
        }
        return false;
    }

    public static final boolean checkNetworkConnection(Context context) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);
        final ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo netInfo = connMgr.getActiveNetworkInfo();
        boolean isReachable = false;

        if (netInfo != null && netInfo.isConnected()) {
            // Some sort of connection is open, check if server is reachable
            try {

                URL url = new URL("https://www.google.com/");

                HttpURLConnection urlc = (HttpURLConnection) url.openConnection();
                urlc.setRequestProperty("User-Agent", "Android Application");
                urlc.setRequestProperty("Connection", "close");
                urlc.setConnectTimeout(10 * 1000);
                try {
                    urlc.connect();
                    System.out.println("-----fffff");
                } catch (Exception e) {

                    System.out.println("-----fffff  " + e);

                }
                isReachable = (urlc.getResponseCode() == 200);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return isReachable;
    }


    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html) {
        Spanned result;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            result = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }

    public static void shareApp(Context context) {
        String appPackageName = context.getPackageName();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        //i.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.share_text) + "\n" + "http://play.google.com/store/apps/details?id=" + appPackageName);
        // i.putExtra(Intent.EXTRA_TEXT, context.getResources().getString(R.string.refer_code_for_libon) + "\n" + Preferences.getInstance(context).getString(Preferences.KEY_REFER_CODE));
        // i.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.share));
        context.startActivity(Intent.createChooser(i, "Share"));

    }

    public static void showMessage(Context mContext, String message, final DialogOptionsSelectedListener dialogOptionsSelectedListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogOptionsSelectedListener != null)
                            dialogOptionsSelectedListener.onSelect(true);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showAlertDialogWithOk(Context mContext, String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(title);
        builder.setMessage(message)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogOptionsSelectedListener != null)
                            dialogOptionsSelectedListener.onSelect(true);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

//    public static void showForceUpdateDialog(final Context mContext, String title, String message) {
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
//        builder.setTitle(title);
//        builder.setMessage(message)
//                .setCancelable(false)
//                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        if (dialogOptionsSelectedListener != null)
//                            dialogOptionsSelectedListener.onSelect(true);
//                        fireIntent(mContext, LoginActivity.class);
//                        dialog.dismiss();
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//
//    }

    public static void showMessage(Context mContext, String message, boolean isCancelable) {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setMessage(message)
                .setCancelable(isCancelable)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogOptionsSelectedListener != null)
                            dialogOptionsSelectedListener.onSelect(true);
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public interface DialogOptionsSelectedListener {
        void onSelect(boolean isYes);
    }

    public interface OkDialogDismissListener {
        void onDismiss();
    }

    public static void showAlertDialogWithYesNo(Context mContext, String message
            , final DialogOptionsSelectedListener dialogOptionsSelectedListener) {
        if (mContext != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setMessage(message)
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (dialogOptionsSelectedListener != null)
                                dialogOptionsSelectedListener.onSelect(true);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public static void hideKeyPad(Context context, View view) {
        InputMethodManager inputManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(view.getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    public static int dpToPx(Context context, int dp) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics());
    }

    public static String formatBirthDate(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM/dd/yyy HH:mm:ss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            //  Log.d("Old Format", originalFormat.format(date));
            //    Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {
            // Handle Exception.
        }
        return newDate;
    }

    public static String formatDate(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MM-dd-yyyy");
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            Log.d("Old Format", originalFormat.format(date));
            Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {
            // Handle Exception.
        }
        return newDate;
    }

    public static String formatTenderDate(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd MMM yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            Log.d("Old Format", originalFormat.format(date));
            Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {
            // Handle Exception.
        }

        return newDate;
    }

    public static String formatFilterDate(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("d MMM yyyy");
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            //   Log.d("Old Format", originalFormat.format(date));
            //   Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {
            // Handle Exception.
        }

        return newDate;
    }

    public static String formatTenderDateABC(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            //   Log.d("Old Format", originalFormat.format(date));
            //  Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {
            // Handle Exception.
        }

        return newDate;
    }

    public static String formatTenderDateNcode(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            //  Log.d("Old Format", originalFormat.format(date));
            //    Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {

        }

        return newDate;
    }

    public static String formatOnDateDropDown(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetFormat = new SimpleDateFormat("dd MMM yyyy");
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            //  Log.d("Old Format", originalFormat.format(date));
            // Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {

        }
        return newDate;
    }

    public static String formatPushDate(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            //  Log.d("Old Format", originalFormat.format(date));
            // Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {
            // Handle Exception.
        }

        return newDate;
    }

    public static String formatFPSMonthYearDropDown(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("MM/yyyy");
        SimpleDateFormat targetFormat = new SimpleDateFormat("MMM yyyy");
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            // Log.d("Old Format", originalFormat.format(date));
            //Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {

        }
        return newDate;
    }

    public static String formatDateOfBirth(String OldDate) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        SimpleDateFormat targetFormat = new SimpleDateFormat("MM-dd-yyyy", Locale.US);
        Date date;
        String newDate = null;
        try {
            date = originalFormat.parse(OldDate);
            //  Log.d("Old Format", originalFormat.format(date));
            //  Log.d("New Format", targetFormat.format(date));
            newDate = targetFormat.format(date);

        } catch (ParseException ex) {
            // Handle Exception.
        }

        return newDate;
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }

    public static String getCurrentDateTimeSec() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("ddMMyyyyHHmmss");
        String formattedDate = df.format(c.getTime());
        //  Log.d("date", formattedDate);

        return formattedDate;
    }

    public static String getCurrentDateForOnDate() {
        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        //   Log.d("date", formattedDate);

        return formattedDate;
    }
}
