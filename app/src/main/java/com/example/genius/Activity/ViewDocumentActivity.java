package com.example.genius.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.genius.R;
import com.github.chrisbanes.photoview.PhotoView;

public class ViewDocumentActivity extends AppCompatActivity {

    Intent intent;
    String PaperPath,Subject;
    Boolean isImage;
    TextView image_name;
    PhotoView image;
    WebView webView;
    ProgressDialog pdialog3;
    private float m_downX;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_document);

        intent = getIntent();
        PaperPath = intent.getStringExtra("PaperPath");
        Subject  = intent.getStringExtra("ProIndex");
        isImage = accept(PaperPath);
        image_name = findViewById(R.id.image_name);
        image = findViewById(R.id.image);
        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        webView.setScrollbarFadingEnabled(false);
        webView.getSettings().setBuiltInZoomControls(true);
        webView.getSettings().setPluginState(WebSettings.PluginState.ON);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setSupportZoom(true);
        image_name.setText(Subject);

        webView.addJavascriptInterface(new TawkJavaScriptInterface(ViewDocumentActivity.this),"com_ashirvadstudycircle_tawk");
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String js1="javascript: var str = document.body.innerHTML;com_ashirvadstudycircle_tawk.rd(str);";
                        webView.loadUrl(js1);
                    }
                }, 3000);
            }
        });

        final SwipeRefreshLayout mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mSwipeRefreshLayout.setRefreshing(false);
                        webView.reload();
                    }
                }, 3000);
            }
        });

        if(isImage){
            image.setVisibility(View.VISIBLE);
            webView.setVisibility(View.GONE);
            RequestOptions options = new RequestOptions()
                    .centerCrop()
                    .placeholder(R.drawable.progress_animation)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)
                    .dontAnimate()
                    .dontTransform();

            Glide.with(getApplicationContext())
                    .load(PaperPath)
                    .apply(options)
                    .into(image);
        }else {
            webView.setVisibility(View.VISIBLE);
            image.setVisibility(View.GONE);
            initWebView();
            webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + PaperPath);
        }
    }

    public class TawkJavaScriptInterface {
        private Context context;
        public TawkJavaScriptInterface(Context context){
            this.context=context;
        }
        @JavascriptInterface
        public void backButtonPressed()
        {
            finish();
            Toast.makeText(getApplicationContext(), "Click", Toast.LENGTH_SHORT).show();
        }

        @JavascriptInterface
        public void rd(String str){
            Log.d("my html",str);
        }
    }

    public boolean accept(String  pathname) {
        final String name = pathname;
        String ext = null;
        int i = name.lastIndexOf('.');
        if (i > 0 && i < name.length() - 1) {
            ext = name.substring(i + 1).toLowerCase();
        }
        if (ext == null)
            return false;
        else if (!ext.equals("jpg") && !ext.equals("jpeg") && !ext.equals("png") && !ext.equals("gif"))
            return false;
        else
            return true;
    }

    private void initWebView() {
        webView.setWebChromeClient(new MyWebChromeClient(this));
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                pdialog3 = new ProgressDialog(ViewDocumentActivity.this);
                pdialog3.setMessage("Please Wait");
                pdialog3.setCancelable(false);
                pdialog3.setIndeterminate(true);
                pdialog3.show();
                invalidateOptionsMenu();
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl("https://drive.google.com/viewerng/viewer?embedded=true&url=" + PaperPath);
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (pdialog3 != null) {
                    pdialog3.dismiss();
                }
                invalidateOptionsMenu();
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                if (pdialog3 != null) {
                    pdialog3.dismiss();
                }
                invalidateOptionsMenu();
            }
        });
        webView.clearCache(true);
        webView.clearHistory();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getPointerCount() > 1) {
                    //Multi touch detected
                    return true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        // save the x
                        m_downX = event.getX();
                    }
                    break;
                    case MotionEvent.ACTION_MOVE:
                    case MotionEvent.ACTION_CANCEL:
                    case MotionEvent.ACTION_UP: {
                        // set x so that it doesn't move
                        event.setLocation(m_downX, event.getY());
                    }
                    break;
                }
                return false;
            }
        });
    }

    private class MyWebChromeClient extends WebChromeClient {
        Context context;
        public MyWebChromeClient(Context context) {
            super();
            this.context = context;
        }
    }
}