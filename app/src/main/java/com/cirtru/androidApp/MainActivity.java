package com.cirtru.androidApp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatTextView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int INPUT_FILE_REQUEST_CODE = 1;
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_CAMERA = 100;
    private static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 101;
    private static final String TAG = MainActivity.class.getSimpleName();
    private WebView cirtruWebView;
//    private ProgressBar cirtruProgressBar;
    private AppCompatTextView citruErrorLoadingTextView;
    private AppCompatButton citruErrorAppReLoadingButton;
    private ValueCallback<Uri> mUploadMessage;
    private Uri mCapturedImageURI = null;
    private ValueCallback<Uri[]> mFilePathCallback;
    private String mCameraPhotoPath;
    private static final String userAgentValue = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_5) AppleWebKit/603.2.4 (KHTML, like Gecko) Version/10.1.1 Safari/603.2.4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*cirtruProgressBar = (ProgressBar) findViewById(R.id.cirtru_progressBar);
        cirtruProgressBar.setVisibility(View.VISIBLE);*/
        cirtruWebView = (WebView) findViewById(R.id.cirtru_webview);
        cirtruWebView.loadUrl("https://www.cirtru.com");
        WebSettings mWebSettings = cirtruWebView.getSettings();
        mWebSettings.setJavaScriptEnabled(true);
        mWebSettings.setLoadWithOverviewMode(true);
        mWebSettings.setAllowFileAccess(true);
        mWebSettings.setDomStorageEnabled(true);
        mWebSettings.setDatabaseEnabled(true);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            mWebSettings.setDatabasePath("/data/data/" + cirtruWebView.getContext().getPackageName() + "/databases/");
        }
        mWebSettings.setUseWideViewPort(false);
        mWebSettings.setSupportZoom(true);
        mWebSettings.setBuiltInZoomControls(true);
        mWebSettings.setSavePassword(true);
        mWebSettings.setUserAgentString(userAgentValue);

        /*Comment between these lines*/
       /* DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        if (metrics.densityDpi < DisplayMetrics.DENSITY_MEDIUM) {
            mWebSettings.setDefaultZoom(WebSettings.ZoomDensity.CLOSE);
        } else if (metrics.densityDpi < DisplayMetrics.DENSITY_HIGH) {
            mWebSettings.setDefaultZoom(WebSettings.ZoomDensity.MEDIUM);
        } else {
            mWebSettings.setDefaultZoom(WebSettings.ZoomDensity.FAR);
        }*/
        /*Comment between these lines*/
        cirtruWebView.setWebViewClient(new MyWebViewClient());
        cirtruWebView.setWebChromeClient(new MyWebChromeClient());
        if (Build.VERSION.SDK_INT >= 19) {
            cirtruWebView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        }
        else if(Build.VERSION.SDK_INT >=11 && Build.VERSION.SDK_INT < 19) {
            cirtruWebView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        citruErrorLoadingTextView = (AppCompatTextView) findViewById(R.id.error_loading_txt);
        citruErrorAppReLoadingButton = (AppCompatButton) findViewById(R.id.error_app_reloading_button);
        citruErrorAppReLoadingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cirtruWebView.loadUrl("https://www.cirtru.com");
                citruErrorLoadingTextView.setVisibility(View.GONE);
                citruErrorAppReLoadingButton.setVisibility(View.GONE);
            }
        });
        citruErrorLoadingTextView.setVisibility(View.GONE);
        citruErrorAppReLoadingButton.setVisibility(View.GONE);
        if (Build.VERSION.SDK_INT >= 23) {
            askCameraPermissionandReturnStatus();
        }

        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            Log.d(TAG, bundleToString(extras, this));
            //bundle must contain all info sent in "data" field of the notification
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && cirtruWebView.canGoBack()) {
            cirtruWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    private class MyWebViewClient extends WebViewClient {

        protected boolean checkIntent(Intent intent) {
            PackageManager packageManager = getPackageManager();
            List activities = packageManager.queryIntentActivities(intent,
                    PackageManager.MATCH_DEFAULT_ONLY);
            boolean isIntentSafe = activities.size() > 0;

            return isIntentSafe;
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            citruErrorLoadingTextView.setVisibility(View.GONE);
            citruErrorAppReLoadingButton.setVisibility(View.GONE);
            if (URLUtil.isNetworkUrl(url)) {
                return false;
            }

            // Otherwise allow the OS to handle things like tel, mailto, etc.
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            if (checkIntent(intent)) {
                startActivity(intent);
            }
            return true;
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);

//            cirtruProgressBar.setVisibility(View.VISIBLE);

//            Snackbar.make(view, "Loading page: " + url, Snackbar.LENGTH_LONG).show();
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);

//            cirtruProgressBar.setVisibility(View.GONE);
//              Snackbar.make(view, "Completed loading page: " + url, Snackbar.LENGTH_SHORT).show();
        }

        public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
            super.onReceivedError(view, errorCode, description, failingUrl);
            citruErrorLoadingTextView.setVisibility(View.VISIBLE);
            citruErrorAppReLoadingButton.setVisibility(View.VISIBLE);
//  Snackbar.make(view, "Error! " + description + "\nError Code: " + errorCode, Snackbar.LENGTH_SHORT).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        return imageFile;
    }

    public class MyWebChromeClient extends WebChromeClient {
        // For Android 5.0
        public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> filePath, WebChromeClient.FileChooserParams fileChooserParams) {
            // Double check that we don't have any existing callbacks
            if (mFilePathCallback != null) {
                mFilePathCallback.onReceiveValue(null);
            }
            mFilePathCallback = filePath;
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                // Create the File where the photo should go
                File photoFile = null;
                try {
                    photoFile = createImageFile();
                    takePictureIntent.putExtra("PhotoPath", mCameraPhotoPath);
                } catch (IOException ex) {
                    // Error occurred while creating the File
                    Log.e(TAG, "Unable to create Image File", ex);
                }
                // Continue only if the File was successfully created
                if (photoFile != null) {
                    mCameraPhotoPath = "file:" + photoFile.getAbsolutePath();
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                            Uri.fromFile(photoFile));
                } else {
                    takePictureIntent = null;
                }
            }
            Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
            contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
            contentSelectionIntent.setType("image/*");
            Intent[] intentArray;
            if (takePictureIntent != null) {
                intentArray = new Intent[]{takePictureIntent};
            } else {
                intentArray = new Intent[0];
            }
            Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
            chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
            chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray);
            startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE);
            return true;
        }
        // openFileChooser for Android 3.0+
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            // Create AndroidExampleFolder at sdcard
            // Create AndroidExampleFolder at sdcard
            File imageStorageDir = new File(
                    Environment.getExternalStoragePublicDirectory(
                            Environment.DIRECTORY_PICTURES)
                    , "AndroidExampleFolder");
            if (!imageStorageDir.exists()) {
                // Create AndroidExampleFolder at sdcard
                imageStorageDir.mkdirs();
            }
            // Create camera captured image file path and name
            File file = new File(
                    imageStorageDir + File.separator + "IMG_"
                            + String.valueOf(System.currentTimeMillis())
                            + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);
            // Camera capture image intent
            final Intent captureIntent = new Intent(
                    android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);
            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("image/*");
            // Create file chooser intent
            Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
            // Set camera intent to file chooser
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS
                    , new Parcelable[] { captureIntent });
            // On select image call onActivityResult method of activity
            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        }
        // openFileChooser for Android < 3.0
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }
        //openFileChooser for other Android versions
        public void openFileChooser(ValueCallback<Uri> uploadMsg,
                                    String acceptType,
                                    String capture) {
            openFileChooser(uploadMsg, acceptType);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (requestCode != INPUT_FILE_REQUEST_CODE || mFilePathCallback == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            Uri[] results = null;
            // Check that the response is a good one
            if (resultCode == Activity.RESULT_OK) {
                if (data == null) {
                    // If there is not data, then we may have taken a photo
                    if (mCameraPhotoPath != null) {
                        results = new Uri[]{Uri.parse(mCameraPhotoPath)};
                    }
                } else {
                    String dataString = data.getDataString();
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                }
            }
            mFilePathCallback.onReceiveValue(results);
            mFilePathCallback = null;
        } else if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
            if (requestCode != FILECHOOSER_RESULTCODE || mUploadMessage == null) {
                super.onActivityResult(requestCode, resultCode, data);
                return;
            }
            if (requestCode == FILECHOOSER_RESULTCODE) {
                if (null == this.mUploadMessage) {
                    return;
                }
                Uri result = null;
                try {
                    if (resultCode != RESULT_OK) {
                        result = null;
                    } else {
                        // retrieve from the private variable if the intent is null
                        result = data == null ? mCapturedImageURI : data.getData();
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(), "activity :" + e,
                            Toast.LENGTH_LONG).show();
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            }
        }
        return;
    }

    @TargetApi(23)
    private boolean askCameraPermissionandReturnStatus() {
        if (checkSelfPermission(Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA},
                    MY_PERMISSIONS_REQUEST_CAMERA);
            return false;
        }
        return true;
    }

    @TargetApi(23)
    private boolean askWriteExternalStoragePermissionandReturnStatus() {
        if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera

            }/* else {
                Toast.makeText(this, "Camera access permission denied", Toast.LENGTH_SHORT).show();
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }*/
            if (Build.VERSION.SDK_INT >= 23) {
                askWriteExternalStoragePermissionandReturnStatus();
            }
        }
        if (requestCode == MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to write to external storage

            } /*else {
                Toast.makeText(this, "Write to external storage permission denied", Toast.LENGTH_SHORT).show();
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }*/
        }

    }

    public static void openAppOnGooglePlayStoreApp(Context context, String appId) {
        Log.d(TAG, "test 1");
        Intent openGooglePlayStoreAppIntent = new Intent(Intent.ACTION_VIEW,
                Uri.parse("market://details?id=" + appId));
        boolean marketFound = false;
        Log.d(TAG, "test 2");
        // find all applications able to handle our openGooglePlayStoreAppIntent
        final List<ResolveInfo> otherApps = context.getPackageManager()
                .queryIntentActivities(openGooglePlayStoreAppIntent, 0);
        for (ResolveInfo otherApp: otherApps) {
            // look for Google PlayStore application
            if (otherApp.activityInfo.applicationInfo.packageName
                    .equals("com.android.vending")) {

                ActivityInfo otherAppActivity = otherApp.activityInfo;
                ComponentName componentName = new ComponentName(
                        otherAppActivity.applicationInfo.packageName,
                        otherAppActivity.name
                );
                // make sure it does NOT open in the stack of your activity
                openGooglePlayStoreAppIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                // task reparenting if needed
                openGooglePlayStoreAppIntent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                // if the Google Play was already open in a search result
                //  this make sure it still go to the app page you requested
                openGooglePlayStoreAppIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // this make sure only the Google Play app is allowed to
                // intercept the intent
                openGooglePlayStoreAppIntent.setComponent(componentName);
                Log.d(TAG, "test 3");
                context.startActivity(openGooglePlayStoreAppIntent);
                marketFound = true;
                break;

            }
        }

        // if Google PlayStore app is not present on device, open web browser
        if (!marketFound) {
            Log.d(TAG, "test 4");
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id="+appId));
            context.startActivity(webIntent);
        }
    }

    private String _packageName = BuildConfig.APPLICATION_ID;

    private String bundleToString(final Bundle bundle, final Context mContext) {
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            Log.d(TAG, key);
            if(key.contains("app_package")) {
                Log.d(TAG, "before alert dlg");
                showAppPackageDialog(bundle, key);
            }
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " }Bundle";
        return string;
    }

    private void showAppPackageDialog(final Bundle bundle, final String key) {
        _packageName = bundle.getString(key);

        try {
            AlertDialog.Builder builder = new CustomAlertDialogBuilder(MainActivity.this);
            /*builder.setTitle(bundle.getString("title", "New Citru app update available!"));
            builder.setMessage(bundle.getString("message", "Kindly update Citru app from Google Play Store to enjoy all the latest exciting features and code fixes!"));
            */
            builder.setTitle(bundle.getString("title"));
            builder.setMessage(bundle.getString("message"));

            builder.setPositiveButton(bundle.getString("positive_button"), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    openAppOnGooglePlayStoreApp(getApplicationContext(), _packageName);
                    bundle.remove(key);
                    dialog.dismiss();
                }
            });
            builder.setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    dialog.dismiss();
                }
            });
            builder.setIcon(R.mipmap.ic_launcher);
            builder.setCancelable(false);
            AlertDialog dialog = builder.create();
            dialog.show();
        }catch (Exception e){
            Log.e(TAG, e.toString());
            e.printStackTrace();}
    }

}
