package com.cirtru.androidApp;

import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFCMService";
//    String _packageName = BuildConfig.APPLICATION_ID;
//    public static WelcomeActivity mWelcomeActivity;
//    Context mContext;

    public MyFirebaseMessagingService() {
//        mContext = this;
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // ...

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            /*final Map<String, String> data = remoteMessage.getData();

            try {
                Log.d(TAG, "app_package: " + data.get("app_package"));
//                openAppOnGooglePlayStoreApp(getApplicationContext(), data.get("app_package"));
                *//*if (data.get("app_package") != null) *//*{


                    _packageName = data.get("app_package");

                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        public void run() {
                            AlertDialog alertDialog = new AlertDialog.Builder(mContext)
                                    .setTitle(data.get("title"))
                                    .setMessage(data.get("message"))
                                    .setPositiveButton("Open Google Play Store!",new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            openAppOnGooglePlayStoreApp(getApplicationContext(), _packageName);
                                        }
                                    })
                                    .setIcon(R.drawable.ic_launcher)
                                    .create();

                            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                            alertDialog.show();
                        }
                    });

                    *//*AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                    builder.setTitle(remoteMessage.getNotification().getTitle());
                    builder.setMessage(remoteMessage.getNotification().getBody());
                    builder.setPositiveButton("Open Google Play Store!",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            openAppOnGooglePlayStoreApp(getApplicationContext(), _packageName);
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();*//*

//                    openAppOnGooglePlayStoreApp(getApplicationContext(), _packageName);
                }
            } catch (Exception e) {
                Log.d(TAG, "Exception: "+e);
            }*/
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
    }

    /*public static void openAppOnGooglePlayStoreApp(Context context, String appId) {
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
    }*/
}
